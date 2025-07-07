package com.orcapp.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FiltroUtils {

    //para correos electronicos: modelo@dominio.com
    public static List<String> extraerCorreos(String texto) {
        List<String> resultados = new ArrayList<>();
        Pattern pattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        Matcher matcher = pattern.matcher(texto);

        while (matcher.find()) {
            resultados.add(matcher.group());
        }

        return resultados;
    }

    //para las fechas: 02/07/2025, 1/1/25, 3-3-23, 03-03-2023, etc.
    public static List<String> extraerFechas(String texto) {
        List<String> resultados = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\b\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}\\b");
        Matcher matcher = pattern.matcher(texto);

        while (matcher.find()) {
            resultados.add(matcher.group());
        }

        return resultados;
    }
    //para numeros de telefonos: +50760000000 o 60000000, 6565-1234, 233-4567
    public static List<String> extraerTelefonos(String texto) {
        List<String> resultados = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\+507\\s?)?(\\d{4}-\\d{4}|\\d{7,8})\\b");
        Matcher matcher = pattern.matcher(texto);

        while (matcher.find()) {
            resultados.add(matcher.group());
        }

        return resultados;
    }

    //para cedulas panameñas
    public static List<String> extraerCedulas(String texto) {
        List<String> resultados = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\b\\d{1,2}-\\d{3,4}-\\d{3,6}\\b");
        Matcher matcher = pattern.matcher(texto);

        while (matcher.find()) {
            resultados.add(matcher.group());
        }

        return resultados;
    }
}
