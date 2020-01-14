package io.github.mpao

import io.github.mpao.metal.MetalArchiveScraper
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MetalArchiveTests{

    private val metalArchiveScraper: Scraper = MetalArchiveScraper()

    @Test
    fun mockBandScraper(){
        val band: Band = object: Band(){
            override val name: String
                get() = "Nazgul"
            override var lastAlbum: Album? = object: Album(){
                    override val title: String
                        get() = "De Expugnatione Elfmuth"
                    override val year: Int
                        get() = 2002
                }
        }
        println(band.toString())
        assertTrue(band.toString() == "Nazgul: De Expugnatione Elfmuth, 2002")
    }

    @Test
    fun noNameSpecified(){
        val exception = assertThrows<Exception> { metalArchiveScraper.searchBand("") }
        assertTrue(exception.message?.contains("a name is needed") == true)
    }

    @Test
    fun singleBandResultOnMetalArchive(){
        val band = metalArchiveScraper.searchBand("emyn muil")
        println(band?.toString())
        assertTrue(band != null)
    }

    @Test
    fun bandNotFoundOnMetalArchive(){
        val band = metalArchiveScraper.searchBand("orietta berti")
        assertTrue(band != null)
        assertTrue(band?.toString() == "ORIETTA BERTI is not present on Metal Archive; are you sure you spelled it correctly?")
    }

    @Test
    fun multipleResultsOnMetalArchive(){
        val band = metalArchiveScraper.searchBand("draugr")
        assertTrue(band != null)
        assertTrue(band?.toString() == "DRAUGR non-unique result: you must specify the ID next to the name in the form name::id")
    }

    @Test
    fun singleBandWithIDOnMetalArchive(){
        val band = metalArchiveScraper.searchBand("draugr::14632")
        assertTrue(band != null)
        println(band?.toString())
        assert(true)
    }

}