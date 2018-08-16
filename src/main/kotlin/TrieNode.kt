import sun.text.normalizer.Trie

class TrieNode {
    private val children = mutableMapOf<Char, TrieNode>()
    private var isWord = false

    fun add(word: String) {
        add(word.toLowerCase().asIterable().iterator())
    }

    fun isWord(word: String): Boolean {
        return isWord(word.toLowerCase().asIterable().iterator())
    }

    fun findWords(letters: String): Set<String> {
        val sanitizedLetters = letters.toLowerCase().replace("[^a-z.]","")
        val words = mutableSetOf<String>()
        findWords(LetterBag(sanitizedLetters), words, "")
        return words
    }

    fun boggleWords(boggleBoard: Boggle): Set<String> {
        val words = mutableSetOf<String>()
        boggleBoard.tiles.forEach { tile ->
            boggleWords(tile, words, "")
        }
        return words
    }

    private fun add(letters: Iterator<Char>) {
        if (letters.hasNext()) {
            val letter = letters.next()
            val child = children.getOrPut(letter) { TrieNode() }
            child.add(letters)
        }
        else
            isWord = true
    }

    private fun isWord(letters: Iterator<Char>): Boolean {
        return if (letters.hasNext()) {
            val letter = letters.next()
            children[letter]?.isWord(letters) ?: false
        }
        else
            isWord
    }

    class LetterBag(letters: String) {
        private val letterMap = letters.groupingBy { it }
                .eachCount()
                .toMutableMap()

        fun add(letter: Char) {
            letterMap[letter] = letterMap[letter]?.inc() ?: 1
        }

        fun remove(letter: Char) {
            letterMap[letter]?.let { if (it > 0) letterMap[letter] = it.dec() }
        }

        fun currentSet(): Set<Char> {
            return letterMap.filterValues { it > 0 }.keys
        }
    }

    private fun findWords(letters: LetterBag, words: MutableSet<String>, word: String) {
        if (isWord) words.add(word)

        val currentLetters = letters.currentSet()
        if (currentLetters.isNotEmpty() && children.isNotEmpty()) {
            if (currentLetters.contains('.')) { // Deal with blank tiles
                letters.remove('.')
                children.forEach { it.value.findWords(letters, words, word + it.key) }
                letters.add('.')
            }
            children.filterKeys { currentLetters.contains(it) }
                    .forEach {
                        letters.remove(it.key)
                        it.value.findWords(letters, words, word + it.key)
                        letters.add(it.key)
                    }
        }
    }

    private fun boggleWords(tile: Boggle.Tile, words: MutableSet<String>, word: String) {
        if (isWord) words.add(word)

        val unvisitedNeighbors = tile.unvisitedNeighbors()

        if (unvisitedNeighbors.isNotEmpty() && children.isNotEmpty()) {
            tile.visit()
            unvisitedNeighbors.forEach { neighborTile ->
                val letter = neighborTile.letter
                children[letter]?.boggleWords(neighborTile, words, word + letter)
            }
            tile.reset()
        }
    }
}
