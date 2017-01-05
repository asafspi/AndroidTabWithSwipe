package myappDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import inducesmile.com.androidtabwithswipe.Movie;


public class MovieDBhelper extends SQLiteOpenHelper {

    public static final String TAG = "ZZZ-MovieDBhelper";
    public final String ID = "_id";
    public final String URLID = "urlid";
    public final String TITLE = "title";
    public final String URL = "url";
    public final String DURATION = "duration";
    public final String CHANNEL = "channel";

    public final String MOVIES = "movies";

    public MovieDBhelper(Context context, String name, CursorFactory factory,
                         int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table movies( _id integer primary key autoincrement," + "title text, urlid text, channel text, duration text, url text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    public void insertMovie(Movie m) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, m.getId());
        values.put(TITLE, m.getTitle());
        values.put(URLID, m.getUrl());
        values.put(CHANNEL, m.getChannel());
        values.put(DURATION, m.getDuration());
        //values.put(URL, m.getUrl() );

        db.insert(MOVIES, null, values);
    }

    public Cursor getMovie() {

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select _id, title, urlid, duration  from movies", null);
        return c;
    }

    public void deleteMovie(String videoId) {
        Log.d(TAG, "deleteMovie with videoId: " + videoId);
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE from movies WHERE _id=" + videoId);
    }

    public void deleteMovies() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("movies", null, null);
    }

    public Movie getMovieDetail(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * from movies WHERE _id=" + id, null);
        Movie m = null;
        if (c.moveToFirst()) {
            m = new Movie(c.getString(c.getColumnIndex("title")), c.getString(c.getColumnIndex("urlid")), null, c.getString(c.getColumnIndex("url")), null);
        }

        return m;
    }

    public Movie getMoviesDetail(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * from movies WHERE _id=" + id, null);
        Movie m = null;
        if (c.moveToFirst()) {
            do
                m = new Movie(c.getString(c.getColumnIndex("title")), c.getString(c.getColumnIndex("urlid")), null, c.getString(c.getColumnIndex("url")), null);
            while (c.moveToNext());
        }

        return m;
    }

    public int updateMovie(long id, Movie m) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, m.getId());
        values.put(TITLE, m.getTitle());
        values.put(CHANNEL, m.getChannel());
        values.put(DURATION, m.getDuration());
        values.put(URL, m.getUrl());

        return db.update("movies", values, "_id=?", new String[]{id + ""});
    }
}
