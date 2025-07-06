package com.orcapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.orcapp.models.*;
import com.orcapp.adapters.*;
import com.orcapp.db.*;

public class HistorialActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistorialAdapter adapter;
    private List<TextoEscaneado> listaHistorial;
    private Button btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        setControls();
        cargarHistorial();
        volverEvent();
    }

    public void setControls(){
        btnVolver = findViewById(R.id.btnVolver);
        recyclerView = findViewById(R.id.recyclerViewHistorial);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    private List<TextoEscaneado> cargarHistorialDesdeBD() {
        DBHelper dbHelper = new DBHelper(this);
        SessionManager smHelper = new SessionManager(this);
        int idUsuario = smHelper.obtenerIdUsuario();
        List<TextoEscaneado> escaneos = dbHelper.obtenerHistorialPorUsuario(idUsuario);
        return escaneos;
    }

    public void cargarHistorial() {
        listaHistorial = cargarHistorialDesdeBD();
        adapter = new HistorialAdapter(listaHistorial);

        adapter.setOnItemClickListener(v -> {
            int position = recyclerView.getChildAdapterPosition(v);
            if (position != RecyclerView.NO_POSITION) {
                TextoEscaneado item = listaHistorial.get(position);

                Intent intent = new Intent(HistorialActivity.this, FiltrosActivity.class);
                intent.putExtra("textoParaFiltrar", item.getTexto());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);
    }


    public void volverEvent(){
        btnVolver.setOnClickListener(v ->
                startActivity(new Intent(HistorialActivity.this, WelcomeActivity.class))
        );
    }
}
