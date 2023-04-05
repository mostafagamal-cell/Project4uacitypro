package com.udacity.project4.authentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity


/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */

class AuthenticationActivity : AppCompatActivity() {

    lateinit var binding: ActivityAuthenticationBinding
    val SIGN_IN_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(getLayoutInflater())
        setContentView(binding.root)
        Log.i("here ffaa","AuthenticationActivity")
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)

        if(sharedPreference.getBoolean("this",false))
        {
            val intent = Intent(this, RemindersActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.authButton.setOnClickListener({
            launchSignInFlow()
        })
    }

    fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            SIGN_IN_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                Log.i(
                    "ok passed",
                    "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )
                val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
                var editor = sharedPreference.edit()
                editor.putBoolean("this",true)
                editor.apply()
                val intent = Intent(this, RemindersActivity::class.java)
                startActivity(intent)
                finish()
                } else {
                Toast.makeText(this, "canceled opration", Toast.LENGTH_SHORT).show()
            }
        }
    }

}


