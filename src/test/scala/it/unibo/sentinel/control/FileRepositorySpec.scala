package it.unibo.sentinel.control

import it.unibo.sentinel.UnitTest
import it.unibo.sentinel.core.warehouse.{Warehouse, Position, Tile}
import it.unibo.sentinel.core.simulation.Tick
import it.unibo.sentinel.control.serialization.FileRepository
import it.unibo.sentinel.control.serialization.Codec.Validation
import it.unibo.sentinel.control.serialization.JsonSerialization.given
import java.nio.file.{Files, Path, Paths}
import java.io.IOException
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

class FileRepositorySpec
    extends UnitTest
    with BeforeAndAfterEach
    with BeforeAndAfterAll:

  val fileName: String = "test-warehouse.json"
  val root: String = sys.props("user.home")
  val testFolderPath: Path = Paths.get(root, ".sentinel", "test")
  val filePath: Path = testFolderPath.resolve(fileName)

  val warehouse: Warehouse = Warehouse
    .empty(3, 3)
    .withTile(Position(1, 1)):
      Tile.Floor(Tick.unit)

  val repo: FileRepository[Warehouse] = new FileRepository[Warehouse] {}

  private def deleteTestFolderIfExists(): Unit =
    if Files.exists(testFolderPath) then
      util.Using(Files.walk(testFolderPath)) { stream =>
        stream
          .forEach { path =>
            try Files.deleteIfExists(path)
            catch
              case _: IOException =>
                path.toFile.deleteOnExit()
          }
      }

  override def beforeEach(): Unit =
    deleteTestFolderIfExists()
    Files.createDirectories(testFolderPath)

  override def afterEach(): Unit =
    deleteTestFolderIfExists()

  override def afterAll(): Unit =
    deleteTestFolderIfExists()

  "A file repository" when:

    "creating a file" should:

      "save the file correctly" in:
        repo.save(warehouse, s"test/$fileName") shouldBe Right(())
        Files.exists(filePath) shouldBe true

      "give a validation error if the file already exists" in:
        repo.save(warehouse, s"test/$fileName") shouldBe Right(())
        repo.save(warehouse, s"test/$fileName") shouldBe Left(
          Validation.FileAlreadyExists(filePath.toString)
        )

    "loading a file" should:

      "load the correct model" in:
        repo.save(warehouse, s"test/$fileName") shouldBe Right(())
        repo.load(filePath.toString) shouldBe Right(warehouse)

      "give a validation error if the file does not exist" in:
        val fakeFile: String = testFolderPath.resolve("fake-file.json").toString
        repo.load(fakeFile) shouldBe Left(Validation.FileNotFound(fakeFile))
