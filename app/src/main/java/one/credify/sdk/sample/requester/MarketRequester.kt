package one.credify.sdk.sample.requester

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import one.credify.sdk.sample.model.MarketInfo
import one.credify.sdk.sample.response.AccessTokenResponse
import org.json.JSONObject

class MarketRequester {
    fun pushClaimToken(
        context: Context,
        marketInfo: MarketInfo,
        localId: String,
        credifyId: String,
        onRequest: () -> Unit,
        onResult: (isSuccess: Boolean) -> Unit
    ) {
        onRequest()

        val url = marketInfo.getPushClaimTokenUrl

        Log.d("UserRequester", "Url: $url")

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)

        // Request a string response from the provided URL.
        val stringRequest = object : JsonObjectRequest(
            Method.POST,
            url,
            JSONObject().apply {
                put("id", localId)
                put("credify_id", credifyId)
            },
            { _ ->
                onResult(true)
            },
            {
                // Error
                Log.d("MarketRequester", "pushClaimToken -> Error: $it")
                onResult(false)
            }
        ) {}

        queue.add(stringRequest)
    }
}