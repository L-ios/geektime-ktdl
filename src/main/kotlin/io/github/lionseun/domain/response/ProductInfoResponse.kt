package io.github.lionseun.domain.response

data class ProductInfo(
    val author: Author?,
    val begin_time: Long?,
    val bgcolor: String?,
    val column: Column?,
    val cover: Cover?,
    val ctime: Long?,
    val end_time: Long?,
    val extra: Extra?,
    val fav_qrcode: String?,
    val id: Int,
    val in_pvip: Int?,
    val is_audio: Boolean?,
    val is_core: Boolean?,
    val is_dailylesson: Boolean?,
    val is_finish: Boolean?,
    val is_groupbuy: Boolean?,
    val is_include_preview: Boolean?,
    val is_onborad: Boolean?,
    val is_opencourse: Boolean?,
    val is_preorder: Boolean?,
    val is_sale: Boolean?,
    val is_sale_product: Boolean?,
    val is_video: Boolean,
    val last_chapter_id: Int?,
    val nav_id: Int?,
    val nps_min: Int?,
    val path: Path?,
    val price: Price?,
    val recommend_articles: List<Any>?,
    val seo: Seo?,
    val share: Share?,
    val show_chapter: Boolean?,
    val study_service: List<Int>?,
    val subtitle: String?,
    val title: String,
    val total_length: Int?,
    val type: String?,
    val ucode: String?,
    val unit: String?,
    val update_frequency: String?,
    val utime: Long?
)



data class Author(
    val avatar: String?,
    val brief: String?,
    val brief_html: String?,
    val intro: String?,
    val name: String?
)

data class Column(
    val catalog_pic_url: String?,
    val hot_comments: List<HotComment>?,
    val hot_lines: List<HotLine>?,
    val in_rank: Boolean?,
    val intro_bg_style: Int?,
    val ranks: List<Rank>?
)

data class Cover(
    val color: String?,
    val horizontal: String?,
    val rectangle: String?,
    val square: String?,
    val transparent: String?
)

data class Extra(
    val any_read: AnyRead?,
    val cert: Cert?,
    val channel: Channel?,
    val cid: Int?,
    val fav: Fav?,
    val first_aids: List<Int>?,
    val first_award: FirstAward?,
    val first_promo: FirstPromo?,
    val group_buy: GroupBuy?,
    val helper: List<Helper>?,
    val modules: List<Module>?,
    val nps: Nps?,
    val select_comment_count: Int?,
    val sharesale: Sharesale?,
)


data class Path(
    val desc: String?,
    val desc_html: String?
)

data class Price(
    val end_time: Long?,
    val market: Int?,
    val promo_end_time: Int?,
    val sale: Int?,
    val sale_type: Int?,
    val start_time: Long?
)

data class Seo(
    val keywords: List<String>?
)

data class HotComment(
    val aid: Int?,
    val can_delete: Boolean?,
    val comment_content: String?,
    val comment_ctime: Int?,
    val comment_is_top: Boolean?,
    val had_liked: Boolean?,
    val id: Int?,
    val like_count: Int?,
    val product_id: Int?,
    val product_type: String?,
    val race_model: Int?,
    val replies: Any?,
    val score: Int?,
    val ucode: String?,
    val uid: Int?,
    val user_header: String?,
    val user_name: String?
)

data class HotLine(
    val aid: Int?,
    val from: String?,
    val note: String?,
    val product_id: Int?,
    val product_type: String?,
    val tips: String?,
    val uline_id: Int?,
    val user_count: Int?
)

data class Rank(
    val id: Int?,
    val name: String?,
    val score: Int?
)

data class AnyRead(
    val count: Int?,
    val total: Int?
)

data class Cert(
    val id: String?
)

data class Channel(
    val back_amount: Int?,
    val `is`: Boolean?
)

data class Fav(
    val count: Int?,
    val had_done: Boolean?
)

data class FirstAward(
    val amount: Int?,
    val expire_time: Int?,
    val reads: Int?,
    val redirect_param: String?,
    val redirect_type: String?,
    val show: Boolean?,
    val talks: Int?
)

data class FirstPromo(
    val could_join: Boolean?,
    val price: Int?
)

data class GroupBuy(
    val could_groupbuy: Boolean?,
    val had_join: Boolean?,
    val join_code: String?,
    val list: List<Any>?,
    val price: Int?,
    val success_ucount: Int?
)

data class Helper(
    val desc: String?,
    val icon: String?,
    val title: String?
)

data class Module(
    val content: String?,
    val is_top: Boolean?,
    val name: String?,
    val title: String?,
    val type: String?
)

data class Nps(
    val status: Int?,
    val url: String?
)

data class Sharesale(
    val amount: Int?,
    val `data`: String?,
    val `is`: Boolean?,
    val is_shareget: Boolean?,
    val max_amount: Int?,
    val title: String?
)

