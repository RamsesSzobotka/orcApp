package com.orcapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.orcapp.R;
import com.orcapp.db.SessionManager;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        tvWelcome = findViewById(R.id.tvWelcome);

        // Mostrar mensaje de bienvenida con el nombre de usuario si está disponible
        String username = sessionManager.obtenerNombreUsuario();
        if (username != null && !username.isEmpty()) {
            tvWelcome.setText("Bienvenido, " + username);
        } else {
            tvWelcome.setText("Bienvenido");
        }
    }
}