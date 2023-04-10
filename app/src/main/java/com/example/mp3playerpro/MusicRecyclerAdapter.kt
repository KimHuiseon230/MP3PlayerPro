package com.example.mp3playerpro

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mp3playerpro.databinding.ItemRecyclerBinding
import java.text.SimpleDateFormat

class MusicRecyclerAdapter(val context: Context, val musicList: MutableList<MusicData>) :
    RecyclerView.Adapter<MusicRecyclerAdapter.CustomViewHolder>() {
    val ALBUM_IMAGE_SIZE = 90

    // +++ 2.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding =
            ItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun getItemCount(): Int = musicList.size

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val binding = holder.binding
        // 이미지, artiest, tiltle, duration binding
        // bitmap 먼저 갖고옴 .
        val bitmap = musicList.get(position).getAlbumBitmap(context, ALBUM_IMAGE_SIZE)
        if(bitmap != null){
            binding.imageAlbum.setImageBitmap(bitmap)
        }else{
            binding.imageAlbum.setImageResource(R.drawable.music_24)
        }
        binding.textArtist.text = musicList.get(position).artists
        binding.textTitle.text = musicList.get(position).title
        binding.textDuration.text =
            SimpleDateFormat("mm:ss").format(musicList.get(position).duration)
        //아이템 항목 클릭시 플레이 액티비티로  음악 정보 전달
        binding.root.setOnClickListener {
            val intent = Intent(binding.root.context, PlayActivity::class.java)
            // 현재의 position만 을 넘김.
            intent.putExtra("musicData", musicList.get(position))
            context.startActivity(intent)
        }
        
    }



    // +++ 1. 내부 클래스 선언
    inner class CustomViewHolder(val binding: ItemRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root)
}