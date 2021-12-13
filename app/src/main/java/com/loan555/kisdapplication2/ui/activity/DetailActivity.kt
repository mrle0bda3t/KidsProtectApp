package com.loan555.kisdapplication2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.loan555.kisdapplication2.R
import com.loan555.kisdapplication2.constant.KEY_DETAIL_NEWS
import com.loan555.kisdapplication2.model.view.DinhDangThongTinTuyenTruyen
import com.loan555.kisdapplication2.model.view.LoaiThongTinTuyenTruyen
import com.loan555.kisdapplication2.model.view.ThongTinTuyenTruyen
import com.loan555.kisdapplication2.ui.adapter.NewsAdapter
import com.loan555.kisdapplication2.util.GlideImageGetter
import com.loan555.kisdapplication2.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.detail_activity.*
import kotlinx.android.synthetic.main.toolbar_common.view.*

class DetailActivity : AppCompatActivity() {
    private var viewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = MainViewModel(application)
        setContentView(R.layout.detail_activity)
        initControl()
    }

    private fun initControl() {
        toolbar.btnMoreToolbarCommon.visibility = View.GONE
        toolbar.btnBackToolbarCommon.setOnClickListener {
            finish()
        }
        val bundle = intent.getBundleExtra(KEY_DETAIL_NEWS)
        val tin = bundle?.getSerializable(KEY_DETAIL_NEWS) as ThongTinTuyenTruyen
        val id = tin.id
        tin.let {
            ngayDang.text = it.ngayDang.substring(0, 10)
            tenBaiViet.text = it.tenBaiViet
            chuThich.text = "#${it.chuThich}"
            tacGia.text = it.tacGia
            noiDung.text =
                android.text.Html.fromHtml(
                    it.noidung,
                    GlideImageGetter(noiDung),
                    null
                )
            toolbar.titlToolbarCommon.text = ""
            if (it.loaiThongTinTuyenTruyen.tenLoai.length > 50) {
                toolbar.titlToolbarCommon.text =
                    it.loaiThongTinTuyenTruyen.tenLoai.substring(0, 46) + "..."
            } else
                toolbar.titlToolbarCommon.text = it.loaiThongTinTuyenTruyen.tenLoai
        }
        viewModel?.loadDetail(id)?.observe(this, { result ->
            val list = mutableListOf<ThongTinTuyenTruyen>()
            result.baiVietLienQuan?.forEach {
                list += ThongTinTuyenTruyen(
                    it.id,
                    it.tenBaiViet,
                    it.tacGia,
                    it.chuThich,
                    LoaiThongTinTuyenTruyen(it.loaiThongTinTuyenTruyen, "Bài viết liên quan", 0),
                    DinhDangThongTinTuyenTruyen(it.id, "", 0),
                    it.ngayDang,
                    it.tinhTrang,
                    it.anhDaiDien,
                    it.noiDung
                )
            }
            dsBaiLienQuan.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            dsBaiLienQuan.adapter =
                NewsAdapter(this, onNewsClick).apply { setList(list) }
        })
    }

    private val onNewsClick: (Int, ThongTinTuyenTruyen) -> Unit = { pos, tin ->
        val bundle = Bundle()
        bundle.putSerializable(KEY_DETAIL_NEWS, tin)
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(KEY_DETAIL_NEWS, bundle)
        startActivity(intent)
    }
}