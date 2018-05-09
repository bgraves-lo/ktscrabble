import io.ktor.server.netty.Netty
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.server.engine.embeddedServer
import io.ktor.features.ContentNegotiation
import io.ktor.gson.*

fun main(args: Array<String>) {
    val dictionary = KtScrabble("assets/enable1.txt")

    embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }
        routing {
            get("/") {
                call.respond("Oh.  Hai.  Do '/find?letters=<my_sweet_letters>'.  Use a '.' for blank tiles")
            }
            get("/find") {
                val letters = call.parameters["letters"]
                val words = letters?.let {
                    dictionary.findWords(letters).groupBy { it.length }.toSortedMap()
                } ?: sortedMapOf()
                call.respond(words)
            }
            get("/check/{word}") {
                val word = call.parameters["word"] ?: ""
                val response = if (dictionary.isWord(word)) "'$word' is a word!" else "'$word' is not a word :("
                call.respond(response)
            }
        }
    }.start(wait = true)
}
