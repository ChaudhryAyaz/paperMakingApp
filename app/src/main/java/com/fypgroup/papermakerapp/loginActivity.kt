package com.fypgroup.papermakerapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View

import android.widget.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


class  loginActivity : AppCompatActivity() {
    var counter = 0;
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
                        val user = FirebaseAuth.getInstance().currentUser;
                        if (user?.isEmailVerified != true)
                        {
                            txtemail.setError("Please Verify your Email Try Again")

                            val snackBar = Snackbar.make( findViewById(android.R.id.content)  , "Email Not Verified Please check your inbox and verifiy it",
                                Snackbar.LENGTH_LONG
                            ).setAction("Action", null)
                            snackBar.setActionTextColor(Color.BLUE)
                            val snackBarView = snackBar.view
                            snackBarView.setBackgroundColor(Color.CYAN)
                            val textView = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
                            textView.setTextColor(Color.BLUE)
                            snackBar.show()
                            user?.sendEmailVerification()?.addOnCompleteListener {

                                if (it.isSuccessful) {

                                } else{
                                    Toast.makeText(this, "Email Cannot be sent at this time Try Again", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }

                            FirebaseAuth.getInstance().signOut();
                            progressBar.visibility=View.GONE;


                        }else{
                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent);
                        finish();}

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
    fun changeinputtype(view: View) {
        val imgbtn  = findViewById<ImageButton>(R.id.btnshowpassword2)
        val passwordview = findViewById<EditText>(R.id.login_password)
        if (counter==0) {
            counter=1
            passwordview.transformationMethod = HideReturnsTransformationMethod.getInstance()
            imgbtn.setImageResource(R.drawable.eye_off_svgrepo_com)
        }
        else{
            counter=0
            passwordview.transformationMethod = PasswordTransformationMethod.getInstance();
            imgbtn.setImageResource(R.drawable.ic_baseline_remove_red_eye_24)
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