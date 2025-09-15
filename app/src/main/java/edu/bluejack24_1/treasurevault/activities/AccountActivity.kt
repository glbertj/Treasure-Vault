package edu.bluejack24_1.treasurevault.activities

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import edu.bluejack24_1.treasurevault.R
import edu.bluejack24_1.treasurevault.databinding.ActivityAccountBinding
import edu.bluejack24_1.treasurevault.databinding.PopupProfileImageChangeBinding
import edu.bluejack24_1.treasurevault.models.Setting
import edu.bluejack24_1.treasurevault.models.User
import edu.bluejack24_1.treasurevault.utilities.AuthenticationUtility
import edu.bluejack24_1.treasurevault.utilities.NavigationUtility
import edu.bluejack24_1.treasurevault.viewmodels.AccountViewModel

class AccountActivity : AppCompatActivity() {
    private lateinit var accountBinding: ActivityAccountBinding
    private lateinit var viewModel: AccountViewModel

    private lateinit var popUpBinding: PopupProfileImageChangeBinding
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AuthenticationUtility.isNotAuthenticated(this)

        accountBinding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(accountBinding.root)

        accountBinding.profileEmail.text = User.email ?: "not logged in (?)"
        accountBinding.themeToggle.isChecked = Setting.useDarkMode
//        accountBinding.languageToggle.isChecked = Setting.useIndonesian

        viewModel = ViewModelProvider(this)[AccountViewModel::class.java]

        User.profilePictureUrl?.let { profileUrl ->
            Glide.with(this)
                .load(profileUrl)
                .placeholder(R.drawable.ic_person)
                .into(accountBinding.profileImage)
        }

        accountBinding.backArrow.setOnClickListener {
            finish()
        }

        accountBinding.editImage.setOnClickListener {
            viewModel.onEditImageClicked()
        }

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data
                viewModel.onImagePicked(imageUri)
            }
        }

        accountBinding.themeToggle.setOnCheckedChangeListener { _, isChecked ->
            Setting.useDarkMode = isChecked
        }

        accountBinding.languageToggle.setOnCheckedChangeListener { _, isChecked ->
            Setting.useIndonesian = isChecked
            Setting.applyLanguage(this)

            restartActivity()
        }

        accountBinding.privacySecurityArrow.setOnClickListener {
            NavigationUtility.navigateTo(this, PrivacySecurityActivity::class.java, false)
        }

        accountBinding.logoutButton.setOnClickListener {
            viewModel.logout(this)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.imagePickerTrigger.observe(this) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        viewModel.showImageDialogTrigger.observe(this) {
            showPopupProfileImageChange()
        }

        viewModel.selectedImageUri.observe(this) { uri ->
            if (::popUpBinding.isInitialized) {
                popUpBinding.uploadedImageView.setImageURI(uri)
                popUpBinding.uploadImageText.visibility = View.GONE
            }
        }

        viewModel.dismissDialogTrigger.observe(this) {
            if (::popUpBinding.isInitialized) {
                popUpBinding.cancelButton.performClick()
            }
        }
    }

    private fun showPopupProfileImageChange() {
        popUpBinding = PopupProfileImageChangeBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(this)
        builder.setView(popUpBinding.root)
        val dialog = builder.create()

        popUpBinding.uploadedImageView.setOnClickListener {
            viewModel.onImagePickerClicked()
        }

        popUpBinding.confirmButton.setOnClickListener {
            viewModel.onSaveImageClicked()
        }

        popUpBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun restartActivity() {
        val intent = intent
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        startActivity(intent)
    }
}
