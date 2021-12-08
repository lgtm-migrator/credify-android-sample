package one.credify.sdk.sample.model

import com.google.gson.annotations.SerializedName
import one.credify.sdk.core.model.Environment
import java.io.Serializable

class MarketInfo(
    @SerializedName("name")
    val name: String,
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("path")
    val path: String,
    @SerializedName("environment")
    val environment: Environment
) : Serializable {
    private val getEnvironmentOnSubDomainName: String
        get() {
            return when (environment) {
                Environment.DEVELOP -> "dev"
                Environment.SIT -> "sit"
                Environment.SANDBOX -> "sandbox"
                Environment.UAT -> "uat"
                else -> ""
            }
        }

    private val getTopDomainName: String
        get() {
            return when (environment) {
                Environment.DEVELOP, Environment.SIT -> "ninja"
                Environment.UAT, Environment.SANDBOX -> "dev"
                else -> ""
            }
        }

    val getRandomUserUrl: String
        get() {
            return "https://${getEnvironmentOnSubDomainName}-demo-api.credify.${getTopDomainName}/${path}/demo-user"
        }

    val getPushClaimTokenUrl: String
        get() {
            return "https://${getEnvironmentOnSubDomainName}-demo-api.credify.${getTopDomainName}/${path}/push-claims"
        }

    val getAccessTokenUrl: String
        get() {
            return "https://${getEnvironmentOnSubDomainName}-api.credify.${getTopDomainName}/v1/token"
        }

    fun getUserByIdUrl(id: String): String {
        return "https://${getEnvironmentOnSubDomainName}-demo-api.credify.${getTopDomainName}/${path}/demo-user?id=${id}"
    }

    fun getUpdateUserByIdUrl(id: String): String {
        return "https://${getEnvironmentOnSubDomainName}-demo-api.credify.${getTopDomainName}/${path}/demo-user/${id}"
    }
}