package com.example.appmaissaude;

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
        TextView txtCategoria, txtLocal, txtDescricao;
        ImageView btnExcluir;

        public RegistroViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCategoria = itemView.findViewById(R.id.txtItemCategoria);
            txtLocal = itemView.findViewById(R.id.txtItemLocal);
            txtDescricao = itemView.findViewById(R.id.txtItemDescricao);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }
    }
}