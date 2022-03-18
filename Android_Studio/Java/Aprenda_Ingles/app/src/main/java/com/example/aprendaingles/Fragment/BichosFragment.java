package com.example.aprendaingles.Fragment;


import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.aprendaingles.R;


public class BichosFragment extends Fragment implements View.OnClickListener {
    private ImageButton botaoCao, botaoGato, botaoLeao, botaoMacaco, botaoOvelha, botaoVaca;
    private MediaPlayer mediaPlayer;

    public BichosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_bichos, container, false);

        botaoCao = view.findViewById(R.id.botaoCao);
        botaoGato = view.findViewById(R.id.botaoGato);
        botaoLeao = view.findViewById(R.id.botaoLeao);
        botaoMacaco = view.findViewById(R.id.botaoMacaco);
        botaoOvelha = view.findViewById(R.id.botaoOvelha);
        botaoVaca = view.findViewById(R.id.botaoVaca);

        // Aplica eventos de click
        botaoCao.setOnClickListener(this);
        botaoGato.setOnClickListener(this);
        botaoLeao.setOnClickListener(this);
        botaoMacaco.setOnClickListener(this);
        botaoOvelha.setOnClickListener(this);
        botaoVaca.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.botaoCao:
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.dog);
                tocarSom();
                break;
            case R.id.botaoGato:
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.cat);
                tocarSom();
                break;
            case R.id.botaoLeao:
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.lion);
                tocarSom();
                break;
            case R.id.botaoMacaco:
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.monkey);
                tocarSom();
                break;
            case R.id.botaoOvelha:
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.sheep);
                tocarSom();
                break;
            case R.id.botaoVaca:
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.cow);
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




