/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exploradormarte;

import java.util.Random;
/**
 *
 * @author Lalo
 */
public class ExploradorMarte {
    // Coordenadas de la nave
    public static final int SHIPX = 20;
    public static final int SHIPY = 20;
    
    // Dimension del mapa a explorar
    public static final int GLOBALX = 20;
    public static final int GLOBALY = 20;
    
    // Creacion del mapa
    public static int [][] map = new int [GLOBALX][GLOBALY];
    // Creacion de mapa de densidad
    public static int [][] densityMap = new int [GLOBALX][GLOBALY];
    
    public static void main(String[] args) {
        map = mapGeneration(20);
        // Desicion de la posicion de la nave
        // While (hay rocas en el planeta)
        // Seleccionar nuevo objetivo
        // While(no estoy en mi objetivo)Moverme al objetivo
        printMatrix();
    }
    
    public static void printMatrix(){
        for(int i = 0; i < GLOBALX; i++){
            for(int j = 0; j < GLOBALY; j++){
                System.out.print(map[i][j]+ " ");
            }
            System.out.println();
        }
    }
    
    public static void densityMapGeneration(){
        for(int i = 0; i < GLOBALX; i++){
            for(int j = 0; j < GLOBALY; j++){
                densityMap[i][j] = 0;
            }
        }
    }
    
    public static int[][] mapGeneration(int rocks){
        Random rand = new Random();
        int totalCells = GLOBALX*GLOBALY;
        int currentSamples = 0;
        
        for(int i = 0; i < GLOBALX; i++){
            for(int j = 0; j < GLOBALY; j++){
                if(currentSamples < rocks){
                    int random = rand.nextInt(totalCells);
                    // System.out.println(random);
                    
                    if(random < rocks){
                        map[i][j] = rand.nextInt(3) == 1 ? 2 : 1;
                        currentSamples++;
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
        
        return map;
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
        for(int i = minX; i < maxX; i++){
            for(int j = minY; j < maxY; j++){
                if(map[i][j] == 1 || map[i][j] == 2){
                    int distance = Math.abs(i - currentLoc.x) + Math.abs(j - currentLoc.y);
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
        // 0 = nw, 1 = ne, 2 = sw, 3 = se
        int [] directions = new int[4];
        
        // Se cuentan los espacios visitados por cuadrante para saber cual direccion tomar
        for(int i = 0; i < GLOBALY; i++){
            for(int j = 0; j < GLOBALX; j++){
                if(i < currentLoc.y){ // North
                    if(j < currentLoc.x){ // West
                        directions[0]++;
                    }
                    else{ // East
                        directions[1]++;
                    }
                }
                else{ // South
                    if(j < currentLoc.x){ // West
                        directions[2]++;
                    }
                    else{ // East
                        directions[3]++;
                    }
                }
            }
        }
        
        // Se obtiene el maximo de los cuadrantes
        int max = 0;
        for(int i = 1; i < directions.length; i++){
            if(directions[i] > directions[max]){
                max = i;
            }
        }
        
        // Se obtiene el nuevo objetivo de acuerdo al cuadrante al que se va a mover el robot
        switch(max){
            case 0:
                newObj.x = currentLoc.x - step > 0 ? currentLoc.x - step : 0;
                newObj.y = currentLoc.y - step > 0 ? currentLoc.y - step : 0;
            case 1:
                // Se hace GLOBALX -1 porque sino se sale de la matriz
                newObj.x = currentLoc.x + step < GLOBALX ? currentLoc.x + step : GLOBALX -1;
                newObj.y = currentLoc.y - step > 0 ? currentLoc.y - step : 0;
            case 2:
                newObj.x = currentLoc.x - step > 0 ? currentLoc.x - step : 0;
                newObj.y = currentLoc.y + step < GLOBALY ? currentLoc.y + step : GLOBALY -1;
            case 3:
                newObj.x = currentLoc.x + step < GLOBALX ? currentLoc.x + step : GLOBALX -1;
                newObj.y = currentLoc.y + step < GLOBALY ? currentLoc.y + step : GLOBALY -1;
            default:
                break;
        }
        
        
        return newObj;
    }
    
    // Decide cual va a ser el nuevo objetivo
    public static Coord objectiveSelection(Robot explorer, Coord currentLoc){
        Coord newObj = new Coord();
        
        // Si tiene las muestras entonces el objetivo es la nave
        if(explorer.samples == 1){
            newObj.x = SHIPX;
            newObj.y = SHIPY;
        }
        // Si no tiene muestras busca la siguiente roca mas cercana
        else{
            Coord neerRock = scanNeerestRock(currentLoc, 5);
            // Si no encontro ninguna roca entonces decide nuevo objetivo
            if(neerRock.x == -1){
                newObj = nextNonRockObjective(currentLoc, 5);
            }
            // Si si encontro roca cercana entonces ese es nuevo objetivo
            else{
                newObj = neerRock;
            }
        }
        return newObj;
    }
    
}
