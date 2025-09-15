package edu.bluejack24_1.treasurevault.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import edu.bluejack24_1.treasurevault.R
import edu.bluejack24_1.treasurevault.databinding.ActivityAddTransactionDetailBinding
import edu.bluejack24_1.treasurevault.models.Transaction
import edu.bluejack24_1.treasurevault.models.User
import edu.bluejack24_1.treasurevault.ai.CategoryClassifier
import edu.bluejack24_1.treasurevault.utilities.ToastUtility
import edu.bluejack24_1.treasurevault.viewmodels.AddTransactionDetailViewModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AddTransactionDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTransactionDetailBinding
    private lateinit var viewModel: AddTransactionDetailViewModel
    private val accountIdMap = mutableMapOf<String, String>()

    private val handler = Handler(Looper.getMainLooper())
    private val debounceRunnable = Runnable {
        performCategoryPrediction()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionDetailBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[AddTransactionDetailViewModel::class.java]
        setContentView(binding.root)

        val type = intent.getStringExtra("type")
        if (type != null) {
            setupUI(type)
        }

        binding.backArrow.setOnClickListener {
            finish()
        }

        val calendar = Calendar.getInstance()
        binding.transactionDate.text =
            "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${
                calendar.get(Calendar.YEAR)
            }"
        binding.transactionTime.text = String.format(
            "%02d:%02d",
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        )

        binding.dateLayout.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    binding.transactionDate.text = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        binding.timeLayout.setOnClickListener {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    binding.transactionTime.text =
                        String.format("%02d:%02d", selectedHour, selectedMinute)
                },
                hour, minute, true
            )
            timePickerDialog.show()
        }

        viewModel.fetchUserAccounts(User.userId!!)
        viewModel.accounts.observe(this) { accountList ->
            accountIdMap.clear()
            val accountNames = accountList.map { account ->
                accountIdMap[account.id] = account.name
                account.name
            }

            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, accountNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.accountSpinner.adapter = adapter
        }

        binding.descriptionInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(debounceRunnable)
                handler.postDelayed(debounceRunnable, 500)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        //TODO!! Implement AI here
        val categories = arrayOf("Food", "Allowance", "Transport", "Rent", "Shopping")
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.categoryAutoComplete.setAdapter(categoryAdapter)
        binding.categoryAutoComplete.threshold = 1

        binding.saveButton.setOnClickListener {
            val selectedDate = binding.transactionDate.text.toString()
            val selectedTime = binding.transactionTime.text.toString()

            val formatter = DateTimeFormatter.ofPattern("d/M/yyyy HH:mm")
            val dateTimeString = "$selectedDate $selectedTime"

            val localDateTime = LocalDateTime.parse(dateTimeString, formatter)
            val millis = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            var amount = binding.amountInput.text.toString().toDoubleOrNull()
            val selectedAccountName = binding.accountSpinner.selectedItem.toString()
            val selectedAccountId = accountIdMap.entries.find { it.value == selectedAccountName }?.key
            val selectedCategory = binding.categoryAutoComplete.text.toString()

            if (amount == null) {
                ToastUtility.showToast(this, "Please fill in the amount")
                return@setOnClickListener
            }
            if (selectedAccountId.isNullOrEmpty()) {
                ToastUtility.showToast(this, "Please select an account")
                return@setOnClickListener
            }
            if (selectedCategory.isEmpty()) {
                ToastUtility.showToast(this, "Please fill in the category")
                return@setOnClickListener
            }

            if (type == "expense") {
                amount *= -1
            }

            val transaction = Transaction(
                accountId = selectedAccountId,
                amount = amount,
                description = binding.descriptionInput.text.toString(),
                category = selectedCategory,
                timestamp = millis,
            )
            viewModel.addTransaction(transaction, {
                ToastUtility.showToast(this, "Transaction added successfully")
                finish()
            }, {
                ToastUtility.showToast(this, "Failed to add transaction")
            })
        }
    }

    private fun performCategoryPrediction() {
        val description = binding.descriptionInput.text.toString()
        val predictedCategory = CategoryClassifier.predictCategory(description)
        binding.categoryAutoComplete.setText(predictedCategory, false)
    }

    private fun setupUI(type: String) {
        if (type == "income") {
            binding.textTitle.text = getString(R.string.income)
            binding.saveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
        } else if (type == "expense") {
            binding.textTitle.text = getString(R.string.expense)
            binding.saveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
        }
    }
}