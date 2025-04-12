package com.example.coleapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ActividadAdapter(private val actividades: List<Actividad>) :
    RecyclerView.Adapter<ActividadAdapter.ActividadViewHolder>() {

    class ActividadViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImagenActividad: ImageView = view.findViewById(R.id.ivImagenActividad)
        val tvTituloActividad: TextView = view.findViewById(R.id.tvTituloActividad)
        val tvDescripcionActividad: TextView = view.findViewById(R.id.tvDescripcionActividad)
        val tvFechaActividad: TextView = view.findViewById(R.id.tvFechaActividad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActividadViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_actividad, parent, false)
        return ActividadViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActividadViewHolder, position: Int) {
        val actividad = actividades[position]
        holder.tvTituloActividad.text = actividad.titulo
        holder.tvDescripcionActividad.text = actividad.descripcion
        holder.tvFechaActividad.text = actividad.fecha

        val imageUrl = actividad.imagenUrl ?: "" // Si es null, usa cadena vacía

        if (imageUrl.isNotBlank()) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder) // Imagen temporal mientras carga
                .error(R.drawable.placeholder) // Imagen en caso de error de carga
                .into(holder.ivImagenActividad)
        } else {
            holder.ivImagenActividad.setImageResource(R.drawable.matematicas) // Imagen en drawable
        }
    }

    override fun getItemCount(): Int = actividades.size
}
