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
    int estado;
    int direccion;
    int rocas;


    public Robot(){
        super();
        estado = 0;
        direccion = 1;
    }
}
