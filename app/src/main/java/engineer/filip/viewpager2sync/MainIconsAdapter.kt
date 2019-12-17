package engineer.filip.viewpager2sync

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class MainIconsAdapter(private val list: List<IconItem>) : RecyclerView.Adapter<IconsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.main_icon_item, parent, false)
        return IconsViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: IconsViewHolder, position: Int) {
        holder.setIcon(list[position])
    }

}


class IconsViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun setIcon(icon: IconItem) {
        view.findViewById<ImageView>(R.id.icon).apply {
            setImageResource(icon.whiteIcon)
            tag = icon.tag
        }
        view.findViewById<ImageView>(R.id.blue_icon).apply {
            setImageResource(icon.blueIcon)
            tag = icon.tag
        }
    }

}