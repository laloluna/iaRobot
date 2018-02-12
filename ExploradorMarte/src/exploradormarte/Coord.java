/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exploradormarte;

/**
 *
 * @author Lalo
 */
public class Coord {
    public int x;
    public int y;
    
    Coord(){
        x = -1;
        y = -1;
    }
    
    Coord(int newX, int newY){
        x = newX;
        y = newY;
    }
    
    public static boolean compare(Coord X, Coord Y){
        return X.x == Y.x && X.y == Y.y;
    }
}
