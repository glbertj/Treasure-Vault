package edu.bluejack24_1.treasurevault.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.bluejack24_1.treasurevault.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _loginResult = MutableLiveData<String>()
    val loginResult: LiveData<String> get() = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = UserRepository.login(email, password)
            _loginResult.value = result
        }
    }
}
