package com.example.coleapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.coleapp.EditarMatriculaActivity
import com.example.coleapp.R
import com.example.coleapp.models.Matricula
import com.google.firebase.firestore.FirebaseFirestore

class MatriculaAdapter(private val matriculaList: MutableList<Matricula>) :
    RecyclerView.Adapter<MatriculaAdapter.MatriculaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatriculaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_matricula, parent, false)
        return MatriculaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatriculaViewHolder, position: Int) {
        val matricula = matriculaList[position]
        holder.bind(matricula)
    }

    override fun getItemCount(): Int {
        return matriculaList.size
    }

    inner class MatriculaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.tvNombre)
        private val gradoTextView: TextView = itemView.findViewById(R.id.tvGrado)
        private val fechaNacimientoTextView: TextView = itemView.findViewById(R.id.tvFechaNacimiento)
        private val btnEditar: Button = itemView.findViewById(R.id.btnEditar)
        private val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)

        fun bind(matricula: Matricula) {
            // Mostrar los datos de la matrícula en los TextViews
            nombreTextView.text = matricula.nombresCompletos
            gradoTextView.text = matricula.grado
            fechaNacimientoTextView.text = matricula.fechaNacimiento

            // Configurar el botón de editar
            btnEditar.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, EditarMatriculaActivity::class.java)
                intent.putExtra("matriculaId", matricula.id)
                context.startActivity(intent)
            }

            // Configurar el botón de eliminar
            btnEliminar.setOnClickListener {
                val context = itemView.context
                val db = FirebaseFirestore.getInstance()
                db.collection("matriculas").document(matricula.id)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Matrícula eliminada", Toast.LENGTH_SHORT).show()
                        matriculaList.removeAt(adapterPosition)
                        notifyItemRemoved(adapterPosition)
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Error al eliminar matrícula", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
