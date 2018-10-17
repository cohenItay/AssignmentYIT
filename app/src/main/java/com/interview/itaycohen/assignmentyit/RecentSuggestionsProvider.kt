package com.interview.itaycohen.assignmentyit

import android.content.ContentProvider
import android.content.ContentValues
import android.content.SearchRecentSuggestionsProvider
import android.database.Cursor
import android.net.Uri

class RecentSuggestionsProvider : SearchRecentSuggestionsProvider() {

    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        val AUTHORITY = RecentSuggestionsProvider::class.java.name
        const val MODE: Int = SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES
    }
}
