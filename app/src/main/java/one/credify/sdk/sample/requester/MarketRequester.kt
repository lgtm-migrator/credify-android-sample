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
    fun getAccessToken(
        context: Context,
        url: String,
        apiKey: String,
        onRequest: () -> Unit,
        onResult: (accessToken: String?) -> Unit
    ) {
        onRequest()

        Log.d("MarketRequester", "Url: $url")

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)

        // Request a string response from the provided URL.
        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            { response ->
                // Display the first 500 characters of the response string.
                val gson = Gson()
                val accessToken = gson.fromJson(response, AccessTokenResponse::class.java)

                onResult(accessToken.data?.accessToken)
            },
            {
                // Error
                Log.d("MarketRequester", "$it")
                onResult(null)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return HashMap<String, String>().apply {
                    put("X-API-KEY", apiKey)
                }
            }
        }

        queue.add(stringRequest)
    }

    fun pushClaimToken(
        context: Context,
        marketInfo: MarketInfo,
        localId: String,
        credifyId: String,
        onRequest: () -> Unit,
        onResult: (isSuccess: Boolean) -> Unit
    ) {
        getAccessToken(
            context = context,
            url = marketInfo.getAccessTokenUrl,
            apiKey = marketInfo.apiKey,
            onRequest = {
                onRequest()
            },
            onResult = OnResult@ { accessToken ->
                if (!accessToken.isNullOrEmpty()) {
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
                    ) {
                        override fun getHeaders(): MutableMap<String, String> {
                            return HashMap<String, String>().apply {
                                put("Authorization", accessToken)
                            }
                        }
                    }

                    queue.add(stringRequest)

                    return@OnResult
                }

                onResult(false)
            }
        )
    }
}