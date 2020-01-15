package io.github.mpao

import io.github.mpao.metal.MetalArchiveScraper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {

    val list = File(args[0]).useLines {
        it.toList()
    }

    runBlocking {
        val time = measureTimeMillis {
            list.asSequence().map {
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

