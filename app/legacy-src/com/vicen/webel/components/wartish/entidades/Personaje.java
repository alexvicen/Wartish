package com.vicen.webel.components.wartish.entidades;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "mos_personaje")
public class Personaje {

    public static final String ID_PERSONAJE = "_id_personaje";
    public static final String FK_USUARIO="fk_usuario";
    public static final String NOMBRE_PERSONAJE = "nombre_personaje";
    public static final String NIVEL = "nivel";
    public static final String EXPERIENCIA = "experiencia";
    public static final String NIVEL_CASCO = "nivCasco";
    public static final String NIVEL_ARCO = "nivArco";
    public static final String NIVEL_ESCUDO = "nivEscudo";
    public static final String NIVEL_GUANTES = "nivGuantes";
    public static final String NIVEL_BOTAS = "nivBotas";
    public static final String NIVEL_FLECHA = "nivFlecha";
    public static final String VIDA = "vida";
    public static final String ATAQUE = "ataque";
    public static final String MAGIA = "magia";
    public static final String DEFENSA = "defensa";
    public static final String VELOCIDAD = "velocidad";
    public static final String CRITICO = "critico";
    public static final String PEPITA = "pepita";
    public static final String HIERRO = "hierro";
    public static final String GEMA_BRUTO = "gema_bruto";
    public static final String ROCA = "roca";
    public static final String TRONCO = "tronco";
    public static final String LINGOTE_ORO = "lingote_oro";
    public static final String LINGOTE_HIERRO = "lingote_hierro";
    public static final String GEMA = "gema";
    public static final String PIEDRA = "piedra";
    public static final String TABLA_MADERA = "tabla_madera";

    @DatabaseField(generatedId = true, columnName = ID_PERSONAJE)
    private int id_personaje;
    @DatabaseField(columnName = FK_USUARIO)
    private int fk_usuario;
    @DatabaseField(columnName = NOMBRE_PERSONAJE)
    private String nombre_personaje;
    @DatabaseField(columnName = NIVEL)
    private int nivel;
    @DatabaseField(columnName = EXPERIENCIA)
    private int experiencia;
    @DatabaseField(columnName = NIVEL_CASCO)
    private int nivCasco;
    @DatabaseField(columnName = NIVEL_ARCO)
    private int nivArco;
    @DatabaseField(columnName = NIVEL_ESCUDO)
    private int nivEscudo;
    @DatabaseField(columnName = NIVEL_GUANTES)
    private int nivGuantes;
    @DatabaseField(columnName = NIVEL_BOTAS)
    private int nivBotas;
    @DatabaseField(columnName = NIVEL_FLECHA)
    private int nivFlecha;
    @DatabaseField(columnName = VIDA)
    private int vida;
    @DatabaseField(columnName = ATAQUE)
    private int ataque;
    @DatabaseField(columnName = MAGIA)
    private int magia;
    @DatabaseField(columnName = DEFENSA)
    private int defensa;
    @DatabaseField(columnName = VELOCIDAD)
    private int velocidad;
    @DatabaseField(columnName = CRITICO)
    private int critico;
    @DatabaseField(columnName = PEPITA)
    private int pepita;
    @DatabaseField(columnName = HIERRO)
    private int hierro;
    @DatabaseField(columnName = GEMA_BRUTO)
    private int gema_bruto;
    @DatabaseField(columnName = ROCA)
    private int roca;
    @DatabaseField(columnName = TRONCO)
    private int tronco;
    @DatabaseField(columnName = LINGOTE_ORO)
    private int lingote_oro;
    @DatabaseField(columnName = LINGOTE_HIERRO)
    private int lingote_hierro;
    @DatabaseField(columnName = GEMA)
    private int gema;
    @DatabaseField(columnName = PIEDRA)
    private int piedra;
    @DatabaseField(columnName = TABLA_MADERA)
    private int tabla_madera;

    public Personaje(){

    }

    public Personaje(int fk_usuario,String nombre_personaje,int nivel,int experiencia,int nivCasco, int nivArco, int nivEscudo, int nivGuantes, int nivBotas, int nivFlecha,
                     int pepita,int hierro,int gema_bruto,int roca,int tronco,int lingote_oro, int lingote_hierro, int gema, int piedra, int tabla_madera) {
        this.fk_usuario=fk_usuario;
        this.nombre_personaje=nombre_personaje;
        this.nivel = nivel;
        this.experiencia=experiencia;
        this.nivCasco = nivCasco;
        this.nivArco = nivArco;
        this.nivEscudo = nivEscudo;
        this.nivGuantes = nivGuantes;
        this.nivBotas = nivBotas;
        this.nivFlecha = nivFlecha;
        this.pepita=pepita;
        this.hierro=hierro;
        this.gema_bruto=gema_bruto;
        this.roca=roca;
        this.tronco=tronco;
        this.lingote_oro=lingote_oro;
        this.lingote_hierro=lingote_hierro;
        this.gema=gema;
        this.piedra=piedra;
        this.tabla_madera=tabla_madera;
        this.vida = Vida(nivel,nivCasco);
        this.ataque = Ataque(nivel,nivArco,nivFlecha);
        this.magia = Magia(nivel,nivGuantes);
        this.defensa = Defensa(nivel,nivEscudo);
        this.velocidad = Velocidad(nivel,nivBotas,nivGuantes);
        this.critico=Critico(nivel,nivArco,nivGuantes,nivFlecha);
    }
    public Personaje(int nivel){
        this.nivel = nivel;
        this.experiencia=0;
        int nivCasco=0;
        this.nivCasco = nivCasco;
        int nivArco=0;
        this.nivArco = nivArco;
        int nivEscudo=0;
        this.nivEscudo = nivEscudo;
        int nivGuantes=0;
        this.nivGuantes = nivGuantes;
        int nivBotas=0;
        this.nivBotas = nivBotas;
        int nivFlecha=0;
        this.nivFlecha = nivFlecha;

        this.vida = Vida(nivel,nivCasco);
        this.ataque = Ataque(nivel,nivArco,nivFlecha);
        this.magia = Magia(nivel,nivGuantes);
        this.defensa = Defensa(nivel,nivEscudo);
        this.velocidad = Velocidad(nivel,nivBotas,nivGuantes);
        this.critico=Critico(nivel,nivArco,nivGuantes,nivFlecha);
    }
    private int Vida(int nivel, int casco){
        int resulvid;
        resulvid = (nivel*70)+(casco*5);
        return resulvid;
    }
    private int Ataque(int nivel, int arco,int flecha){
        int resulata;
        resulata = (nivel*3)+(arco*2)+(flecha);
        return resulata;
    }
    private int Magia(int nivel, int guantes){
        int resulata;
        resulata = (nivel*3)+(guantes*2);
        return resulata;
    }
    private int Defensa(int nivel,int escudo){
        int resuldef;
        resuldef = (nivel*3)+(escudo*2);
        return resuldef;
    }
    private int Velocidad(int nivel,int botas,int guantes){
        int resulvel;
        resulvel = (nivel*3)+(botas*2)+(guantes);
        return resulvel;
    }
    private int Critico(int nivel,int arco,int guantes,int flecha){
        int resulcrit;
        resulcrit = (nivel*3)+arco+guantes+flecha;
        return resulcrit;
    }

    public String getNombre_personaje() {
        return nombre_personaje;
    }
    public void setNombre_personaje(String nombre_personaje) {
        this.nombre_personaje = nombre_personaje;
    }
    public int getId_personaje() {
        return id_personaje;
    }
    public void setId_personaje(int id_personaje) {
        this.id_personaje = id_personaje;
    }
    public int getNivel() {
        return nivel;
    }
    public void setNivel(int nivel) {
        this.nivel = nivel;
    }
    public int getNivCasco() {
        return nivCasco;
    }
    public void setNivCasco(int nivCasco) {
        this.nivCasco = nivCasco;
    }
    public int getNivArco() {
        return nivArco;
    }
    public void setNivArco(int nivArco) {
        this.nivArco = nivArco;
    }
    public int getNivEscudo() {
        return nivEscudo;
    }
    public void setNivEscudo(int nivEscudo) {
        this.nivEscudo = nivEscudo;
    }
    public int getNivGuantes() {
        return nivGuantes;
    }
    public void setNivGuantes(int nivGuantes) {
        this.nivGuantes = nivGuantes;
    }
    public int getNivBotas() {
        return nivBotas;
    }
    public void setNivBotas(int nivBotas) {
        this.nivBotas = nivBotas;
    }
    public int getNivFlecha() {
        return nivFlecha;
    }
    public void setNivFlecha(int nivFlecha) {
        this.nivFlecha = nivFlecha;
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
    public int getMagia() {
        return magia;
    }
    public void setMagia(int magia) {
        this.magia = magia;
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
    public static String getIdPersonaje() {
        return ID_PERSONAJE;
    }
    public int getPepita() {
        return pepita;
    }
    public void setPepita(int pepita) {
        this.pepita = pepita;
    }
    public int getHierro() {
        return hierro;
    }
    public void setHierro(int hierro) {
        this.hierro = hierro;
    }
    public int getGema_bruto() {
        return gema_bruto;
    }
    public void setGema_bruto(int gema_bruto) {
        this.gema_bruto = gema_bruto;
    }
    public int getRoca() {
        return roca;
    }
    public void setRoca(int roca) {
        this.roca = roca;
    }
    public int getTronco() {
        return tronco;
    }
    public void setTronco(int tronco) {
        this.tronco = tronco;
    }
    public int getLingote_oro() {
        return lingote_oro;
    }
    public void setLingote_oro(int lingote_oro) {
        this.lingote_oro = lingote_oro;
    }
    public int getLingote_hierro() {
        return lingote_hierro;
    }
    public void setLingote_hierro(int lingote_hierro) {
        this.lingote_hierro = lingote_hierro;
    }
    public int getGema() {
        return gema;
    }
    public void setGema(int gema) {
        this.gema = gema;
    }
    public int getPiedra() {
        return piedra;
    }
    public void setPiedra(int piedra) {
        this.piedra = piedra;
    }
    public int getTabla_madera() {
        return tabla_madera;
    }
    public void setTabla_madera(int tabla_madera) {
        this.tabla_madera = tabla_madera;
    }
    public int getFk_usuario() {
        return fk_usuario;
    }
    public void setFk_usuario(int fk_usuario) {
        this.fk_usuario = fk_usuario;
    }
    public int getExperiencia() {
        return experiencia;
    }
    public void setExperiencia(int experiencia) {
        this.experiencia = experiencia;
    }
}
