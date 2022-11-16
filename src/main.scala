object Example {
  def main(args: Array[String]): Unit = {
    val summary = {
      val fname = Option(System.getenv("GITHUB_STEP_SUMMARY")).getOrElse("summary-dev.txt")
      new java.io.PrintWriter(
        new java.io.FileOutputStream(fname, true))
    }

    summary.println("## Hello World")

    summary.close()
  }
}
