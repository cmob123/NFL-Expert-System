///////////////////////////////////////////////////////////////////////////////////
//   Name: Chris O'Brien
//   Date: 2/12/2016
//   Purpose: take games[] array and predict weeks 12, 13, and 14
//   Inputs: games[] array
//   Outputs: predictions
///////////////////////////////////////////////////////////////////////////////////

import javax.swing.JOptionPane;
import java.io.*;
import java.util.*;

public class Work {
    final static int PW = 7; // # of weeks to evaluate prior to predicted game (performance window)
    
    public static void main(String args[]) throws IOException{
      Game.input(); // organize data
      int index; // to pass to predictGame method     
      for(int i=16*11; i < 16*14; i++){ // predict the games from week 12-14
         index = i-i%16;
         predictGame(Game.games[i], index);
      }
      Output.printResults();
      System.exit(0);
    } // end main method
    
    public static void predictGame (Game game, int index) { // evaluate teams and output prediction
      Game temp = new Game(); // placeholder for current game being evaluated
      double score1=0, score2=0; // overall certainty scores
      int roadPts1=0, roadPts2=0, // tally of points scored while on the road
          roadWins1=0, roadWins2=0, // tracks on-the-road win streak
          wStreak1=0, lStreak1=0, wStreak2=0, lStreak2=0, // determines if a team has a streak of winning and losing, 
                                                          // and the size of the streak (-1 means no streak)
          ptsCount1=0, ptsCount2=0, // weighted tally of points scored by both teams within the PW
          dWins = 0, // used in Rule 1 to calculate home-field advantage (-99 by default)
          gameDif, //multiplier used to weigh the scores
          max=0, // tracks the highest score for the week immediately before the week of the game being evaluated
          streak; // absolute value of the difference between the 2 teams' streaks 
                  // (aka 'X' in Rule from Trono's heuristics)
      boolean team1Road = true, team2Road = true, // used to determine if teams are on a streak on the road 
              team1Max = true, team2Max = true, // says whether either team had the most points in the week before this one
              roadStreak1=true, roadStreak2=true; // tracks whether or not teams have lost on the road
      String team1 = game.winner, 
             team2 = game.loser;

      for(int i=1; i<PW*16; i++){ // look back at each game in the PW in reverse order
         temp = Game.games[index-i]; // game being evaluated
         if(temp.home) dWins++; // increment dWins if home team won
         else dWins--; // decrement otherwise
         gameDif = game.week-temp.week; // how many weeks back you're looking
         if (temp.wScore > max && gameDif == 1){ // (Rule 5) calculate max and the team it belongs to
            max = temp.wScore;
            if (temp.winner.equals(team1)) team1Max = true;
            else team1Max = false;
            if (temp.winner.equals(team2)) team2Max = true;
            else team2Max = false;
         }
         if(temp.winner.equals(team1)){ // evaluate games won by team1
            ptsCount1 += temp.wScore * (PW-gameDif+1); // (Rule 6) weight the points based on how recent the game is
            if(wStreak1>=0) wStreak1++; // Rule 2
            lStreak1 = -1;
            if(temp.home) team1Road = false; // no more road streaks possible (for rule 3)
            if(team1Road){
               roadPts1 += temp.wScore; // for Rule 4
               roadWins1++;
            }
         }
         if(temp.loser.equals(team1)){ // evaluate games lost by team1
            ptsCount1 += temp.lScore * (PW-gameDif); // (Rule 6) weight the points based on how recent the game is
            if(lStreak1>=0) lStreak1++; // Rule 2
            wStreak1 = -1;
            if(!temp.home) team1Road = false; // no more road streaks possible (for rule 3)
            if(team1Road){
               roadStreak1 = false;
               roadPts1 += temp.lScore; // for Rule 4
            }
         }
         if(temp.winner.equals(team2)){ // evaluate games won by team2
            ptsCount2 += temp.wScore * (PW-gameDif); // (Rule 6) weight the points based on how recent the game is
            if(wStreak2>=0) wStreak2++; // Rule 2
            lStreak2 = -1;
            if(temp.home) team2Road = false; // no more road streaks possible (for rule 3)
            if(team2Road){
               roadPts2 += temp.wScore; // for Rule 4
               roadWins2++;                 
            }
         }
         if(temp.loser.equals(team2)){ // evaluate games lost by team2
            ptsCount2 += temp.lScore * (PW-gameDif); // (Rule 6) weight the points based on how recent the game is
            if(lStreak2>=0) lStreak2++; // Rule 2
            wStreak2 = -1;
            if(!temp.home) team2Road = false; // no more road streaks possible (for rule 3)
            if(team2Road){
               roadStreak2 = false;
               roadPts2 += temp.lScore; // for Rule 4
            }
         }
      } // end for
      
      // Rule Base
      
      // Rule 1: Home-field advantage (using dWins). 
              // The larger dWins is, the more likely home team will win (and vice versa)
         if(game.home) // if (home)
            score1 = 3 + .05*dWins; // then (team wins)
         else score2 = 3 + .05*dWins; // else (team loses)
      
      // Rule 2: If team x is on a winning streak and team y is on a losing streak, 
              // take team x (longer streaks => more certainty)
         if (lStreak1 != -1 && wStreak2 != -1){
            streak = Math.abs(lStreak1 - wStreak2); // 'X' in Trono's heuristics
            score2 += .3 + .05*streak;
         } else if (wStreak1 != -1 && lStreak2 != -1){
            streak = Math.abs(wStreak1 - lStreak2);
            score1 += .3 + .05*streak;     
         }
         
      // Rule 3: If team1's winning on the road, take team2
         if (roadWins1 >= 2) score2 += .4 + .5*roadWins1;
         else if (roadWins2 >= 2) score1 += .4 + .5*roadWins2;
         
      // Rule 4: If team x scored >30 points on the road, take team y
         if (roadPts1 > 30) score1 -= .3;
         if (roadPts2 > 30) score2 -= .3;
      
      // Rule 5: If team x scored most last week, take team y
         if(team1Max) score1 -= .3; 
         if(team2Max) score2 -= .3;
         
      // Rule 6: If team x has scored more points than team y in the PW, take team x
      // (weighted based on how recent) seems to be the most valuable rule
         score1 += ptsCount1/25;
         score2 += ptsCount2/25;
                
      // add prediction to games[] array
      if (score1 >= score2) { // predict team1 wins
         Game.games[game.index].predWinner = team1;
         Game.games[game.index].predLoser = team2;
      } else {                // predict team2 wins
         Game.games[game.index].predWinner = team2;
         Game.games[game.index].predLoser = team1;
      }     
    } // end predictGame method
} //end the class