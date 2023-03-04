package com.fypgroup.papermakerapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()
        val txtfullname = findViewById<EditText>(R.id.fullName);
        val txtemail = findViewById<EditText>(R.id.userEmailId);
        val password  = findViewById<EditText>(R.id.password);
        val btnreg = findViewById<Button>(R.id.signUpBtn);
        val fauth =FirebaseAuth.getInstance();
        val progressBar = findViewById<ProgressBar>(R.id.progressBar);
        if(fauth.currentUser != null){
            val intent = Intent(this,  MainActivity::class.java)
            startActivity(intent)
            finish()

        }
        btnreg.setOnClickListener {
            val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
            if (txtfullname.length() == 0) {
                txtfullname.error = "This field is required";
            } else if (txtemail.length() == 0) {
                txtemail.error = "This field is required";
            } else if (password.length() == 0) {
                password.error = "This Field iS required"
            }else if (password.length() < 6) {
                password.error = "The password Must be 6 or more long";
            }
            else if(txtemail.text.toString().trim().matches(emailRegex.toRegex()) == false)
            {
                txtemail.error = "Please Enter valid Email";

            }
            else {
                progressBar.visibility= View.VISIBLE;
                fauth.createUserWithEmailAndPassword(txtemail.text.toString().trim(),password.text.toString()).addOnCompleteListener {
                    task->
                    if(task.isSuccessful){
                        val authuser = FirebaseAuth.getInstance().currentUser;
                        val updateuserprofile = UserProfileChangeRequest.Builder().setDisplayName(txtfullname.text.toString()).build();
                        authuser?.updateProfile(updateuserprofile);

                        val intent = Intent(this,  loginActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this, "Account has been Created Successfully Please Login", Toast.LENGTH_SHORT).show()
                        finish();

                    }
                    else{
                        progressBar.visibility= View.GONE;
                        Toast.makeText(this, "The Task is not completed", Toast.LENGTH_SHORT).show();

                    }
                }
            }

        }

    }
    public  fun alreadyaccound(view: View){
        val intent = Intent(  this,loginActivity::class.java )
        startActivity(intent)
        finish()
    }
}