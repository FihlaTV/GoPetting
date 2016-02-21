package com.example.sumit.apple;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.sumit.apple.bus.CheckMenuItemEvent;
import com.example.sumit.apple.bus.MoveToFragmentEvent;
import com.example.sumit.apple.bus.UpdateActionBarTitleEvent;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavigationView mNavDrawer;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set a Toolbar to replace the ActionBar.
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Find our drawer view
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavDrawer = (NavigationView) findViewById(R.id.nvView);

        //Adding Navigation Header to NavigationView
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.navigation_header,null,false);
        mNavDrawer.addHeaderView(view);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.flContent, HomeFragment.newInstance()) //TODO: Update this
                    .commit();
        }


        // Setup drawer view ; Set Navigation Item Selection Listener
        mNavDrawer.setNavigationItemSelectedListener(new NavigationItemSelectedListener());

//TODO: main logic is till here! Delete once done

        //Setting listener for Drawer open/close events

        final CharSequence mDrawerTitle;
        mTitle = mDrawerTitle = getTitle();
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                 R.string.drawer_open, R.string.drawer_close) {

//           Called when a drawer has settled in a completely closed state.

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
//                EventBus.getDefault().post(new UpdateActionBarTitleEvent(mTitle));
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

//             Called when a drawer has settled in a completely open state.

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                EventBus.getDefault().post(new UpdateActionBarTitleEvent(mDrawerTitle));
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }


    private class NavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            selectDrawerItem(menuItem);
            return true;
        }

    }

    public void selectDrawerItem(MenuItem menuItem) {

        //TODO: Update all fragments to extend from Basefragment: Done
        //TODO: Change cases like Homefragment - HomeFragment.newInstance()
        switch(menuItem.getItemId()) {
            case R.id.home:
                EventBus.getDefault().post(new MoveToFragmentEvent(HomeFragment.newInstance()));
                break;
            case R.id.location:
                EventBus.getDefault().post(new MoveToFragmentEvent(new LocationFragment()));
                break;
            case R.id.orders:
                EventBus.getDefault().post(new MoveToFragmentEvent(new OrdersFragment()));
                break;
            case R.id.ContactUs:
                EventBus.getDefault().post(new MoveToFragmentEvent(new ContactUsFragment()));
                break;
            case R.id.TC:
                EventBus.getDefault().post(new MoveToFragmentEvent(new TermsConditionsFragment()));
                break;
            case R.id.AboutUs:
                EventBus.getDefault().post(new MoveToFragmentEvent(new AboutUsFragment()));
                break;
            default:
                EventBus.getDefault().post(new MoveToFragmentEvent(new HomeFragment()));
        }
/*        try {
            fragment = (Fragment) FragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        mFragmentManager = getSupportFragmentManager();

//        Log.e("Count in Fragment", "" + mFragmentManager.getBackStackEntryCount());
        mFragmentManager.beginTransaction().replace(R.id.flContent,fragment).addToBackStack(null).commit();*/
//
//        if (mFragmentManager.getBackStackEntryCount()>0){
//            mFragmentManager.popBackStack();
//        }
//        else {
//            mFragmentManager.beginTransaction().replace(R.id.flContent,fragment).addToBackStack(null).commit();
//
//        }


        // Highlight the selected item, update the toolbar title, and close the drawer
        menuItem.setChecked(true);
//        setTitle(menuItem.getTitle());
//        Toast.makeText(this, menuItem.getItemId(), Toast.LENGTH_SHORT).show();  //temp toast
        mDrawerLayout.closeDrawer(mNavDrawer);


    }

    public void onEvent(MoveToFragmentEvent e) {
        if (e.getFragment() instanceof HomeFragment) {

            getSupportFragmentManager().beginTransaction().replace(R.id.flContent,e.getFragment()).addToBackStack(null).commit();

        } else if (e.getFragment() instanceof LocationFragment) {

            getSupportFragmentManager().beginTransaction().replace(R.id.flContent,e.getFragment()).addToBackStack(null).commit();

        } else if (e.getFragment() instanceof OrdersFragment) {

            getSupportFragmentManager().beginTransaction().replace(R.id.flContent,e.getFragment()).addToBackStack(null).commit();

        }  else if (e.getFragment() instanceof ContactUsFragment) {

            getSupportFragmentManager().beginTransaction().replace(R.id.flContent,e.getFragment()).addToBackStack(null).commit();

        }  else if (e.getFragment() instanceof TermsConditionsFragment) {

            getSupportFragmentManager().beginTransaction().replace(R.id.flContent,e.getFragment()).addToBackStack(null).commit();

        }  else if (e.getFragment() instanceof AboutUsFragment) {

            getSupportFragmentManager().beginTransaction().replace(R.id.flContent,e.getFragment()).addToBackStack(null).commit();

        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        //TODO : Add/Handle Action bar options item
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    // Make sure this is the method with just `Bundle` as the signature
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed(){
//        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
//            mDrawerLayout.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

/*
    public static class PlaceholderFragment extends BaseFragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
			*//* Update fragment's title.*//*
            EventBus.getDefault().post(new UpdateActionBarTitleEvent(getActivity().getTitle()));
            View rootView = inflater.inflate(R.layout.fragment_home, container, false); //TODO: Change/Rename fragment_home layout name to something meaningful like fragment_placeholder
            TextView text = (TextView) rootView.findViewById(R.id.fragment_text);
            text.setText(R.string.home_text);

*//*
			*//*
*//* Move to fragment that can demonstrate sticky-event. *//**//*

            rootView.findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new MoveToFragmentEvent(new SecondFragment()));
                }
            });
			*//*
*//* Move to fragment that can not accept sticky-event. *//**//*

            rootView.findViewById(R.id.btn_no_sticky).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new MoveToFragmentEvent(new NoStickyFragment()));
                }
            });
*//*

            return rootView;
        }
    }*/

    public void onEvent(UpdateActionBarTitleEvent e) {
        getSupportActionBar().setTitle(e.getTitle());

    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }



}

