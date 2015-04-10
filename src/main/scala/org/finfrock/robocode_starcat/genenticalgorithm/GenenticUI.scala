package org.finfrock.robocode_starcat.genenticalgorithm

import java.awt.BorderLayout
import java.awt.EventQueue
import java.awt.FlowLayout
import java.awt.SystemColor
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.JButton
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JPanel
import javax.swing.JProgressBar
import org.finfrock.robocode_starcat.genenticalgorithm.fitnesstest.IFitnessTest
import org.finfrock.robocode_starcat.PropertyUtilities

class GenenticUI(generationRunner: GenerationRunner,
                 fitnessTest: IFitnessTest) extends JFrame with Runnable {
  // --------------------------------------------------------------------------
  // #region Private Data
  // --------------------------------------------------------------------------

  private val serialVersionUID = 3286354597317648794L;

  private val jPanel1 = new JPanel();

  private val jPanel2 = new JPanel();

  private val southjPanel = new JPanel();

  private val progressBar1 = new JProgressBar();

  private var running = false;

  private val borderLayout1 = new BorderLayout();

  private val startJButton = new JButton();

  private val pauseJButton = new JButton();

  private val generationStringjLabel = new JLabel();

  private val generationCountjLabel = new JLabel();

  private val flowLayout1 = new FlowLayout()

  jbInit()
  SetWindowLocationSize();
  setName("Genectic UI");
  setTitle("Genectic UI");

  // --------------------------------------------------------------------------
  // Public Members
  // --------------------------------------------------------------------------

  def getProgressBar(): IProgress = new Progress(progressBar1)

  def startJButton_mouseClicked(e: MouseEvent) {
    if (!running) {
      startJButton.setEnabled(false);
      pauseJButton.setEnabled(true);
      running = true;
      new Thread(this).start();
    } else {
      generationRunner.unPause();
      startJButton.setEnabled(false);
      pauseJButton.setEnabled(true);
    }
  }

  def pauseJButton_mouseClicked(e: MouseEvent) {
    generationRunner.pause();
    startJButton.setEnabled(true);
    pauseJButton.setEnabled(false);
  }

  def run() {
    while (running) {
      generationRunner.run();
      generationCountjLabel.setText(generationRunner
        .getNumberOfGenerationsRan()
        + "");
    }
  }

  // --------------------------------------------------------------------------
  // Private Members
  // --------------------------------------------------------------------------

  private def SetWindowLocationSize() {
    val windowWidth = 256; // dim.width / 4;
    val windowHeight = 150; // dim.height / 4;
    val windowX = windowWidth / 4;
    val windowY = windowHeight / 4;

    this.setSize(windowWidth, windowHeight);
    this.setLocation(windowX, windowY);
    this.setResizable(false);
  }

  private def jbInit() {
    getContentPane().setLayout(borderLayout1);

    // jPanel1
    jPanel1.setBackground(SystemColor.inactiveCaption);
    jPanel1.setLayout(flowLayout1);

    // jPanel2
    jPanel2.setBackground(SystemColor.activeCaption);
    jPanel2.setLayout(flowLayout1);

    // southjPanel
    southjPanel.setBackground(SystemColor.inactiveCaption);
    southjPanel.setLayout(flowLayout1);

    // Menu bar
    // Create the menu bar
    val menuBar = new JMenuBar();

    val toolsMenu = createToolsMenu();
    val performanceMenu = createPerformanceMenu();

    menuBar.add(toolsMenu);
    menuBar.add(performanceMenu);

    // Install the menu bar in the frame
    setJMenuBar(menuBar);

    // generationStringjLabel
    generationStringjLabel.setText("Generations:");
    southjPanel.add(generationStringjLabel, null);

    // generationCountjLabel
    generationCountjLabel.setText("0");
    southjPanel.add(generationCountjLabel, null);

    // progressBar1
    progressBar1.setValue(0);
    progressBar1.setStringPainted(true);
    jPanel2.add(progressBar1, null);

    // Start button
    startJButton.setText("Start");
    startJButton.addMouseListener(new GenenticUI_startJButton_mouseAdapter(
      this));
    jPanel1.add(startJButton, null);

    // Stop button
    pauseJButton.setText("Pause");
    pauseJButton.addMouseListener(new GenenticUI_pauseJButton_mouseAdapter(
      this));
    pauseJButton.setEnabled(false);
    jPanel1.add(pauseJButton, null);

    this.getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);
    this.getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);
    this.getContentPane().add(southjPanel, java.awt.BorderLayout.SOUTH);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  private def createPerformanceMenu(): JMenu = {
    // Create a menu
    val toolsMenu = new JMenu("Performance");

    // Create a menu item
    val item = new JMenuItem("Show Individual");
    item.addActionListener(new ActionListener() {
      def actionPerformed(e: ActionEvent) {
        val chooser = new JFileChooser();

        // only allows the user to choose a directory
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // opens the browse window
        val returnVal = chooser.showOpenDialog(GenenticUI.this);

        // check if the user choose a directory
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          // get the folder chosen
          val file = chooser.getSelectedFile();

          new PropertyUtilities().readInProperties(file) match {
            case Some(properties) => {
              val chromosome = new BotcatChromosome(properties)
              val individaul = new Individual(chromosome);

              GenenticUI.this.setEnabled(false);

              val thread = new Thread() {
                override def run() {
                  fitnessTest.run(individaul);

                  EventQueue.invokeLater(new Runnable() {
                    def run() {
                      GenenticUI.this.setEnabled(true);
                    }
                  });
                }
              };

              thread.start();
            }
          }
        }
      }
    });

    toolsMenu.add(item);

    return toolsMenu;
  }

  private def createToolsMenu(): JMenu = {
    // Create a menu
    val toolsMenu = new JMenu("Tools");

    // Create a menu item
    val item = new JMenuItem("Save All");
    item.addActionListener(new ActionListener() {
      def actionPerformed(e: ActionEvent) {
        val chooser = new JFileChooser();

        // only allows the user to choose a directory
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // opens the browse window
        val returnVal = chooser.showOpenDialog(GenenticUI.this);

        // check if the user choose a directory
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          // get the folder chosen
          val file = chooser.getSelectedFile();

          generationRunner.saveAll(file);
        }
      }
    });

    toolsMenu.add(item);

    val item1 = new JMenuItem("Load");
    item1.addActionListener(new ActionListener() {
      def actionPerformed(e: ActionEvent) {
        val chooser = new JFileChooser();

        // only allows the user to choose a directory
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // opens the browse window
        val returnVal = chooser.showOpenDialog(GenenticUI.this);

        // check if the user choose a directory
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          // get the folder chosen
          val file = chooser.getSelectedFile();

          generationRunner.load(file);

          generationCountjLabel.setText(generationRunner
            .getNumberOfGenerationsRan()
            + "");
        }
      }
    });

    toolsMenu.add(item1);

    return toolsMenu;
  }
}
// --------------------------------------------------------------------------
// Local Classes
// --------------------------------------------------------------------------

class GenenticUI_startJButton_mouseAdapter(adaptee: GenenticUI) extends MouseAdapter {

  override def mouseClicked(e: MouseEvent) {
    adaptee.startJButton_mouseClicked(e);
  }
}

class GenenticUI_pauseJButton_mouseAdapter(adaptee: GenenticUI) extends MouseAdapter {

  override def mouseClicked(e: MouseEvent) {
    adaptee.pauseJButton_mouseClicked(e);
  }
}