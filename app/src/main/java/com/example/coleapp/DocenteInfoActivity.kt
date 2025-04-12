package com.example.coleapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DocenteInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docente_info) // Asegúrate de que este XML existe

        // Botón para regresar a la pantalla anterior
        val btnRegresar = findViewById<Button>(R.id.btnReg)
        btnRegresar.setOnClickListener {
            finish() // Cierra la actividad y vuelve a la anterior
        }
    }
}