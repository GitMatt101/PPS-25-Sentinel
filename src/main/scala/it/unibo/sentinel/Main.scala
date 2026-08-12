package it.unibo.sentinel

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.StackPane
import scalafx.scene.control.Label

/** A trivial GUI application to test if dependencies have been correctly set.
  */
object GUI extends JFXApp3:
  /** Describe the GUI there.
    */
  override def start(): Unit =
    stage = new JFXApp3.PrimaryStage:
      title = "Multi-Platform ScalaFX"
      scene = new Scene(400, 300):
        root = new StackPane:
          children = new Label("Hello from a Fat Jar on any OS!")

/** The launcher of the program.
  */
object Launcher:
  /** @param args
    *   unused
    */
  def main(args: Array[String]): Unit =
    GUI.main(args)
