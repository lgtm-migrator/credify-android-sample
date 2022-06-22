package one.credify.sdk.sample.requester

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import one.credify.sdk.core.model.*
import one.credify.sdk.sample.model.User

class UserRequester {
    fun getUserProfile(
        context: Context,
        url: String,
        onRequest: () -> Unit,
        onResult: (isSuccess: Boolean, user: UserProfile?) -> Unit
    ) {
        onRequest()

        //Log.d("UserRequester", "Url: $url")

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)

        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                val gson = Gson()
                val user = gson.fromJson(response, User::class.java)

                val userProfile = UserProfile(
                    id = user.id,
                    name = UserName(
                        firstName = user.firstName,
                        lastName = user.lastName,
                        middleName = null,
                        fullName = ""
                    ),
                    phone = UserPhoneNumber(
                        phoneNumber = user.phoneNumber,
                        countryCode = user.phoneCountryCode
                    ),
                    email = user.email,
                    dob = null,
                    address = null
                )

                onResult(true, userProfile)
            },
            {
                onResult(false, null)
            }
        )

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun updateUserProfile(
        context: Context,
        url: String,
        firstName: String,
        lastName: String,
        email: String,
        countryCode: String,
        phoneNumber: String,
        onRequest: () -> Unit,
        onResult: (isSuccess: Boolean) -> Unit
    ) {
        onRequest()

        //Log.d("UserRequester", "Url: $url")

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(
            Method.PUT,
            url,
            { _ ->
                onResult(true)
            },
            {
                onResult(false)
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return HashMap<String, String>().apply {
                    put("first_name", firstName)
                    put("last_name", lastName)
                    put("email", email)
                    put("country_code", countryCode)
                    put("phone_number", phoneNumber)
                }
            }
        }

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }
}