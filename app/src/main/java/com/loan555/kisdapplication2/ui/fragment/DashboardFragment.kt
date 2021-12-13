package com.loan555.kisdapplication2.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.loan555.kisdapplication2.databinding.FragmentDashboardBinding
import com.loan555.kisdapplication2.ui.adapter.ViewPagerAdapter
import com.loan555.kisdapplication2.viewmodel.DashboardViewModel

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initControl()
        initData()
        return root
    }

    private fun initControl() {
        dashboardViewModel.getLoaiTin().observe(viewLifecycleOwner, {
            it.forEach {
                binding.tabLayout.addTab(
                    binding.tabLayout.newTab().setText(it.tenLoai)
                )
            }
            val adapter = ViewPagerAdapter(requireActivity().supportFragmentManager, it)
            binding.viewPagerCata.adapter = adapter
        })

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewPagerCata.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        binding.viewPagerCata.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                binding.tabLayout
            )
        )
    }

    private fun initData() {
        dashboardViewModel.loadLoaiTin()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}