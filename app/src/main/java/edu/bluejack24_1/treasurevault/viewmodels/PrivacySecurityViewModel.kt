package edu.bluejack24_1.treasurevault.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.bluejack24_1.treasurevault.repository.UserRepository
import kotlinx.coroutines.launch

class PrivacySecurityViewModel : ViewModel() {
    private val _changePasswordStatus = MutableLiveData<String>()
    val changePasswordStatus: LiveData<String> get() = _changePasswordStatus

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            val result = UserRepository.changePassword(currentPassword, newPassword)
            _changePasswordStatus.value = result
        }
    }
}