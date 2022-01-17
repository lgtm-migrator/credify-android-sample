package one.credify.sdk.sample

import android.app.Application
import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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
                    context = this,
                    color = ThemeColor(
                        primaryBrandyStart = getColor(this, R.color.color_primary_brandy_start),
                        primaryBrandyEnd = getColor(this, R.color.color_primary_brandy_end),
                        topBarTextColor = Color.WHITE,
                        primaryButtonBrandyStart = getColor(
                            this,
                            R.color.color_button_brandy_start
                        ),
                        primaryButtonBrandyEnd = getColor(this, R.color.color_button_brandy_end),
                        primaryButtonTextColor = Color.WHITE,
                        secondaryActive = getColor(this, R.color.color_secondary_active),
                        secondaryDisable = getColor(this, R.color.color_secondary_disable),
                        secondaryComponentBackground = getColor(
                            this,
                            R.color.color_secondary_component_background
                        ),
                        secondaryBackground = getColor(this, R.color.color_secondary_background),
                    ),
                    font = ThemeFont(
                        context = this,
                        primaryFontFamily = ResourcesCompat.getFont(
                            this,
                            R.font.font_roboto_regular
                        ),
                        secondaryFontFamily = ResourcesCompat.getFont(
                            this,
                            R.font.font_quicksand_regular
                        ),
                    ),
                    actionBarTopLeftRadius = 0F,
                    actionBarBottomLeftRadius = 0F,
                    actionBarTopRightRadius = 0F,
                    actionBarBottomRightRadius = 0F,
                    datePickerStyle = R.style.CustomDatePickerStyle,
                    elevation = 4F,
                    inputFieldRadius = 5F,
                    buttonRadius = 5F,
                    modelRadius = 10,
                )
            )
            .build()
    }

    private fun getColor(context: Context, id: Int): Int {
        return ContextCompat.getColor(context, id)
    }
}