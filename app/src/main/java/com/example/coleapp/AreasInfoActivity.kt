package com.example.coleapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AreasInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_areas_info)

        // Botón para regresar a la pantalla anterior
        val btnRegresar = findViewById<Button>(R.id.btnR)
        btnRegresar.setOnClickListener {
            finish() // Cierra la actividad y vuelve a la anterior
        }
    }
}
