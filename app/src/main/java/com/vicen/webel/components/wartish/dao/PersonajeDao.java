package com.vicen.webel.components.wartish.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.vicen.webel.components.wartish.dbHelper.DBHelperMOS;
import com.vicen.webel.components.wartish.entidades.Personaje;

import java.sql.SQLException;
import java.util.List;


public class PersonajeDao extends DBHelperMOS {

    static Dao<Personaje, Integer> dao;
    public static void cargarDao(Context context) throws SQLException {
        dao = getHelper(context).getPersonajeDao();
    }
    //FUNCIONES DE CREACION
    public static boolean newPersonaje(Context context,int fk_usuario,String nombre_personaje,int nivel,int experiencia,int nivCasco, int nivArco, int nivEscudo, int nivGuantes, int nivBotas, int nivFlecha,int pepita,int hierro,int gema_bruto,int roca,int tronco,int lingote_oro, int lingote_hierro, int gema, int piedra, int tabla_madera){
        Personaje p = montarPersonaje(fk_usuario,nombre_personaje,nivel,experiencia,nivCasco, nivArco, nivEscudo, nivGuantes, nivBotas, nivFlecha,pepita,hierro,gema_bruto,roca,tronco,lingote_oro,lingote_hierro,gema, piedra, tabla_madera);
        return crearPersonaje(p,context);
    }
    public static boolean crearPersonaje(Personaje p,Context context){
        try {
            cargarDao(context);
            dao.create(p);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static Personaje montarPersonaje(int fk_usuario,String nombre_personaje,int nivel,int experiencia,int nivCasco, int nivArco, int nivEscudo, int nivGuantes, int nivBotas, int nivFlecha,int pepita,int hierro,int gema_bruto,int roca,int tronco,int lingote_oro, int lingote_hierro, int gema, int piedra, int tabla_madera){
        Personaje p = new Personaje(fk_usuario,nombre_personaje,nivel,experiencia, nivCasco, nivArco, nivEscudo, nivGuantes, nivBotas, nivFlecha,pepita,hierro,gema_bruto,roca,tronco,lingote_oro,lingote_hierro,gema,piedra,tabla_madera);
        return p;
    }
    //FUNCIONES DE BORRADO
    public static void borrarPersonaje(Context context)throws SQLException{
        cargarDao(context);
        DeleteBuilder<Personaje, Integer> deleteBuilder = dao.deleteBuilder();
        deleteBuilder.delete();
    }

    public static Personaje buscarPersonaje(Context context) throws SQLException {
        cargarDao(context);
        List<Personaje> listadoPersonaje= dao.queryForAll();
        if(listadoPersonaje.isEmpty()) {
            return null;
        }else{
            return listadoPersonaje.get(0);
        }
    }
    //FUNCIONES ACTUALIZAR
    public static void actualizarNombrePersonaje( Context context, String nombre_personaje) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        updateBuilder.updateColumnValue(Personaje.NOMBRE_PERSONAJE, nombre_personaje);
        updateBuilder.update();

    }
    public static void actualizarNivel( Context context, int nivel) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        updateBuilder.updateColumnValue(Personaje.NIVEL, nivel);
        updateBuilder.update();
        actualizarVida(context);
        actualizarAtaque(context);
        actualizarMagia(context);
        actualizarDefensa(context);
        actualizarVelocidad(context);
        actualizarCritico(context);
    }
    public static void actualizarExperiencia(Context context,int experiencia) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        updateBuilder.updateColumnValue(Personaje.EXPERIENCIA, experiencia);
        updateBuilder.update();
    }
    public static void actualizarNivelCasco( Context context, int nivelCasco) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        updateBuilder.updateColumnValue(Personaje.NIVEL_CASCO, nivelCasco);
        updateBuilder.update();

    }
    public static void actualizarNivelArco( Context context, int nivelArco) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        updateBuilder.updateColumnValue(Personaje.NIVEL_ARCO, nivelArco);
        updateBuilder.update();

    }
    public static void actualizarNivelEscudo( Context context, int nivelEscudo) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        updateBuilder.updateColumnValue(Personaje.NIVEL_ESCUDO, nivelEscudo);
        updateBuilder.update();

    }
    public static void actualizarNivelGuantes( Context context, int nivelGuantes) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        updateBuilder.updateColumnValue(Personaje.NIVEL_GUANTES, nivelGuantes);
        updateBuilder.update();

    }
    public static void actualizarNivelBotas( Context context, int nivelBotas) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        updateBuilder.updateColumnValue(Personaje.NIVEL_BOTAS, nivelBotas);
        updateBuilder.update();

    }
    public static void actualizarNivelFlecha( Context context, int nivelFlecha) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        updateBuilder.updateColumnValue(Personaje.NIVEL_FLECHA, nivelFlecha);
        updateBuilder.update();

    }
    public static void actualizarPepita( Context context, int pepita) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        pepita = pepita+buscarPersonaje(context).getPepita();
        updateBuilder.updateColumnValue(Personaje.PEPITA, pepita);
        updateBuilder.update();

    }
    public static void actualizarHierro( Context context, int hierro) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        hierro = hierro+buscarPersonaje(context).getHierro();
        updateBuilder.updateColumnValue(Personaje.HIERRO, hierro);
        updateBuilder.update();

    }
    public static void actualizarGemaBruto( Context context, int gema_bruto) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        gema_bruto = gema_bruto+buscarPersonaje(context).getGema_bruto();
        updateBuilder.updateColumnValue(Personaje.GEMA_BRUTO, gema_bruto);
        updateBuilder.update();

    }
    public static void actualizarRoca( Context context, int roca) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        roca = roca+buscarPersonaje(context).getRoca();
        updateBuilder.updateColumnValue(Personaje.ROCA, roca);
        updateBuilder.update();

    }
    public static void actualizarTronco( Context context, int tronco) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        tronco = tronco+buscarPersonaje(context).getTronco();
        updateBuilder.updateColumnValue(Personaje.TRONCO, tronco);
        updateBuilder.update();

    }
    public static void actualizarLingoteOro( Context context, int lingote_oro) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        lingote_oro = lingote_oro+buscarPersonaje(context).getLingote_oro();
        updateBuilder.updateColumnValue(Personaje.LINGOTE_ORO, lingote_oro);
        updateBuilder.update();

    }
    public static void actualizarLingoteHierro( Context context, int lingote_hierro) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        lingote_hierro = lingote_hierro+buscarPersonaje(context).getLingote_hierro();
        updateBuilder.updateColumnValue(Personaje.LINGOTE_HIERRO, lingote_hierro);
        updateBuilder.update();

    }
    public static void actualizarGema( Context context, int gema) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        gema = gema+buscarPersonaje(context).getGema();
        updateBuilder.updateColumnValue(Personaje.GEMA, gema);
        updateBuilder.update();

    }
    public static void actualizarPiedra( Context context, int piedra) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        piedra = piedra+buscarPersonaje(context).getPiedra();
        updateBuilder.updateColumnValue(Personaje.PIEDRA, piedra);
        updateBuilder.update();

    }
    public static void actualizarTablaMadera( Context context, int tabla_madera) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        tabla_madera = tabla_madera+buscarPersonaje(context).getTabla_madera();
        updateBuilder.updateColumnValue(Personaje.TABLA_MADERA, tabla_madera);
        updateBuilder.update();

    }
    private static void actualizarVida( Context context) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        int nivel = PersonajeDao.buscarPersonaje(context).getNivel();
        int casco = PersonajeDao.buscarPersonaje(context).getNivCasco();
        int vida = (nivel*100)+(casco*5);
        updateBuilder.updateColumnValue(Personaje.VIDA, vida);
        updateBuilder.update();

    }
    private static void actualizarAtaque( Context context) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        int nivel = PersonajeDao.buscarPersonaje(context).getNivel();
        int arco = PersonajeDao.buscarPersonaje(context).getNivArco();
        int flecha = PersonajeDao.buscarPersonaje(context).getNivFlecha();
        int ataque = (nivel*3)+(arco*2)+(flecha);
        updateBuilder.updateColumnValue(Personaje.ATAQUE, ataque);
        updateBuilder.update();

    }
    private static void actualizarMagia( Context context) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        int nivel = PersonajeDao.buscarPersonaje(context).getNivel();
        int guantes = PersonajeDao.buscarPersonaje(context).getNivGuantes();
        int magia = (nivel*3)+(guantes*2);
        updateBuilder.updateColumnValue(Personaje.MAGIA, magia);
        updateBuilder.update();

    }
    private static void actualizarDefensa( Context context) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        int nivel = PersonajeDao.buscarPersonaje(context).getNivel();
        int escudo = PersonajeDao.buscarPersonaje(context).getNivEscudo();
        int defensa = (nivel*3)+(escudo*2);
        updateBuilder.updateColumnValue(Personaje.DEFENSA, defensa);
        updateBuilder.update();

    }
    private static void actualizarVelocidad( Context context) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        int nivel = PersonajeDao.buscarPersonaje(context).getNivel();
        int botas = PersonajeDao.buscarPersonaje(context).getNivBotas();
        int guantes = PersonajeDao.buscarPersonaje(context).getNivGuantes();
        int velocidad =(nivel*3)+(botas*2)+(guantes);;
        updateBuilder.updateColumnValue(Personaje.VELOCIDAD, velocidad);
        updateBuilder.update();

    }
    private static void actualizarCritico( Context context) throws SQLException {
        cargarDao(context);
        UpdateBuilder<Personaje, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Personaje.ID_PERSONAJE,buscarPersonaje(context).getId_personaje());
        int nivel = PersonajeDao.buscarPersonaje(context).getNivel();
        int arco = PersonajeDao.buscarPersonaje(context).getNivArco();
        int guantes = PersonajeDao.buscarPersonaje(context).getNivGuantes();
        int flecha = PersonajeDao.buscarPersonaje(context).getNivFlecha();
        int critico =(nivel*3)+arco+guantes+flecha;
        updateBuilder.updateColumnValue(Personaje.CRITICO, critico);
        updateBuilder.update();

    }
}
