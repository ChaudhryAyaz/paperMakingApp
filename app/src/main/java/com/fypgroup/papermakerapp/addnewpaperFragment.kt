package com.fypgroup.papermakerapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.chinalwb.are.AREditor

import android.net.Uri
import android.os.Build
import android.os.Environment

import android.provider.MediaStore
import android.text.TextPaint
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer

import android.graphics.*


import com.yalantis.ucrop.UCrop


import java.io.File
import java.io.FileOutputStream

import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.text.Html
import android.text.Spanned
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.layout.LayoutArea
import com.itextpdf.layout.layout.LayoutContext
import com.itextpdf.layout.layout.LayoutResult
import com.itextpdf.layout.renderer.DocumentRenderer
import com.itextpdf.layout.renderer.IRenderer
import com.itextpdf.styledxmlparser.jsoup.Jsoup
import com.itextpdf.styledxmlparser.jsoup.safety.Whitelist

import java.io.IOException

class addnewpaperFragment : Fragment() {

    private lateinit var papersdb: ArrayList<dataclasspaper>
    lateinit var txttitle: EditText;
    private lateinit var databaseHelper: paperdatabasehelper
    lateinit var txtcontent: AREditor;

    private lateinit var captureButton: FloatingActionButton
    private lateinit var textRecognizer: FirebaseVisionTextRecognizer

    companion object {

        private const val PERMISSION_REQUEST_CODE = 123
        private const val REQUEST_IMAGE_CAPTURE = 102
        private const val REQUEST_PERMISSION_CAMERA = 201
        private const val DATABASE_NAME = "allpapers.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "papers"
        private const val COL_ID = "id"
        private const val COL_TITLE = "title"
        private const val COL_CONTENT = "content"




    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle back button press here
                showPopup()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }


    @SuppressLint("ResourceAsColor", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_addnewpaper, container, false)

        papersdb = ArrayList()
        databaseHelper = paperdatabasehelper(requireContext())
        loadData()
        txtcontent = view.findViewById(R.id.txtareeditor)

        txttitle = view.findViewById(R.id.txttitle)
        var paperID: Long? = null
        val receivedValue = arguments?.getInt("position")

        if (receivedValue != null) {
            var itemtobeedited = papersdb[receivedValue]
            paperID = itemtobeedited.id
            val editableTitle: Editable =
                Editable.Factory.getInstance().newEditable(itemtobeedited.title)
            txttitle.text = editableTitle
            txtcontent.fromHtml(itemtobeedited.content)

        }
        captureButton = view.findViewById(R.id.btnCaptureImage)


        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            textRecognizer = FirebaseVision.getInstance().onDeviceTextRecognizer
            captureButton.setOnClickListener {
                checkPermissionAndCaptureImage()
            }
    }

    @SuppressLint("Range")
    private fun loadData() {
        val cursor = databaseHelper.readableDatabase.query(
            addnewpaperFragment.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )
        papersdb.clear()
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(addnewpaperFragment.COL_ID))
            val title = cursor.getString(cursor.getColumnIndex(addnewpaperFragment.COL_TITLE))
            val content = cursor.getString(cursor.getColumnIndex(addnewpaperFragment.COL_CONTENT))

            val paper = dataclasspaper(id, title, content)
            papersdb.add(paper)
        }
        cursor.close()

    }

    fun savetodb() {
        var paperID: Long? = null
        val receivedValue = arguments?.getInt("position")

        if (receivedValue != null) {
            var itemtobeedited = papersdb[receivedValue]
            paperID = itemtobeedited.id

        }
        if (validateinput()) {
            if (receivedValue != null) {
                if (paperID != null) {
                    paperdatabasehelper(requireContext()).updateNoteData(
                        paperID, txttitle.text.toString(),
                        txtcontent.getHtml()
                    )

                }
            } else {
                val paper = dataclasspaper(
                    title = txttitle.text.toString(),
                    content = txtcontent.getHtml()
                )
                paperdatabasehelper(requireContext()).insert(paper);

                Toast.makeText(
                    requireContext(),
                    "Data has been entered succeefully",
                    Toast.LENGTH_SHORT
                ).show()
                gotohome()
            }
        } else {
            Toast.makeText(context, "There Was A Error", Toast.LENGTH_SHORT).show()
        }

    }



    private fun validateinput(): Boolean {
        var flag = false
        val string = txtcontent.getHtml()
        if (txttitle.text.isEmpty()) {
            txttitle.setError("Title Cant be Empty")
            flag = false
        } else if (string.trim().length <= 0) {
            flag = false
            Toast.makeText(context, "Please Enter Paper's Content !", Toast.LENGTH_SHORT).show()
        } else {
            flag = true
        }

        return flag
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.addnewpapermenu, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        var id = item.itemId
        if (id == R.id.savetodb) {
            savetodb()
            return true
        }else if(id== R.id.cancel){
            parentFragmentManager.beginTransaction()
                .replace(R.id.flFragment, HomeFragment())
                .addToBackStack(null)
                .commit()

            return true
        }
        else if (id==R.id.print){
            generatePDFFromFormattedString(txtcontent.getHtml().toString())
            gotohome()
            return true
        }
        else if (id==R.id.export_pdf){

            gotohome()
            return true
        }
        else {
            return super.onOptionsItemSelected(item)
        }

    }

    private fun showPopup() {
        val inflater = LayoutInflater.from(requireContext())
        val popupView: View = inflater.inflate(R.layout.alertdialoguescd, null)

        val btnSave: Button = popupView.findViewById(R.id.btnSave)
        val btnCancel: Button = popupView.findViewById(R.id.btnCancel)
        val btnDiscard: Button = popupView.findViewById(R.id.btnDiscard)

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(popupView)

        val dialog = dialogBuilder.create()

        btnSave.setOnClickListener {
            savetodb()
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {

            dialog.dismiss()
        }

        btnDiscard.setOnClickListener {
            gotohome()
            dialog.dismiss()
        }

        dialog.show()
    }
    fun gotohome(){
        parentFragmentManager.beginTransaction()
            .replace(R.id.flFragment, HomeFragment())
            .addToBackStack(null)
            .commit()

    }

    private fun checkPermissionAndCaptureImage() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            captureImage()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_PERMISSION_CAMERA
            )
        }
    }

    private var capturedImageUri: Uri? = null

    private fun captureImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file = File(requireContext().externalCacheDir, "temp.jpg")
        val uri = FileProvider.getUriForFile(requireContext(), requireContext().packageName + ".provider", file)
        capturedImageUri = uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    private fun startCropActivity(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped.jpg"))

        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f) // Set the desired aspect ratio for cropping
            .start(requireContext(), this)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    capturedImageUri?.let { uri ->
                        startCropActivity(uri)
                    }
                }
                UCrop.REQUEST_CROP -> {
                    val resultUri = UCrop.getOutput(data!!)
                    resultUri?.let { uri ->
                        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                        processImage(bitmap)
                    }
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            // Handle the error occurred during cropping
            Toast.makeText(requireContext(), "Crop failed: ${cropError?.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun processImage(bitmap: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        textRecognizer.processImage(image)
            .addOnSuccessListener { firebaseVisionText ->
                val extractedText = extractTextFromBlocks(firebaseVisionText)
                // Use the extracted text as per your requirement
                txtcontent.fromHtml(extractedText)
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occurred during OCR
                Toast.makeText(requireContext(), "OCR failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun extractTextFromBlocks(firebaseVisionText: FirebaseVisionText): String {
        val stringBuilder = StringBuilder()
        for (block in firebaseVisionText.textBlocks) {
            for (line in block.lines) {
                stringBuilder.append(line.text)
                stringBuilder.append("\n")
            }
        }
        return stringBuilder.toString()
    }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun generatePDFFromFormattedString(input: String) {

        val outputDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val outputFilePath = File(outputDirectory, "output.pdf")
        val pdfWriter = PdfWriter(FileOutputStream(outputFilePath))
        val pdfDocument = PdfDocument(pdfWriter)
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



}
