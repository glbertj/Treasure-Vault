package edu.bluejack24_1.treasurevault.models

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import edu.bluejack24_1.treasurevault.utilities.LocalizationUtility

object Setting {
    private const val PREFERENCES_NAME = "SettingPrefs"
    private const val USE_PIN = "usePin"
    private const val USE_BIOMETRIC = "useBiometric"
    private const val USE_DARK_MODE = "useDarkMode"
    private const val USE_INDONESIAN = "useIndonesian"

    private lateinit var preferences: SharedPreferences

    var usePin: Boolean
        get() = preferences.getBoolean(USE_PIN, true)
        set(value) = preferences.edit().putBoolean(USE_PIN, value).apply()

    var useBiometric: Boolean
        get() = preferences.getBoolean(USE_BIOMETRIC, true)
        set(value) = preferences.edit().putBoolean(USE_BIOMETRIC, value).apply()

    var useDarkMode: Boolean
        get() = preferences.getBoolean(USE_DARK_MODE, false)
        set(value) {
            preferences.edit().putBoolean(USE_DARK_MODE, value).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (value) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

    var useIndonesian: Boolean
        get() = preferences.getBoolean(USE_INDONESIAN, false)
        set(value) = preferences.edit().putBoolean(USE_INDONESIAN, value).apply()

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        AppCompatDelegate.setDefaultNightMode(
            if (useDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        applyLanguage(context)
    }

    fun applyLanguage(context: Context) {
        val languageCode = if (useIndonesian) "id" else "en"
        LocalizationUtility.setLocale(context, languageCode)
    }
}
