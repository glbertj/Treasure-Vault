package edu.bluejack24_1.treasurevault.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import edu.bluejack24_1.treasurevault.R
import edu.bluejack24_1.treasurevault.adapters.AccountAdapter
import edu.bluejack24_1.treasurevault.databinding.FragmentAssetDetailBinding
import edu.bluejack24_1.treasurevault.models.User
import edu.bluejack24_1.treasurevault.viewmodels.AssetDetailViewModel
import edu.bluejack24_1.treasurevault.viewmodels.AssetOverviewViewModel

class AssetDetailFragment : Fragment() {

    private lateinit var binding: FragmentAssetDetailBinding
    private lateinit var accountAdapter: AccountAdapter
    private lateinit var viewModel: AssetDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAssetDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = User.userId ?: return

        viewModel = ViewModelProvider(this)[AssetDetailViewModel::class.java]

        accountAdapter = AccountAdapter()
        binding.rvAccounts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAccounts.adapter = accountAdapter

        viewModel.accounts.observe(viewLifecycleOwner) { accounts ->
            accountAdapter.submitList(accounts)
        }

        viewModel.getAccounts(userId)
    }
}
