package com.vicen.webel.components.wartish.dbHelper;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * 
 * @author Rodrigo</br>
 * Esta clase sirve para cargar el dbHelper, siempre que lo necesitemos simplemente debemos
 * extender de ella.
 * 
 */
public abstract class DBHelperMOS extends AppCompatActivity {
	public static DBHelper mDBHelper;
	
	protected static DBHelper getHelper(Context context) {
		if (mDBHelper == null) {
			mDBHelper = OpenHelperManager.getHelper(context, DBHelper.class);
		}
		return mDBHelper;
	}
}
