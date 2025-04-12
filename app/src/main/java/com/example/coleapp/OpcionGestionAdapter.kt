package com.example.coleapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.appcole.OpcionGestion
import com.example.coleapp.R

class OpcionGestionAdapter(private val context: Context, private val opciones: List<OpcionGestion>) : BaseAdapter() {

    override fun getCount(): Int = opciones.size

    override fun getItem(position: Int): Any = opciones[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val opcion = opciones[position]
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_opcion_gestion, parent, false)

        val iconoImageView = view.findViewById<ImageView>(R.id.icono)
        val tituloTextView = view.findViewById<TextView>(R.id.titulo)

        iconoImageView.setImageResource(opcion.iconoResId)
        tituloTextView.text = opcion.titulo

        return view
    }
}
