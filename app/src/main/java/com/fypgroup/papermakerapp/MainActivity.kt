package com.fypgroup.papermakerapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth



class MainActivity : AppCompatActivity() {
    private val EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1
    private val INTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2
    lateinit var bottomNavigationView : NavigationBarView
    private lateinit var navController: NavController

    companion object {
        private const val PREFS_NAME = "MyPrefs"
        private const val KEY_FIRST_TIME = "firstTime"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = "Paper Making App"
        val STORAGE_PERMISSION_REQUEST_CODE = 1


        val sharedPreferences: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isFirstTime: Boolean = sharedPreferences.getBoolean(KEY_FIRST_TIME, true)

        if (isFirstTime) {
            val demo : String = "<p>i. Suppose that a charge q is moving in a uniform magnetic field with a velocity v why is there no work done by the magnetic force that acts on the charge q?</p> <p>ii. If a charged particle moves in a straight line through some region of space, can you say that the magnetic field in the region is zero?</p> <p>iii. Why does the picture on a TV screen become distorted when a magnet is brought the screen?</p> <p>iv. Is it possible to orient a current loop in a uniform magnetic field such that the loop will not tends to rotate? Explain</p> <p>v. Why the resistance of an ammeter should be very low?</p> <p>vi. Four unmarked wires emerge from a transformer. What steps would you take to determine the turns ratio?</p> <p>vii. Can an electric motor be used to drive an electric generator with the output from the generator being used to operate the motor?</p> <p>viii. Can a D.C. motor be turned into D.C. generator? What changes are required to be done?</p> <p>ix. Does the induced emf always acts to decrees the magnetic flux through a circuit.</p> <p>x. When does light behave as a wave? When does it behave as a particle?</p> <p>xi. Why don't we observe a Compton effect with visible light?</p> <p>xii. Will bright light eject more electrons from a metal surface than dimmer light of the same color?</p> <p>xiii. Write the postulate of special theory of relativity.</p> <p>xiv. What are eddy currents?</p> <p>xv. Write the uses of CRO?</p> <p>Long Questions</p> <p>Q. No.1</p> <p>a) What is the magnetic force? Find the torque on current carrying coil?</p> <p>b) The back emf in a motor is 120V when the motor is turning at 1680 rev per minute. What is the back emf when the motor turns 3360 rev per minute?</p> <p>Q. No. 2</p> <p>a) Write a note on A.C. generator?</p> <p>b) What currents should pass through a solenoid that is 0.5m long with 1000 turns of copper wire so that it will have a magnetic field of 0.4T?</p>\n"

            val paper = dataclasspaper(

                title =  "Demo Paper", content = demo
            )
            paperdatabasehelper(this).insert(paper);


            // Update the first time flag in SharedPreferences
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putBoolean(KEY_FIRST_TIME, false)
            editor.apply()
        }





        val user = FirebaseAuth.getInstance().currentUser;
        user?.let {
            val name = it.displayName;
        }
        val fauth = FirebaseAuth.getInstance()
        if (fauth.currentUser == null) {
            val intent = Intent(this, loginActivity::class.java)
            startActivity(intent)
            finish()

        }

        val homefrag = HomeFragment();
        val userprofile = UserProfleFragment1();
        val contactUsfrag = ContactUsFragment();
        setCurrentFragment(HomeFragment())
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Add OnDestinationChangedListener to hide/show the bottom navigation bar

        bottomNavigationView.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.home -> setCurrentFragment(homefrag)
                R.id.userprofile -> setCurrentFragment(userprofile)
                R.id.contact_us -> setCurrentFragment(contactUsfrag)

            }
            true
        }


    }
    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()

        }


    private fun hideBottomNavigationBar() {
        bottomNavigationView?.visibility = View.GONE
    }

    private fun showBottomNavigationBar() {
        bottomNavigationView?.visibility = View.VISIBLE
    }

    fun logout()
    {
        FirebaseAuth.getInstance().signOut();
        val intent = Intent(this,loginActivity::class.java);
        startActivity(intent);
        finish();
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val currentFragment = supportFragmentManager.findFragmentById(R.id.flFragment)
//        when (currentFragment) {
//            is addnewpaperFragment -> {
//                menuInflater.inflate(R.menu.addnewpapermenu, menu)
//                return true
//            }
//            is HomeFragment -> {
//                menuInflater.inflate(R.menu.appbarmenu, menu)
//                return true
//            }
//
//        }
//
//        menuInflater.inflate(R.menu.appbarmenu,menu)
//        return true;
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val addnewpaperfragment = addnewpaperFragment()
//        val id = item.itemId;
//        when(id) {
//            R.id.logout->logout()
//
//
//        }
//        return true;
//    }

    private fun showPopupAndNavigate(destination: Int) {
        val inflater = layoutInflater
        val popupView = inflater.inflate(R.layout.alertdialoguescd, null)

        val btnSave: Button = popupView.findViewById(R.id.btnSave)
        val btnCancel: Button = popupView.findViewById(R.id.btnCancel)
        val btnDiscard: Button = popupView.findViewById(R.id.btnDiscard)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(popupView)

        val dialog = dialogBuilder.create()

        btnSave.setOnClickListener {
            // Handle save option
            dialog.dismiss()
            navController.navigate(destination)
        }

        btnCancel.setOnClickListener {
            // Handle cancel option
            dialog.dismiss()
        }

        btnDiscard.setOnClickListener {
            // Handle discard option
            dialog.dismiss()
            navController.navigate(destination)
        }

        dialog.show()
    }




    private fun performExternalStorageOperations() {
        // Perform read/write operations on external storage here
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun performInternalStorageOperations() {
        // Perform read/write operations on internal storage here
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // External storage permission granted, proceed with read/write operations

                } else {
                    // External storage permission denied, handle accordingly (e.g., show a message)
                }
            }
            INTERNAL_STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Internal storage permission granted, proceed with read/write operations

                } else {
                    // Internal storage permission denied, handle accordingly (e.g., show a message)
                }
            }
        }
    }



}




