package edu.bluejack24_1.treasurevault.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack24_1.treasurevault.databinding.ItemTransactionBinding
import edu.bluejack24_1.treasurevault.models.Transaction
import edu.bluejack24_1.treasurevault.repository.AccountRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionAdapter(
    private var transactions: List<Transaction>,
    private val onAccountNameRetrieved: (String, (String) -> Unit) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTransactionBinding.inflate(inflater, parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.bind(transaction)

        onAccountNameRetrieved(transaction.accountId) { accountName ->
            holder.updateAccountName(accountName)
        }
    }

    override fun getItemCount() = transactions.size

    fun updateTransactions(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }

    class TransactionViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            binding.tvAmount.text = "Rp${transaction.amount}"
            binding.tvDate.text = formatDate(transaction.timestamp)

            val color = if (transaction.amount < 0) android.graphics.Color.RED else android.graphics.Color.GREEN
            binding.tvAmount.setTextColor(color)
        }

        fun updateAccountName(accountName: String) {
            binding.tvAccountName.text = accountName
        }

        private fun formatDate(dateMillis: Long): String {
            val date = Date(dateMillis)
            val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            return format.format(date)
        }
    }
}