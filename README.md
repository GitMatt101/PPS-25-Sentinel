# Scala sbt Project Template

This repository is a [GitHub Template](https://docs.github.com/en/repositories/creating-and-managing-repositories/creating-a-template-repository) designed to help you bootstrap new Scala 3 projects quickly and consistently.

## What does it provide?

This template comes pre-configured with some tools and workflows out of the box:

* **Branching:** [git flow](https://nvie.com/posts/a-successful-git-branching-model/) model support.
* **Build System:** `sbt` build definition featuring:
    * **Testing:** [ScalaTest](https://www.scalatest.org/)
    * **Coverage:** [Scoverage](https://github.com/scoverage/sbt-scoverage)
    * **Formatting:** [Scalafmt](https://scalameta.org/scalafmt/)
    * **Linting:** [Wartremover](https://www.wartremover.org/)
    * **Report generation:** `sbt build` produces the Quarkdown report as HTML in `target/api/report` and PDF in `target/dist`
