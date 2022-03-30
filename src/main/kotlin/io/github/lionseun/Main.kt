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
import java.util.concurrent.atomic.AtomicInteger


fun main() {
    var workspace = "d:/temp"
    var threadpool = Executors.newFixedThreadPool(4);
    for (productId in listOf<Int>(
        100104501,100104601,100104301,100103401,100102601,100101501,100101301,100100901,100100701,100099801,100098801,
        100098901,100097301,100095401,100094901,100093001,100094401,100093501,100093301,100091501,100091101,100109601,
        100109401,100109201,100108401,100107801,100105701,100104701,100090601,100090001,100085501,100085201,100085301,
        100085401,100085101,100084801,100084301,100084201,100083301,100082501,100082101,100081901,100081501,100080901,
        100079601,100079901,100079201,100079101,100078501,100078401,100077001,100076701,100076501,100075401,100074001,
        100073401,100073301,100073201,100072201,100072001,100071001,100070901,100070801,100069901,100070001,100069101,
        100068401,100067701,100066601,100066301,100065501,100064501,100064801,100063801,100063601,100062901,100062401,
        100062001,100061801,100061901,100061401,100060801,100060601,100060501,100059901,100059201,100058801,100058401,
        100058001,100057701,100057401,100056701,100056401,100056201,100055801,100055601,100055001,100053901,100053801,
        100053601,100053301,100053201,100052801,100052601,100052401,100052201,100051901,100051801,100051201,100050701,
        100050201,100050101,100049401,100049101,100048401,100048201,100048001,100047701,100046901,100046801,100046401,
        100046301,100046201,100046101,100045801,100044601,100044301,100044201,100085801,100043901,100043001,100042601,
        100042501,100041701,100041101,100040501,100040201,100040001,100039701,100039001,100038501,100038001,100037701,
        100037301,100036701,100036601,100036501,100036401,100036001,100035801,100035501,100034901,100034501,100034201,
        100034101,100034001,100033601,100032701,100032301,100032201,100031801,100031401,100031101,100031001,100030701,
        100030501,100029601,100029501,100029201,100029001,100028901,100028301,100028001,100027801,100027701,100026901,
        100026801,100026001,100025901,100025301,100025201,100025001,100024701,100024601,100024501,100024001,100023901,
        100023701,100023501,100023401,100023201,100023001,100019601,100022301,100021701,100021601,100021201,100021101,
        100020901,100020801,100020301,100020201,100020001,100019701,100017501,100017301,100015201,100014401,100014301,
        100013101,100012101,100012001,100010301,100009801,100009701,100009601,100009301,100008801,100008701,100007201,
        100007101,100007001,100006701,100006601,100006501,100006201,100005701,100005101,100003901,100003401,100003101,
        100002601,100002401,100002201,100002101,100001901
    )) {
        var product = productInfo(productId)
        // extra -> modules type(activity)

        downloader(workspace, product)
    }
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
            downloadVideo(product.title, oneArticles.article_title, m3u8Url)
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
    var fileName = repaireDirName(article.article_title)
    html2xhtml(workspace, htmlContent, fileName)
}

private fun html2xhtml(workspace: String, htmlContent: String, fileName: String) {
    val client = HttpClient()
    val imgDir = workspace + "/Images"
    File(imgDir).mkdirs()
    val audioDir = workspace + "/Audios"
    File(audioDir).mkdirs()
    val document = Jsoup.parse(htmlContent)
    var fileIndex = AtomicInteger()

    document.select("img").forEach {
        val imgUrl = it.attr("src")
        // 文章名字 + index
        val downloaded = client.get(imgUrl)
        it.attr("src", "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(downloaded))
        it.attr("origin-src", imgUrl)
        val imgFileName = "${fileName}-${fileIndex.getAndIncrement()}.jpeg"
        println("save to ${imgDir}/${imgFileName}")
        File("${imgDir}/${imgFileName}").writeBytes(downloaded!!)
        it.attr("local-src", "./Images/${imgFileName}")
    }

    fileIndex.set(0)
    document.select("source").forEach {
        val mp3Url = it.attr("src")
        val downloaded = client.get(mp3Url)
        // 文章名字 + index
        it.attr("src", "data:audio/mpeg;base64," + Base64.getEncoder().encodeToString(downloaded))
        it.attr("origin-src", mp3Url)
        val audioFileName = "${fileName}-${fileIndex.getAndIncrement()}.mp3"
        println("save to ${audioDir}/${audioFileName}")
        File("${audioDir}/${audioFileName}").writeBytes(downloaded!!)
        it.attr("local-src", "./Audios/${audioFileName}")
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
    return dirName.replace("|", "｜")
        .replace(":", "：")
        .replace("*", "＊")
        .replace("?", "？")
        .replace("\"", "＂")
        .replace("<", "＜")
        .replace(">", "＞")
        .replace("/", "／")
}

private fun downloadVideo(productName: String, title: String, m3u8Url: String) {
    // 使用全角对目录名进行修复 :*?"<>|
    var baseDir = "d:/temp/" + productName + "/" + repaireDirName(title.replace(" |", "")) + "/"
    File(baseDir).mkdirs()
    var m3u8File = File(baseDir + "base.m3u8")
    if (m3u8File.exists() || true) {
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
