package edu.bluejack24_1.treasurevault.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack24_1.treasurevault.models.Transaction
import edu.bluejack24_1.treasurevault.repository.TransactionRepository

class TransactionViewModel : ViewModel() {
    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchTransactions(userId: String, year: Int, month: Int) {
        TransactionRepository.fetchTransactionsForMonth(userId, year, month, { transactions ->
            _transactions.value = transactions
        }, { exception ->
            _error.value = exception.localizedMessage
        })
    }
}