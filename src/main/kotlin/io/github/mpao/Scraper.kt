package io.github.mpao

import okhttp3.OkHttpClient
import okhttp3.Request

abstract class Scraper(val baseurl: String){

    open var client = OkHttpClient.Builder().build()

    open fun getPageResponse(url: String): String?{
        return client.newCall(
            Request.Builder()
                .url(url)
                .build()
        ).execute().use { it.body?.string() }
    }

    abstract fun searchBand(name: String ): Band?
}