package com.fypgroup.papermakerapp

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title="Paper Making App"

        val user = FirebaseAuth.getInstance().currentUser;
        user?.let {
            val name = it.displayName;
        }
        val fauth = FirebaseAuth.getInstance()
        if(fauth.currentUser == null){
            val intent = Intent(this,  loginActivity::class.java)
            startActivity(intent)
            finish()

        }

        val homefrag = HomeFragment();
        val userprofile =  UserProfleFragment1();
        val contactUsfrag = ContactUsFragment();
        setCurrentFragment(HomeFragment())
        val bottomNavigationView = findViewById<NavigationBarView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home->setCurrentFragment(homefrag)
                R.id.userprofile->setCurrentFragment(userprofile)
                R.id.contact_us->setCurrentFragment(contactUsfrag)

            }
            true
        }


    }
    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()

        }
    fun logout()
    {
        FirebaseAuth.getInstance().signOut();
        val intent = Intent(this,loginActivity::class.java);
        startActivity(intent);
        finish();
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbarmenu,menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId;
        when(id) {
            R.id.logout->logout()
        }
        return true;
    }


}





private fun ActionBar?.setBackgroundDrawable(darkBlue: Int) {
    TODO("Not yet implemented")
}
