package io.github.lionseun.domain.response

data class Data(
    val articles: List<Article>,
    val has_expiring_product: Boolean,
    val page: Page,
    val products: List<Product>
)


data class Article(
    val chapter_id: Int,
    val chapter_title: String,
    val comment_count: Int,
    val content: String,
    val could_preview: Boolean,
    val cover_hidden: Boolean,
    val cshort: String,
    val ctime: Int,
    val float_app_qrcode: String,
    val float_qrcode: String,
    val float_qrcode_jump: String,
    val had_freelyread: Boolean,
    val id: Int,
    val in_pvip: Int,
    val is_required: Boolean,
    val is_video: Boolean,
    val pid: Int,
    val poster_wxlite: String,
    val score: Int,
    val share_title: String,
    val subtitle: String,
    val summary: String,
    val title: String,
    val type: Int,
    val video_could_preview: Boolean,
)

data class Page(
    val count: Int,
    val more: Boolean,
    val score: Int,
    val score0: Int
)

data class Product(
    val available_coupons: List<Int>,
    val begin_time: Long,
    val column_type: Int,
    val ctime: Long,
    val end_time: Long,
    val fav_qrcode: String,
    val id: Int, // 课程id
    val in_pvip: Int,
    val intro: String,
    val intro_html: String,
    val is_audio: Boolean,
    val is_column: Boolean,
    val is_core: Boolean,
    val is_dailylesson: Boolean,
    val is_finish: Boolean,
    val is_groupbuy: Boolean,
    val is_onborad: Boolean,
    val is_opencourse: Boolean,
    val is_promo: Boolean,
    val is_qconp: Boolean,
    val is_sale: Boolean,
    val is_shareget: Boolean,
    val is_sharesale: Boolean,
    val is_university: Boolean,
    val is_video: Boolean,
    val labels: List<Int>,
    val nav_id: Int,
    val spu: Int,
    val subtitle: String,
    val time_not_sale: Int,
    val title: String,
    val type: String,
    val ucode: String,
    val unit: String,
    val utime: Long
)

