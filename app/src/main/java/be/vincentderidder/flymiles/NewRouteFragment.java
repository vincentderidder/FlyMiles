package be.vincentderidder.flymiles;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Prediction;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewRouteFragment extends Fragment implements AdapterView.OnItemClickListener{
    public static ArrayList<Place> routeLoc= new ArrayList<>();
    public showMapFragmentListener showMapListener;
    ArrayList<String> viewloc;
    private Place selected;
    Button btnMap;
    ListView lstSelected;
    GooglePlaces client;
    ArrayAdapter<String> listAdapter;
    AutoCompleteTextView autoCompView;
    public NewRouteFragment() {
        // Required empty public constructor
    }
    public static NewRouteFragment newInstance(ArrayList<Place> routeLoc){
        NewRouteFragment fragment = new NewRouteFragment();
        fragment.setRoute(routeLoc);
        return fragment;
    }
    private void setRoute(ArrayList<Place> route){
        if(route != null){
            routeLoc = route;
        }

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new GooglePlaces("AIzaSyBwLWUVxPSo7zi7X0TUZ4B280tAGnbJGho");
        viewloc = new ArrayList<>();
        setHasOptionsMenu(true);



    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //This makes sure that the container activity has implemented
        //the callback interface. If not, it throws an exception.
        try
        {
            showMapListener = (showMapFragmentListener) activity;
        }

        catch(ClassCastException e)
        {
            throw new ClassCastException(activity.toString()+ " must implement showMapListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_route, container, false);
        // Inflate the layout for this fragment
        autoCompView = (AutoCompleteTextView) v.findViewById(R.id.autoCompleteTextView);
        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.list_item));
        autoCompView.setOnItemClickListener(this);
        listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1,viewloc);
        lstSelected = (ListView) v.findViewById(R.id.lstSelected);
        lstSelected.setAdapter(listAdapter);
        return v;
    }

    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        selected = (Place) adapterView.getItemAtPosition(position);
        new Thread(){
            public void run(){
                se.walkercrou.places.Place p = client.getPlaceById(selected.id);
                selected.lat = p.getLatitude();
                selected.lng = p.getLongitude();
                if(routeLoc.size() != 0){
                    Place last = routeLoc.get(routeLoc.size()-1);
                    LatLng prev = new LatLng(last.lat, last.lng);
                    selected.dist = SphericalUtil.computeDistanceBetween(prev, new LatLng(selected.lat, selected.lng));
                }
                else{selected.dist = 0;}
                routeLoc.add(selected);
                selected.save();

            }
        }.start();
        listAdapter.add(selected.address);
        autoCompView.setText("");
        Toast.makeText(getActivity(), selected.address, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_map: {showMapListener.showMapFragment(routeLoc);return true;}
            default:  return super.onOptionsItemSelected(item);
        }

    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {

        private ArrayList<Place> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {

            super(context, textViewResourceId);

        }

        @Override
        public int getCount() {

            return resultList.size();
        }

        @Override
        public Place getItem(int index) {
                if(resultList.size()>0){
                    return resultList.get(index);
                }
            else return null;

        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults filterResults = new FilterResults();

                    if (constraint != null) {
                        resultList = new ArrayList<>();
                        // Retrieve the autocomplete results.
                        List<Prediction> predictions = client.getPlacePredictions(constraint.toString());
                        for(int i=0; i<predictions.size() ; i++){
                            Place l = new Place();
                            l.address = predictions.get(i).getDescription();
                            l.id = predictions.get(i).getPlaceId();
                            resultList.add(l);
                        }
                        // Assign the data to the FilterResults

                        filterResults.values = resultList;

                        filterResults.count = resultList.size();

                    }

                    return filterResults;

                }

                @Override

                protected void publishResults(CharSequence constraint, FilterResults results) {

                    if (results != null && results.count > 0) {

                        notifyDataSetChanged();

                    } else {

                        notifyDataSetInvalidated();

                    }

                }

            };

            return filter;

        }

    }

    public interface showMapFragmentListener{
        public void showMapFragment(ArrayList<Place> route);
    }

}
