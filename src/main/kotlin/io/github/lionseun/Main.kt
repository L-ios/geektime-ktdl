package io.github.lionseun

import com.fasterxml.jackson.core.type.TypeReference
import io.github.lionseun.client.HttpClient
import io.github.lionseun.domain.request.ArticlesRequest
import io.github.lionseun.domain.request.LearnRequest
import io.github.lionseun.domain.response.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


fun main() {
    var workspace = "/data/EData/temp"
    var productIds = getProductIds()
    var threadpool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    for (productId in productIds.shuffled()) {
        threadpool.submit {
            var product = productInfo(productId)
            downloader(workspace, product)
        }
    }
    threadpool.awaitTermination(3, TimeUnit.DAYS)
}
fun getProductIds(): List<Int>{
    var content = """
            {"can_bind":0,"goods_name":"","label_id":0,"record_id":1405744,"type":0}P

    """.trimIndent()
    var resp = HttpClient().post("https://apptime.geekbang.org/api/service/es/vip/column-label-skus", content,
        object : TypeReference<GResponse<SkuData>>() {})
    return resp.data.list.sortedBy { it.column_ctime }.reversed().map { it.column_sku }.toList()
//    return listOf(100023501)
}

fun downloader(workspace: String, product: ProductInfo) {
    println("start download " + product.id + " ---> " + product.title)
    var productWorkspace = workspace + "/" + repaireDirName(product.title)
    saveProductIntro(productWorkspace, product)
    var chapters = articles(product.id)
    for (article2 in chapters.data.list) {
        var oneArticles = oneArticles(article2.id).data
        println("start download " + product.id + " -> " + product.title + " -> " +oneArticles.id + " -> " + oneArticles.article_title)
        if (product.is_video) {
            var m3u8Url = ""
            if (oneArticles.hls_videos is Map<*, *>) {
                m3u8Url = (oneArticles.hls_videos as Map<String, Map<String, *>>).get("sd")!!.get("url").toString()
            }
            downloadVideo(productWorkspace, oneArticles.article_title, m3u8Url)
        } else {
            saveArticleContent2xHtml(productWorkspace, product, oneArticles)
        }
    }
}

private fun saveProductIntro(workspace: String, product: ProductInfo) {
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
    <title>${product.title} </title>
  </head>
  <body>
      <h1>${product.title}</h1>
      <br/>
      <h1>简介</h1>
      ${content}
  </body>
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
</html>
    """.trimIndent()
    html2xhtml(workspace, htmlContent, "index")
}

private fun saveArticleContent2xHtml(workspace: String, product: ProductInfo, article: OneArticle ) {
    var fileName = repaireDirName(article.article_title)
    if (File("${workspace}/${fileName}.xhtml").exists()) {
        println("${fileName} has downloaded..")
        return
    }

    // xhtml 还有一些 img 结束符有问题
    var htmlContent = """
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="cn">
  <head>
    <title>${article.article_title} </title>
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
</html>
    """.trimIndent()
    html2xhtml(workspace, htmlContent, fileName)
}

private fun html2xhtml(workspace: String, htmlContent: String, fileName: String) {
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
    document.select("source").forEach {
        val mp3Url = it.attr("src")
        val downloaded = client.get(mp3Url)
        // 文章名字 + index
        it.attr("src", "data:audio/mpeg;base64," + Base64.getEncoder().encodeToString(downloaded))
        it.attr("origin-src", mp3Url)
    }

    File(workspace).mkdirs()
    document.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
    println("save to ${workspace}/${fileName}.xhtml")
    File("${workspace}/${fileName}.xhtml").writeText(document.html())
}

private fun productInfo(id: Int): ProductInfo {
    var response = HttpClient().post(
        "https://apptime.geekbang.org/api/time/serv/v3/column/info",
        "{\"product_id\":${id},\"with_recommend_article\":true}\n",
        object : TypeReference<GResponse<ProductInfo>>() {}
    )
    return response.data!!
}


private fun product(): GResponse<Data> {
    var reqeust = LearnRequest(
        size = 10,
        last_learn = 1,

        desc = true,
        expire = 1,
        with_learn_count = 1,
        prev = 0,
        type = "",
        sort = 1,
        learn_status = 0,
    )
    var resp = HttpClient().post(
        "https://apptime.geekbang.org/api/time/serv/v3/learn/product",
        reqeust,
        object : TypeReference<GResponse<Data>>() {}
    )
    return resp
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

private fun downloadVideo(productWorkspace: String, title: String, m3u8Url: String) {
    // 使用全角对目录名进行修复 :*?"<>|
    var baseDir ="${productWorkspace}/${repaireDirName(title.replace(" |", ""))}/"
    File(baseDir).mkdirs()
    var m3u8File = File(baseDir + "base.m3u8")
    if (m3u8File.exists()) {
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

    m3u8File.writeText(sb.toString())
}
