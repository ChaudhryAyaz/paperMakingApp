package com.fypgroup.papermakerapp

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ClipData

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import android.text.style.BulletSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.SearchEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast

import com.google.android.material.floatingactionbutton.FloatingActionButton



private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var btnplus: FloatingActionButton
    private lateinit var listView: ListView
    private lateinit var searchView: SearchView
    private lateinit var papersdb: ArrayList<dataclasspaper>
    private lateinit var adapter: PaperAdapter
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


        val view = inflater.inflate(R.layout.fragment_home, container, false)
        searchView = view.findViewById(R.id.txtsearch)
        listView = view.findViewById(R.id.list_pprs)

        btnplus = view.findViewById(R.id.fab)
        btnplus.setOnClickListener {
            val newNoteFragment = addnewpaperFragment()
            fragmentManager?.beginTransaction()?.replace(R.id.flFragment, newNoteFragment)
                ?.addToBackStack(null)?.commit()

        }


        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        papersdb = ArrayList()
        databaseHelper = paperdatabasehelper(requireContext())
        loadData()
        adapter = PaperAdapter(requireContext(), papersdb)
        listView.adapter = adapter

        listView.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { _, view, position, _ ->

                val popupMenu = PopupMenu(context, view)
                popupMenu.menuInflater.inflate(R.menu.listitem_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_delete -> {
                            deletepaper(position)
                            true
                        }

                        R.id.menu_duplicate -> {
//                        duplicateItem(item)
                            true
                        }

                        R.id.menu_share -> {
//                        shareItem(item)
                            true
                        }

                        else -> false
                    }
                }
                popupMenu.show()

                true
            }

        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                var itemtoupdate = position
//                Toast.makeText(context, itemtoupdate.title, Toast.LENGTH_SHORT).show()
                val bundle = Bundle()
                bundle.putInt("position", position)

                val destinationFragment = addnewpaperFragment()
                destinationFragment.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.flFragment, destinationFragment)
                    .addToBackStack(null)
                    .commit()
            }



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
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.appbarmenu, menu) // Replace with the correct menu XML file
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onResume() {
        super.onResume()
        requireActivity().invalidateOptionsMenu()
    }
    fun deletepaper(position: Int) {
        val papertodelete = papersdb[position]
        deletePaperFromDatabase(papertodelete.id)
        papersdb.removeAt(position);
        adapter.notifyDataSetChanged()
        Toast.makeText(context, "Paper Has been Deleted Succeefully", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("Range")
    private fun loadData() {
        val cursor =
            databaseHelper.readableDatabase.query(TABLE_NAME, null, null, null, null, null, null)
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

    private fun deletePaperFromDatabase(id: Long) {
        val dbhelper = paperdatabasehelper(requireContext())
        val db = dbhelper.writableDatabase
        val selection = "id = ?"
        val selectedArgs = arrayOf(id.toString())
        val deletedRows = db.delete(TABLE_NAME, selection, selectedArgs)
        db.close()
        if (deletedRows > 0) {

        } else {

        }

    }

}

