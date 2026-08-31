package it.unibo.sentinel.control

import it.unibo.sentinel.UnitTest

class ReaderWriterSpec extends UnitTest:

  val fileName: String = "test-file.json"
  val content: String =
    """
    {
      test: "test"
    }
    """

  "A reader and a writer" when:

    "writing and reading a file" should:

      "save the correct content and read it" in:
        Writer.writeToRoot(fileName, content) shouldBe true
        Reader.readFromRoot(fileName) shouldBe Right(content)
