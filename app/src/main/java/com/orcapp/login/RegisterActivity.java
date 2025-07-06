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

    //Componentes de la interfaz
    private EditText etUsername, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;

    //Helper para operaciones con la base de datos
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setControls();
        initObjects();
        setListeners();
    }

    //Vincula las vistas del layout con variables
    private void setControls() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
    }

    //Inicializa los objetos necesarios
    private void initObjects() {
        dbHelper = new DBHelper(this);
    }

    //Define los eventos para los botones
    private void setListeners() {
        btnRegister.setOnClickListener(v -> registerUser());
        tvLogin.setOnClickListener(v -> finish());
    }

    //Maneja el proceso de registro de usuario
    private void registerUser() {

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        String error = ValidationUtils.validarRegistro(username, password, confirmPassword);
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.registrarUsuario(username, password)) {
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
        }
    }
}
