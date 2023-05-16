package com.fypgroup.papermakerapp

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Dialog

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import android.text.style.BulletSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SearchEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast

import com.google.android.material.floatingactionbutton.FloatingActionButton


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var btnplus: FloatingActionButton
    private lateinit var listView: ListView
    private lateinit var searchView: SearchView
    private lateinit var papersdb: ArrayList<dataclasspaper>

    private lateinit var databaseHelper: paperdatabasehelper





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    companion object {
        private const val DATABASE_NAME = "allpapers.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "papers"
        private const val COL_ID = "id"
        private const val COL_TITLE = "title"
        private const val COL_CONTENT = "content"



    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view =  inflater.inflate(R.layout.fragment_home, container, false)
        searchView = view.findViewById(R.id.txtsearch)
        listView = view.findViewById(R.id.list_pprs)

        btnplus = view.findViewById(R.id.fab)
        btnplus.setOnClickListener{
            val newNoteFragment = addnewpaperFragment()
            fragmentManager?.beginTransaction()?.replace(R.id.flFragment, newNoteFragment)?.addToBackStack(null)?.commit()

        }

        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        papersdb = ArrayList()
        databaseHelper = paperdatabasehelper(requireContext())
        loadData()
        val adapter = PaperAdapter(requireContext(), papersdb)
        listView.adapter = adapter
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })

    }
    @SuppressLint("Range")
    private fun loadData() {
        val cursor = databaseHelper.readableDatabase.query(TABLE_NAME, null, null, null, null, null, null)
        papersdb.clear()
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(COL_ID))
            val title = cursor.getString(cursor.getColumnIndex(COL_TITLE))
            val content = cursor.getString(cursor.getColumnIndex(COL_CONTENT))
            val paper = dataclasspaper(id, title, content)
            papersdb.add(paper)
        }
        cursor.close()

    }
    private fun showData() {



    }
    }

