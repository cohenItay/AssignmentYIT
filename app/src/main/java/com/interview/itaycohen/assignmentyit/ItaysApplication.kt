package com.interview.itaycohen.assignmentyit

import android.app.Application
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class ItaysApplication : Application() {

    private var _requestQueue : RequestQueue? = null
    val requestsQueue : RequestQueue
        get() {
            if (_requestQueue == null)
                _requestQueue = Volley.newRequestQueue(this)
            return _requestQueue!! // no need to check if other thread sets it tu null..
        }
}