package io.github.lionseun.domain.request

data class LearnRequest(
    val size: Int,
    val last_learn: Int,
    val desc: Boolean,
    val expire: Int,
    val with_learn_count: Int,
    val prev: Int,
    val type: String,
    val sort: Int,
    val learn_status: Int,
)
