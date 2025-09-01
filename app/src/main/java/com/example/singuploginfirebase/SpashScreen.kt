package com.example.singuploginfirebase

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SpashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_spash_screen)

        Handler().postDelayed({
            val intent = Intent(this@SpashScreen, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, tiempoCarga.toLong())
    }

    companion object {
        var tiempoCarga: Int = 2000
    }
}