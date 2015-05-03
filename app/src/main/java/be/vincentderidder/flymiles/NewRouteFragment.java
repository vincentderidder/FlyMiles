package be.vincentderidder.flymiles;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
 import android.widget.Toast;

import be.vincentderidder.flymiles.data.AutocompleteLoader;
import be.vincentderidder.flymiles.data.CoordinateLoader;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewRouteFragment extends Fragment implements AdapterView.OnItemClickListener {

    public static final ArrayList<location> routeLoc= new ArrayList<>();
    private location selected;
    Button btnAdd;

    public NewRouteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_route, container, false);
        // Inflate the layout for this fragment
        AutoCompleteTextView autoCompView = (AutoCompleteTextView) v.findViewById(R.id.autoCompleteTextView);
        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.list_item));
        autoCompView.setOnItemClickListener(this);
        btnAdd = (Button) getActivity().findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                CoordinateLoader cl = new CoordinateLoader(getActivity(), selected);
                selected = cl.loadInBackground();
                routeLoc.add(selected);
            }
        });
        return v;
    }
    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        selected = (location) adapterView.getItemAtPosition(position);
        Toast.makeText(getActivity(), selected.address, Toast.LENGTH_SHORT).show();
    }




    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {

        private ArrayList<location> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {

            super(context, textViewResourceId);

        }

        @Override
        public int getCount() {

            return resultList.size();
        }

        @Override
        public location getItem(int index) {

            return resultList.get(index);

        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults filterResults = new FilterResults();

                    if (constraint != null) {

                        // Retrieve the autocomplete results.
                        AutocompleteLoader al = new AutocompleteLoader(getActivity(), constraint.toString());
                        resultList = al.loadInBackground();

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

}
