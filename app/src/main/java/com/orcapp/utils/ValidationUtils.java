package com.orcapp.utils;

public class ValidationUtils {
    public static String validarLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return "Todos los campos son obligatorios";
        }
        return null;
    }

    public static String validarRegistro(String username, String password, String confirmPassword) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return "Todos los campos son obligatorios";
        }
        if (!password.equals(confirmPassword)) {
            return "Las contraseñas no coinciden";
        }
        if (password.length() < 6) {
            return "La contraseña debe tener al menos 6 caracteres";
        }
        return null;
    }
}