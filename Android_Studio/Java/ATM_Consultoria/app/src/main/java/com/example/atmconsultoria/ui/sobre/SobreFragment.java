package com.example.atmconsultoria.ui.sobre;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.atmconsultoria.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

/**
 * A simple {@link Fragment} subclass.
 */
public class SobreFragment extends Fragment
{


    public SobreFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        String descricao = "A ATM Consultoria tem como missão apoiar organizações que desejam" +
        "alcançar o sucesso através da excelência em gestão e da busca pela qualidade.";

        Element versao = new Element();
        versao.setTitle( "Versão 1.0");

        View view = new AboutPage(getActivity())
                .setImage(R.drawable.logo)
                .setDescription(descricao)
                .addGroup("Entre em contato")
                .addEmail("atendimento@atmconsultoria.com.br", "Envie um e-mail")
                .addGroup("Redes Sociais")
                .addGitHub("GiovaniDiasGomes", "GitHub")
                .addYoutube("UC1ZmDgVV5Y8RiFlxQFHEcew", "Youtube")
                .addWebsite("https://www.google.com.br","Acesse nosso site")
                .addFacebook("ATM Consultoria", "Facebook")
                .addTwitter("ATM Consultoria", "Twitter")
                .addInstagram("ATM Consultoria", "Instagram")
                .addPlayStore("com.google.android.apps.plus", "Download App")
                .addItem(versao)
                .create();

        return view;
    }

}
