package io.github.mpao

abstract class Band() {
    abstract val name: String
    abstract var lastAlbum: Album?
    override fun toString() = "$name: ${lastAlbum?.let { "${it.title}, ${it.year}" }?: "---"}"
}

abstract class Album {
    abstract val title: String
    abstract val year: Int
}