package io.github.lionseun

import com.fasterxml.jackson.core.type.TypeReference
import io.github.lionseun.client.HttpClient
import io.github.lionseun.domain.request.ArticlesRequest
import io.github.lionseun.domain.request.LearnRequest
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
        }
    }
    latch.await(3, TimeUnit.DAYS)
}
fun getProductIds(): List<Int>{
    var content = """
        {"label_id":0,"type":0}
    """.trimIndent()
    var resp = HttpClient().post("https://time.geekbang.org/serv/v3/lecture/list", content,
        object : TypeReference<GResponse<MinProductInfoData>>() {})
    return resp.data.list.filter { it.in_pvip == 1 }.reversed().map { it.pid }.toList()
}

fun downloader(workspace: String, product: ProductInfo) {
    logger.info("start download " + product.id + " ---> " + product.title)
    var productWorkspace = workspace + "/" + repaireDirName(product.title)
    var chapters = articles(product.id)
    saveProductIntro(productWorkspace, product, chapters.data.list)
    for (article2 in chapters.data.list) {
        var oneArticles = oneArticles(article2.id).data
        logger.info("start download " + product.id + " -> " + product.title + " -> " +oneArticles.id + " -> " + oneArticles.article_title)
        if (product.is_video) {
            var m3u8Url = ""
            if (oneArticles.hls_videos is Map<*, *>) {
                m3u8Url = (oneArticles.hls_videos as Map<String, Map<String, *>>).get("hd")!!.get("url").toString()
            }
            downloadVideo(productWorkspace, oneArticles, m3u8Url)
        } else {
            saveArticleContent2xHtml(productWorkspace, product, oneArticles)
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

private fun saveArticleContent2xHtml(workspace: String, product: ProductInfo, article: OneArticle ) {
    var fileName = repaireDirName(article.article_title)
    if (File("${workspace}/${fileName}.html").exists()) {
        logger.info("${fileName} has downloaded..")
        return
    }

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
    var fileIndex = AtomicInteger()

    document.select("img").forEach {
        val imgUrl = it.attr("src")
        // 文章名字 + index
        val downloaded = client.get(imgUrl)
        it.attr("src", "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(downloaded))
        it.attr("origin-src", imgUrl)
    }

    fileIndex.set(0)
    if (!is_video) {
        document.select("source").forEach {
            val mp3Url = it.attr("src")
            val downloaded = client.get(mp3Url)
            // 文章名字 + index
            it.attr("src", "data:audio/mpeg;base64," + Base64.getEncoder().encodeToString(downloaded))
            it.attr("origin-src", mp3Url)
        }
    }

    File(workspace).mkdirs()
    document.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
    File("${workspace}/${fileName}.html").writeText(document.html())
    logger.info("save to ${workspace}/${fileName}.html")
}

private fun productInfo(id: Int): ProductInfo {
    var response = HttpClient().post(
        "https://time.geekbang.org/serv/v3/column/info",
        "{\"product_id\":${id},\"with_recommend_article\":true}",
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
        "https://time.geekbang.org/serv/v1/column/articles",
        request,
        object : TypeReference<GResponse<Article2Data>>() {}
    )
}

private fun oneArticles(articleId: Int): GResponse<OneArticle> {
    return HttpClient().post(
        "https://time.geekbang.org/serv/v1/article",
        mapOf("id" to articleId),
        object : TypeReference<GResponse<OneArticle>>() {}
    )
}

private fun m3u8(url: String): String? {
    return HttpClient().getContent(url)
}

private fun repaireDirName(dirName: String): String {
    return dirName.trim().replace("|", "｜")
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


private fun downloadVideo(productWorkspace: String, article: OneArticle, m3u8Url: String) {
    val title = article.article_title
    // 使用全角对目录名进行修复 :*?"<>|
    var baseDir ="${productWorkspace}/${repaireDirName(title)}/"
    File(baseDir).mkdirs()
    var m3u8File = File(baseDir + "base.m3u8")
    if (m3u8File.exists()) {
        logger.info("${title} has downloaded..")
        return
    }

    var content = m3u8(m3u8Url) // 关键的url mp4的
    val baseUrl = m3u8Url.substring(0, m3u8Url.lastIndexOf("/")+1)
    val sb = StringBuilder()
    for (line in content!!.lines()) {
        if (line.endsWith(".ts")) {
            var content = HttpClient().get(baseUrl + line)
            File(baseDir +  line).writeBytes(content!!)
        } else if (line.contains("https")) {
            var url = line.substring(line.indexOf("\"")+ 1, line.length -1)
            var sec = HttpClient().get(url)
            File(baseDir + "aes.key").writeBytes(sec!!)
            sb.appendLine(line.replace(url, "./aes.key"))
            continue
        }
        sb.appendLine(line)
    }
    var htmlContent = videoContent(article)
    html2xhtml(baseDir, htmlContent, "index", true)
    m3u8File.writeText(sb.toString())
}
