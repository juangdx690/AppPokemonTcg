package com.example.myapplication


import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.apppokemontcg.databinding.RowManusBinding


class PokemonViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
    //  private val miBinding=UsuariosLayoutBinding.bind(vista)
    private val miBinding = RowManusBinding.bind(vista)
    fun inflar(
        anime: Pokemon
    ) {
        if (anime.nombre.length < 25) {

            miBinding.tvTitle.text = anime.nombre
        }else{
            miBinding.tvTitle.text = anime.nombre.take(25)+" ..."

        }


    }


}
