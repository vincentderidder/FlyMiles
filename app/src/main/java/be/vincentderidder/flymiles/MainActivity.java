package be.vincentderidder.flymiles;



import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;


public class MainActivity extends ActionBarActivity implements NewRouteFragment.showMapFragmentListener, MapsFragment.showListFragmentListener {
    private ArrayList<Place> currentRoute = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void showListFragment(ArrayList<Place> route){
        NewRouteFragment nFragment =NewRouteFragment.newInstance(route);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, nFragment).addToBackStack(null).commit();
    }
    public void showMapFragment(ArrayList<Place> route){
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }
}
