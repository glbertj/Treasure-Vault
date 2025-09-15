package edu.bluejack24_1.treasurevault.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.bluejack24_1.treasurevault.models.Asset

object AssetRepository {
    private val databaseReference = FirebaseDatabase.getInstance().getReference("users")

    fun saveAsset(
        userId: String,
        asset: Asset,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val assetsRef =
            databaseReference.child(userId).child("assets").child(asset.timestamp.toString())
        assetsRef.setValue(asset)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun fetchAssetsInRange(
        userId: String,
        startDate: Long,
        endDate: Long,
        onSuccess: (List<Asset>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val assetsRef = databaseReference.child(userId).child("assets")

        assetsRef.orderByChild("timestamp").startAt(startDate.toDouble()).endAt(endDate.toDouble())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val assets = snapshot.children.mapNotNull { it.getValue(Asset::class.java) }
                    onSuccess(assets)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println(databaseError.toException())
                    onFailure(databaseError.toException())
                }
            })
    }
}