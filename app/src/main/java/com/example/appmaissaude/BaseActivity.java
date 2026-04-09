package com.example.appmaissaude;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Classe base que fornece funcionalidades comuns a todas as Activities:
 * - Badge de alertas no ícone de notificações
 */
public abstract class BaseActivity extends AppCompatActivity {

    /** Atualiza o badge (bolinha vermelha) no ícone do sino com o nº de pendentes. */
    protected void atualizarBadgeAlertas() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;
        int pendentes = GerenciadorDados.contarPorStatus(this, StatusRegistro.PENDENTE);
        BadgeDrawable badge = bottomNav.getOrCreateBadge(R.id.nav_notificacoes);
        if (pendentes > 0) {
            badge.setVisible(true);
            badge.setNumber(pendentes);
        } else {
            badge.setVisible(false);
        }
    }
}
