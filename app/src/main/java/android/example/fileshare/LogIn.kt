package android.example.fileshare

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest


class LogIn : AppCompatActivity() {

    private val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        if(firebaseUser!=null)
        {
            homeActivity()
        }
        else{
            val providers = arrayListOf(
                AuthUI.IdpConfig.PhoneBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
            )

            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(providers)
                .build()
            signInLauncher.launch(signInIntent)
        }
    }
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        val user = FirebaseAuth.getInstance().currentUser
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            Log.d("userDisplayName", "onSignInResult: ${user?.displayName}")
            if(user?.displayName!=null){
                Toast.makeText(this@LogIn, "Logging In", Toast.LENGTH_SHORT).show()
                homeActivity()
            }
            else {
                val i2=Intent(this@LogIn,UsernameActivity::class.java)
                startActivity(i2)
                finish()
            }
        } else {
            if (response == null) {
                // User pressed back button
                Toast.makeText(this@LogIn, "Back button pressed!", Toast.LENGTH_SHORT).show()
                return;
            }

            if (response.error?.errorCode == ErrorCodes.NO_NETWORK) {
                return;
            }
            Toast.makeText(this@LogIn, "Please Wait", Toast.LENGTH_SHORT).show()
        }
    }
    private fun homeActivity() {
        val i= Intent(this@LogIn, MainActivity::class.java)
        startActivity(i)
        finish()
    }
}
