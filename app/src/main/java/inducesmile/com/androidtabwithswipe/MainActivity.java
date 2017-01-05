package inducesmile.com.androidtabwithswipe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.dailymotion.websdk.DMWebVideoView;
import com.ironsource.mobilcore.CallbackResponse;
import com.ironsource.mobilcore.MobileCore;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;

import java.util.Locale;

import myappDB.MovieDBhelper;


public class MainActivity extends AppCompatActivity implements ActionBar.TabListener {

    private final String KEY_ADS_BOOLEAN = "keyAdsBoolean";
    public static final String TAG = "ZZZ-MainActivity";
    SectionsPagerAdapter mSectionsPagerAdapter;
    TabHost tabHost;
    ViewPager mViewPager;
    public DMWebVideoView mVideoView;
    android.app.ActionBar.Tab videosTab, favoritesTab;
    int icons[] = {R.drawable.videos_on, R.drawable.favorites_on};
    private MovieDBhelper helper;
    private FavoritesFragment favoritesFragment;
    private ListView list;
    private ImageView emptyFavorites;
    private TextView textViewEmptyFavorites;
    private final String MOBILE_HASH = "7HI5CZPBF7JBNSG0ZDZOZPKDEL9VF";
    private final String startApp = "208897484";
    private int minimum = 1;
    private boolean killApp = false;

    private boolean b;
    private static boolean dontDo = true;
    private static boolean firstTime = true;
    public StartAppAd startAppAd = new StartAppAd(this);
    public static String PACKAGE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MobileCore.init(this, MOBILE_HASH, MobileCore.LOG_TYPE.DEBUG, MobileCore.AD_UNITS.INTERSTITIAL);
        StartAppSDK.init(this, startApp, true);
        PACKAGE_NAME = getApplicationContext().getPackageName();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        b = prefs.getBoolean(KEY_ADS_BOOLEAN, true);
        if (dontDo) {
            if (b) {

                StartAppAd.showSplash(this, savedInstanceState);
                changeBoolean();
                dontDo = false;
            } else {

                MobileCore.showInterstitial(MainActivity.this, null);
                changeBoolean();
                dontDo = false;
            }
        }
        if (killApp) {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            finish();
        }

        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("");
        actionBar.setLogo(R.drawable.action_bar_logo);
        actionBar.setDisplayUseLogoEnabled(true);

        if (getIntent() != null) {
            Intent intent = getIntent();
            String message = intent.getStringExtra("message");
            Bundle bundle = new Bundle();
            bundle.putString("message", "From Activity");
            VideosFragment fragobj = new VideosFragment();
            fragobj.setArguments(bundle);
        }
        setViewPager();

    }

    private void setViewPager() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayUseLogoEnabled(true);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setSelectedNavigationItem(position);
            }

        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(actionBar.newTab().setText("   " + mSectionsPagerAdapter.getPageTitle(i)).setIcon(icons[i]).setTabListener(this));


        }
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 1) {
                    mVideoView = ((DMWebVideoView) findViewById(R.id.dmWebVideoView2));
                    mVideoView.onPause();
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    mVideoView = ((DMWebVideoView) findViewById(R.id.dmWebVideoView2));
                    mVideoView.onPause();

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        hideKeyboard();
        final MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            searchView.setFocusable(false);
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    onUserTextSubmit(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
            searchView.setFocusable(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void onUserTextSubmit(String query) {
        Log.d(TAG, "onQueryTextSubmit  query: " + query);
        mViewPager.setCurrentItem(0);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("query", query); //data is a string variable holding some value.
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                //  openSearch();
                Toast.makeText(MainActivity.this, "search", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_delete_all:
                list = (ListView) findViewById(R.id.listViewUrlFavorites);
                MovieDBhelper helper = new MovieDBhelper(getApplication(), "movie.db", null, 1);
                helper.deleteMovies();
                loadDataFromDB();
                emptyFavorites = (ImageView) findViewById(R.id.empty_favorites);
                textViewEmptyFavorites = (TextView) findViewById(R.id.textViewEmptyFavorites);
                emptyFavorites.setVisibility(View.VISIBLE);
                textViewEmptyFavorites.setVisibility(View.VISIBLE);

                return true;
            case R.id.share:

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Check out this Daily Motion player -" + "\n" + "https://play.google.com/store/apps/details?id=" + PACKAGE_NAME;
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hey! ");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

                return true;
            case R.id.rate:


                final Dialog dialog1 = new Dialog(MainActivity.this);
                dialog1.setTitle("Rate App");
                LinearLayout ll = new LinearLayout(MainActivity.this);
                ll.setOrientation(LinearLayout.VERTICAL);

                TextView tv = new TextView(MainActivity.this);
                tv.setText("If you enjoy using , please take a moment to rate it. Thanks for your support!");
                RatingBar rate = new RatingBar(MainActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(20, 20, 20, 20);
                rate.setLayoutParams(params);
                rate.setNumStars(5);

                rate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBarr, float rate, boolean fromUser) {
                        if (rate == 5) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PACKAGE_NAME)));
                            dialog1.dismiss();
                        } else {
//                            Toast.makeText(MainActivity.this, "Thank you!", Toast.LENGTH_LONG).show();
                            dialog1.dismiss();
                        }
                    }
                });
                ll.addView(rate);
                dialog1.setContentView(ll);
                dialog1.show();


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadDataFromDB() {
        helper = new MovieDBhelper(getApplication(), "movie.db", null, 1);
        String[] from = new String[]{"title", "urlid", "duration"};
        int to[] = new int[]{R.id.title, R.id.channel, R.id.id2};
        //     dapter = new SimpleCursorAdapter(getActivity(), R.layout.row_layout_favorites, helper.getMovie(), from, to, 2);

        DBAdapter adapter = new DBAdapter(getApplication(), R.layout.row_layout_favorites, helper.getMovie(), from, to, mCallBack);
        list.setAdapter(adapter);
    }

    DBAdapter.CallBack mCallBack = new DBAdapter.CallBack() {
        @Override
        public void onRefresh() {
            loadDataFromDB();
        }
    };


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // actionBar.addTab(actionBar.newTab().setText(" hi  ").setIcon(R.drawable.like_off).setTabListener(this));

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:

                    return new VideosFragment();
                case 1:
                    return new FavoritesFragment();
            }

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                //    case 2:
                //        return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void changeTabsImages() {
        //mViewPager.setCurrentItem(0);
        if (mViewPager.getCurrentItem() == 0) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.addTab(actionBar.newTab().setText("   Videos").setIcon(R.drawable.videos_off));
            actionBar.addTab(actionBar.newTab().setText("   Favorites").setIcon(R.drawable.favorites_on));
        }
    }

    public void changeBoolean() {
        if (b == true) {
            b = false;
        } else if (b == false) {
            b = true;
        }
    }

    @Override
    public void onBackPressed() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
//            if (b) {
//                changeBoolean();
//                killApp = true;
//                startAppAd.onBackPressed();
//                MainActivity.this.finish();
//                finishAffinity();
////                            android.os.Process.killProcess(android.os.Process.myPid());
////                            System.exit(1);
//
//            } else {
//                changeBoolean();
//                killApp = true;
//                MobileCore.showInterstitial(MainActivity.this, new CallbackResponse() {
//                    @Override
//                    public void onConfirmation(TYPE type) {
//                        MainActivity.this.finish();
//                        finishAffinity();
//                    }
//                });
////                            android.os.Process.killProcess(android.os.Process.myPid());
////                            System.exit(1);
//            }
//          //  extras = null;
//          //  mVideoView.handleBackPress(MainActivity.this);
            super.onBackPressed();
        } else {
            //    extras = null;
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_exit)
                    .setTitle("Exit")
                    .setMessage("Are you sure you want exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (b) {
                                changeBoolean();
                                killApp = true;
                                startAppAd.onBackPressed();
                                MainActivity.this.finish();
                                finishAffinity();
//                            android.os.Process.killProcess(android.os.Process.myPid());
//                            System.exit(1);

                            } else {
                                changeBoolean();
                                killApp = true;
                                MobileCore.showInterstitial(MainActivity.this, new CallbackResponse() {
                                    @Override
                                    public void onConfirmation(TYPE type) {
                                        MainActivity.this.finish();
                                        finishAffinity();
                                    }
                                });
//                            android.os.Process.killProcess(android.os.Process.myPid());
//                            System.exit(1);
                            }
                            //MainActivity.this.finish();

                        }

                    })
                    .setNegativeButton("No", null)
                    .show();

        }
    }

    @Override
    protected void onDestroy() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.edit().putBoolean(KEY_ADS_BOOLEAN, b).commit();
//        if (b) {
//            changeBoolean();
//            killApp = true;
//            startAppAd.onBackPressed();
//            MainActivity.this.finish();
//            finishAffinity();
//
//        } else {
//            changeBoolean();
//            killApp = true;
//            MobileCore.showInterstitial(MainActivity.this, new CallbackResponse() {
//                @Override
//                public void onConfirmation(TYPE type) {
//                    MainActivity.this.finish();
//                    finishAffinity();
//                }
//            });
//
//        }
        mVideoView = ((DMWebVideoView) findViewById(R.id.dmWebVideoView2));
        mVideoView.onPause();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.edit().putBoolean(KEY_ADS_BOOLEAN, b).commit();
        mVideoView = ((DMWebVideoView) findViewById(R.id.dmWebVideoView2));
        mVideoView.onPause();
        super.onStop();
    }
}
