package edu.bluejack24_1.treasurevault.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import edu.bluejack24_1.treasurevault.databinding.ActivityRegisterBinding
import edu.bluejack24_1.treasurevault.utilities.AuthenticationUtility
import edu.bluejack24_1.treasurevault.utilities.NavigationUtility
import edu.bluejack24_1.treasurevault.utilities.ToastUtility
import edu.bluejack24_1.treasurevault.viewmodels.RegisterViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (AuthenticationUtility.isLoggedIn(this)) { return }

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.registrationResult.observe(this) { message ->
            if (message.isEmpty()) {
                ToastUtility.showToast(this, "Successfully registered")
                NavigationUtility.navigateTo(this, LoginActivity::class.java)
            } else {
                ToastUtility.showToast(this, message)
            }
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                ToastUtility.showToast(this, "Please fill all fields")
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                ToastUtility.showToast(this, "Password and Confirm Password must be the same")
                return@setOnClickListener
            }

            // Trigger the registration process
            viewModel.register(email, password)
        }

        binding.btnToLogin.setOnClickListener {
            NavigationUtility.navigateTo(this, LoginActivity::class.java)
        }
    }
}