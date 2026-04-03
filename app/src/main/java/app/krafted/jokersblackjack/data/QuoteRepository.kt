package app.krafted.jokersblackjack.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class QuoteRepository(context: Context) {
    private val quotes: Map<String, List<String>>

    init {
        val inputStream = context.assets.open("joker_quotes.json")
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<Map<String, List<String>>>() {}.type
        quotes = Gson().fromJson(reader, type)
        reader.close()
    }

    fun getRandomQuote(eventKey: String): String {
        val quoteList = quotes[eventKey]
        if (quoteList.isNullOrEmpty()) return "..."
        return quoteList.random()
    }
}
