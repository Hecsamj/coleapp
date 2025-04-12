package com.example.coleapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class NoticiaAdapter(private val noticiasList: List<Noticia>) :
    RecyclerView.Adapter<NoticiaAdapter.NoticiaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticiaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_noticia, parent, false)
        return NoticiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticiaViewHolder, position: Int) {
        val noticia = noticiasList[position]
        holder.bind(noticia)
    }

    override fun getItemCount(): Int = noticiasList.size

    class NoticiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgNoticia: ImageView = itemView.findViewById(R.id.imgNoticia)
        private val tvTitulo: TextView = itemView.findViewById(R.id.tvTituloNoticia)
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcionNoticia)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFechaNoticia)

        fun bind(noticia: Noticia) {
            tvTitulo.text = noticia.titulo
            tvDescripcion.text = noticia.descripcion
            tvFecha.text = noticia.fecha

            // Cargar imagen con Glide desde la URL almacenada en Firestore
            Glide.with(itemView.context)
                .load(noticia.imagenUrl)
                .placeholder(R.drawable.placeholder_image) // Imagen por defecto
                .into(imgNoticia)
        }
    }
}
