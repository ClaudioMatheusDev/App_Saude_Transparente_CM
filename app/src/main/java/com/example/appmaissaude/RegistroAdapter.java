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
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RegistroAdapter extends RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder> {

    private List<Registro> listaRegistros;

    public RegistroAdapter(List<Registro> listaRegistros) {
        this.listaRegistros = listaRegistros;
    }

    // Método para atualizar dados sem recriar o adapter
    public void atualizarDados(List<Registro> novosRegistros) {
        this.listaRegistros = novosRegistros;
        notifyDataSetChanged();
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
            StatusRegistro proximoStatus = status.proximo();
            registro.setStatus(proximoStatus);
            
            // Salvar alteração
            GerenciadorDados.salvarRegistros(v.getContext(), listaRegistros);
            
            // Atualizar visual
            holder.txtStatus.setText(proximoStatus.getTexto());
            holder.txtStatus.setBackgroundColor(Color.parseColor(proximoStatus.getCor()));
            
            // Feedback
            Toast.makeText(v.getContext(), "Status alterado para: " + proximoStatus.getTexto(), Toast.LENGTH_SHORT).show();
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
                    .setTitle("Confirmar Exclusão")
                    .setMessage("Deseja realmente apagar este registro? Esta ação não pode ser desfeita.")
                    .setPositiveButton("Sim, Excluir", (dialog, which) -> {
                        // Lógica de exclusão que já tínhamos (Só acontece se clicar em Sim)
                        int currentPos = holder.getAdapterPosition(); // Forma mais segura de pegar a posição
                        if (currentPos != RecyclerView.NO_POSITION) {
                            listaRegistros.remove(currentPos);
                            GerenciadorDados.salvarRegistros(v.getContext(), listaRegistros);
                            notifyItemRemoved(currentPos);
                            notifyItemRangeChanged(currentPos, listaRegistros.size());
                            Toast.makeText(v.getContext(), "Registro removido!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancelar", null) // Se clicar em cancelar, a janela apenas fecha
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return listaRegistros.size();
    }

    class RegistroViewHolder extends RecyclerView.ViewHolder {
        TextView txtCategoria, txtLocal, txtDescricao, txtDataHora, txtStatus;
        ImageView btnExcluir, btnEditar;

        public RegistroViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCategoria = itemView.findViewById(R.id.txtItemCategoria);
            txtStatus = itemView.findViewById(R.id.txtItemStatus);
            txtLocal = itemView.findViewById(R.id.txtItemLocal);
            txtDescricao = itemView.findViewById(R.id.txtItemDescricao);
            txtDataHora = itemView.findViewById(R.id.txtItemDataHora);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
            btnEditar = itemView.findViewById(R.id.btnEditar);
        }
    }
}