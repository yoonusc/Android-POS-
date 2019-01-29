package com.ionob.pos.domain

import android.content.ContentValues

import com.ionob.pos.db.Database
import com.ionob.pos.db.DatabaseContents

/**
 * Saves and loads language preference from database.
 *
 * @author Ionob Team
 */
class LanguageController private constructor() {

    /**
     * Returns current language.
     * @return current language.
     */
    /**
     * Sets language for use in application.
     * @param localeString local string of country.
     */
    var language: String
        get() {
            val contents = database!!.select("SELECT * FROM " + DatabaseContents.LANGUAGE)

            if (contents!!.isEmpty()) {
                val defualtLang = ContentValues()
                defualtLang.put("language", DEFAULT_LANGUAGE)
                database!!.insert(DatabaseContents.LANGUAGE.toString(), defualtLang)

                return DEFAULT_LANGUAGE
            }
            val content = contents[0] as ContentValues
            return content.getAsString("language")
        }
        set(localeString) {
            database!!.execute("UPDATE " + DatabaseContents.LANGUAGE + " SET language = '" + localeString + "'")
        }

    companion object {

        private val DEFAULT_LANGUAGE = "en"
        private var database: Database? = null
        private var instance: LanguageController? = null

        internal fun getInstance(): LanguageController {
            if (instance == null)
                instance = LanguageController()
            return instance as LanguageController
        }

        /**
         * Sets database for use in this class.
         * @param db database.
         */
        internal  fun setDatabase(db: Database) {
            database = db
        }
    }

}
