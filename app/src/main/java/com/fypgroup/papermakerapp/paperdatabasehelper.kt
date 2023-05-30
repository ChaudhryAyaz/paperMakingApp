package com.fypgroup.papermakerapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class paperdatabasehelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "allpapers.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "papers"
        private const val COL_ID = "id"
        private const val COL_TITLE = "title"
        private const val COL_CONTENT = "content"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable =
            "CREATE TABLE $TABLE_NAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_TITLE TEXT, $COL_CONTENT TEXT);"
        db?.execSQL(createTable)
    }

    fun updateNoteData(id: Long, title: String, content: String): Int {
        val values = ContentValues()
        values.put(COL_TITLE, title)
        values.put(COL_CONTENT, content)

        val db = writableDatabase
        val whereClause = "$COL_ID = ?"
        val whereArgs = arrayOf(id.toString())
        return db.update(TABLE_NAME, values, whereClause, whereArgs)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTable = "DROP TABLE IF EXISTS $TABLE_NAME;"
        db?.execSQL(dropTable)
        onCreate(db)
    }

    fun insert(paper: dataclasspaper) {
        val values = ContentValues().apply {
            put(COL_TITLE, paper.title)
            put(COL_CONTENT, paper.content)
        }
        writableDatabase.insert(TABLE_NAME, null, values)
    }

}
