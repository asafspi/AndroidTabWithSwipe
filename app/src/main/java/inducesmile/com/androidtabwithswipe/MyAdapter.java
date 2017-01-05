package inducesmile.com.androidtabwithswipe;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import myappDB.MovieDBhelper;


public class MyAdapter extends ArrayAdapter<Movie> {
    public MyAdapter(Context context, ArrayList<Movie> movie) {
        super(context, 0, movie);
    }

    MainActivity mainActivity;
    VideosFragment blank;
    public static String PACKAGE_NAME;

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        Movie movie = getItem(position);
        PACKAGE_NAME = getContext().getApplicationContext().getPackageName();

        ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_layout, parent, false);
        }
        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView channel = (TextView) convertView.findViewById(R.id.channel);
        ImageView image = (ImageView) convertView.findViewById(R.id.imageList);

        TextView duration = (TextView) convertView.findViewById(R.id.duration);
        TextView id = (TextView) convertView.findViewById(R.id.id);

        Button buttonShare = (Button) convertView.findViewById(R.id.buttonShare);
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
                getContext().startActivity(Intent.createChooser(sharingIntent, "Share via"));
                sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            }
        });

        final Button buttonFavorites = (Button) convertView.findViewById(R.id.buttonFavorites);
        buttonFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View parentView = (View) view.getParent();
                //Movie movie = (Movie)parentView.getTag(R.string.movie_tag);
                // final int position = getgetPositionForView((View) view.getParent());
                Movie movie = getItem(position);
                final MovieDBhelper helper = new MovieDBhelper(getContext(), "movie.db", null, 1);
                // Movie m = new Movie(tvMainTitle.getText().toString(), textView4.getText().toString(), textView3.getText().toString(),  textView.getText().toString(), null);
                //  Movie m = new Movie(title.getText().toString(),null , id.getText().toString(), null, movie.getUrl());
                Movie m = new Movie(movie.getTitle(), null, movie.getId(), null, movie.getUrl());
                helper.insertMovie(m);
                //                  buttonFavorites.setBackgroundResource(R.drawable.like_on);
                //buttonFavorites.setBackgroundResource(R.drawable.like_on);
                Snackbar.make(view, "The video was added to your favorites", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
            }

        });

        viewHolder = new ViewHolder(title, image, channel, duration, null, id, buttonShare, buttonFavorites);
        convertView.setTag(R.string.view_holder_tag, viewHolder);

        // Populate the data into the template view using the data object


        viewHolder = (ViewHolder) convertView.getTag(R.string.view_holder_tag);
        viewHolder.title.setText(movie.getTitle());
        viewHolder.channel.setText(movie.getChannel());
        viewHolder.duration.setText(movie.getDuration());
        viewHolder.id.setText(movie.getId());
        viewHolder.image.getId();
        Picasso.with(getContext()).load(movie.getUrl()).into(viewHolder.image);

        return convertView;
    }

    class ViewHolder {
        TextView title;
        TextView channel;
        ImageView image;
        Button buttonShare;
        Button buttonFavorites;
        TextView duration;
        TextView url;
        TextView id;

        public ViewHolder(TextView title, ImageView image, TextView channel, TextView duration, TextView url, TextView id, Button buttonShare, Button buttonFavorites) {
            this.title = title;
            this.image = image;
            this.channel = channel;
            this.buttonFavorites = buttonFavorites;
            this.buttonShare = buttonShare;
            this.duration = duration;
            this.url = url;
            this.id = id;
        }
    }
}