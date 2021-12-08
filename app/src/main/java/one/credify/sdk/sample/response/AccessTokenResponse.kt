package one.credify.sdk.sample.response

import com.google.gson.annotations.SerializedName

class AccessTokenResponse : BaseResponse<AccessToken>()

class AccessToken {
    @SerializedName("access_token")
    lateinit var accessToken: String
}