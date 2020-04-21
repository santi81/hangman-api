package backend.api.util

object Constants {
    val numGuesses = 10
    val wordFetcherURL = "http://api.wordnik.com/v4/words.json/" +
      "randomWord?hasDictionaryDef=true&includePartOfSpeech=noun&minCorpusCount=8000&maxCorpusCount=-1&" +
      "minDictionaryCount=3&maxDictionaryCount=-1&" +
      "minLength=6&maxLength=9&api_key=a2a73e7b926c924fad7001ca3111acd55af2ffabf50eb4ae5"
}
