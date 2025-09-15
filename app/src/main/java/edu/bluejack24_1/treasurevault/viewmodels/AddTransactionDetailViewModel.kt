package edu.bluejack24_1.treasurevault.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack24_1.treasurevault.models.Account
import edu.bluejack24_1.treasurevault.models.Transaction
import edu.bluejack24_1.treasurevault.repository.AccountRepository
import edu.bluejack24_1.treasurevault.repository.TransactionRepository

class AddTransactionDetailViewModel : ViewModel() {

    private val _accounts = MutableLiveData<List<Account>>()
    val accounts: LiveData<List<Account>> get() = _accounts

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun addTransaction(transaction: Transaction, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        TransactionRepository.saveTransaction(transaction, onSuccess, onFailure)
    }

    fun fetchUserAccounts(userId: String) {
        AccountRepository.getAccounts(userId, { accountList ->
            _accounts.value = accountList
        }, { exception ->
            _error.value = exception.localizedMessage
        })
    }
}


