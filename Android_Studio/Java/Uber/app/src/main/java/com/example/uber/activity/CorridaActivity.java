package com.example.uber.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import com.example.uber.config.ConfiguracaoFirebase;
import com.example.uber.helper.Local;
import com.example.uber.helper.UsuarioFirebase;
import com.example.uber.model.Destino;
import com.example.uber.model.Requisicao;
import com.example.uber.model.Usuario;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.uber.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class CorridaActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng localMotorista;
    private LatLng localPassageiro;
    private Usuario motorista;
    private Usuario passageiro;
    private String idRequisicao;
    private Requisicao requisicao;
    private DatabaseReference firebaseRef;
    private Button buttonAceitarCorrida;
    private Marker marcadorMotorista;
    private Marker marcadorPassageiro;
    private Marker marcadorDestino;
    private String statusRequisicao;
    private boolean requisicaoAtiva;
    private FloatingActionButton fabRota;
    private Destino destino;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corrida);

        inicializarComponentes();

        // Recuperar dados do usuário
        if(getIntent().getExtras().containsKey("idRequisicao") &&
                getIntent().getExtras().containsKey("motorista"))
        {
            Bundle extras = getIntent().getExtras();
            motorista = (Usuario) extras.getSerializable("motorista");
            localMotorista = new LatLng(Double.parseDouble(motorista.getLatitude()),
                    Double.parseDouble(motorista.getLongitude()));
            idRequisicao = extras.getString("idRequisicao");
            requisicaoAtiva = extras.getBoolean("requisicaoAtiva");
            verificaStatusRequisicao();
        }
    }

    private void verificaStatusRequisicao()
    {
        DatabaseReference requisicoes = firebaseRef.child("requisicoes").child(idRequisicao);
        requisicoes.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                // Recuperar a requisição
                requisicao = snapshot.getValue(Requisicao.class);
                if(requisicao != null)
                {
                    passageiro = requisicao.getPassageiro();
                    localPassageiro = new LatLng(Double.parseDouble(passageiro.getLatitude()),
                            Double.parseDouble(passageiro.getLongitude()));

                    statusRequisicao = requisicao.getStatus();
                    destino = requisicao.getDestino();
                    alterarInterfaceStatusRequisicao(statusRequisicao);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    private void alterarInterfaceStatusRequisicao(String status)
    {
        switch (status)
        {
            case Requisicao.STATUS_AGUARDANDO:
                requisicaoAguardando();
                break;
            case Requisicao.STATUS_A_CAMINHO:
                requisicaoACaminho();
                break;
            case Requisicao.STATUS_VIAGEM:
                requisicaoViagem();
                break;
            case Requisicao.STATUS_FINALIZADA:
                requisicaoFinalizada();
                break;
            case Requisicao.STATUS_CANCELADA:
                requisicaoCancelada();
                break;
        }
    }

    private void requisicaoAguardando()
    {
        buttonAceitarCorrida.setText("Aceitar corrida");

        // Exibir marcador do motorista
        adicionarMarcadorMotorista(localMotorista, motorista.getNome());

        centralizarMarcador(localMotorista);
    }

    private void requisicaoACaminho()
    {
        buttonAceitarCorrida.setText("A caminho do passageiro");
        fabRota.setVisibility(View.VISIBLE);

        // Exibir marcador do motorista
        adicionarMarcadorMotorista(localMotorista, motorista.getNome());

        // Exibir marcador do passageiro
        adicionarMarcadorPassageiro(localPassageiro, passageiro.getNome());

        // Centralizar dois marcadores
        centralizarDoisMarcadores(marcadorMotorista, marcadorPassageiro);

        // Iniciar monitoramento do motorista/passageiro
        iniciarMonitoramento(motorista, localPassageiro, Requisicao.STATUS_VIAGEM);
    }

    private void requisicaoViagem()
    {
        // Alterar interface
        fabRota.setVisibility(View.VISIBLE);
        buttonAceitarCorrida.setText("A caminho do destino");

        // Exibir marcador do motorista
        adicionarMarcadorMotorista(localMotorista, motorista.getNome());

        // Exibir marcador do destino
        LatLng localDestino = new LatLng(Double.parseDouble(destino.getLatitude()),
                Double.parseDouble(destino.getLongitude()));
        adicionarMarcadorDestino(localDestino, "Destino");

        // Centralizar marcadores motorista / destino
        centralizarDoisMarcadores(marcadorMotorista, marcadorDestino);

        // Iniciar monitoramento do motorista/passageiro
        iniciarMonitoramento(motorista, localDestino, Requisicao.STATUS_FINALIZADA);

    }

    private void requisicaoFinalizada()
    {
        fabRota.setVisibility(View.GONE);
        requisicaoAtiva = false;

        if(marcadorMotorista != null)
        {
            marcadorMotorista.remove();
        }

        if(marcadorDestino != null)
        {
            marcadorDestino.remove();
        }

        // Exibir marcador de destino
        LatLng localDestino = new LatLng(Double.parseDouble(destino.getLatitude()),
                Double.parseDouble(destino.getLongitude()));
        adicionarMarcadorDestino(localDestino, "Destino");

        centralizarMarcador(localDestino);

        // Calcular a distância
        float distancia = Local.calcularDistancia(localPassageiro, localDestino);
        float valor = distancia * 8;
        DecimalFormat decimal = new DecimalFormat("0.00");
        String resultado = decimal.format(valor);

        buttonAceitarCorrida.setText("Corrida finalizada - R$" + resultado);

    }

    private void requisicaoCancelada()
    {
        Toast.makeText(this, "Requisição cancelada pelo passageiro",
                Toast.LENGTH_SHORT).show();
        startActivity(new Intent(CorridaActivity.this, RequisicoesActivity.class));
    }

    private void iniciarMonitoramento(final Usuario usuarioOrigem, LatLng localDestino, final String status)
    {
        // Inicializar GeoFire
        DatabaseReference localUsuario = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("local_usuario");
        GeoFire geoFire = new GeoFire(localUsuario);

        // Adicionar círculo no passageiro
        final Circle circulo = mMap.addCircle(new CircleOptions().center(localDestino).radius(50)
                .fillColor(Color.argb(90, 255, 153, 0))
                .strokeColor(Color.argb(190, 255, 153, 0)));

        final GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(localDestino.latitude,
                localDestino.longitude),0.05);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener()
        {
            @Override
            public void onKeyEntered(String key, GeoLocation location)
            {
                //if(key.equals(passageiro.getId()))
                //{
                //    Log.d("onKeyEntered", "onKeyEntered: Passageiro dentro da área");
                //}
                //else
                if (key.equals(usuarioOrigem.getId()))
                {
                    //Log.d("onKeyEntered", "onKeyEntered: Motorista dentro da área");
                    // Alterar status da requisição
                    requisicao.setStatus(status);
                    requisicao.atualizarStatus();

                    // Remover listener
                    geoQuery.removeAllListeners();
                    circulo.remove();

                }
            }

            @Override
            public void onKeyExited(String key)
            {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location)
            {

            }

            @Override
            public void onGeoQueryReady()
            {

            }

            @Override
            public void onGeoQueryError(DatabaseError error)
            {

            }
        });

    }

    private void centralizarDoisMarcadores(Marker marcador1, Marker marcador2)
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(marcador1.getPosition());
        builder.include(marcador2.getPosition());

        LatLngBounds bounds = builder.build();

        int largura = getResources().getDisplayMetrics().widthPixels;
        int altura = getResources().getDisplayMetrics().heightPixels;
        int espacoInterno = (int) (largura * 0.20);

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, largura, altura, espacoInterno));
    }

    private void centralizarMarcador(LatLng local)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(local, 18));
    }

    private void adicionarMarcadorMotorista(LatLng localizacao, String titulo)
    {
        if(marcadorMotorista != null)
        {
            marcadorMotorista.remove();
        }

        marcadorMotorista = mMap.addMarker(new MarkerOptions().position(localizacao).title(titulo)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.carro)));

    }

    private void adicionarMarcadorPassageiro(LatLng localizacao, String titulo)
    {
        if(marcadorPassageiro != null)
        {
            marcadorPassageiro.remove();
        }

        marcadorPassageiro = mMap.addMarker(new MarkerOptions().position(localizacao).title(titulo)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.usuario)));

    }

    private void adicionarMarcadorDestino(LatLng localizacao, String titulo)
    {
        if(marcadorPassageiro != null)
        {
            marcadorPassageiro.remove();
        }

        if(marcadorDestino != null)
        {
            marcadorDestino.remove();
        }

        marcadorDestino = mMap.addMarker(new MarkerOptions().position(localizacao).title(titulo)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.destino)));

    }

    public void aceitarCorrida(View view)
    {
        // Configurar requisição
        requisicao = new Requisicao();
        requisicao.setId(idRequisicao);
        requisicao.setMotorista(motorista);
        requisicao.setStatus(Requisicao.STATUS_A_CAMINHO);

        requisicao.atualizar();
    }

    private void recuperarLocalizacaoUsuario()
    {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(@NonNull Location location)
            {
                // Recuperar latitude e longitude
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                localMotorista = new LatLng(latitude, longitude);

                // Atualizar o Geofire
                UsuarioFirebase.atualizarDadosLocalizacao(latitude, longitude);

                // Atualizar localização motorista no Firebase
                motorista.setLatitude(String.valueOf(latitude));
                motorista.setLongitude(String.valueOf(longitude));
                requisicao.setMotorista(motorista);
                requisicao.atualizarLocalizacaoMotorista();

                alterarInterfaceStatusRequisicao(statusRequisicao);

            }
        };

        // Solicitar atualizações de localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,
                    10, locationListener);
        }
    }

    private void inicializarComponentes()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Iniciar corrida");

        // Inicializar componentes
        buttonAceitarCorrida = findViewById(R.id.buttonAceitarCorrida);

        // Configurações iniciais
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        // Inicializar o mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Adicionar evento de clique no FAB Rota
        fabRota = findViewById(R.id.fabRota);
        fabRota.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String status = statusRequisicao;
                if(status != null && !status.isEmpty())
                {
                    String lat = "";
                    String lng = "";

                    switch (status)
                    {
                        case Requisicao.STATUS_A_CAMINHO:
                            lat = String.valueOf(localPassageiro.latitude);
                            lng = String.valueOf(localPassageiro.longitude);
                            break;
                        case Requisicao.STATUS_VIAGEM:
                            lat = destino.getLatitude();
                            lng = destino.getLongitude();
                            break;
                    }

                    // Abrir rota
                    String latLong = lat + "," + lng;
                    Uri uri = Uri.parse("google.navigation:q=" + latLong + "&mode=d");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);

                }


            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        // Recuperar localizção do usuário
        recuperarLocalizacaoUsuario();
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        if(requisicaoAtiva)
        {
            Toast.makeText(CorridaActivity.this,
                    "Necessário encerrar a requisição atual!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent = new Intent(CorridaActivity.this, RequisicoesActivity.class);
            startActivity(intent);
        }

        // Verificar o status da requisição para encerrar
        if(statusRequisicao != null && !statusRequisicao.isEmpty())
        {
            requisicao.setStatus(Requisicao.STATUS_ENCERRADA);
            requisicao.atualizarStatus();
        }

        return false;
    }

}
