package com.example.singuploginfirebase

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Contactanos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contactanos)
        val atrasInicio = findViewById<AppCompatButton>(R.id.backInicio)
        atrasInicio.setOnClickListener {
            navegaciónDispensador()
        }
    }

    private fun navegaciónDispensador() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }
}