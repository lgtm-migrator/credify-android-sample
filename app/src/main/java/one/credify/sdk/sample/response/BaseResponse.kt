package one.credify.sdk.sample.response

import com.google.gson.annotations.SerializedName

open class BaseResponse<T> {
    @SerializedName("is_success")
    var isSuccess: Boolean = false

    @SerializedName("data")
    var data: T? = null
}