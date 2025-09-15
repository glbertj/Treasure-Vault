package edu.bluejack24_1.treasurevault.utilities

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocalizationUtility {
    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}