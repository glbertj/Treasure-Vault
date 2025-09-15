package edu.bluejack24_1.treasurevault.models

import android.content.Context

object User {
    var isAuthenticated: Boolean = false
    var userId: String? = null
    var email: String? = null
    var profilePictureUrl: String? = null

    fun init(context: Context) {
        val userSharedPrefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userId = userSharedPrefs.getString("userId", null)
        email = userSharedPrefs.getString("userEmail", null)
        profilePictureUrl = userSharedPrefs.getString("userProfilePictureUrl", null)
    }

    fun clearSession() {
        isAuthenticated = false
        userId = null
        email = null
        profilePictureUrl = null
    }
}
