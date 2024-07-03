package org.projectPA.petdiary.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ListPetBinding
import org.projectPA.petdiary.model.Pet

class PetMyProfileAdapter(val onClick: (Pet, View) -> Unit) :
    ListAdapter<Pet, PetMyProfileAdapter.ViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<Pet>() {
        override fun areContentsTheSame(oldItem: Pet, newItem: Pet): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: Pet, newItem: Pet): Boolean {
            return oldItem.id == newItem.id
        }
    }

    inner class ViewHolder(private val binding: ListPetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pet: Pet) = with(binding) {
            val context = itemView.context
            petNameTV.text = pet.name
            petTypeTV.text = pet.type
            petGenderTV.text = pet.gender
            petAgeTV.text = context.getString(R.string.age_pet, pet.age)

            Glide.with(petImageIV.context).load(pet.imageUrl).placeholder(R.drawable.image_blank)
                .into(petImageIV)

            petDetailBtn.setOnClickListener {
                onClick(pet, it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListPetBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}