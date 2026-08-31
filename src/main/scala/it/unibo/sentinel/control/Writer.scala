package it.unibo.sentinel.control

import java.nio.file.Paths
import scala.util.Try
import java.nio.file.Files
import java.nio.charset.StandardCharsets
import java.nio.file.StandardOpenOption
import java.nio.file.Path

/** Writes on files in the user's root.
  */
trait Writer:

  /** Creates/updates a file with the given text.
    *
    * @param fileName
    *   name of the file.
    * @param content
    *   new content of the file.
    * @return
    *   `true` if the write was successful, `false` otherwise.
    */
  def writeToRoot(fileName: String, content: String): Boolean

object Writer extends Writer:

  override def writeToRoot(fileName: String, content: String): Boolean =
    val root: String = sys.props("user.home")
    val correctName: String =
      if fileName.endsWith(".json") then fileName else fileName + ".json"
    val folderPath: Path = Paths.get(root, ".sentinel")
    val filePath: Path = folderPath.resolve(correctName)
    Try {
      if !Files.exists(folderPath) then Files.createDirectories(folderPath)
      Files.writeString(
        filePath,
        content,
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING
      )
    }.isSuccess
