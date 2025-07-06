package com.orcapp.login;

import android.content.Intent;
import android.os.Bundle;
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

    // Componentes de la interfaz
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    // Utilidades de base de datos y sesión
    private DBHelper dbHelper;
    private SessionManager sessionManager;

    // Configura la actividad al ser creada: interfaz, objetos, sesión y listeners
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setControls();
        initObjects();
        checkActiveSession();
        setListeners();
    }

    // Enlaza los elementos visuales con sus IDs del layout
    private void setControls() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
    }

    // Inicializa la base de datos y el manejador de sesión
    private void initObjects() {
        dbHelper = new DBHelper(this);
        sessionManager = new SessionManager(this);
    }

    // Verifica si ya hay una sesión activa y redirige al usuario si es así
    private void checkActiveSession() {
        if (sessionManager.haySesionActiva()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    // Asigna las acciones a los botones de login y registro
    private void setListeners() {
        btnLogin.setOnClickListener(v -> loginUser());
        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    // Lógica para validar y realizar el inicio de sesión
    private void loginUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        String error = ValidationUtils.validarLogin(username, password);
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            return;
        }

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