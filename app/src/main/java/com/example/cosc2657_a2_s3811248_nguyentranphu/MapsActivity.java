package com.example.cosc2657_a2_s3811248_nguyentranphu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import android.location.LocationListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.example.cosc2657_a2_s3811248_nguyentranphu.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

// Calculate the direction between two locations and display the route on a Google Map using the Google Directions API
// https://github.com/jd-alexander/Google-Directions-Android
import com.directions.route.RoutingListener;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;

// Cluster - KTX for the Maps SDK for Android Utility Library
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener, ClusterManager.OnClusterClickListener<MyClusterItem>, ClusterManager.OnClusterItemClickListener<MyClusterItem>, LocationListener {
    private FireBaseHandler fireBaseHandler = new FireBaseHandler();

    private SearchView locationSearchView;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    protected FusedLocationProviderClient client;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private Polyline currentDirection;

    private LatLng chosenLocation;
    private Marker locationMarker;
    // location update
    protected LocationRequest mLocationRequest;

    private List<Polyline> polylines;
    private ArrayList<Task<QuerySnapshot>> siteData;
    private ClusterManager<MyClusterItem> clusterManager;
    private HashMap<String,SiteModel> siteMap = new HashMap<String,SiteModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set up the layout
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);

        //Implement Search View
        locationSearchView = findViewById(R.id.locationSearchView);
        locationSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = locationSearchView.getQuery().toString();
                List<Address> addresses = null;

                if (location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addresses = geocoder.getFromLocationName(location, 1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addresses.size() > 0) {
                        Address address = addresses.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    } else {
                        Toast.makeText(MapsActivity.this,"Address Not Found, Please Try Again!", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mapFragment.getMapAsync(this);
    }

    public void changeMainMode(View view) {
        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
        //kill previous activities
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void changeSigninMode(View view) {
        Intent intent = new Intent(MapsActivity.this, SigninActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation(View view){
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Toast.makeText(MapsActivity.this,  "My Current Location", Toast.LENGTH_SHORT).show();
                if (locationMarker != null) {
                    locationMarker.remove();
                }
                if (currentDirection != null) {
                    currentDirection.remove();
                }
                locationMarker = mMap.addMarker(new MarkerOptions().position(chosenLocation).title("My Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarker)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(chosenLocation));
            }
        });
    }

    public void onLocationChanged(Location location){
        chosenLocation = new LatLng(location.getLatitude(), location.getLongitude());
        locationMarker = mMap.addMarker(new MarkerOptions().position(chosenLocation).
                title("My Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarker)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(chosenLocation, 18));
    }

    @SuppressLint({"MissingPermission", "RestrictedApi"})
    private void locationUpdate(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        client.requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult){
                onLocationChanged(locationResult.getLastLocation());
            }
        }, null);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        client = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        requestPermission();
        mMap = googleMap;

        // if no signin, hide home button
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Button button  = (Button) findViewById(R.id.homeButton);
            button.setVisibility(View.GONE);
            Button signinButton  = (Button) findViewById(R.id.signinButton);
            signinButton.setVisibility(View.VISIBLE);
        }



        //Load data as cluster
        clusterManager = new ClusterManager<MyClusterItem>(this, mMap);
        clusterManager.setRenderer(new ClusterItemRenderer());
        loadSiteData();

        // Locate Current Location Marker
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng latLng) {
                if (currentDirection != null) {
                    currentDirection.remove();
                }
            }
        });
        locationUpdate();

    }

    @Override
    public boolean onClusterClick(Cluster<MyClusterItem> cluster) {
        return false;
    }

    @Override
    public boolean onClusterItemClick(MyClusterItem item) {
        View view = LayoutInflater.from(MapsActivity.this).inflate(R.layout.dialog_register,null);
        TextView host = view.findViewById(R.id.host);
        TextView location = view.findViewById(R.id.location);
        TextView date = view.findViewById(R.id.date);
        TextView testedPeople = view.findViewById(R.id.tested_people);
        TextView participants = view.findViewById(R.id.participant);
        TextView title = view.findViewById(R.id.title);
        Button registerBtn = view.findViewById(R.id.register_button);
        Button directionBtn = view.findViewById(R.id.direction_button);

        SiteModel site = siteMap.get(item.getTitle());
        host.setText(site.getHost());
        location.setText(site.getLocation());
        date.setText(site.getDate());
        testedPeople.setText("" + site.getTestedPeople());
        participants.setText("" + site.getParticipants().size());
        title.setText("Site Covid Info");

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    if (!host.getText().equals(email)) {
                        if (!siteMap.get(location.getText()).getParticipants().contains(email)) {
                            fireBaseHandler.updateParticipant(item.getTitle(), email);
                            Toast.makeText(MapsActivity.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                            loadSiteData();
                        } else {
                            Toast.makeText(MapsActivity.this, "You already joined this site", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MapsActivity.this, "You're the owner of this site", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Intent intent = new Intent (MapsActivity.this, SigninActivity.class);
                    startActivity(intent);
                }
                alertDialog.dismiss();
            }
        });

        directionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng end = item.getPosition();
                findRoutes(chosenLocation,end);
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
        return true;
    }

    private void loadSiteData() {
        siteMap.clear();
        siteData = fireBaseHandler.retrieveSiteData();
        siteData.get(0).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String location, host, date, description, testedPeople;
                List<String> participants;
                int thumbnail;
                GeoPoint geoPoint;
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    location = Objects.requireNonNull(documentSnapshot.getString("location"));
                    host = Objects.requireNonNull(documentSnapshot.getString("host"));
                    date = Objects.requireNonNull(documentSnapshot.getString("date"));
                    description = Objects.requireNonNull(documentSnapshot.getString("description"));
                    geoPoint = Objects.requireNonNull(documentSnapshot.getGeoPoint("geoPoint"));
                    testedPeople = Objects.requireNonNull(documentSnapshot.getData().get("testedPeople").toString());
                    participants = (List<String>) documentSnapshot.getData().get("participants");
                    thumbnail = Integer.parseInt(Objects.requireNonNull(documentSnapshot.getData().get("thumbnail").toString()));
                    SiteModel site = new SiteModel(location, host, date, description,geoPoint,Integer.parseInt(testedPeople), thumbnail);
                    site.setParticipants(participants);
                    siteMap.put(location,site);
                }
                setupCluster();
            }
        });
    }
    private void setupCluster() {
        // Point the map's listeners at the listeners implemented by the ClusterManager.
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);
        clusterManager.setOnClusterItemClickListener(this);
        clusterManager.setOnClusterClickListener(this);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {

        // Set some lat/lng coordinates to start with.
        GeoPoint geoPoint;
        String location;

        clusterManager.clearItems();
        // Add ten cluster items in close proximity, for purposes of this example.
        for (Map.Entry mapElement: siteMap.entrySet()) {
            geoPoint = ((SiteModel) mapElement.getValue()).getGeoPoint();
            location = ((SiteModel) mapElement.getValue()).getLocation();
            MyClusterItem offsetItem = new MyClusterItem(geoPoint.getLatitude(), geoPoint.getLongitude(), location);
            clusterManager.addItem(offsetItem);
        }
        clusterManager.cluster();

    }

    private void removeItems() {
        for (MyClusterItem myClusterItem :clusterManager.getAlgorithm().getItems()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            final LocalDate dt = LocalDate.parse(siteMap.get(myClusterItem.getTitle()).getDate(),formatter);
            if (dt.isBefore(LocalDate.now())) {
                clusterManager.removeItem(myClusterItem);
            }
        }
    }

    private class ClusterItemRenderer extends DefaultClusterRenderer<MyClusterItem> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public ClusterItemRenderer() {
            super(getApplicationContext(), mMap, clusterManager);

            View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) 150;
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) 10;
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(@NonNull MyClusterItem clusterItem, MarkerOptions markerOptions) {
            // Draw a single person - show their profile photo and set the info window to show their name
            markerOptions
                    .icon(getItemIcon(clusterItem))
                    .title(clusterItem.getTitle());
        }

        @Override
        protected void onClusterItemUpdated(@NonNull MyClusterItem clusterItem, Marker marker) {
            // Same implementation as onBeforeClusterItemRendered() (to update cached markers)
            marker.setIcon(getItemIcon(clusterItem));
            marker.setTitle(clusterItem.getTitle());
        }


        private BitmapDescriptor getItemIcon(MyClusterItem clusterItem) {
            mImageView.setImageResource(R.drawable.covidzone);
            Bitmap icon = mIconGenerator.makeIcon();
            return BitmapDescriptorFactory.fromBitmap(icon);
        }

        @Override
        protected void onBeforeClusterRendered(@NonNull Cluster<MyClusterItem> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            markerOptions.icon(getClusterIcon(cluster));
        }

        @Override
        protected void onClusterUpdated(@NonNull Cluster<MyClusterItem> cluster, Marker marker) {
            // Same implementation as onBeforeClusterRendered() (to update cached markers)
            marker.setIcon(getClusterIcon(cluster));
        }

        /**
         * Get a descriptor for multiple people (a cluster) to be used for a marker icon. Note: this
         * method runs on the UI thread. Don't spend too much time in here (like in this example).
         *
         * @param cluster cluster to draw a BitmapDescriptor for
         * @return a BitmapDescriptor representing a cluster
         */
        private BitmapDescriptor getClusterIcon(Cluster<MyClusterItem> cluster) {
            List<Drawable> profilePhotos = new ArrayList<>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (MyClusterItem p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable drawable = getResources().getDrawable(R.drawable.covidzone);
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            return BitmapDescriptorFactory.fromBitmap(icon);
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

    private void findRoutes(LatLng start, LatLng end) {
        if(start==null || end==null) {
            Toast.makeText(MapsActivity.this,"Unable to get location",Toast.LENGTH_LONG).show();
        }
        else
        {

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(start, end)
                    .key("AIzaSyA0MZ2lI7xT8OR0PAsQIS1u1wpotjZlgmM")  // My Google Map API Key
                    .build();
            routing.execute();
        }
    }
    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar= Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
//    Findroutes(start,end);
    }

    @Override
    public void onRoutingStart() {

    }


    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if (currentDirection != null) {
            currentDirection.remove();
        }
        if(polylines!=null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();

        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i <route.size(); i++) {

            if(i==shortestRouteIndex)
            {
                polyOptions.color(getResources().getColor(R.color.black));
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                currentDirection = mMap.addPolyline(polyOptions);
                int k=currentDirection.getPoints().size();
                polylines.add(currentDirection);

            }
            else {

            }

        }

    }

    @Override
    public void onRoutingCancelled() {
        // Do nothing there

    }
}