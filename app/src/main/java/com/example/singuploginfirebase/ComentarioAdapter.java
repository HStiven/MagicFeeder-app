package com.example.singuploginfirebase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder> {

    private ArrayList<Comentario> listaComentarios;
    private final OnDeleteClickListener deleteClickListener;
    private final String currentUserNombre;

    public interface OnDeleteClickListener {
        void onDeleteClick(Comentario comentario);
    }

    public ComentarioAdapter(ArrayList<Comentario> listaComentarios, OnDeleteClickListener deleteListener, String currentUserNombre) {
        this.listaComentarios = listaComentarios;
        this.deleteClickListener = deleteListener;
        this.currentUserNombre = currentUserNombre;
    }

    @NonNull
    @Override
    public ComentarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comentario, parent, false);
        return new ComentarioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentarioViewHolder holder, int position) {
        Comentario comentario = listaComentarios.get(position);
        holder.nombreUsuarioTextView.setText(comentario.getNombreUsuario());
        holder.contenidoTextView.setText(comentario.getContenido());

        Glide.with(holder.itemView.getContext())
                .load(comentario.getImagenPerfilUrl())
                .circleCrop()
                .placeholder(R.drawable.perfil)
                .error(R.drawable.no_accounts_24)
                .into(holder.imagenPerfilImageView);

        boolean isCurrentUserComment = comentario.getNombreUsuario().equals(currentUserNombre);
        holder.btnBorrarComentario.setVisibility(isCurrentUserComment ? View.VISIBLE : View.GONE);

        holder.btnBorrarComentario.setOnClickListener(v -> {
            if (isCurrentUserComment && deleteClickListener != null) {
                deleteClickListener.onDeleteClick(comentario);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaComentarios.size();
    }

    public static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreUsuarioTextView;
        public TextView contenidoTextView;
        public ImageView imagenPerfilImageView;
        public Button btnBorrarComentario;

        public ComentarioViewHolder(View itemView) {
            super(itemView);
            nombreUsuarioTextView = itemView.findViewById(R.id.nombreUsuarioTextView);
            contenidoTextView = itemView.findViewById(R.id.contenidoTextView);
            imagenPerfilImageView = itemView.findViewById(R.id.imagenPerfilImageView);
            btnBorrarComentario = itemView.findViewById(R.id.btnBorrarComentario);
        }
    }
}