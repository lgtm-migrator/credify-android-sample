package one.credify.sdk.sample

import android.app.Application
import android.content.Context
import androidx.core.content.ContextCompat
import one.credify.sdk.CredifySDK
import one.credify.sdk.ServiceXThemeConfig
import one.credify.sdk.ThemeColor
import one.credify.sdk.ThemeFont

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        CredifySDK.Builder()
            .withApiKey(Constants.API_KEY)
            .withContext(this)
            .withEnvironment(Constants.ENVIRONMENT)
            .withTheme(
                theme = ServiceXThemeConfig(
                    color = ThemeColor(
                        primaryBrandyStart = "#1382F8",
                        primaryBrandyEnd = "#1382F8",
                        topBarTextColor = "#FFFFFF",
                        primaryButtonBrandyStart = "#02D15D",
                        primaryButtonBrandyEnd = "#01B779",
                        primaryButtonTextColor = "#FFFFFF",
                        secondaryActive = "#1483F7",
                        secondaryDisable = "#E0E0E0",
                        secondaryComponentBackground = "#DFF3FE",
                        secondaryBackground = "#DFF3FE",
                    ),
                    font = ThemeFont(
                        primaryFontFamily = "Roboto",
                        secondaryFontFamily = "Quicksand",
                    ),
                    inputFieldRadius = 5F,
                    buttonRadius = 5F,
                    modelRadius = 10F,
                )
            )
            .build()
    }

    private fun getColor(context: Context, id: Int): Int {
        return ContextCompat.getColor(context, id)
    }
}