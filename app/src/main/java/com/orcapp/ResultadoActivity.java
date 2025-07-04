package com.orcapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ClipboardManager;
import android.content.ClipData;


import java.util.Locale;

public class ResultadoActivity extends AppCompatActivity {

    TextView txtTextoDetectado;
    Button btnGuardar, btnFiltrar,btnCopiar;
    ;
    String textoRecibido;
    int usuarioId = 1; // Reemplaza con el ID real del usuario logueado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        setControls();
        setTexto();
        btnCopiar.setOnClickListener(v -> copiarAlPortapapeles());
        //btnGuardar.setOnClickListener(v -> guardarTexto());
        /*
        btnFiltrar.setOnClickListener(v -> {
            Intent intent = new Intent(ResultadoActivity.this, FiltrosActivity.class);
            intent.putExtra("textoParaFiltrar", textoRecibido);
            startActivity(intent);
        });*/
    }
    private void setControls() {
        txtTextoDetectado = findViewById(R.id.txtTextoDetectado);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnFiltrar = findViewById(R.id.btnFiltrar);
        btnCopiar = findViewById(R.id.btnCopiar);
    }

    public void setTexto(){

        textoRecibido = getIntent().getStringExtra("textoDetectado");

        if (textoRecibido != null && !textoRecibido.isEmpty()) {
            txtTextoDetectado.setText(textoRecibido);
        } else {
            txtTextoDetectado.setText("No se recibió texto");
            btnGuardar.setEnabled(false);
            btnFiltrar.setEnabled(false);
        }
    }
    public void copiarAlPortapapeles() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Texto OCR", textoRecibido);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Texto copiado al portapapeles", Toast.LENGTH_SHORT).show();
    }

    /*
    private void guardarTexto() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        DBHelper db = new DBHelper(this);
        String fecha = sdf.format(new Date());

        boolean guardado = db.insertarTexto(textoRecibido, fecha, usuarioId);

        if (guardado) {
            Toast.makeText(this, "Texto guardado en historial", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al guardar texto", Toast.LENGTH_SHORT).show();
        }
    }*/
}
