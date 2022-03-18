package com.example.aprendaingles.Fragment;


import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.aprendaingles.R;


public class VogaisFragment extends Fragment implements View.OnClickListener
{
    private ImageButton botaoA, botaoE, botaoI, botaoO, botaoU;
    private MediaPlayer mediaPlayer;

    public VogaisFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vogais, container, false);

        botaoA = view.findViewById(R.id.botaoA);
        botaoE = view.findViewById(R.id.botaoE);
        botaoI = view.findViewById(R.id.botaoI);
        botaoO = view.findViewById(R.id.botaoO);
        botaoU = view.findViewById(R.id.botaoU);

        // Aplica eventos de click
        botaoA.setOnClickListener(this);
        botaoE.setOnClickListener(this);
        botaoI.setOnClickListener(this);
        botaoO.setOnClickListener(this);
        botaoU.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.botaoA:
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.a);
                tocarSom();
                break;
            case R.id.botaoE:
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.e);
                tocarSom();
                break;
            case R.id.botaoI:
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.i);
                tocarSom();
                break;
            case R.id.botaoO:
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.o);
                tocarSom();
                break;
            case R.id.botaoU:
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.u);
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
