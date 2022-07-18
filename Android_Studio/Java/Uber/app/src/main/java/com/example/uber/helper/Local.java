package com.example.uber.helper;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.Map;

public class Local
{
    public static float calcularDistancia(LatLng latLngInicial, LatLng latLngFinal)
    {
        Location localInicial = new Location("Local inicial");
        localInicial.setLatitude(latLngInicial.latitude);
        localInicial.setLongitude(latLngInicial.longitude);

        Location localFinal = new Location("Local Final");
        localFinal.setLatitude(latLngFinal.latitude);
        localFinal.setLongitude(latLngFinal.longitude);

        // Calcular distância - Resultado em metros
        // dividir por 1000 para converter em Km
        float distancia = localInicial.distanceTo(localFinal) / 1000;

        return distancia;
    }

    public static String formatarDistancia(float distancia)
    {
        String distanciaFormatada;
        if (distancia < 1)
        {
            distancia = distancia * 1000;
            distanciaFormatada = Math.round(distancia) + "m";
        }
        else
        {
            DecimalFormat decimal = new DecimalFormat("0.0");
            distanciaFormatada = decimal.format(distancia) + "Km";
        }

        return distanciaFormatada;
    }

}
