package com.orcapp.utils;

import java.util.regex.Pattern;

/**
 * Clase utilitaria para validaciones comunes en la aplicación
 */
public class ValidationUtils {

    // Patrones de validación
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(\\+507)?\\s?[0-9]{8}$|^(\\+507)?\\s?[0-9]{4}[-\\s]?[0-9]{4}$"
    );

    private static final Pattern CEDULA_PANAMA_PATTERN = Pattern.compile(
            "^[0-9]{1,2}-[0-9]{1,4}-[0-9]{1,4}$"
    );

    private static final Pattern DATE_PATTERN = Pattern.compile(
            "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/[0-9]{4}$"
    );

    // Constantes de validación
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 20;
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 50;
    public static final int MAX_TEXT_LENGTH = 5000;

    /**
     * Valida un nombre de usuario
     * @param username Nombre de usuario a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean esUsernameValido(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        String cleanUsername = username.trim();

        // Verificar longitud
        if (cleanUsername.length() < MIN_USERNAME_LENGTH ||
                cleanUsername.length() > MAX_USERNAME_LENGTH) {
            return false;
        }

        // Verificar que solo contenga letras, números y guiones bajos
        return cleanUsername.matches("^[a-zA-Z0-9_]+$");
    }

    /**
     * Valida una contraseña
     * @param password Contraseña a validar
     * @return true si es válida, false en caso contrario
     */
    public static boolean esPasswordValida(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        // Verificar longitud mínima
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }

        // Verificar longitud máxima
        if (password.length() > MAX_PASSWORD_LENGTH) {
            return false;
        }

        // Verificar que contenga al menos una letra y un número
        boolean tieneLetra = password.matches(".*[a-zA-Z].*");
        boolean tieneNumero = password.matches(".*[0-9].*");

        return tieneLetra && tieneNumero;
    }

    /**
     * Valida si las contraseñas coinciden
     * @param password Contraseña original
     * @param confirmPassword Confirmación de contraseña
     * @return true si coinciden, false en caso contrario
     */
    public static boolean passwordsCoinciden(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }

    /**
     * Valida un correo electrónico
     * @param email Correo a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean esEmailValido(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Valida un número telefónico panameño
     * @param phone Teléfono a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean esTelefonoValido(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Valida una cédula panameña
     * @param cedula Cédula a validar
     * @return true si es válida, false en caso contrario
     */
    public static boolean esCedulaValida(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) {
            return false;
        }
        return CEDULA_PANAMA_PATTERN.matcher(cedula.trim()).matches();
    }

    /**
     * Valida una fecha en formato dd/MM/yyyy
     * @param date Fecha a validar
     * @return true si es válida, false en caso contrario
     */
    public static boolean esFechaValida(String date) {
        if (date == null || date.trim().isEmpty()) {
            return false;
        }
        return DATE_PATTERN.matcher(date.trim()).matches();
    }

    /**
     * Valida la longitud de un texto
     * @param text Texto a validar
     * @param maxLength Longitud máxima permitida
     * @return true si es válido, false en caso contrario
     */
    public static boolean esTextoValidoLongitud(String text, int maxLength) {
        if (text == null) {
            return false;
        }
        return text.length() <= maxLength;
    }

    /**
     * Valida que un texto no esté vacío
     * @param text Texto a validar
     * @return true si no está vacío, false en caso contrario
     */
    public static boolean noEstaVacio(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /**
     * Limpia y valida un nombre de usuario
     * @param username Nombre de usuario a limpiar
     * @return Nombre de usuario limpio o null si no es válido
     */
    public static String limpiarUsername(String username) {
        if (username == null) {
            return null;
        }

        String cleaned = username.trim().toLowerCase();
        return esUsernameValido(cleaned) ? cleaned : null;
    }

    /**
     * Obtiene un mensaje de error específico para validación de username
     * @param username Username a validar
     * @return Mensaje de error o null si es válido
     */
    public static String getMensajeErrorUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "El nombre de usuario no puede estar vacío";
        }

        String cleanUsername = username.trim();

        if (cleanUsername.length() < MIN_USERNAME_LENGTH) {
            return "El nombre de usuario debe tener al menos " + MIN_USERNAME_LENGTH + " caracteres";
        }

        if (cleanUsername.length() > MAX_USERNAME_LENGTH) {
            return "El nombre de usuario no puede tener más de " + MAX_USERNAME_LENGTH + " caracteres";
        }

        if (!cleanUsername.matches("^[a-zA-Z0-9_]+$")) {
            return "El nombre de usuario solo puede contener letras, números y guiones bajos";
        }

        return null; // Es válido
    }

    /**
     * Obtiene un mensaje de error específico para validación de contraseña
     * @param password Contraseña a validar
     * @return Mensaje de error o null si es válida
     */
    public static String getMensajeErrorPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return "La contraseña no puede estar vacía";
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            return "La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres";
        }

        if (password.length() > MAX_PASSWORD_LENGTH) {
            return "La contraseña no puede tener más de " + MAX_PASSWORD_LENGTH + " caracteres";
        }

        if (!password.matches(".*[a-zA-Z].*")) {
            return "La contraseña debe contener al menos una letra";
        }

        if (!password.matches(".*[0-9].*")) {
            return "La contraseña debe contener al menos un número";
        }

        return null; // Es válida
    }

    /**
     * Valida todos los campos de registro
     * @param username Nombre de usuario
     * @param password Contraseña
     * @param confirmPassword Confirmación de contraseña
     * @return Mensaje de error o null si todo es válido
     */
    public static String validarRegistro(String username, String password, String confirmPassword) {
        String errorUsername = getMensajeErrorUsername(username);
        if (errorUsername != null) {
            return errorUsername;
        }

        String errorPassword = getMensajeErrorPassword(password);
        if (errorPassword != null) {
            return errorPassword;
        }

        if (!passwordsCoinciden(password, confirmPassword)) {
            return "Las contraseñas no coinciden";
        }

        return null; // Todo válido
    }

    /**
     * Valida los campos de login
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return Mensaje de error o null si todo es válido
     */
    public static String validarLogin(String username, String password) {
        if (!noEstaVacio(username)) {
            return "El nombre de usuario no puede estar vacío";
        }

        if (!noEstaVacio(password)) {
            return "La contraseña no puede estar vacía";
        }

        return null; // Todo válido
    }
}