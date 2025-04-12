package com.example.coleapp

import Docente
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class DocenteAdapter(private val docentesList: List<Docente>) :
    RecyclerView.Adapter<DocenteAdapter.DocenteViewHolder>() {

    class DocenteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.tvNombre)
        val especialidadTextView: TextView = itemView.findViewById(R.id.tvEspecialidad)
        val correoTextView: TextView = itemView.findViewById(R.id.tvCorreo)
        val fotoImageView: ImageView = itemView.findViewById(R.id.ivFoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocenteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_docente, parent, false)
        return DocenteViewHolder(view)
    }

    override fun onBindViewHolder(holder: DocenteViewHolder, position: Int) {
        val docente = docentesList[position]
        holder.nombreTextView.text = docente.nombre
        holder.especialidadTextView.text = docente.especialidad
        holder.correoTextView.text = docente.correo

        Glide.with(holder.itemView.context)
            .load(docente.fotoUrl)
            .placeholder(R.drawable.ic_person) // Imagen por defecto mientras carga
            .into(holder.fotoImageView)
    }

    override fun getItemCount(): Int = docentesList.size
}
