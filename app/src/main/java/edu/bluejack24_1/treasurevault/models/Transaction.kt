package edu.bluejack24_1.treasurevault.models

data class Transaction(
    val id: String = "",
    val accountId: String = "",
    val amount: Double = 0.0,
    val description: String = "",
    val category: String = "",
    val timestamp: Long = 0L
)