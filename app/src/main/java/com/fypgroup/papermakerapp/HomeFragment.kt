package com.fypgroup.papermakerapp

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ClipData

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.BulletSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.SearchEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Text
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


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
        private const val PAGE_WIDTH = 595 // A4 paper width in points (1 point = 1/72 inch)
        private const val PAGE_HEIGHT = 842 // A4 paper height in points

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

                        R.id.export_pdf -> {
                            generatePDFFromFormattedString(position)
                            true
                        }

                        R.id.menu_share -> {
                            shareTextViaOtherApps(position)
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

                adapter.filter(newText)

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




    private fun createBitmapFromTextView(content: String): Bitmap {
        val textView = TextView(requireContext())
        textView.text = content
        textView.measure(
            View.MeasureSpec.makeMeasureSpec(PAGE_WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        textView.layout(0, 0, textView.measuredWidth, textView.measuredHeight)

        val bitmap = Bitmap.createBitmap(textView.measuredWidth, textView.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        textView.draw(canvas)

        return bitmap
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
    fun generatePDFFromFormattedString(position: Int ) {
        var filetitle = papersdb.get(position).title

        val input : String = papersdb.get(position).content
        val outputDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val outputFilePath = File(outputDirectory, "$filetitle+.pdf")
        val pdfWriter = PdfWriter(FileOutputStream(outputFilePath))
        val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(pdfWriter)
        val document = Document(pdfDocument, PageSize.A4)

        val font = PdfFontFactory.createFont()
        val color = ColorConstants.BLACK

        // Split the input string into lines
        // Remove HTML tags while preserving formatting
        val cleanedInput: Spanned = Html.fromHtml(input)

        // Split the cleaned input string into lines
        val lines = TextUtils.split(cleanedInput.toString(), "\n")

        // Iterate over each line and add it to the PDF document
        for (line in lines) {
            val paragraph = Paragraph()

            // Split the line into words
            val words = line.split(" ")

            // Iterate over each word and add it to the paragraph with the specified font and color
            for (word in words) {
                val text = Text(word).setFont(font).setFontColor(color)
                paragraph.add(text)
                paragraph.add(" ")
            }

            document.add(paragraph)
        }

        // Close the document
        document.close()
        Toast.makeText(requireContext()     , "PDF generated successfully. File saved at: $outputFilePath", Toast.LENGTH_SHORT).show()


    }
    fun shareTextViaOtherApps(position: Int) {

        val text  = papersdb.get(position).content

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)

        val chooserIntent = Intent.createChooser(intent, "Share via")
        chooserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        chooserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        // Replace "context" with your actual context object
        context?.startActivity(chooserIntent)
    }

}

