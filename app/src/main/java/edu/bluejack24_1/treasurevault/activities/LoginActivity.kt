package edu.bluejack24_1.treasurevault.activities

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import edu.bluejack24_1.treasurevault.composes.SetPinActivity
import edu.bluejack24_1.treasurevault.databinding.ActivityLoginBinding
import edu.bluejack24_1.treasurevault.models.User
import edu.bluejack24_1.treasurevault.utilities.AuthenticationUtility
import edu.bluejack24_1.treasurevault.utilities.NavigationUtility
import edu.bluejack24_1.treasurevault.utilities.ToastUtility
import edu.bluejack24_1.treasurevault.viewmodels.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (AuthenticationUtility.isLoggedIn(this)) { return }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.loginResult.observe(this) { message ->
            if (message.isEmpty()) {
                saveUserToPrefs()
                NavigationUtility.navigateTo(this, SetPinActivity::class.java)
            } else {
                ToastUtility.showToast(this, message)
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isBlank() || password.isBlank()) {
                ToastUtility.showToast(this, "All fields must be filled")
                return@setOnClickListener
            }

            viewModel.login(email, password)
        }

        binding.btnToRegister.setOnClickListener {
            NavigationUtility.navigateTo(this, RegisterActivity::class.java)
        }
    }

    private fun saveUserToPrefs() {
        getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit().apply {
            putString("userId", User.userId)
            putString("userEmail", User.email)
            putString("userProfilePictureUrl", User.profilePictureUrl)
        }.apply()

        User.init(this)
    }
}
