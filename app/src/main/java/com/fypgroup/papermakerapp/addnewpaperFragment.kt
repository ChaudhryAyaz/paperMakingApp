package com.fypgroup.papermakerapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [addnewpaperFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class addnewpaperFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var txttitle :EditText;
    lateinit var txtcontent: EditText;
    lateinit var btnsave: Button;
    lateinit var btnbullet : Button;
    lateinit var btnNumbering : Button;
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
        val view =inflater.inflate(R.layout.fragment_addnewpaper, container, false)
        txttitle=view.findViewById(R.id.txttitle)
        txtcontent = view.findViewById(R.id.txtpapercontent)
        btnsave = view.findViewById(R.id.btnsavepaper)
        btnbullet= view.findViewById(R.id.bulletButton);
        btnNumbering=view.findViewById(R.id.numberButton);
        btnbullet.setOnClickListener {
            addBulletToList(view)

        }
        btnNumbering.setOnClickListener {
            addNumberToList(view)
        }
        btnsave.setOnClickListener {
        val paper = dataclasspaper(title = txttitle.text.toString(), content = txtcontent.text.toString().trim())

        paperdatabasehelper(requireContext()).insert(paper);
            Toast.makeText(requireContext(), "Data has been entered succeefully", Toast.LENGTH_SHORT).show()
        }

        // Inflate the layout for this fragment
        return view;
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            addnewpaperFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun addBulletToList(view: View) {
        val selectedText = txtcontent.text.toString().substring(
            txtcontent.selectionStart,
            txtcontent.selectionEnd
        )
        val bulletText = "\u2022 $selectedText\n" // Bullet symbol: \u2022
        insertTextAtCursor(bulletText)
    }

    fun addNumberToList(view: View) {
        val selectedText = txtcontent.text.toString().substring(
            txtcontent.selectionStart,
            txtcontent.selectionEnd
        )
        val numberText = "${txtcontent.lineCount}. $selectedText\n"
        insertTextAtCursor(numberText)
    }

    private fun insertTextAtCursor(text: String) {
        val editable = txtcontent.text
        val start = txtcontent.selectionStart
        editable.insert(start, text)
    }
}