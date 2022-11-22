package com.alimardon.homeork

import android.content.Context
import android.view.LayoutInflater
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alimardon.homeork.databinding.RecyclerItemLayoutBinding


class RecyclerViewAdapter :
    ListAdapter<Note, RecyclerViewAdapter.MyViewHolder>(diffUtil) {
    class MyViewHolder(val binding: RecyclerItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    private var Listener: SetOnLongClickListener? = null

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                return newItem == oldItem
            }

            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                return newItem.id == oldItem.id
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            RecyclerItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        val animation= AnimationUtils.loadAnimation(holder.itemView.context, androidx.appcompat.R.anim.abc_slide_in_bottom)
        holder.itemView.startAnimation(animation)
        holder.binding.apply {
            title.text = item.title
            description.text = item.description
        }
        holder.itemView.setOnClickListener {
            Listener?.setOnClickListener(item)
        }
        holder.itemView.setOnLongClickListener {
            Listener?.longClick(item)
            true // <- set to true
        }
    }

    interface SetOnLongClickListener {
        fun longClick(note: Note)
        fun setOnClickListener(note: Note)
    }

    fun setClickListener(setOnLongClickListener: SetOnLongClickListener) {
        Listener = setOnLongClickListener
    }
}