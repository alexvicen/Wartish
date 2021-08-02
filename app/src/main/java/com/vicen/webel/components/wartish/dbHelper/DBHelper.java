package com.vicen.webel.components.wartish.dbHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.vicen.webel.components.wartish.constats.BBDDConstantes;
import com.vicen.webel.components.wartish.entidades.Personaje;

import java.sql.SQLException;

/**
 * 
 * Esta clase se encarga de crear, actualizar y proporcionar acceso a la base de datos.</br> En las primeras lineas se declaran el nombre de la base de datos y la version.</br> A continuacion se
 * declaran los DAO, que nos permitiran realizar todas las operaciones en base de datos, cada objeto del modelo tiene un DAO asociado.</br> Los metodos con public Dao... sirven para recuperar los DAO,
 * crean el DAO si no esta inicializado o lo devuelven si ya esta creado.
 * 
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {

	private Context context;

	/**
	 * Constructor del helper
	 * 
	 * @param context
	 */
	public DBHelper(Context context) {
		super(context, BBDDConstantes.DATABASE_NAME, null, BBDDConstantes.DATABASE_VERSION);
		this.context = context;
	}

	/**
	 * Metodo implementado que crea las tablas, llama a la funcion crearTablas que se encarga de ello.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			BBDDConstantes.crearTablas(connectionSource);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			BBDDConstantes.borrarTablas(connectionSource);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		onCreate(db, connectionSource);
	}

	/**
	 * Este metodo libera los recursos
	 */
	@Override
	public void close() {
		BBDDConstantes.cerrarDao();
	}

	public Dao<Personaje, Integer> getPersonajeDao() throws SQLException {
		if (BBDDConstantes.personajeDao == null) {
			BBDDConstantes.personajeDao = getDao(Personaje.class);
		}
		return BBDDConstantes.personajeDao;
	}


}
