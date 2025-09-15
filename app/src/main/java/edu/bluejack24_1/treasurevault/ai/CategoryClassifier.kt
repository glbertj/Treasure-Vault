package edu.bluejack24_1.treasurevault.ai

import android.content.Context
import android.content.res.AssetManager
import edu.bluejack24_1.treasurevault.models.Tokenizer
import org.json.JSONArray
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

object CategoryClassifier {
    private lateinit var tfliteInterpreter: Interpreter
    private lateinit var tokenizer: Tokenizer
    private lateinit var categories: Array<String>

    fun init(context: Context) {
        try {
            val assetManager = context.assets

            tfliteInterpreter = Interpreter(loadModelFile(assetManager))
            categories = loadCategoriesFile(assetManager)
//            tokenizer = loadTokenizer(assetManager)
//            println("CategoryClassifier:" + "Initialization successful!")
        } catch (e: Exception) {
//            println("CategoryClassifier:" + "Error during initialization: ${e.message}")
        }
    }

    private fun loadModelFile(assetManager: AssetManager): ByteBuffer {
        val fileDescriptor = assetManager.openFd("transaction_classifier.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.length
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun loadTokenizer(assetManager: AssetManager): Tokenizer {
        val tokenizerJson = assetManager.open("tokenizer.json").bufferedReader().use { it.readText() }
        val tokenizerObj = JSONObject(tokenizerJson)
        return Tokenizer(tokenizerObj)
    }

    private fun loadCategoriesFile(assetManager: AssetManager): Array<String> {
        val inputStream = assetManager.open("categories.json")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val jsonText = reader.use { it.readText() }
        val jsonArray = JSONArray(jsonText)
        return Array(jsonArray.length()) { jsonArray.getString(it) }
    }

    fun predictCategory(description: String): String {
        try {
            val tokens = tokenizer.textToSequence(description)
            val paddedTokens = tokenizer.padSequences(listOf(tokens), maxlen = 100)
            val input = arrayOf(paddedTokens[0])
            val output = Array(1) { FloatArray(categories.size) }
            tfliteInterpreter.run(input, output)
            val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
            return categories[maxIndex]
        } catch (e: Exception) {
            return "Other"
        }
    }
}
