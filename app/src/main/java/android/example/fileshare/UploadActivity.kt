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
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_upload.*


class UploadActivity : AppCompatActivity() {

    var dbRef = FirebaseFirestore.getInstance()
    var storageRef = FirebaseStorage.getInstance().reference
    var fileUrl: Uri? = null
    var imageUrl:Uri?=null
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val signedInUser = firebaseUser?.displayName

    val resultFile = registerForActivityResult(ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri ->
                if (uri != null) {
                    tvSelectedFileUrl.text = uri.toString()
                    fileUrl = uri
                    Log.d("TAG90", "onCreate: ${getExtension(fileUrl!!)}")
                }
                Log.d("fileUrl", "$fileUrl")
            })

    val resultImage=registerForActivityResult(ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri->
                ivNewImage.setImageURI(uri)
                imageUrl=uri
                Log.d("imageUrl","$imageUrl")
            })
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        btnUploadFile.setOnClickListener {
            llFile.isVisible=true
            uploadFilePublic()
            uploadFilePrivate()
        }
        btnUploadImages.setOnClickListener {
            llImage.isVisible=true
            uploadImagePublic()
            uploadImagePrivate()
        }
    }

    fun uploadFilePublic(){

        btnChooseFile.setOnClickListener {
            resultFile.launch("application/*")
        }
        btnUploadFileToFirebasePublic.setOnClickListener {
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
    fun uploadFilePrivate(){

        btnChooseFile.setOnClickListener {
            resultFile.launch("application/*")
        }
        btnUploadFileToFirebasePrivate.setOnClickListener {
            val fileName=etFileName.text.toString()
            if (fileUrl != null && fileName.length>0) {
                val fileRef = storageRef.child("files/${System.currentTimeMillis()}${getExtension(fileUrl!!)}")
                fileRef.putFile(fileUrl!!).continueWithTask { photoUploadTask ->
                    fileRef.downloadUrl
                }.continueWithTask { downloadUrlTask ->
                    val file = getExtension(fileUrl!!)?.let { it1 ->
                        PrivateFile(
                                System.currentTimeMillis(),
                                it1,
                                fileName,
                                downloadUrlTask.result.toString(),
                                signedInUser!!
                        )
                    }
                    dbRef.collection("privateFiles").add(file!!)
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
                    Toast.makeText(this@UploadActivity, "File Uploaded", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                Toast.makeText(this@UploadActivity, "Fields cannot be empty", Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }
    fun uploadImagePublic() {
        btnGallery.setOnClickListener {
            resultImage.launch("image/*")
        }
        btnUploadImgToFirebasePublic.setOnClickListener {
            val fileName=etImageName.text.toString()
            if(imageUrl!=null){
                val photoRef=storageRef.child("images/${System.currentTimeMillis()}-photo.jpg")
                photoRef.putFile(imageUrl!!).continueWithTask { photoUploadTask->
                    photoRef.downloadUrl
                }.continueWithTask { downloadUrlTask->
                    val file = PrivateFile(
                                System.currentTimeMillis(),
                                ".jpg",
                                fileName,
                                downloadUrlTask.result.toString(),
                                signedInUser!!
                        )
                    dbRef.collection("files").add(file)
                }.addOnCompleteListener { postCreationTask->
                    if(!postCreationTask.isSuccessful) {
                        Toast.makeText(this@UploadActivity,"${postCreationTask.exception}",Toast.LENGTH_SHORT).show()
                    }
                    val i=Intent(this@UploadActivity,MainActivity::class.java)
                    startActivity(i)
                    Toast.makeText(this@UploadActivity, "Image Uploaded", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            else {
                Toast.makeText(this@UploadActivity,"Image cannot be empty",Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun uploadImagePrivate() {
        btnGallery.setOnClickListener {
            resultImage.launch("image/*")
        }
        btnUploadImgToFirebasePrivate.setOnClickListener {
            val fileName=etImageName.text.toString()
            if(imageUrl!=null){
                val photoRef=storageRef.child("images/${System.currentTimeMillis()}-photo.jpg")
                photoRef.putFile(imageUrl!!).continueWithTask { photoUploadTask->
                    photoRef.downloadUrl
                }.continueWithTask { downloadUrlTask->
                    val file = File(
                            System.currentTimeMillis(),
                            ".jpg",
                            fileName,
                            downloadUrlTask.result.toString(),
                            signedInUser!!
                    )
                    dbRef.collection("privateFiles").add(file)
                }.addOnCompleteListener { postCreationTask->
                    if(!postCreationTask.isSuccessful) {
                        Toast.makeText(this@UploadActivity,"${postCreationTask.exception}",Toast.LENGTH_SHORT).show()
                    }
                    val i=Intent(this@UploadActivity,MainActivity::class.java)
                    startActivity(i)
                    Toast.makeText(this@UploadActivity, "Image Uploaded", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            else {
                Toast.makeText(this@UploadActivity,"Image cannot be empty",Toast.LENGTH_SHORT).show()
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
