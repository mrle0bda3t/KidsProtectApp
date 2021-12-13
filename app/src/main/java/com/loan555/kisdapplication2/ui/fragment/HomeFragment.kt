package com.loan555.kisdapplication2.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.loan555.kisdapplication2.R
import com.loan555.kisdapplication2.constant.KEY_DETAIL_NEWS
import com.loan555.kisdapplication2.model.view.ThongTinTuyenTruyen
import com.loan555.kisdapplication2.ui.adapter.NewsAdapter
import com.loan555.kisdapplication2.ui.activity.DetailActivity
import com.loan555.kisdapplication2.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment(private val listener: LoadDataResult) : Fragment() {

    private var homeViewModel: MainViewModel? = null
    private var adapter: NewsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = activity?.let { MainViewModel(it.application) }
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initControl()
        initData()
    }

    private fun initControl() {
        adapter = NewsAdapter(this.requireContext(), onNewsClick)
        homeViewModel?.getListThongTinTuyenTruyen()?.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                listener.dataOk()
                adapter!!.setList(it)
            }
        })
        recyclerViewHome.layoutManager =
            LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerViewHome.adapter = adapter
    }

    private fun initData() {
        homeViewModel?.loadListThongTin(20)
    }

    private val onNewsClick: (Int, ThongTinTuyenTruyen) -> Unit = { pos, tin ->
        val intent = Intent(this.context, DetailActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable(KEY_DETAIL_NEWS, tin)
        intent.putExtra(KEY_DETAIL_NEWS, bundle)
        startActivity(intent)
    }

    interface LoadDataResult {
        fun dataOk()
    }
}