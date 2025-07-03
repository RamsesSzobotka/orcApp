package com.orcapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "OCRapp.db";
    private static final int DB_VERSION = 1;
    private static final String TAG = "DBHelper";

    // Tabla usuarios
    private static final String TABLE_USUARIOS = "usuarios";
    private static final String COL_USUARIO_ID = "id";
    private static final String COL_NOMBRE_USUARIO = "nombre_usuario";
    private static final String COL_CONTRASEÑA = "contraseña";
    private static final String COL_FECHA_REGISTRO = "fecha_registro";

    // Tabla historial
    private static final String TABLE_HISTORIAL = "historial";
    private static final String COL_HISTORIAL_ID = "id";
    private static final String COL_TEXTO = "texto";
    private static final String COL_FECHA = "fecha";
    private static final String COL_ID_USUARIO = "id_usuario";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla usuarios con fecha de registro
        String createUsuarios = "CREATE TABLE " + TABLE_USUARIOS + " (" +
                COL_USUARIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOMBRE_USUARIO + " TEXT NOT NULL UNIQUE, " +
                COL_CONTRASEÑA + " TEXT NOT NULL, " +
                COL_FECHA_REGISTRO + " TEXT NOT NULL)";

        // Crear tabla historial
        String createHistorial = "CREATE TABLE " + TABLE_HISTORIAL + " (" +
                COL_HISTORIAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TEXTO + " TEXT NOT NULL, " +
                COL_FECHA + " TEXT NOT NULL, " +
                COL_ID_USUARIO + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + COL_ID_USUARIO + ") REFERENCES " + TABLE_USUARIOS + "(" + COL_USUARIO_ID + "))";

        db.execSQL(createUsuarios);
        db.execSQL(createHistorial);

        Log.d(TAG, "Base de datos creada exitosamente");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORIAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
        Log.d(TAG, "Base de datos actualizada de versión " + oldVersion + " a " + newVersion);
    }

    /**
     * Registra un nuevo usuario en la base de datos
     * @param nombreUsuario Nombre único del usuario
     * @param contraseña Contraseña del usuario (será hasheada)
     * @return true si el registro fue exitoso, false en caso contrario
     */
    public boolean registrarUsuario(String nombreUsuario, String contraseña) {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty() ||
                contraseña == null || contraseña.trim().isEmpty()) {
            Log.e(TAG, "Nombre de usuario o contraseña vacíos");
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE_USUARIO, nombreUsuario.trim());
        values.put(COL_CONTRASEÑA, hashear(contraseña));
        values.put(COL_FECHA_REGISTRO, obtenerFechaActual());

        try {
            long result = db.insert(TABLE_USUARIOS, null, values);
            if (result != -1) {
                Log.d(TAG, "Usuario registrado exitosamente: " + nombreUsuario);
                return true;
            } else {
                Log.e(TAG, "Error al registrar usuario: " + nombreUsuario);
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Excepción al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Valida las credenciales de un usuario
     * @param nombreUsuario Nombre del usuario
     * @param contraseña Contraseña del usuario
     * @return true si las credenciales son válidas, false en caso contrario
     */
    public boolean validarUsuario(String nombreUsuario, String contraseña) {
        if (nombreUsuario == null || contraseña == null) {
            return false;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_USUARIOS +
                            " WHERE " + COL_NOMBRE_USUARIO + "=? AND " + COL_CONTRASEÑA + "=?",
                    new String[]{nombreUsuario.trim(), hashear(contraseña)});
            boolean existe = cursor.getCount() > 0;
            Log.d(TAG, "Validación de usuario " + nombreUsuario + ": " + existe);
            return existe;
        } catch (Exception e) {
            Log.e(TAG, "Error al validar usuario: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Obtiene el ID de un usuario por su nombre
     * @param nombreUsuario Nombre del usuario
     * @return ID del usuario o -1 si no existe
     */
    public int obtenerIdUsuario(String nombreUsuario) {
        if (nombreUsuario == null) {
            return -1;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT " + COL_USUARIO_ID + " FROM " + TABLE_USUARIOS +
                            " WHERE " + COL_NOMBRE_USUARIO + "=?",
                    new String[]{nombreUsuario.trim()});
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(0);
                Log.d(TAG, "ID obtenido para usuario " + nombreUsuario + ": " + id);
                return id;
            }
            return -1;
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener ID de usuario: " + e.getMessage());
            return -1;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Inserta un texto escaneado en el historial
     * @param texto Texto reconocido por OCR
     * @param fecha Fecha del escaneo (puede ser null para usar fecha actual)
     * @param idUsuario ID del usuario que realizó el escaneo
     * @return true si la inserción fue exitosa, false en caso contrario
     */
    public boolean insertarTexto(String texto, String fecha, int idUsuario) {
        if (texto == null || texto.trim().isEmpty() || idUsuario <= 0) {
            Log.e(TAG, "Parámetros inválidos para insertar texto");
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TEXTO, texto.trim());
        values.put(COL_FECHA, fecha != null ? fecha : obtenerFechaActual());
        values.put(COL_ID_USUARIO, idUsuario);

        try {
            long result = db.insert(TABLE_HISTORIAL, null, values);
            if (result != -1) {
                Log.d(TAG, "Texto insertado exitosamente para usuario ID: " + idUsuario);
                return true;
            } else {
                Log.e(TAG, "Error al insertar texto para usuario ID: " + idUsuario);
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Excepción al insertar texto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene el historial de escaneos de un usuario específico
     * @param idUsuario ID del usuario
     * @return Cursor con los resultados
     */
    public Cursor obtenerHistorialPorUsuario(int idUsuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_HISTORIAL +
                        " WHERE " + COL_ID_USUARIO + "=? ORDER BY " + COL_FECHA + " DESC",
                new String[]{String.valueOf(idUsuario)});
    }

    /**
     * Obtiene el historial como lista de objetos para mayor facilidad de uso
     * @param idUsuario ID del usuario
     * @return Lista de registros del historial
     */
    public List<HistorialItem> obtenerHistorialComoLista(int idUsuario) {
        List<HistorialItem> historial = new ArrayList<>();
        Cursor cursor = obtenerHistorialPorUsuario(idUsuario);

        try {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_HISTORIAL_ID));
                    String texto = cursor.getString(cursor.getColumnIndexOrThrow(COL_TEXTO));
                    String fecha = cursor.getString(cursor.getColumnIndexOrThrow(COL_FECHA));

                    historial.add(new HistorialItem(id, texto, fecha));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener historial como lista: " + e.getMessage());
        } finally {
            cursor.close();
        }

        return historial;
    }

    /**
     * Elimina un elemento específico del historial
     * @param idHistorial ID del elemento a eliminar
     * @param idUsuario ID del usuario (para verificar permisos)
     * @return true si se eliminó exitosamente, false en caso contrario
     */
    public boolean eliminarElementoHistorial(int idHistorial, int idUsuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int rowsDeleted = db.delete(TABLE_HISTORIAL,
                    COL_HISTORIAL_ID + "=? AND " + COL_ID_USUARIO + "=?",
                    new String[]{String.valueOf(idHistorial), String.valueOf(idUsuario)});
            Log.d(TAG, "Elementos eliminados: " + rowsDeleted);
            return rowsDeleted > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error al eliminar elemento del historial: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina todo el historial de un usuario
     * @param idUsuario ID del usuario
     * @return true si se eliminó exitosamente, false en caso contrario
     */
    public boolean limpiarHistorialUsuario(int idUsuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int rowsDeleted = db.delete(TABLE_HISTORIAL,
                    COL_ID_USUARIO + "=?",
                    new String[]{String.valueOf(idUsuario)});
            Log.d(TAG, "Historial limpiado para usuario ID " + idUsuario + ". Elementos eliminados: " + rowsDeleted);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error al limpiar historial: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si un nombre de usuario ya existe
     * @param nombreUsuario Nombre a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existeUsuario(String nombreUsuario) {
        if (nombreUsuario == null) {
            return false;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USUARIOS +
                            " WHERE " + COL_NOMBRE_USUARIO + "=?",
                    new String[]{nombreUsuario.trim()});
            if (cursor.moveToFirst()) {
                return cursor.getInt(0) > 0;
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error al verificar existencia de usuario: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Obtiene la cantidad total de escaneos de un usuario
     * @param idUsuario ID del usuario
     * @return Número de escaneos realizados
     */
    public int contarEscaneosUsuario(int idUsuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_HISTORIAL +
                            " WHERE " + COL_ID_USUARIO + "=?",
                    new String[]{String.valueOf(idUsuario)});
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } catch (Exception e) {
            Log.e(TAG, "Error al contar escaneos: " + e.getMessage());
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Hashea una contraseña usando SHA-256
     * @param contraseña Contraseña a hashear
     * @return Contraseña hasheada en Base64
     */
    private String hashear(String contraseña) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(contraseña.getBytes());
            return Base64.encodeToString(hash, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Error al hashear contraseña: " + e.getMessage());
            return contraseña; // Fallback (no recomendado en producción)
        }
    }

    /**
     * Obtiene la fecha actual en formato legible
     * @return Fecha actual como string
     */
    private String obtenerFechaActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * Clase auxiliar para representar elementos del historial
     */
    public static class HistorialItem {
        private int id;
        private String texto;
        private String fecha;

        public HistorialItem(int id, String texto, String fecha) {
            this.id = id;
            this.texto = texto;
            this.fecha = fecha;
        }

        // Getters
        public int getId() { return id; }
        public String getTexto() { return texto; }
        public String getFecha() { return fecha; }

        // Setters
        public void setId(int id) { this.id = id; }
        public void setTexto(String texto) { this.texto = texto; }
        public void setFecha(String fecha) { this.fecha = fecha; }

        @Override
        public String toString() {
            return "HistorialItem{id=" + id + ", texto='" + texto + "', fecha='" + fecha + "'}";
        }
    }
}