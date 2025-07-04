package com.orcapp.db;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "SesionPrefs";
    private static final String KEY_ID_USUARIO = "idUsuario";
    private static final String KEY_NOMBRE_USUARIO = "nombreUsuario";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void guardarSesion(int idUsuario, String nombreUsuario) {
        editor.putInt(KEY_ID_USUARIO, idUsuario);
        editor.putString(KEY_NOMBRE_USUARIO, nombreUsuario);
        editor.apply();
    }

    public boolean haySesionActiva() {
        return prefs.contains(KEY_ID_USUARIO);
    }

    public int obtenerIdUsuario() {
        return prefs.getInt(KEY_ID_USUARIO, -1);
    }

    public void cerrarSesion() {
        editor.clear();
        editor.apply();
    }

    public String obtenerNombreUsuario() {
        return prefs.getString(KEY_NOMBRE_USUARIO, null);
    }
}