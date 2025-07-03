package com.orcapp.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.orcapp.MainActivity;
import com.orcapp.R;
import com.orcapp.db.DBHelper;
import com.orcapp.db.SessionManager;
import com.orcapp.utils.ValidationUtils;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    private DBHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar vistas
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        // Inicializar DB y Session
        dbHelper = new DBHelper(this);
        sessionManager = new SessionManager(this);

        // Verificar sesión activa
        if (sessionManager.haySesionActiva()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        // Listeners
        btnLogin.setOnClickListener(v -> loginUser());
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void loginUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validar campos
        String error = ValidationUtils.validarLogin(username, password);
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar credenciales
        if (dbHelper.validarUsuario(username, password)) {
            int userId = dbHelper.obtenerIdUsuario(username);
            sessionManager.guardarSesion(userId, username);
            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }
}