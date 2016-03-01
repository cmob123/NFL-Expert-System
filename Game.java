/////////////////////////////////////////////////////////////////////////////////////////////
//   Name: Chris O'Brien
//   Date: 2/12/16
//   Inputs: Text file
//   Outputs: games[] array
//////////////////////////////////////////////////////////////////////////////////////////////

import javax.swing.JOptionPane;
import java.io.*;
import java.util.*;

public class Game {
   static Game[] games = new Game[224]; // array of 'game' objects to store info
   String winner, loser, predWinner, predLoser;
   static String teams[] = new String[32];
   int wScore, lScore, week, index;
   boolean home;
   
    public static void input()throws IOException{
      String team1 = "", team2 = "", winner, loser, temp;
      int score1, score2, wScore, lScore, week;
      boolean home;
      
      File input = new File("pros2015new.doc"); // link file  
      Scanner reader = new Scanner(input);
      for(int i=0; i<224; i++){ // loop through 224 lines (14 weeks)
         temp = reader.next();
         team1 = temp;
         temp = reader.next();
         score1 = Integer.parseInt(temp);
         temp = reader.next();
         team2 = temp;
         temp = reader.next();
         score2 = Integer.parseInt(temp);
         temp = reader.next();
         if(temp.equals("H")) home = true;
         else home = false;
         if(score1 > score2){
            winner = team1;
            loser = team2;
            wScore = score1;
            lScore = score2;
         } else {
            winner = team2;
            loser = team1;
            wScore = score2;
            lScore = score1;
            home = !home; // flip home
         }
         week = i/16+1;
         games[i] = new Game(winner, wScore, loser, lScore, home, week, i);
      }
      reader.close();
      
      File input2 = new File("NFLteams.txt"); // link other file
      Scanner reader2 = new Scanner(input2);
      for(int i=0; i<32; i++){ // read all 32 teams
         temp = reader2.next();
         teams[i] = temp;
      }
      reader2.close();
    } //end input()

   public Game(){ // create blank game
      this.winner = "none";
      this.wScore = -99;
      this.loser = "none";
      this.lScore = -99;
      this.week = -99;
      this.predWinner = null;
      this.predLoser = null;
   }
   
   public Game(String win, int winScore, String lose, int loseScore, boolean home, int weekNum, int index){
      this.winner = win;
      this.wScore = winScore;
      this.loser = lose;
      this.lScore = loseScore;
      this.home = home;
      this.week = weekNum;
      this.predWinner = null;
      this.predLoser = null;
      this.index = index;
   }
   public static String printGame(Game game){
      String temp, result;
      if (game.home) temp = "home";
      else temp = "away";
      result = game.winner + " beat " + game.loser + " " + game.wScore + "-" + 
               game.lScore + " " + temp + " in week " + game.week + '.';
      return result;
   }
}