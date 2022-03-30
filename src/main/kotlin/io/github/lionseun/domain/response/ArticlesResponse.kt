package io.github.lionseun.domain.response

data class GResponse<T> (
    val code: Int,
    val `data`: T,
    val error: Any?,
    val extra: Any?,
    var origin: Any?
)

data class Article2Data(
    val list: List<Article2>,
    val page: Page
)

data class Article2(
    val article_could_preview: Boolean,
    val article_cover: String,
    val article_ctime: Int,
    val article_subtitle: String,
    val article_summary: String,
    val article_title: String,
    val chapter_id: String,
    val column_had_sub: Boolean,
    val column_sku: Int,
    val had_viewed: Boolean,
    val id: Int,
    val include_audio: Boolean,
    val is_finished: Boolean,
    val is_required: Boolean,
    val is_video_preview: Boolean,
    val offline: Offline,
    val rate_percent: Int,
    val score: Long,
    val subtitles: List<Any>,
    val video_cover: String?,
    val video_id: String?,
    val video_max_play_seconds: Int,
    val video_play_seconds: Int?,
    val video_play_utime: Int?,
    val video_size: Int?,
    val video_time: String?,
    val video_time_arr: VideoTimeArr?,
    val video_total_seconds: Int?
)

data class Offline(
    val download_url: String,
    val file_name: String
)

data class VideoTimeArr(
    val h: String,
    val m: String,
    val s: String
)

data class Video(
    val size: Int,
    val url: String?
)

fun testb() {
    """
{
    "code": 0,
    "error": [],
    "data": {
        "list": [
            {
                "article_could_preview": true,
                "article_cover": "",
                "article_ctime": 1549875000,
                "article_subtitle": "",
                "article_summary": "",
                "article_title": "01 | Spring课程介绍",
                "chapter_id": "201",
                "column_had_sub": true,
                "column_sku": 100023501,
                "had_viewed": false,
                "id": 81418,
                "include_audio": false,
                "is_finished": false,
                "is_required": true,
                "is_video_preview": true,
                "offline": {
                    "download_url": "",
                    "file_name": ""
                },
                "rate": {
                    "1": {
                        "cur_rate": 0,
                        "cur_version": 0,
                        "is_finished": false,
                        "learned_seconds": 0,
                        "max_rate": 0,
                        "total_rate": 0
                    },
                    "2": {
                        "cur_rate": 0,
                        "cur_version": 0,
                        "is_finished": false,
                        "learned_seconds": 0,
                        "max_rate": 0,
                        "total_rate": 0
                    },
                    "3": {
                        "cur_rate": 0,
                        "cur_version": 0,
                        "is_finished": false,
                        "learned_seconds": 0,
                        "max_rate": 0,
                        "total_rate": 0
                    }
                },
                "rate_percent": 0,
                "score": 11549875000,
                "subtitles": [],
                "video_cover": "https://static001.geekbang.org/resource/image/55/8a/551965b602363ece00d826b6cfa72b8a.jpg",
                "video_id": "162081a6df274502bfa2055f0749c0d6",
                "video_max_play_seconds": 0,
                "video_media_map": {
                    "hd": {
                        "size": 78343924
                    },
                    "ld": {
                        "size": 36933352
                    },
                    "sd": {
                        "size": 59340696
                    }
                },
                "video_play_seconds": 0,
                "video_play_utime": 0,
                "video_size": 74325939,
                "video_time": "00:04:46",
                "video_time_arr": {
                    "h": "00",
                    "m": "04",
                    "s": "46"
                },
                "video_total_seconds": 286
            },
            {
                "article_could_preview": true,
                "article_cover": "",
                "article_ctime": 1549875060,
                "article_subtitle": "",
                "article_summary": "",
                "article_title": "02 | 一起认识Spring家族的主要成员",
                "chapter_id": "201",
                "column_had_sub": true,
                "column_sku": 100023501,
                "had_viewed": false,
                "id": 80182,
                "include_audio": false,
                "is_finished": false,
                "is_required": true,
                "is_video_preview": true,
                "offline": {
                    "download_url": "",
                    "file_name": ""
                },
                "rate": {
                    "1": {
                        "cur_rate": 0,
                        "cur_version": 0,
                        "is_finished": false,
                        "learned_seconds": 0,
                        "max_rate": 0,
                        "total_rate": 0
                    },
                    "2": {
                        "cur_rate": 0,
                        "cur_version": 0,
                        "is_finished": false,
                        "learned_seconds": 0,
                        "max_rate": 0,
                        "total_rate": 0
                    },
                    "3": {
                        "cur_rate": 0,
                        "cur_version": 0,
                        "is_finished": false,
                        "learned_seconds": 0,
                        "max_rate": 0,
                        "total_rate": 0
                    }
                },
                "rate_percent": 0,
                "score": 11549875060,
                "subtitles": [],
                "video_cover": "https://media001.geekbang.org/a741c237c712488a80fec6c31e9e802a/snapshots/2070a0f44e764e9389314a8f7f549c9a-00005.jpg",
                "video_id": "a741c237c712488a80fec6c31e9e802a",
                "video_max_play_seconds": 0,
                "video_media_map": {
                    "hd": {
                        "size": 40777200
                    },
                    "ld": {
                        "size": 17985960
                    },
                    "sd": {
                        "size": 23588172
                    }
                },
                "video_play_seconds": 0,
                "video_play_utime": 0,
                "video_size": 242879925,
                "video_time": "00:08:27",
                "video_time_arr": {
                    "h": "00",
                    "m": "08",
                    "s": "27"
                },
                "video_total_seconds": 507
            },
            {
                "article_could_preview": true,
                "article_cover": "",
                "article_ctime": 1549875120,
                "article_subtitle": "",
                "article_summary": "",
                "article_title": "03 | 跟着Spring了解技术趋势",
                "chapter_id": "201",
                "column_had_sub": true,
                "column_sku": 100023501,
                "had_viewed": false,
                "id": 80187,
                "include_audio": false,
                "is_finished": false,
                "is_required": true,
                "is_video_preview": true,
                "offline": {
                    "download_url": "",
                    "file_name": ""
                },
                "rate": {
                    "1": {
                        "cur_rate": 0,
                        "cur_version": 0,
                        "is_finished": false,
                        "learned_seconds": 0,
                        "max_rate": 0,
                        "total_rate": 0
                    },
                    "2": {
                        "cur_rate": 0,
                        "cur_version": 0,
                        "is_finished": false,
                        "learned_seconds": 0,
                        "max_rate": 0,
                        "total_rate": 0
                    },
                    "3": {
                        "cur_rate": 0,
                        "cur_version": 0,
                        "is_finished": false,
                        "learned_seconds": 0,
                        "max_rate": 0,
                        "total_rate": 0
                    }
                },
                "rate_percent": 0,
                "score": 11549875120,
                "subtitles": [],
                "video_cover": "https://media001.geekbang.org/6af79e2eaa704e4280d06288efca5f0b/snapshots/59ce9d52d32c458b9bf63f1405ab76ad-00005.jpg",
                "video_id": "6af79e2eaa704e4280d06288efca5f0b",
                "video_max_play_seconds": 0,
                "video_media_map": {
                    "hd": {
                        "size": 31470636
                    },
                    "ld": {
                        "size": 14225772
                    },
                    "sd": {
                        "size": 18311952
                    }
                },
                "video_play_seconds": 0,
                "video_play_utime": 0,
                "video_size": 154257626,
                "video_time": "00:07:32",
                "video_time_arr": {
                    "h": "00",
                    "m": "07",
                    "s": "32"
                },
                "video_total_seconds": 452
            },
            {
                "article_could_preview": true,
                "article_cover": "",
                "article_ctime": 1549875180,
                "article_subtitle": "",
                "article_summary": "",
                "article_title": "04 | 编写你的第一个Spring程序",
                "chapter_id": "201",
                "column_had_sub": true,
                "column_sku": 100023501,
                "had_viewed": false,
                "id": 80189,
                "include_audio": false,
                "is_finished": false,
                "is_required": true,
                "is_video_preview": true,
                "offline": {
                    "download_url": "",
                    "file_name": ""
                },
                "rate": {
                    "1": {
                        "cur_rate": 0,
                        "cur_version": 0,
                        "is_finished": false,
                        "learned_seconds": 0,
                        "max_rate": 0,
                        "total_rate": 0
                    },
                    "2": {
                        "cur_rate": 0,
                        "cur_version": 0,
                        "is_finished": false,
                        "learned_seconds": 0,
                        "max_rate": 0,
                        "total_rate": 0
                    },
                    "3": {
                        "cur_rate": 0,
                        "cur_version": 0,
                        "is_finished": false,
                        "learned_seconds": 0,
                        "max_rate": 0,
                        "total_rate": 0
                    }
                },
                "rate_percent": 0,
                "score": 11549875180,
                "subtitles": [],
                "video_cover": "https://media001.geekbang.org/71f28f3616eb4748b0ce655734d241d2/snapshots/59a0e5a56a644c8b806b44e8130118c7-00005.jpg",
                "video_id": "71f28f3616eb4748b0ce655734d241d2",
                "video_max_play_seconds": 0,
                "video_media_map": {
                    "hd": {
                        "size": 46011496
                    },
                    "ld": {
                        "size": 19901304
                    },
                    "sd": {
                        "size": 27016540
                    }
                },
                "video_play_seconds": 0,
                "video_play_utime": 0,
                "video_size": 286697328,
                "video_time": "00:07:52",
                "video_time_arr": {
                    "h": "00",
                    "m": "07",
                    "s": "52"
                },
                "video_total_seconds": 472
            }
        ],
        "page": {
            "count": 4,
            "more": false
        }
    },
    "extra": []
}""".trimIndent()
}