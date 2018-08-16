class Boggle {
    val tiles = (1..16).map { Tile() }

    init {
        listOf(
            listOf(1,4,5),
            listOf(0,2,4,5,6),
            listOf(1,3,5,6,7),
            listOf(2,6,7),
            listOf(0,1,5,8,9),
            listOf(0,1,2,4,6,8,9,10),
            listOf(1,2,3,5,7,9,10,11),
            listOf(2,3,6,10,11),
            listOf(4,5,9,12,13),
            listOf(4,5,6,8,10,12,13,14),
            listOf(5,6,7,9,11,13,14,15),
            listOf(6,7,10,14,15),
            listOf(8,9,13),
            listOf(8,9,10,12,14),
            listOf(9,10,11,13,15),
            listOf(10,11,14)
        ).forEachIndexed { tileId, neighborIds ->
            tiles[tileId].neighbors = neighborIds.map { tiles[it] }
        }
    }

    fun setLetters(letters: String) {
        letters.forEachIndexed { id, letter ->
            tiles[id].letter = letter
        }
    }

    class Tile {
        var letter: Char = 'a'
        private var notVisited = true
        lateinit var neighbors: List<Tile>

        fun visit() { notVisited = false }
        fun reset() { notVisited = true }

        fun unvisitedNeighbors(): List<Tile> {
            return neighbors.filter { it.notVisited }
        }
    }
}
