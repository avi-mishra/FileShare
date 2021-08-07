package android.example.fileshare

import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.privatefilelayout.view.*

class PrivateFileAdapter(var files:ArrayList<PrivateFile>,var listener: PrivateClickListener): RecyclerView.Adapter<PrivateFileAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val ibView=itemView.findViewById<ImageButton>(R.id.privateibDownload)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView=MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.privatefilelayout,parent,false))
        itemView.ibView.setOnClickListener {
            listener.onItemClick(files[itemView.absoluteAdapterPosition])
        }
        return itemView
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.tvPrivateUserName.text=files[position].user
        holder.itemView.tvPrivateUploadTime.text= DateUtils.getRelativeTimeSpanString(files[position].currentTimeMs)
        holder.itemView.tvPrivateFileName.text=files[position].fileName
    }

    override fun getItemCount(): Int {
       return files.size
    }
}
interface PrivateClickListener{
    fun onItemClick(file:PrivateFile)
}