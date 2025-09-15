package edu.bluejack24_1.treasurevault.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import edu.bluejack24_1.treasurevault.models.Asset
import edu.bluejack24_1.treasurevault.models.User
import edu.bluejack24_1.treasurevault.repository.AccountRepository
import edu.bluejack24_1.treasurevault.repository.AssetRepository.saveAsset
import java.util.Calendar

class DailyAssetWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val userId = User.userId ?: return Result.failure()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val today = calendar.timeInMillis

        AccountRepository.getAccounts(
            userId,
            onSuccess = { accounts ->
                val totalAssets = accounts.sumOf { it.balance }
                val asset = Asset(timestamp = today, asset = totalAssets)
                saveAsset(
                    userId,
                    asset,
                    onSuccess = { Result.success() },
                    onFailure = { Result.failure() }
                )
            },
            onFailure = {
                Result.failure()
            }
        )
        return Result.success()
    }
}