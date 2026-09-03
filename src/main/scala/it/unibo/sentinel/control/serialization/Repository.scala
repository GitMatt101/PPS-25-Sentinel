package it.unibo.sentinel.control.serialization

import it.unibo.sentinel.control.serialization.Codec.Validation
import scala.util.Try
import scala.io.Source
import java.io.FileNotFoundException
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.Files
import java.nio.charset.StandardCharsets
import java.nio.file.StandardOpenOption
import java.nio.file.FileAlreadyExistsException

/** A repository interface for persisting and retrieving domain models.
  *
  * @tparam Key
  *   the type of the unique identifier used to retrieve models.
  * @tparam M
  *   the type of the domain model managed by this repository.
  */
trait Repository[Key, M]:

  /** Persists a domain model instance to storage.
    *
    * @param model
    *   The domain model instance to save.
    * @param fileName
    *   The target file name or path segment where the model data should be
    *   written.
    */
  def save(model: M, fileName: String): Either[Validation, Unit]

  /** Loads a domain model instance associated with the specified key.
    *
    * @param key
    *   The unique identifier of the domain model to retrieve.
    * @return
    *   `Right(M)` containing the loaded model if found and valid, or
    *   `Left(Validation)` if the operation fails or the model is invalid.
    */
  def load(key: Key): Either[Validation, M]

object FileRepository:

  final val root: String = sys.props("user.home")
  final val folderPath: Path = Paths.get(root, ".sentinel")

  extension (path: String) def inRoot: Path = folderPath.resolve(path)

trait FileRepository[M: Codec] extends Repository[String, M]:

  def save(model: M, fileName: String): Either[Validation, Unit] =
    val data = summon[Codec[M]].encode(model)
    writeToFile(data, fileName)

  def load(filePath: String): Either[Validation, M] =
    readFromFile(filePath).flatMap:
      summon[Codec[M]].decode(_)

  private def writeToFile(
      data: String,
      fileName: String
  ): Either[Validation, Unit] =
    import it.unibo.sentinel.control.serialization.FileRepository.inRoot
    val folder = FileRepository.folderPath
    Try {
      if !Files.exists(folder) then
        Files.createDirectories(folder)
      Files.writeString(
        fileName.inRoot,
        data,
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE_NEW,
        StandardOpenOption.WRITE
      )
      ()
    }.toEither.left.map { case _: FileAlreadyExistsException =>
      Validation.FileAlreadyExists(fileName.inRoot.toString)
    }

  private def readFromFile(filePath: String): Either[Validation, String] =
    Try {
      val source = Source.fromFile(filePath)
      try source.mkString
      finally source.close()
    }.toEither.left.map { case _: FileNotFoundException =>
      Validation.FileNotFound(filePath)
    }
