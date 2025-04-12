package com.example.coleapp

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class CursoHorarioAdapter(
    private var listaCursos: MutableList<CursoHorario>,
    private val nivel: String,
    private val grado: String
) : RecyclerView.Adapter<CursoHorarioAdapter.CursoViewHolder>() {

    inner class CursoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtCurso: TextView = view.findViewById(R.id.txtCurso)
        val txtHorario: TextView = view.findViewById(R.id.txtHorario)
        val btnEditar: Button = view.findViewById(R.id.btnEditar)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CursoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_curso_horario, parent, false)
        return CursoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CursoViewHolder, position: Int) {
        val cursoHorario = listaCursos[position]

        holder.txtCurso.text = cursoHorario.curso
        holder.txtHorario.text = cursoHorario.horario

        // Editar
        holder.btnEditar.setOnClickListener {
            val context = holder.itemView.context
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_editar_curso, null)

            val edtCurso = dialogView.findViewById<EditText>(R.id.edtCurso)
            val edtHorario = dialogView.findViewById<EditText>(R.id.edtHorario)

            edtCurso.setText(cursoHorario.curso)
            edtHorario.setText(cursoHorario.horario)

            AlertDialog.Builder(context)
                .setTitle("Editar Curso")
                .setView(dialogView)
                .setPositiveButton("Guardar") { _, _ ->
                    val nuevoCurso = edtCurso.text.toString()
                    val nuevoHorario = edtHorario.text.toString()

                    if (nuevoCurso.isNotEmpty() && nuevoHorario.isNotEmpty()) {
                        cursoHorario.curso = nuevoCurso
                        cursoHorario.horario = nuevoHorario
                        notifyItemChanged(position)
                        guardarCambiosEnFirebase(context)
                    } else {
                        Toast.makeText(context, "No puede haber campos vacíos", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // Eliminar
        holder.btnEliminar.setOnClickListener {
            val context = holder.itemView.context
            AlertDialog.Builder(context)
                .setTitle("Eliminar")
                .setMessage("¿Deseas eliminar este curso?")
                .setPositiveButton("Sí") { _, _ ->
                    eliminarCurso(position)
                    Toast.makeText(context, "Curso eliminado", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun getItemCount(): Int = listaCursos.size

    // Agregar curso
    fun agregarCurso(curso: CursoHorario) {
        listaCursos.add(curso)
        notifyItemInserted(listaCursos.size - 1)
        // Actualiza en Firebase
        guardarCambiosEnFirebase(null)
    }

    // Eliminar curso
    private fun eliminarCurso(position: Int) {
        listaCursos.removeAt(position)
        notifyItemRemoved(position)
        guardarCambiosEnFirebase(null)
    }

    // Guardar en Firebase
    private fun guardarCambiosEnFirebase(context: android.content.Context?) {
        val db = FirebaseFirestore.getInstance()
        val coleccion = if (nivel == "Primaria") "CursosPrimaria" else "CursosSecundaria"

        val nuevosCursos = listaCursos.map { it.curso }
        val nuevosHorarios = listaCursos.map { it.horario }

        db.collection(coleccion).document(grado)
            .update("cursos", nuevosCursos, "horarios", nuevosHorarios)
            .addOnSuccessListener {
                context?.let {
                    Toast.makeText(it, "Cambios guardados", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                context?.let {
                    Toast.makeText(it, "Error al guardar", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
