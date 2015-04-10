package org.finfrock.robocode_starcat.genenticalgorithm

import javax.swing.JProgressBar

class Progress(progressBar:JProgressBar) extends IProgress {
   
  def setValue(value:Int){
      progressBar.setValue(value)
   }
}