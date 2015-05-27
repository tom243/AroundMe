package com.aroundme;

import com.aroundme.adapter.ViewPagerAdapter;
import com.aroundme.common.SlidingTabLayout;
import com.aroundme.controller.Controller;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
 
public class MainActivity extends ActionBarActivity { 
	//implements IAppCallBack<List<UserAroundMe>> {
 
    // Declaring Your View and Variables
 
    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Friends","Conversations"};
    int Numboftabs =2;
    Controller controller;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        controller = Controller.getInstance();
        // Creating The Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);
        
        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);
 
		// Assiging the Sliding Tab Layout View
		tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
 
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        tabs.setViewPager(pager);
        
        //controller.getAllUsersFromServer(this);
        
    }
 
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
 
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
 
        return super.onOptionsItemSelected(item);
    }
/*
	@Override
	public void done(List<UserAroundMe> ret, Exception e) {
        // Setting the ViewPager For the SlidingTabsLayout
		System.out.println("BOOOOOOOOOOOOOOOOOOOOOOOOO MAIN-ACTIVITY");
      
	}
*/
}