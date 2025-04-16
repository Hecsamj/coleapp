package com.example.coleapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

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

        inicializarFirebase()
        inicializarVistas()
        configurarSpinnerNivel()
        configurarBotones()

        etFechaNacimiento.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anioActual = calendario.get(Calendar.YEAR)
            val mesActual = calendario.get(Calendar.MONTH)
            val diaActual = calendario.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, anio, mes, dia ->
                val mesFormateado = String.format("%02d", mes + 1)
                val diaFormateado = String.format("%02d", dia)
                etFechaNacimiento.setText("$anio-$mesFormateado-$diaFormateado")
            }, anioActual, mesActual, diaActual)

            datePickerDialog.datePicker.maxDate = calendario.timeInMillis
            datePickerDialog.show()
        }
    }

    private fun inicializarFirebase() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    private fun inicializarVistas() {
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
    }

    private fun configurarSpinnerNivel() {
        val niveles = listOf("Primaria", "Secundaria")
        val nivelAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, niveles)
        nivelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spNivel.adapter = nivelAdapter
    }

    private fun configurarBotones() {
        btnRegistrar.setOnClickListener { validarYRegistrarMatricula() }

        btnVolver.setOnClickListener {
            val intent = Intent(this, DrawerBaseActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun validarYRegistrarMatricula() {
        val correo = etCorreo.text.toString().trim()
        val nombre = etNombre.text.toString().trim()
        val apellidoPaterno = etApellidoPaterno.text.toString().trim()
        val apellidoMaterno = etApellidoMaterno.text.toString().trim()
        val fechaNacimiento = etFechaNacimiento.text.toString().trim()
        val grado = etGrado.text.toString().trim()
        val colegioProcedencia = etColegioProcedencia.text.toString().trim()
        val fotoUrl = etFotoUrl.text.toString().trim()
        val nivel = spNivel.selectedItem.toString().trim()

        if (correo.isEmpty() || nombre.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa el correo y nombre", Toast.LENGTH_SHORT).show()
            return
        }

        if (!grado.isValidForNivel(nivel)) {
            Toast.makeText(this, "Grado no válido para el nivel seleccionado", Toast.LENGTH_SHORT).show()
            return
        }

        registrarMatricula(
            correo, nombre, apellidoPaterno, apellidoMaterno,
            fechaNacimiento, grado, nivel, colegioProcedencia, fotoUrl
        )
    }

    private fun registrarMatricula(
        correo: String, nombre: String, apellidoPaterno: String,
        apellidoMaterno: String, fechaNacimiento: String, grado: String,
        nivel: String, colegioProcedencia: String, fotoUrl: String
    ) {
        auth.createUserWithEmailAndPassword(correo, "ContraseñaTemporal123")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val datos = hashMapOf(
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

                    db.collection("matriculas").document(correo).set(datos)
                        .addOnSuccessListener {
                            enviarCorreoRestablecerPassword(correo)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al registrar en Firestore", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Error en Firebase Auth: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun enviarCorreoRestablecerPassword(correo: String) {
        auth.sendPasswordResetEmail(correo)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Correo enviado para restablecer la contraseña", Toast.LENGTH_LONG).show()
                    volverAlMenuPrincipal()
                } else {
                    Toast.makeText(this, "Error al enviar el correo: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun volverAlMenuPrincipal() {
        val intent = Intent(this, DrawerBaseActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // Extensión para validar grado según nivel
    private fun String.isValidForNivel(nivel: String): Boolean {
        val gradoTrimmed = this.trim()
        val gradosPrimaria = listOf("1ro", "2do", "3ro", "4to", "5to", "6to")
        val gradosSecundaria = listOf("1ro", "2do", "3ro", "4to", "5to")

        return when (nivel) {
            "Primaria" -> gradoTrimmed in gradosPrimaria
            "Secundaria" -> gradoTrimmed in gradosSecundaria
            else -> false
        }
    }
}
