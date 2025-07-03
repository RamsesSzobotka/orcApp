package com.orcapp.db;

/**
 * Clase que contiene todas las constantes relacionadas con la base de datos
 * Esto facilita el mantenimiento y evita errores de tipeo
 */
public class DatabaseConstants {

    // Información general de la base de datos
    public static final String DATABASE_NAME = "OCRapp.db";
    public static final int DATABASE_VERSION = 1;

    // Tabla Usuarios
    public static final String TABLE_USUARIOS = "usuarios";
    public static final String COLUMN_USUARIO_ID = "id";
    public static final String COLUMN_NOMBRE_USUARIO = "nombre_usuario";
    public static final String COLUMN_CONTRASEÑA = "contraseña";
    public static final String COLUMN_FECHA_REGISTRO = "fecha_registro";

    // Tabla Historial
    public static final String TABLE_HISTORIAL = "historial";
    public static final String COLUMN_HISTORIAL_ID = "id";
    public static final String COLUMN_TEXTO = "texto";
    public static final String COLUMN_FECHA = "fecha";
    public static final String COLUMN_ID_USUARIO_FK = "id_usuario";

    // SQL para crear tablas
    public static final String CREATE_TABLE_USUARIOS =
            "CREATE TABLE " + TABLE_USUARIOS + " (" +
                    COLUMN_USUARIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOMBRE_USUARIO + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_CONTRASEÑA + " TEXT NOT NULL, " +
                    COLUMN_FECHA_REGISTRO + " TEXT NOT NULL)";

    public static final String CREATE_TABLE_HISTORIAL =
            "CREATE TABLE " + TABLE_HISTORIAL + " (" +
                    COLUMN_HISTORIAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TEXTO + " TEXT NOT NULL, " +
                    COLUMN_FECHA + " TEXT NOT NULL, " +
                    COLUMN_ID_USUARIO_FK + " INTEGER NOT NULL, " +
                    "FOREIGN KEY (" + COLUMN_ID_USUARIO_FK + ") REFERENCES " +
                    TABLE_USUARIOS + "(" + COLUMN_USUARIO_ID + "))";

    // SQL para eliminar tablas
    public static final String DROP_TABLE_USUARIOS = "DROP TABLE IF EXISTS " + TABLE_USUARIOS;
    public static final String DROP_TABLE_HISTORIAL = "DROP TABLE IF EXISTS " + TABLE_HISTORIAL;

    // Consultas comunes
    public static final String SELECT_USER_BY_CREDENTIALS =
            "SELECT * FROM " + TABLE_USUARIOS +
                    " WHERE " + COLUMN_NOMBRE_USUARIO + "=? AND " + COLUMN_CONTRASEÑA + "=?";

    public static final String SELECT_USER_ID =
            "SELECT " + COLUMN_USUARIO_ID + " FROM " + TABLE_USUARIOS +
                    " WHERE " + COLUMN_NOMBRE_USUARIO + "=?";

    public static final String SELECT_HISTORIAL_BY_USER =
            "SELECT * FROM " + TABLE_HISTORIAL +
                    " WHERE " + COLUMN_ID_USUARIO_FK + "=? ORDER BY " + COLUMN_FECHA + " DESC";

    public static final String COUNT_USER_SCANS =
            "SELECT COUNT(*) FROM " + TABLE_HISTORIAL +
                    " WHERE " + COLUMN_ID_USUARIO_FK + "=?";

    public static final String CHECK_USER_EXISTS =
            "SELECT COUNT(*) FROM " + TABLE_USUARIOS +
                    " WHERE " + COLUMN_NOMBRE_USUARIO + "=?";

    // Configuración de SharedPreferences para SessionManager
    public static final String PREF_NAME = "usuario_sesion";
    public static final String KEY_USER_ID = "id_usuario";
    public static final String KEY_USERNAME = "nombre_usuario";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";

    // Códigos de resultado para Activities
    public static final int RESULT_LOGIN_SUCCESS = 100;
    public static final int RESULT_REGISTER_SUCCESS = 101;
    public static final int RESULT_LOGOUT = 102;

    // Extras para Intent
    public static final String EXTRA_USERNAME = "extra_username";
    public static final String EXTRA_USER_ID = "extra_user_id";
    public static final String EXTRA_SCANNED_TEXT = "extra_scanned_text";
    public static final String EXTRA_FILTER_TYPE = "extra_filter_type";

    // Tipos de filtros
    public static final String FILTER_EMAIL = "email";
    public static final String FILTER_PHONE = "phone";
    public static final String FILTER_DATE = "date";
    public static final String FILTER_CEDULA = "cedula";

    // Mensajes de error comunes
    public static final String ERROR_EMPTY_FIELDS = "Por favor complete todos los campos";
    public static final String ERROR_USER_EXISTS = "El usuario ya existe";
    public static final String ERROR_INVALID_CREDENTIALS = "Usuario o contraseña incorrectos";
    public static final String ERROR_DATABASE = "Error en la base de datos";
    public static final String ERROR_REGISTRATION_FAILED = "Error al registrar usuario";

    // Mensajes de éxito
    public static final String SUCCESS_REGISTRATION = "Usuario registrado exitosamente";
    public static final String SUCCESS_LOGIN = "Inicio de sesión exitoso";
    public static final String SUCCESS_TEXT_SAVED = "Texto guardado en el historial";

    // Configuraciones de validación
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_USERNAME_LENGTH = 20;
    public static final int MAX_TEXT_LENGTH = 5000;

    // Formato de fechas
    public static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final String DATE_FORMAT_SHORT = "dd/MM/yyyy";
}