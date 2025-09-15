package edu.bluejack24_1.treasurevault.models

sealed class RegisterResult {
    data class Success(val userId: String) : RegisterResult()
    data class Failure(val errorMessage: String) : RegisterResult()
}