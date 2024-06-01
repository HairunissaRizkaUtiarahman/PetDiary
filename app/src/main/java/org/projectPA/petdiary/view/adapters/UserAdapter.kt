package org.projectPA.petdiary.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.projectPA.petdiary.R
import org.projectPA.petdiary.databinding.ListUserBinding
import org.projectPA.petdiary.model.User

class UserAdapter(val onClick: (User, View) -> Unit) :
    ListAdapter<User, UserAdapter.ViewHolder>(Companion) {
    private lateinit var context: Context

    companion object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }
    }

    inner class ViewHolder(private val binding: ListUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) = with(binding) {
            nameTV.text = user.name
            bioTV.text = user.bio

            Glide.with(profileImageIV.context).load(user.imageUrl)
                .placeholder(R.drawable.image_profile).into(profileImageIV)

            profileCV.setOnClickListener {
                onClick(user, it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding = ListUserBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}