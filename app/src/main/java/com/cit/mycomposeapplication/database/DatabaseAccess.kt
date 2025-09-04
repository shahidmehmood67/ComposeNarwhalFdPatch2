package com.cit.mycomposeapplication.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.RectF
//import android.graphics.RectF
import android.util.Log
import com.cit.mycomposeapplication.QuranApplication
import com.cit.mycomposeapplication.models.Aya
import com.cit.mycomposeapplication.models.AyaRect
import com.cit.mycomposeapplication.models.Page
import com.cit.mycomposeapplication.models.Quarter
import com.cit.mycomposeapplication.models.QuarterPage
import com.cit.mycomposeapplication.models.Sora
import com.cit.mycomposeapplication.utils.AppConstants
import com.cit.mycomposeapplication.utils.DisplayInfoUtil.adjustVertical
import com.cit.mycomposeapplication.utils.SharedPref
import com.cit.mycomposeapplication.utils.SharedPref.getIntPref
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


/**
 * A class for access quran databases
 */
class DatabaseAccess(private val context: Context)
/**
 * Empty constructor for database class
 */
{
    /**
     * Function to open connection with database
     *
     * @param path database path in mobile
     * @return database object to start queries
     */
    fun openDB(path: String?): SQLiteDatabase? {
        Log.d("DATABASE", path!!)
        val db: SQLiteDatabase
        db = try {
            SQLiteDatabase.openDatabase(path, null, 0)
        } catch (e: Exception) {
//            e.printStackTrace();
            return null
        }
        return db
    }

    fun getSoraFirstAya(soraNumber: Int): String {
        val sqLiteDatabase = openDB(MAIN_DATABASE)
        val sql: String
        sql = if (soraNumber != 9) {
            "select `text` from `aya` where `soraid`=$soraNumber  limit 1 OFFSET 1;"
        } else {
            "select `text` from `aya` where `soraid`=$soraNumber limit 1"
        }
        val cursor = sqLiteDatabase!!.rawQuery(sql, null)
        var aya = ""
        if (cursor.moveToFirst()) {
            aya += cursor.getString(0)
        }
        cursor.close()
        closeDB(sqLiteDatabase)
        return aya
    }

    /**
     * Function to close connection with database
     *
     * @param db database object you would to close
     */
    fun closeDB(db: SQLiteDatabase?) {
        db?.close()
    }

    /**
     * Function to get all Qura'n by sora and other some information
     *
     * @return List of Sora
     */
    val allSora: List<Sora>
        get() {
            val allQuranBySora: MutableList<Sora> = ArrayList()
            try {
                val db = openDB(MAIN_DATABASE)

                val scriptType = context.getIntPref(SharedPref.SELECTED_SCRIPT, 1)
                val pageColumn = if (scriptType == 0) "b.page" else "b.page_indopak"

//                Log.d("DatabaseAccess", ": (175) $pageColumn");

                val sql = """
                SELECT a.name, a.name_english, COUNT(b.ayaid), b.joza, MIN($pageColumn), a.place
                FROM sora a, aya b 
                WHERE b.soraid = a.soraid 
                GROUP BY a.name 
                ORDER BY a.soraid;
            """.trimIndent()

                val cursor = db!!.rawQuery(sql, null)
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
/*                    // 🔹 Debug log: print every column so you can verify
                    Log.d(
                        "AllSoraDebug",
                        "name=${cursor.getString(0)}, " +
                                "english=${cursor.getString(1)}, " +
                                "ayahCount=${cursor.getInt(2)}, " +
                                "ayahCount-1=${cursor.getInt(2)-1}, " +
                                "joza=${cursor.getInt(3)}, " +
                                "page=${cursor.getInt(4)}, " +
                                "place=${cursor.getInt(5)}"
                    )*/
                    allQuranBySora.add(
                        Sora(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getInt(2) - 1,
                            cursor.getInt(4),
                            cursor.getInt(3),
                            cursor.getInt(5)
                        )
                    )
                    cursor.moveToNext()
                }
                cursor.close()
                closeDB(db)
            } catch (e: Exception) {
                Log.e("AllSoraError", "Error while fetching allSora", e)
                return allQuranBySora
            }
            return allQuranBySora
        }


    val allQuarters2: List<Quarter>
        /**
         * Function to get all Qura'n by Quarter and other some information
         *
         * @return List of Quraters
         */
        get() {
            var counter = 1
            val allQuranBySora: MutableList<Quarter> = ArrayList()
            try {
                val db = openDB(MAIN_DATABASE)
                val sql =
                    "select b.name , b.name_english , a.soraid , a.page , a.text , a.hezb , a.quarter , a.joza , a.ayaid from aya a" +
                            " , sora b where quarterstart = 1 and b.soraid = a.soraid order by a.soraid , a.ayaid "
                val cursor = db!!.rawQuery(sql, null)
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    allQuranBySora.add(
                        Quarter(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getInt(2),
                            cursor.getInt(3),
                            cursor.getInt(5),
                            cursor.getInt(6),
                            cursor.getString(4),
                            cursor.getInt(8),
                            if (cursor.getInt(6) == 1) counter++ else 0,
                            cursor.getInt(7)
                        )
                    )
                    cursor.moveToNext()
                }
                cursor.close()
                closeDB(db)
            } catch (e: Exception) {
                return allQuranBySora
            }
            return allQuranBySora
        }

    val allQuarters: List<Quarter>
        get() {
            var counter = 1
            val allQuranBySora: MutableList<Quarter> = ArrayList()
            try {
                val db = openDB(MAIN_DATABASE)

                // get script type (0 = Usmani, 1 = IndoPak)
                val scriptType = context.getIntPref(SharedPref.SELECTED_SCRIPT, -1)
                val pageColumn = if (scriptType == 0) "a.page" else "a.page_indopak"

                val sql = """
                SELECT b.name, b.name_english, a.soraid, $pageColumn, a.text, 
                       a.hezb, a.quarter, a.joza, a.ayaid
                FROM aya a, sora b
                WHERE quarterstart = 1 AND b.soraid = a.soraid
                ORDER BY a.soraid, a.ayaid
            """.trimIndent()

                val cursor = db!!.rawQuery(sql, null)
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
/*                    // 🔹 Debug log: print every column so you can verify
                    Log.d(
                        "AllQuarterDebug",
                        "name=${cursor.getString(0)}, " +
                                "english=${cursor.getString(1)}, " +
                                "soraId=${cursor.getInt(2)}, " +
                                "startPageNumber=${cursor.getInt(3)}, " +
                                "hezbNumber=${cursor.getInt(5)}" +
                                "partNumber=${cursor.getInt(6)}" +
                                 "firstVerseText=${cursor.getInt(4)}, " +
                                 "ayaFirstNumber=${cursor.getInt(8)}, " +
                                 "counter=${if (cursor.getInt(6) == 1) counter++ else 0}, " +
                                "joza=${cursor.getInt(7)}, "
                    )*/
                    allQuranBySora.add(
                        Quarter(
                            cursor.getString(0),   // soraName
                            cursor.getString(1),   // soraNameEnglish
                            cursor.getInt(2),      // soraId
                            cursor.getInt(3),      // startPageNumber (depends on scriptType)
                            cursor.getInt(5),      // hezbNumber
                            cursor.getInt(6),      // partNumber
                            cursor.getString(4),   // firstVerseText
                            cursor.getInt(8),      // ayaFirstNumber
                            if (cursor.getInt(6) == 1) counter++ else 0, // counter
                            cursor.getInt(7)       // joza
                        )
                    )
                    cursor.moveToNext()
                }
                cursor.close()
                closeDB(db)
            } catch (e: Exception) {
                return allQuranBySora
            }
            return allQuranBySora
        }


    val allQuartersPages: List<QuarterPage>
        //getAllQuartersPages
        get() {
            val counter = 1
            val allQuranBySora1: MutableList<QuarterPage> = ArrayList()
            val db = openDB(MAIN_DATABASE)
            val sql = "select a.page from aya a" +
                    " , sora b where quarterstart = 1 and b.soraid = a.soraid order by a.soraid , a.ayaid "
            val sq = "select a.page from aya a"
            val cursor = db!!.rawQuery(sql, null)
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                allQuranBySora1.add(QuarterPage(cursor.getInt(0)))
                cursor.moveToNext()
            }
            cursor.close()
            closeDB(db)
            return allQuranBySora1
        }

    /**
     * Function to get page information
     *
     * @param page Page number
     * @return Page object contain all information you need
     */
    fun getPageInfoBoth2(pageNo: Int): Page? {
        val scriptType = context.getIntPref(SharedPref.SELECTED_SCRIPT, -1)
        val actualPage = if (scriptType == 0) pageNo else pageNo + 1
        val pageColumn = if (scriptType == 0) "page" else "page_indopak"

        var pageInfo: Page? = null
        try {
            val db = openDB(MAIN_DATABASE) ?: return null
            val sql = """
                SELECT DISTINCT a.$pageColumn, a.joza, MIN(b.name), MIN(b.name_english), a.hezb
                FROM aya a, sora b
                WHERE a.soraid = b.soraid AND a.$pageColumn = $actualPage
                GROUP BY a.$pageColumn;
            """.trimIndent()
            val cursor = db.rawQuery(sql, null)
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                pageInfo = Page(
                    cursor.getInt(0),
                    pageNumberUsmani = cursor.getInt(0),
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(4),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getInt(4)
                )
                cursor.moveToNext()
            }
            cursor.close()
            closeDB(db)
        } catch (e: Exception) {
            // handle error
        }
        return pageInfo
    }

    fun getPageInfoBoth3(pageNo: Int): Page? {
        val scriptType = context.getIntPref(SharedPref.SELECTED_SCRIPT, -1)
        val actualPage = if (scriptType == 0) pageNo else pageNo + 1
        val pageColumn = if (scriptType == 0) "page" else "page_indopak"

        var pageInfo: Page? = null
        try {
            val db = openDB(MAIN_DATABASE) ?: return null
            val sql = """
            SELECT a.$pageColumn, a.joza, b.name, b.name_english, a.hezb, a.soraid, MIN(a.ayaid)
            FROM aya a
            JOIN sora b ON a.soraid = b.soraid
            WHERE a.$pageColumn = $actualPage
            GROUP BY a.soraid
            ORDER BY a.$pageColumn, MIN(a.ayaid)
            LIMIT 1;
        """.trimIndent()

            val cursor = db.rawQuery(sql, null)
            if (cursor.moveToFirst()) {
                pageInfo = Page(
                    pageNumber = cursor.getInt(0),
                    pageNumberUsmani = cursor.getInt(0),
                    pageNumberIndoPak = cursor.getInt(0),
                    jozaNumber = cursor.getInt(1),
                    soraName = cursor.getString(2),
                    soraNameEnglish = cursor.getString(3),
                    hezbNumber = cursor.getInt(4),
                    soraId = cursor.getInt(5),
                    ayaStart = cursor.getInt(6)
                )
            }
            cursor.close()
            closeDB(db)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return pageInfo
    }

    fun getPageInfoBoth4(pageNo: Int): Page? {
        val scriptType = context.getIntPref(SharedPref.SELECTED_SCRIPT, -1)
        val actualPage = if (scriptType == 0) pageNo else pageNo + 1
        val pageColumn = if (scriptType == 0) "page" else "page_indopak"

        var pageInfo: Page? = null
        try {
            val db = openDB(MAIN_DATABASE) ?: return null
            val sql = """
            SELECT a.page, a.page_indopak, a.joza, b.name, b.name_english, a.hezb, a.soraid, MIN(a.ayaid)
            FROM aya a
            JOIN sora b ON a.soraid = b.soraid
            WHERE a.$pageColumn = $actualPage
            GROUP BY a.soraid
            ORDER BY a.$pageColumn, MIN(a.ayaid)
            LIMIT 1;
        """.trimIndent()

            val cursor = db.rawQuery(sql, null)
            if (cursor.moveToFirst()) {
                pageInfo = Page(
                    pageNumber = cursor.getInt(0),         // usmani
                    pageNumberUsmani = cursor.getInt(0),         // usmani
                    pageNumberIndoPak = cursor.getInt(1),  // indo-pak
                    jozaNumber = cursor.getInt(2),
                    soraName = cursor.getString(3),
                    soraNameEnglish = cursor.getString(4),
                    hezbNumber = cursor.getInt(5),
                    soraId = cursor.getInt(6),
                    ayaStart = cursor.getInt(7)
                )
            }
            cursor.close()
            closeDB(db)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return pageInfo
    }

    fun getPageInfoBoth(pageNo: Int): Page? {
        val scriptType = context.getIntPref(SharedPref.SELECTED_SCRIPT, -1)
        val actualPage = if (scriptType == 0) pageNo else pageNo + 1
        val pageColumn = if (scriptType == 0) "page" else "page_indopak"

        Log.d("PageInfo", "Requested pageNo=$pageNo, scriptType=$scriptType")
        Log.d("PageInfo", "Resolved actualPage=$actualPage using column=$pageColumn")

        var pageInfo: Page? = null
        try {
            val db = openDB(MAIN_DATABASE) ?: return null
            val sql = """
            SELECT a.page, a.page_indopak, a.joza, b.name, b.name_english, a.hezb, a.soraid, MIN(a.ayaid)
            FROM aya a
            JOIN sora b ON a.soraid = b.soraid
            WHERE a.$pageColumn = $actualPage
            GROUP BY a.soraid
            ORDER BY a.$pageColumn, MIN(a.ayaid)
            LIMIT 1;
        """.trimIndent()

            Log.d("PageInfo", "SQL query:\n$sql")

            val cursor = db.rawQuery(sql, null)
            Log.d("PageInfo", "Cursor count=${cursor.count}")

            if (cursor.moveToFirst()) {
                val usmaniPage = cursor.getInt(0)
                val indoPakPage = cursor.getInt(1)
                val current = if (scriptType == 0) usmaniPage else indoPakPage

                Log.d("PageInfo", "Row data: usmaniPage=$usmaniPage, indoPakPage=$indoPakPage, current=$current")
                Log.d("PageInfo", "Other fields -> joza=${cursor.getInt(2)}, soraName=${cursor.getString(3)}, " +
                        "soraNameEnglish=${cursor.getString(4)}, hezb=${cursor.getInt(5)}, " +
                        "soraId=${cursor.getInt(6)}, ayaStart=${cursor.getInt(7)}")

                pageInfo = Page(
                    pageNumber = current,
                    pageNumberUsmani = usmaniPage,
                    pageNumberIndoPak = indoPakPage,
                    jozaNumber = cursor.getInt(2),
                    soraName = cursor.getString(3),
                    soraNameEnglish = cursor.getString(4),
                    hezbNumber = cursor.getInt(5),
                    soraId = cursor.getInt(6),
                    ayaStart = cursor.getInt(7)
                )
            } else {
                Log.w("PageInfo", "No data returned for pageNo=$pageNo (actualPage=$actualPage)")
            }

            cursor.close()
            closeDB(db)
        } catch (e: Exception) {
            Log.e("PageInfo", "Error while fetching page info", e)
        }
        Log.d("PageInfo", "Final Page object: $pageInfo")
        return pageInfo
    }


    /**
     * Function to get the part start page
     *
     * @param partNumber Part Number
     * @return Page number
     */
    fun getPartStartPage(partNumber: Int): Int {
        val scriptType = context.getIntPref(SharedPref.SELECTED_SCRIPT, 1)
        val pageColumn = if (scriptType == 0) "page" else "page_indopak"

        Log.d("DatabaseAccess", ": (526) $pageColumn");

        var pageNumber = 0
        val db = openDB(MAIN_DATABASE)
        val sql = "select $pageColumn from aya where joza = $partNumber limit 1 ;"
        val cursor = db!!.rawQuery(sql, null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            pageNumber = cursor.getInt(0)
            cursor.moveToNext()
        }
        cursor.close()
        closeDB(db)
        return pageNumber
    }


    /**
     * Function to get page first aya id (works for both Usmani & IndoPak)
     *
     * @param page Page number
     * @return Aya id
     */
    fun getPageStartAyaID(page: Int): Aya? {
        var aya: Aya? = null
        try {
            val db = openDB(MAIN_DATABASE)
            val scriptType = context.getIntPref(SharedPref.SELECTED_SCRIPT, -1)

            // Choose correct page column based on script
            val pageColumn = if (scriptType == 0) "page" else "page_indopak"
            val pageIncremented = if (scriptType == 0) page else page+1

            val sql = "SELECT ayaid, soraid FROM aya WHERE $pageColumn = $pageIncremented AND ayaid <> 0 LIMIT 1;"
            val cursor = db!!.rawQuery(sql, null)
            if (cursor.moveToFirst()) {
                // aya = Aya(suraID, ayaID)
                aya = Aya(cursor.getInt(1), cursor.getInt(0))
            }
            cursor.close()
            closeDB(db)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return aya
    }


    /**
     * Function to get all ayat of a page (works for both Usmani & IndoPak)
     *
     * @param page Page number
     * @return List of ayat
     */
    fun getPageAyat(page: Int): List<Aya> {
        val pageAyat: MutableList<Aya> = ArrayList()
        try {
            Log.d("getPageAyat", "Requested page: $page")

            val db = openDB(MAIN_DATABASE)
            val scriptType = context.getIntPref(SharedPref.SELECTED_SCRIPT, -1)
//            Log.d("getPageAyat", "Script type: $scriptType")

            // Decide which column to use
            val pageColumn: String
            var pageToQuery = page

            if (scriptType == 0) {
                pageColumn = "page"
            } else {
                pageColumn = "page_indopak"
                pageToQuery = page + 1
            }

//            Log.d("getPageAyat", "Using page column: $pageColumn, page to query: $pageToQuery")

            val sql = """
            SELECT soraid, ayaid 
            FROM aya 
            WHERE $pageColumn = $pageToQuery AND ayaid <> 0 
            ORDER BY soraid, ayaid;
        """.trimIndent()

//            Log.d("getPageAyat", "SQL Query: $sql")

            val cursor = db!!.rawQuery(sql, null)
//            Log.d("getPageAyat", "Cursor count: ${cursor.count}")

            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val soraId = cursor.getInt(0)
                val ayaId = cursor.getInt(1)
//                Log.d("getPageAyat", "Row -> soraid: $soraId, ayaid: $ayaId, page: $pageToQuery")

                pageAyat.add(Aya(page, soraId, ayaId))

                cursor.moveToNext()
            }
            cursor.close()
            closeDB(db)

//            Log.d("getPageAyat", "Total Aya collected: ${pageAyat.size}")

        } catch (e: Exception) {
            Log.e("getPageAyat", "Error: ${e.message}", e)
            return pageAyat
        }
        return pageAyat
    }



    /**
     * Function to get aya position
     *
     * @param soraID Sora id
     * @param ayaID  Aya id
     * @return Position of aya in database
     */
    fun getAyaPosition(soraID: Int, ayaID: Int): Int {
        var position = 0
        try {
            val db = openDB(MAIN_DATABASE)
            val sql = "select count(*) from aya where soraid < $soraID and ayaid <> 0 ;"
            val cursor = db!!.rawQuery(sql, null)
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                position = cursor.getInt(0)
                cursor.moveToNext()
            }
            cursor.close()
            closeDB(db)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return position + (ayaID - 1)
    }

    /**
     * Function to get aya from position
     *
     * @param position Position of aya
     * @return Aya object
     */
    fun getAyaFromPositionUsmani(position: Int): Aya? {
        var aya: Aya? = null
        try {
            val db = openDB(MAIN_DATABASE)
            val sql =
                "select a.name , a.name_english , b.soraid , b.ayaid , b.page , b.text  from sora a , aya b  where b.ayaid <> 0 and a.soraid = b.soraid  limit $position,1 ;"
            val cursor = db!!.rawQuery(sql, null)
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                aya = Aya(
                    cursor.getInt(4),
                    cursor.getInt(3),
                    cursor.getInt(2),
                    cursor.getString(5),
                    cursor.getString(0),
                    cursor.getString(1)
                )
                cursor.moveToNext()
            }
            cursor.close()
            closeDB(db)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return aya
    }

    fun getAyaFromPositionIndopak(position: Int): Aya? {
        var aya: Aya? = null
        try {
            val db = openDB(MAIN_DATABASE)
            val sql =
                "select a.name , a.name_english , b.soraid , b.ayaid , b.page_indopak , b.text  from sora a , aya b  where b.ayaid <> 0 and a.soraid = b.soraid  limit $position,1 ;"
            val cursor = db!!.rawQuery(sql, null)
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                aya = Aya(
                    cursor.getInt(4),
                    cursor.getInt(3),
                    cursor.getInt(2),
                    cursor.getString(5),
                    cursor.getString(0),
                    cursor.getString(1)
                )
                cursor.moveToNext()
            }
            cursor.close()
            closeDB(db)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return aya
    }


    fun getAyaFromPositionBoth(position: Int): Aya? {
        Log.d("DatabaseAccess", "(545) getAyaFromPositionBoth() called with: position = $position");
        var aya: Aya? = null
        try {
            val db = openDB(MAIN_DATABASE) ?: return null
            val scriptType = context.getIntPref(SharedPref.SELECTED_SCRIPT, -1)
            val pageColumn = if (scriptType == 0) "page" else "page_indopak"

            val sql = """
            SELECT a.name, a.name_english, b.soraid, b.ayaid, b.$pageColumn, b.text
            FROM sora a, aya b
            WHERE b.ayaid <> 0 AND a.soraid = b.soraid
            LIMIT $position, 1;
        """.trimIndent()

            val cursor = db.rawQuery(sql, null)
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                aya = Aya(
                    cursor.getInt(4),
                    cursor.getInt(3),
                    cursor.getInt(2),
                    cursor.getString(5),
                    cursor.getString(0),
                    cursor.getString(1)
                )
                cursor.moveToNext()
            }
            cursor.close()
            closeDB(db)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return aya
    }


    /**
     * Function to get aya page number
     *
     * @param soraID  Sora id
     * @param verseID Verse id
     * @return Page number to open
     */
    fun getAyaPage(soraID: Int, verseID: Int): Int {
        val scriptType = context.getIntPref(SharedPref.SELECTED_SCRIPT, -1)
        val pageColumn = if (scriptType == 0) "page" else "page_indopak"
        Log.d("DatabaseAccess", "getAyaPage: (724) $pageColumn");
        var page = 0
        try {
            val db = openDB(MAIN_DATABASE)
            val sql = "select $pageColumn from aya where ayaid = $verseID and soraid = $soraID ;"
            val cursor = db!!.rawQuery(sql, null)
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                page = cursor.getInt(0)
                cursor.moveToNext()
            }
            cursor.close()
            closeDB(db)
            return page
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return page
    }



    private val lastBookmark: Int
        /**
         * Function to get last bookmark id
         *
         * @return Bookmark id
         */
        private get() = try {
            val db = openDB(MAIN_DATABASE)
            val sql = "select bookmarkid from bookmarks order by bookmarkid desc limit 1 ;"
            val cursor = db!!.rawQuery(sql, null)
            cursor.moveToFirst()
            var newBookmarkID = -1
            if (!cursor.isAfterLast) {
                newBookmarkID = cursor.getInt(0)
                cursor.moveToNext()
            }
            cursor.close()
            closeDB(db)
            newBookmarkID
        } catch (e: Exception) {
            -1
        }

    /**
     * Function to add new bookmark
     *
     * @param page Page to bookmark
     * @return Flag success of not
     */
    fun bookmark(page: Int): Boolean {
        return try {
            val db = openDB(MAIN_DATABASE)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val currentDateandTime = sdf.format(Date())
            val row = ContentValues()
            row.put("page", page)
            row.put("bookmarkTime", currentDateandTime)
            //  String sql = "insert into `bookmarks` (`page` , `bookmarkTime`) values (" + page + " , '" + currentDateandTime + "')";
            // db.execSQL(sql);
            val addRow = db!!.insert("bookmarks", null, row) > 0
            closeDB(db)
            addRow
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Function to remove bookmark
     *
     * @return Flag success of not
     */
    fun removeBookmark(page: Int): Boolean {
        return try {
            Log.d("BOOKMARKID", page.toString() + "")
            val db = openDB(MAIN_DATABASE)
            val deleteItem = db!!.delete("bookmarks", "`page` =$page", null) > 0
            //  String sql = "Delete from `bookmarks` where  " + bookmarkID + ";";
            //    db.execSQL(sql);
            closeDB(db)
            deleteItem
        } catch (e: Exception) {
            false
        }
    }

    // page is bookmarked or not
    fun isPageBookmarked(page: Int): Boolean {
        val sqLiteDatabase = openDB(MAIN_DATABASE)
        val cursor = sqLiteDatabase!!.rawQuery(
            "select `page` from `bookmarks` where `page`=$page limit 1",
            null
        )
        var isBookmarked = false
        if (cursor.count > 0) {
            isBookmarked = true
        }
        cursor.close()
        closeDB(sqLiteDatabase)
        return isBookmarked
    }

    /**
     * Function to update bookmark
     *
     * @param bookmarkID
     * @param page
     * @return Flag success of not
     */
    fun updateBookmark(bookmarkID: Int, page: Int): Boolean {
        return try {
            val db = openDB(MAIN_DATABASE)
            val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
            val currentDateandTime = sdf.format(Date())
            val sql =
                "update bookmarks set page = " + page + " , bookmarkTime = '" + currentDateandTime + "' " +
                        " where bookmarkId = " + bookmarkID + " ; "
            db!!.execSQL(sql)
            closeDB(db)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Function to search in quran
     *
     * @return List of aya have same search text
     */
    fun quranSearch(searchText: String): List<Aya> {
        val ayas: MutableList<Aya> = ArrayList()
        try {
            val db = openDB(MAIN_DATABASE)
            val sql =
                "select a.page ,  a.ayaid , a.searchtext , b.name , b.name_english , a.soraid from aya a, " +
                        "sora  b where b.soraid = a.soraid and a.ayaid <> 0 and  a.searchtext like '%" + searchText + "%' order by a.page ;"
            val cursor = db!!.rawQuery(sql, null)
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                ayas.add(
                    Aya(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(5),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                    )
                )
                cursor.moveToNext()
            }
            cursor.close()
            closeDB(db)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ayas
    }

    /**
     * Function to get aya rectangle
     *
     * @param pageNumber Page number
     * @param suraID     Sura ID
     * @param ayaID      aya ID
     * @return Aya Rectangle
     */
    @Synchronized
    fun getAyaRectsMapUsmaniold(pageNumber: Int, suraID: Int, ayaID: Int): List<RectF?>? {
        Log.d("DATABASE", "$pageNumber--$suraID--$ayaID")
        var lastLine = 0
        var newLine = 0
        var strat = true
        var unionFlage = true
        val db = openDB(SELECTION_DATABASE_COPIED)
        val sqlRect =
            "select minx , maxx , miny , maxy , line from ayarects where page = " + pageNumber + " and" +
                    " soraid = " + suraID + " and  ayaid = " + ayaID + " ;"
        Log.d("DatabaseAccess", "getAyaRectsMap: (671) $sqlRect")
        var mainRect = RectF()
        var rects: MutableList<RectF?>? = null
        val cursor = db!!.rawQuery(sqlRect, null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            newLine = cursor.getInt(4)
            if (strat) {
                rects = ArrayList()
                lastLine = newLine
                strat = false
            }
            if (newLine != lastLine) {
                rects!!.add(mainRect)
                mainRect = RectF()
                lastLine = newLine
            }
            mainRect.union(
                RectF(
                    cursor.getInt(0).toFloat(), cursor.getInt(2).toFloat(),
                    cursor.getInt(1).toFloat(), cursor.getInt(3).toFloat()
                )
            )
            unionFlage = true
            cursor.moveToNext()
        }
        if (unionFlage && !strat) rects!!.add(mainRect)
        Log.d("Rects", "rect")
        cursor.close()
        closeDB(db)
        return rects
    }

    @Synchronized
    fun getAyaRectsMapUsmani(pageNumber: Int, suraID: Int, ayaID: Int): List<AyaRect>? {
        Log.d("DATABASE", "$pageNumber--$suraID--$ayaID")
        var lastLine = 0
        var newLine = 0
        var strat = true
        val db = openDB(SELECTION_DATABASE_COPIED)
        val sqlRect =
            "select minx , maxx , miny , maxy , line from ayarects where page = " + pageNumber + " and" +
                    " soraid = " + suraID + " and  ayaid = " + ayaID + " ;"
        Log.d("DatabaseAccess", "getAyaRectsMap: (671) $sqlRect")
        var currentRect: AyaRect? = null
        var rects: MutableList<AyaRect>? = null
        val cursor = db!!.rawQuery(sqlRect, null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            newLine = cursor.getInt(4)
            if (strat) {
                rects = ArrayList()
                lastLine = newLine
                strat = false
            }

            val minx = cursor.getInt(0).toFloat()
            val maxx = cursor.getInt(1).toFloat()
            val miny = cursor.getInt(2).toFloat()
            val maxy = cursor.getInt(3).toFloat()

            if (newLine != lastLine) {
                rects!!.add(currentRect!!)
                currentRect = AyaRect(minx, miny, maxx, maxy)
                lastLine = newLine
            } else {
                currentRect = if (currentRect == null) {
                    AyaRect(minx, miny, maxx, maxy)
                } else {
                    // Union operation: take the min of left/top and max of right/bottom
                    AyaRect(
                        minOf(currentRect.left, minx),
                        minOf(currentRect.top, miny),
                        maxOf(currentRect.right, maxx),
                        maxOf(currentRect.bottom, maxy)
                    )
                }
            }
            cursor.moveToNext()
        }
        if (!strat && currentRect != null) {
            rects!!.add(currentRect)
        }
        Log.d("Rects", "rect")
        cursor.close()
        closeDB(db)
        return rects
    }

    @Synchronized
    fun getAyaRectsMapIndoPakold(pageNumber: Int, suraID: Int, ayaID: Int): List<RectF> {
        val db = openDB(SELECTION_DATABASE_COPIED_INDOPAK)
        val rects: MutableList<RectF> = ArrayList()
        val sqlRect = "SELECT min_x, max_x, min_y, max_y, line_number " +
                "FROM glyphs " +
                "WHERE page_number = ? AND sura_number = ? AND ayah_number = ? " +
                "ORDER BY line_number ASC"
        var cursor: Cursor? = null
        try {
            cursor = db!!.rawQuery(
                sqlRect,
                arrayOf(pageNumber.toString(), suraID.toString(), ayaID.toString())
            )
            if (cursor == null) {
                Log.d("getAyaRectsMap", "Cursor is null, no data fetched.")
                return rects
            }
            if (!cursor.moveToFirst()) {
                Log.d(
                    "getAyaRectsMap",
                    "No records found for page=$pageNumber, sura=$suraID, aya=$ayaID"
                )
                return rects
            }
            var currentLineRect: RectF? = null
            var lastLine = -1
            do {
                val lineNum = cursor.getInt(cursor.getColumnIndexOrThrow("line_number"))
                val minx = cursor.getFloat(cursor.getColumnIndexOrThrow("min_x"))
                val maxx = cursor.getFloat(cursor.getColumnIndexOrThrow("max_x"))
                val miny = cursor.getFloat(cursor.getColumnIndexOrThrow("min_y"))
                val maxy = cursor.getFloat(cursor.getColumnIndexOrThrow("max_y"))
                Log.d(
                    "getAyaRectsMap",
                    "Record: line=" + lineNum + ", minx=" + minx + ", maxx=" + maxx +
                            ", miny=" + miny + ", maxy=" + maxy
                )
                val rect = RectF(minx, miny, maxx, maxy)
                if (lineNum != lastLine) {
                    if (currentLineRect != null) {
                        rects.add(currentLineRect)
                        Log.d("getAyaRectsMap", "Added Rect for line $lastLine: $currentLineRect")
                    }
                    currentLineRect = RectF(rect)
                    lastLine = lineNum
                } else {
                    currentLineRect!!.union(rect)
                }
            } while (cursor.moveToNext())
            if (currentLineRect != null) {
                rects.add(currentLineRect)
                Log.d("getAyaRectsMap", "Added final Rect for line $lastLine: $currentLineRect")
            }
        } catch (e: Exception) {
            Log.e("getAyaRectsMap", "Exception in getAyaRectsMap", e)
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            closeDB(db)
        }
        Log.d("getAyaRectsMap", "Total rectangles returned: " + rects.size)
        return rects
    }

    @Synchronized
    fun getAyaRectsMapIndoPak(pageNumber: Int, suraID: Int, ayaID: Int): List<AyaRect> {
        val db = openDB(SELECTION_DATABASE_COPIED_INDOPAK)
        val rects: MutableList<AyaRect> = ArrayList()
        val sqlRect = "SELECT min_x, max_x, min_y, max_y, line_number " +
                "FROM glyphs " +
                "WHERE page_number = ? AND sura_number = ? AND ayah_number = ? " +
                "ORDER BY line_number ASC"
        var cursor: Cursor? = null
        try {
            cursor = db!!.rawQuery(
                sqlRect,
                arrayOf(pageNumber.toString(), suraID.toString(), ayaID.toString())
            )
            if (cursor == null) {
                Log.d("getAyaRectsMap", "Cursor is null, no data fetched.")
                return rects
            }
            if (!cursor.moveToFirst()) {
                Log.d(
                    "getAyaRectsMap",
                    "No records found for page=$pageNumber, sura=$suraID, aya=$ayaID"
                )
                return rects
            }
            var currentLineRect: AyaRect? = null
            var lastLine = -1
            do {
                val lineNum = cursor.getInt(cursor.getColumnIndexOrThrow("line_number"))
                val minx = cursor.getFloat(cursor.getColumnIndexOrThrow("min_x"))
                val maxx = cursor.getFloat(cursor.getColumnIndexOrThrow("max_x"))
                val miny = cursor.getFloat(cursor.getColumnIndexOrThrow("min_y"))
                val maxy = cursor.getFloat(cursor.getColumnIndexOrThrow("max_y"))
                Log.d(
                    "getAyaRectsMap",
                    "Record: line=" + lineNum + ", minx=" + minx + ", maxx=" + maxx +
                            ", miny=" + miny + ", maxy=" + maxy
                )

                if (lineNum != lastLine) {
                    if (currentLineRect != null) {
                        rects.add(currentLineRect)
                        Log.d("getAyaRectsMap", "Added Rect for line $lastLine: $currentLineRect")
                    }
                    currentLineRect = AyaRect(minx, miny, maxx, maxy)
                    lastLine = lineNum
                } else {
                    // Union operation: take the min of left/top and max of right/bottom
                    currentLineRect = AyaRect(
                        minOf(currentLineRect!!.left, minx),
                        minOf(currentLineRect.top, miny),
                        maxOf(currentLineRect.right, maxx),
                        maxOf(currentLineRect.bottom, maxy)
                    )
                }
            } while (cursor.moveToNext())

            if (currentLineRect != null) {
                rects.add(currentLineRect)
                Log.d("getAyaRectsMap", "Added final Rect for line $lastLine: $currentLineRect")
            }
        } catch (e: Exception) {
            Log.e("getAyaRectsMap", "Exception in getAyaRectsMap", e)
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            closeDB(db)
        }
        Log.d("getAyaRectsMap", "Total rectangles returned: " + rects.size)
        return rects
    }

    fun getAyaRectsMapBoth(pageNumber: Int, suraID: Int, ayaID: Int): List<AyaRect> {
        val scriptType = context.getIntPref(SharedPref.SELECTED_SCRIPT, -1)
        val yOffset = context.getIntPref(SharedPref.YOFFSET, 0).toFloat()
        return if (scriptType == 0) {
            Log.d("DatabaseAccess", "getAyaRectsMapBoth: (969) yOffset: $yOffset");
            val rects = getAyaRectsMapUsmani(pageNumber, suraID, ayaID)?.filterNotNull() ?: emptyList()
            rects.adjustVertical(offsetTop = yOffset, offsetBottom = yOffset)
        } else {
            Log.d("DatabaseAccess", "getAyaRectsMapBoth: (972) yOffset: $yOffset");
            val rects = getAyaRectsMapIndoPak(pageNumber+1, suraID, ayaID)
            val swQualifier = context.getIntPref(SharedPref.SWQUALIFIER, 0)
            if (swQualifier >= 411) {
                Log.d("DatabaseAccess", "getAyaRectsMapBoth: (979) ");
                adjustAyaRects(rects, yOffset) // row-wise correction
            }else{
                Log.d("DatabaseAccess", "getAyaRectsMapBoth: (981) ");
                rects.adjustVertical(offsetTop = yOffset, offsetBottom = yOffset)
            }
        }
    }


    // Row ranges (from your table)
    private val rowRanges = listOf(
        1 to (65f..215f),
        1 to (66f..215f),
        2 to (215f..340f),
        3 to (340f..464f),
        4 to (464f..589f),
        5 to (589f..714f),
        6 to (714f..839f),
        7 to (839f..964f),
        8 to (964f..1089f),
        9 to (1089f..1213f),
        10 to (1213f..1338f),
        11 to (1338f..1463f),
        12 to (1463f..1588f),
        13 to (1588f..1713f),
        14 to (1713f..1837f),
        15 to (1837f..1984f),
        15 to (1837f..1987f)
    )


    /**
     * Find which row a rect belongs to using its top/bottom
     */
    fun findRowForRect(top: Float, bottom: Float): Int {
        // First, check exact matches
        val exactRow = rowRanges.firstOrNull { (row, range) ->
            top == range.start && bottom == range.endInclusive
        }?.first
        if (exactRow != null) {
            Log.d("DatabaseAccess", "findRowForRect (EXACT): top=$top bottom=$bottom -> row=$exactRow")
            return exactRow
        }

        // Otherwise, use normal "in range" logic
        val row = rowRanges.firstOrNull { (_, range) ->
            top in range || bottom in range
        }?.first ?: -1

        Log.d("DatabaseAccess", "findRowForRect: top=$top bottom=$bottom -> row=$row")
        return row
    }



    /**
     * Get row-specific offsets based on YOFFSET
     * Example rules:
     *   rows near the top shift less, middle rows shift more, etc.
     */
    fun getRowOffsets(row: Int, yOffset: Float): Pair<Float, Float> {
        if (row == -1) return 0f to 0f

        val (startOffset, endOffset) = when (row) {
            1, 2 -> -8f to -8f     // top rows, slight difference
            3 -> -12f to -12f
            4, 5 -> -26f to -26f
            in 6..9 -> yOffset to yOffset
            10 -> -32f to -32f
            11 -> -44f to -46f
            12 -> -48f to -48f
            13 -> -50f to -50f
            14 -> -50f to -50f
            15 -> -50f to -60f  // strongest shift, different ends
            else -> yOffset to yOffset
        }

        Log.d(
            "DatabaseAccess",
            "getRowOffsets: (1032) row=$row startOffset=$startOffset endOffset=$endOffset"
        )

        return startOffset to endOffset
    }


    fun getRowStartEndOffsets(rectLeft: Float, rectRight: Float): Pair<Float, Float> {
        var left = rectLeft
        var right = rectRight

        // If rect touches the left side (≈60px), shift a bit more
        if (left <= 60f) {
            left += 10f
        }

        // If rect touches the right side (≈1092px), shift a bit less
        if (right >= 1092f) {
            right -= 10f
        }

        return left to right
    }



    /**
     * Adjust a list of rects row by row
     */
    fun adjustAyaRects(rects: List<AyaRect>, yOffset: Float): List<AyaRect> {
        return rects.map { rect ->
            val rowNumber = findRowForRect(rect.top, rect.bottom)
            val (offsetTop, offsetBottom) = getRowOffsets(rowNumber, yOffset)
            val (offsetLeft, offsetRight) = getRowStartEndOffsets(rect.left, rect.right)
            AyaRect(offsetLeft, rect.top + offsetTop, offsetRight, rect.bottom + offsetBottom)
        }
    }



    /**
     * Function to get aya touched rectangle dimensions and info
     *
     * @param pageNumber Page number
     * @param positionX  Touch X position
     * @param positionY  Touch Y position
     * @return Aya Selection
     */
    fun getTouchedAyaUsmani(pageNumber: Int, positionX: Float, positionY: Float): Aya {
        var suraID = -1
        var ayaID = -1
        val db = openDB(SELECTION_DATABASE_COPIED)
        val sqlPosition = "select soraid , ayaid from ayarects where page = " +
                "" + pageNumber + " and minx <= " + positionX + " and maxx >= " +
                positionX + " and miny <= " + positionY + " and maxy >= " + positionY + " ;"
        val cursor = db!!.rawQuery(sqlPosition, null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            suraID = cursor.getInt(0)
            ayaID = cursor.getInt(1)
            cursor.moveToNext()
        }
        cursor.close()
        closeDB(db)
        return Aya(pageNumber, suraID, ayaID, getAyaRectsMapBoth(pageNumber, suraID, ayaID))
    }

    /**
     * Function to get aya touched rectangle dimensions and info (IndoPak script)
     *
     * @param pageNumber Page number
     * @param positionX  Touch X position
     * @param positionY  Touch Y position
     * @return Aya Selection
     */
    fun getTouchedAyaIndoPakMod1(pageNumber: Int, positionX: Float, positionY: Float): Aya {
        Log.d("DatabaseAccess", "(1135) getTouchedAyaIndoPak() called with: pageNumber = $pageNumber, positionX = $positionX, positionY = $positionY");
//        var pageNumberIncremented = pageNumber
        var pageNumberIncremented = pageNumber+1
        var suraID = -1
        var ayaID = -1
        val db = openDB(SELECTION_DATABASE_COPIED_INDOPAK)
        val sqlPosition = """
        SELECT sura_number, ayah_number 
        FROM glyphs 
        WHERE page_number = $pageNumberIncremented 
          AND min_x <= $positionX AND max_x >= $positionX 
          AND min_y <= $positionY AND max_y >= $positionY
        LIMIT 1
    """.trimIndent()

        var cursor: Cursor? = null
        try {
            cursor = db!!.rawQuery(sqlPosition, null)
            if (cursor.moveToFirst()) {
                suraID = cursor.getInt(cursor.getColumnIndexOrThrow("sura_number"))
                ayaID = cursor.getInt(cursor.getColumnIndexOrThrow("ayah_number"))
            }
        } finally {
            cursor?.close()
            closeDB(db)
        }

        return Aya(pageNumber, suraID, ayaID, getAyaRectsMapBoth(pageNumber, suraID, ayaID))
    }

    fun getTouchedAyaIndoPak(pageNumber: Int, positionX: Float, positionY: Float): Aya {
        Log.d("DatabaseAccess", "(1135) getTouchedAyaIndoPak() called with: pageNumber = $pageNumber, positionX = $positionX, positionY = $positionY")

        var pageNumberIncremented = pageNumber + 1
        var suraID = -1
        var ayaID = -1

        // Get database instance with detailed logging
        val db = openDB(SELECTION_DATABASE_COPIED_INDOPAK)
        if (db == null) {
            Log.e("DatabaseAccess", "Database is null - may not have been opened successfully")
            return Aya(pageNumber, suraID, ayaID, emptyList())
        }

        // Log database path and version
        Log.d("DatabaseAccess", "Database path: ${db.path}")
        Log.d("DatabaseAccess", "Database version: ${db.version}")

        // Check if the glyphs table exists
        val tableCheckSql = "SELECT name FROM sqlite_master WHERE type='table' AND name='glyphs'"
        val tableCursor = db.rawQuery(tableCheckSql, null)
        if (tableCursor.count == 0) {
            Log.e("DatabaseAccess", "glyphs table does not exist in the database")

            // List all available tables for debugging
            val allTablesCursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
            Log.d("DatabaseAccess", "Available tables:")
            while (allTablesCursor.moveToNext()) {
                val tableName = allTablesCursor.getString(allTablesCursor.getColumnIndexOrThrow("name"))
                Log.d("DatabaseAccess", " - $tableName")
            }
            allTablesCursor.close()
        } else {
            Log.d("DatabaseAccess", "glyphs table exists")
        }
        tableCursor.close()

        val sqlPosition = """
        SELECT sura_number, ayah_number 
        FROM glyphs 
        WHERE page_number = $pageNumberIncremented 
          AND min_x <= $positionX AND max_x >= $positionX 
          AND min_y <= $positionY AND max_y >= $positionY
        LIMIT 1
    """.trimIndent()

        Log.d("DatabaseAccess", "Executing SQL: $sqlPosition")

        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(sqlPosition, null)
            Log.d("DatabaseAccess", "Query returned ${cursor.count} rows")

            if (cursor.moveToFirst()) {
                suraID = cursor.getInt(cursor.getColumnIndexOrThrow("sura_number"))
                ayaID = cursor.getInt(cursor.getColumnIndexOrThrow("ayah_number"))
                Log.d("DatabaseAccess", "Found sura: $suraID, aya: $ayaID")
            } else {
                Log.d("DatabaseAccess", "No results found for the query")
            }
        } catch (e: Exception) {
            Log.e("DatabaseAccess", "Error executing query: ${e.message}")
            e.printStackTrace()
        } finally {
            cursor?.close()
            closeDB(db)
        }

        return Aya(pageNumber, suraID, ayaID, getAyaRectsMapBoth(pageNumber, suraID, ayaID))
    }

    fun getTouchedAya(pageNumber: Int, positionX: Float, positionY: Float): Aya {
        val scriptType = context.getIntPref(SharedPref.SELECTED_SCRIPT, -1)
//        val yOffset = context.getIntPref(SharedPref.YOFFSET, 0).toFloat()
        Log.d("DatabaseAccess", "(1165) getTouchedAyaBoth() called with: pageNumber = $pageNumber, positionX = $positionX, positionY = $positionY");
        return if (scriptType == 0) {
            Log.d("DatabaseAccess", "getTouchedAyaBoth: (1166) ");
            getTouchedAyaUsmani(pageNumber,positionX , positionY)
        } else {
            Log.d("DatabaseAccess", "getTouchedAyaBoth: (1169) ");
            getTouchedAyaIndoPak(pageNumber ,positionX , positionY)
        }
    }


    /**
     * Function to get sura name
     *
     * @param suraID Sura id
     * @return Object of sura name
     */
    fun getSuraNameByID(suraID: Int): Sora? {
        var sora: Sora? = null
        try {
            val db = openDB(MAIN_DATABASE)
            val sql = "select * from sora where soraid = $suraID ; "
            val cursor = db!!.rawQuery(sql, null)
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                sora = Sora(cursor.getString(1), cursor.getString(2))
                cursor.moveToNext()
            }
            cursor.close()
            closeDB(db)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sora
    }

    //function page contain quarter
    fun getPageQuarter(pageNumber: Int): Int {
        var Quarter = 0
        try {
            val db = openDB(MAIN_DATABASE)
            val sql = "select quarter from aya where page = $pageNumber ;"
            val cursor = db!!.rawQuery(sql, null)
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                Quarter = cursor.getInt(0)
                cursor.moveToNext()
            }
            cursor.close()
            closeDB(db)
            return Quarter
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Quarter
    }

    //function if page contain hezb
    fun getPageHezb(pageNumber: Int): Int {
        var hezbNumber = 0
        try {
            val db = openDB(MAIN_DATABASE)
            val sql = "select hezb from aya where page = $pageNumber ;"
            val cursor = db!!.rawQuery(sql, null)
            if (cursor.moveToLast()) {
                hezbNumber = cursor.getInt(0)
            }
            cursor.close()
            closeDB(db)
            return hezbNumber
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return hezbNumber
    }

    companion object {
        val MAIN_DATABASE = QuranApplication.cw.filesDir.toString() + AppConstants.Paths.MAIN_DATABASE_PATH
        val TAFSEER_DATABASE = QuranApplication.cw.filesDir.toString() + AppConstants.Paths.TAFSEER_DATABASE_PATH
        val TRANSLATION_DATABASE = QuranApplication.cw.filesDir.toString() + AppConstants.Paths.MAIN_DATABASE_PATH
        val SELECTION_DATABASE_COPIED_INDOPAK =
            QuranApplication.cw.filesDir.toString() + "/al_quran_data" + "/quranpages_info_indopak.db"
        val SELECTION_DATABASE_COPIED =
            QuranApplication.cw.filesDir.toString() + "/al_quran_data" + "/quranpages.sqlite"
    }
}
