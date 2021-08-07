package android.example.fileshare

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_private_file.*

class PrivateFileActivity : AppCompatActivity(),PrivateClickListener {

    var dbRef= FirebaseFirestore.getInstance()
    var files=ArrayList<PrivateFile>()
    lateinit var adapter:PrivateFileAdapter
    val firebaseUser= FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private_file)

        adapter= PrivateFileAdapter(files,this)
        var fileRef=dbRef.collection("privateFiles")
            .orderBy("currentTimeMs", Query.Direction.DESCENDING)

        fileRef.addSnapshotListener { snapshot, exception ->
            if(exception!=null || snapshot==null) {
                Toast.makeText(this@PrivateFileActivity, "Error while retrieving private files", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            var fileList= snapshot.toObjects(PrivateFile::class.java)
            val size=fileList.size
            for(i in 0 until size){
                if(fileList[i].user==firebaseUser?.displayName) {
                    files.add(fileList[i])
                    adapter.notifyDataSetChanged()
                }
            }
        }
        rvPrivateFiles.layoutManager=LinearLayoutManager(this)
        rvPrivateFiles.adapter=adapter
    }

    override fun onItemClick(file: PrivateFile) {
        if(firebaseUser!=null){
            Toast.makeText(this@PrivateFileActivity, "Downloading!!", Toast.LENGTH_SHORT).show()
            val url = "${file.fileUrl}"
            val request = DownloadManager.Request(Uri.parse(url))
            request.setDescription("${file.fileName}")
            request.setTitle("New File Downloaded")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner()
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            }
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${file.fileName}${file.extension}")

            val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        }
        else {
            Toast.makeText(this@PrivateFileActivity, "Please Log In to continue!!", Toast.LENGTH_SHORT).show()
            val i= Intent(this@PrivateFileActivity,LogIn::class.java)
            startActivity(i)
            finish()
        }
    }
}