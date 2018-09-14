package com.antaresnav.navigation.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.antaresnav.maps.AntaresMap;
import com.antaresnav.maps.AntaresMap.OnMarkerClickListener;
import com.antaresnav.maps.AntaresMap.OnStyleLoadedCallback;
import com.antaresnav.maps.CameraUpdateFactory;
import com.antaresnav.maps.model.BitmapDescriptor;
import com.antaresnav.maps.model.BitmapDescriptorFactory;
import com.antaresnav.maps.model.LatLng;
import com.antaresnav.maps.model.Marker;
import com.antaresnav.maps.model.MarkerOptions;
import com.antaresnav.navigation.AntaresNavigation;
import com.antaresnav.navigation.NavigationView;
import com.antaresnav.navigation.OnNavigationReadyCallback;
import com.antaresnav.navigation.deprecated.routing.mapbox.MapboxRoutingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements OnStyleLoadedCallback {

    private static final String TAG = "MainActivity";
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int STYLE_CHOOSER_REQUEST_CODE = 2;

    private Toolbar toolbar;
    private NavigationView navigationView;
    private TextView mapLoadingTV;
    private TextView destinationNameTV;
    private FloatingActionButton startNavigationFab;
    private FloatingActionButton stopNavigationFab;
    private BitmapDescriptor selectedIcon;
    private BitmapDescriptor defaultIcon;

    private AntaresMap map;
    private AntaresNavigation navigation;
    private Marker selectedMarker;
    private boolean navigationActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BitmapDescriptorFactory descriptorFactory = new BitmapDescriptorFactory(this);
        defaultIcon = descriptorFactory.fromResource(R.drawable.marker_default);
        selectedIcon = descriptorFactory.fromResource(R.drawable.marker_selected);

        navigationView = findViewById(R.id.navigationView);
        destinationNameTV = findViewById(R.id.destinationNameTv);
        startNavigationFab = findViewById(R.id.startNavigationFab);
        stopNavigationFab = findViewById(R.id.stopNavigationFab);
        toolbar = findViewById(R.id.toolbar);
        mapLoadingTV = findViewById(R.id.mapLoadingTV);
        // TODO: 2018. 09. 14. fix me
        mapLoadingTV.setVisibility(View.GONE);
        setSupportActionBar(toolbar);

        navigationView.onCreate(savedInstanceState);
        navigationView.setManeuverViewVisibility(false);
        navigationView.setInformationViewVisibility(false);
        navigationView.setBackgroundColor(0xfff8f4f0);
        navigationView.getNavigationAsync(new OnNavigationReadyCallback() {

            @Override
            public void onNavigationReady(final AntaresMap antaresMap, final AntaresNavigation antaresNavigation) {
                map = antaresMap;
                navigation = antaresNavigation;

                navigation.setRoutingService(new MapboxRoutingService());
                map.setStyleUrl("http://temp.geox.hu:18093/demo/vectiles_styles/openmaptiles_bright_antaresmod.json");
                map.getUiSettings().setCompassEnabled(false);
                map.setOnMarkerClickListener(new OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        setDestinationMarker(marker);
                        return true;
                    }
                });

                map.setOnMapClickListener(new AntaresMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng point) {
                        if (!navigationActive) {
                            clearDestinationMarker();
                        }
                    }
                });

                map.setOnMapLongClickListener(new AntaresMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(@NonNull LatLng position) {
                        if (!navigationActive) {
                            map.clear();
                            Marker placeMarker = map.addMarker(new MarkerOptions().position(position));
                            setDestinationMarker(placeMarker);
                        }
                    }
                });
            }
        });

        startNavigationFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startNavigation(selectedMarker.getPosition());
            }
        });

        stopNavigationFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopNavigation();
            }
        });
    }

    // Add the mapView lifecycle to the activity's lifecycle methods
    @Override
    protected void onStart() {
        super.onStart();
        navigationView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        navigationView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        navigationView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        navigationView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        navigationView.onLowMemory();
    }

    @Override
    public void onPause() {
        super.onPause();
        navigationView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.searchPlace:
                searchPlace();
                return true;
            case R.id.chooseStyle:
                chooseStyle();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onStyleLoaded(String styleUrl) {
        mapLoadingTV.setVisibility(View.GONE);
    }

    @Override
    public void onStyleLoadFailed(String styleUrl, final Throwable throwable) {
        mapLoadingTV.setVisibility(View.GONE);
        Snackbar.make(mapLoadingTV, "Failed to load the map.", Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(getResources().getColor(R.color.primaryLightColor))
                .setAction("details", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String errorHint;
                        if (throwable instanceof FileNotFoundException) {
                            errorHint = "The requested style url was not found! ";
                        } else if (throwable instanceof JsonProcessingException) {
                            errorHint = "An error occurred during processing the map style! ";
                        } else if (throwable instanceof IOException) {
                            errorHint = "Connection failed to the style url! ";
                        } else {
                            errorHint = "Unknown error occurred! ";
                        }
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Style loading error")
                                .setMessage(errorHint + throwable.getMessage())
                                .setPositiveButton("close", null)
                                .show();
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                map.clear();
                com.google.android.gms.location.places.Place place = PlaceAutocomplete.getPlace(this, data);
                LatLng position = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                Marker placeMarker = map.addMarker(new MarkerOptions().position(position));
                placeMarker.setTag(place.getName());
                setDestinationMarker(placeMarker);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e(TAG, "Error: Status = " + status);
            }
        } else if (requestCode == STYLE_CHOOSER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                StyleUrl styleUrl = data.getParcelableExtra(StyleActivity.EXTRA_STYLE_URL);
                map.setStyleUrl(styleUrl.getUrl(), MainActivity.this);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (navigationActive) {
            stopNavigation();
        } else {
            super.onBackPressed();
        }
    }

    private void searchPlace() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this,
                    e.getConnectionStatusCode(), 0).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
            Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void chooseStyle() {
        Intent intent = new Intent(this, StyleActivity.class);
        startActivityForResult(intent, STYLE_CHOOSER_REQUEST_CODE);
    }

    private void setDestinationMarker(Marker marker) {
        if (selectedMarker != null) {
            selectedMarker.setIcon(defaultIcon);
        }
        selectedMarker = marker;
        selectedMarker.setIcon(selectedIcon);
        destinationNameTV.setText(marker.getTag() != null ? (String) marker.getTag() : marker.getPosition().toFormattedString());
        startNavigationFab.setVisibility(View.VISIBLE);
        destinationNameTV.setVisibility(View.VISIBLE);
    }

    private void clearDestinationMarker() {
        if (selectedMarker != null) {
            selectedMarker.setIcon(defaultIcon);
        }
        selectedMarker = null;
        startNavigationFab.setVisibility(View.GONE);
        destinationNameTV.setVisibility(View.GONE);
    }

    private void startNavigation(LatLng destination) {
        clearDestinationMarker();
        navigationView.setManeuverViewVisibility(true);
        navigationView.setInformationViewVisibility(true);
        stopNavigationFab.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.GONE);
        navigation.startNavigation(destination);
        navigationActive = true;
    }

    private void stopNavigation() {
        navigationView.setManeuverViewVisibility(false);
        navigationView.setInformationViewVisibility(false);
        stopNavigationFab.setVisibility(View.GONE);
        toolbar.setVisibility(View.VISIBLE);
        navigation.stopNavigation();
        navigationActive = false;
    }
}
