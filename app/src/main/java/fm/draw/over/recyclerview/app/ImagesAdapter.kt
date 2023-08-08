package fm.draw.over.recyclerview.app

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fm.draw.over.recyclerview.app.databinding.ItemImageBinding

class ImagesAdapter(context: Context) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemImageBinding.inflate(layoutInflater, parent, false))
    }

    override fun getItemCount(): Int = ITEM_COUNT

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    class ViewHolder(
        private val binding: ItemImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val imageRes = when (position % 3) {
                0 -> R.drawable.cover
                1 -> R.drawable.cover2
                else -> R.drawable.cover3
            }
            binding.imageView.setImageResource(imageRes)
        }
    }

    companion object {
        private const val ITEM_COUNT = 24
    }
}