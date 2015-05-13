package be.vincentderidder.flymiles;


import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment{
    ArrayList<location> route;
    private GoogleMap map;
    private  static View view;

    public MapsFragment(){}

    public static MapsFragment newInstance(ArrayList<location> routeLoc){
        MapsFragment fragment = new MapsFragment();
        fragment.setRoute(routeLoc);
        return fragment;
    }
    private void setRoute(ArrayList<location> route){
            if(route != null){
                this.route = route;
            }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        this.map = ((MapFragment)getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();
        testMap();
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    private void testMap() {
        map.setMyLocationEnabled(true);
        for(int i=0; i<route.size();i++){
            location l1 = route.get(i);
            if(i+1 < route.size()){
                location l2 = route.get(i+1);
                map.addPolyline(new PolylineOptions().geodesic(true).width(3)
                        .add(new LatLng(l1.lat, l1.lon))
                        .add(new LatLng(l2.lat, l2.lon)));
                map.addMarker(new MarkerOptions().position(new LatLng(l1.lat, l1.lon)));

            }
            else return;


        }
        map.addMarker(new MarkerOptions().position(new LatLng(route.get(route.size()).lat, route.get(route.size()).lon)));
    }

}
