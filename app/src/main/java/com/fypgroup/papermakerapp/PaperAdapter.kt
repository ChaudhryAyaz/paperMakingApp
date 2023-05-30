package com.fypgroup.papermakerapp

import android.content.Context
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.text.HtmlCompat

class PaperAdapter(context: Context, private val notes: ArrayList<dataclasspaper>) : ArrayAdapter<dataclasspaper>(context, 0, notes) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_paper, parent, false)
        }
        val note = notes[position]
        val titleTextView = view!!.findViewById<TextView>(R.id.text_view_title)
        val contentTextView = view.findViewById<TextView>(R.id.text_view_content)
        titleTextView.text = note.title
        val formattedHtml: Spanned = HtmlCompat.fromHtml(note.content, HtmlCompat.FROM_HTML_MODE_LEGACY)
        contentTextView.text = formattedHtml
        return view
    }


}