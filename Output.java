/////////////////////////////////////////////////////////////////////////////////////////////
//   Name: Chris O'Brien
//   Date: 2/12/16
//   Purpose: Format and print predictions
//   Inputs: Predictions
//   Outputs: Predictions (printed to the console)
//////////////////////////////////////////////////////////////////////////////////////////////

import javax.swing.JOptionPane;
import java.io.*;
import java.util.*;
import java.text.DecimalFormat; // for formatting output

public class Output {
    public static void printResults(){
       Game temp = new Game();
       int numCorrect = 0, // # of correct predictions
           weekCount = 11; // Track which week is being outputted
       double accuracy; // proportion of predictions correct
       for(int i=16*11; i < 16*14; i++){
          temp = Game.games[i];
          if (temp.week != weekCount) {
             weekCount++;
             System.out.println("\nWEEK " + weekCount + ":");
          }
          System.out.println("\nGame " + i + ": " + temp.winner + " vs " + temp.loser);
          System.out.println("  Predicted winner: " + temp.predWinner);
          System.out.println("  Actual winner: " + temp.winner);
          if (temp.winner.equals(temp.predWinner)) numCorrect++;
       } // end for
       
       DecimalFormat formatter = new DecimalFormat ("#0.00");
       accuracy = (double) numCorrect/(16*3);
       System.out.println("\nAccuracy: " + numCorrect + "/48 = " + formatter.format(accuracy*100) + "%");
    } //end main method
}