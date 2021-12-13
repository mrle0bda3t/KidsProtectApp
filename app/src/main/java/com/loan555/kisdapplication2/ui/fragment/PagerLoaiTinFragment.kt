package com.loan555.kisdapplication2.ui.fragment

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.loan555.kisdapplication2.constant.KEY_DETAIL_NEWS
import com.loan555.kisdapplication2.databinding.PagerLoaiTinFragmentBinding
import com.loan555.kisdapplication2.model.view.ThongTinTuyenTruyen
import com.loan555.kisdapplication2.ui.adapter.NewsAdapter
import com.loan555.kisdapplication2.ui.activity.DetailActivity
import com.loan555.kisdapplication2.viewmodel.PagerLoaiTinViewModel

class PagerLoaiTinFragment(private val id: String) : Fragment() {

    private lateinit var viewModel: PagerLoaiTinViewModel
    private var _binding: PagerLoaiTinFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(PagerLoaiTinViewModel::class.java)
        _binding = PagerLoaiTinFragmentBinding.inflate(inflater, container, false)
        val root = binding.root

        initControl()
        initData()
        return root
    }

    private fun initData() {
        viewModel.loadListThongTinTheoTheLoai(id, 20)
    }

    private fun initControl() {
        var adapter = NewsAdapter(this.requireContext(), onNewsClick)
        viewModel.getListThongTinTuyenTruyenTheoTheLoai().observe(viewLifecycleOwner,{
            adapter.setList(it)
        })
        binding.recyclerViewCata.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewCata.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val onNewsClick: (Int, ThongTinTuyenTruyen) -> Unit = { pos, tin ->
        val bundle = Bundle()
        bundle.putSerializable(KEY_DETAIL_NEWS,tin)
        val intent = Intent(this.context, DetailActivity::class.java)
        intent.putExtra(KEY_DETAIL_NEWS, bundle)
        startActivity(intent)
    }

}