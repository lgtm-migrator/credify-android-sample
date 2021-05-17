package one.credify.sdk.sample

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import one.credify.sdk.core.model.Offer

class OfferAdapter(private val mOnItemClick: (offer: Offer) -> Unit) :
    RecyclerView.Adapter<OfferAdapter.ViewHolder>() {
    private val mData = ArrayList<Offer>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.view_offer_item_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position]) {
            mOnItemClick(mData[position])
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun updateList(list: List<Offer>) {
        synchronized(mData) {
            mData.clear()
            mData.addAll(list)
            notifyDataSetChanged()
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val ivOfferImage: ImageView = view.findViewById(R.id.ivOfferImage)
        private val tvOfferDescription: TextView = view.findViewById(R.id.tvOfferDescription)

        fun bind(offer: Offer, onItemClick: () -> Unit) {
            load(iv = ivOfferImage, offer.thumbnailUrl, 36, true)

            tvOfferDescription.text = offer.campaign.description ?: ""

            view.setOnClickListener {
                onItemClick()
            }
        }

        private fun load(
            iv: ImageView,
            url: String,
            roundingRadius: Int,
            reset: Boolean = false
        ) {
            if (reset) {
                iv.setImageResource(android.R.color.transparent)
            }

            Glide
                .with(iv.context)
                .load(url)
                .transform(MultiTransformation(CenterCrop(), RoundedCorners(roundingRadius)))
                .placeholder(ColorDrawable(Color.TRANSPARENT))
                .into(iv)
        }
    }
}