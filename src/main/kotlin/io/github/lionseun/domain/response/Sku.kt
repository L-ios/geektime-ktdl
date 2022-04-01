package io.github.lionseun.domain.response

data class SkuData(
    val list: List<Sku>,
)

data class Sku(
    val can_bind: Boolean,
    val column_ctime: Long,
    val column_groupbuy: Int,
    val column_price: Int,
    val column_price_first: Int,
    val column_price_market: Int,
    val column_sku: Int,
    val column_type: Int,
    val had_sub: Boolean,
    val id: Int,
    val in_pvip: Int,
    val is_channel: Int,
    val is_experience: Boolean,
    val is_real_sub: Boolean,
    val is_vip: Boolean,
    val last_aid: Int,
    val last_chapter_id: Int,
    val price_type: Int,
    val sub_count: Int,
    val top_level: Int
)
