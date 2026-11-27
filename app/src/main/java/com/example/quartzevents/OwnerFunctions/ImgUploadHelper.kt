package com.example.quartzevents

import android.content.Context
import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

suspend fun uploadToImgBB(context: Context, imageUri: Uri): String? = withContext(Dispatchers.IO) {
    try {
        val bytes = context.contentResolver.openInputStream(imageUri)?.readBytes() ?: return@withContext null

        val formBody = FormBody.Builder()
            .add("key", "25585fbb1314fd70c360600a0cccd574")
            .add("image", Base64.encodeToString(bytes, Base64.NO_WRAP))
            .build()

        val request = Request.Builder()
            .url("https://api.imgbb.com/1/upload")
            .post(formBody)
            .build()

        val response = OkHttpClient().newCall(request).execute()

        if (response.isSuccessful) {
            val json = JSONObject(response.body?.string() ?: "")
            return@withContext json.getJSONObject("data").getString("url")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return@withContext null
}