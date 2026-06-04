# WakeUp 课程表 BUPT — Material 3 视觉重构设计

## 背景

基于 WakeUpSchedule_BUPT 开源项目进行二次开发，目标是：
1. 全面视觉升级至 Material 3 设计语言
2. 去除所有非必要的服务器依赖，保持纯本地运行
3. 简化代码结构，降低维护门槛

## 范围

- **视觉大改**：课表主页 Compose + M3 重写，次要页面 M3 主题统一
- **去服务器依赖**：移除 AppCenter、版本更新检查、使用统计、QQ 客服
- **不改动**：教务系统导入逻辑、课程数据模型、Room 数据库结构、闹钟提醒

---

## 1. 构建工具链升级

| 项目 | 当前 | 升级后 |
|------|------|--------|
| AGP | 3.5.3 | 7.4.2 |
| Gradle | 5.4.1+ | 8.2 |
| Kotlin | 1.3.61 | 1.9.22 |
| compileSdk | 29 | 34 |
| targetSdk | 29 | 34 |
| minSdk | 21 | 24 |
| Java | 1.8 | 17 |
| Material Components | 1.2.0-alpha04 | com.google.android.material:material:1.11.0 |
| Jetpack Compose | 无 | BOM 2024.02.00 |

### 依赖变更

**新增：**
- `androidx.compose:compose-bom:2024.02.00`
- `androidx.compose.material3:material3`
- `androidx.compose.ui:ui`
- `androidx.activity:activity-compose:1.8.2`
- `androidx.navigation:navigation-compose:2.7.6`

**移除：**
- `com.microsoft.appcenter:appcenter-analytics`
- `com.microsoft.appcenter:appcenter-crashes`
- `com.squareup.retrofit2:retrofit`（保留依赖本身，教务导入需要；仅删除 `MyRetrofitUtils` 中调用原作者服务器的 `addCount()`、`getUpdateInfo()`、`getDonateList()` 方法）
- `kotlin-android-extensions` 插件

**升级：**
- Room 2.2.3 → 2.6.1
- Glide 4.11.0 → 4.16.0
- Jsoup 1.12.2 → 1.17.2
- Gson 2.8.6 → 2.10.1
- Navigation 2.2.1 → 2.7.6
- Coroutines 1.3.3 → 1.7.3

### 配置变更

- 简化 product flavors 为单一 flavor（去掉 google/huawei 渠道区分）
- 签名配置移至 `local.properties` 引用，删除硬编码凭据
- `build.gradle` 迁移至 Kotlin DSL（`build.gradle.kts`）— 可选，非必须

---

## 2. 主课表界面 — Compose 重写

### 2.1 整体布局

```
┌─────────────────────────────────┐
│  TopAppBar (M3)                 │
│  ┌───────────────────────────┐  │
│  │ 日期  周一  第X周    [+] [=]│  │
│  └───────────────────────────┘  │
│  ┌──┬──┬──┬──┬──┬──┬──┐        │
│  │时│一│二│三│四│五│六│ 表头    │
│  ├──┼──┼──┼──┼──┼──┼──┤        │
│  │ 1│  │  │  │  │  │  │        │
│  │  │课│课│课│课│课│课│ 课表    │
│  │ 2│程│程│程│程│程│程│ 网格    │
│  │  │卡│卡│卡│卡│卡│卡│        │
│  │ 3│片│片│片│片│片│片│        │
│  │..│  │  │  │  │  │  │        │
│  └──┴──┴──┴──┴──┴──┴──┘        │
│  ─── ModalBottomSheet ───       │
│  [周数选择] [多课表切换]          │
│  [捷径按钮行]                    │
└─────────────────────────────────┘
│◀── NavigationDrawer ────────────│
```

### 2.2 课程卡片设计

- 圆角：12dp（M3 large corner radius）
- 背景：课程颜色 tonal palette 的 container 色
- 文字：课程名 + 教室 + 教师，onContainer 色
- 高度：可配置（继承 `TableBean.itemHeight`）
- 透明度：可配置（继承 `TableBean.itemAlpha`）
- 今日高亮：M3 primary tonal container + 边框
- 点击：展开显示课程详情（Compose 动画）

### 2.3 关键 Compose 组件

| 组件 | 职责 |
|------|------|
| `ScheduleScreen` | 顶层 Scaffold，管理 TopAppBar + 内容 + BottomSheet |
| `ScheduleGrid` | 课表网格，LazyVerticalGrid 或自定义 Layout |
| `DayHeaderRow` | 星期表头行 |
| `TimeColumn` | 左侧节次/时间列 |
| `CourseCard` | 单个课程卡片，支持点击展开 |
| `CourseDetailSheet` | 课程详情弹出面板 |
| `WeekSelector` | 周数选择 Chips 行 |
| `TableSwitcher` | 多课表切换 RecyclerView 替代 |

### 2.4 数据层不变

- `ScheduleViewModel` 保持现有逻辑
- LiveData → Compose State 转换使用 `collectAsState()` / `observeAsState()`
- Room DAO 查询不变

---

## 3. 次要页面 — M3 主题统一

以下页面保留 XML 布局，通过升级主题自动适配 M3 风格：

- `AddCourseActivity` — 课程添加/编辑
- `ScheduleSettingsActivity` — 设置页
- `LoginWebActivity` / `SchoolListActivity` — 教务导入
- `AboutActivity` — 关于页
- 各 Fragment（课程详情、选周、选时间等）

### 主题配置

- 基础主题：`Theme.Material3.DayNight.NoActionBar`
- 支持动态取色（Android 12+）：`DynamicColors.applyToActivityIfAvailable()`
- 降级方案：预设 M3 tonal palette 调色板
- Shape：统一使用 M3 shape tokens（小 8dp，中 12dp，大 16dp）

---

## 4. 色彩系统

### 动态取色
- Android 12+ 使用 `Monet` 取色引擎
- 自动从用户壁纸提取主色调
- 通过 `DynamicColors` API 应用

### 课程颜色
- 扩展预设调色板至 16 色（原 9 色）
- 使用 M3 tonal palette 算法：每个基色生成 5 级色阶
- 导入时轮询分配，用户可手动修改
- 深色模式下自动切换至对应 tonal dark 色

### 深色模式
- M3 原生 token 支持，light/dark 自动切换
- 继承现有日夜模式设置（跟随系统/强制亮/强制暗）

---

## 5. 去除服务器依赖

### 移除项

| 模块 | 文件 | 操作 |
|------|------|------|
| AppCenter SDK | `App.kt`, `build.gradle` | 删除 import 和初始化代码，删除依赖 |
| 使用统计 | `MyRetrofitUtils.addCount()` | 删除调用 |
| 版本更新检查 | `MyRetrofitUtils.getUpdateInfo()` | 删除整个检查逻辑 |
| 捐赠列表远程获取 | `DonateActivity.kt`, `DonateFragment.kt` | 改为本地硬编码数据 |
| 苏大生活模块 | `suda_life/` 整个包 | 删除目录 |
| QQ 客服链接 | `ScheduleActivity.kt` 中 3 处 `support.qq.com` | 替换为本地联系方式（QQ 号） |
| 简书帮助文档链接 | `ExportSettingsFragment.kt` | 删除或改为本地说明 |

### 保留项

- 教务系统登录和课程导入（用户主动发起的 HTTP 请求）
- Jsoup 解析（本地 HTML 解析，不依赖外部服务）
- Retrofit 依赖（教务导入模块的 HTTP 客户端仍需要）

### 联系方式

- 原作者 QQ 客服链接 → 替换为开发者自己的 QQ 号
- 放置位置：关于页面 + 侧边栏「帮助」入口

---

## 6. 代码清理

- 删除 `kotlin-android-extensions` 插件
  - XML 页面中改用 ViewBinding（`buildFeatures.viewBinding = true`）
  - Compose 页面不需要
- 清理 `splitties` 依赖（如仅用于 dip/dp 计算，可用 Compose 内置 `dp` 替代）
- 简化 product flavors 为单一 `defaultConfig`
- 签名配置引用 `local.properties`，不在代码中硬编码

---

## 7. 实施顺序

1. **工具链升级** — build.gradle、Gradle wrapper、依赖版本
2. **去服务器依赖** — 移除 AppCenter、更新检查、统计、QQ 链接
3. **M3 主题配置** — 更新主题、颜色、shape
4. **XML 页面 ViewBinding 迁移** — 替换 kotlin-android-extensions
5. **Compose 主课表重写** — ScheduleActivityUI + ScheduleUI → Compose
6. **课程颜色系统升级** — 16 色 tonal palette
7. **测试与验证** — 各功能回归测试

---

## 非目标

- 不迁移至 Jetpack Compose Navigation（保留 XML Navigation for 次要页面）
- 不引入 Hilt/Dagger 依赖注入（保持现有手动 ViewModel 创建）
- 不改数据模型（Room Entity 不变）
- 不移除教务导入功能的网络请求
