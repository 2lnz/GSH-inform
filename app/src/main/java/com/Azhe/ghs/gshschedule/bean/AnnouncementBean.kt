package com.Azhe.ghs.gshschedule.bean

import com.google.gson.annotations.SerializedName

/**
 * 公告数据模型，对应 GitHub Pages 上托管的 JSON 格式。
 *
 * JSON 示例：
 * {
 *   "id": 1,
 *   "title": "新版发布",
 *   "content": "课程表 v2.0 已发布...",
 *   "type": "once",
 *   "startTime": "2026-06-01 00:00",
 *   "endTime": "2026-06-30 23:59",
 *   "link": "https://example.com"
 * }
 *
 * type 说明：
 *  - "once"：每个用户只弹一次，已读 ID 记录在 SharedPreferences
 *  - "every"：每次打开 App 都弹（在有效期内）
 */
data class AnnouncementBean(
    val id: Int = 0,
    val title: String = "",
    val content: String = "",
    val type: String = "once",            // "once" | "every"
    val startTime: String = "",           // 格式：yyyy-MM-dd HH:mm
    val endTime: String = "",             // 格式：yyyy-MM-dd HH:mm
    val link: String = ""                 // 可选：点击跳转链接
)
