import io.ktor.server.netty.Netty
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.server.engine.embeddedServer
import io.ktor.features.ContentNegotiation
import io.ktor.gson.*
import io.ktor.http.HttpStatusCode

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
                call.respond("Oh.  Hai.  Do '/find?letters=<my_sweet_letters>'.  Use a '.' for blank tiles\n\nOR, do '/boggle?letters=<my_16_letters>' to find some sweet boggle words.")
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
            get("/boggle") {
                val letters = call.parameters["letters"]
                if (letters?.length ?: 0 != 16)
                    call.respond(HttpStatusCode.BadRequest, "'letters' param must have 16 letters")
                else {
                    val words = letters?.let {
                        dictionary.boggleWords(letters).sorted().groupBy { it.length }.toSortedMap()
                    } ?: sortedMapOf()
                    call.respond(words)
                }
            }
        }
    }.start(wait = true)
}
