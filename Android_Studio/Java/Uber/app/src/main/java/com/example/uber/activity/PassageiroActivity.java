package com.example.uber.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.example.uber.config.ConfiguracaoFirebase;
import com.example.uber.helper.Local;
import com.example.uber.helper.UsuarioFirebase;
import com.example.uber.model.Destino;
import com.example.uber.model.Requisicao;
import com.example.uber.model.Usuario;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.uber.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PassageiroActivity extends AppCompatActivity implements OnMapReadyCallback
{
/*
    Lat / Long destino: -23.572999, -46.643880 Rua Des. Eliseu Guilherme, 147
    Lat / Long passageiro: -23.562791 , -46.654668 Av. Paulista, 1374
    Lat / Long motorista inicial: -23.556407 , -46.662365 Av. Paulista, 2439
    Lat / Long motorista intermediaria: -23.559458 , -46.658470 Av. Paulista, 1900
    Lat / Long motorista final: -23.5628, -46.6547 Av. Paulista, 1380
    Lat / Long destino intermediario1: -23.5672, -46.6496 Av. Paulista, 659
    Lat / Long destino intermediario2: -23.5713, -46.6443 Av. Paulista, 7
    Lat / Long destino intermediario3: -23.5723, -46.6441 Rua Des. Eliseu Guilherme, 10

     */

    private GoogleMap mMap;
    private FirebaseAuth autenticacao;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private EditText editDestino;
    private LinearLayout linearLayoutDestino;
    private Button buttonChamarUber;
    private LatLng localPassageiro;
    private boolean cancelarUber = false;
    private DatabaseReference firebaseRef;
    private Requisicao requisicao;
    private Usuario passageiro;
    private String statusRequisicao;
    private Destino destino;
    private Marker marcadorMotorista;
    private Marker marcadorPassageiro;
    private Marker marcadorDestino;
    private Usuario motorista;
    private LatLng localMotorista;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passageiro);

        inicializarComponentes();

        // Adicionar listener para status da requisição
        verificaStatusRequisicao();

    }

    private void verificaStatusRequisicao()
    {
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        DatabaseReference requisicoes = firebaseRef.child("requisicoes");
        final Query requisicaoPesquisa = requisicoes.orderByChild("passageiro/id")
                .equalTo(usuarioLogado.getId());

        requisicaoPesquisa.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                List<Requisicao> lista = new ArrayList<>();

                for(DataSnapshot ds : snapshot.getChildren())
                {
                    lista.add(ds.getValue(Requisicao.class));

                }

                Collections.reverse(lista);
                if( lista != null && lista.size() >0)
                {
                    requisicao = lista.get(0);

                    if(requisicao != null)
                    {
                        if(!requisicao.getStatus().equals(Requisicao.STATUS_ENCERRADA))
                        {
                            passageiro = requisicao.getPassageiro();
                            localPassageiro = new LatLng(Double.parseDouble(passageiro.getLatitude()),
                                    Double.parseDouble(passageiro.getLongitude()));

                            statusRequisicao = requisicao.getStatus();
                            destino = requisicao.getDestino();
                            if (requisicao.getMotorista() != null)
                            {
                                motorista = requisicao.getMotorista();
                                localMotorista = new LatLng(Double.parseDouble(motorista.getLatitude()),
                                        Double.parseDouble(motorista.getLongitude()));
                            }
                            alterarInterfaceStatusRequisicao(statusRequisicao);
                        }
                    }

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
        if(status != null && !status.isEmpty())
        {
            cancelarUber = false;
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
        else
        {
            // Adicionar marcador passageiro
            adicionarMarcadorPassageiro(localPassageiro, "Seu local");
            centralizarMarcador(localPassageiro);
        }

    }

    private void requisicaoAguardando()
    {
        linearLayoutDestino.setVisibility(View.GONE);
        buttonChamarUber.setText("Cancelar Uber");
        cancelarUber = true;

        // Adicionar marcador passageiro
        adicionarMarcadorPassageiro(localPassageiro, passageiro.getNome());
        centralizarMarcador(localPassageiro);

    }

    private void requisicaoACaminho()
    {
        linearLayoutDestino.setVisibility(View.GONE);
        buttonChamarUber.setText("Motorista a caminho");
        buttonChamarUber.setEnabled(false);

        // Adicionar marcador passageiro
        adicionarMarcadorPassageiro(localPassageiro, passageiro.getNome());

        // Adicionar marcador motorista
        adicionarMarcadorMotorista(localMotorista, motorista.getNome());

        // Centralizar passageiro / motorista
        centralizarDoisMarcadores(marcadorMotorista, marcadorPassageiro);

    }

    private void requisicaoViagem()
    {
        linearLayoutDestino.setVisibility(View.GONE);
        buttonChamarUber.setText("A caminho do destino");
        buttonChamarUber.setEnabled(false);

        // Adicionar marcador motorista
        adicionarMarcadorMotorista(localMotorista, motorista.getNome());

        // Adicionar marcador de destino
        LatLng localDestino = new LatLng(Double.parseDouble(destino.getLatitude()),
                Double.parseDouble(destino.getLongitude()));
        adicionarMarcadorDestino(localDestino, "Destino");

        // Centralizar marcadores motorista / destino
        centralizarDoisMarcadores(marcadorMotorista, marcadorDestino);

    }

    private void requisicaoFinalizada()
    {
        linearLayoutDestino.setVisibility(View.GONE);
        buttonChamarUber.setEnabled(false);

        // Adicionar marcador de destino
        LatLng localDestino = new LatLng(Double.parseDouble(destino.getLatitude()),
                Double.parseDouble(destino.getLongitude()));
        adicionarMarcadorDestino(localDestino, "Destino");
        centralizarMarcador(localDestino);

        // Calcular a distância
        float distancia = Local.calcularDistancia(localPassageiro, localDestino);
        float valor = distancia * 8;
        DecimalFormat decimal = new DecimalFormat("0.00");
        String resultado = decimal.format(valor);

        buttonChamarUber.setText("Corrida finalizada - R$" + resultado);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Total da viagem").setMessage("Sua viagem ficou: R$" + resultado)
                .setCancelable(false)
                .setNegativeButton("Encerrar viagem", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        requisicao.setStatus(Requisicao.STATUS_ENCERRADA);
                        requisicao.atualizarStatus();

                        finish();
                        startActivity(new Intent(getIntent()));
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void requisicaoCancelada()
    {
        linearLayoutDestino.setVisibility(View.VISIBLE);
        buttonChamarUber.setText("Cancelar Uber");
        cancelarUber = false;
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

    private void adicionarMarcadorMotorista(LatLng localizacao, String titulo)
    {
        if(marcadorMotorista != null)
        {
            marcadorMotorista.remove();
        }

        marcadorMotorista = mMap.addMarker(new MarkerOptions().position(localizacao).title(titulo)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.carro)));

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

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        // Recuperar localizção do usuário
        recuperarLocalizacaoUsuario();
    }

    public void chamarUber(View view)
    {
        if( cancelarUber)
        {
            // Uber pode ser cancelado
            // Cancelar requisição
            requisicao.setStatus(Requisicao.STATUS_CANCELADA);
            requisicao.atualizarStatus();
        }
        else
        {
            // Uber não foi chamado
            String enderecoDestino = editDestino.getText().toString();

            if ( !enderecoDestino.equals("") || enderecoDestino != null)
            {
                Address addressDestino = recuperarEndereco(enderecoDestino);
                if( addressDestino != null)
                {
                    final Destino destino = new Destino();
                    destino.setCidade(addressDestino.getSubAdminArea());
                    destino.setCep(addressDestino.getPostalCode());
                    destino.setBairro(addressDestino.getSubLocality());
                    destino.setRua(addressDestino.getThoroughfare());
                    destino.setNumero(addressDestino.getFeatureName());
                    destino.setLatitude(String.valueOf(addressDestino.getLatitude()));
                    destino.setLongitude(String.valueOf(addressDestino.getLongitude()));

                    StringBuilder mensagem = new StringBuilder();
                    mensagem.append("Cidade: " + destino.getCidade() );
                    mensagem.append("\nRua: " + destino.getRua());
                    mensagem.append("\nBairro: " + destino.getBairro());
                    mensagem.append("\nNúmero: " + destino.getNumero());
                    mensagem.append("\nCep: " + destino.getCep());

                    // Exemplo correto: "Avenida Paulista n,1374"
                    // Exemplo correto: "Rua Abilio Soares n, 75"
                    // Exemplo correto: "Avenida Bernardino de Campos n, 158"

                    AlertDialog.Builder builder = new AlertDialog.Builder(this)
                            .setTitle("Confirme seu endereço").setMessage(mensagem)
                            .setPositiveButton("Confirmar", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {
                                    // Salvar a requisição
                                    salvarRequisicao(destino);

                                }
                            }).setNegativeButton("cancelar", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {

                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();


                }
            }
            else
            {
                Toast.makeText(this, "Informe o endereço de destino!",
                        Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void salvarRequisicao(Destino destino)
    {
        Requisicao requisicao = new Requisicao();
        requisicao.setDestino(destino);

        Usuario usuarioPassageiro = UsuarioFirebase.getDadosUsuarioLogado();
        usuarioPassageiro.setLatitude(String.valueOf(localPassageiro.latitude));
        usuarioPassageiro.setLongitude(String.valueOf(localPassageiro.longitude));

        requisicao.setPassageiro(usuarioPassageiro);
        requisicao.setStatus(Requisicao.STATUS_AGUARDANDO);
        requisicao.salvar();

        linearLayoutDestino.setVisibility(View.GONE);
        buttonChamarUber.setText("Cancelar Uber");


    }

    private Address recuperarEndereco(String endereco)
    {
        /*
        * DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(3);

        double lat = Double.parseDouble(df.format(currentUserLocation.latitude));
        double lon = Double.parseDouble(df.format(currentUserLocation.longitude));
        Geocoder geocoder = new Geocoder(myContext, Locale.getDefault());
         */
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try
        {
            List<Address> listaEnderecos = geocoder.getFromLocationName(endereco, 1);
            if(listaEnderecos != null && listaEnderecos.size() > 0)
            {
                Address address = listaEnderecos.get(0);

                return address;

            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;

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
                localPassageiro = new LatLng(latitude, longitude);

                // Atualizar o Geofire
                UsuarioFirebase.atualizarDadosLocalizacao(latitude, longitude);

                // Alterar interface de acordo com o status
                alterarInterfaceStatusRequisicao(statusRequisicao);

                if(statusRequisicao != null && statusRequisicao.isEmpty())
                {

                    if (statusRequisicao.equals(Requisicao.STATUS_VIAGEM) ||
                            statusRequisicao.equals(Requisicao.STATUS_FINALIZADA))
                    {
                        locationManager.removeUpdates(locationListener);
                    }
                    else
                    {
                        // Solicitar atualizações de localização
                        if (ActivityCompat.checkSelfPermission(PassageiroActivity.this,
                                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                        {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                    5000, 10, locationListener);
                        }
                    }
                }
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
        toolbar.setTitle("Iniciar uma viagem");
        setSupportActionBar(toolbar);

        // Inicializar componentes
        editDestino = findViewById(R.id.editDestino);
        linearLayoutDestino = findViewById(R.id.linearLayoutDestino);
        buttonChamarUber = findViewById(R.id.buttonChamarUber);

        // Configurações iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        // Inicializar o mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menuSair:
                autenticacao.signOut();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
