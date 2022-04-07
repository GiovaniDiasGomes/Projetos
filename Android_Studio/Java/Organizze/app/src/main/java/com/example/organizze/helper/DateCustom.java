package com.example.organizze.helper;

import java.text.SimpleDateFormat;

public class DateCustom
{
    public static String dataAtual()
    {
        long data = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/YYYY");
        String dataString = simpleDateFormat.format(data);
        return dataString;
    }

    public static String mesAnoDataEscolhida(String data)
    {
        String retornoData[] = data.split("/");
        String dia = retornoData[0]; // recebe o item 0 do array
        String mes = retornoData[1]; // recebe o item 1 do array
        String ano = retornoData[2]; // recebe o item 2 do array

        String mesAno = mes + ano;

        return mesAno;
    }
}
