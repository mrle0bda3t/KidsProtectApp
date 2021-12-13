package com.loan555.kisdapplication2.ui.adapter

import android.app.Application
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.loan555.kisdapplication2.R
import com.loan555.kisdapplication2.database.NotificationEntity
import com.loan555.kisdapplication2.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(application: Application) :
    RecyclerView.Adapter<NotificationAdapter.MyViewHolder>() {
    private val mainViewModel: MainViewModel by lazy {
        MainViewModel(application)
    }
    private val mContext = application.applicationContext
    private var list: List<NotificationEntity> = listOf()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.titleNotification)
        private val mess: TextView = itemView.findViewById(R.id.messNotification)
        private val time: TextView = itemView.findViewById(R.id.timeNotification)
        private val url: TextView = itemView.findViewById(R.id.appUrlNotification)
        private val img: ImageView = itemView.findViewById(R.id.imgNotification)
        private val layoutView: ConstraintLayout = itemView.findViewById(R.id.itemNotification)
        private val btnMore: ImageView = itemView.findViewById(R.id.btnMoreNotification)

        fun onBind(position: Int) {
            val item = list[position]
            title.text = item.title
            mess.text = item.message
            url.text = item.appUrl
            list[position].avatarApp?.let {
                Glide.with(mContext).load(it).into(img)
            }
            item.time?.let { time.text = item.time.toString() }
            if (item.read) {
                layoutView.setBackgroundColor(Color.WHITE)
            }
            itemView.setOnClickListener {
                list[position].let {
                    if (!it.read) {
                        it.read = true
                        mainViewModel.markRead(it.id)
                        notifyDataSetChanged()
                    }
                }
            }
            btnMore.setOnClickListener {
                val popupMenu: PopupMenu = PopupMenu(mContext, btnMore)
                popupMenu.menuInflater.inflate(R.menu.option_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.delete -> {
                            list.drop(position)
                            mainViewModel.deleteNotification(list[position].id)
                            notifyDataSetChanged()
                        }
                    }
                    true
                })
                popupMenu.show()
            }
        }

        private fun getTime(time: Long): String {
            val now = Calendar.getInstance().timeInMillis
            val checkTime = if (time <= now) now - time
            else 0
            val seconds = checkTime / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            var timeStr = ""
            when {
                hours in 1..24 -> {
                    timeStr = "$hours giờ trước"
                    return timeStr
                }
                minutes in 1..60 -> {
                    timeStr = "$minutes phút trước"
                    return timeStr
                }
                seconds in 1..60 -> {
                    timeStr = "$seconds giây trước"
                    return timeStr
                }
                else -> {
                    timeStr = SimpleDateFormat("d/MM/y lúc HH:mm").format(Date(time))
                }
            }
            return timeStr
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_adapter_view, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = list.size

    fun setList(newList: List<NotificationEntity>) {
        this.list = newList
        notifyDataSetChanged()
    }
}