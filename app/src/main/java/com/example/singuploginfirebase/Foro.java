package com.example.singuploginfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class Foro extends AppCompatActivity implements ComentarioAdapter.OnDeleteClickListener {

    private Button btnComentario;
    private EditText comentarioEditText;
    private RecyclerView comentariosRecyclerView;
    private ComentarioAdapter adapter;
    private ArrayList<Comentario> comentarios;
    private TextView nombreTextView1;
    private ImageView fotoPerfil;
    private AppCompatButton backInicio;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DatabaseReference mDatabase;

    private ValueEventListener imagenPerfilListener;
    private String currentUserId;
    private String currentUserNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foro);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initializeComponents();
        initializeListeners();
        loadComentarios();
        loadUserData();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            setupImagenPerfilListener();
            loadProfileImage(currentUserId);
        }
    }

    private void initializeComponents() {
        btnComentario = findViewById(R.id.btnComentario);
        comentarioEditText = findViewById(R.id.Comentario);
        comentariosRecyclerView = findViewById(R.id.comentariosRecyclerView);
        comentariosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        backInicio = findViewById(R.id.backInicio);
        nombreTextView1 = findViewById(R.id.nombreTextView1);
        fotoPerfil = findViewById(R.id.FotoPerfil);

        comentarios = new ArrayList<>();
        adapter = new ComentarioAdapter(comentarios, this, getCurrentUserNombre());
        comentariosRecyclerView.setAdapter(adapter);
        comentariosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        comentariosRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        backInicio.setOnClickListener(v -> {
            Intent intent = new Intent(Foro.this, MenuActivity.class);
            startActivity(intent);
        });
    }

    private void initializeListeners() {
        btnComentario.setOnClickListener(v -> enviarComentario());
    }

    private void loadComentarios() {
        db.collection("comentarios")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Foro", "Error al cargar comentarios", error);
                        Toast.makeText(this, "Error al cargar comentarios: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        comentarios.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Comentario comentario = doc.toObject(Comentario.class);
                            comentario.setId(doc.getId());
                            comentarios.add(comentario);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void loadProfileImage(String userId) {
        DatabaseReference userRef = mDatabase.child("users").child(userId).child("profileImageUrl");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imageUrl = dataSnapshot.getValue(String.class);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(Foro.this)
                            .load(imageUrl)
                            .circleCrop()
                            .placeholder(R.drawable.perfil)
                            .error(R.drawable.no_accounts_24)
                            .into(fotoPerfil);
                } else {
                    fotoPerfil.setImageResource(R.drawable.perfil);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Foro.this, "Error al cargar la imagen de perfil: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarComentario() {
        String contenido = comentarioEditText.getText().toString().trim();
        if (!contenido.isEmpty()) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                getNombreUsuarioEImagen(currentUser, (nombreUsuario, imagenPerfilUrl) -> {
                    Comentario nuevoComentario = new Comentario(nombreUsuario, contenido, imagenPerfilUrl, System.currentTimeMillis());
                    db.collection("comentarios")
                            .add(nuevoComentario)
                            .addOnSuccessListener(documentReference -> {
                                comentarioEditText.setText("");
                                Toast.makeText(Foro.this, "Comentario enviado con éxito", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(Foro.this, "Error al enviar el comentario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                });
            } else {
                Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "El comentario no puede estar vacío", Toast.LENGTH_SHORT).show();
        }
    }

    private void getNombreUsuarioEImagen(FirebaseUser usuario, OnNombreUsuarioEImagenObtenidosListener listener) {
        String userId = usuario.getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nickname = null;
                String imagenPerfilUrl = null;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String key = childSnapshot.getKey();
                    if (key != null) {
                        if (key.contains("usuario") || key.contains("nickname") || key.contains("name")) {
                            nickname = childSnapshot.getValue(String.class);
                        } else if (key.equals("profileImageUrl")) {
                            imagenPerfilUrl = childSnapshot.getValue(String.class);
                        }
                    }
                }

                if (nickname == null || nickname.isEmpty()) {
                    nickname = usuario.getEmail();
                }
                listener.onNombreUsuarioEImagenObtenidos(nickname, imagenPerfilUrl);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onNombreUsuarioEImagenObtenidos(usuario.getEmail(), null);
            }
        });
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        mDatabase.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String key = childSnapshot.getKey();
                        if (key != null && (key.contains("usuario") || key.contains("nickname") || key.contains("name"))) {
                            currentUserNombre = childSnapshot.getValue(String.class);
                            break;
                        }
                    }
                    if (currentUserNombre != null && !currentUserNombre.isEmpty()) {
                        nombreTextView1.setText(currentUserNombre);
                        updateCommentsWithNewNickname(currentUser.getEmail(), currentUserNombre);
                        adapter = new ComentarioAdapter(comentarios, Foro.this, getCurrentUserNombre());
                        comentariosRecyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Foro.this, "Error al cargar los datos del usuario: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupImagenPerfilListener() {
        DatabaseReference userRef = mDatabase.child("users").child(currentUserId);
        imagenPerfilListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String newImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);
                if (newImageUrl != null) {
                    Glide.with(Foro.this)
                            .load(newImageUrl)
                            .circleCrop()
                            .placeholder(R.drawable.perfil)
                            .error(R.drawable.no_accounts_24)
                            .into(fotoPerfil);
                    updateComentariosConNuevaImagen(newImageUrl);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Foro.this, "Error al cargar la imagen de perfil: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        userRef.addValueEventListener(imagenPerfilListener);
    }

    private void updateComentariosConNuevaImagen(String newImageUrl) {
        for (Comentario comentario : comentarios) {
            if (comentario.getNombreUsuario().equals(nombreTextView1.getText().toString())) {
                comentario.setImagenPerfilUrl(newImageUrl);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (imagenPerfilListener != null && currentUserId != null) {
            mDatabase.child("users").child(currentUserId).removeEventListener(imagenPerfilListener);
        }
    }

    private void updateCommentsWithNewNickname(String oldNickname, String newNickname) {
        for (Comentario comentario : comentarios) {
            if (comentario.getNombreUsuario().equals(oldNickname)) {
                comentario.setNombreUsuario(newNickname);
            }
        }
        adapter.notifyDataSetChanged();

        db.collection("comentarios")
                .whereEqualTo("nombreUsuario", oldNickname)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().update("nombreUsuario", newNickname);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Foro.this, "Error al actualizar comentarios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDeleteClick(Comentario comentario) {
        if (comentario.getNombreUsuario().equals(getCurrentUserNombre())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Eliminar Comentario")
                    .setMessage("¿Estás seguro de que quieres eliminar este comentario?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        db.collection("comentarios").document(comentario.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    comentarios.remove(comentario);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(Foro.this, "Comentario eliminado", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(Foro.this, "Error al eliminar el comentario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        } else {
            Toast.makeText(this, "No tienes permiso para eliminar este comentario", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrentUserNombre() {
        return currentUserNombre != null ? currentUserNombre : "";
    }

    interface OnNombreUsuarioEImagenObtenidosListener {
        void onNombreUsuarioEImagenObtenidos(String nombreUsuario, String imagenPerfilUrl);
    }

    public String getNombre(TextView nombreTextView1) {
        return nombreTextView1.getText().toString();
    }

    public String getComentario() {
        return comentarioEditText.getText().toString();
    }

    public ImageView getFotoPerfil() {
        return fotoPerfil;
    }
}