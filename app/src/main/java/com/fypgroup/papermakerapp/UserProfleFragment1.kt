package com.fypgroup.papermakerapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserProfleFragment1.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserProfleFragment1 : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var prograssbarimageview: ProgressBar
    lateinit var btncancel: Button
    lateinit var userimageciew: CircleImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CutPasteId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userimageciew = view.findViewById(R.id.userimg)
        val user = FirebaseAuth.getInstance().currentUser;
        var name = ""
        var email = ""
        var oldemail = ""
        user?.let {
            name = it.displayName.toString();
            email = it.email.toString();
            oldemail = it.email.toString();
        }

        prograssbarimageview = view.findViewById(R.id.imageviewprograssbar)
        val txtnewpass = view.findViewById<EditText>(R.id.txtnewpassword)
        val txtoldpass = view.findViewById<EditText>(R.id.txtoldpassword)
        val prograssbar = view.findViewById<ProgressBar>(R.id.pbarPUI)
        var btnchangepassword: Button = view.findViewById(R.id.btnchangepassword)
        val txtname: EditText = view.findViewById(R.id.txtname)
        txtname.setText(name)
        val txtemail: EditText = view.findViewById(R.id.txtemail)
        txtemail.setText(email)
        val btnsave = view.findViewById<Button>(R.id.btnsave)
        val btneditdetails = view.findViewById<Button>(R.id.btnedit)
        btncancel = view.findViewById(R.id.btncancel)

        try {
            prograssbarimageview.visibility = View.VISIBLE
            val storageref = FirebaseStorage.getInstance().reference
            val storageReference = storageref.child(user?.uid.toString() + ".jpg")
            storageReference.downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(userimageciew)

            }

        } catch (e: java.lang.Exception) {
            prograssbarimageview.visibility = View.GONE
        }


        prograssbarimageview.visibility = View.GONE
        btneditdetails.setOnClickListener {
            btnsave.visibility = View.VISIBLE
            btncancel.visibility = View.VISIBLE
            btneditdetails.visibility = View.GONE;
            txtname.isEnabled = true
            txtemail.isEnabled = true
            txtoldpass.visibility = View.VISIBLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                txtname.focusable = View.FOCUSABLE
            }

        }
        btnchangepassword.setOnClickListener {
            var txtnewpassword: EditText = view.findViewById(R.id.txtnewpassword)
            txtnewpassword.visibility = View.VISIBLE;
            var txtoldpassword: EditText = view.findViewById(R.id.txtoldpassword)
            txtoldpassword.visibility = View.VISIBLE
            btnsave.visibility = View.VISIBLE;
            btnchangepassword.visibility = View.GONE;
            btncancel.visibility = View.VISIBLE

        }
        btncancel.setOnClickListener {
            reloadfrag()
        }


        var flag1: Boolean = false
        var flag2: Boolean = false
        btnsave.setOnClickListener {
            val user1 = FirebaseAuth.getInstance().currentUser
            prograssbar.visibility = View.VISIBLE
            if (txtnewpass.visibility == View.VISIBLE) {
                if (txtnewpass.length() < 6) {
                    prograssbar.visibility = View.GONE
                    txtnewpass.setError("New Passwrod must be altease 6 in length")
                } else if (txtoldpass.length() < 6) {
                    prograssbar.visibility = View.GONE
                    txtoldpass.setError("Invalid Password")
                } else {
                    prograssbar.visibility = View.VISIBLE
                    val user = FirebaseAuth.getInstance().currentUser
                    val credential: AuthCredential = EmailAuthProvider.getCredential(
                        txtemail.text.toString(),
                        txtoldpass.text.toString()
                    )
                    user?.reauthenticate(credential)?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            prograssbar.visibility = View.GONE;
                            user?.updatePassword(txtnewpass.text.toString())
                            Toast.makeText(
                                requireContext(),
                                "Password Updated Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            reloadfrag()

                        } else {
                            prograssbar.visibility = View.GONE;
                            txtoldpass.setError("Password Does not Match")

                        }

                    }
                }
            }
            if (btneditdetails.visibility == View.GONE) {
                if (txtoldpass.text.isEmpty()) {
                    prograssbar.visibility = View.GONE
                    txtoldpass.setError("Plase Enter Your Current Password")
                } else {
                    val password = txtoldpass.text.toString()
                    val oldcredential: AuthCredential = EmailAuthProvider.getCredential(
                        oldemail, password
                    )
                    if (!txtname.text.isEmpty()) {
                        val updateuserprofile = UserProfileChangeRequest.Builder()
                            .setDisplayName(txtname.text.toString()).build();
                        flag1 = true
                        user1?.updateProfile(updateuserprofile)?.addOnCompleteListener {

                            if (it.isSuccessful) {

                            } else {
                                flag1 = false
                                prograssbar.visibility = View.GONE
                                Toast.makeText(
                                    requireContext(),
                                    "UserName Did not change succeessfully Please Try Again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }


                    } else {
                        prograssbar.visibility = View.GONE
                        txtname.setError("Please Enter Your Name")
                    }
                    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
                    if (!txtemail.text.isEmpty()) {
                        if (txtemail.text.toString().trim()
                                .matches(emailRegex.toRegex()) != false
                        ) {
                            user1?.reauthenticate(oldcredential)?.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    user1?.updateEmail(txtemail.text.toString().trim())
                                        ?.addOnCompleteListener {
                                            if (it.isSuccessful) {

                                                if (flag1 == true) {
                                                    reloadfrag()
                                                }

                                            } else {
                                                prograssbar.visibility = View.GONE
                                                txtemail.setError("Not updated or Email Taken Already")

                                                Toast.makeText(
                                                    requireContext(),
                                                    "Email Not Updated Successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Opperation not Complete",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }


                        } else {
                            prograssbar.visibility = View.GONE
                            txtemail.setError("Please Enter Valid Email")
                        }
                    }

                }
            }
        }
        val btnchgpic = view.findViewById<ImageButton>(R.id.btnchgimg)
        btnchgpic.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1000)

        }
        val btnlogout = view.findViewById<Button>(R.id.btnlogout)
        btnlogout.setOnClickListener {
            logout()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data1: Intent?) {
        super.onActivityResult(requestCode, resultCode, data1)
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                val imguri: Uri? = data1?.data
                if (imguri != null) {
                    prograssbarimageview.visibility = View.VISIBLE
                    uploadimagetofirebase(imguri)
                }
            }
        }
    }

    private fun uploadimagetofirebase(imgageuri: Uri) {
        var storageRef = FirebaseStorage.getInstance().reference
        val firebaseAuth = FirebaseAuth.getInstance().currentUser

        val st: StorageReference = storageRef.child(firebaseAuth?.uid + ".jpg")
        st.putFile(imgageuri).addOnCompleteListener {
            if (it.isSuccessful) {
                val userimageview = view?.findViewById<CircleImageView>(R.id.userimg)
                prograssbarimageview.visibility = View.GONE
                st.downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it).into(userimageview)
                }

                Toast.makeText(requireContext(), "Image Uploaded Succeessfully", Toast.LENGTH_SHORT)
                    .show()
            } else {
                prograssbarimageview.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "not Completed Please Check Your Internet Connection",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    //    fun  restartfrag(){
//        val frg = requireActivity().supportFragmentManager.findFragmentById(R.id.use)
//        val ft: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
//        ft.detach(frg)
//        ft.attach(frg)
//        ft.commit()
//    }
    fun logout() {
        FirebaseAuth.getInstance().signOut();
        val intent = Intent(requireContext(), loginActivity::class.java);
        startActivity(intent);
    }

    fun changepiccode() {


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserProfleFragment1.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserProfleFragment1().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun reloadfrag() {
        val nextFrag = UserProfleFragment1()
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.flFragment, nextFrag, "findThisFragment")
            ?.commit()
    }
}