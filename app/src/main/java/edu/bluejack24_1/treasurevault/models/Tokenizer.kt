package edu.bluejack24_1.treasurevault.models

import org.json.JSONObject
import java.util.Locale

class Tokenizer(config: JSONObject) {
    private val wordIndex: Map<String, Int>
    private val numWords: Int
    private val oovToken: String

    init {
        val configObj = config.getJSONObject("config")
        numWords = configObj.getInt("num_words")
        oovToken = configObj.getString("oov_token")
        val wordIndexJson = configObj.getJSONObject("word_index")
        wordIndex = wordIndexJson.toMap()

        println("numWords: $numWords")
        println("oovToken: $oovToken")
        println("wordIndex: $wordIndex")
    }

    fun textToSequence(text: String): List<Int> {
        val tokens = text.split(" ").map { it.lowercase(Locale.getDefault()) }
        return tokens.map { token ->
            wordIndex[token] ?: wordIndex[oovToken] ?: 0
        }.take(numWords)
    }

    fun padSequences(sequences: List<List<Int>>, maxlen: Int, padding: String = "post"): List<List<Int>> {
        return sequences.map { seq ->
            val truncatedSeq = seq.take(maxlen)
            val paddingSize = maxlen - truncatedSeq.size
            val paddedSeq = if (padding == "post") {
                truncatedSeq + List(paddingSize) { 0 }
            } else {
                List(paddingSize) { 0 } + truncatedSeq
            }
            paddedSeq
        }
    }

    private fun JSONObject.toMap(): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        val keys = this.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            map[key] = this.getInt(key)
        }
        return map
    }
}