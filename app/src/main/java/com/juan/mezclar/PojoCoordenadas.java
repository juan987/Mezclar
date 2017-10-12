package com.juan.mezclar;

/**
 * Created by Juan on 09/10/2017.
 */


public class PojoCoordenadas {
    String coordX;
    String coordY;

    public PojoCoordenadas(){

    }

    @Override
    public String toString() {
        return "PojoCoordenadas{" +
                "coordX='" + coordX + '\'' +
                ", coordY='" + coordY + '\'' +
                '}';
    }

    public String getCoordX() {
        return coordX;
    }

    public void setCoordX(String coordX) {
        this.coordX = coordX;
    }

    public String getCoordY() {
        return coordY;
    }

    public void setCoordY(String coordY) {
        this.coordY = coordY;
    }


}
