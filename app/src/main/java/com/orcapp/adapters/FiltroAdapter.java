package com.orcapp.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

public class FiltroAdapter extends ArrayAdapter <String>{

    public FiltroAdapter (@NonNull Context context, @NonNull List<String> filtros){
        super(context, android.R.layout.simple_spinner_item, filtros);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }
}
