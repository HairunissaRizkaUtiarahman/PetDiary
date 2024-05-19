package org.projectPA.petdiary.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ListMyPetBinding
import org.projectPA.petdiary.model.Pet

class MyPetAdapter(val onClick: (Pet, View) -> Unit) :
    ListAdapter<Pet, MyPetAdapter.ViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<Pet>() {
        override fun areItemsTheSame(oldItem: Pet, newItem: Pet): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Pet, newItem: Pet): Boolean {
            return oldItem.id == newItem.id
        }
    }

    inner class ViewHolder(private val binding: ListMyPetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pet: Pet) = with(binding) {
            petNameTV.text = pet.name
            petTypeTV.text = pet.type

            Glide.with(petImageIV.context).load(pet.imageUrl).placeholder(R.drawable.image_blank)
                .into(petImageIV)

            petDetailBtn.setOnClickListener {
                onClick(pet, it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListMyPetBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}