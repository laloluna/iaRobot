/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exploradormarte;

import com.golden.gamedev.object.sprite.AdvanceSprite;
/**
 *
 * @author Lalo
 */
public class Robot extends AdvanceSprite {
    int state;
    int rocks;
    int samples;


    public Robot(){
        super();
        state = 0;
        rocks = 0;
        samples = 0;
    }
    
    public void checkRock(){
        rocks++;
    }
    
    public void pickSamples(){
        samples++;
    }
    
    public void returnShip(){
        state = 1;
    }
    
    public void leaveSamples(){
        state = 0;
    }
}
