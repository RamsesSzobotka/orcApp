package com.orcapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.orcapp.db.DBHelper;
import com.orcapp.db.SessionManager;
import com.orcapp.login.LoginActivity;

public class WelcomeActivity extends AppCompatActivity {
    private Button btnEscanear, btnHistorial, btnCerrarSesion;
    private TextView txtBienvenida;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        setControls();
        setEvents();
        nameCharged();
    }

    private void setControls() {
        btnEscanear = findViewById(R.id.btnEscanear);
        btnHistorial = findViewById(R.id.btnHistorial);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        txtBienvenida = findViewById(R.id.txtBienvenida);
    }

    public void setEvents() {
        btnEscanear.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        btnHistorial.setOnClickListener(v ->{
            Intent intent = new Intent(this, HistorialActivity.class);
            startActivity(intent);
        });
        btnCerrarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            SessionManager sessionManager = new SessionManager(this);
            sessionManager.cerrarSesion();
            startActivity(intent);
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        });
    }

    public void nameCharged(){
        SessionManager session = new SessionManager(this);
        txtBienvenida.setText("¡Bienvenid@," + session.obtenerNombreUsuario()+"! ");
    }
}