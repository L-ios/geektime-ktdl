package io.github.lionseun

import com.fasterxml.jackson.core.type.TypeReference
import io.github.lionseun.client.HttpClient
import io.github.lionseun.domain.request.ArticlesRequest
import io.github.lionseun.domain.request.MinProductInfoData
import io.github.lionseun.domain.response.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.Logger

var logger = Logger.getLogger("geektime_dl")

fun main() {
    var workspace = "/data/EData/temp"
    var productIds = getProductIds()
    var threadpool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    var latch = CountDownLatch(productIds.size)
    for (productId in productIds.shuffled()) {
        threadpool.submit {
            logger.info("download ${productId}")
            var product = productInfo(productId)
            downloader(workspace, product)
            latch.countDown()
        File("${workspace}/downloaded").appendText("${productId}\n")
        }
    }
    latch.await(3, TimeUnit.DAYS)
}
fun getProductIds(): List<Int>{
    var content = """
            {"can_bind":0,"goods_name":"","label_id":0,"record_id":1405744,"type":0}
    """.trimIndent()
    var resp = HttpClient().post("https://apptime.geekbang.org/api/service/es/vip/column-label-skus", content,
        object : TypeReference<GResponse<SkuData>>() {})
    return resp.data.list.sortedBy { it.column_ctime }.reversed().map { it.column_sku }.toList()
//    return listOf(100008801)
}

fun downloader(workspace: String, product: ProductInfo) {
    logger.info("start download " + product.id + " ---> " + product.title)
    var productWorkspace = workspace + "/" + repaireDirName(product.title)
    var chapters = articles(product.id)
    saveProductIntro(productWorkspace, product, chapters.data.list)
    for (article2 in chapters.data.list) {
        logger.info("start download ${product.id} : ${product.title} -> ${article2.id} : ${article2.article_title}")
        if (product.is_video) {
            downloadVideo(productWorkspace, article2)
        } else {
            saveArticleContent2xHtml(productWorkspace, product, article2)
        }
    }
}

private fun saveProductIntro(workspace: String, product: ProductInfo, articles: List<Article2>) {
    val modules = product.extra?.modules
    val content = modules?.joinToString(separator = "\n<hr/>\n", transform = {
        """
          <h3>${it.title}</h3>
          <div>${it.content}</div>
          """.trimIndent()
    })
    var htmlContent = """
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="cn">
  <head>
    <meta charset="utf-8">
    <meta name="renderer" content="webkit">
    <title>${product.title} </title>
    <style>
      body {
        max-width: 800px;
        margin: 0 auto;
        font-size: 17px;
        line-height: 30px;
      }
      img {
        max-width: 100%;
      }
      audio {
        width: 100%;
      }
    </style>
  </head>
  <body>
      <h1>${product.title}</h1>
      <br/>
      <div>
      <h2>目录</h2>
      ${buildContentIndex(product, articles)}
      </div>
      <h1>简介</h1>
      ${content}
  </body>
</html>
    """.trimIndent()
    html2xhtml(workspace, htmlContent, "index")
}

private fun buildContentIndex(product: ProductInfo, articles: List<Article2>): String {
    if (product.is_video) {
        return articles.map {
            """<a href="${repaireDirName(it.article_title)}/index.html">${it.article_title}</a>"""
        }.joinToString("<br/>")
    } else {
        return articles.map {
            """<a href="${repaireDirName(it.article_title)}.html">${it.article_title}</a>"""
        }.joinToString("<br/>")
    }
}

private fun saveArticleContent2xHtml(workspace: String, product: ProductInfo, article2: Article2 ) {
    var fileName = repaireDirName(article2.article_title)
    if (File("${workspace}/${fileName}.html").exists()) {
        logger.info("${fileName} has downloaded..")
        return
    }
    var article = oneArticles(article2.id).data
    // xhtml 还有一些 img 结束符有问题
    var htmlContent = """
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="cn">
  <head>
    <meta charset="utf-8">
    <meta name="renderer" content="webkit">
    <title>${article.article_title} </title>
    <style>
      body {
        max-width: 800px;
        margin: 0 auto;
        font-size: 17px;
        line-height: 30px;
      }
      img {
        max-width: 100%;
      }
      audio {
        width: 100%;
      }
    </style>
  </head>
  <body>
      <h1>${article.article_title}</h1>
      <h5>${product.title}</h5>
      <br/>
      <img src="${article.article_cover}" alt="cover"/>
      <br/>
      <br/>
      <audio controls="controls">
          <source src="${article.audio_download_url}"/>
      </audio>
      <br/>
      <br/>
      ${article.article_content}
  </body>
</html>
    """.trimIndent()
    html2xhtml(workspace, htmlContent, fileName)
}

private fun html2xhtml(workspace: String, htmlContent: String, fileName: String, is_video: Boolean=false) {
    val client = HttpClient()
    val document = Jsoup.parse(htmlContent)

    document.select("img").forEach {
        val imgUrl = it.attr("src")
        if (!imgUrl.startsWith("https://")) {
            logger.warning("wrong url: ${imgUrl}")
            return
        }
        // 文章名字 + index
        val downloaded = client.get(imgUrl)
        it.attr("src", "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(downloaded))
        it.attr("origin-src", imgUrl)
    }

    var mp4FileIndex = AtomicInteger()
    var m3u8DirIndex = AtomicInteger()
    val mp4Dir = "${workspace}/mp4"
    val m3u8Dir = "${workspace}/m3u8"
    if (!is_video) {
        document.select("source").forEach {
            val mediaUrl = it.attr("src")
            if (mediaUrl.length == 0) {
                return
            }

            // 文章名字 + index
            if (mediaUrl.endsWith(".m3u8")) {
                File(m3u8Dir).mkdirs()
                val m3u8BaseDir = "${fileName}-${m3u8DirIndex.getAndIncrement()}"
                File("${m3u8Dir}/${m3u8BaseDir}").mkdirs()
                val m3u8FileName = "${m3u8BaseDir}/base.m3u8"
                logger.info("save to ${m3u8Dir}/${m3u8FileName}")
                // 需要解析m3u8
                downloadm3u8(mediaUrl, "${m3u8Dir}/${m3u8BaseDir}/")
                client.downloadFile(mediaUrl, "${m3u8Dir}/${m3u8FileName}")
                it.attr("src", "./m3u8/${m3u8FileName}")
            } else if (mediaUrl.endsWith(".mp4")) {
                File(mp4Dir).mkdirs()
                val mp4FileName = "${fileName}-${mp4FileIndex.getAndIncrement()}.mp4"
                logger.info("save to ${mp4Dir}/${mp4FileName}")
                client.downloadFile(mediaUrl, "${mp4Dir}/${mp4FileName}")
                it.attr("src", "./mp4/${mp4FileName}")
            } else if (mediaUrl.endsWith(".mp3")) {
                val downloaded = client.get(mediaUrl)
                it.attr("src", "data:audio/mpeg;base64," + Base64.getEncoder().encodeToString(downloaded))
            } else {
                logger.severe("url: ${mediaUrl} can not parse")
            }
            it.attr("origin-src", mediaUrl)
        }
        document.select("video").forEach {
            it.attr("width", "800")
        }
    }

    File(workspace).mkdirs()
    document.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
    File("${workspace}/${fileName}.html").writeText(document.html())
    logger.info("save to ${workspace}/${fileName}.html")
}

private fun productInfo(id: Int): ProductInfo {
    var response = HttpClient().post(
        "https://apptime.geekbang.org/api/time/serv/v3/column/info",
        mapOf("product_id" to id, "with_recommend_article" to true),
        object : TypeReference<GResponse<ProductInfo>>() {}
    )
    return response.data!!
}

private fun articles(productId: Int): GResponse<Article2Data> {
    var request = ArticlesRequest(
        cid = productId,
        size = 300,
        prev = 0,
        order = "earliest",
        sample = false,
    )

    return HttpClient().post(
        "https://apptime.geekbang.org/api/time/serv/v1/column/articles",
        request,
        object : TypeReference<GResponse<Article2Data>>() {}
    )
}

private fun oneArticles(articleId: Int): GResponse<OneArticle> {
    return HttpClient().post(
        "https://apptime.geekbang.org/api/time/serv/v1/article",
        mapOf(
            "id" to articleId,
            "include_neighbors" to true,
            "is_freelyread" to true,
            "reverse" to false
        ),
        object : TypeReference<GResponse<OneArticle>>() {}
    )
}

private fun m3u8(url: String): String? {
    return HttpClient().getContent(url)
}

private fun repaireDirName(dirName: String): String {
    return dirName.trim().replace("|", "｜")
        .replace("\t", " ")
        .replace(":", "：")
        .replace("*", "＊")
        .replace("?", "？")
        .replace("\"", "＂")
        .replace("<", "＜")
        .replace(">", "＞")
        .replace("/", "／")
}

private fun videoContent(article: OneArticle) : String{
    return """
        <!DOCTYPE html
          PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
        <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="cn">

        <head>
          <title>${article.article_title}</title>
          <link href="https://vjs.zencdn.net/7.18.1/video-js.css" rel="stylesheet" />
          <meta charset="utf-8">
          <meta name="renderer" content="webkit">
          <style>
            body {
              max-width: 800px;
              margin: 0 auto;
              font-size: 17px;
              line-height: 30px;
            }

            img {
              max-width: 100%;
            }
          </style>
        </head>

        <body>
          <h1>${article.article_title}</h1>
          <div>
            <video id="my-video" class="video-js" controls preload="auto" width="800px" data-setup="{}" controls>
              <source type="application/x-mpegURL" src="base.m3u8">
              <p class="vjs-no-js">
                To view this video please enable JavaScript, and consider upgrading to a
                web browser that
              </p>
            </video>
          </div>
          <h2>本节摘要</h2>
          <div>
            ${article.article_content}
          </div>
          <script src="https://vjs.zencdn.net/7.18.1/video.min.js"></script>
        </body>

        </html>
    """.trimIndent()
}


private fun downloadVideo(productWorkspace: String, article2: Article2) {
    val title = article2.article_title
    // 使用全角对目录名进行修复 :*?"<>|
    var baseDir ="${productWorkspace}/${repaireDirName(title)}/"
    File(baseDir).mkdirs()
    var m3u8File = File(baseDir + "base.m3u8")
    if (m3u8File.exists()) {
        logger.info("${title} has downloaded..")
        return
    }

    var article = oneArticles(article2.id).data
    if (article.hls_videos is Map<*, *>) {
        val m3u8Url = (article.hls_videos as Map<String, Map<String, *>>).get("hd")!!.get("url").toString()
        val m3u8Content = downloadm3u8(m3u8Url, baseDir)
        var htmlContent = videoContent(article)
        html2xhtml(baseDir, htmlContent, "index", true)
        m3u8File.writeText(m3u8Content)
    } else {
        logger.severe("cat not download video to ${title}")
    }
}

private fun downloadm3u8(m3u8Url: String, baseDir: String): String {
    var content = m3u8(m3u8Url) // 关键的url mp4的
    val baseUrl = m3u8Url.substring(0, m3u8Url.lastIndexOf("/") + 1)
    val sb = StringBuilder()
    for (line in content!!.lines()) {
        if (line.endsWith(".ts")) {
            var content = HttpClient().get(baseUrl + line)
            File(baseDir + line).writeBytes(content!!)
        } else if (line.contains("https")) {
            var url = line.substring(line.indexOf("\"") + 1, line.length - 1)
            var sec = HttpClient().get(url)

            File(baseDir + "aes.key").writeBytes(sec!!)
            sb.appendLine(line.replace(url, "./aes.key"))
            continue
        }
        sb.appendLine(line)
    }
    return sb.toString()
}
