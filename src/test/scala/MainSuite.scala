import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

class MainSuite extends AnyFlatSpec with Matchers:
  behavior of "Project"

  it should "have the right name" in:
    Main.projectName shouldBe "scala3-template"

  it should "greet the user" in:
    Main.greetingMessage shouldBe s"Welcome to ${Main.projectName}"

  it should "have a main" in:
    "Main.main(Array.empty)" should compile
