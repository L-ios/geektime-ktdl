package io.github.lionseun.client

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.github.lionseun.domain.response.GResponse
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

open class HttpClient {

    var logger = Logger.getLogger("HttpClient")

    open fun <R, S> post(url: String, body: R, respClass: TypeReference<S>): S {
        var requestBody: RequestBody
        if (body is String) {
            requestBody = body.toRequestBody("application/json".toMediaTypeOrNull())
        } else {
            requestBody = kotlinMapper.writeValueAsBytes(body).toRequestBody("application/json".toMediaTypeOrNull())
        }

        var request: Request = Request.Builder().url(url).headers(headers).post(requestBody).build()
        var content = ""
        var retry = 0;
        var retryMap = mapOf(
            0 to 0L,
            1 to 1L,
            2 to 10L,
            3 to 30L,
            4 to 60L,
        )
        var random = Random()
        while (true) {
            val response = client.newCall(request).execute()
            content = response.body?.string()!!
            if (content.trim().length != 0) {
                break
            }
            logger.warning("---------- has error code: ${response.code} , sleep ${retryMap.get(retry)} content: ${content}")

            var header = Headers.Builder().addAll(headers).add("User-Agent",
                "PostmanRuntime/7.${random.nextInt(1000)}.${random.nextInt(100)}").build()
            request = Request.Builder().url(url).headers(header).post(requestBody).build()
            TimeUnit.SECONDS.sleep(retryMap.get(retry++)!!)
            retry %= 5
        }
        try {
//            var readTree = kotlinMapper.readTree(content)
            var readValue = kotlinMapper.readValue(content, respClass)
            if (readValue is GResponse<*>) {
                readValue.origin = kotlinMapper.readValue(content, Map::class.java)["data"]
            }
            return readValue
        } catch (e: JsonMappingException) {
            logger.severe("----------------------------------------------has json mapping exception ")
            throw e
        }
    }

    open fun getContent(url: String): String? {
        Request.Builder().url(url).build()
        var response = client.newCall(Request.Builder().url(url).build()).execute()
        return response.body?.string()
    }

    open fun get(url: String): ByteArray? {
        logger.info("download url: ${url}")
        var i = 30
        var exc: Exception = Exception("download failed")
        while (i >= 0) {
            i--
            try {
                var response = client.newCall(Request.Builder().url(url).build()).execute()
                return response.body?.bytes()
            } catch (e: Exception) {
                exc = e
                if (i < 0) {
                    throw e;
                }
            }
        }
        throw exc
    }

    open fun downloadFile(url: String, filePath: String) {
        logger.info("download url: ${url} to file: ${filePath}")
        var i = 30
        while (i >= 0) {
            i--
            try {
                File(filePath).deleteOnExit()
                var response = client.newCall(Request.Builder().url(url).build()).execute()
                var byteStream = response.body!!.byteStream()
                var outputStream = FileOutputStream(filePath)

                val buff = ByteArray(1024 * 4)
                while (true) {
                    val readed = byteStream.read(buff)
                    if (readed == -1) {
                        break
                    }
                    outputStream.write(buff, 0, readed)
                }
                return
            } catch (e: Exception) {
                if (i < 0) {
                    throw e;
                }
            }
        }
    }

    companion object {
        private val client: OkHttpClient = buildClient()
        private val kotlinMapper: ObjectMapper = buildRespMapper()

        fun buildClient(): OkHttpClient {
            return OkHttpClient().newBuilder().build()
        }

        fun buildRespMapper(): ObjectMapper {
            val kotlinModule: KotlinModule = KotlinModule.Builder()
                .configure(KotlinFeature.NullToEmptyCollection, true)
                .configure(KotlinFeature.NullToEmptyMap, true)
                .configure(KotlinFeature.NullIsSameAsDefault, false)
                .configure(KotlinFeature.SingletonSupport, true)
                .configure(KotlinFeature.StrictNullChecks, false)
                .build()
            return ObjectMapper()
                .registerModule(kotlinModule)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

        val headers: Headers
            get() = Headers.Builder()
                .add(
                    "User-Agent",
                    "Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.91 Mobile Safari/537.36 Edg/101.0.495"
                )
                .add("Accept", "application/json, text/plain, */*")
                .add("Origin", "https://wxtime.geekbang.org")
                .add(
                    "Cookie",
                    """
                        LF_ID=1644589747652-5436158-2490103; 
                        GCID=875b4bd-62bc2ff-6e162f7-8d2c300; 
                        GRID=875b4bd-62bc2ff-6e162f7-8d2c300; 
                        ECID=27fe820-67528c9-6d1dafe-fd083d6; 
                        ERID=27fe820-67528c9-6d1dafe-fd083d6; 
                        gksskpitn=94600883-e704-49b3-96b6-61f198e3ed8f; 
                        GCESS=BgsCBgAJAQ0HBIzJv0YNAQIFBAAAAAAMAQEBCNYULQAAAAAAAgSfuUFiCgQAAAAABAQALw0ABgS_8o7kAwSfuUFiCAED; 
                    """.trimIndent().replace("\n", "")
                // GCESS 是验证用的
                ).build()
    }
}
