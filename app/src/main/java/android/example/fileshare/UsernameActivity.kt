package android.example.fileshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_username.*

class UsernameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_username)
        val user=FirebaseAuth.getInstance().currentUser

        btnSubmitUsername.setOnClickListener {
            var username=etUsername.text.toString()
            if(username.isNotEmpty()){
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()
                user?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("username", "User profile updated.")
                            val i = Intent(this,MainActivity::class.java)
                            startActivity(i)
                            Toast.makeText(this, "Username confirmed, Redirecting", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
            }
            else {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}