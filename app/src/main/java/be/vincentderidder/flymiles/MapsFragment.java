package be.vincentderidder.flymiles;


import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
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
public class MapsFragment extends Fragment implements OnMapReadyCallback {


    MapFragment fMap;
    private static GoogleMap gMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        fMap = getMapFragment();
        fMap.getMapAsync(this);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private MapFragment getMapFragment() {
        FragmentManager fm;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            fm = getFragmentManager();
        } else {

            fm = getChildFragmentManager();
        }
        return (MapFragment) fm.findFragmentById(R.id.map);
    }
    @Override
    public void onMapReady(GoogleMap map) {
        map.addPolyline(new PolylineOptions().geodesic(true)
                        .add(new LatLng(50.85, 4.3517103)) //Brussel
                        .add(new LatLng(37.7749295, -122.41941)) //san francisco
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        if  (gMap == null){
            gMap = fMap.getMap();
        }
    }
}
