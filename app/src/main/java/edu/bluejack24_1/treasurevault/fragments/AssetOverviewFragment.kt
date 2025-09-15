package edu.bluejack24_1.treasurevault.fragments

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import edu.bluejack24_1.treasurevault.R
import edu.bluejack24_1.treasurevault.databinding.FragmentAssetOverviewBinding
import edu.bluejack24_1.treasurevault.models.Asset
import edu.bluejack24_1.treasurevault.viewmodels.AssetOverviewViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class AssetOverviewFragment : Fragment() {
    private lateinit var binding: FragmentAssetOverviewBinding
    private lateinit var viewModel: AssetOverviewViewModel

    private var isAllSelected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetOverviewBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[AssetOverviewViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.totalAssets.observe(viewLifecycleOwner) { totalAssets ->
            binding.totalAssetsAmount.text = "Rp$totalAssets"
        }

        viewModel.difference.observe(viewLifecycleOwner) { difference ->
            if (difference >= 0) {
                binding.difference.setTextColor(resources.getColor(android.R.color.holo_green_dark))
            } else {
                binding.difference.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            }
            binding.difference.text = "$difference"
        }

        viewModel.assetsInTimeframe.observe(viewLifecycleOwner) {
            viewModel.calculateDifference(viewModel.totalAssets.value ?: 0.0)
            setupChart(it)
        }

        viewModel.loadTotalAssets()
        updateToggleState()

        binding.tvAll.setOnClickListener {
            isAllSelected = true
            updateToggleState()
        }
        binding.tvWeek.setOnClickListener {
            isAllSelected = false
            updateToggleState()
        }
    }

    private fun updateToggleState() {
        val colorWhite = ContextCompat.getColor(binding.root.context, R.color.white)
        val colorBlack = ContextCompat.getColor(binding.root.context, R.color.black)

        if (isAllSelected) {
            binding.tvAll.setBackgroundResource(R.drawable.toggle_selected_background)
            binding.tvWeek.setBackgroundResource(R.drawable.toggle_unselected_background)

            ObjectAnimator.ofObject(
                binding.tvAll,
                "textColor",
                ArgbEvaluator(),
                colorBlack,
                colorWhite
            ).setDuration(300).start()

            ObjectAnimator.ofObject(
                binding.tvWeek,
                "textColor",
                ArgbEvaluator(),
                colorWhite,
                colorBlack
            ).setDuration(300).start()

            val today = System.currentTimeMillis()
            val beginning = 0L
            binding.timeFrame.text = "All"

            viewModel.loadAssetsForTimeframe(beginning, today)
        } else {
            binding.tvAll.setBackgroundResource(R.drawable.toggle_unselected_background)
            binding.tvWeek.setBackgroundResource(R.drawable.toggle_selected_background)

            ObjectAnimator.ofObject(
                binding.tvAll,
                "textColor",
                ArgbEvaluator(),
                colorWhite,
                colorBlack
            ).setDuration(300).start()

            ObjectAnimator.ofObject(
                binding.tvWeek,
                "textColor",
                ArgbEvaluator(),
                colorBlack,
                colorWhite
            ).setDuration(300).start()

            val today = System.currentTimeMillis()
            val sevenDaysAgo = today - TimeUnit.DAYS.toMillis(7)
            binding.timeFrame.text = getString(R.string.last_week)

            viewModel.loadAssetsForTimeframe(sevenDaysAgo, today)
        }
    }

    private fun setupChart(assets: List<Asset>) {
        val entries = mutableListOf<Entry>()
        val type = binding.timeFrame.text.toString()

        val calendar = Calendar.getInstance()
        val now = calendar.time

        val typeCalendar = Calendar.getInstance()

        if(type == "Last Week"){
            typeCalendar.add(Calendar.DAY_OF_YEAR, -6)

            var prevAmount = 0f
            while (typeCalendar.time.before(now) || typeCalendar.time.equals(now)) {
                val currentDate = typeCalendar.time
                val currentDateStartOfDay = getStartOfDay(currentDate)

                val amount = assets.find { asset ->
                    val assetDate = Date(asset.timestamp)
                    val assetDateStartOfDay = getStartOfDay(assetDate)
                    assetDateStartOfDay == currentDateStartOfDay
                }?.asset
                if (amount != null) {
                    prevAmount = amount.toFloat()
                    entries.add(Entry((currentDate.time).toFloat(), amount.toFloat()))
                } else {
                    entries.add(Entry((currentDate.time).toFloat(), prevAmount))
                }
                typeCalendar.add(Calendar.DAY_OF_YEAR, 1)
            }
        } else {
            //TODO!!! Improve This
            assets.forEachIndexed { index, asset ->
                entries.add(Entry(index.toFloat(), asset.asset.toFloat()))
            }
        }

        entries.sortWith(EntryXComparator())

        val lineDataSet = LineDataSet(entries, getString(R.string.assets_over_time))
        lineDataSet.setDrawCircles(true)
        lineDataSet.setDrawValues(true)

        val lineData = LineData(lineDataSet)
        binding.lineChart.data = lineData

        val xAxis: XAxis = binding.lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.valueFormatter = XAxisFormatter(assets, SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))
        binding.lineChart.description.isEnabled = false
        binding.lineChart.axisRight.isEnabled = false

        if (binding.timeFrame.text.toString() == "All") {
            xAxis.setLabelCount(5, true)
        } else {
            xAxis.setLabelCount(assets.size, false)
        }

        binding.lineChart.invalidate()
    }

    private fun getStartOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    private class XAxisFormatter(
        private val assets: List<Asset>,
        private val dateFormat: SimpleDateFormat
    ) : com.github.mikephil.charting.formatter.ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val index = value.toInt()
            return if (index in assets.indices) {
                val date = Date(assets[index].timestamp)
                dateFormat.format(date)
            } else {
                ""
            }
        }
    }
}
