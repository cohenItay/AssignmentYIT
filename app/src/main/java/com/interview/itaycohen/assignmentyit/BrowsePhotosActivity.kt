package com.interview.itaycohen.assignmentyit

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.databinding.DataBindingUtil
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.interview.itaycohen.assignmentyit.databinding.ActivityBrowsePhotosBinding


class BrowsePhotosActivity : AppCompatActivity() {

    lateinit private var binding : ActivityBrowsePhotosBinding
    lateinit private var logic : BrowsePhotosLogic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_browse_photos)
        setSupportActionBar(findViewById(R.id.toolbar))

        logic = BrowsePhotosLogic(this)
        if (validateInternetConnection(this) == true){
            handleIntent(intent)
            initRecycler()
        } else {
            Toast.makeText(this, getString(R.string.noInternetConnectionWarning), Toast.LENGTH_LONG).show()
        }
    }

    private fun initRecycler() {
        binding.recycler.setHasFixedSize(true)
        binding.recycler.layoutManager = GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, true)
        binding.recycler.itemAnimator = DefaultItemAnimator()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu?.findItem(R.id.search)?.actionView as SearchView).also {
            it.setSearchableInfo(searchManager.getSearchableInfo(componentName)) // search value will be passed by intent (with intent android.intent.action.SEARCH)
            it.setOnSuggestionListener(OnSuggestionClickImpl(it))
        }
        return true
    }

    class OnSuggestionClickImpl(searchView: SearchView) : SearchView.OnSuggestionListener {

        open val searchView = searchView

        override fun onSuggestionSelect(position: Int): Boolean {
            //do nothing
            return true
        }

        override fun onSuggestionClick(position: Int): Boolean {
            val selectedView = searchView.suggestionsAdapter
            val cursor = selectedView.getItem(position) as Cursor
            val index = cursor.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_TEXT_1)
            searchView.setQuery(cursor.getString(index), true)
            return true
        }
    }

    override fun onNewIntent(intent: Intent?) {
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (Intent.ACTION_SEARCH == intent?.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                SearchRecentSuggestions(
                        this@BrowsePhotosActivity ,
                        RecentSuggestionsProvider.AUTHORITY,
                        RecentSuggestionsProvider.MODE
                ).saveRecentQuery(query, null)
                logic.sendSearchQuery(query)
            }
        }
    }

    private fun handleSucces(response: String?) {

    }
}