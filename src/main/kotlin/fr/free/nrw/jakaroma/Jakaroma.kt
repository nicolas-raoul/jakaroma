package fr.free.nrw.jakaroma

import com.atilika.kuromoji.ipadic.Token
import com.atilika.kuromoji.ipadic.Tokenizer

private const val DEBUG = false

class Jakaroma {
    private var isPronunciation = false // Class property
    private val tokenizer = Tokenizer()

    /**
     * Converts kanji, etc to romaji
     *
     * @param input           the string to convert
     * @param trailingSpace   add trailing space after the token
     * @param capitalizeWords start token translation with a capital letter
     * @return romaji representation
     */
    fun convert(input: String, trailingSpace: Boolean, capitalizeWords: Boolean): String {
        val tokens = tokenizer.tokenize(halfWidthToFullWidth(input))
        val tokensSize = tokens.size
        val buffer = StringBuilder()

        if (DEBUG) {
            tokens.forEach { token -> println("${token.surface}\t${token.allFeatures}") }
        }

        var i = 0 // Initialize index for while loop
        while (i < tokensSize) {
            val token = tokens[i]
            val type = token.allFeaturesArray[1] // Safe if allFeaturesArray always has at least 2 elements

            if (DEBUG) {
                println("Token: ${token.surface}")
                println("Type: $type")
            }

            if (token.allFeaturesArray[0] == "記号") { // Safe if allFeaturesArray always has at least 1 element
                buffer.append(KanaToRomaji.convert(token.surface))
                i++ // Increment index
                continue
            }

            when (type) {
                "数", "アルファベット", "サ変接続" -> {
                    buffer.append(KanaToRomaji.convert(token.surface))
                }
                else -> {
                    var romaji: String
                    val katakanaReading = getKatakana(token)

                    if (katakanaReading == "*") {
                        romaji = kuromojiFailedConvert(token)
                    } else {
                        val currentKatakana = katakanaReading
                        if (DEBUG) {
                            println("Katakana: $currentKatakana")
                        }

                        if (currentKatakana.endsWith("ッ")) {
                            if (i == tokensSize - 1) { // Sokuon at the very end
                                romaji = if (currentKatakana.length > 1) {
                                    // Remove potential preceding space if this is the first actual output character.
                                    // The original Java code had complex space deletion logic here.
                                    // A simple approach: if buffer ends with space, remove it.
                                    if (buffer.isNotEmpty() && buffer.last() == ' ') {
                                        buffer.deleteCharAt(buffer.length - 1)
                                    }
                                    KanaToRomaji.convert(currentKatakana.substring(0, currentKatakana.length - 1)) + "!"
                                } else {
                                    "!" // Just "ッ" at the end becomes "!"
                                }
                                if (DEBUG) println("Exclamation sokuon, romaji becomes: $romaji")
                            } else { // Sokuon followed by another token
                                romaji = smallTsuRomaji(token, tokens[i + 1])
                                if (DEBUG) println("Sokuon detected, merged romaji: $romaji")
                                i++ // Crucial: skip the next token as it has been processed
                            }
                        } else {
                            romaji = KanaToRomaji.convert(currentKatakana)
                        }
                    }

                    if (romaji.isNotEmpty()) {
                        if (capitalizeWords) {
                            buffer.append(romaji.first().uppercaseChar()).append(romaji.substring(1))
                        } else {
                            if (token.surface == katakanaReading && isAllKatakana(token.surface)) {
                                buffer.append(romaji.uppercase())
                            } else {
                                buffer.append(romaji)
                            }
                        }
                    }
                }
            }

            // Add trailing space if needed
            // Only add space if something was appended in this iteration and it's not the last token
            if (trailingSpace && i < tokensSize - 1) {
                // Check if the current token actually produced output
                // This is a bit tricky as the buffer might have been modified by previous tokens.
                // A simpler rule: if buffer is not empty and does not end with a space, add one.
                // This might differ slightly from original's complex space logic.
                if (buffer.isNotEmpty() && buffer.last() != ' ') {
                    // Specific types in original code avoided trailing spaces by `continue`
                    // Here, we ensure those types don't add a space if they were the last thing processed.
                    val isSymbolLike = type in listOf("数", "アルファベット", "サ変接続", "記号")
                    if (!isSymbolLike || KanaToRomaji.convert(token.surface).isNotEmpty()) { // Add space for symbols only if they produced output
                        buffer.append(" ")
                    }
                }
            }
            i++
        }

        // Final trim of trailing space if any exists
        if (buffer.isNotEmpty() && buffer.last() == ' ') {
            buffer.deleteCharAt(buffer.length - 1)
        }

        if (DEBUG) {
            println(buffer.toString())
        }
        return buffer.toString()
    }

    private fun getKatakana(token: Token): String {
        val result = if (isPronunciation) {
            token.pronunciation
        } else {
            token.reading
        }
        return result ?: "*"
    }

    private fun smallTsuRomaji(currentToken: Token, nextToken: Token): String {
        val currentKatakana = getKatakana(currentToken) // e.g., "カッ"
        val nextKatakana = getKatakana(nextToken)     // e.g., "ト"

        // Convert current token's katakana part (before "ッ") to romaji
        val currentRomajiPart = KanaToRomaji.convert(currentKatakana.dropLast(1)) // "カ" -> "ka"

        val nextTokenRomaji = KanaToRomaji.convert(nextKatakana) // "ト" -> "to"

        return if (nextTokenRomaji.isNotEmpty()) {
            "$currentRomajiPart${nextTokenRomaji.first()}$nextTokenRomaji" // "ka" + "t" + "to" = "katto"
        } else {
            currentRomajiPart // Should not happen if nextToken is valid
        }
    }

    private fun kuromojiFailedConvert(token: Token): String {
        val surface = token.surface
        val buffer = StringBuilder()
        var idx = 0
        while (idx < surface.length) {
            val char = surface[idx]
            if (char == 'ッ') {
                if (idx + 1 < surface.length) {
                    val nextChar = surface[idx + 1]
                    val nextRomaji = KanaToRomaji.convert(nextChar.toString())
                    if (nextRomaji.isNotEmpty() && nextRomaji != nextChar.toString()) {
                        // If next char is a convertible Kana, double its first consonant
                        buffer.append(nextRomaji.first())
                    } else {
                        // If next char is not Kana or some other symbol, append 't' or 'tsu' for 'ッ'
                        buffer.append('t') // Or "tsu" or "" depending on desired behavior
                    }
                } else {
                    // Small tsu at the end of a surface form when Kuromoji fails to provide reading
                    buffer.append('!') // Or "tsu" or ""
                }
            } else {
                buffer.append(KanaToRomaji.convert(char.toString()))
            }
            idx++
        }
        return buffer.toString()
    }

    private fun isAllKatakana(text: String): Boolean {
        if (text.isEmpty()) return false
        return text.all { char ->
            // Check for Katakana Unicode block or Prolonged Sound Mark
            (char in '\u30A0'..'\u30FF') || char == '\u30FC'
        }
    }

    private fun halfWidthToFullWidth(fullWidthStr: String?): String {
        if (fullWidthStr.isNullOrEmpty()) {
            return ""
        }
        return fullWidthStr.map { char ->
            when (char.code) {
                // Full-width ASCII variants to Half-width ASCII
                in 0xFF01..0xFF5E -> (char.code - 0xFEE0).toChar()
                // Full-width space (Ideographic Space) to standard space
                0x3000 -> ' '
                else -> char
            }
        }.joinToString("")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val instance = Jakaroma()
            instance.isPronunciation = true // Default for CLI tool

            val input: String = if (args.isEmpty() || args[0].isBlank()) {
                System.err.println("Jakaroma warn: no argument passed, reading stdin...")
                System.err.flush()
                // Read all input from stdin, trim whitespace
                System.`in`.bufferedReader().readText().trim()
            } else {
                args[0]
            }

            if (input.isNotEmpty()) {
                val result = instance.convert(input, trailingSpace = true, capitalizeWords = true)
                println(result)
            } else {
                 System.err.println("Jakaroma warn: input is empty, nothing to convert.")
            }
        }
    }
}
