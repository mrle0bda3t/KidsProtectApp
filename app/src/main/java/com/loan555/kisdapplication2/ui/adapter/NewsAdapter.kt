package com.loan555.kisdapplication2.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.loan555.kisdapplication2.R
import com.loan555.kisdapplication2.model.view.ThongTinTuyenTruyen

class NewsAdapter(
    private val context: Context,
    val onItemClick: (Int, ThongTinTuyenTruyen) -> Unit
) :
    RecyclerView.Adapter<NewsAdapter.MyViewHolder>() {
    private var list: List<ThongTinTuyenTruyen> = listOf()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tenBai: TextView = itemView.findViewById(R.id.tenBaiViet)
        private val anhDaiDien: ImageView = itemView.findViewById(R.id.anhDaiDien)
        private val ngayDang: TextView = itemView.findViewById(R.id.ngayDang)
        private val loaiTin: TextView = itemView.findViewById(R.id.loaiTin)
        private val itemLayout: RelativeLayout = itemView.findViewById(R.id.itemNews)
        fun onBind(tin: ThongTinTuyenTruyen) {
            tenBai.text = tin.tenBaiViet
            ngayDang.text = tin.ngayDang.substring(0, 10)
            loaiTin.text = tin.loaiThongTinTuyenTruyen.tenLoai
            Glide.with(context).asBitmap().load(tin.anhDaiDien).into(anhDaiDien)
            itemLayout.setOnClickListener { onItemClick(layoutPosition, tin) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.news_adapter, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun setList(newList: List<ThongTinTuyenTruyen>) {
        this.list = newList
        notifyDataSetChanged()
    }
}