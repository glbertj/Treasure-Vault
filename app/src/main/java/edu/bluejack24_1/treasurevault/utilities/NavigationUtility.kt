package edu.bluejack24_1.treasurevault.utilities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

object NavigationUtility {
    fun navigateTo(context: Context, activityClass: Class<*>, finishCurrent: Boolean = true, dataKey: String? = null, dataValue: String? = null) {
        Intent(context, activityClass).apply {
            if (dataKey != null && dataValue != null) {
                putExtra(dataKey, dataValue)
            }
            context.startActivity(this)
            if (finishCurrent && context is AppCompatActivity) {
                context.finish()
            }
        }
    }
}