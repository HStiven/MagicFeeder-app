package com.example.singuploginfirebase;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.example.singuploginfirebase.databinding.ActivityPerfilBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Perfil extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST_USUARIO = 1;
    private static final int PICK_IMAGE_REQUEST_FONDO = 2;

    ActivityPerfilBinding binding;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    AppCompatButton backInicio;

    ImageView fech;
    Spinner spinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fech = findViewById(R.id.fech);
        spinner = findViewById(R.id.spinner);

        backInicio = findViewById(R.id.backInicio);

        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        llenarSpinner();

        binding.usuariolindo.setOnClickListener(v -> usuariolindo());
        binding.pano.setOnClickListener(v -> fondocambio());
        binding.Guardar.setOnClickListener(v -> guardarDatos());
        binding.Cancelar.setOnClickListener(v -> limpiarCampos());

        loadSavedImages();
        loadUserData();

        fech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Perfil.this, MenuActivity.class);
                startActivity(intent);
            }
        });
        backInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Perfil.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(Perfil.this, "Seción cerrada correctamente", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void usuariolindo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST_USUARIO);
    }

    private void fondocambio() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST_FONDO);
    }


    private void llenarSpinner() {
        ArrayList<String> reciclajeOptions = new ArrayList<>();
        reciclajeOptions.add("None");
        reciclajeOptions.add("Macho");
        reciclajeOptions.add("Hembra");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, reciclajeOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMaterial = (String) parent.getItemAtPosition(position);
                //Guardar esta selección en una variable si es necesario.
                if (!selectedMaterial.equals("None")) {
                    Toast.makeText(Perfil.this, "Seleccionado: " + selectedMaterial, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada si no se selecciona nada
            }
        });
    }


    private void guardarDatos() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference userRef = mDatabase.child("users").child(userId);

        userRef.child("nombre").setValue(getNombre());
        userRef.child("apellido").setValue(getApellido());
        userRef.child("usuario").setValue(getUsuario());
        userRef.child("numero").setValue(getNumero());
        userRef.child("edad").setValue(getEdad());
        userRef.child("nombreMascota").setValue(getNombreMascota());
        userRef.child("razaMascota").setValue(getRazaMascota());
        userRef.child("tipoMascota").setValue(getTipoMascota());

        String selectedMaterial = spinner.getSelectedItem().toString();  // Obtener la selección del Spinner
        userRef.child("recyclableMaterial").setValue(selectedMaterial);  // Guardar en Firebase

        showConfirmationDialog();
    }

    private void limpiarCampos() {
        binding.Nombre.setText("");
        binding.Apellido.setText("");
        binding.Usuario.setText("");
        binding.NMero.setText("");
        binding.Edad.setText("");
        binding.NombreMascota.setText("");
        binding.RazaMascota.setText("");
        binding.TipoMascota.setText("");
        spinner.setSelection(0);
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Datos Actualizados");
        builder.setMessage("Se actualizaron sus datos");
        builder.setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadSavedImages() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);
                    String backgroundImageUrl = dataSnapshot.child("backgroundImageUrl").getValue(String.class);

                    if (profileImageUrl != null) {
                        Glide.with(Perfil.this).load(profileImageUrl).circleCrop().into(binding.usuariolindo);
                    }
                    if (backgroundImageUrl != null) {
                        Glide.with(Perfil.this).load(backgroundImageUrl).centerCrop().into(binding.pano);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Perfil.this, "Error al cargar las imágenes: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    binding.Nombre.setText(dataSnapshot.child("nombre").getValue(String.class));
                    binding.Apellido.setText(dataSnapshot.child("apellido").getValue(String.class));
                    binding.Usuario.setText(dataSnapshot.child("usuario").getValue(String.class));
                    binding.NMero.setText(dataSnapshot.child("numero").getValue(String.class));
                    binding.NombreMascota.setText(dataSnapshot.child("nombreMascota").getValue(String.class));
                    binding.Edad.setText(dataSnapshot.child("edad").getValue(String.class));
                    binding.RazaMascota.setText(dataSnapshot.child("razaMascota").getValue(String.class));
                    binding.TipoMascota.setText(dataSnapshot.child("tipoMascota").getValue(String.class));

                    // Cargar la selección del Spinner
                    String selectedMaterial = dataSnapshot.child("recyclableMaterial").getValue(String.class);
                    if (selectedMaterial != null) {
                        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
                        int position = adapter.getPosition(selectedMaterial);
                        spinner.setSelection(position);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Perfil.this, "Error al cargar los datos del usuario: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadImage(imageUri, requestCode);
        }
    }

    private void uploadImage(Uri imageUri, int requestCode) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String imageName = (requestCode == PICK_IMAGE_REQUEST_USUARIO) ? "profile_image.jpg" : "background_image.jpg";
        StorageReference imageRef = storage.getReference().child("users").child(userId).child(imageName);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        updateUserImage(userId, imageUrl, requestCode);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Perfil.this, "Error al subir la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUserImage(String userId, String imageUrl, int requestCode) {
        String imageType = (requestCode == PICK_IMAGE_REQUEST_USUARIO) ? "profileImageUrl" : "backgroundImageUrl";

        mDatabase.child("users").child(userId).child(imageType).setValue(imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Imagen actualizada en la base de datos", Toast.LENGTH_SHORT).show();
                    if (requestCode == PICK_IMAGE_REQUEST_USUARIO) {
                        Glide.with(this).load(imageUrl).into(binding.usuariolindo);
                    } else {
                        Glide.with(this).load(imageUrl).into(binding.pano);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al actualizar la imagen en la base de datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public String getNombre() {
        return binding.Nombre.getText().toString();
    }

    public String getApellido() {
        return binding.Apellido.getText().toString();
    }

    public String getEdad() {
        return binding.Edad.getText().toString();
    }

    public String getUsuario() {return binding.Usuario.getText().toString();
    }

    public String getNumero() {
        return binding.NMero.getText().toString();
    }

    public String getTipoMascota() {
        return binding.TipoMascota.getText().toString();
    }

    public String getNombreMascota() {
        return binding.NombreMascota.getText().toString();
    }

    public String getRazaMascota() {
        return binding.RazaMascota.getText().toString();
    }
}