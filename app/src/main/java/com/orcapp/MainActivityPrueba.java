package com.orcapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.orcapp.db.SessionManager;
import com.orcapp.login.LoginActivity;

public class MainActivityPrueba extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvWelcome;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogout = findViewById(R.id.btnLogout);

        //agregue esto para una prueba rapida
        // Mostrar mensaje de bienvenida
        String username = sessionManager.obtenerNombreUsuario();
        if (username != null && !username.isEmpty()) {
            tvWelcome.setText("Bienvenido, " + username);
        } else {
            tvWelcome.setText("Bienvenido");
        }

        // Configurar botón de cerrar sesión
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.cerrarSesion();
                startActivity(new Intent(MainActivityPrueba.this, LoginActivity.class));
                finish();
            }
        });
    }
}