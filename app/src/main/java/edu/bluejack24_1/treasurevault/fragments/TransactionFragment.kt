package edu.bluejack24_1.treasurevault.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.tabs.TabLayout
import edu.bluejack24_1.treasurevault.R
import edu.bluejack24_1.treasurevault.adapters.TransactionAdapter
import edu.bluejack24_1.treasurevault.databinding.FragmentTransactionBinding
import edu.bluejack24_1.treasurevault.databinding.ItemTabLayoutBinding
import edu.bluejack24_1.treasurevault.models.Transaction
import edu.bluejack24_1.treasurevault.models.User
import edu.bluejack24_1.treasurevault.repository.AccountRepository
import edu.bluejack24_1.treasurevault.utilities.CacheUtility
import edu.bluejack24_1.treasurevault.viewmodels.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.absoluteValue

class TransactionFragment : Fragment() {
    private lateinit var binding: FragmentTransactionBinding
    private lateinit var viewModel: TransactionViewModel
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: TransactionAdapter

    private var currentYear: Int = Calendar.getInstance().get(Calendar.YEAR)
    private var currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH)

    private var isIncomeTab: Boolean = true
    private lateinit var transactions: List<Transaction>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = User.userId ?: return

        tabLayout = binding.tabLayout
        setupTab()

        binding.rvTransactions.layoutManager = LinearLayoutManager(context)

        adapter = TransactionAdapter(emptyList()) { accountId, onAccountNameFetched ->
            fetchAndCacheAccountName(accountId, onAccountNameFetched)
        }
        binding.rvTransactions.adapter = adapter

        viewModel.transactions.observe(viewLifecycleOwner) { fetchTransactions ->
            transactions = fetchTransactions
            setupChart(fetchTransactions)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
        }

        loadTransactionsForCurrentMonth(userId)

        updateMonthDisplay()
        binding.btnPreviousMonth.setOnClickListener {
            goToPreviousMonth()
            loadTransactionsForCurrentMonth(userId)
        }

        binding.btnNextMonth.setOnClickListener {
            goToNextMonth()
            loadTransactionsForCurrentMonth(userId)
        }


    }

    private fun splitTransactions(transactions: List<Transaction>): Pair<List<Transaction>, List<Transaction>> {
        val incomeTransactions = transactions.filter { it.amount > 0 }
        val expenseTransactions = transactions.filter { it.amount < 0 }

        val income = incomeTransactions.sumOf { it.amount }.toString()
        val expense = expenseTransactions.sumOf { it.amount }.absoluteValue.toString()

        updateTabAmounts(income, expense)
        if(isIncomeTab) adapter.updateTransactions(incomeTransactions)
        else adapter.updateTransactions(expenseTransactions)

        return Pair(incomeTransactions, expenseTransactions)
    }

    private fun setupChart(transactions: List<Transaction>) {
        val (incomeTransactions, expenseTransactions) = splitTransactions(transactions)
        val selectedTransactions = if (isIncomeTab) incomeTransactions else expenseTransactions

        val categoryAmounts = mutableMapOf<String, Float>()

        selectedTransactions.forEach { transaction ->
            val category = transaction.category
            val amount = transaction.amount.toFloat()
            categoryAmounts[category] = categoryAmounts.getOrDefault(category, 0f) + amount
        }

        val pieEntries = categoryAmounts.map { (category, amount) ->
            PieEntry(amount.absoluteValue, category)
        }

        val pieDataSet = PieDataSet(pieEntries, "Categories")
        pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        pieDataSet.valueTextSize = 16f

        val pieData = PieData(pieDataSet)

        binding.pieChart.data = pieData
        binding.pieChart.description.isEnabled = false
        binding.pieChart.setCenterText(if (isIncomeTab) getString(R.string.income_by_category) else getString(
            R.string.expenses_by_category
        ))
        binding.pieChart.setDrawEntryLabels(true)

        binding.pieChart.invalidate()
    }

    private fun setupTab() {
        val incomeBinding = ItemTabLayoutBinding.inflate(LayoutInflater.from(context))
        incomeBinding.tabTitle.text = "Income"
        incomeBinding.tabAmount.text = ""
        incomeBinding.tabAmount.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.green
            )
        )

        val incomeTab = tabLayout.newTab()
        incomeTab.customView = incomeBinding.root
        tabLayout.addTab(incomeTab)

        val expenseBinding = ItemTabLayoutBinding.inflate(LayoutInflater.from(context))
        expenseBinding.tabTitle.text = "Expense"
        expenseBinding.tabAmount.text = ""
        expenseBinding.tabAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))

        val expenseTab = tabLayout.newTab()
        expenseTab.customView = expenseBinding.root
        tabLayout.addTab(expenseTab)

        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)
            val binding = ItemTabLayoutBinding.bind(tab?.customView!!)
            binding.tabTitle.alpha = if (i == 0) 1.0f else 0.3f
        }

        tabLayout.selectTab(tabLayout.getTabAt(0))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val binding = ItemTabLayoutBinding.bind(tab.customView!!)
                binding.tabTitle.alpha = 1.0f
                isIncomeTab = tab.position == 0
                setupChart(transactions)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val binding = ItemTabLayoutBinding.bind(tab.customView!!)
                binding.tabTitle.alpha = 0.3f
            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    private fun updateTabAmounts(income: String, expense: String) {
        val incomeTab = tabLayout.getTabAt(0)
        val incomeBinding = ItemTabLayoutBinding.bind(incomeTab?.customView!!)
        incomeBinding.tabAmount.text = "Rp" + income

        val expenseTab = tabLayout.getTabAt(1)
        val expenseBinding = ItemTabLayoutBinding.bind(expenseTab?.customView!!)
        expenseBinding.tabAmount.text = "Rp" + expense
    }

    private fun loadTransactionsForCurrentMonth(userId: String) {
        viewModel.fetchTransactions(userId, currentYear, currentMonth)
        updateMonthDisplay()
    }

    private fun goToPreviousMonth() {
        if (currentMonth == Calendar.JANUARY) {
            currentMonth = Calendar.DECEMBER
            currentYear--
        } else {
            currentMonth--
        }
    }

    private fun goToNextMonth() {
        if (currentMonth == Calendar.DECEMBER) {
            currentMonth = Calendar.JANUARY
            currentYear++
        } else {
            currentMonth++
        }
    }

    private fun updateMonthDisplay() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.YEAR, currentYear)

        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val monthYearString = monthYearFormat.format(calendar.time)

        binding.tvCurrentMonth.text = monthYearString
    }

    private fun fetchAndCacheAccountName(
        accountId: String,
        onAccountNameFetched: (String) -> Unit
    ) {
        val cachedAccountName = CacheUtility.getCachedAccountName(accountId)
        if (cachedAccountName != null) {
            onAccountNameFetched(cachedAccountName)
        } else {
            AccountRepository.getAccountName(
                accountId,
                onSuccess = { accountName ->
                    CacheUtility.cacheAccountName(accountId, accountName)
                    onAccountNameFetched(accountName)
                },
                onFailure = {
                    onAccountNameFetched("Unknown")
                    Toast.makeText(context, "Failed to load account name", Toast.LENGTH_SHORT)
                        .show()
                }
            )
        }
    }
}