/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exploradormarte;

import com.golden.gamedev.*;
import com.golden.gamedev.object.*;
import java.util.Random;
import com.golden.gamedev.object.background.*;
import com.golden.gamedev.object.font.SystemFont;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Calendar;
// import java.awt.event.KeyEvent;
/**
 *
 * @author Lalo
 */
public class Main extends Game{
    // Timer para la ejecucion por pasos
    Timer timer = new Timer(1000);
    public static boolean showSample = false;
    SystemFont pencil;
    public static Robot explorer = new Robot();
    
    // Fondo con el terreno y robot y grupo de obstaculos y muestras
    Background backgnd;
    Sprite robotSprite;
    Sprite ship;
    Sprite sample;
    SpriteGroup obstacles = new SpriteGroup("Obstacles");
    SpriteGroup samples = new SpriteGroup("Samples");
    SpriteGroup rocks = new SpriteGroup("Rocks");
    
    // Cantidad de rocas
    public static final int ROCKS = 20;
    public static final int OBSTACLES = 20;
    
    // Coordenadas de la nave
    public static final int SHIPX = 9;
    public static final int SHIPY = 10;
    
    // Coordenadas de la nave
    public static final int ROBOTX = 10;
    public static final int ROBOTY = 10;
    
    // Dimension del mapa a explorar
    public static final int GLOBALX = 20;
    public static final int GLOBALY = 20;
    
    // Tamanio del fondo
    public static final int SIZEX = 1200;
    public static final int SIZEY = 1200;
    // Tamanio por casilla
    public static final int PIXELSTEP = SIZEX/GLOBALX;
    
    // Creacion del mapa
    public static int [][] map = new int [GLOBALX][GLOBALY];
    // Creacion de mapa de densidad
    public static int [][] densityMap = new int [GLOBALX][GLOBALY];
    public static Coord globalShip = new Coord();
    
    public static void main(String[] args) {
        Thread thread = new Thread(){
            @Override
            public void run(){
                try{
                    sleep(4000);
                }
                catch(InterruptedException e){
                }
                int avaliableSamples = mapGeneration(ROCKS, OBSTACLES);
                Coord currentObjective;
                Coord currentPosition = new Coord(ROBOTX, ROBOTY);

                printMatrix();
                System.out.println();

                int count = 0;
                // Desicion de la posicion de la nave
                while(explorer.samples < avaliableSamples){
                    map[currentPosition.y][currentPosition.x] = 8;
                    currentObjective = objectiveSelection(explorer, currentPosition);

                    /*
                    //Impresion de valores
                    System.out.println("----------OBJETIVO ACTUAL------------");
                    System.out.println("x = "+currentObjective.x);
                    System.out.println("y = "+currentObjective.y);

                    System.out.println("pos x = "+currentPosition.x);
                    System.out.println("pos y = "+currentPosition.y);
                    System.out.println("value = "+map[currentObjective.y][currentObjective.x]);
                    System.out.println("----------OBJETIVO ACTUAL------------");
                    */


                    // Parte del codigo que hace que el robot se mueva al objetivo actual
                    while(!Coord.compare(currentObjective,currentPosition)){

                        // Moverme al objetivo
                        int directionX = Integer.compare(currentPosition.x, currentObjective.x);
                        int directionY = Integer.compare(currentPosition.y, currentObjective.y);
                        currentPosition = moove(currentPosition, directionX, directionY);
                        
                        // Sleep para la parte grafica
                        try{
                            sleep(1000);
                        }
                        catch(InterruptedException e){
                        }
                        
                        // Variables para la actualizacion de la parte grafica
                        globalShip.x = currentPosition.x;
                        globalShip.y = currentPosition.y;
                        
                        // Si esa casilla tiene muestras, aniadele una al robot
                        if(map[currentPosition.y][currentPosition.x] == 2 || map[currentPosition.y][currentPosition.x] == 1){
                            // Se imprime la hora en la que consiguio llegar a la roca
                            Calendar calendar = Calendar.getInstance();
                            java.util.Date now = calendar.getTime();
                            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
                            System.out.println(explorer.rocks + " " +currentTimestamp);
                            
                            // Aniade en uno las rocas que ha revisado
                            explorer.checkRock();
                            if(map[currentPosition.y][currentPosition.x] == 2){
                                // Si encuentra una muesra la aniade a su sistema
                                explorer.pickSamples();
                                explorer.returnShip();
                                showSample = true;
                                // Sleep para la parte grafica
                                try{
                                    sleep(1000);
                                }
                                catch(InterruptedException e){
                                }
                                showSample = false;
                            }
                        }
                        
                        // Animacion de regreso a la nave
                        if(currentPosition.x == SHIPX && currentPosition.y == SHIPY){
                            showSample = true;
                            // Sleep para la parte grafica
                            try{
                                sleep(1000);
                            }
                            catch(InterruptedException e){
                            }
                            showSample = false;
                        }
                        
                        // Marca las casillas como visitadas
                        map[currentPosition.y][currentPosition.x] = 8;
                        densityMap[currentPosition.y][currentPosition.x] = 1;

                        /*
                        //Imprimir valores DEBUG 
                        printMatrix();
                        System.out.println("------------"+count+"------------");
                        System.out.println("Obj x = "+currentObjective.x);
                        System.out.println("Obj y = "+currentObjective.y);
                        System.out.println("Pos x = "+currentPosition.x);
                        System.out.println("Pos y = "+currentPosition.y);
                        System.out.println("------------"+count+"------------");
                        */

                        count++;
                    }
                }

                System.out.println();
                System.out.println(">>>>>>Matriz resultante<<<<<<<");
                printMatrix();
            }
        };
        thread.start();
        GameLoader game = new GameLoader();
        game.setup(new Main(), new Dimension(SIZEX/3, SIZEX/3), false);
        game.start();
        //game.run();
        
    }
    
    
    
    // Metodo que hace que se mueva el robot eligiendo direccion y esquivando obstaculos
    public static Coord moove(Coord current, int difX, int difY){
        // Se crea un random que se usara para posibles nuevas elecciones de direccion
        Random rand = new Random();
        Coord newCoord = new Coord(current.x, current.y);
        int directionX = difX * -1;
        int directionY = difY * -1;
        
        int randDir;
        
        // Si va en diagonal
        if(directionX != 0 && directionY != 0){
            // Si hay un obstaculo
            if(map[newCoord.y + directionY][newCoord.x + directionX] == 3){
                randDir = rand.nextInt(2);
                if(randDir == 0){
                    directionX *= 0;
                }
                else{
                    directionY *= 0;
                }
            }
        }
        
        // Si no va en diagonal
        if(directionX == 0 || directionY == 0){
            // Si esta bloqueado el lugar elige otro aleatoriamente
            while(map[newCoord.y + directionY][newCoord.x + directionX] == 3){
                do{
                    randDir = rand.nextInt(3);
                    directionX = randDir -1;

                    randDir = rand.nextInt(3);
                    directionY = randDir -1;
                    // While para no salirse de la matriz
                }while( (newCoord.y + directionY) < 0 ||
                        (newCoord.y + directionY) >= GLOBALY ||
                        (newCoord.x + directionX) < 0 ||
                        (newCoord.x + directionX) >= GLOBALX);
            }
        }
        
        newCoord.x += directionX;
        newCoord.y += directionY;
        
        // Manda error si la nueva posicion es una casilla de obstaculo
        if(map[newCoord.y][newCoord.x] == 3){
            System.out.println("Error");
        }
        
        return newCoord;
    }
    
    
    // Metodo viejo que decidia la direccion pero no sirve tan bien 
    public static Coord moove2(Coord current, int directionXX, int directionYY){
        Coord newCoord = new Coord(current.x, current.y);
        int directionX = directionXX;
        int directionY = directionYY;
        
        // Si va en diagonal
        if(directionX != 0 && directionY != 0){
            
        }
        
        // Hacia la izquierda
        if(directionX == 1){
            // Si no hay obstaculo o se va a mover en diagonal
            if(map[newCoord.y][newCoord.x - 1] != 3 || directionY != 0){
                newCoord.x--;
            }
            // Si hay obstaculo y no se va en diagonal
            else if(directionY == 0){
                if(newCoord.y > 0 && map[newCoord.y -1][newCoord.x] != 3){
                    newCoord.y--;
                }
                else if(map[newCoord.y + 1][newCoord.x] != 3){
                    newCoord.y++;
                }
            }
        }
        // Hacia la derecha
        else if(directionX == -1){
            if(map[newCoord.y][newCoord.x + 1] != 3 || directionY != 0){
                newCoord.x++;
            }
            else if(directionY == 0){
                if(newCoord.y > 0 && map[newCoord.y -1][newCoord.x] != 3){
                    newCoord.y--;
                }
                else if(map[newCoord.y + 1][newCoord.x] != 3){
                    newCoord.y++;
                }
            }
        }
        
        // Hacia arriba
        if(directionY == 1){
            if(map[newCoord.y - 1][newCoord.x] != 3 || directionX != 0){
                newCoord.y--;
            }
            else if(directionX == 0){
                if(newCoord.x > 0 && map[newCoord.y][newCoord.x -1] != 3){
                    newCoord.x--;
                }
                else if(map[newCoord.y][newCoord.x +1] != 3){
                    newCoord.x++;
                }
            }
        }
        // Hacia abajo
        else if(directionY == -1){
            if(map[newCoord.y + 1][newCoord.x] != 3 || directionX != 0){
                newCoord.y++;
            }
            else if(directionX == 0){
                if(newCoord.x > 0 && map[newCoord.y][newCoord.x -1] != 3){
                    newCoord.x--;
                }
                else if(map[newCoord.y][newCoord.x +1] != 3){
                    newCoord.x++;
                }
            }
        }
        
        // Mensaje de error por si se atoro en algun obstaculo
        if(Coord.compare(newCoord, current)){
            System.out.println("ERROR");
            System.out.println("! "+current.x+" "+current.y+" ! ");
            System.out.println("! "+newCoord.x+" "+newCoord.y+" ! ");
            System.out.println("ERROR");
        }
        
        return newCoord;
    }
    
    // Metodo que regresa la direccion inversa
    public static int inverse(int direction){
        return Math.abs(direction-2);
    }
    
    // Impresion de la matriz para debugeo
    public static void printMatrix(){
        for(int i = 0; i < GLOBALX; i++){
            for(int j = 0; j < GLOBALY; j++){
                System.out.print(map[i][j]+ " ");
            }
            System.out.println();
        }
    }
    
    // Mapa de densidad para saber por donde ya hemos visitado
    public static void densityMapGeneration(){
        for(int i = 0; i < GLOBALX; i++){
            for(int j = 0; j < GLOBALY; j++){
                densityMap[i][j] = 0;
            }
        }
    }
    
    // Metodo para la generacion del mapa aleatorio
    public static int mapGeneration(int rocks, int obstacles){
        Random rand = new Random();
        int totalCells = GLOBALX*GLOBALY;
        int currentSamples = 0;
        
        // Recorrido de la matriz
        for(int i = 0; i < GLOBALY; i++){
            for(int j = 0; j < GLOBALX; j++){
                // Condicion para la generacion de las celdas de  obstaculos y muestras
                if(currentSamples < rocks){
                    int random = rand.nextInt(totalCells);
                    
                    if(random < rocks){
                        // Una de cada 3 rocas va a ser una muestra
                        map[i][j] = rand.nextInt(3) == 1 ? 2 : 1;
                        currentSamples++;
                    }
                    else if(random > (totalCells - obstacles)){
                        map[i][j] = 3;
                    }
                    else{
                        map[i][j] = 0;
                    }
                }
                else{
                    map[i][j] = 0;
                }
            }
        }
        
        return currentSamples;
    }
    
    // Retorna la posicion de la roca mas cercana para ser el siguiente objetivo
    public static Coord scanNeerestRock(Coord currentLoc, int scope){
        Coord neerRock = new Coord();
        int minDistance = scope * 2;
        
        // Se hacen los minimos y maximos para no salirnos de la matriz 
        // tomando en cuenta el alcance del radar que se define por scope
        int minX = currentLoc.x - scope > 0 ? (currentLoc.x - scope) : 0;
        int maxX = currentLoc.x + scope < GLOBALX ? (currentLoc.x + scope) : GLOBALX;
        
        int minY = currentLoc.y - scope > 0 ? (currentLoc.y - scope) : 0;
        int maxY = currentLoc.y + scope < GLOBALY ? (currentLoc.y + scope) : GLOBALY;
        
        // Se busca alrededor del robot por la roca mas cercana
        for(int i = minY; i < maxY; i++){
            for(int j = minX; j < maxX; j++){
                if(map[i][j] == 1 || map[i][j] == 2){
                    int distance = Math.abs(j - currentLoc.x) + Math.abs(i - currentLoc.y);
                    if(distance < minDistance){
                        neerRock.x = j;
                        neerRock.y = i;
                        minDistance = distance;
                    }
                }
            }
        }
        return neerRock;
    }
    
    // Retorna el nuebo objetivo cuando se sabe que no hay una roca cercana
    public static Coord nextNonRockObjective(Coord currentLoc, int step){
        Coord newObj = new Coord();
        // Array para contar los elementos visitaddos en cada cuadrante
        // 0 = norOeste, 1 = norEste, 2 = surOeste, 3 = surEste
        int [] directions = {0,0,0,0};
        
        // Se cuentan los espacios visitados por cuadrante para saber cual direccion tomar
        for(int i = 0; i < GLOBALY; i++){
            for(int j = 0; j < GLOBALX; j++){
                if(densityMap[i][j] == 0){
                    if(i < currentLoc.y){ // Norte
                        if(j < currentLoc.x){ // Oeste
                            directions[0]++;
                        }
                        else{ // Este
                            directions[1]++;
                        }
                    }
                    else{ // Sur
                        if(j < currentLoc.x){ // Oeste
                            directions[2]++;
                        }
                        else{ // Este
                            directions[3]++;
                        }
                    }
                }
            }
        }
        
        /*
        // Impresion de valores
        System.out.println("-----------VALORES POR CUADRANTE--------");
        for(int i = 0; i < 4; i++){
            System.out.println(i + " = " +directions[i]);
        }
        */
        
        // Se obtiene el maximo de los cuadrantes
        int max = 0;
        for(int i = 1; i < directions.length; i++){
            if(directions[i] > directions[max]){
                max = i;
            }
        }
        Random rand = new Random();
        int randStepX = rand.nextInt(step);
        int randStepY = rand.nextInt(step);
        
        
        // Se obtiene el nuevo objetivo de acuerdo al cuadrante al que se va a mover el robot
        switch(max){
            case 0:
                newObj.x = currentLoc.x - randStepX > 0 ? currentLoc.x - randStepX : 0;
                newObj.y = currentLoc.y - randStepY > 0 ? currentLoc.y - randStepY : 0;
                break;
            case 1:
                // Se hace GLOBALX -1 porque sino se sale de la matriz
                newObj.x = currentLoc.x + randStepX < GLOBALX ? currentLoc.x + randStepX : GLOBALX -1;
                newObj.y = currentLoc.y - randStepY > 0 ? currentLoc.y - randStepY : 0;
                break;
            case 2:
                newObj.x = currentLoc.x - randStepX > 0 ? currentLoc.x - randStepX : 0;
                newObj.y = currentLoc.y + randStepY < GLOBALY ? currentLoc.y + randStepY : GLOBALY -1;
                break;
            case 3:
                newObj.x = currentLoc.x + randStepX < GLOBALX ? currentLoc.x + randStepX : GLOBALX -1;
                newObj.y = currentLoc.y + randStepY < GLOBALY ? currentLoc.y + randStepY : GLOBALY -1;
                break;
            default:
                break;
        }
        
        
        return newObj;
    }
    
    // Decide cual va a ser el nuevo objetivo
    public static Coord objectiveSelection(Robot explorer, Coord currentLoc){
        Coord newObj = new Coord();
        
        // Si tiene las muestras entonces el objetivo es la nave
        if(explorer.state == 1){
            newObj.x = SHIPX;
            newObj.y = SHIPY;
            explorer.leaveSamples();
        }
        // Si no tiene muestras busca la siguiente roca mas cercana
        else{
            Coord neerRock = scanNeerestRock(currentLoc, 5);
            // Si no encontro ninguna roca entonces decide nuevo objetivo
            if(neerRock.x == -1){
                newObj = nextNonRockObjective(currentLoc, 6);
            }
            // Si si encontro roca cercana entonces ese es nuevo objetivo
            else{
                newObj = neerRock;
            }
        }
        return newObj;
    }
    
    // Metodo para cargar las imagenes de las rocas y las muestras
    public void loadRenderables() {
        for(int i = 0; i < GLOBALY; i++){
            for(int j = 0; j < GLOBALX; j++){
                if(map[i][j] == 3){
                    Sprite rock = new Sprite(getImage("images/rock60.png"), j*PIXELSTEP, i*PIXELSTEP);
                    obstacles.add(rock);
                }
                if(map[i][j] == 1 || map[i][j] == 2){
                    Sprite possibleSample = new Sprite(getImage("images/sample60.png"), j*PIXELSTEP, i*PIXELSTEP);
                    rocks.add(possibleSample);
                }
            }
        }
    }

    @Override
    public void initResources() {
        // Creacion del pincel
        Font verdana = new Font("Verdana", Font.BOLD, 12);
        pencil = new SystemFont(verdana, Color.white);
        // Render de la nave
        ship = new Sprite(getImage("images/ship60.png"), SHIPX*PIXELSTEP, SHIPY*PIXELSTEP);
        
        // Render del robot y la muestra
        robotSprite = new Sprite(getImage("images/robot60.png"), PIXELSTEP*ROBOTX, PIXELSTEP*ROBOTY);
        sample = new Sprite(getImage("images/diamond60.png"), 0, 0);
        
        
        // Generar los obstaculos y las muestras
        loadRenderables();
        
        // Fondo del escenario
        backgnd = new ImageBackground(getImage("images/terrain12.png"));
        backgnd.setClip(0, 0, SIZEX/3, SIZEY/3);
        
        // Fijar todo al background
        rocks.setBackground(backgnd);
        obstacles.setBackground(backgnd);
        ship.setBackground(backgnd);
        robotSprite.setBackground(backgnd);
        sample.setBackground(backgnd);
    }

    @Override
    public void update(long elapsedTime) {
        //execute();
        robotSprite.setX(PIXELSTEP*globalShip.x);
        robotSprite.setY(PIXELSTEP*globalShip.y);
        sample.setX(PIXELSTEP*globalShip.x);
        sample.setY(PIXELSTEP*globalShip.y);
        backgnd.setToCenter(robotSprite);
    }
    
    @Override
    public void render(Graphics2D g) {
        backgnd.render(g);
        obstacles.render(g);
        rocks.render(g);
        ship.render(g);
        robotSprite.render(g);
        pencil.drawString(g, "Picked Samples: "+ explorer.samples, 0, 0);
        
        if(showSample == true){
            sample.render(g);
        }
    }
}
