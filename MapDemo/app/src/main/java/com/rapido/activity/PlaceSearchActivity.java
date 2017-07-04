package com.rapido.activity;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rapido.R;

import java.util.ArrayList;


/**
 * Created by pragati.singh on 7/4/2017.
 */

public final class PlaceSearchActivity extends BaseActivity {
    private static ArrayList<LatLng> latLngArrayList = new ArrayList<>();

    public static final ArrayList<LatLng> getPlaceLatLog() {
        return latLngArrayList;
    }

    public static final void clearPlaceLatLog() {
        latLngArrayList.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_place_activity);
        Button showOnMapButton = (Button) findViewById(R.id.btn_show_onmap);
        showOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != latLngArrayList && !latLngArrayList.isEmpty()) {
                    startActivity(new Intent(PlaceSearchActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(PlaceSearchActivity.this, "please select location", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    /*
     * In this method, Start PlaceAutocomplete activity
     * PlaceAutocomplete activity provides--
     * a search box to search Google places
     */
    public void findPlace(View view) {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .build();
            Intent intent =
                    new PlaceAutocomplete
                            .IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, 1);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    // A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // retrive the data by using getPlace() method.
                Place place = PlaceAutocomplete.getPlace(this, data);
                LatLng latLng = place.getLatLng();
                latLngArrayList.add(latLng);
                Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber());
                if (null != latLngArrayList) {
                    StringBuilder tempStringBuilder = new StringBuilder();
                    for (LatLng latLng1 : latLngArrayList) {
                        tempStringBuilder.append(place.getAddress());
                    }

                    ((TextView) findViewById(R.id.searched_address))
                            .setText(tempStringBuilder.toString());
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.e("Tag", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}