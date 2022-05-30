package com.example.youtube.giovanidiasgomes.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.youtube.giovanidiasgomes.R;
import com.example.youtube.giovanidiasgomes.adapter.AdapterVideo;
import com.example.youtube.giovanidiasgomes.api.YoutubeService;
import com.example.youtube.giovanidiasgomes.helper.RetrofitConfig;
import com.example.youtube.giovanidiasgomes.helper.YoutubeConfig;
import com.example.youtube.giovanidiasgomes.listener.RecyclerItemClickListener;
import com.example.youtube.giovanidiasgomes.model.Item;
import com.example.youtube.giovanidiasgomes.model.Resultado;
import com.example.youtube.giovanidiasgomes.model.Video;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity
{

    //private YouTubePlayerView youTubePlayerView;
    //private YouTubePlayer.PlaybackEventListener playbackEventListener;
    //private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener;
    private RecyclerView recyclerVideos;
    private MaterialSearchView searchView;
    //private List<Video> videos = new ArrayList<>();
    private List<Item> videos = new ArrayList<>();
    private Resultado resultado;
    private AdapterVideo adapterVideo;

    // Retrofit
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar componentes
        recyclerVideos = findViewById(R.id.recyclerVideos);
        searchView = findViewById(R.id.searchView);

        // Configurações iniciais
        retrofit = RetrofitConfig.getRetrofit();

        // Configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Youtube");
        setSupportActionBar(toolbar);

        // Recuperar vídeos
        recuperarVideos("");


        // Configurar métodos para SearchView
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                recuperarVideos(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener()
        {
            @Override
            public void onSearchViewShown()
            {

            }

            @Override
            public void onSearchViewClosed()
            {
                recuperarVideos("");
            }
        });


        /*playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener()
        {
            @Override
            public void onLoading()
            {
                Toast.makeText(MainActivity.this, "Video carregando",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoaded(String s)
            {
                Toast.makeText(MainActivity.this, "Video carregado",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdStarted()
            {
                Toast.makeText(MainActivity.this, "Propaganda iniciou",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVideoStarted()
            {
                Toast.makeText(MainActivity.this, "Video está començando",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVideoEnded()
            {
                Toast.makeText(MainActivity.this, "Video chegou ao final",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason)
            {
                Toast.makeText(MainActivity.this,
                        "Erro ao recuperar enventos de carregamento", Toast.LENGTH_SHORT)
                        .show();
            }
        };

        playbackEventListener = new YouTubePlayer.PlaybackEventListener()
        {
            @Override
            public void onPlaying()
            {
                Toast.makeText(MainActivity.this, "Video executando",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPaused()
            {
                Toast.makeText(MainActivity.this, "Video pausado",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopped()
            {
                Toast.makeText(MainActivity.this, "Video parado",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBuffering(boolean b)
            {
                Toast.makeText(MainActivity.this, "Video pré-carregando",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSeekTo(int i)
            {
                Toast.makeText(MainActivity.this, "Movimentando SeekBar",
                        Toast.LENGTH_SHORT).show();
            }
        };

        // Inicializar componentes
        youTubePlayerView = findViewById(R.id.viewYoutubePlayer);
        youTubePlayerView.initialize(GOOGLE_API_KEY, this);*/

    }

    private void recuperarVideos(String pesquisa)
    {
        String q = pesquisa.replaceAll(" ", "+");

        YoutubeService youtubeService = retrofit.create(YoutubeService.class);
        youtubeService.recuperarVideos( "snippet", "date", "20",
                YoutubeConfig.YOUTUBE_API_KEY, YoutubeConfig.CANAL_ID, q)
                .enqueue(new Callback<Resultado>()
        {
            @Override
            public void onResponse(Call<Resultado> call, Response<Resultado> response)
            {
                Log.d("resultado", "reusltado:: " + response.toString());
                if(response.isSuccessful())
                {
                    resultado = response.body();
                    videos = resultado.items;
                    configurarRecyclerView();

                }
            }

            @Override
            public void onFailure(Call<Resultado> call, Throwable t)
            {

            }
        });

    }

    public void configurarRecyclerView()
    {
        adapterVideo = new AdapterVideo(videos, this);
        recyclerVideos.setLayoutManager(new LinearLayoutManager(this));
        recyclerVideos.setHasFixedSize(true);
        recyclerVideos.setAdapter(adapterVideo);

        // Configurar evento de clique
        recyclerVideos.addOnItemTouchListener(new RecyclerItemClickListener(this,
                recyclerVideos, new RecyclerItemClickListener.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                Item video = videos.get(position);
                String idVideo = video.id.videoId;

                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                intent.putExtra("idVideo", idVideo);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position)
            {

            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {

            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.menu_search);
        searchView.setMenuItem(item);

        return true;
    }

    /*@Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean foiRestaurado)
    {
        Toast.makeText(this, "Player iniciado com sucesso", Toast.LENGTH_SHORT).show();
        Log.i("estado_player", "estado: " + foiRestaurado);

        //youTubePlayer.setPlaybackEventListener(playbackEventListener);
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);

        if (!foiRestaurado)
        {
            // Carregar vídeo direto
            //youTubePlayer.loadVideo("Osk3GBHhW0U");
            // Video disponível na tela, clicar no player para carregar o video
            //youTubePlayer.cueVideo("Osk3GBHhW0U");
            // Carregar playlist
            youTubePlayer.cuePlaylist("PLytxfSZMFqOEKu7cxwHZS3r1aYOnBuWus");
        }

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult)
    {
        Toast.makeText(this, "Erro ao iniciar Player"
                        + youTubeInitializationResult.toString(), Toast.LENGTH_SHORT).show();
    }*/
}
