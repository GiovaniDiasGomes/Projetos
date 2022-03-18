package com.example.aprendaingles.Fragment;


import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.aprendaingles.R;


public class NumerosFragment  extends Fragment implements View.OnClickListener
{
    private ImageButton botaoUm, botaoDois, botaoTres, botaoQuatro, botaoCinco, botaoSeis;
    private MediaPlayer mediaPlayer;

    public NumerosFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_numeros, container, false);

        botaoUm = view.findViewById(R.id.botaoUm);
        botaoDois = view.findViewById(R.id.botaoDois);
        botaoTres = view.findViewById(R.id.botaoTres);
        botaoQuatro = view.findViewById(R.id.botaoQuatro);
        botaoCinco = view.findViewById(R.id.botaoCinco);
        botaoSeis = view.findViewById(R.id.botaoSeis);

        // Aplica eventos de click
        botaoUm.setOnClickListener(this);
        botaoDois.setOnClickListener(this);
        botaoTres.setOnClickListener(this);
        botaoQuatro.setOnClickListener(this);
        botaoCinco.setOnClickListener(this);
        botaoSeis.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.botaoUm:
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.one);
                tocarSom();
                break;
            case R.id.botaoDois:
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.two);
                tocarSom();
                break;
            case R.id.botaoTres:
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.three);
                tocarSom();
                break;
            case R.id.botaoQuatro:
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.four);
                tocarSom();
                break;
            case R.id.botaoCinco:
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.five);
                tocarSom();
                break;
            case R.id.botaoSeis:
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.six);
                tocarSom();
                break;
        }
    }
    public void tocarSom()
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer)
                {
                    mediaPlayer.release();
                }
            });
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (mediaPlayer !=null)
        {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
