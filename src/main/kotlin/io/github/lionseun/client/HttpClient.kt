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

open class HttpClient {
    open fun <R, S> post(url: String, body: R, respClass: TypeReference<S>): S {
        val requestBody: RequestBody =
            kotlinMapper.writeValueAsBytes(body)
                .toRequestBody("application/json".toMediaTypeOrNull())

        val request: Request = Request.Builder().url(url).headers(headers).post(requestBody).build()
        val response = client.newCall(request).execute()
        var content : String = response.body?.string()!!
        try {
//            var readTree = kotlinMapper.readTree(content)
            var readValue = kotlinMapper.readValue(content, respClass)
            if (readValue is GResponse<*>) {
                readValue.origin = kotlinMapper.readValue(content, Map::class.java)["data"]
            }
            return readValue
        } catch (e: JsonMappingException) {
            println(content)
            throw e;
        }
    }

    open fun getContent(url: String): String? {
        Request.Builder().url(url).build()
        var response = client.newCall(Request.Builder().url(url).build()).execute()
        return response.body?.string()
    }

    open fun get(url: String): ByteArray? {
        Request.Builder().url(url).build()
        val i = 3;
        while (true) {
            try {
                var response = client.newCall(Request.Builder().url(url).build()).execute()
                return response.body?.bytes()
            } catch (e: Exception) {
                if (i > 0) {
                    continue
                } else {
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
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.74 Safari/537.36 Edg/99.0.1150.46"
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
