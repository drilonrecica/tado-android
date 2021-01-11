package dev.recica.drilon.tadoandroid.internal

import android.util.Log
import dev.recica.drilon.tadoandroid.BuildConfig
import dev.recica.drilon.tadoandroid.TadoException
import dev.recica.drilon.tadoandroid.internal.Constants.JSON_KEY_CODE
import dev.recica.drilon.tadoandroid.internal.Constants.JSON_KEY_CONTENT_TYPE_VALUE
import dev.recica.drilon.tadoandroid.internal.Constants.JSON_KEY_ERRORS
import dev.recica.drilon.tadoandroid.internal.Constants.JSON_KEY_TITLE
import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject

internal object RequestUtils {
    @Throws(TadoException::class)
    fun checkException(json: JSONObject) {
        if (json.has(JSON_KEY_ERRORS)) {
            val errorsJson = json.getJSONArray(JSON_KEY_ERRORS)
            val errorJson = errorsJson.getJSONObject(0)
            throw TadoException(
                errorJson.optString(JSON_KEY_CODE), errorJson.optString(
                    JSON_KEY_TITLE
                )
            )
        }
    }

    fun debugMessage(printDebugResponses: Boolean, message: String) {
        if (printDebugResponses && BuildConfig.DEBUG) {
            Log.d("[TADO_ANDROID_DEBUG]", message)
        }
    }

    @Throws(IOException::class)
    fun doGetRequest(
        client: OkHttpClient,
        url: String,
        headers: Map<String, String>?
    ): String? {
        val request: Request =
            if (headers != null) Request.Builder().url(url).headers(headers.toHeaders())
                .build() else Request.Builder().url(url).build()
        val response: Response = client.newCall(request).execute()
        return response.body?.string()
    }

    @Throws(IOException::class)
    fun doPostRequest(
        client: OkHttpClient,
        url: String,
        body: Map<String, String>,
        headers: Map<String, String>?
    ): String? {
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        for ((key, value) in body) {
            builder.addFormDataPart(key, value)
        }
        val formBody: MultipartBody = builder.build()
        val request: Request
        request =
            if (headers != null) Request.Builder().url(url).post(formBody)
                .headers(headers.toHeaders())
                .build() else Request.Builder().url(url).post(formBody).build()
        val response: Response = client.newCall(request).execute()
        return response.body?.string()
    }

    @Throws(IOException::class)
    fun doPutRequest(
        client: OkHttpClient,
        url: String,
        jsonBody: String,
        headers: Map<String, String>?
    ): String? {
        val mediaType: MediaType? = JSON_KEY_CONTENT_TYPE_VALUE.toMediaTypeOrNull()
        val body: RequestBody = jsonBody.toRequestBody(mediaType)
        val request: Request
        request =
            if (headers != null) Request.Builder().url(url).put(body)
                .headers(headers.toHeaders())
                .build() else Request.Builder().url(url).put(body).build()
        val response: Response = client.newCall(request).execute()
        return response.body?.string()
    }

    @Throws(IOException::class)
    fun doDeleteRequest(
        client: OkHttpClient,
        url: String,
        headers: Map<String, String>?
    ): String? {
        val request: Request =
            if (headers != null) Request.Builder().url(url).delete()
                .headers(headers.toHeaders())
                .build() else Request.Builder().url(url).delete().build()
        val response: Response = client.newCall(request).execute()
        return response.body?.string()
    }
}