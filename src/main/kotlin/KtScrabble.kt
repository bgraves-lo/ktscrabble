import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Stream

class KtScrabble constructor(wordFile: String) {
    private val topNode = TrieNode()
    private val boggleBoard = Boggle()

    init {
        val stream = Files.lines(Paths.get(wordFile))
        addWords(stream)
    }

    fun addWords(words: Stream<String>) {
        words.forEach { topNode.add(it) }
    }

    fun isWord(word: String): Boolean {
        return topNode.isWord(word)
    }

    fun findWords(letters: String): Set<String> {
        return topNode.findWords(letters)
    }

    fun boggleWords(letters: String): Set<String> {
        boggleBoard.setLetters(letters)
        return topNode.boggleWords(boggleBoard)
    }

}
