package com.Azhe.ghs.gshschedule.announcement

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.Azhe.ghs.gshschedule.base_view.BaseActivity
import com.Azhe.ghs.gshschedule.databinding.ActivityAnnouncementListBinding
import com.Azhe.ghs.gshschedule.utils.AnnouncementManager
import kotlinx.coroutines.launch

/**
 * 公告列表页 — 展示所有有效公告（已读 + 未读）。
 *
 * 入口：
 *  - 侧边栏通知铃铛图标点击
 *  - 弹窗「查看全部」按钮（预留）
 */
class AnnouncementActivity : BaseActivity() {

    private lateinit var binding: ActivityAnnouncementListBinding
    private lateinit var adapter: AnnouncementAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnnouncementListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 使用 Toolbar 替代 ActionBar
        // 按项目通用模式：用 spacer view 撑开状态栏高度，避免 Toolbar 与状态栏重叠
        resizeStatusBar(binding.vStatus)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        adapter = AnnouncementAdapter(this, emptyList())
        binding.rvAnnouncements.layoutManager = LinearLayoutManager(this)
        binding.rvAnnouncements.adapter = adapter

        loadAnnouncements()
    }

    private fun loadAnnouncements() {
        lifecycleScope.launch {
            try {
                val list = AnnouncementManager.fetchAllAnnouncements(this@AnnouncementActivity)
                // id 大的排在前面（新公告在上，符合日常使用习惯）
                adapter.updateData(list.sortedByDescending { it.id })
            } catch (_: Exception) {
                // 静默失败
            }
        }
    }
}
