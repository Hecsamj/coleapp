package com.example.coleapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MatriculaActivity : AppCompatActivity() {

    private lateinit var etCorreo: EditText
    private lateinit var etNombre: EditText
    private lateinit var etApellidoPaterno: EditText
    private lateinit var etApellidoMaterno: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var etGrado: EditText
    private lateinit var etColegioProcedencia: EditText
    private lateinit var etFotoUrl: EditText
    private lateinit var ivFotoEstudiante: ImageView
    private lateinit var btnRegistrar: Button
    private lateinit var btnVolver: Button
    private lateinit var spNivel: Spinner
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matricula)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        etCorreo = findViewById(R.id.etCorreo)
        etNombre = findViewById(R.id.etNombre)
        etApellidoPaterno = findViewById(R.id.etApellidoPaterno)
        etApellidoMaterno = findViewById(R.id.etApellidoMaterno)
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento)
        etGrado = findViewById(R.id.etGrado)
        etColegioProcedencia = findViewById(R.id.etColegioProcedencia)
        etFotoUrl = findViewById(R.id.etFotoUrl)
        ivFotoEstudiante = findViewById(R.id.ivFotoEstudiante)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        btnVolver = findViewById(R.id.btnVolver)
        spNivel = findViewById(R.id.spNivel)

        val nivelAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOf("Primaria", "Secundaria")
        )
        nivelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spNivel.adapter = nivelAdapter

        btnRegistrar.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val nombre = etNombre.text.toString().trim()
            val apellidoPaterno = etApellidoPaterno.text.toString().trim()
            val apellidoMaterno = etApellidoMaterno.text.toString().trim()
            val fechaNacimiento = etFechaNacimiento.text.toString().trim()
            val grado = etGrado.text.toString().trim()
            val colegioProcedencia = etColegioProcedencia.text.toString().trim()
            val fotoUrl = etFotoUrl.text.toString().trim()
            val nivel = spNivel.selectedItem.toString().trim()

            Log.d("MatriculaActivity", "Grado ingresado:$grado")
            Log.d("MatriculaActivity", "Nivel seleccionado:$nivel")

            if (grado.isValidForNivel(nivel)) {
                if (correo.isNotEmpty() && nombre.isNotEmpty()) {
                    registrarMatricula(
                        correo, nombre, apellidoPaterno, apellidoMaterno,
                        fechaNacimiento, grado, nivel, colegioProcedencia, fotoUrl
                    )
                } else {
                    Toast.makeText(this, "Por favor ingresa el correo y nombre", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Grado no válido para el nivel seleccionado", Toast.LENGTH_SHORT).show()
            }
        }

        // 🔁 Botón para regresar sin registrar
        btnVolver.setOnClickListener {
            val intent = Intent(this, DrawerBaseActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun registrarMatricula(
        correo: String, nombre: String, apellidoPaterno: String,
        apellidoMaterno: String, fechaNacimiento: String, grado: String,
        nivel: String, colegioProcedencia: String, fotoUrl: String
    ) {
        auth.createUserWithEmailAndPassword(correo, "ContraseñaTemporal123")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val matriculaData = hashMapOf(
                        "correo" to correo,
                        "nombre" to nombre,
                        "apellidoPaterno" to apellidoPaterno,
                        "apellidoMaterno" to apellidoMaterno,
                        "fechaNacimiento" to fechaNacimiento,
                        "grado" to grado,
                        "nivel" to nivel,
                        "colegioProcedencia" to colegioProcedencia,
                        "fotoUrl" to fotoUrl
                    )
                    db.collection("matriculas").document(correo).set(matriculaData)
                        .addOnSuccessListener {
                            enviarCorreoRestablecerPassword(correo)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al registrar la matrícula", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Error al registrar en Firebase Auth: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun enviarCorreoRestablecerPassword(correo: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(correo)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Te hemos enviado un correo para restablecer tu contraseña", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, DrawerBaseActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Error al enviar el correo: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun String.isValidForNivel(nivel: String): Boolean {
        val gradoTrimmed = this.trim()
        Log.d("Validation", "Validando grado: '$gradoTrimmed' con nivel: '$nivel'")

        return when (nivel) {
            "Primaria" -> listOf("1ro", "2do", "3ro", "4to", "5to", "6to").contains(gradoTrimmed)
            "Secundaria" -> listOf("1ro", "2do", "3ro", "4to", "5to").contains(gradoTrimmed)
            else -> false
        }
    }
}
