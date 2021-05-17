package one.credify.sdk.sample

import android.app.Application
import one.credify.sdk.CredifySDK
import one.credify.sdk.core.model.Environment
import java.util.*

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        CredifySDK.Builder()
            .withApiKey(Constants.API_KEY)
            .withContext(this)
            .withEnvironment(Constants.ENVIRONMENT)
            .build()
    }
}