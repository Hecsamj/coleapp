package com.example.coleapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CalificacionAdapter : RecyclerView.Adapter<CalificacionAdapter.CalificacionViewHolder>() {

    private var calificaciones = listOf<Calificacion>()

    fun actualizarDatos(nuevas: List<Calificacion>) {
        calificaciones = nuevas.sortedWith(compareBy({ it.curso }, { it.alumnoNombre }))
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalificacionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calificacion, parent, false)
        return CalificacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalificacionViewHolder, position: Int) {
        val cal = calificaciones[position]
        holder.bind(cal)
    }

    override fun getItemCount() = calificaciones.size

    class CalificacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCurso: TextView = itemView.findViewById(R.id.tvCurso)
        private val tvAlumno: TextView = itemView.findViewById(R.id.tvAlumno)
        private val tvNota: TextView = itemView.findViewById(R.id.tvNota)

        fun bind(calificacion: Calificacion) {
            tvCurso.text = "Curso: ${calificacion.curso}"
            tvAlumno.text = "Alumno: ${calificacion.alumnoNombre}"
            tvNota.text = "Nota: ${calificacion.nota}"
        }
    }
}
