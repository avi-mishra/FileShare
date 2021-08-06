package android.example.fileshare

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.filelayout.view.*

class FileAdapter(var file:ArrayList<File>,var listener: ClickListener): RecyclerView.Adapter<FileAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ibView=itemView.findViewById<ImageButton>(R.id.ibDownload)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView=MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.filelayout,parent,false))
        itemView.ibView.setOnClickListener {
            listener.onItemClick(file[itemView.absoluteAdapterPosition])
        }
        return itemView
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.tvUserName.text=file[position].user
        holder.itemView.tvUploadTime.text=DateUtils.getRelativeTimeSpanString(file[position].currentTimeMs)
        holder.itemView.tvFileName.text=file[position].fileName
    }

    override fun getItemCount(): Int {
        return file.size
    }
}
interface ClickListener{
    fun onItemClick(file:File)
}