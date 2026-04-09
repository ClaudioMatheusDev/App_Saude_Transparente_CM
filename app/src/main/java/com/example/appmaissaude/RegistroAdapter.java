package com.example.appmaissaude;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RegistroAdapter extends RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder> {

    private List<Registro> listaRegistros;

    public RegistroAdapter(List<Registro> listaRegistros) {
        this.listaRegistros = listaRegistros;
    }

    // Método para atualizar dados com DiffUtil (animações eficientes)
    public void atualizarDados(List<Registro> novosRegistros) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(
                new RegistroDiffCallback(this.listaRegistros, novosRegistros));
        this.listaRegistros = novosRegistros;
        result.dispatchUpdatesTo(this);
    }

    private static class RegistroDiffCallback extends DiffUtil.Callback {
        private final List<Registro> oldList;
        private final List<Registro> newList;

        RegistroDiffCallback(List<Registro> oldList, List<Registro> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() { return oldList.size(); }

        @Override
        public int getNewListSize() { return newList.size(); }

        @Override
        public boolean areItemsTheSame(int oldPos, int newPos) {
            return oldList.get(oldPos).getId().equals(newList.get(newPos).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldPos, int newPos) {
            Registro o = oldList.get(oldPos);
            Registro n = newList.get(newPos);
            return o.getCategoria().equals(n.getCategoria())
                    && o.getLocal().equals(n.getLocal())
                    && o.getDescricao().equals(n.getDescricao())
                    && o.getStatus() == n.getStatus();
        }
    }

    @NonNull
    @Override
    public RegistroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_registro, parent, false);
        return new RegistroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistroViewHolder holder, int position) {
        Registro registro = listaRegistros.get(position);
        holder.txtCategoria.setText(registro.getCategoria().toUpperCase());
        holder.txtLocal.setText("Local: " + registro.getLocal());
        holder.txtDescricao.setText(registro.getDescricao());
        
        // Mostrar data/hora
        if (registro.getDataHora() != null) {
            holder.txtDataHora.setText("📅 " + registro.getDataHora());
            holder.txtDataHora.setVisibility(View.VISIBLE);
        } else {
            holder.txtDataHora.setVisibility(View.GONE);
        }

        // Configurar STATUS com cor e texto
        StatusRegistro status = registro.getStatus();
        holder.txtStatus.setText(status.getTexto());
        holder.txtStatus.setBackgroundColor(Color.parseColor(status.getCor()));

        // CLICAR NO STATUS para alternar
        holder.txtStatus.setOnClickListener(v -> {
            StatusRegistro proximoStatus = registro.getStatus().proximo();
            registro.setStatus(proximoStatus);
            
            // Salvar alteração
            GerenciadorDados.salvarRegistros(v.getContext(), listaRegistros);
            
            // Atualizar visual
            holder.txtStatus.setText(proximoStatus.getTexto());
            holder.txtStatus.setBackgroundColor(Color.parseColor(proximoStatus.getCor()));
            
            // Feedback
            Toast.makeText(v.getContext(), v.getContext().getString(R.string.status_alterado_para, proximoStatus.getTexto()), Toast.LENGTH_SHORT).show();
        });

        // AÇÃO DO BOTÃO COMPARTILHAR
        holder.btnCompartilhar.setOnClickListener(v -> {
            String texto = "📋 " + registro.getCategoria()
                    + "\n📍 " + registro.getLocal()
                    + "\n📅 " + registro.getDataHora()
                    + "\n\n" + registro.getDescricao()
                    + "\n\nStatus: " + registro.getStatus().getTexto();
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, texto);
            v.getContext().startActivity(Intent.createChooser(share, "Compartilhar Registro"));
        });

        // AÇÃO DO BOTÃO EDITAR
        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), NovoRegistroActivity.class);
            intent.putExtra("registro_para_editar", registro);
            v.getContext().startActivity(intent);
        });

        // AÇÃO DO BOTÃO EXCLUIR COM CONFIRMAÇÃO
        holder.btnExcluir.setOnClickListener(v -> {

            // Criar o Alerta
            new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                    .setTitle(R.string.dialogo_titulo_excluir)
                    .setMessage(R.string.dialogo_msg_excluir)
                    .setPositiveButton(R.string.dialogo_sim_excluir, (dialog, which) -> {
                        // Lógica de exclusão que já tínhamos (Só acontece se clicar em Sim)
                        int currentPos = holder.getAdapterPosition(); // Forma mais segura de pegar a posição
                        if (currentPos != RecyclerView.NO_POSITION) {
                            listaRegistros.remove(currentPos);
                            GerenciadorDados.salvarRegistros(v.getContext(), listaRegistros);
                            notifyItemRemoved(currentPos);
                            notifyItemRangeChanged(currentPos, listaRegistros.size());
                            Toast.makeText(v.getContext(), R.string.registro_removido, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(R.string.dialogo_cancelar, null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return listaRegistros.size();
    }

    class RegistroViewHolder extends RecyclerView.ViewHolder {
        TextView txtCategoria, txtLocal, txtDescricao, txtDataHora, txtStatus;
        ImageView btnExcluir, btnEditar, btnCompartilhar;

        public RegistroViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCategoria = itemView.findViewById(R.id.txtItemCategoria);
            txtStatus = itemView.findViewById(R.id.txtItemStatus);
            txtLocal = itemView.findViewById(R.id.txtItemLocal);
            txtDescricao = itemView.findViewById(R.id.txtItemDescricao);
            txtDataHora = itemView.findViewById(R.id.txtItemDataHora);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnCompartilhar = itemView.findViewById(R.id.btnCompartilhar);
        }
    }
}