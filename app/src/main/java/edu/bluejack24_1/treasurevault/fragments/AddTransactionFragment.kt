package edu.bluejack24_1.treasurevault.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.bluejack24_1.treasurevault.R
import edu.bluejack24_1.treasurevault.activities.AddTransactionDetailActivity
import edu.bluejack24_1.treasurevault.activities.PrivacySecurityActivity
import edu.bluejack24_1.treasurevault.databinding.ActivityAddTransactionDetailBinding
import edu.bluejack24_1.treasurevault.databinding.FragmentAddTransactionBinding
import edu.bluejack24_1.treasurevault.utilities.NavigationUtility

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddTransactionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddTransactionFragment : Fragment() {
    private lateinit var binding: FragmentAddTransactionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.expenseButton.setOnClickListener{
            NavigationUtility.navigateTo(requireContext(), AddTransactionDetailActivity::class.java, false, "type", "expense")
        }

        binding.incomeButton.setOnClickListener{
            NavigationUtility.navigateTo(requireContext(), AddTransactionDetailActivity::class.java, false, "type", "income")
        }
    }
}