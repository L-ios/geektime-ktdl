package io.github.lionseun.domain.response

data class OneArticle(
    val article_content: String,
    val article_could_preview: Boolean,
    val article_cover: String,
    val article_cover_hidden: Boolean,
    val article_cshort: String,
    val article_ctime: Int,
    val article_features: Int,
    val article_poster_wxlite: String,
    val article_sharetitle: String,
    val article_subtitle: String,
    val article_summary: String,
    val article_title: String,
    val audio_download_url: String,
    val audio_dubber: String,
    val audio_md5: String,
    val audio_size: Int,
    val audio_time: String,
    val audio_title: String,
    val audio_url: String,
    val author_name: String,
    val chapter_id: String,
    val cid: Int,
    val column_could_sub: Boolean,
    val column_had_sub: Boolean,
    val column_id: Int,
    val column_is_experience: Boolean,
    val column_is_onboard: Boolean,
    val column_sale_type: Int,
    val comment_count: Int,
    val float_app_qrcode: String,
    val float_qrcode: String,
    val float_qrcode_jump: String,
    val footer_cover_data: FooterCoverData,
    val free_get: Boolean,
    val had_liked: Boolean,
    val had_viewed: Boolean,
    val hls_videos: Any?,
    val id: Int,
    val in_pvip: Int,
    val is_finished: Boolean,
    val is_required: Boolean,
    val is_video_preview: Boolean,
    val like: Like,
    val like_count: Int,
    val offline: Offline,
    val offline_package: String,
    val product_id: Int,
    val product_type: String,
    val rate_percent: Int,
    val score: String,
    val share: Share,
    val sku: String,
    val subtitles: List<Any>,
    val text_read_percent: Long,
    val text_read_version: Int,
    val video_cover: String?,
    val video_height: Int?,
    val video_id: String?,
    val video_max_play_seconds: Int?,
    val video_play_seconds: Int?,
    val video_play_utime: Int?,
    val video_preview: VideoPreview?,
    val video_preview_second: Int?,
    val video_size: Int?,
    val video_time: String?,
    val video_time_arr: VideoTimeArr?,
    val video_total_seconds: Int?,
    val video_width: Int?
)

data class FooterCoverData(
    val img_url: String,
    val link_url: String,
    val mp_url: String
)

data class Like(
    val count: Int,
    val had_done: Boolean
)

data class Share(
    val content: String,
    val cover: String,
    val poster: String,
    val title: String
)

data class VideoPreview(
    val sd: SdXX
)

data class SdXX(
    val size: Int,
    val url: String
)