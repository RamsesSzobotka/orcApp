package com.orcapp.db;

import android.content.Context;
import android.content.SharedPreferences;

/*
 Maneja la sesión del usuario utilizando SharedPreferences
 Permite guardar, recuperar y eliminar información de la sesión actual
 */
public class SessionManager {
    //Nombres de las preferencias y claves para almacenamiento
    private static final String PREF_NAME = "SesionPrefs";
    private static final String KEY_ID_USUARIO = "idUsuario";
    private static final String KEY_NOMBRE_USUARIO = "nombreUsuario";

    //Objetos para manejar las preferencias compartidas
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;


    //Constructor que inicializa las preferencias compartidas

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }


     //Guarda los datos de la sesión del usuario
    public void guardarSesion(int idUsuario, String nombreUsuario) {
        editor.putInt(KEY_ID_USUARIO, idUsuario);
        editor.putString(KEY_NOMBRE_USUARIO, nombreUsuario);
        editor.apply(); // Guarda los cambios de forma asíncrona
    }

    /*
     Verifica si existe una sesión activa
     retorna true si hay un usuario logueado, false en caso contrario
     */
    public boolean haySesionActiva() {
        return prefs.contains(KEY_ID_USUARIO);
    }

    /*
     Obtiene el ID del usuario actualmente logueado
     retorna ID del usuario o -1 si no hay sesión activa
     */
    public int obtenerIdUsuario() {
        return prefs.getInt(KEY_ID_USUARIO, -1);
    }


     //Cierra la sesión actual eliminando todos los datos almacenados
    public void cerrarSesion() {
        editor.clear();  // Elimina todos los valores
        editor.apply();  // Aplica los cambios
    }

    /*
     Obtiene el nombre de usuario de la sesión actual
     retorna Nombre del usuario o null si no hay sesión activa
     */
    public String obtenerNombreUsuario() {
        return prefs.getString(KEY_NOMBRE_USUARIO, null);
    }
}