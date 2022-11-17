//> using lib "com.lihaoyi::requests:0.7.1"
//> using lib "org.scala-lang.modules::scala-xml:2.1.0"

import scala.xml.XML
import scala.concurrent.duration._

case class RequestStat(bodyLength: Long, valid: Boolean, duration: Duration) {
  val tableLine = {
    val validStr =
      if(valid)
        """<img src="https://github.githubassets.com/images/icons/emoji/unicode/2714.png" height="20"/>"""
      else
        """<img src="https://github.githubassets.com/images/icons/emoji/unicode/274c.png" height="20"/>"""
    s"<tr><td>$bodyLength</td><td>$validStr</td><td>${duration.toMillis} ms</td></tr>"
  }
}

object ContentApiBodyLength {

  def main(args: Array[String]): Unit = {
    val summary = {
      val fname = Option(System.getenv("GITHUB_STEP_SUMMARY")).getOrElse("summary-dev.txt")
      new java.io.PrintWriter(new java.io.FileOutputStream(fname))
    }

    val url = "https://content.guardianapis.com/search"
    val apiKey = Option(System.getenv("CAPI_KEY")).get

    val params = Map(
      "format"        -> "xml",
      "show-elements" -> "image",
      "order-by"      -> "newest",
      "show-fields"   -> "all",
      "page-size"     -> "200",
      "page"          -> "3",
      "from-date"     -> "2022-11-13T06:59:44Z",
      "to-date"       -> "2022-11-15T06:59:44Z",
      "api-key"       -> apiKey,
      "use-date"      -> "published"
    )


    val sess = requests.Session(
      headers = requests.BaseSession.defaultHeaders - "Accept-Encoding" // disable gzip compression
    )

    val max = 2
    val delay = 20.millis

    println(s"Running $max requests")

    val statsBuf = scala.collection.mutable.ListBuffer.empty[RequestStat]

    for(n <- (0 until max)) {
      val start = System.nanoTime()
      val res = sess.get(
        url, params=params, compress=requests.Compress.None
      )
      val dur = Duration.fromNanos(System.nanoTime() - start)

      val validXml = try {
        XML.loadString(res.text())
        true
      } catch {
        case e: Throwable => false
      }

      val stat = RequestStat(res.bytes.length, validXml, dur)

      statsBuf += stat

      Thread.sleep(delay.toMillis)
    }

    val stats = statsBuf.toList

    val validCount = stats.filter(_.valid).length
    val sizes = stats.map(_.bodyLength).toSet

    summary.println("## Test result")
    summary.println(s"\nValid: $validCount of $max")

    summary.println("\n<table><tr><th>Body len</th><th>Valid</th><th>Duration</th></tr>")
    for(stat <- stats) summary.println(stat.tableLine)
    summary.println("</table>")

    summary.close()
  }
}
