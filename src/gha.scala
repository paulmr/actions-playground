package gha

class Summary {

  private val fd = {
    val fname = Option(System.getenv("GITHUB_STEP_SUMMARY")).getOrElse("summary-dev.txt")
    new java.io.PrintWriter(new java.io.FileOutputStream(fname))
  }

  def addLine(s: String) = fd.println(s)

  def addHeader(s: String) = addLine("## " + s)

  def addPara(s: String) = addLine(s + "\n") // blank space

  private def makeRow(row: List[String], tag: String = "td") =
    "<tr>" + row.map(s => s"<$tag>$s</$tag>").mkString + "</tr>"

  def addTable(tbl: List[List[String]], header: Boolean = true) = {
    addLine("<table>")
    tbl.headOption.foreach { hdr =>
      addLine(makeRow(hdr, if(header) "th" else "tr"))
    }
    tbl.tail.foreach { row =>
      addLine(makeRow(row))
    }
    addLine("</table>")
  }

  def close(): Unit = fd.close()

}
