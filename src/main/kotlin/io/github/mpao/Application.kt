package io.github.mpao

import io.github.mpao.metal.MetalArchiveScraper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main() {

    val list = listOf(
        "Nazgul::7829",
        "draugr",
        "orietta berti",
        "emyn muil",
        "atavicus",
        "selvans"
    )

    runBlocking {
        val time = measureTimeMillis {
            list.map {
                async(Dispatchers.IO) {
                    MetalArchiveScraper().searchBand(it)
                }
            }.forEach {
                println(it.await())
            }
        }
        println("Completed in $time ms")
    }

}

