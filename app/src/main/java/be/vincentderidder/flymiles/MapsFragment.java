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
import com.google.android.gms.maps.model.PolylineOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment{

    private GoogleMap map;
    private  static View view;

    public MapsFragment(){}


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
        map.addPolyline(new PolylineOptions().geodesic(true).width(3)
                        .add(new LatLng(50.85, 4.3517103)) //Brussel
                        .add(new LatLng(37.7749295, -122.41941)) //san francisco
        );
    }

}
