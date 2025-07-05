package com.orcapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.orcapp.adapters.FiltroAdapter;
import com.orcapp.utils.FiltroUtils;

import java.util.List;

public class FiltrosActivity extends AppCompatActivity {

    //variables a utilizar
    private Spinner spnFiltros;
    private Button btnAplicarFiltro,btnVerOriginal;
    private ListView lvResultados;
    private String textoOg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_filtros);

        inicializarControles();
    }

    private void inicializarControles(){
        spnFiltros = findViewById(R.id.spnFiltros);
        btnAplicarFiltro = findViewById(R.id.btnAplicarFiltro);
        btnVerOriginal = findViewById(R.id.btnVerOriginal);
        lvResultados = findViewById(R.id.lvResultados);

        //Opciones de la lista de filtros
        List<String> filtros = List.of(
                "Correos electrónicos",
                "Fechas",
                "Números telefónicos",
                "Cédulas panameñas"
        );

        //para utilizar el adapter
        FiltroAdapter adapter = new FiltroAdapter(this, filtros);
        spnFiltros.setAdapter(adapter);

        // Obtener texto original desde el intent y guardarlo
        textoOg = getIntent().getStringExtra("textoParaFiltrar");
        if (textoOg == null) textoOg = "";

        // Guardar en SharedPreferences el texto original para cuando quiera verlo nuevamente
        SharedPreferences prefs = getSharedPreferences("orcPrefs", MODE_PRIVATE);
        prefs.edit().putString("textoOriginal", textoOg).apply();
    }


    public void AplicarFiltro(View view){
        //mensajillo para el usuario
        Toast.makeText(this, "Identificando patrones", Toast.LENGTH_SHORT).show();

        String filtroSeleccionado = spnFiltros.getSelectedItem().toString();
        List<String> resultados = null;

        switch (filtroSeleccionado){
            case "Correos electrónicos":
                resultados= FiltroUtils.extraerCorreos(textoOg);
                break;
            case "Fechas":
                resultados= FiltroUtils.extraerFechas(textoOg);
                break;
            case "Números telefónicos":
                resultados= FiltroUtils.extraerTelefonos(textoOg);
                break;
            case "Cédulas panameñas":
                resultados= FiltroUtils.extraerCedulas(textoOg);
                break;
        }

        //mostrar resultados
        if (resultados != null) {
            ArrayAdapter<String> adapterResultados = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, resultados);
            lvResultados.setAdapter(adapterResultados);
            lvResultados.setVisibility(View.VISIBLE);
        }
        // Verificar si hay resultados y los muestra
        if (resultados != null && !resultados.isEmpty()) {
            ArrayAdapter<String> adapterResultados = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, resultados);
            lvResultados.setAdapter(adapterResultados);
            lvResultados.setVisibility(View.VISIBLE);
            //si no encuentra resultados
        } else {
            Toast.makeText(this, "No se encontraron coincidencias.", Toast.LENGTH_SHORT).show();
            lvResultados.setVisibility(View.GONE); // ocultar lista si no hay nada
        }

    }
    public void verTextoOG(View view) {
        SharedPreferences prefs = getSharedPreferences("orcPrefs", MODE_PRIVATE);
        String textoOriginal = prefs.getString("textoOriginal", "No hay texto disponible");

        // Convertir el texto en una lista con un solo elemento
        List<String> listaOriginal = List.of(textoOriginal);

        // Adaptador y muestra
        ArrayAdapter<String> adapterOriginal = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaOriginal);
        lvResultados.setAdapter(adapterOriginal);
        lvResultados.setVisibility(View.VISIBLE);

        Toast.makeText(this, "Mostrando texto original", Toast.LENGTH_SHORT).show();
    }

}