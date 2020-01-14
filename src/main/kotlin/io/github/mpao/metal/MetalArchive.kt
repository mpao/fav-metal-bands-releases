package io.github.mpao.metal

import io.github.mpao.Album
import io.github.mpao.Band
import io.github.mpao.Scraper
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import java.lang.Exception

data class MetalArchiveBand(
    val id: Long,
    override val name: String,
    val genre: String,
    val status: String,
    val country: String
): Band(){
    override var lastAlbum: Album? = null
    override fun toString() = "$name ($status): ${lastAlbum?.let { "${it.title}, ${it.year}" }?: "---"}"
}

data class MetalArchiveBandNotFound(override val name: String): Band() {
    override var lastAlbum: Album? = null
    override fun toString() = "${name.toUpperCase()} is not present on Metal Archive; are you sure you spelled it correctly?"
}

data class MetalArchiveNameOverload(override val name: String): Band() {
    override var lastAlbum: Album? = null
    override fun toString() = "${name.toUpperCase()} non-unique result: you must specify the ID next to the name in the form name::id"
}

data class MetalArchiveAlbum(
    override val title: String,
    override val year: Int,
    val type: String
): Album()

class MetalArchiveScraper: Scraper("https://www.metal-archives.com/search/ajax-band-search/"){
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun searchBand(search: String): Band? {
        if(search.isEmpty()) throw Exception("a name is needed")

        val page = if (search.contains("::")){
            getPageResponse("$baseurl?field=id&query=${search.split("::").last()}")
        } else getPageResponse("$baseurl?field=name&query=$search")

        return page?.let {
            val json = JSONObject(it).getJSONArray("aaData")
            when(json.length()){
                0 -> MetalArchiveBandNotFound(search)
                1 -> parseSingleEntry(json)
                else -> MetalArchiveNameOverload(search)
            }

        }?: throw Exception("something went wrong")
    }

    private fun parseSingleEntry(json: JSONArray): Band?{
        val url = Jsoup.parse((json[0] as JSONArray)[0].toString()).selectFirst("a").attr("href")
        val name = Jsoup.parse((json[0] as JSONArray)[0].toString()).selectFirst("a").html()
        val id = url.split("/").last().toLong()
        val genre = (json[0] as JSONArray)[1].toString()
        val country = (json[0] as JSONArray)[2].toString()
        val infoPage = client.newCall(
            Request.Builder()
                .url(url)
                .build()
        ).execute().use { it.body?.string() }
        val htmlBand = Jsoup.parse(infoPage)
        val status = htmlBand.selectFirst("dd[class]").html()

        return MetalArchiveBand(
            id = id,
            name = name,
            genre = genre,
            status = status,
            country = country
        ).apply {
            val albumPage = client.newCall(
                Request.Builder()
                    .url("https://www.metal-archives.com/band/discography/id/$id/tab/all")
                    .build()
            ).execute().use { it.body?.string() }
            val htmlAlbum = Jsoup.parse(albumPage)
            val xml = htmlAlbum.select("tr").last().select("td")
            this.lastAlbum = MetalArchiveAlbum(
                title = xml[0].select("a").html(),
                year = xml[2].html().toInt(),
                type = xml[1].html()
            )
        }

    }

}