package be.vincentderidder.flymiles;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    Spinner spUnit, spMap;
    Button btnDelete;
    ArrayList<Place> route = new ArrayList<>();
    NewRouteFragment.showMapFragmentListener mapFragmentListener;
    MapsFragment.showListFragmentListener listFragmentListener;
    public static SettingsFragment newInstance(){
        return new SettingsFragment();
    }
    public SettingsFragment() {
        // Required empty public constructor
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_map: mapFragmentListener.showMapFragment(route);break;
            case R.id.action_list: listFragmentListener.showListFragment(route);break;
            default:  return super.onOptionsItemSelected(item);
        }
        return true;

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //This makes sure that the container activity has implemented
        //the callback interface. If not, it throws an exception.
        try
        {
            listFragmentListener = (MapsFragment.showListFragmentListener) activity;
            mapFragmentListener = (NewRouteFragment.showMapFragmentListener) activity;
        }

        catch(ClassCastException e)
        {
            throw new ClassCastException(activity.toString()+ " must implement showMapListener");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        spUnit = (Spinner) v.findViewById(R.id.spUnit);
        ArrayAdapter<CharSequence> unitAdapter= ArrayAdapter.createFromResource(getActivity(), R.array.unit_options, android.R.layout.simple_spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnit.setAdapter(unitAdapter);
        spMap = (Spinner) v.findViewById(R.id.spMap);
        ArrayAdapter<CharSequence> mapAdapter= ArrayAdapter.createFromResource(getActivity(), R.array.map_options, android.R.layout.simple_spinner_item);
        mapAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMap.setAdapter(mapAdapter);
        if(!MainActivity.EXTRA_MAP.equals("")){
            int spinnerposition = mapAdapter.getPosition(MainActivity.EXTRA_MAP);
            spMap.setSelection(spinnerposition);
        }
        if(!MainActivity.EXTRA_UNIT.equals("")){
            int spinnerposition = unitAdapter.getPosition(MainActivity.EXTRA_UNIT);
            spUnit.setSelection(spinnerposition);
        }
        btnDelete = (Button) v.findViewById(R.id.btnDelete);
        Iterator<Place> Route = Place.findAll(Place.class);
        while(Route.hasNext()){
            route.add(Route.next());
        }
        final SharedPreferences prefs = getActivity().getSharedPreferences("be.vincentderidder.flymiles", Context.MODE_PRIVATE);
        spUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 MainActivity.EXTRA_UNIT = (String)parent.getSelectedItem();
                 prefs.edit().putString("unit", MainActivity.EXTRA_UNIT);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spMap.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.EXTRA_MAP = (String)parent.getSelectedItem();
                prefs.edit().putString("map", MainActivity.EXTRA_MAP);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Place.deleteAll(Place.class);
                route = new ArrayList<Place>();
            }
        });
        return v;
    }

    public interface showSettingsFragmentListener{
        public void showSettingsFragment();
    }


}
