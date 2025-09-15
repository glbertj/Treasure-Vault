package edu.bluejack24_1.treasurevault.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack24_1.treasurevault.models.Asset
import edu.bluejack24_1.treasurevault.models.User
import edu.bluejack24_1.treasurevault.repository.AccountRepository
import edu.bluejack24_1.treasurevault.repository.AssetRepository

class AssetOverviewViewModel : ViewModel() {
    private val _totalAssets = MutableLiveData<Double>()
    val totalAssets: LiveData<Double> = _totalAssets

    private val _difference = MutableLiveData<Double>()
    val difference: LiveData<Double> = _difference

    private val _assetsInTimeframe = MutableLiveData<List<Asset>>()
    val assetsInTimeframe: LiveData<List<Asset>> = _assetsInTimeframe

    fun loadTotalAssets() {
        val userId = User.userId ?: return
        AccountRepository.getAccounts(userId,
            onSuccess = { accounts ->
                val total = accounts.sumOf { it.balance }
                _totalAssets.postValue(total)
            },
            onFailure = { exception ->
                println(exception)
            }
        )
    }

    fun loadAssetsForTimeframe(startDate: Long, endDate: Long) {
        val userId = User.userId ?: return
        AssetRepository.fetchAssetsInRange(userId, startDate, endDate,
            onSuccess = { assets ->
                _assetsInTimeframe.postValue(assets)
            },
            onFailure = { exception ->
                println(exception)
            }
        )
    }

    fun calculateDifference(currentTotal: Double) {
        val earliestAsset = _assetsInTimeframe.value?.minByOrNull { it.timestamp }
        val difference = if (earliestAsset != null) {
            currentTotal - earliestAsset.asset
        } else {
            0.0
        }
        _difference.postValue(difference)
    }
}
