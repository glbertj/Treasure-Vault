package edu.bluejack24_1.treasurevault.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.work.Configuration
import androidx.work.WorkManager
import edu.bluejack24_1.treasurevault.R
import edu.bluejack24_1.treasurevault.databinding.ActivityMainBinding
import edu.bluejack24_1.treasurevault.fragments.AddTransactionFragment
import edu.bluejack24_1.treasurevault.fragments.AssetFragment
import edu.bluejack24_1.treasurevault.fragments.TransactionFragment
import edu.bluejack24_1.treasurevault.utilities.AuthenticationUtility
import edu.bluejack24_1.treasurevault.utilities.NavigationUtility
import edu.bluejack24_1.treasurevault.utilities.WorkerUtility

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (AuthenticationUtility.isNotAuthenticated(this)) { return }
        WorkerUtility.scheduleAssetWorker(this)

        replaceFragment(AssetFragment())
        binding.bottomNavBar.menu.findItem(R.id.spacer).isEnabled = false
        binding.bottomNavBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.assets -> {
                    binding.bottomNavBar.menu.findItem(R.id.assets).isChecked = true
                    replaceFragment(AssetFragment())
                }
                R.id.transactions -> {
                    binding.bottomNavBar.menu.findItem(R.id.transactions).isChecked = true
                    replaceFragment(TransactionFragment())
                }
            }
            true
        }

        binding.header.ivProfile.setOnClickListener {
            NavigationUtility.navigateTo(this, AccountActivity::class.java, false)
        }

        binding.fab.setOnClickListener {
            binding.bottomNavBar.menu.findItem(R.id.spacer).isChecked = true
            replaceFragment(AddTransactionFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()

    }
}