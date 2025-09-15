package edu.bluejack24_1.treasurevault.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.bluejack24_1.treasurevault.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val _registrationResult = MutableLiveData<String>()
    val registrationResult: LiveData<String> get() = _registrationResult

    fun register(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val message = try {
                UserRepository.register(email, password)
            } catch (e: Exception) {
                "An error occurred: ${e.localizedMessage}"
            }
            _registrationResult.postValue(message)
        }
    }
}
