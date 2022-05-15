package com.example.instagram.fragment;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.instagram.R;
import com.example.instagram.activity.FiltroActivity;
import com.example.instagram.helper.Permissao;

import java.io.ByteArrayOutputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostagemFragment extends Fragment
{
    private Button buttonAbrirGaleria, buttonAbrirCamera;
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private String[] permissoesNecessarias = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};


    public PostagemFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_postagem, container, false);

        // Validar permissões
        Permissao.validarPermissoes(permissoesNecessarias, getActivity(), 1);

        // Inicializar os componentes
        buttonAbrirGaleria = view.findViewById(R.id.buttonAbrirGaleria);
        buttonAbrirCamera = view.findViewById(R.id.buttonAbrirCamera);

        // Adicionar evento de clique no botão da galeria
        buttonAbrirGaleria.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if( intent.resolveActivity(getActivity().getPackageManager()) != null )
                {
                    startActivityForResult(intent,SELECAO_GALERIA );
                }
            }
        });

        // Adicionar evento de clique no botão da câmera
        buttonAbrirCamera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getActivity().getPackageManager()) != null)
                {
                    startActivityForResult(intent,SELECAO_CAMERA );
                }
            }
        });



        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == getActivity().RESULT_OK)
        {
            Bitmap imagem = null;

            try
            {
                // Validar o tipo de seleção da imagem
                switch (requestCode)
                {
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),
                                localImagemSelecionada);
                        break;
                }

                // Validar imagem selecionada
                if(imagem != null)
                {
                    // Converte imagem em byte array
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    // Enviar imagem escolhida para aplicação de filtro
                    Intent intent = new Intent(getActivity(), FiltroActivity.class);
                    intent.putExtra("fotoEscolhida", dadosImagem);
                    startActivity(intent);
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }
}
