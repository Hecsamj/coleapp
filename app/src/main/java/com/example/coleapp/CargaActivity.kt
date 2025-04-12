package com.example.coleapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class CargaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carga)  // Asegúrate de tener un layout creado para esta actividad.

        // Espera 3 segundos y luego abre DrawerBaseActivity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, DrawerBaseActivity::class.java)
            startActivity(intent)
            finish() // Cierra la actividad de carga para que no se pueda volver atrás
        }, 3000) // 3000 milisegundos = 3 segundos
    }
}
