package com.orcapp.db;

import android.content.Context;
import android.content.SharedPreferences;

// Clase que maneja la sesión del usuario utilizando SharedPreferences
public class SessionManager {

    // Nombres de las claves para el almacenamiento en SharedPreferences
    private static final String PREF_NAME = "SesionPrefs";
    private static final String KEY_ID_USUARIO = "idUsuario";
    private static final String KEY_NOMBRE_USUARIO = "nombreUsuario";

    // Objetos para acceder y modificar las preferencias
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    // Inicializa SharedPreferences para manejar la sesión
    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Guarda el ID y nombre del usuario en la sesión
    public void guardarSesion(int idUsuario, String nombreUsuario) {
        editor.putInt(KEY_ID_USUARIO, idUsuario);
        editor.putString(KEY_NOMBRE_USUARIO, nombreUsuario);
        editor.apply();
    }

    // Verifica si hay una sesión activa
    public boolean haySesionActiva() {
        return prefs.contains(KEY_ID_USUARIO);
    }

    // Devuelve el ID del usuario en sesión o -1 si no hay
    public int obtenerIdUsuario() {
        return prefs.getInt(KEY_ID_USUARIO, -1);
    }

    // Elimina todos los datos de sesión (cierre de sesión)
    public void cerrarSesion() {
        editor.clear();
        editor.apply();
    }
    // Devuelve el nombre del usuario en sesión o null si no hay
    public String obtenerNombreUsuario() {
        return prefs.getString(KEY_NOMBRE_USUARIO, null);
    }
}
