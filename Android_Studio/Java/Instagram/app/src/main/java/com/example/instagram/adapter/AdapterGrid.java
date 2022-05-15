package com.example.instagram.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.instagram.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

public class AdapterGrid extends ArrayAdapter<String>
{
    private Context context;
    private int layoutResource;
    private List<String> urlFotos;

    public AdapterGrid(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutResource = resource;
        this.urlFotos = objects;
    }

    public class ViewHolder
    {
        ImageView imagem;
        ProgressBar progressBar;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        // Caso a view não esteja inflada, precisamos inflar
        final ViewHolder viewHolder;
        if(convertView == null)
        {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder.progressBar = convertView.findViewById(R.id.progressGridPerfil);
            viewHolder.imagem = convertView.findViewById(R.id.imageGridPerfil);

            convertView.setTag(viewHolder);

        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Recuperar dados da imagem
        String urlImagem = getItem(position);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(urlImagem, viewHolder.imagem, new ImageLoadingListener()
        {
            @Override
            public void onLoadingStarted(String imageUri, View view)
            {
                viewHolder.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason)
            {
                viewHolder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
            {
                viewHolder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view)
            {
                viewHolder.progressBar.setVisibility(View.GONE);
            }
        });

        return convertView;
    }
}
