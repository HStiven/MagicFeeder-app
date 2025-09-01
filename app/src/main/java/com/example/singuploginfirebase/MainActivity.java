package com.example.singuploginfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_main);

  Button btnInicioSeccion = findViewById(R.id.button1);
  btnInicioSeccion.setOnClickListener(new View.OnClickListener() {
   @Override
   public void onClick(View v) {
    navegacionInicioSeccion();
   }
  });

  Button btnRegistro = findViewById(R.id.button2);
  btnRegistro.setOnClickListener(new View.OnClickListener() {
   @Override
   public void onClick(View v) {
    navegacionRegistro();
   }
  });
 }

 private void navegacionInicioSeccion() {
  Intent intent = new Intent(this, LoginActivity.class);
  startActivity(intent);
 }

 private void navegacionRegistro() {
  Intent intent = new Intent(this,SingUpActivity.class);
  startActivity(intent);
 }
}

