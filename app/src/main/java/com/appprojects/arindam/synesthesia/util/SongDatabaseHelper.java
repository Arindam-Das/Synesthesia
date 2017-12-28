package com.appprojects.arindam.synesthesia.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * OrmLiteSqliteOpenHelper helper for storing Song objects in ORM SQLiteDatabase.
 * @author Arindam Das
 * @version 13-12-2017.
 */

public class SongDatabaseHelper extends OrmLiteSqliteOpenHelper {

    //file name for thr data base file to be used by this app
    private static final String DATABASE_NAME = "synesthesia_songs.db";

    //current version of the database
    private static int DATABASE_VERSION = 5;

    /* Data Access object for Song objects in our database */
    private Dao<Song, String> dao = null;

    //Object for handling exception from the Dao
    private RuntimeExceptionDao<Song, String> runtimeExceptionDao;

    private static final AtomicInteger usageCounter = new AtomicInteger(0);

    private static SongDatabaseHelper helper = null;

    /**
     * Constructor for objects of class SongDatabaseHelper
     * @param context {@link Context} in which this constructor is invoked.
     */
    private SongDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /** Returns a new or a pre-cached instance of SongDatabaseHelper
     * @param context {@link Context} in which this method is invoked.
     */
    public static synchronized SongDatabaseHelper getHelper(Context context){
        if(helper == null)
            helper = new SongDatabaseHelper(context);
        usageCounter.incrementAndGet();
        return helper;
    }

    /**
     * <p>Returns the DAO for Song objects. If present a cached version of the
     * Dao is returned. Otherwise a new instance is obtained with
     * {@link OrmLiteSqliteOpenHelper#getDao(Class)}</p>
     *
     * @return {@link Dao} for {@link Song} objects
     * @throws SQLException if {@link OrmLiteSqliteOpenHelper#getDao(Class)}
     * throws {@link SQLException}
     */
    public Dao<Song, String> getDao() throws SQLException{
        if(dao == null)
            dao = getDao(Song.class);
        return dao;
    }

    /**
     * Returns a new intsance of cahced version of the exception handler DAO.
     * @return the {@link RuntimeExceptionDao} for the corresponding {@link Dao}
     * of this class.
     */
    public RuntimeExceptionDao<Song, String> getRuntimeExceptionDao() {
        if(runtimeExceptionDao == null)
            runtimeExceptionDao = getRuntimeExceptionDao(Song.class);
        return runtimeExceptionDao;
    }

    /**
     * <p>
     * Invoked when the database is first created. All tables are created here.
     * In essence this method contains invocations to the
     * {@link com.j256.ormlite.table.TableUtils#createTable(ConnectionSource, Class)}
     * method for the creation of the tables required by the app.
     * </p>
     * @param sqLiteDatabase the{@link SQLiteDatabase} database for storing objects
     * @param connectionSource the {@link ConnectionSource} to our Database
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            Log.e(SongDatabaseHelper.class.getName(),
                    "Creating table in SongDatabaseHelper.onCreate()");
            TableUtils.createTable(connectionSource, Song.class);
        } catch (SQLException e) {
            Log.d(SongDatabaseHelper.class.getName(), "Can't create database.");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * Invoked when the database backing our application needs to be upgraded.
     * In effect we drop the existing table for Songs in our database with
     * {@link TableUtils#dropTable(ConnectionSource, Class, boolean)} and re-invoke
     * {@link SongDatabaseHelper#onCreate(SQLiteDatabase, ConnectionSource)} for reinitializing our
     * database.
     * </p>
     * @param sqLiteDatabase the database to be upgraded
     * @param connectionSource the connectionSource to our database
     * @param oldVersion old DATABASE_VERSION
     * @param newVersion new DATABASE_VERSION
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            Log.e(SongDatabaseHelper.class.getName(), "Dropping tables from the database.");
            TableUtils.dropTable(connectionSource, Song.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            Log.d(SongDatabaseHelper.class.getName(), "Can't drop tables");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Close the database connections and clear any cached DAOs. For each call to {@link #getHelper(Context)}, there
     * should be 1 and only 1 call to this method. If there were 3 calls to {@link #getHelper(Context)} then on the 3rd
     * call to this method, the helper and the underlying database connections will be closed.
     */
    @Override
    public void close() {
        if (usageCounter.decrementAndGet() == 0) {
            super.close();
            this.dao = null;
            helper = null;
        }
    }
}
