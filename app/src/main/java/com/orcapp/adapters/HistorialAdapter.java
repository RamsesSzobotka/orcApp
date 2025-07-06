package com.orcapp.adapters;
import android.view.LayoutInflater;

import com.orcapp.R;
import com.orcapp.models.*;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {
    private List<TextoEscaneado> lista;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView texto, fecha;
        public ViewHolder(View v) {
            super(v);
            texto = v.findViewById(R.id.txtContenido);
            fecha = v.findViewById(R.id.txtFecha);
        }
    }

    public HistorialAdapter(List<TextoEscaneado> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public HistorialAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_template, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextoEscaneado item = lista.get(position);
        holder.texto.setText(item.getTexto());
        holder.fecha.setText(item.getFecha());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}
