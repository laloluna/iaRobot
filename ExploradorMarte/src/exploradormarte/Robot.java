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
    int direction;
    int samples;


    public Robot(){
        super();
        state = 0;
        direction = 1;
        samples = 0;
    }
    
    public void pickSamples(){
        samples = 1;
    }
}
