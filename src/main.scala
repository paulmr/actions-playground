object Example {
  def main(args: Array[String]): Unit = {
    val summary = {
      val fname = Option(System.getenv("GITHUB_STEP_SUMMARY")).getOrElse("summary-dev.txt")
      new java.io.PrintWriter(
        new java.io.FileOutputStream(fname, true))
    }

    val secret = Option(System.getenv("GHA_TEST_SECRET")).getOrElse("?missing?")

    summary.println("## Hello World")

    summary.println(s"Secret from env: $secret")

    summary.close()
  }
}
