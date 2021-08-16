package one.credify.sdk.sample

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley.*
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.android.synthetic.main.activity_offer_example.*
import one.credify.sdk.CredifySDK
import one.credify.sdk.core.CredifyError
import one.credify.sdk.core.callback.OfferListCallback
import one.credify.sdk.core.model.*
import one.credify.sdk.core.request.GetOfferListParam
import org.json.JSONObject

class OfferExampleActivity : BaseActivity() {
    private lateinit var mAdapter: OfferAdapter

    private var mCredifyId: String? = null
    private var mUserProfile: UserProfile? = null

    private var mMarketAccessToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_example)

        mAdapter = OfferAdapter { offer ->
            mUserProfile?.run {
                showOfferDetail(this, offer)
            }
        }

        rvList?.run {
            this.adapter = mAdapter
            this.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
            this.setHasFixedSize(true)
        }

        btnReload?.setOnClickListener {
            getNewUserAndReloadOfferList()
        }

        btnShowReferralResult?.setOnClickListener {
            if (etUserId?.text.isNullOrEmpty())
                return@setOnClickListener

            val user = mUserProfile ?: return@setOnClickListener

            CredifySDK.instance.showReferralResult(
                context = it.context,
                userProfile = user,
                marketName = "HouseCare",
                callback = object : CredifySDK.OnShowReferralResultCallback {
                    override fun onShow() {
                        showToast(this@OfferExampleActivity, "onShow")
                    }

                    override fun onError(ex: Exception) {
                        showToast(this@OfferExampleActivity, "onError: ${ex.message}")
                    }

                    override fun onClose() {
                        showToast(this@OfferExampleActivity, "onClose")
                    }
                }
            )
        }

        showLoading()
        getMarketAccessTokenByApiKey(Constants.API_KEY) { isSuccess ->
            hideLoading()

            if (!isSuccess) {
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (mUserProfile != null) {
            getOfferList(mUserProfile!!, mCredifyId)
        }
    }

    private fun getNewUserAndReloadOfferList() {
        // Instantiate the RequestQueue.
        val queue = newRequestQueue(this)

        // Get new user info
        val stringRequest = StringRequest(
            Request.Method.GET,
            Constants.GET_USER_URL,
            { response ->
                // Display the first 500 characters of the response string.
                val gson = Gson()
                val user = gson.fromJson(response, User::class.java)

                // Create user profile object
                mUserProfile = UserProfile(
                    id = user.id,
                    name = UserName(
                        firstName = user.firstName,
                        lastName = user.lastName,
                        middleName = null,
                        fullName = null
                    ),
                    phone = UserPhoneNumber(
                        phoneNumber = user.phoneNumber,
                        countryCode = user.phoneCountryCode
                    ),
                    email = user.email,
                    dob = null,
                    address = null
                )
                Toast.makeText(this, "UserId: ${user.id}", Toast.LENGTH_SHORT).show()

                // Clear old user info in the SDK
                CredifySDK.instance.clearCache()

                // Get list of offer with new user profile
                getOfferList(mUserProfile!!, mCredifyId)

                etUserId?.setText(user.id)
            },
            {
                // Error
                Log.e("OfferExampleActivity", "${it}")
            }
        )
        queue.add(stringRequest)
    }

    /**
     * Get offer detail list
     */
    private fun getOfferList(user: UserProfile, credifyId: String?) {
        showLoading()

        val params = GetOfferListParam(
            phoneNumber = user.phone.phoneNumber,
            countryCode = user.phone.countryCode,
            localId = user.id,
            credifyId = credifyId
        )

        CredifySDK.instance.getOfferList(
            params = params,
            callback = object : OfferListCallback {
                override fun onSuccess(model: OfferList) {
                    Log.d(
                        "MainActivity",
                        "Offer size : ${model.offerList.size}, credifyId: ${model.credifyId}"
                    )

                    // Cached credify id. If it is null that means the user does not have
                    // credify account yet
                    mCredifyId = model.credifyId

                    // Update the adapter to show offer list on the UI
                    mAdapter.updateList(model.offerList)

                    hideLoading()
                }

                override fun onError(throwable: CredifyError) {
                    Log.d("MainActivity", "Error: ${throwable.throwable}")

                    mAdapter.updateList(emptyList())
                    hideLoading()
                }
            }
        )
    }

    /**
     * Show offer detail page
     */
    private fun showOfferDetail(user: UserProfile, offer: Offer) {
        CredifySDK.instance.showOffer(
            context = this@OfferExampleActivity,
            offer = offer,
            userProfile = user,
            credifyId = mCredifyId,
            marketName = Constants.MARKET_NAME,
            pushClaimCallback = object : CredifySDK.PushClaimCallback {
                override fun onPushClaim(
                    credifyId: String,
                    user: UserProfile,
                    resultCallback: CredifySDK.PushClaimResultCallback
                ) {
                    pushClaim(
                        localId = user.id,
                        credifyId = credifyId,
                        marketAccessToken = mMarketAccessToken ?: ""
                    ) { isSuccess ->
                        resultCallback.onPushClaimResult(isSuccess = isSuccess)
                    }
                }
            },
            offerPageCallback = object : CredifySDK.OfferPageCallback {
                override fun onClose() {
                    Toast.makeText(
                        this@OfferExampleActivity,
                        "Offer page is close",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    /**
     * Get access token by API key. After getting access token successfully, you can using
     * the [pushClaim] method for pushing claims.
     *
     * When you integrate with Credify you will receive a API from Credify
     */
    private fun getMarketAccessTokenByApiKey(
        apiKey: String,
        callback: (isSuccess: Boolean) -> Unit
    ) {
        // Instantiate the RequestQueue.
        val queue = newRequestQueue(this)

        // Request a string response from the provided URL.
        val stringRequest = object : StringRequest(
            Method.POST,
            Constants.GET_ACCESS_TOKEN_BY_API_KEY_URL,
            { response ->
                // Display the first 500 characters of the response string.
                val gson = Gson()
                val accessToken = gson.fromJson(response, AccessTokenResponse::class.java)

                mMarketAccessToken = accessToken.data?.accessToken

                callback(true)
            },
            {
                // Error
                Log.d("OfferExampleActivity", "$it")
                callback(false)
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

    /**
     * This method will call to server for pushing claims
     */
    private fun pushClaim(
        localId: String,
        credifyId: String,
        marketAccessToken: String,
        callback: (isSuccess: Boolean) -> Unit
    ) {
        // Instantiate the RequestQueue.
        val queue = newRequestQueue(this)

        // Request a string response from the provided URL.
        val stringRequest = object : JsonObjectRequest(
            Method.POST,
            Constants.PUSH_CLAIMS_URL,
            JSONObject().apply {
                put("id", localId)
                put("credify_id", credifyId)
            },
            { _ ->
                callback(true)
            },
            {
                // Error
                Log.d("OfferExampleActivity", "${it}")
                callback(false)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return HashMap<String, String>().apply {
                    put("Authorization", marketAccessToken)
                }
            }
        }
        queue.add(stringRequest)
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    }

    class AccessToken {
        @SerializedName("access_token")
        lateinit var accessToken: String
    }

    class AccessTokenResponse : BaseResponse<AccessToken>()

    open class BaseResponse<T> {
        @SerializedName("is_success")
        var isSuccess: Boolean = false

        @SerializedName("data")
        var data: T? = null
    }
}