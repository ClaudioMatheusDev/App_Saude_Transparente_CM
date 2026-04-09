package com.example.appmaissaude;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.util.Locale;

public class MapPickerActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 101;

    private MapView mapView;
    private Marker marcador;
    private Button btnConfirmar;
    private TextView txtCoordenadas;
    private boolean modoVisualizacao = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE));
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_map_picker);

        modoVisualizacao = "viewer".equals(getIntent().getStringExtra("modo"));

        mapView = findViewById(R.id.mapView);
        btnConfirmar = findViewById(R.id.btnConfirmarLocal);
        Button btnMinhaLocalizacao = findViewById(R.id.btnMinhaLocalizacao);
        ImageView btnVoltar = findViewById(R.id.btnVoltarMap);
        TextView txtTitulo = findViewById(R.id.txtTituloMap);
        View txtDica = findViewById(R.id.txtDicaMapa);
        txtCoordenadas = findViewById(R.id.txtCoordenadasMap);

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(14.0);
        // Centro padrão: Brasília
        mapView.getController().setCenter(new GeoPoint(-15.77972, -47.92972));

        if (modoVisualizacao) {
            txtTitulo.setText("Localização da denúncia");
            btnConfirmar.setVisibility(View.GONE);
            btnMinhaLocalizacao.setVisibility(View.GONE);
            txtDica.setVisibility(View.GONE);

            double lat = getIntent().getDoubleExtra("latitude", 0.0);
            double lon = getIntent().getDoubleExtra("longitude", 0.0);
            if (lat != 0.0 || lon != 0.0) {
                GeoPoint ponto = new GeoPoint(lat, lon);
                mapView.getController().setCenter(ponto);
                mapView.getController().setZoom(16.0);
                adicionarMarcador(ponto);
                txtCoordenadas.setText(String.format(Locale.getDefault(), "📍 %.5f, %.5f", lat, lon));
                txtCoordenadas.setVisibility(View.VISIBLE);
            }
        } else {
            // Modo picker — tap no mapa para marcar
            MapEventsReceiver receptor = new MapEventsReceiver() {
                @Override
                public boolean singleTapConfirmedHelper(GeoPoint p) {
                    adicionarMarcador(p);
                    btnConfirmar.setEnabled(true);
                    txtCoordenadas.setText(String.format(Locale.getDefault(),
                            "📍 %.5f, %.5f", p.getLatitude(), p.getLongitude()));
                    txtCoordenadas.setVisibility(View.VISIBLE);
                    return true;
                }

                @Override
                public boolean longPressHelper(GeoPoint p) { return false; }
            };
            mapView.getOverlays().add(new MapEventsOverlay(receptor));

            btnConfirmar.setOnClickListener(v -> {
                if (marcador != null) {
                    Intent result = new Intent();
                    result.putExtra("latitude", marcador.getPosition().getLatitude());
                    result.putExtra("longitude", marcador.getPosition().getLongitude());
                    setResult(Activity.RESULT_OK, result);
                    finish();
                }
            });

            btnMinhaLocalizacao.setOnClickListener(v -> usarMinhaLocalizacao());
        }

        btnVoltar.setOnClickListener(v -> finish());
    }

    private void adicionarMarcador(GeoPoint ponto) {
        if (marcador == null) {
            marcador = new Marker(mapView);
            marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mapView.getOverlays().add(marcador);
        }
        marcador.setPosition(ponto);
        mapView.invalidate();
    }

    private void usarMinhaLocalizacao() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION);
            return;
        }
        obterLocalizacaoAtual();
    }

    private void obterLocalizacaoAtual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location localizacao = null;
        for (String provider : lm.getProviders(true)) {
            Location l = lm.getLastKnownLocation(provider);
            if (l != null) {
                localizacao = l;
                break;
            }
        }

        if (localizacao != null) {
            GeoPoint ponto = new GeoPoint(localizacao.getLatitude(), localizacao.getLongitude());
            mapView.getController().animateTo(ponto);
            mapView.getController().setZoom(16.0);
            adicionarMarcador(ponto);
            btnConfirmar.setEnabled(true);
            txtCoordenadas.setText(String.format(Locale.getDefault(),
                    "📍 %.5f, %.5f", ponto.getLatitude(), ponto.getLongitude()));
            txtCoordenadas.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "Localização não disponível. Toque no mapa para marcar.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obterLocalizacaoAtual();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
