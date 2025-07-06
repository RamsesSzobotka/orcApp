package com.orcapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.orcapp.models.TextoEscaneado;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

// Clase encargada de manejar la base de datos SQLite para usuarios e historial
public class DBHelper extends SQLiteOpenHelper {

    // Configuración de la base de datos
    private static final String DATABASE_NAME = "OrcAppDB.db";
    private static final int DATABASE_VERSION = 1;

    // Columnas y tabla de usuarios
    public static final String TABLE_USUARIOS = "usuarios";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE_USUARIO = "nombre_usuario";
    public static final String COLUMN_CONTRASENA = "contraseña";

    // Columnas y tabla de historial
    public static final String TABLE_HISTORIAL = "historial";
    public static final String COLUMN_TEXTO = "texto";
    public static final String COLUMN_FECHA = "fecha";
    public static final String COLUMN_ID_USUARIO = "id_usuario";

    // Constructor que inicializa el helper con nombre y versión de la base de datos
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Crea las tablas de usuarios e historial cuando se crea la base de datos por primera vez
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USUARIOS_TABLE = "CREATE TABLE " + TABLE_USUARIOS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOMBRE_USUARIO + " TEXT NOT NULL UNIQUE, " +
                COLUMN_CONTRASENA + " TEXT NOT NULL)";

        String CREATE_HISTORIAL_TABLE = "CREATE TABLE " + TABLE_HISTORIAL + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TEXTO + " TEXT NOT NULL, " +
                COLUMN_FECHA + " TEXT NOT NULL, " +
                COLUMN_ID_USUARIO + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_ID_USUARIO + ") REFERENCES " + TABLE_USUARIOS + "(" + COLUMN_ID + "))";

        db.execSQL(CREATE_USUARIOS_TABLE);
        db.execSQL(CREATE_HISTORIAL_TABLE);
    }

    // Actualiza la base de datos eliminando las tablas y recreándolas
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORIAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }

    // Registra un nuevo usuario si no existe previamente
    public boolean registrarUsuario(String nombreUsuario, String contrasena) {
        if (existeUsuario(nombreUsuario)) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE_USUARIO, nombreUsuario);
        values.put(COLUMN_CONTRASENA, hashear(contrasena));
        long resultado = db.insert(TABLE_USUARIOS, null, values);
        return resultado != -1;
    }

    // Valida que las credenciales del usuario sean correctas
    public boolean validarUsuario(String nombreUsuario, String contrasena) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USUARIOS,
                new String[]{COLUMN_ID},
                COLUMN_NOMBRE_USUARIO + " = ? AND " + COLUMN_CONTRASENA + " = ?",
                new String[]{nombreUsuario, hashear(contrasena)},
                null, null, null);
        boolean existe = cursor.moveToFirst();
        cursor.close();
        return existe;
    }

    // Obtiene el ID de un usuario a partir de su nombre
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

    // Inserta un nuevo registro de texto en el historial del usuario
    public boolean insertarTexto(String texto, String fecha, int idUsuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEXTO, texto);
        values.put(COLUMN_FECHA, fecha);
        values.put(COLUMN_ID_USUARIO, idUsuario);
        long resultado = db.insert(TABLE_HISTORIAL, null, values);
        return resultado != -1;
    }



    // Obtiene el historial de un usuario ordenado por fecha descendente
    public List<TextoEscaneado> obtenerHistorialPorUsuario(int idUsuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<TextoEscaneado> lista = new ArrayList<>();

        Cursor cursor = db.query(TABLE_HISTORIAL,
                null,
                COLUMN_ID_USUARIO + " = ?",
                new String[]{String.valueOf(idUsuario)},
                null, null,
                COLUMN_FECHA + " DESC");
        int indexTexto = cursor.getColumnIndexOrThrow("texto");
        int indexFecha = cursor.getColumnIndexOrThrow("fecha");
        if (cursor.moveToFirst()){
        if (indexTexto != -1 && indexFecha != -1) {
            do {
                String texto = cursor.getString(indexTexto);
                String fecha = cursor.getString(indexFecha);
                lista.add(new TextoEscaneado(texto, fecha));
            } while (cursor.moveToNext());

        }}return lista;
    }

    // Verifica si ya existe un usuario con el nombre dado
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

    // Hashea una cadena de texto usando SHA-256
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
            return input; // Fallback inseguro, debería manejarse mejor
        }
    }
}
