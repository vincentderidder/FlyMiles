package be.vincentderidder.flymiles;



import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment{
    ArrayList<Place> route;
    private GoogleMap map;
    private  static View view;
    public showListFragmentListener showListFragmentListener;
    public MapsFragment(){}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_list: showListFragmentListener.showListFragment(route);
        }
        return super.onOptionsItemSelected(item);
    }

    public static MapsFragment newInstance(ArrayList<Place> routeLoc){
        MapsFragment fragment = new MapsFragment();
        fragment.setRoute(routeLoc);
        return fragment;
    }
    private void setRoute(ArrayList<Place> route){
            if(route != null){
                this.route = route;
            }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view != null)
        {
            ViewGroup parent = (ViewGroup) view.getParent();
            if(parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_map, container, false);
        }
        catch(InflateException ex){
            Log.d("An Error occured: ", ex.getMessage());
        }
        this.map = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();
        testMap();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //This makes sure that the container activity has implemented
        //the callback interface. If not, it throws an exception.
        try
        {
            showListFragmentListener = (showListFragmentListener) activity;
        }

        catch(ClassCastException e)
        {
            throw new ClassCastException(activity.toString()+ " must implement showMapListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    private void addMarker(double lat, double lng){
        map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.bullseye)).anchor(0.5f,0.5f).position(new LatLng(lat, lng)));
    }
    private void testMap() {
        if(route.size()>0){
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(route.get(0).lat, route.get(0).lng), 3));
        }

        addMarker(route.get(0).lat, route.get(0).lng);
        for(int i=0; i<route.size();i++){
            Place l1 = route.get(i);
            if(i+1 < route.size()){
                Place l2 = route.get(i+1);
                map.addPolyline(new PolylineOptions().geodesic(true).width(3).color(Color.rgb(37, 77, 117))
                        .add(new LatLng(l1.lat, l1.lng))
                        .add(new LatLng(l2.lat, l2.lng)));
                addMarker(l2.lat, l2.lng);

            }
            else return;


        }

    }

    public interface showListFragmentListener{
        public void showListFragment(ArrayList<Place> route);
    }

}
