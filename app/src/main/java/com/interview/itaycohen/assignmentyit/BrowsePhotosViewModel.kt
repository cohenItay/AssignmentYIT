package com.interview.itaycohen.assignmentyit

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion.User
import android.arch.lifecycle.LiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion.User





class BrowsePhotosViewModel(app : Application) : AndroidViewModel(app) {

    private var browsePhotosLiveData : MutableLiveData<String>? = null
    private var queue : RequestQueue? = null

    fun getBrowsePhotosLiveData(savedInstanceState : Bundle?) {
        if (browsePhotosLiveData == null) {
            browsePhotosLiveData = MutableLiveData()
            if (savedInstanceState == null) {

            }
        }

    }

    fun sendSearchQuery(query: String) :  {
        if (queue = null)
        var queue = Volley.newRequestQueue(ctx)
        var pixabayUrl = res.getString(R.string.pixabay_url, query, res.getString(R.string.pixabay_key))
        var request = StringRequest(
                Request.Method.GET,
                pixabayUrl,
                Response.Listener { response -> handleSucces(response) },
                Response.ErrorListener { Toast.makeText(this, getString(R.string.error_connecting_server), Toast.LENGTH_LONG).show() })
    }
}