package edu.bluejack24_1.treasurevault.utilities

import android.content.Context
import edu.bluejack24_1.treasurevault.activities.LoginActivity
import edu.bluejack24_1.treasurevault.activities.MainActivity
import edu.bluejack24_1.treasurevault.composes.EnterPinActivity
import edu.bluejack24_1.treasurevault.models.Setting
import edu.bluejack24_1.treasurevault.models.User

object AuthenticationUtility {

    fun isNotAuthenticated(context: Context): Boolean {
        if (!User.isAuthenticated) {
            NavigationUtility.navigateTo(context, LoginActivity::class.java)
            return true
        }
        return false
    }

    fun isLoggedIn(context: Context): Boolean {
        if (User.userId == null) {
            return false
        }

        if (Setting.usePin && !User.isAuthenticated) {
            NavigationUtility.navigateTo(context, EnterPinActivity::class.java)
        } else {
            User.isAuthenticated = true
            NavigationUtility.navigateTo(context, MainActivity::class.java)
        }
        return true
    }
}