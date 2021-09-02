package org.abubaker.happyplaces.adapters

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.abubaker.happyplaces.databinding.ItemHappyPlaceBinding
import org.abubaker.happyplaces.models.HappyPlaceModel

open class HappyPlacesAdapter(
    private val context: Context,
    private var list: ArrayList<HappyPlaceModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // STEP 03 - Add a variable for onClickListener interface.
    private var onClickListener: OnClickListener? = null

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        //
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemHappyPlaceBinding.inflate(layoutInflater, parent, false)

        return MyViewHolder(binding)
    }

    // STEP 04
    /**
     * A function to bind the onclickListener.
     */
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        //
        val model = list[position]

        //
        if (holder is MyViewHolder) {
            holder.binding.ivPlaceImage.setImageURI(Uri.parse(model.image))
            holder.binding.tvTitle.text = model.title
            holder.binding.tvDescription.text = model.description

            // Finally add an onclickListener to the item.
            holder.itemView.setOnClickListener {

                if (onClickListener != null) {

                    // We are passing the POSITION of the clicked CARD / ROW
                    // + We are passing our model = HappyPlaceModel.kt
                    onClickListener!!.onClick(position, model)

                }
            }
        }

    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {

        // Returns Total # of Rows
        return list.size

    }

    // STEP 02 - Create an interface
    interface OnClickListener {
        fun onClick(position: Int, model: HappyPlaceModel)
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    private class MyViewHolder(val binding: ItemHappyPlaceBinding) :
        RecyclerView.ViewHolder(binding.root)
}