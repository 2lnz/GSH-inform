package com.Azhe.ghs.gshschedule.announcement

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.Azhe.ghs.gshschedule.bean.AnnouncementBean
import com.Azhe.ghs.gshschedule.databinding.ItemAnnouncementBinding

/**
 * 公告列表 RecyclerView Adapter
 */
class AnnouncementAdapter(
    private val context: Context,
    private var items: List<AnnouncementBean>
) : RecyclerView.Adapter<AnnouncementAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAnnouncementBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAnnouncementBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvItemTitle.text = item.title
            tvItemContent.text = item.content
            tvItemTime.text = buildString {
                if (item.startTime.isNotBlank()) append(item.startTime)
                if (item.endTime.isNotBlank()) append(" ~ ").append(item.endTime)
            }

            // 有链接则点击跳转
            if (item.link.isNotBlank()) {
                tvItemContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                root.setOnClickListener {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.link))
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                }
            } else {
                root.setOnClickListener(null)
                root.isClickable = false
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<AnnouncementBean>) {
        items = newItems
        notifyDataSetChanged()
    }
}
