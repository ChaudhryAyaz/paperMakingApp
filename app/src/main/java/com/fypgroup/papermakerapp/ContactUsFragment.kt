package com.fypgroup.papermakerapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.fypgroup.papermakerapp.R
import com.fypgroup.papermakerapp.R.id.youtube
import com.google.android.material.textfield.TextInputEditText

class ContactUsFragment : Fragment() {

    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextMessage: TextInputEditText


    private lateinit var youtubeIcon:ImageView
    private lateinit var  facebookIcon:ImageView
    private lateinit var  whatsappIcon:ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact_us, container, false)

        editTextName = view.findViewById(R.id.editTextName)
        editTextEmail = view.findViewById(R.id.editTextEmail)
        editTextMessage = view.findViewById(R.id.editTextMessage)



        youtubeIcon = view.findViewById(R.id.youtube)
        facebookIcon= view.findViewById(R.id.facebook)
        whatsappIcon = view.findViewById(R.id.whatsapp)

        val sendButton: Button = view.findViewById(R.id.sendButton)
        sendButton.setOnClickListener {
            sendEmail()
        }

        // Set a click listener for the YouTube icon
        youtubeIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://youtube.com/shorts/PhqMvUQURc4?feature=share")
            startActivity(intent)
        }


        // Set a click listener for the Facebook icon
        facebookIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://web.facebook.com/ayazullah.Chuadhry/")
            startActivity(intent)
        }

        // Set a click listener for the WhatsApp icon
        whatsappIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://wa.link/mo71lz")
            startActivity(intent)
        }


        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.appbarmenu, menu) // Replace with the correct menu XML file
        super.onCreateOptionsMenu(menu, inflater)
    }
    private fun sendEmail() {
        val name = editTextName.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val message = editTextMessage.text.toString().trim()

        val recipientEmail = "ayazullah4239@gmail.com" // Replace with actual developer's email address
        val subject = "Contact Us - Query from $name"
        val emailBody = "Name: $name\nEmail: $email\nMessage: $message"

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$recipientEmail")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, emailBody)
        }

        val packageManager = requireActivity().packageManager // Access package manager from the activity

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)

            Toast.makeText(context, "Thank you for Contacting we Will reach you Soon", Toast.LENGTH_SHORT).show()


        }
    }
    override fun onResume() {
        super.onResume()
        requireActivity().invalidateOptionsMenu()
    }
    fun reloadfrag() {
        val nextFrag = ContactUsFragment()
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.flFragment, nextFrag, "findThisFragment")
            ?.commit()
    }
    companion object {
        fun newInstance() = ContactUsFragment()
    }

}
