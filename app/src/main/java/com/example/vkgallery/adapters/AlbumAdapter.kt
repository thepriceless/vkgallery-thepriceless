package com.example.vkgallery.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vkgallery.R
import com.example.vkgallery.activities.SelectingPhotosActivity
import com.example.vkgallery.models.Album

class AlbumAdapter(private val context: Context, private val albumList: MutableList<Album>) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.album_zone_layout, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val photoUrl = albumList[position].url
        Glide.with(holder.albumImage)
            .load(photoUrl)
            .into(holder.albumImage)
        holder.albumName.text = albumList[position].title
    }

    override fun getItemCount(): Int {
        return albumList.size
    }

    inner class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            super.itemView

            itemView.setOnClickListener {v ->
                val menu = PopupMenu(context, v)
                menu.menu.add("Add photos to this album")
                menu.setOnMenuItemClickListener { item ->
                    if (item.title == "Add photos to this album") {
                        val intent = Intent(context, SelectingPhotosActivity::class.java)
                        intent.putExtra("album_id", albumList[adapterPosition].id)
                        context.startActivity(intent)
                        // Toast.makeText(context, "Folder deleted", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                menu.show()
                true
            }

        }

        val albumImage: ImageView = itemView.findViewById(R.id.album_image)
        var albumName: TextView = itemView.findViewById(R.id.album_name)
    }
}