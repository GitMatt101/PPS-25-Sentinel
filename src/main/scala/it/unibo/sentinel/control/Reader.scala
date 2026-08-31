package it.unibo.sentinel.control

import java.nio.file.Paths
import scala.util.Try
import scala.io.Source

/** Trait for file readers.
  */
trait Reader:

  /** Reads a file from the user's root.
    *
    * @param fileName
    *   name of the file to read.
    * @return
    *   an `Option` containing a String with the content of the file if the
    *   reading was successful, `None` otherwise.
    */
  def readFromRoot(fileName: String): Either[Throwable, String]

object Reader extends Reader:

  def readFromRoot(fileName: String): Either[Throwable, String] =
    val root: String = sys.props("user.home")
    val filePath: String = Paths.get(root, ".sentinel", fileName).toString
    Try {
      val source = Source.fromFile(filePath)
      try source.mkString
      finally source.close()
    }.toEither
