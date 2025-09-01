package com.example.singuploginfirebase


import FirstApp.PrimeraAppActivity
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class MenuActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        // Botón Perfil
        val perfil = findViewById<Button>(R.id.perfil)
        perfil.setOnClickListener { navegacionPerfil() }

        // Botón Foro
        val foro = findViewById<Button>(R.id.btnForo)
        foro.setOnClickListener { navegacionForo() }

        // Botón Dispensador
        val btnDispensador = findViewById<Button>(R.id.btnDispensador)
        btnDispensador.setOnClickListener { navegacionDispensador() }

        // Botón Contacto
        val contacto = findViewById<Button>(R.id.btnContacto)
        contacto.setOnClickListener { navegacionContacto() }

        // Botón Recomendaciones
        val Recomendaciones = findViewById<Button>(R.id.btnTips)
        Recomendaciones.setOnClickListener { navegacionRecomendaciones() }
    }

    private fun navegacionRecomendaciones() {
        val intent = Intent(this, PrimeraAppActivity::class.java)
        startActivity(intent)
    }

    private fun navegacionForo() {
        val intent = Intent(this, Foro::class.java)
        startActivity(intent)
    }

    private fun navegacionDispensador() {
        val intent = Intent(this,DispensadorMenu::class.java)
        startActivity(intent)
    }

    private fun navegacionPerfil() {
        val intent = Intent(this, Perfil::class.java)
        startActivity(intent)
    }

    private fun navegacionContacto() {
        val intent = Intent(this, Contactanos::class.java)
        startActivity(intent)
    }
}