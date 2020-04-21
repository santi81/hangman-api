package backend.api.util
import org.json4s._
import org.json4s.native.JsonMethods._
// scala-style:ignore
object URLFetcher
  {
    @throws(classOf[java.io.IOException])
    @throws(classOf[java.net.SocketTimeoutException])
    // scalastyle:ignore
    def get(url: String,
            connectTimeout: Int = 5000,
            readTimeout: Int = 5000,
            requestMethod: String = "GET"):String =
    {
      import java.net.{URL, HttpURLConnection}
      val connection = (new URL(url)).openConnection.asInstanceOf[HttpURLConnection]
      connection.setConnectTimeout(connectTimeout)
      connection.setReadTimeout(readTimeout)
      connection.setRequestMethod(requestMethod)
      val inputStream = connection.getInputStream
      val content = scala.io.Source.fromInputStream(inputStream).mkString
      if (inputStream != null) inputStream.close
      content
    }
    def fetchRandomWord(): String = {
      val responseString = get(Constants.wordFetcherURL)
      val responseJSON = parse(responseString)
      val wordValue = responseJSON \ "word"
      return wordValue.values.toString
    }
  }
