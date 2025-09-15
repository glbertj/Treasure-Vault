package edu.bluejack24_1.treasurevault.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.bluejack24_1.treasurevault.models.Account
import edu.bluejack24_1.treasurevault.models.User

object AccountRepository {
    private val databaseReference = FirebaseDatabase.getInstance().getReference("users")

    fun saveAccount(
        userId: String,
        account: Account,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userAccountRef = databaseReference.child(userId).child("accounts").push()
        userAccountRef.setValue(account)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getAccounts(
        userId: String,
        onSuccess: (List<Account>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val accountsRef = databaseReference.child(userId).child("accounts")

        accountsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val accounts = dataSnapshot.children.mapNotNull { snapshot ->
                    snapshot.getValue(Account::class.java)
                }
                onSuccess(accounts)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onFailure(databaseError.toException())
            }
        })
    }

    fun getAccountName(
        accountId: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val accountsRef = databaseReference.child(User.userId!!).child("accounts").child(accountId)

        accountsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val accountName = dataSnapshot.child("name").getValue(String::class.java)
                if (accountName != null) {
                    onSuccess(accountName)
                } else {
                    onFailure(Exception("Account name not found"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onFailure(databaseError.toException())
            }
        })
    }
}
