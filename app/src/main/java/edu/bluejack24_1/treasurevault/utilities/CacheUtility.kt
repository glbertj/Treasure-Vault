package edu.bluejack24_1.treasurevault.utilities

object CacheUtility {
    private val accountCache = mutableMapOf<String, String>()
    private val cacheExpiryMap = mutableMapOf<String, Long>()

    private const val CACHE_EXPIRY_TIME = 600000

    fun getCachedAccountName(accountId: String): String? {
        val currentTime = System.currentTimeMillis()
        return if (accountCache.containsKey(accountId) && currentTime < (cacheExpiryMap[accountId]
                ?: 0)
        ) {
            accountCache[accountId]
        } else {
            null
        }
    }

    fun cacheAccountName(accountId: String, accountName: String) {
        accountCache[accountId] = accountName
        cacheExpiryMap[accountId] = System.currentTimeMillis() + CACHE_EXPIRY_TIME
    }

    fun flushCache() {
        accountCache.clear()
        cacheExpiryMap.clear()
    }

    fun clearExpiredCache() {
        val currentTime = System.currentTimeMillis()
        val expiredKeys = cacheExpiryMap.filterValues { it < currentTime }.keys
        for (key in expiredKeys) {
            accountCache.remove(key)
            cacheExpiryMap.remove(key)
        }
    }
}
