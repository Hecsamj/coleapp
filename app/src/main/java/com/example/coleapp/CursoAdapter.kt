package com.example.coleapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CursoAdapter(private val cursos: List<Curso>) : RecyclerView.Adapter<CursoAdapter.CursoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CursoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_curso, parent, false)
        return CursoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CursoViewHolder, position: Int) {
        val curso = cursos[position]
        holder.bind(curso)
    }

    override fun getItemCount(): Int {
        return cursos.size
    }

    inner class CursoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cursoName: TextView = itemView.findViewById(R.id.tvCurso)
        private val horario: TextView = itemView.findViewById(R.id.tvHorario)

        fun bind(curso: Curso) {
            // Asegúrate de que la propiedad sea correcta. Aquí usamos curso.curso para el nombre del curso
            cursoName.text = curso.curso

            // Crear una cadena con los horarios
            val horariosText = curso.horarios.joinToString("\n") { horario ->
                "${horario.horaInicio} - ${horario.horaFin}\nDías: ${horario.dias.joinToString(", ")}"
            }
            horario.text = horariosText
        }
    }
}
