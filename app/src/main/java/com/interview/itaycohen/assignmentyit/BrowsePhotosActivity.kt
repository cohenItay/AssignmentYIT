package com.interview.itaycohen.assignmentyit

import android.app.SearchManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.widget.Toast
import com.android.volley.VolleyError
import com.interview.itaycohen.assignmentyit.databinding.ActivityBrowsePhotosBinding
import kotlinx.android.synthetic.main.activity_browse_photos.*


class BrowsePhotosActivity : AppCompatActivity() {

    lateinit private var binding : ActivityBrowsePhotosBinding
    lateinit var viewModel : BrowsePhotosViewModel
    private var pageCounter = 1

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu?.findItem(R.id.search)?.actionView as SearchView).let {
            it.setSearchableInfo(searchManager.getSearchableInfo(componentName)) // get the search value and share it by intent (with intent android.intent.action.SEARCH)
            val searchChangeListener = OnSearchValueChosenImpl(it);
            it.setOnSuggestionListener(searchChangeListener)
            it.setOnQueryTextListener(searchChangeListener)
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //
        binding = DataBindingUtil.setContentView(this, R.layout.activity_browse_photos)
        setSupportActionBar(findViewById(R.id.toolbar))

        if (validateInternetConnection(this) != true){
            Toast.makeText(this, getString(R.string.noInternetConnectionWarning), Toast.LENGTH_LONG).show()
            return
        }

        viewModel = ViewModelProviders.of(this).get(BrowsePhotosViewModel::class.java)
        viewModel.errorLiveData.observe(this, getErrorObserverImpl())
        handleIntent(intent)
        initRecycler()
    }

    private fun getErrorObserverImpl(): Observer<VolleyError> {
        return Observer {
            Toast.makeText(this, getString(R.string.error_connecting_server), Toast.LENGTH_LONG).show()
            if (recycler.adapter != null)
                (recycler.adapter as PhotosAdapter).shouldShowProgressBar = false
        }
    }

    inner class OnSearchValueChosenImpl(var searchView: SearchView) : SearchView.OnSuggestionListener, SearchView.OnQueryTextListener {
        override fun onQueryTextChange(newText: String?): Boolean {
            return false
        }

        override fun onQueryTextSubmit(query: String?): Boolean {
            // called when suggestion or ime done key clicked
            pageCounter = 1
            return false
        }

        override fun onSuggestionSelect(position: Int): Boolean {
            //do nothing
            return true
        }

        override fun onSuggestionClick(position: Int): Boolean {
            val cursorAdapter = searchView.suggestionsAdapter
            val cursor = cursorAdapter.getItem(position) as Cursor
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
                        this ,
                        RecentSuggestionsProvider.AUTHORITY,
                        RecentSuggestionsProvider.MODE
                ).saveRecentQuery(query, null)
                val liveData = viewModel.getBrowsePhotosLiveData(query, pageCounter.toString())
                if (!liveData.hasObservers()) {
                    liveData.observe(this, Observer { handleSuccess(it) })
                }
            }
        }
    }

    private fun handleSuccess(jsonResponseString: String?) {
        if (jsonResponseString == null)
            return

        val images = parseToImageDataList(jsonResponseString)
        if (recycler.adapter == null){
            recycler.adapter =  PhotosAdapter(images)
        } else {
            val adapter = recycler.adapter as PhotosAdapter
            if (pageCounter == 1)
                adapter.images =  images
            else
                adapter.images.addAll(images)
            recycler.adapter.notifyDataSetChanged()
        }
    }

    private fun initRecycler() {
        binding.recycler.setHasFixedSize(true)
        val gridLayoutManager = GridLayoutManager(this, 3)
        gridLayoutManager.spanSizeLookup = getSpanSizeLookup()
        binding.recycler.layoutManager = gridLayoutManager
        binding.recycler.addOnScrollListener(getOnScrollListenerImpl(gridLayoutManager))
        //should have used custom ItemDecoration divider for gridlayout, but i presume its not the case in this task, so i used padding
    }

    private fun getSpanSizeLookup(): GridLayoutManager.SpanSizeLookup {
        return object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val adapter = binding.recycler.adapter
                adapter?.let {
                    it as PhotosAdapter
                    if (it.images.size == position) // its the footer (last cell)
                        return 3 // for progressbar
                }
                return 1
            }
        }
    }

    private fun getOnScrollListenerImpl(gridLayoutManager: GridLayoutManager): RecyclerView.OnScrollListener {
        return object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                val mostBottomViewedPosition = gridLayoutManager.findLastCompletelyVisibleItemPosition()
                val lastPosition = gridLayoutManager.itemCount - 1
                if (lastPosition == mostBottomViewedPosition) {
                    pageCounter++
                    handleIntent(intent)
                }
            }
        }
    }
}