package com.orcapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.orcapp.db.*;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ClipboardManager;
import android.content.ClipData;


import java.util.Locale;

public class ResultadoActivity extends AppCompatActivity {

    private TextView txtTextoDetectado;
    private Button  btnFiltrar,btnCopiar;
    private String textoRecibido;
    int usuarioId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        setControls();
        setTexto();
        setEvents();
        guardarTexto();
    }
    private void setControls() {
        txtTextoDetectado = findViewById(R.id.txtTextoDetectado);
        btnFiltrar = findViewById(R.id.btnFiltrar);
        btnCopiar = findViewById(R.id.btnCopiar);
    }

    public void setEvents(){
        btnCopiar.setOnClickListener(v -> copiarAlPortapapeles());
        btnFiltrar.setOnClickListener(v -> {
            Intent intent = new Intent(ResultadoActivity.this, FiltrosActivity.class);
            intent.putExtra("textoParaFiltrar", textoRecibido);
            startActivity(intent);
        });
    }
    public void setTexto(){

        textoRecibido = getIntent().getStringExtra("textoDetectado");

        if (textoRecibido != null && !textoRecibido.isEmpty()) {
            txtTextoDetectado.setText(textoRecibido);
        } else {
            txtTextoDetectado.setText("No se recibió texto");
            btnFiltrar.setEnabled(false);
        }
    }
    public void copiarAlPortapapeles() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Texto OCR", textoRecibido);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Texto copiado al portapapeles", Toast.LENGTH_SHORT).show();
    }


    private void guardarTexto() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        DBHelper dbHelper = new DBHelper(this);
        SessionManager smHelper = new SessionManager(this);
        usuarioId = smHelper.obtenerIdUsuario();
        String fecha = sdf.format(new Date());

        boolean guardado = dbHelper.insertarTexto(textoRecibido, fecha, usuarioId);

        if (guardado) {
            Toast.makeText(this, "Texto guardado en historial", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al guardar texto", Toast.LENGTH_SHORT).show();
        }
    }
}
