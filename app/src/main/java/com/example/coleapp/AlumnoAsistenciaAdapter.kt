package com.example.coleapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlumnoAsistenciaAdapter(
    private val listaAlumnos: List<Map<String, Any>>
) : RecyclerView.Adapter<AlumnoAsistenciaAdapter.ViewHolder>() {

    private val listaAsistencias = mutableListOf<AlumnoAsistencia>()

    init {
        for (alumno in listaAlumnos) {
            val nombre = (alumno["nombre"] ?: "") as String
            val apellidoPaterno = (alumno["apellidoPaterno"] ?: "") as String
            val apellidoMaterno = (alumno["apellidoMaterno"] ?: "") as String
            val correo = (alumno["correo"] ?: "") as String
            val nivel = (alumno["nivel"] ?: "") as String
            val grado = (alumno["grado"] ?: "") as String
            val asistio = alumno["asistio"] as? Boolean ?: false

            listaAsistencias.add(
                AlumnoAsistencia(
                    correo = correo,
                    nombre = "$nombre $apellidoPaterno $apellidoMaterno",
                    nivel = nivel,
                    grado = grado,
                    asistio = asistio
                )
            )
        }
    }

    fun obtenerAsistencias(): List<AlumnoAsistencia> {
        return listaAsistencias
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alumno_asistencia, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alumno = listaAsistencias[position]
        holder.txtNombre.text = alumno.nombre

        // Estado inicial
        holder.checkAsistio.isChecked = alumno.asistio
        holder.checkNoAsistio.isChecked = !alumno.asistio

        // Listeners cruzados
        holder.checkAsistio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                holder.checkNoAsistio.isChecked = false
                listaAsistencias[position].asistio = true
            }
        }

        holder.checkNoAsistio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                holder.checkAsistio.isChecked = false
                listaAsistencias[position].asistio = false
            }
        }
    }

    override fun getItemCount(): Int = listaAsistencias.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombreAlumno)
        val checkAsistio: CheckBox = itemView.findViewById(R.id.checkAsistio)
        val checkNoAsistio: CheckBox = itemView.findViewById(R.id.checkNoAsistio)
    }
}
