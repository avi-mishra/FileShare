package android.example.fileshare

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_upload.*


class UploadActivity : AppCompatActivity() {

    var dbRef = FirebaseFirestore.getInstance()
    var storageRef = FirebaseStorage.getInstance().reference
    var fileUrl: Uri? = null
    val firebaseUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        val signedInUser = firebaseUser?.displayName

        val result = registerForActivityResult(ActivityResultContracts.GetContent(),
                ActivityResultCallback { uri ->
                    if (uri != null) {
                        tvSelectedFileUrl.text = uri.toString()
                        fileUrl = uri
                        Log.d("TAG90", "onCreate: ${getExtension(fileUrl!!)}")
                    }
                    Log.d("fileUrl", "$fileUrl")
                })

        btnChooseFile.setOnClickListener {
            result.launch("application/*")
        }
        btnUpload.setOnClickListener {
            val fileName=etFileName.text.toString()
            if (fileUrl != null && fileName.length>0) {
                val fileRef = storageRef.child("files/${System.currentTimeMillis()}${getExtension(fileUrl!!)}")
                fileRef.putFile(fileUrl!!).continueWithTask { photoUploadTask ->
                    fileRef.downloadUrl
                }.continueWithTask { downloadUrlTask ->
                    val file = getExtension(fileUrl!!)?.let { it1 ->
                        File(
                                System.currentTimeMillis(),
                                it1,
                                fileName,
                                downloadUrlTask.result.toString(),
                                signedInUser!!
                        )
                    }
                    dbRef.collection("files").add(file!!)
                }.addOnCompleteListener { postCreationTask ->
                    if (!postCreationTask.isSuccessful) {
                        Toast.makeText(
                                this@UploadActivity,
                                "${postCreationTask.exception}",
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                    val i = Intent(this@UploadActivity, MainActivity::class.java)
                    startActivity(i)
                    finish()
                }
            } else {
                Toast.makeText(this@UploadActivity, "Fields cannot be empty", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    fun getExtension(uri: Uri): String? {
        var result: String? = null
        if (uri.getScheme().equals("content")) {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.getPath()
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                if (result != null) {
                    if (cut != null) {
                        result = result.substring(cut + 1)
                    }
                }
            }
        }
        Log.d("TAG80", "getExtension: $result")
        return result?.substring(result.lastIndexOf("."))
    }
}