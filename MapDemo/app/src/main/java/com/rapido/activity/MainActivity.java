package com.rapido.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.rapido.net.ApiClient;
import com.rapido.net.ApiInterface;
import com.rapido.net.NetUtil;
import com.rapido.parser.DirectionsJSONParser;
import com.rapido.R;
import com.rapido.util.RapidoLog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rapido.R.*;


public final class MainActivity extends BaseActivity {

	GoogleMap map;
	ArrayList<LatLng> markerPoints;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layout.activity_main_n);

		// Initializing
		markerPoints = new ArrayList<LatLng>();

		// Getting reference to SupportMapFragment of the activity_main
		SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(id.map);

		// Getting reference to Button
		Button btnDraw = (Button) findViewById(id.btn_draw);

		// Getting Map for the SupportMapFragment
		map = fm.getMap();
		setMarkerOnMap();
		// Enable MyLocation Button in the Map


		// The map will be cleared on long click
		map.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng point) {
				// Removes all the points from Google Map
				map.clear();

				// Removes all the points in the ArrayList
				markerPoints.clear();

			}
		});

		// Click event handler for Button btn_draw
		btnDraw.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// Checks, whether start and end locations are captured
				if (markerPoints.size() >= 2) {
					LatLng origin = markerPoints.get(0);
					LatLng dest = markerPoints.get(1);

					// Getting URL to the Google Directions API
					String url = getDirectionsUrl(origin, dest);
					// Start downloading json data from Google Directions API
					getLocationDataFromServer(url, "", 0, false);

				}

			}
		});
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		map.setMyLocationEnabled(true);

	}

	private boolean setMarkerOnMap() {
		try {
            if (null!= PlaceSearchActivity.getPlaceLatLog() &&null!= map){

                for (LatLng point : PlaceSearchActivity.getPlaceLatLog()){
                    // Already 10 locations with 8 waypoints and 1 start location and 1 end location.
                    // Upto 8 waypoints are allowed in a query for non-business users
                    if(markerPoints.size()>=10){
						return true;
                    }

                    // Adding new item to the ArrayList
                    markerPoints.add(point);

                    // Creating MarkerOptions
                    MarkerOptions options = new MarkerOptions();

                    // Setting the position of the marker
                    options.position(point);

                    /**
                     * For the start location, the color of marker is GREEN and
                     * for the end location, the color of marker is RED and
                     * for the rest of markers, the color is AZURE
                     */
                    if(markerPoints.size()==1){
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    }else if(markerPoints.size()==2){
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }else{
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    }

                    // Add new marker to the Google Map Android API V2
                    map.addMarker(options);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		return false;
	}

	private String getDirectionsUrl(LatLng origin,LatLng dest){

		// Origin of route
		String str_origin = "origin="+origin.latitude+","+origin.longitude;

		// Destination of route
		String str_dest = "destination="+dest.latitude+","+dest.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		// Waypoints
		String waypoints = "";
		for(int i=2;i<markerPoints.size();i++){
			LatLng point  = (LatLng) markerPoints.get(i);
			if(i==2)
				waypoints = "waypoints=";
			waypoints += point.latitude + "," + point.longitude + "|";
		}
		// Building the parameters to the web service
		String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+waypoints;

		// Output format
		String output = "json";
		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;


		return url;
	}

	private void getLocationDataFromServer(final String searchUrl,final String searchParam, int offset, final boolean isPagination) {

		RapidoLog.d("url" + searchUrl);
		ApiInterface apiService = ApiClient.getClient(NetUtil.getBaseUrl( searchUrl)).create(ApiInterface.class);
		Call<JsonObject> filmDataResponseCall = apiService.getLocationData(NetUtil.getSubUrl(searchUrl));
		filmDataResponseCall.clone().enqueue(new Callback<JsonObject>() {
			@Override
			public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
				if (response != null && response.isSuccessful() && response.code() == HttpURLConnection.HTTP_OK) {
					try {
						JSONObject responseJson = new JSONObject(response.body().toString());
						ParserTask parserTask = new ParserTask();
						// Invokes the thread for parsing the JSON data
						parserTask.execute(response.body().toString());
						RapidoLog.d("parsed " + responseJson.toString());
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			}

			@Override
			public void onFailure(Call<JsonObject> call, Throwable t) {

				if (call.isCanceled()) {
					RapidoLog.e("Home card request was cancelled");
				}
			}
		});


	}




	/** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

    	// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

            try{
            	jObject = new JSONObject(jsonData[0]);
            	DirectionsJSONParser parser = new DirectionsJSONParser();

            	// Starts parsing data
            	routes = parser.parse(jObject);
            }catch(Exception e){
            	e.printStackTrace();
            }
            return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {

			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;

			// Traversing through all the routes
			for(int i=0;i<result.size();i++){
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for(int j=0;j<path.size();j++){
					HashMap<String,String> point = path.get(j);

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(2);
				lineOptions.color(Color.RED);
			}

			// Drawing polyline in the Google Map for the i-th route
			map.addPolyline(lineOptions);
		}
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		PlaceSearchActivity.clearPlaceLatLog();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}