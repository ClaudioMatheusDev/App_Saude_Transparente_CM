package com.example.appmaissaude;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.io.File;
import java.util.List;

public class NovoRegistroActivity extends BaseActivity {

    private String categoriaSelecionada = "";
    private Uri imagemSelecionadaUri = null;
    private Registro registroEmEdicao = null;
    private boolean modoEdicao = false;
    private double mapaLat = 0.0;
    private double mapaLon = 0.0;
    private MapView mapViewInline;
    private Marker marcadorInline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE));
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_novo_registro);

        // VERIFICAR SE É MODO DE EDIÇÃO
        if (getIntent().hasExtra("registro_para_editar")) {
            registroEmEdicao = getIntent().getParcelableExtra("registro_para_editar");
            modoEdicao = true;
        }

        // Referências dos componentes
        ImageView btnVoltarNovo = findViewById(R.id.btnVoltarNovo);
        Button btnEnviar = findViewById(R.id.btnEnviar);
        EditText editDescricao = findViewById(R.id.editDescricao);
        Spinner spinnerCentrosSaude = findViewById(R.id.spinnerCentrosSaude);
        ImageView btnAddFoto = findViewById(R.id.btnAddFoto);
        ImageView imgPreview = findViewById(R.id.imgPreview);
        TextView txtTitulo = findViewById(R.id.txtTituloNovoRegistro);
        Button btnMarcarMapa = findViewById(R.id.btnMarcarMapa);
        TextView txtCoordsMapa = findViewById(R.id.txtCoordenadasSelecionadas);
        mapViewInline = findViewById(R.id.mapViewInline); 

        // Atualizar UI se for modo de edição
        if (modoEdicao && registroEmEdicao != null) {
            if (txtTitulo != null) {
                txtTitulo.setText(R.string.titulo_editar_registro);
            }
            btnEnviar.setText(R.string.btn_atualizar);
        }

        // 1. Configurar o Spinner (Lista de Locais via resources)
        String[] locais = getResources().getStringArray(R.array.locais_saude);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locais);
        spinnerCentrosSaude.setAdapter(adapter);

        // Popular campos se for modo de edição
        if (modoEdicao && registroEmEdicao != null) {
            editDescricao.setText(registroEmEdicao.getDescricao());
            categoriaSelecionada = registroEmEdicao.getCategoria();
            
            // Selecionar local no spinner
            for (int i = 0; i < locais.length; i++) {
                if (locais[i].equals(registroEmEdicao.getLocal())) {
                    spinnerCentrosSaude.setSelection(i);
                    break;
                }
            }
            // Pre-popular coordenadas se houver
            if (registroEmEdicao.temLocalizacao()) {
                mapaLat = registroEmEdicao.getLatitude();
                mapaLon = registroEmEdicao.getLongitude();
                txtCoordsMapa.setText(String.format(java.util.Locale.getDefault(),
                        "📍 %.5f, %.5f", mapaLat, mapaLon));
                txtCoordsMapa.setVisibility(android.view.View.VISIBLE);
            }            
            // Carregar imagem em background thread para não bloquear UI
            if (registroEmEdicao.getCaminhoImagem() != null) {
                File imgFile = new File(registroEmEdicao.getCaminhoImagem());
                if (imgFile.exists()) {
                    java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        runOnUiThread(() -> {
                            if (bitmap != null) {
                                imgPreview.setImageBitmap(bitmap);
                                imgPreview.setPadding(0, 0, 0, 0);
                                imgPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            }
                        });
                    });
                }
            }
        }

        // 2. Inicializar mapa inline
        mapViewInline.setTileSource(TileSourceFactory.MAPNIK);
        mapViewInline.setMultiTouchControls(true);
        mapViewInline.getController().setZoom(13.0);
        GeoPoint centroPadrao = new GeoPoint(-15.77972, -47.92972);
        mapViewInline.getController().setCenter(centroPadrao);

        // Evitar conflito de scroll com ScrollView
        mapViewInline.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN
                    || event.getAction() == MotionEvent.ACTION_MOVE) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
            } else {
                v.getParent().requestDisallowInterceptTouchEvent(false);
            }
            return false;
        });

        // Tap no mapa para marcar ponto
        MapEventsReceiver receptor = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                mapaLat = p.getLatitude();
                mapaLon = p.getLongitude();
                adicionarMarcadorInline(p);
                txtCoordsMapa.setText(String.format(java.util.Locale.getDefault(),
                        "📍 %.5f, %.5f", mapaLat, mapaLon));
                txtCoordsMapa.setVisibility(View.VISIBLE);
                return true;
            }
            @Override
            public boolean longPressHelper(GeoPoint p) { return false; }
        };
        mapViewInline.getOverlays().add(new MapEventsOverlay(receptor));

        // Pre-popular mapa se modo edição com coords
        if (modoEdicao && registroEmEdicao != null && registroEmEdicao.temLocalizacao()) {
            GeoPoint ponto = new GeoPoint(mapaLat, mapaLon);
            mapViewInline.getController().setCenter(ponto);
            mapViewInline.getController().setZoom(15.0);
            adicionarMarcadorInline(ponto);
        }

        // Botão Minha Localização
        btnMarcarMapa.setOnClickListener(v -> usarMinhaLocalizacaoInline(txtCoordsMapa));

        // 3. Lógica da Galeria de Fotos
        ActivityResultLauncher<Intent> galeriaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        // Validar imagem antes de aceitar
                        String erroValidacao = GerenciadorDados.validarImagem(this, uri);
                        if (erroValidacao != null) {
                            Toast.makeText(this, erroValidacao, Toast.LENGTH_LONG).show();
                            return;
                        }
                        imagemSelecionadaUri = uri;
                        imgPreview.setImageURI(imagemSelecionadaUri);
                        imgPreview.setPadding(0, 0, 0, 0);
                        imgPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                }
        );

        btnAddFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galeriaLauncher.launch(intent);
        });

        // 3. Lógica das Categorias (Cores e Seleção)
        LinearLayout[] listaCategorias = {
                findViewById(R.id.catInfra), findViewById(R.id.catMedicamentos),
                findViewById(R.id.catAtendimento), findViewById(R.id.catAgendamento),
                findViewById(R.id.catVacinacao), findViewById(R.id.catTransporte),
                findViewById(R.id.catLimpeza), findViewById(R.id.catFila),
                findViewById(R.id.catAcessibilidade)
        };

        View.OnClickListener clickCategoria = v -> {
            for (LinearLayout cat : listaCategorias) {
                cat.setBackgroundColor(Color.TRANSPARENT);
            }
            v.setBackgroundColor(v.getContext().getColor(R.color.category_selected));
            int id = v.getId();
            if (id == R.id.catInfra) categoriaSelecionada = "Infraestrutura";
            else if (id == R.id.catMedicamentos) categoriaSelecionada = "Medicamentos";
            else if (id == R.id.catAtendimento) categoriaSelecionada = "Atendimento";
            else if (id == R.id.catAgendamento) categoriaSelecionada = "Agendamento";
            else if (id == R.id.catVacinacao) categoriaSelecionada = "Vacinação";
            else if (id == R.id.catTransporte) categoriaSelecionada = "Transporte";
            else if (id == R.id.catLimpeza) categoriaSelecionada = "Limpeza";
            else if (id == R.id.catFila) categoriaSelecionada = "Gestão de Fila";
            else if (id == R.id.catAcessibilidade) categoriaSelecionada = "Acessibilidade";
        };

        for (LinearLayout cat : listaCategorias) {
            cat.setOnClickListener(clickCategoria);
        }

        // Destacar categoria se for modo de edição
        if (modoEdicao && registroEmEdicao != null) {
            for (LinearLayout cat : listaCategorias) {
                int id = cat.getId();
                String catNome = "";
                if (id == R.id.catInfra) catNome = "Infraestrutura";
                else if (id == R.id.catMedicamentos) catNome = "Medicamentos";
                else if (id == R.id.catAtendimento) catNome = "Atendimento";
                else if (id == R.id.catAgendamento) catNome = "Agendamento";
                else if (id == R.id.catVacinacao) catNome = "Vacinação";
                else if (id == R.id.catTransporte) catNome = "Transporte";
                else if (id == R.id.catLimpeza) catNome = "Limpeza";
                else if (id == R.id.catFila) catNome = "Gestão de Fila";
                else if (id == R.id.catAcessibilidade) catNome = "Acessibilidade";
                
                if (catNome.equals(registroEmEdicao.getCategoria())) {
                    cat.setBackgroundColor(cat.getContext().getColor(R.color.category_selected));
                }
            }
        }

        // 4. BOTÃO ENVIAR (Persistência Real com Validação)
        btnEnviar.setOnClickListener(v -> {
            // VALIDAÇÃO COMPLETA
            if (categoriaSelecionada.isEmpty()) {
                Toast.makeText(this, getString(R.string.erro_categoria_vazia), Toast.LENGTH_SHORT).show();
                return;
            }

            String desc = editDescricao.getText().toString().trim();
            if (desc.isEmpty()) {
                Toast.makeText(this, getString(R.string.erro_descricao_vazia), Toast.LENGTH_SHORT).show();
                editDescricao.requestFocus();
                return;
            }

            if (desc.length() < 10) {
                Toast.makeText(this, getString(R.string.erro_descricao_curta), Toast.LENGTH_SHORT).show();
                editDescricao.requestFocus();
                return;
            }

            v.setEnabled(false);
            String local = spinnerCentrosSaude.getSelectedItem().toString();
            String imagemAnterior = (modoEdicao && registroEmEdicao != null)
                    ? registroEmEdicao.getCaminhoImagem() : null;

            // Progress indicator
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(imagemSelecionadaUri != null ? "Salvando imagem..." : "Salvando...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
            java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
                String caminhoImagem = imagemAnterior;
                if (imagemSelecionadaUri != null) {
                    caminhoImagem = GerenciadorDados.salvarImagem(NovoRegistroActivity.this, imagemSelecionadaUri);
                    if (caminhoImagem == null) {
                        // Imagem falhou — não prosseguir
                        handler.post(() -> {
                            progressDialog.dismiss();
                            v.setEnabled(true);
                            Toast.makeText(NovoRegistroActivity.this,
                                    getString(R.string.erro_salvar_imagem), Toast.LENGTH_LONG).show();
                        });
                        return;
                    }
                }
                final String caminhoFinal = caminhoImagem;
                handler.post(() -> {
                    progressDialog.dismiss();
                    if (modoEdicao && registroEmEdicao != null) {
                        registroEmEdicao.setCategoria(categoriaSelecionada);
                        registroEmEdicao.setLocal(local);
                        registroEmEdicao.setDescricao(desc);
                        registroEmEdicao.setCaminhoImagem(caminhoFinal);
                        if (mapaLat != 0.0 || mapaLon != 0.0) {
                            registroEmEdicao.setLatitude(mapaLat);
                            registroEmEdicao.setLongitude(mapaLon);
                        }
                        boolean sucesso = GerenciadorDados.atualizarRegistro(NovoRegistroActivity.this, registroEmEdicao);
                        Toast.makeText(NovoRegistroActivity.this,
                                sucesso ? getString(R.string.registro_atualizado) : getString(R.string.erro_atualizar_registro),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Registro novoRegistro = new Registro(categoriaSelecionada, local, desc, caminhoFinal);
                        novoRegistro.setLatitude(mapaLat);
                        novoRegistro.setLongitude(mapaLon);
                        List<Registro> listaAtual = GerenciadorDados.carregarRegistros(NovoRegistroActivity.this);
                        listaAtual.add(novoRegistro);
                        GerenciadorDados.salvarRegistros(NovoRegistroActivity.this, listaAtual);
                        Toast.makeText(NovoRegistroActivity.this,
                                getString(R.string.registro_salvo), Toast.LENGTH_SHORT).show();
                    }
                    finish();
                });
            });
        });

        btnVoltarNovo.setOnClickListener(v -> finish());

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_novo); // Marca "Novo" como selecionado
        
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_home) {
                // Volta para MainActivity
                Intent intent = new Intent(NovoRegistroActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Limpa a pilha e volta para MainActivity
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_novo) {
                // Já está em novo registro
                return true;
            } else if (itemId == R.id.nav_historico) {
                Intent intent = new Intent(NovoRegistroActivity.this, AcompanhamentoActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_notificacoes) {
                startActivity(new Intent(NovoRegistroActivity.this, AlertasActivity.class));
                return true;
            } else if (itemId == R.id.nav_perfil) {
                startActivity(new Intent(NovoRegistroActivity.this, PerfilActivity.class));
                return true;
            }
            
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarBadgeAlertas();
        if (mapViewInline != null) mapViewInline.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapViewInline != null) mapViewInline.onPause();
    }

    private void adicionarMarcadorInline(GeoPoint ponto) {
        if (marcadorInline == null) {
            marcadorInline = new Marker(mapViewInline);
            marcadorInline.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mapViewInline.getOverlays().add(marcadorInline);
        }
        marcadorInline.setPosition(ponto);
        mapViewInline.invalidate();
    }

    private void usarMinhaLocalizacaoInline(TextView txtCoordsMapa) {
        if (androidx.core.content.ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            androidx.core.app.ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION}, 201);
            return;
        }
        android.location.LocationManager lm =
                (android.location.LocationManager) getSystemService(LOCATION_SERVICE);
        android.location.Location loc = null;
        for (String provider : lm.getProviders(true)) {
            android.location.Location l = lm.getLastKnownLocation(provider);
            if (l != null) { loc = l; break; }
        }
        if (loc != null) {
            mapaLat = loc.getLatitude();
            mapaLon = loc.getLongitude();
            GeoPoint ponto = new GeoPoint(mapaLat, mapaLon);
            mapViewInline.getController().animateTo(ponto);
            mapViewInline.getController().setZoom(16.0);
            adicionarMarcadorInline(ponto);
            txtCoordsMapa.setText(String.format(java.util.Locale.getDefault(),
                    "📍 %.5f, %.5f", mapaLat, mapaLon));
            txtCoordsMapa.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "Localização não disponível. Toque no mapa para marcar.",
                    Toast.LENGTH_LONG).show();
        }
    }
}