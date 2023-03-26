package com.fypgroup.papermakerapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ResetPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        supportActionBar?.hide()
    }

    fun resetpassword(view: View) {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
        var email = findViewById<EditText>(R.id.txtresetemail)
        if(email.length()<=0){
            email.setError("Please Enter Email")
        }
        else if (email.text.toString().trim().matches(emailRegex.toRegex()) == false)
        {
            email.setError("Please Enter Valid Email")
        }
        else {
            val prograssbar = findViewById<ProgressBar>(R.id.pbonresetscreen)
            prograssbar.visibility=View.VISIBLE
            val emailAddress = email.text.toString()

            Firebase.auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        prograssbar.visibility=View.GONE
                        Log.d(TAG, "Email sent.")
                        Toast.makeText(this, "Email sent to your Email Address", Toast.LENGTH_SHORT).show()
                        val intent  = Intent(this,loginActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        prograssbar.visibility=View.GONE
                        Toast.makeText(this, "Your account does not exist or Internet is not avilable", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    fun gobacktologin(view: View) {
        val intent = Intent(this,loginActivity::class.java)
        startActivity(intent)
        finish()
    }
}