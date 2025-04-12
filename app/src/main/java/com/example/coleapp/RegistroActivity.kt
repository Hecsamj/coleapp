package com.example.coleapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.coleapp.databinding.ActivityRegistroBinding
import com.google.firebase.auth.FirebaseAuth

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private lateinit var auth: FirebaseAuth
    private val adminEmail = "pi44140208@idat.pe"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnRegistrar.setOnClickListener {
            val email = binding.etCorreoReg.text.toString().trim()
            val password = binding.etPasswordReg.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (email != adminEmail) {
                Toast.makeText(this, "Acceso restringido solo para el administrador", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    getSharedPreferences("UsuarioPrefs", MODE_PRIVATE).edit().putBoolean("esAdmin", true).apply()
                    startActivity(Intent(this, DrawerBaseActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al iniciar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
                    getSharedPreferences("UsuarioPrefs", MODE_PRIVATE).edit().putBoolean("esAdmin", false).apply()
                }
        }

        binding.btnRecuperarClave.setOnClickListener {
            val email = binding.etCorreoReg.text.toString().trim()
            if (email != adminEmail) {
                Toast.makeText(this, "Solo el correo del administrador puede recuperar la contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(this, "Correo de recuperación enviado", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
