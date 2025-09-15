package edu.bluejack24_1.treasurevault.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.bluejack24_1.treasurevault.models.Account
import edu.bluejack24_1.treasurevault.models.Transaction
import edu.bluejack24_1.treasurevault.models.User
import java.util.Calendar

object TransactionRepository {
    private val databaseReference = FirebaseDatabase.getInstance().getReference("users")

    fun saveTransaction(
        transaction: Transaction,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = User.userId ?: return
        val transactionRef = databaseReference.child(userId).child("transactions")
        val accountRef = databaseReference.child(userId).child("accounts").child(transaction.accountId)

        val newTransactionRef = transactionRef.push()
        val transactionId = newTransactionRef.key ?: return

        val updatedTransaction = transaction.copy(id = transactionId)

        newTransactionRef.setValue(updatedTransaction)
            .addOnSuccessListener {
                accountRef.get().addOnSuccessListener { snapshot ->
                    val account = snapshot.getValue(Account::class.java)
                    if (account != null) {
                        val updatedBalance = account.balance + transaction.amount
                        val updatedAccount = account.copy(balance = updatedBalance)

                        accountRef.setValue(updatedAccount)
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener {
                                onFailure(it)
                            }
                    } else {
                        onFailure(Exception("Account not found"))
                    }
                }.addOnFailureListener {
                    onFailure(it)
                }
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun fetchTransactionsForMonth(
        userId: String, year: Int, month: Int,
        onSuccess: (List<Transaction>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val startOfMonth = Calendar.getInstance().apply {
            set(year, month, 1, 0, 0, 0)
        }.timeInMillis

        val endOfMonth = Calendar.getInstance().apply {
            set(year, month, getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
        }.timeInMillis

        val query = databaseReference.child(userId).child("transactions")
            .orderByChild("timestamp")
            .startAt(startOfMonth.toDouble())
            .endAt(endOfMonth.toDouble())

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val transactions = dataSnapshot.children.mapNotNull { it.getValue(Transaction::class.java) }
                onSuccess(transactions)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onFailure(databaseError.toException())
            }
        })
    }
}