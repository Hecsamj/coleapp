package com.example.coleapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class VerAsistenciasAdapter(
    private val listaAsistencias: List<AlumnoAsistencia>
) : RecyclerView.Adapter<VerAsistenciasAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ver_asistencia, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val asistencia = listaAsistencias[position]
        holder.txtNombre.text = asistencia.nombre
        holder.txtAsistencia.text = if (asistencia.asistio) "Asistió: Sí" else "Asistió: No"

        asistencia.fecha?.let {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            holder.txtFecha.text = "Fecha: ${sdf.format(it.toDate())}"
        } ?: run {
            holder.txtFecha.text = "Fecha: No disponible"
        }
    }

    override fun getItemCount(): Int = listaAsistencias.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombre)
        val txtAsistencia: TextView = itemView.findViewById(R.id.txtAsistencia)
        val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
    }
}
