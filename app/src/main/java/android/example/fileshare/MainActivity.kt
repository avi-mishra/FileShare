package android.example.fileshare

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),ClickListener {

    var dbRef= FirebaseFirestore.getInstance()
    var files=ArrayList<File>()
    lateinit var adapter:FileAdapter
    val firebaseUser=FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        adapter= FileAdapter(files, this)
        var fileRef=dbRef.collection("files")
            .orderBy("currentTimeMs", Query.Direction.DESCENDING)

        fileRef.addSnapshotListener { snapshot, exception ->
            if(exception!=null || snapshot==null) {
                Toast.makeText(this@MainActivity, "Error while retrieving post data", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            var fileList= snapshot.toObjects(File::class.java)
            files.clear()
            files.addAll(fileList)
            adapter.notifyDataSetChanged()
        }
        rvFiles.layoutManager= LinearLayoutManager(this)
        rvFiles.adapter=adapter

        fbUpload.setOnClickListener {
            if(firebaseUser!=null) {
                val i=Intent(this, UploadActivity::class.java)
                startActivity(i)
            }
            else {
                Toast.makeText(this, "Please Log In to upload Files", Toast.LENGTH_SHORT).show()
                val i=Intent(this, LogIn::class.java)
                startActivity(i)
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (firebaseUser!=null) {
            menuInflater.inflate(R.menu.nav_menu, menu)
            return true
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.navSignOut -> {
                Toast.makeText(this@MainActivity, "Signing Out", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
                val i = Intent(this@MainActivity, LogIn::class.java)
                startActivity(i)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(file: File) {
        if(firebaseUser!=null){
            Toast.makeText(this@MainActivity, "Downloading!!", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this@MainActivity, "Please Log In to continue!!", Toast.LENGTH_SHORT).show()
            val i=Intent(this@MainActivity,LogIn::class.java)
            startActivity(i)
            finish()
        }
    }
}