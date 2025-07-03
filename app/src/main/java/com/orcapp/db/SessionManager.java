package com.orcapp.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Clase para manejar la sesión del usuario usando SharedPreferences
 * Permite guardar, recuperar y eliminar información de la sesión actual
 */
public class SessionManager {

    private static final String TAG = "SessionManager";
    private static final String PREF_NAME = "usuario_sesion";
    private static final String KEY_USER_ID = "id_usuario";
    private static final String KEY_USERNAME = "nombre_usuario";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_LOGIN_TIME = "login_time";
    private static final String KEY_LAST_ACTIVITY = "last_activity";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Guarda la información de la sesión del usuario
     * @param userId ID del usuario
     * @param username Nombre del usuario
     */
    public void guardarSesion(int userId, String username) {
        if (userId <= 0 || username == null || username.trim().isEmpty()) {
            Log.e(TAG, "Parámetros inválidos para guardar sesión");
            return;
        }

        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username.trim());
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis());
        editor.putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis());
        editor.apply();

        Log.d(TAG, "Sesión guardada para usuario: " + username + " (ID: " + userId + ")");
    }

    /**
     * Sobrecarga del método anterior para mantener compatibilidad
     * @param userId ID del usuario
     */
    public void guardarSesion(int userId) {
        // Si no se proporciona username, intentamos obtenerlo de la base de datos
        DBHelper dbHelper = new DBHelper(context);
        // Como no tenemos método para obtener username por ID, usamos un valor por defecto
        guardarSesion(userId, "Usuario_" + userId);
    }

    /**
     * Obtiene el ID del usuario actual
     * @return ID del usuario o -1 si no hay sesión activa
     */
    public int obtenerIdUsuario() {
        int userId = prefs.getInt(KEY_USER_ID, -1);
        if (userId != -1) {
            actualizarUltimaActividad();
        }
        return userId;
    }

    /**
     * Obtiene el nombre del usuario actual
     * @return Nombre del usuario o null si no hay sesión activa
     */
    public String obtenerNombreUsuario() {
        String username = prefs.getString(KEY_USERNAME, null);
        if (username != null) {
            actualizarUltimaActividad();
        }
        return username;
    }

    /**
     * Verifica si hay una sesión activa
     * @return true si hay un usuario logueado, false en caso contrario
     */
    public boolean haySesionActiva() {
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);
        int userId = prefs.getInt(KEY_USER_ID, -1);

        // Verificamos que tanto el flag como el ID sean válidos
        boolean sesionValida = isLoggedIn && userId > 0;

        if (sesionValida) {
            actualizarUltimaActividad();
        }

        return sesionValida;
    }

    /**
     * Cierra la sesión actual eliminando todos los datos
     */
    public void cerrarSesion() {
        String username = obtenerNombreUsuario();
        editor.clear();
        editor.apply();
        Log.d(TAG, "Sesión cerrada para usuario: " + (username != null ? username : "desconocido"));
    }

    /**
     * Actualiza el timestamp de la última actividad
     */
    public void actualizarUltimaActividad() {
        editor.putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis());
        editor.apply();
    }

    /**
     * Obtiene el tiempo transcurrido desde el inicio de sesión en milisegundos
     * @return Tiempo transcurrido o -1 si no hay sesión activa
     */
    public long getTiempoSesion() {
        if (!haySesionActiva()) {
            return -1;
        }

        long loginTime = prefs.getLong(KEY_LOGIN_TIME, 0);
        if (loginTime == 0) {
            return -1;
        }

        return System.currentTimeMillis() - loginTime;
    }

    /**
     * Obtiene el tiempo transcurrido desde la última actividad en milisegundos
     * @return Tiempo transcurrido o -1 si no hay sesión activa
     */
    public long getTiempoInactividad() {
        if (!haySesionActiva()) {
            return -1;
        }

        long lastActivity = prefs.getLong(KEY_LAST_ACTIVITY, 0);
        if (lastActivity == 0) {
            return -1;
        }

        return System.currentTimeMillis() - lastActivity;
    }

    /**
     * Verifica si la sesión ha expirado por inactividad
     * @param timeoutMinutes Minutos de inactividad permitidos
     * @return true si la sesión ha expirado, false en caso contrario
     */
    public boolean haExpiradoSesion(int timeoutMinutes) {
        if (!haySesionActiva()) {
            return true;
        }

        long inactiveTime = getTiempoInactividad();
        long timeoutMillis = timeoutMinutes * 60 * 1000; // Convertir a milisegundos

        return inactiveTime > timeoutMillis;
    }

    /**
     * Renueva la sesión actualizando los timestamps
     */
    public void renovarSesion() {
        if (haySesionActiva()) {
            long currentTime = System.currentTimeMillis();
            editor.putLong(KEY_LOGIN_TIME, currentTime);
            editor.putLong(KEY_LAST_ACTIVITY, currentTime);
            editor.apply();
            Log.d(TAG, "Sesión renovada para usuario ID: " + obtenerIdUsuario());
        }
    }

    /**
     * Obtiene información completa de la sesión actual
     * @return Información de la sesión o null si no hay sesión activa
     */
    public SesionInfo obtenerInfoSesion() {
        if (!haySesionActiva()) {
            return null;
        }

        return new SesionInfo(
                obtenerIdUsuario(),
                obtenerNombreUsuario(),
                prefs.getLong(KEY_LOGIN_TIME, 0),
                prefs.getLong(KEY_LAST_ACTIVITY, 0)
        );
    }

    /**
     * Limpia solo los datos de sesión pero mantiene configuraciones
     */
    public void limpiarDatosSesion() {
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_LOGIN_TIME);
        editor.remove(KEY_LAST_ACTIVITY);
        editor.apply();
        Log.d(TAG, "Datos de sesión limpiados");
    }

    /**
     * Verifica si es la primera vez que se ejecuta la app
     * @return true si es primera vez, false en caso contrario
     */
    public boolean esPrimeraVez() {
        return prefs.getBoolean("primera_vez", true);
    }

    /**
     * Marca que ya no es la primera vez que se ejecuta la app
     */
    public void marcarNoEsPrimeraVez() {
        editor.putBoolean("primera_vez", false);
        editor.apply();
    }

    /**
     * Clase interna para encapsular información de la sesión
     */
    public static class SesionInfo {
        private int userId;
        private String username;
        private long loginTime;
        private long lastActivity;

        public SesionInfo(int userId, String username, long loginTime, long lastActivity) {
            this.userId = userId;
            this.username = username;
            this.loginTime = loginTime;
            this.lastActivity = lastActivity;
        }

        // Getters
        public int getUserId() { return userId; }
        public String getUsername() { return username; }
        public long getLoginTime() { return loginTime; }
        public long getLastActivity() { return lastActivity; }

        // Métodos utilitarios
        public long getDuracionSesion() {
            return System.currentTimeMillis() - loginTime;
        }

        public long getTiempoInactividad() {
            return System.currentTimeMillis() - lastActivity;
        }

        public boolean esValida() {
            return userId > 0 && username != null && !username.trim().isEmpty();
        }

        @Override
        public String toString() {
            return "SesionInfo{" +
                    "userId=" + userId +
                    ", username='" + username + '\'' +
                    ", loginTime=" + loginTime +
                    ", lastActivity=" + lastActivity +
                    '}';
        }
    }
}