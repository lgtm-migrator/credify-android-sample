package one.credify.sdk.sample

import one.credify.sdk.core.model.Environment
import java.util.*

object Constants {
    const val MARKET_NAME = "sendo"
    const val API_KEY = "WaXSjOqK0JqSOH1VJ6Op1kkQTdPAhffv5bflck7SzwRjCK0MqUmjSHyvfAan3djf"
    val ENVIRONMENT = Environment.DEVELOP

//    val GET_USER_URL: String
//        get() {
//            return "https://sandbox-demo-api.credify.dev/${MARKET_NAME.toLowerCase(Locale.ENGLISH)}/demo-user"
//        }
//
//    val PUSH_CLAIMS_URL: String
//        get() {
//            return "https://sandbox-demo-api.credify.dev/${MARKET_NAME.toLowerCase(Locale.ENGLISH)}/push-claims"
//        }
//
//    const val GET_ACCESS_TOKEN_BY_API_KEY_URL =  "https://sandbox-api.credify.dev/v1/token"

    val GET_USER_URL: String
        get() {
            return "https://dev-demo-api.credify.ninja/${MARKET_NAME.toLowerCase(Locale.ENGLISH)}/demo-user"
        }

    val PUSH_CLAIMS_URL: String
        get() {
            return "https://dev-demo-api.credify.ninja/${MARKET_NAME.toLowerCase(Locale.ENGLISH)}/push-claims"
        }

    const val GET_ACCESS_TOKEN_BY_API_KEY_URL =  "https://dev-api.credify.ninja/v1/token"
}