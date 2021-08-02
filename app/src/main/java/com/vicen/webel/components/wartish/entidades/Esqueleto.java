package com.vicen.webel.components.wartish.entidades;

public class Esqueleto {
    private int vida;
    private int ataque;
    private int defensa;
    private int velocidad;
    private int critico;
    private int nivel;

    public Esqueleto(int nivel) {
        this.nivel = nivel;
        this.vida = Vida(nivel);
        this.ataque = Ataque(nivel);
        this.defensa = Defensa(nivel);
        this.velocidad = Velocidad(nivel);
        this.critico=Critico(nivel);
    }

    private int Vida(int nivel){
        int resulvid;
        resulvid = (nivel*20);
        return resulvid;
    }
    private int Ataque(int nivel){
        int resulata;
        resulata = (nivel*5);
        return resulata;
    }
    private int Defensa(int nivel){
        int resuldef;
        resuldef = (nivel*2);
        return resuldef;
    }
    private int Velocidad(int nivel){
        int resulvel;
        resulvel = (nivel*4);
        return resulvel;
    }
    private int Critico(int nivel){
        int resulcrit;
        resulcrit = (nivel*3);
        return resulcrit;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    public int getAtaque() {
        return ataque;
    }

    public void setAtaque(int ataque) {
        this.ataque = ataque;
    }

    public int getDefensa() {
        return defensa;
    }

    public void setDefensa(int defensa) {
        this.defensa = defensa;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }

    public int getCritico() {
        return critico;
    }

    public void setCritico(int critico) {
        this.critico = critico;
    }
}
