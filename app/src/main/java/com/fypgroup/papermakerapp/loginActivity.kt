package com.fypgroup.papermakerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class loginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        val txtemail = findViewById<EditText>(R.id.login_emailid);
        val password = findViewById<EditText>(R.id.login_password);
        val fauth = FirebaseAuth.getInstance();
        val btnlogin = findViewById<Button>(R.id.loginBtn);
        val progressBar = findViewById<ProgressBar>(R.id.pblogin);
        val errorshow = findViewById<TextView>(R.id.errorshow)

        btnlogin.setOnClickListener {
            errorshow.visibility=View.GONE;
            if (txtemail.length()==0){
                txtemail.error="Please Enter Email To Login"
            }
            else if (password.length()==0){
                password.error="Please Enter Password To Login"
            }
            else
            {
                progressBar.visibility=View.VISIBLE;
                fauth.signInWithEmailAndPassword(txtemail.text.toString().trim(),password.text.toString()).addOnCompleteListener {
                    task ->
                    if (task.isSuccessful)
                    {
                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent);
                        finish();
                    }
                    else
                    {   progressBar.visibility=View.GONE;
                        errorshow.visibility=View.VISIBLE;
                        errorshow.text="Invalid Email or Password"
                    }
                }
            }
        }


    }
    fun donthaveacc(view:View)
    {
        val intent = Intent(  this,RegisterActivity::class.java )
        startActivity(intent)
        finish()
    }
    fun resetpassword(view:View){
        val intent = Intent(this , ResetPassword::class.java)
        startActivity(intent)
        finish()
    }
}