package com.interview.itaycohen.assignmentyit

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import org.json.JSONException
import org.json.JSONObject

const val HITS = "hits"
const val ERROR_TAG = "error"
const val WEBFORMAT_URL_JSON_FIELD = "webformatURL"
const val WEBFORMAT_HEIGHT_JSON_FIELD = "webformatHeight"

fun validateInternetConnection(ctx : Context) : Boolean? {
    val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo
    return activeNetwork?.isConnectedOrConnecting
}

fun parseToImageDataList(jsonString : String) : ArrayList<ImageData> {
    val images = ArrayList<ImageData>()
    val mainJson : JSONObject
    try {
        mainJson = JSONObject(jsonString)
        val hitsArr = mainJson.getJSONArray(HITS)
        for (i in 0..(hitsArr.length()-1)) {
            parseImageData(hitsArr.getJSONObject(i))?.let { images.add(it) }
        }
    } catch (exception : JSONException) {
        Log.e(ERROR_TAG, "parseToImageDataList: \n"+exception.message+exception.printStackTrace())
    }
    return images
}

private fun parseImageData(jsonObj: JSONObject): ImageData? {
    val imageData = ImageData(
            jsonObj.getString(WEBFORMAT_URL_JSON_FIELD),
            jsonObj.getInt(WEBFORMAT_HEIGHT_JSON_FIELD)
    )
    return if (imageData.url != null) imageData else null
}
