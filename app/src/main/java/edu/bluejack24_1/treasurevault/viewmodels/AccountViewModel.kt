package edu.bluejack24_1.treasurevault.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.bluejack24_1.treasurevault.activities.LoginActivity
import edu.bluejack24_1.treasurevault.models.User
import edu.bluejack24_1.treasurevault.repository.UserRepository
import edu.bluejack24_1.treasurevault.utilities.CacheUtility
import edu.bluejack24_1.treasurevault.utilities.NavigationUtility
import kotlinx.coroutines.launch

class AccountViewModel : ViewModel() {
    val selectedImageUri = MutableLiveData<Uri?>()

    private val _imagePickerTrigger = MutableLiveData<Unit>()
    val imagePickerTrigger: LiveData<Unit> get() = _imagePickerTrigger

    private val _showImageDialogTrigger = MutableLiveData<Unit>()
    val showImageDialogTrigger: LiveData<Unit> get() = _showImageDialogTrigger

    private val _dismissDialogTrigger = MutableLiveData<Unit>()
    val dismissDialogTrigger: LiveData<Unit> get() = _dismissDialogTrigger

    fun onEditImageClicked() {
        _showImageDialogTrigger.value = Unit
    }

    fun onImagePicked(imageUri: Uri?) {
        selectedImageUri.value = imageUri
    }

    fun onImagePickerClicked() {
        _imagePickerTrigger.value = Unit
    }

    fun onSaveImageClicked() {
        val imageUri = selectedImageUri.value
        if (imageUri != null) {
            uploadProfilePicture(imageUri, onSuccess = {
                _dismissDialogTrigger.value = Unit
            }, onFailure = {
            })
        }
    }

    private fun uploadProfilePicture(imageUri: Uri, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = User.userId ?: return
        UserRepository.uploadProfilePicture(userId, imageUri, { profilePictureUrl ->
            User.profilePictureUrl = profilePictureUrl
            updateUserProfile(userId, profilePictureUrl, onSuccess, onFailure)
        }, onFailure)
    }

    private fun updateUserProfile(userId: String, profilePictureUrl: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        UserRepository.updateUserProfile(userId, profilePictureUrl, onSuccess, onFailure)
    }

    fun logout(context: Context) {
        viewModelScope.launch {
            UserRepository.logout()
            clearSharedPreferences(context)
            CacheUtility.flushCache()
            NavigationUtility.navigateTo(context, LoginActivity::class.java, true)
        }
    }

    private fun clearSharedPreferences(context: Context) {
        val userPrefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userPrefsEditor = userPrefs.edit()
        userPrefsEditor.clear()
        userPrefsEditor.apply()

        val pinPrefs = context.getSharedPreferences("PinPrefs", Context.MODE_PRIVATE)
        val pinPrefsEditor = pinPrefs.edit()
        pinPrefsEditor.clear()
        pinPrefsEditor.apply()

        val settingPrefs = context.getSharedPreferences("SettingPrefs", Context.MODE_PRIVATE)
        val settingPrefsEditor = settingPrefs.edit()
        settingPrefsEditor.clear()
        settingPrefsEditor.apply()
    }
}
