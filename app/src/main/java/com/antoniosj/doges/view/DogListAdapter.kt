package com.antoniosj.doges.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.antoniosj.doges.R
import com.antoniosj.doges.model.DogBreed
import com.antoniosj.doges.util.getProgressDrawable
import com.antoniosj.doges.util.loadImage
import kotlinx.android.synthetic.main.item_dog.view.*

class DogListAdapter(val dogList: ArrayList<DogBreed>): RecyclerView.Adapter<DogListAdapter.DogViewHolder>() {

    fun updateDogList(newDogList: List<DogBreed>) {
        dogList.clear()
        dogList.addAll(newDogList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_dog, parent, false)
        return DogViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dogList.size
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.view.tv_name.text = dogList[position].dogBreed
        holder.view.tv_lifespan.text = dogList[position].lifeSpan
        // using extension to load with glide*
        holder.view.iv_dog.loadImage(dogList[position].imageUrl, getProgressDrawable(holder.view.iv_dog.context))
        holder.view.setOnClickListener {
            Navigation.findNavController(it).navigate(ListFragmentDirections.actionToDetailFragment())
        }
    }

    class DogViewHolder(var view: View): RecyclerView.ViewHolder(view)
}