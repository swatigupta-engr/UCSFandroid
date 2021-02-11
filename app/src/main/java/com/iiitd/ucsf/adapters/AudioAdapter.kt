package com.iiitd.ucsf.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iiitd.ucsf.R
import com.iiitd.ucsf.models.Audio
import kotlinx.android.synthetic.main.single_row_audios.view.*

class AudioAdapter (var context : Context, var audioList : List<Audio>, val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<AudioAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.single_row_audios, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return audioList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.setData(audioList[position], itemClickListener)
    }


    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun setData (audio : Audio, clickListener: OnItemClickListener) {



            itemView.tvAnimalId.text = audio.name
          /*  if(audio.duration_played.equals("null")){
                audio.duration_played="0"
            }*/
            itemView.tvDesc.text=audio.description +"\n\n Last played for :"+audio.duration_played+" seconds "+"\n\n No.of times played: "+audio.count_played +" times"
            itemView.ivAnimalId.setImageResource(audio.image)
            itemView.setOnClickListener {
                clickListener.onItemClick(audio)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(audio : Audio)
    }

}