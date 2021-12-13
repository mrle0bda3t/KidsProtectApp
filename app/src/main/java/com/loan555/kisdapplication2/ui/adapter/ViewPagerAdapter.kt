package com.loan555.kisdapplication2.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.loan555.kisdapplication2.model.view.LoaiThongTinTuyenTruyen
import com.loan555.kisdapplication2.ui.fragment.PagerLoaiTinFragment

class ViewPagerAdapter(fa: FragmentManager, private val list: List<LoaiThongTinTuyenTruyen>) :
    FragmentPagerAdapter(fa, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = list.size

    override fun getItem(position: Int): Fragment {
        return PagerLoaiTinFragment(list[position].id)
    }
}