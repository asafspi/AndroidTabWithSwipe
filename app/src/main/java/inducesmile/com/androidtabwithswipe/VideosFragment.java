package inducesmile.com.androidtabwithswipe;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dailymotion.websdk.DMWebVideoView;
import com.ironsource.mobilcore.MobileCore;
import com.startapp.android.publish.Ad;
import com.startapp.android.publish.AdDisplayListener;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import myappDB.MovieDBhelper;

public class VideosFragment extends Fragment implements AdapterView.OnItemClickListener {
    public static final String TAG = "ZZZ-VideosFragment";
    private MovieDBhelper helper;
    private DMWebVideoView mVideoView;
    private Movie movie;
    private SimpleCursorAdapter adapter = null;
    private EditText search;
    private TextView tvMainTitle;
    private VideosFragment tab;
    private Button mainLikeButton, mainShareeButton;
    private ListView listViewUrl;
    private SearchAsyncTask searchFor;
    private  int minimum = 1;
    private final String startApp = "208897484";
    private boolean b = true;
    public VideosFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        StartAppSDK.init(getActivity(), startApp, true);
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        setViewsAndListeners(view);

        ((MainActivity)getActivity()).startAppAd.showAd(); // show the ad
        ((MainActivity)getActivity()).startAppAd.loadAd(); // load the next ad
        mVideoView.setVideoId("x346bo6");
        mVideoView.setAutoPlay(false);
        setRetainInstance(true);
        String query = getActivity().getIntent().getStringExtra("query");
        String videoId = getActivity().getIntent().getStringExtra("videoId");
        String videoTitle = getActivity().getIntent().getStringExtra("videoTitle");



        if (query != null && query.length() > 0) {
            searchVideos(query);
        } else if (videoId != null && videoId.length() > 0) {
            mVideoView.setVideoId(videoId);
            mVideoView.setAutoPlay(true);
            searchVideos(videoTitle);
        } else {
            searchVideos("funny");
        }
        return view;
    }

    public void searchVideos(String keyword) {
        Log.d(TAG, "searchVideos: " + keyword);
        searchFor = new SearchAsyncTask();
        searchFor.execute(keyword);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.mainShareButton:
                    onMainShareBtnClick();
                    break;
                case R.id.mainLikeButton:
                    onMainLikeBtnClick();
                    break;
            }
        }
    };

    private void onMainShareBtnClick() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Check out this movie\n " + tvMainTitle.getText() + "\n" + " On - https://play.google.com/store/apps/details?id=com.guide.tipsforclash";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hey! ");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void onMainLikeBtnClick() {
        final MovieDBhelper helper = new MovieDBhelper(getActivity(), "movie.db", null, 1);
        // Movie m = new Movie(tvMainTitle.getText().toString(), textView4.getText().toString(), textView3.getText().toString(),  textView.getText().toString(), null);
        Movie m = new Movie(tvMainTitle.getText().toString(), null, movie.getId(), null, movie.getUrl());
        //  Movie m = new Movie(movie.getTitle(), null, movie.getId(), null, movie.getUrl());
        helper.insertMovie(m);
        mainLikeButton.setBackgroundResource(R.drawable.like_on);
        showSnackBar("The video was added to your favorites");
    }

    private void showSnackBar(String msg) {
        Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show();
    }

    private void setViewsAndListeners(View view) {
        mVideoView = ((DMWebVideoView) view.findViewById(R.id.dmWebVideoView2));
        mainShareeButton = (Button) view.findViewById(R.id.mainShareButton);
        tvMainTitle = (TextView) view.findViewById(R.id.videoTitleAbovePlayer);
        mainLikeButton = (Button) view.findViewById(R.id.mainLikeButton);
        listViewUrl = (ListView) view.findViewById(R.id.listViewUrl);
        listViewUrl.setOnItemClickListener(this);
        mainShareeButton.setOnClickListener(clickListener);
        mainLikeButton.setOnClickListener(clickListener);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            final long id) {

        if (b == true) {

            MobileCore.showInterstitial(getActivity(), null);
            changeBoolean();

        } else {

            ((MainActivity)getActivity()).startAppAd.showAd(); // show the ad
            ((MainActivity)getActivity()).startAppAd.loadAd(); // load the next ad
            changeBoolean();
        }
        TextView tvRowLayout = (TextView) view.findViewById(R.id.title);
        TextView tvIdRowLayout = (TextView) view.findViewById(R.id.id);
        tvMainTitle.setText(tvRowLayout.getText().toString());
        mVideoView.setVideoId(tvIdRowLayout.getText().toString());
        mVideoView.setAutoPlay(true);

    }

    @Override
    public void onPause() {
        super.onPause();
        mVideoView.onPause();
        if (searchFor != null) searchFor.cancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mVideoView.onPause();
        }
/*        mVideoView.handleBackPress(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mVideoView.onPause();
        }*/
    }

    private class SearchAsyncTask extends AsyncTask<String, Void, ArrayList<Movie>> {
        private ProgressDialog mDialog;
        private boolean running = true;

        public SearchAsyncTask() {
            //mDialog = new ProgressDialog(activity);
        }

        protected void onProgressUpdate(Integer... progress) {
            //mDialog.show();
        }

        protected void onPreExecute() {
//		 Reset the progress bar
//        mDialog.show();
//        mDialog.setCancelable(true);
//        mDialog.setMessage("Loading...");
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            ArrayList<Movie> result = getMovieData(params[0]);
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> stSearch) {
            super.onPostExecute(stSearch);
            //mDialog.dismiss();
            if (isVisible()) {
                if (stSearch != null) {
                    MyAdapter adapter = new MyAdapter(getActivity(), stSearch);
                    listViewUrl.setAdapter(adapter);
                } else {
                    AlertDialog ad = new AlertDialog.Builder(getActivity()).create();
                    ad.setMessage("No result found");
                    ad.show();
                }
            }
        }


        private ArrayList<Movie> getMovieData(String movie) {
            Movie[] stSearch = null;
            String urlString = "https://api.dailymotion.com/videos?fields=channel,id,thumbnail_180_url,title,duration_formatted&search=" + movie.replace(" ", "+") + "&limit=30";
            BufferedReader input = null;
            HttpURLConnection httpCon = null;
            String result = "";
            StringBuilder Builder = new StringBuilder();
            ArrayList<Movie> movies = new ArrayList<>();
            try {
                URL url = new URL(urlString);
                httpCon = (HttpURLConnection) url.openConnection();

                if (httpCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("Connection error", "Cannot connect to: " + urlString);

                }

                input = new BufferedReader(
                        new InputStreamReader(httpCon.getInputStream()));

                String line = input.readLine();
                Log.e("result: ", result);
                JSONObject obj = new JSONObject(line);
                JSONArray arr = obj.getJSONArray("list");


                for (int i = 0; i < arr.length(); i++) {
                    movies.add(new Movie(arr.getJSONObject(i).getString("title"), arr.getJSONObject(i).getString("channel"), arr.getJSONObject(i).getString("thumbnail_180_url"), arr.getJSONObject(i).getString("id"), arr.getJSONObject(i).getString("duration_formatted")));
                }

            } catch (Exception e) {
                Log.e("ERROR", "Error" + e.getMessage());
            }
            return movies;
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mVideoView.onResume();
        }
    }

    public void changeBoolean(){
        if( b == true){
            b = false;
        }else if(b == false){
            b = true;
        }
    }
}
