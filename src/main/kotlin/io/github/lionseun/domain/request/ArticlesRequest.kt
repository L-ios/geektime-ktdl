package io.github.lionseun.domain.request

data class ArticlesRequest(
    val cid: Int,
    val order: String,
    val prev: Int,
    val sample: Boolean,
    val size: Int
)