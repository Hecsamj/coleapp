package com.example.coleapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VerAsistenciasAdapter(
    private val listaAsistencias: List<AlumnoAsistencia>
) : RecyclerView.Adapter<VerAsistenciasAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alumno_asistencia, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val asistencia = listaAsistencias[position]
        holder.txtNombre.text = asistencia.nombre
        holder.txtEstado.text = if (asistencia.asistio) "✅ Asistió" else "❌ No asistió"
    }

    override fun getItemCount(): Int = listaAsistencias.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombreAlumno)
        val txtEstado: TextView = itemView.findViewById(R.id.txtEstadoAsistencia)
    }
}
