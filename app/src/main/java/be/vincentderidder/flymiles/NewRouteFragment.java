package be.vincentderidder.flymiles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.OnItemMovedListener;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.TouchViewDraggableManager;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.util.Swappable;

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
    DynamicListView lstSelected;
    GooglePlaces client;
    AutoCompleteTextView autoCompView;
    RouteAdapter routeAdapter;
    TouchViewDraggableManager dragManager;
    SwingBottomInAnimationAdapter animationAdapter;
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
        lstSelected = (DynamicListView) v.findViewById(R.id.lstSelected);
        routeAdapter = new RouteAdapter(getActivity(),R.layout.route_item,routeLoc);
        animationAdapter = new SwingBottomInAnimationAdapter(routeAdapter);
        animationAdapter.setAbsListView(lstSelected);
        lstSelected.setAdapter(animationAdapter);
        lstSelected.enableDragAndDrop();
        lstSelected.setDraggableManager(new TouchViewDraggableManager(R.id.txtPlace));
        lstSelected.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        lstSelected.startDragging(position);
                        return true;
                    }


                }
        );
        lstSelected.setOnItemMovedListener(new OnItemMovedListener() {
            @Override
            public void onItemMoved(int i, int i1) {
                routeAdapter.reCalDistance();
            }
        });
        lstSelected.enableSwipeToDismiss(new OnDismissCallback() {
            @Override
            public void onDismiss(ViewGroup viewGroup, int[] ints) {
                for (int position : ints) {
                    Place p = Place.findById(Place.class, routeAdapter.items.get(position).getId());
                    p.delete();
                    routeAdapter.items.remove(position);
                    routeAdapter.notifyDataSetChanged();
                }
            }
        });



        return v;
    }

    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        selected = (Place) adapterView.getItemAtPosition(position);
         Thread t = new Thread(){
            public void run(){
                se.walkercrou.places.Place p = client.getPlaceById(selected.id);
                selected.lat = p.getLatitude();
                selected.lng = p.getLongitude();
                if(routeLoc.size() != 0){
                    Place last = routeLoc.get(routeLoc.size()-1);
                    LatLng prev = new LatLng(last.lat, last.lng);
                    Double d = SphericalUtil.computeDistanceBetween(prev, new LatLng(selected.lat, selected.lng))/1000;
                    selected.dist = d.intValue();
                    selected.pos = routeLoc.size();
                }
                else{selected.dist = 0;selected.pos=0;}


            }
        };
        t.start();
        while(t.isAlive()){}
        routeAdapter.add(selected);
        selected.save();
        routeAdapter.notifyDataSetChanged();
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
    class RouteAdapter extends ArrayAdapter<Place> implements Swappable{
        private ArrayList<Place> items;
        public RouteAdapter(Context context, int textViewResourceId, ArrayList<Place> items){
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if(v==null){
                LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.route_item, null);
            }
            Place p = items.get(position);
            if(p != null){
                TextView name = (TextView)v.findViewById(R.id.txtPlace);
                TextView dist = (TextView) v.findViewById(R.id.txtDistance);
                if(name != null){
                    name.setText(p.address);
                }
                if(dist != null){
                    dist.setText(String.valueOf(p.dist)+" km");
                }
            }
            return v;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).hashCode();
        }

        @Override
        public void swapItems(int i, int i1) {
            Collections.swap(routeLoc, i, i1);
       }
        private void reCalDistance(){
            Place f =  items.get(0);
            f = Place.findById(Place.class, f.getId());
            f.pos = 0;
            f.dist = 0;
            f.save();
            items.set(0, f);
            for(int i=1; i<routeLoc.size(); i++){
                Place c = routeLoc.get(i);
                c = Place.findById(Place.class, c.getId());
                c.pos = i;
                Place p = routeLoc.get(i-1);
                Double d = SphericalUtil.computeDistanceBetween(new LatLng(p.lat, p.lng), new LatLng(c.lat, c.lng))/1000;
                c.dist = d.intValue();
                c.save();
                items.set(i, c);
            }
            notifyDataSetChanged();

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
