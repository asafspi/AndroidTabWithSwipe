package inducesmile.com.androidtabwithswipe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import myappDB.MovieDBhelper;


public class DBAdapter extends SimpleCursorAdapter {

    public static final String TAG = "ZZZ-DBAdapter";
    public static final String COL_ID = "_id";
    public static final String COL_TITLE = "title";
    public static final String COL_URL_ID = "urlid";
    public static final String COL_DURATION = "duration";
    private Context appContext;
    private int layout;
    private final LayoutInflater inflater;
    MovieDBhelper helper;
    private CallBack mCallBack;
    public static String PACKAGE_NAME;

    public DBAdapter(Context context, int layout, Cursor c, String[] from, int[] to, CallBack mCallBack) {
        super(context, layout, c, from, to);
        this.layout = layout;
        this.inflater = LayoutInflater.from(context);
        this.mCallBack = mCallBack;

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout, null);

    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        super.bindView(view, context, cursor);
        PACKAGE_NAME = context.getApplicationContext().getPackageName();
        //     Log.d("TEST", "bindView");
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView channel = (TextView) view.findViewById(R.id.channel);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageList);

        final int titleIndex = cursor.getColumnIndexOrThrow(COL_TITLE);
        final int durationIndex = cursor.getColumnIndexOrThrow(COL_DURATION);
        int urlIdIndex = cursor.getColumnIndexOrThrow(COL_URL_ID);
        int idIndex = cursor.getColumnIndexOrThrow(COL_ID);

        title.setText(cursor.getString(titleIndex));
        channel.setText(cursor.getString(urlIdIndex));
        String imgUrl = cursor.getString(durationIndex);
        final String videoId = cursor.getString(idIndex);

        Picasso.with(context).load(imgUrl).into(imageView);
        Button buttonShare = (Button) view.findViewById(R.id.buttonShare);
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                View parentView = (View) view.getParent();
                TextView title = (TextView) parentView.findViewById(R.id.title);
                // String shareBody = "Check out this movie\n" + title.getText()+ "\n" + "On - https://play.google.com/store/apps/details?id=com.guide.tipsforclash";
                String shareBody = "Check out this movie\n" + title.getText() + "\n" + "On - https://play.google.com/store/apps/details?id=" + PACKAGE_NAME;

                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hey! ");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        Button buttonFavorites = (Button) view.findViewById(R.id.buttonFavorites);
        buttonFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setIcon(R.drawable.ic_delete)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this video?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "buttonFavorites onClick");
                helper = new MovieDBhelper(context, "movie.db", null, 1);
                helper.deleteMovie(videoId);
                if (mCallBack != null) mCallBack.onRefresh();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    interface CallBack {
        void onRefresh();
    }
}