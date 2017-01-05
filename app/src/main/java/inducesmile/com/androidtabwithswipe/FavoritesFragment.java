package inducesmile.com.androidtabwithswipe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dailymotion.websdk.DMWebVideoView;

import myappDB.MovieDBhelper;

public class FavoritesFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnLongClickListener {
    public static final String TAG = "ZZZ-FavoritesFragment";
    private MovieDBhelper helper;
    private DMWebVideoView mVideoView;
    private Movie movie;
    private SimpleCursorAdapter adapter;
    private EditText search;
    private TextView textView, tvMainTitle, textView3, textViewEmptyFavorites;
    private VideosFragment tab;
    private ActionBar actionBar;
    private ListView list;
    private ImageView emptyFavorites;
    MainActivity mainActivity;

    public FavoritesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TEST", "onCreateView");
        View view = inflater.inflate(R.layout.online_search, container, false);
        list = (ListView) view.findViewById(R.id.listViewUrlFavorites);
        list.setOnItemClickListener(FavoritesFragment.this);
        list.setOnLongClickListener(FavoritesFragment.this);
        emptyFavorites = (ImageView)view.findViewById(R.id.empty_favorites);
        textViewEmptyFavorites = (TextView)view.findViewById(R.id.textViewEmptyFavorites);
        registerForContextMenu(list);
        loadDataFromDB();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            loadDataFromDB();
        }
    }

    public void loadDataFromDB() {
        helper = new MovieDBhelper(getActivity(), "movie.db", null, 1);
        String[] from = new String[]{"title", "urlid", "duration"};
        int to[] = new int[]{R.id.title, R.id.channel, R.id.id2};
        //     dapter = new SimpleCursorAdapter(getActivity(), R.layout.row_layout_favorites, helper.getMovie(), from, to, 2);

        DBAdapter adapter = new DBAdapter(getActivity(), R.layout.row_layout_favorites, helper.getMovie(), from, to, mCallBack);
        list.setAdapter(adapter);


        if(adapter.getCount()!=0){
            list.setAdapter(adapter);
            emptyFavorites.setVisibility(View.INVISIBLE);
            textViewEmptyFavorites.setVisibility(View.INVISIBLE);
        }else{
            emptyFavorites.setVisibility(View.VISIBLE);
            textViewEmptyFavorites.setVisibility(View.VISIBLE);
        }
//        if (adapter == null){
//
//                emptyFavorites = (ImageView) getActivity().findViewById(R.id.empty_favorites);
//                emptyFavorites.setVisibility(View.VISIBLE);
//
//        }
    }

    DBAdapter.CallBack mCallBack = new DBAdapter.CallBack() {
        @Override
        public void onRefresh() {
            loadDataFromDB();
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView channel = (TextView) view.findViewById(R.id.channel);
        TextView title = (TextView) view.findViewById(R.id.title);
        openVideo(channel.getText().toString(), title.getText().toString());

        Log.d(TAG, "id: " + id);
    }

    private void openVideo(String videoId, String videoTitle) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("videoId", videoId); //data is a string variable holding some value.
        intent.putExtra("videoTitle", videoTitle); //data is a string variable holding some value.
        //intent.putExtra("from favorites", "yes"); //data is a string variable holding some value.

        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v) {

        return false;
    }

    @Override
    public void onResume() {
        if (adapter != null) {
            adapter.changeCursor(helper.getMovie());
        }
        super.onResume();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.contex_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        switch (item.getItemId()) {
//            case R.id.ContextEdit:
//                editMovie(info.id);
//                return true;
//            case R.id.ContextDelete:
//                deleteMovie(info.id);
//                return true;
//            default:
        return super.onContextItemSelected(item);
    }

    private void editMovie(long id) {
//        getActionBar().setSelectedNavigationItem(0);
        //      Intent in = new Intent(this, AddManual.class);
        //      Movie mov = helper.getMovieDetail(id);

        //     in.putExtra("url", mov.getUrl());
        //     in.putExtra("title", mov.getTitle());
        //      startActivity(in);
        //getTabHost().setCurrentTab(1);
    }
}

    


