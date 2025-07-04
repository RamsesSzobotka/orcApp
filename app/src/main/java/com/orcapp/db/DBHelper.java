package com.orcapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DBHelper extends SQLiteOpenHelper {
    // Configuración básica de la base de datos
    private static final String DATABASE_NAME = "OrcAppDB.db";
    private static final int DATABASE_VERSION = 1;

    // Estructura de la tabla de usuarios
    public static final String TABLE_USUARIOS = "usuarios";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE_USUARIO = "nombre_usuario";
    public static final String COLUMN_CONTRASENA = "contraseña";

    // Estructura de la tabla de historial
    public static final String TABLE_HISTORIAL = "historial";
    public static final String COLUMN_TEXTO = "texto";
    public static final String COLUMN_FECHA = "fecha";
    public static final String COLUMN_ID_USUARIO = "id_usuario";

    // Constructor
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creación de la tabla de usuarios
        String CREATE_USUARIOS_TABLE = "CREATE TABLE " + TABLE_USUARIOS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOMBRE_USUARIO + " TEXT NOT NULL UNIQUE, " +
                COLUMN_CONTRASENA + " TEXT NOT NULL)";

        // Creación de la tabla de historial con clave foránea
        String CREATE_HISTORIAL_TABLE = "CREATE TABLE " + TABLE_HISTORIAL + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TEXTO + " TEXT NOT NULL, " +
                COLUMN_FECHA + " TEXT NOT NULL, " +
                COLUMN_ID_USUARIO + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_ID_USUARIO + ") REFERENCES " + TABLE_USUARIOS + "(" + COLUMN_ID + "))";

        // Ejecutar las sentencias SQL
        db.execSQL(CREATE_USUARIOS_TABLE);
        db.execSQL(CREATE_HISTORIAL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminar tablas antiguas si existen
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORIAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        // Volver a crear la estructura
        onCreate(db);
    }

    /*
     Registra un nuevo usuario en la base de datos
     retorna true si el registro fue exitoso, false si el usuario ya existe
     */
    public boolean registrarUsuario(String nombreUsuario, String contraseña) {
        if (existeUsuario(nombreUsuario)) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE_USUARIO, nombreUsuario);
        values.put(COLUMN_CONTRASENA, hashear(contraseña)); // Almacena contraseña hasheada
        long resultado = db.insert(TABLE_USUARIOS, null, values);
        return resultado != -1;
    }

    /*
     Valida las credenciales de un usuario
     retorna true si las credenciales son correctas
     */
    public boolean validarUsuario(String nombreUsuario, String contraseña) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USUARIOS,
                new String[]{COLUMN_ID},
                COLUMN_NOMBRE_USUARIO + " = ? AND " + COLUMN_CONTRASENA + " = ?",
                new String[]{nombreUsuario, hashear(contraseña)},
                null, null, null);
        boolean existe = cursor.moveToFirst();
        cursor.close();
        return existe;
    }

    /*
     Obtiene el ID de un usuario por su nombre
     retorna ID del usuario o -1 si no existe
     */
    public int obtenerIdUsuario(String nombreUsuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USUARIOS,
                new String[]{COLUMN_ID},
                COLUMN_NOMBRE_USUARIO + " = ?",
                new String[]{nombreUsuario},
                null, null, null);

        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        }
        cursor.close();
        return id;
    }

    /*
     Inserta un nuevo texto en el historial del usuario
     retorna true si la inserción fue exitosa
     */
    public boolean insertarTexto(String texto, String fecha, int idUsuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEXTO, texto);
        values.put(COLUMN_FECHA, fecha);
        values.put(COLUMN_ID_USUARIO, idUsuario);
        long resultado = db.insert(TABLE_HISTORIAL, null, values);
        return resultado != -1;
    }

    /*
     Obtiene el historial de textos de un usuario
     retorna Cursor con los resultados ordenados por fecha descendente
     */
    public Cursor obtenerHistorialPorUsuario(int idUsuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_HISTORIAL,
                null,
                COLUMN_ID_USUARIO + " = ?",
                new String[]{String.valueOf(idUsuario)},
                null, null,
                COLUMN_FECHA + " DESC");
    }

    /*
      Verifica si un nombre de usuario ya existe
      @return true si el usuario existe
     */
    private boolean existeUsuario(String nombreUsuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USUARIOS,
                new String[]{COLUMN_ID},
                COLUMN_NOMBRE_USUARIO + " = ?",
                new String[]{nombreUsuario},
                null, null, null);
        boolean existe = cursor.moveToFirst();
        cursor.close();
        return existe;
    }

    /*
     Genera un hash SHA-256 de un string
     retorna String hasheado o el mismo input si falla
     */
    private String hashear(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return input; // Fallback inseguro, considerar manejar el error
        }
    }
}