import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*
import io.ktor.http.*
import org.slf4j.event.Level

fun main() {
    val dictionary = KtScrabble("assets/enable1.txt")

    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }

        install(CallLogging) {
            level = Level.INFO
        }

        routing {
            get("/") {
                call.respondText("Oh.  Hai.  Do '/find?letters=<my_sweet_letters>'.  Use a '.' for blank tiles\n\nOR, do '/boggle?letters=<my_16_letters>' to find some sweet boggle words.")
            }
            get("/find") {
                val letters = call.request.queryParameters["letters"]
                val words = letters?.let {
                    dictionary.findWords(letters).groupBy { it.length }.toSortedMap()
                } ?: sortedMapOf()
                call.respond(words)
            }
            get("/check/{word}") {
                val word = call.parameters["word"] ?: ""
                val response = if (dictionary.isWord(word)) "'$word' is a word!" else "'$word' is not a word :("
                call.respondText(response)
            }
            get("/boggle") {
                val letters = call.request.queryParameters["letters"]
                if (letters?.length ?: 0 != 16)
                    call.respond(HttpStatusCode.BadRequest, "'letters' param must have 16 letters")
                else {
                    val words = letters?.let {
                        dictionary.boggleWords(letters).sorted().groupBy { it.length }.toSortedMap()
                    } ?: sortedMapOf()
                    call.respond(mapOf("board" to letters?.chunked(4), "words" to words))
                }
            }
        }
    }.start(wait = true)
}
