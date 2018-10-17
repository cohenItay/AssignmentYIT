package com.interview.itaycohen.assignmentyit

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest


class BrowsePhotosViewModel constructor(var app : Application) : AndroidViewModel(app) {

    private var browsePhotosLiveData : MutableLiveData<String>? = null
    var errorLiveData : MutableLiveData<VolleyError> = MutableLiveData()

    fun getBrowsePhotosLiveData(query: String, page: String) : MutableLiveData<String>{
        if (browsePhotosLiveData == null) {
            browsePhotosLiveData = MutableLiveData()
        }
        sendSearchQuery(query, page)
        return browsePhotosLiveData!! // no other thread using so it can't be changed back to null
    }

    fun sendSearchQuery(query: String, page:String) {
        val res = app.applicationContext.resources // Note: send activity from UIComponent is forbidden
        val pixabayUrl = res.getString(R.string.pixabay_url, query, res.getString(R.string.pixabay_key), page)
        val request = StringRequest(
                Request.Method.GET,
                pixabayUrl,
                Response.Listener { response ->  browsePhotosLiveData?.postValue(response) },
                Response.ErrorListener { error -> errorLiveData.postValue(error) })
        (app as ItaysApplication).requestsQueue.add(request)
    }
}