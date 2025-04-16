package com.example.coleapp

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AlumnoActivity : AppCompatActivity() {

    private lateinit var spinnerNivel: Spinner
    private lateinit var spinnerGrado: Spinner
    private lateinit var btnActualizarCursos: Button
    private lateinit var contenedorCursos: LinearLayout
    private lateinit var tvNombre: TextView
    private lateinit var tvMatricula: TextView
    private lateinit var tvGrado: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var tvApellidoPaterno: TextView
    private lateinit var tvApellidoMaterno: TextView
    private lateinit var tvColegioProcedencia: TextView
    private lateinit var tvFechaNacimiento: TextView
    private lateinit var tvFechaRegistro: TextView
    private lateinit var imgFoto: ImageView
    private lateinit var btnCerrarSesion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumno)

        // Referencias de vistas
        spinnerNivel = findViewById(R.id.spinnerNivel)
        spinnerGrado = findViewById(R.id.spinnerGrado)
        btnActualizarCursos = findViewById(R.id.btnActualizarCursos)
        contenedorCursos = findViewById(R.id.contenedorCursos)
        tvNombre = findViewById(R.id.tvNombre)
        tvMatricula = findViewById(R.id.tvMatricula)
        tvGrado = findViewById(R.id.tvGrado)
        tvCorreo = findViewById(R.id.tvCorreo)
        tvApellidoPaterno = findViewById(R.id.tvApellidoPaterno)
        tvApellidoMaterno = findViewById(R.id.tvApellidoMaterno)
        tvColegioProcedencia = findViewById(R.id.tvColegioProcedencia)
        tvFechaNacimiento = findViewById(R.id.tvFechaNacimiento)
        tvFechaRegistro = findViewById(R.id.tvFechaRegistro)
        imgFoto = findViewById(R.id.imgFoto)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

        configurarSpinners()
        obtenerDatosMatricula()

        btnActualizarCursos.setOnClickListener { actualizarCursos() }
        btnCerrarSesion.setOnClickListener { cerrarSesion() }
    }

    private fun configurarSpinners() {
        val niveles = arrayOf("Primaria", "Secundaria")
        val gradosPrimaria = arrayOf("1ro", "2do", "3ro", "4to", "5to", "6to")
        val gradosSecundaria = arrayOf("1ro", "2do", "3ro", "4to", "5to")

        val adapterNivel = ArrayAdapter(this, android.R.layout.simple_spinner_item, niveles)
        adapterNivel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerNivel.adapter = adapterNivel

        spinnerNivel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val grados = if (niveles[position] == "Primaria") gradosPrimaria else gradosSecundaria
                val adapterGrado = ArrayAdapter(this@AlumnoActivity, android.R.layout.simple_spinner_item, grados)
                adapterGrado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerGrado.adapter = adapterGrado
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun obtenerDatosMatricula() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        if (userEmail == null) {
            Toast.makeText(this, "No hay un usuario logueado", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        val matriculaRef = db.collection("matriculas").document(userEmail)

        matriculaRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                tvNombre.text = "Nombre: ${document.getString("nombre") ?: "No disponible"}"
                tvGrado.text = "Grado: ${document.getString("grado") ?: "No disponible"}"
                tvCorreo.text = "Correo: ${document.getString("correo") ?: "No disponible"}"
                tvApellidoPaterno.text = "Apellido Paterno: ${document.getString("apellidoPaterno") ?: "No disponible"}"
                tvApellidoMaterno.text = "Apellido Materno: ${document.getString("apellidoMaterno") ?: "No disponible"}"
                tvColegioProcedencia.text = "Colegio de Procedencia: ${document.getString("colegioProcedencia") ?: "No disponible"}"
                tvFechaNacimiento.text = "Fecha de Nacimiento: ${document.getString("fechaNacimiento") ?: "No disponible"}"

                // Matrícula: Activa (solo "Activa" en verde)
                val textoMatricula = "Matrícula: Activa"
                val spannable = SpannableString(textoMatricula)
                val verde = resources.getColor(R.color.verdeMatricula, theme)
                spannable.setSpan(
                    ForegroundColorSpan(verde),
                    textoMatricula.indexOf("Activa"),
                    textoMatricula.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tvMatricula.text = spannable

                // Mostrar fecha de registro con la fecha actual
                val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                tvFechaRegistro.text = "Registrado el: $fechaActual"
                tvFechaRegistro.visibility = View.VISIBLE

                // Foto de perfil
                val fotoUrl = document.getString("fotoUrl")
                if (!fotoUrl.isNullOrEmpty()) {
                    Glide.with(this).load(fotoUrl).into(imgFoto)
                } else {
                    imgFoto.setImageResource(R.drawable.ic_user)
                }

            } else {
                Toast.makeText(this, "No se encontraron datos de matrícula", Toast.LENGTH_SHORT).show()
                tvMatricula.text = ""
                tvFechaRegistro.visibility = View.GONE
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al obtener datos de matrícula", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarCursos() {
        val nivelSeleccionado = spinnerNivel.selectedItem?.toString()
        val gradoSeleccionado = spinnerGrado.selectedItem?.toString()

        if (nivelSeleccionado.isNullOrEmpty() || gradoSeleccionado.isNullOrEmpty()) {
            Toast.makeText(this, "Seleccione nivel y grado", Toast.LENGTH_SHORT).show()
            return
        }

        val coleccion = if (nivelSeleccionado == "Primaria") "CursosPrimaria" else "CursosSecundaria"

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(coleccion).document(gradoSeleccionado)

        docRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val cursos = document.get("cursos") as? List<String> ?: emptyList()
                val horarios = document.get("horarios") as? List<String> ?: emptyList()
                mostrarCursosEnUI(cursos, horarios)
            } else {
                Toast.makeText(this, "No se encontraron cursos para ese grado", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al obtener cursos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarCursosEnUI(cursos: List<String>, horarios: List<String>) {
        contenedorCursos.removeAllViews()

        cursos.forEachIndexed { index, curso ->
            val horario = horarios.getOrNull(index) ?: "Horario no disponible"

            val textView = TextView(this).apply {
                text = "$curso - $horario"
                textSize = 16f
                setPadding(8, 8, 8, 8)
            }

            contenedorCursos.addView(textView)
        }
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, DrawerBaseActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
