package edu.bluejack24_1.treasurevault.utilities

import android.content.Context
import android.widget.Toast

object ToastUtility {
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}