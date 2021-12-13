package com.loan555.kisdapplication2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.loan555.kisdapplication2.R
import com.loan555.kisdapplication2.ui.adapter.NotificationAdapter
import com.loan555.kisdapplication2.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.toolbar_common.view.*

class NotificationActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by lazy {
        MainViewModel(application)
    }
    private lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        adapter = NotificationAdapter(application)
        initView()
        initObserve()
        initListener()
    }

    private fun initView() {
        toolbar.btnMoreToolbarCommon.visibility = View.GONE
        toolbar.titlToolbarCommon.text = "Thông báo"
        rvNotification.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvNotification.adapter = adapter
    }

    private fun initObserve() {
        mainViewModel.getAllNotification().observe(this, {
            it?.let {
                if (it.isEmpty()) tvEmptyNotification.visibility = View.VISIBLE
                else {
                    tvEmptyNotification.visibility = View.INVISIBLE
                }
            }
            adapter.setList(it)
        })
    }

    private fun initListener() {
        toolbar.btnBackToolbarCommon.setOnClickListener { finish() }
    }
}