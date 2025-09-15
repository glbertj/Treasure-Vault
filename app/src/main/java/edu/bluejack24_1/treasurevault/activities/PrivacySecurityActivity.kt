package edu.bluejack24_1.treasurevault.activities

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import edu.bluejack24_1.treasurevault.composes.SetPinActivity
import edu.bluejack24_1.treasurevault.databinding.ActivityPrivacySecurityBinding
import edu.bluejack24_1.treasurevault.databinding.PopupPasswordChangeBinding
import edu.bluejack24_1.treasurevault.models.Setting
import edu.bluejack24_1.treasurevault.models.User
import edu.bluejack24_1.treasurevault.utilities.AuthenticationUtility
import edu.bluejack24_1.treasurevault.utilities.NavigationUtility
import edu.bluejack24_1.treasurevault.utilities.ToastUtility
import edu.bluejack24_1.treasurevault.viewmodels.PrivacySecurityViewModel

class PrivacySecurityActivity : AppCompatActivity() {
    private lateinit var privacySecurityBinding: ActivityPrivacySecurityBinding
    private lateinit var popupPasswordChangeBinding: PopupPasswordChangeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AuthenticationUtility.isNotAuthenticated(this)

        privacySecurityBinding = ActivityPrivacySecurityBinding.inflate(layoutInflater)
        setContentView(privacySecurityBinding.root)

        privacySecurityBinding.emailValue.text = User.email ?: "tolol"
        privacySecurityBinding.usePinSwitch.isChecked = Setting.usePin
        privacySecurityBinding.useBiometricSwitch.isChecked = Setting.useBiometric

        privacySecurityBinding.backArrow.setOnClickListener {
            finish()
        }

        privacySecurityBinding.changePassword.setOnClickListener {
            showPopupPasswordChange()
        }

        privacySecurityBinding.usePinSwitch.setOnCheckedChangeListener { _, isChecked ->
            Setting.usePin = isChecked
        }

        privacySecurityBinding.changePin.setOnClickListener {
            NavigationUtility.navigateTo(this, SetPinActivity::class.java)
        }

        privacySecurityBinding.useBiometricSwitch.setOnCheckedChangeListener { _, isChecked ->
            Setting.useBiometric = isChecked
        }
    }

    private fun showPopupPasswordChange() {
        popupPasswordChangeBinding = PopupPasswordChangeBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(this)
        builder.setView(popupPasswordChangeBinding.root)
        val dialog = builder.create()

        val viewModel = ViewModelProvider(this)[PrivacySecurityViewModel::class.java]

        popupPasswordChangeBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        popupPasswordChangeBinding.confirmButton.setOnClickListener {
            val currPassword = popupPasswordChangeBinding.currentPassword.text.toString()
            val newPassword = popupPasswordChangeBinding.newPassword.text.toString()
            val confPassword = popupPasswordChangeBinding.confirmPassword.text.toString()

            if (currPassword.isEmpty() || newPassword.isEmpty() || confPassword.isEmpty()) {
                ToastUtility.showToast(this, "Please fill all fields")
                return@setOnClickListener
            }
            if (newPassword != confPassword) {
                ToastUtility.showToast(this, "New password and confirm password must be the same")
                return@setOnClickListener
            }

            viewModel.changePassword(currPassword, newPassword)
        }

        viewModel.changePasswordStatus.observe(this) { status ->
            if (status.isEmpty()) {
                ToastUtility.showToast(this, "Password changed successfully")
                dialog.dismiss()
            } else {
                ToastUtility.showToast(this, status)
            }
        }

        dialog.show()
    }
}