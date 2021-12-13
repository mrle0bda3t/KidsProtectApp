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
import com.loan555.kidsapplication.model.view.ThongTinThanhVienAdapter
import com.loan555.kisdapplication2.R

class AccountAdapter(
    private val context: Context,
    val onItemClick: (Int, List<ThongTinThanhVienAdapter>) -> Unit
) :
    RecyclerView.Adapter<AccountAdapter.MyViewHolder>() {
    private var list: List<ThongTinThanhVienAdapter> = listOf()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val info: TextView = itemView.findViewById(R.id.thongTin)
        private val anhDaiDien: ImageView = itemView.findViewById(R.id.anhDaiDien)
        private val itemLayout: RelativeLayout = itemView.findViewById(R.id.itemLayout)
        fun onBind(data: ThongTinThanhVienAdapter) {
            info.text = data.thongTin
            if (data.anhDaidien != null)
                Glide.with(context).load(data.anhDaidien).into(anhDaiDien)
            itemLayout.setOnClickListener { onItemClick(layoutPosition, list) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.thanh_view_adapter, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun setList(newList: List<ThongTinThanhVienAdapter>) {
        this.list = newList
        notifyDataSetChanged()
    }
}