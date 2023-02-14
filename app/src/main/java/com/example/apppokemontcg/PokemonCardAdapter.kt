import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apppokemontcg.PokemonCard
import com.example.apppokemontcg.R


class PokemonCardAdapter(private var cards: List<PokemonCard>) : RecyclerView.Adapter<PokemonCardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(card: PokemonCard) {
            itemView.setOnClickListener{

                println("item pulsado")

            }
            itemView.findViewById<TextView>(R.id.tvRareza).text = card.rarity
            itemView.findViewById<TextView>(R.id.tvHp).text = card.hp
            itemView.findViewById<TextView>(R.id.tvTitle).text = card.name
            Glide.with(itemView.context)
                .load(card.imageUrl)
                .into(itemView.findViewById<ImageView>(R.id.ivMain))
        }


    }




    fun setList(lista2:MutableList<PokemonCard>){
        this.cards = lista2
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_manus, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cards[position])


    }

    override fun getItemCount() = cards.size
}
