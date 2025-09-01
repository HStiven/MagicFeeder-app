package com.example.singuploginfirebase;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Paginadeinicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Log.d("Paginadeinicio", "La actividad Paginadeinicio se ha iniciado correctamente");

    }

    private void mostrarDialogoCerrarSesion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cerrar sesión");
        builder.setMessage("¿Desea cerrar sesión?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cerrarSesion();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // No hacer nada, simplemente cerrar el diálogo
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void cerrarSesion() {
        // Aquí puedes agregar cualquier código necesario para cerrar la sesión del usuario
        // Por ejemplo, limpiar la información de autenticación, etc.

        // Luego, inicia la actividad MainActivity
        Intent intent = new Intent(Paginadeinicio.this, MainActivity.class);
        startActivity(intent);
        finish(); // Esto asegura que la actividad actual (Paginadeinicio) se cierre después de iniciar MainActivity
    }
}
