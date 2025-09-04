package com.cit.mycomposeapplication.utils

/**
 * Interface for the app constants
 */
interface AppConstants {
    interface General {
        companion object {
            const val SEARCH_TEXT: String = "SearchText"
            const val PAGE_NUMBER: String = "PageNumber"
            const val PAGE: String = "Page"
            const val BOOK_MARK: String = "bookmark"
            const val BOOK_MARK_TAFSEER: String = "bookmark_tafseer"
            const val AYA_ID: String = "aya_id"
            const val SURA_ID: String = "sura_id"
        }
    }

    /**
     * File and folder paths constants
     */
    interface Paths {
        companion object {
            const val MAIN_DATABASE_PATH: String = "/al_quran_data/quran.sqlite"
            const val TAFSEER_DATABASE_PATH: String = "/al_quran_data/tafaseer"
            const val TAFSEER_LINK: String = "http://quran.islam-db.com/data/tafaseer/tafseer"
        }
    }

    /**
     * File extensions constants
     */
    interface Extensions {
        companion object {
            const val MP3: String = ".mp3"
            const val ZIP: String = ".zip"
            const val SQLITE: String = ".sqlite"
        }
    }

    /**
     * Media player constants
     */
    interface MediaPlayer {
        companion object {
            const val INTENT: String = "quranPageReadPlayer"
            const val PLAY: String = "play"
            const val PAUSE: String = "pause"
            const val STOP: String = "stop"
            const val RESUME: String = "resume"
            const val FORWARD: String = "forward"
            const val BACK: String = "back"
            const val REPEAT_ON: String = "repeatOn"
            const val REPEAT_OFF: String = "repeatOff"
            const val STREAM_LINK: String = "streamLink"
            const val AYAT: String = "ayat"
            const val LOCATIONS_LIST: String = "aya_list_locations"
            const val VERSE: String = "aya"
            const val PLAYING: String = "playing"
            const val OTHER_PAGE: String = "other_page"
            const val PAGE: String = "page"
            const val READER: String = "reader"
            const val ONE_VERSE: String = "one_verse"
            const val SURA: String = "sura"
        }
    }

    /**
     * Download constants
     */
    interface Download {
        companion object {
            const val INTENT: String = "DownloadStatusReciver"
            const val DOWNLOAD_URL: String = "download_url"
            const val DOWNLOAD_LOCATION: String = "download_location"
            const val DOWNLOAD: String = "download"
            const val SUCCESS: String = "success"
            const val FAILED: String = "failed"
            const val NUMBER: String = "Number"
            const val MAX: String = "max"
            const val TYPE: String = "download_type"
            const val IN_DOWNLOAD: String = "in download"
            const val IN_EXTRACT: String = "in extract"
            const val FILES: String = "Files"
            const val UNZIP: String = "unzipped"
            const val DOWNLOAD_LINKS: String = "download_links"
        }
    }

    /**
     * Image highlight constants
     */
    interface Highlight {
        companion object {
            const val INTENT: String = "HighlightAya"
            const val VERSE_NUMBER: String = "ayaNumber"
            const val SORA_NUMBER: String = "soraNumber"
            const val PAGE_NUMBER: String = "pageNumber"
            const val ARG_SECTION_NUMBER: String = "section_number"
            const val RESET_IMAGE: String = "RESETIMAGE"
            const val RESET: String = "reset"
            const val INTENT_FILTER: String = "Quran.mindtrack.image"
        }
    }

    /**
     * applications preferences constants
     */
    interface Preferences {
        companion object {
            //download
            const val DOWNLOAD_FAILED: Int = 400
            const val DOWNLOAD_SUCCESS: Int = 200

            //download types
            const val TAFSEER: Int = 1
            const val IMAGES: Int = 2

            //shared preference keys
            const val CONFIG: String = "configurations"
            const val DOWNLOAD_STATUS: String = "download_status"
            const val DOWNLOAD_STATUS_TEXT: String = "download_status_text"
            const val DOWNLOAD_TYPE: String = "download_type"
            const val DOWNLOAD_ID: String = "download_id"
            const val LAST_PAGE_NUMBER: String = "last_page_number"
            const val SCREEN_RESOLUTION: String = "screen_resolution"
            const val VOLUME_NAVIGATION: String = "volume"
            const val LANGUAGE: String = "app_language"
            const val DEFAULT_EXPLANATION: String = "default_tafseer"
            const val ORIENTATION: String = "orientation"
            const val ARABIC_MOOD: String = "language"
            const val NIGHT_MOOD: String = "night"
            const val TRANSLATIONS: String = "translations"
            const val AYA_APPEAR: String = "aya"
            const val TRANSLATION_SIZE: String = "size"
            const val SELECT_VERSE: String = "select"
            const val STREAM: String = "stream"
        }
    }


    /**
     * Tafseer constants
     */
    interface Tafseer {
        companion object {
            const val INTENT: String = "tafseerMood"
            const val MOOD: String = "tafseer_mode"
            const val AYA: String = "aya"
            const val SORA: String = "sora"
        }
    }
}

