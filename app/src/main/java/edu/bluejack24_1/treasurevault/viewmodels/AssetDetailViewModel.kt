package edu.bluejack24_1.treasurevault.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack24_1.treasurevault.models.Account
import edu.bluejack24_1.treasurevault.repository.AccountRepository

class AssetDetailViewModel : ViewModel() {
    private val _accounts = MutableLiveData<List<Account>>()
    val accounts: LiveData<List<Account>> get() = _accounts

    fun getAccounts(userId: String) {
        AccountRepository.getAccounts(userId,
            onSuccess = { accountList ->
                _accounts.postValue(accountList)
            },
            onFailure = {
                _accounts.postValue(emptyList())
            })
    }
}
