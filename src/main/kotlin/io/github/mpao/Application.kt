package io.github.mpao

import io.github.mpao.metal.MetalArchiveScraper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

fun main() {

    val list = listOf(
        "Nazgul::7829",
        "draugr::14632",
        "orietta berti",
        "emyn muil",
        "atavicus",
        "selvans"
    )

    runBlocking {
        list.asSequence().map {
            async(Dispatchers.IO) {
                MetalArchiveScraper().apply { client = ProxyClient().client }.searchBand(it)
            }
        }.forEach {
            println(it.await())
        }
    }

}

