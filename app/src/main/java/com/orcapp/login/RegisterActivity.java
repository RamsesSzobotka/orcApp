package com.orcapp.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.orcapp.R;
import com.orcapp.db.DBHelper;
import com.orcapp.utils.ValidationUtils;

public class RegisterActivity extends AppCompatActivity {

    // Componentes de la interfaz
    private EditText etUsername, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;

    // Helper para operaciones con la base de datos
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicialización de vistas
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        // Inicialización del helper de base de datos
        dbHelper = new DBHelper(this);

        // Listeners para los botones
        btnRegister.setOnClickListener(v -> registerUser()); // Registro al hacer click
        tvLogin.setOnClickListener(v -> finish()); // Cierra esta actividad y vuelve al login
    }

    /**
     * Maneja el proceso de registro de usuario
     * 1. Valida los campos de entrada
     * 2. Intenta registrar en la base de datos
     * 3. Muestra feedback al usuario
     */
    private void registerUser() {
        // Obtiene y limpia los textos ingresados
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validación de campos usando ValidationUtils
        String error = ValidationUtils.validarRegistro(username, password, confirmPassword);
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            return; // Detiene el registro si hay errores
        }

        // Intento de registro en la base de datos
        if (dbHelper.registrarUsuario(username, password)) {
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
            finish(); // Vuelve a LoginActivity después de registrar
        } else {
            Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
        }
    }
}