package be.vincentderidder.flymiles;



import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;


public class MainActivity extends ActionBarActivity {
    public static ArrayList<Place> currentRoute = new ArrayList<>();
    public static String EXTRA_UNIT = "";
    public static String EXTRA_MAP = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SharedPreferences prefs = this.getSharedPreferences("be.vincentderidder.flymiles", Context.MODE_PRIVATE);
        EXTRA_UNIT = prefs.getString("be.vincentderidder.flymiles.unit", "KM");
        EXTRA_MAP = prefs.getString("be.vincentderidder.flymiles.map", "Satellite");
        Iterator<Place> Route = Place.findAll(Place.class);
        while(Route.hasNext()){
            currentRoute.add(Route.next());
        }
        Collections.sort(currentRoute, new Comparator<Place>() {
            @Override
            public int compare(Place p1, Place p2) {

                return Double.compare(p1.pos,p2.pos);
            }
        });
        if (currentRoute.size() == 0) {
            showListFragment(currentRoute);
        }
        else{
            showMapFragment(currentRoute);
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_list: showListFragment(currentRoute); break;
            case R.id.action_settings: showSettingsFragment(); break;
            case R.id.action_map: showMapFragment(currentRoute);break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void showSettingsFragment(){
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        SettingsFragment nFragment = SettingsFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, nFragment).addToBackStack(null).commit();

    }
    public void showListFragment(ArrayList<Place> route){
        NewRouteFragment nFragment =NewRouteFragment.newInstance(route);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, nFragment).addToBackStack(null).commit();
    }
    public void showMapFragment(ArrayList<Place> route){
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        FragmentManager fragmentManager = getSupportFragmentManager();
        MapsFragment mapFragment = MapsFragment.newInstance(route);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, mapFragment).addToBackStack(null).commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}
