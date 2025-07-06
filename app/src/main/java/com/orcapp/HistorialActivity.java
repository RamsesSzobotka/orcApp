package com.orcapp;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        recyclerView = findViewById(R.id.recyclerViewHistorial);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //
        listaHistorial = cargarHistorialDesdeBD();
        adapter = new HistorialAdapter(listaHistorial);
        recyclerView.setAdapter(adapter);
    }

    private List<TextoEscaneado> cargarHistorialDesdeBD() {
        DBHelper dbHelper = new DBHelper(this);
        SessionManager smHelper = new SessionManager(this);
        int idUsuario = smHelper.obtenerIdUsuario();
        List<TextoEscaneado> escaneos = dbHelper.obtenerHistorialPorUsuario(idUsuario);
        return escaneos;
    }
}
