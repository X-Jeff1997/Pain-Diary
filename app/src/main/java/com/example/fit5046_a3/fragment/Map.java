package com.example.fit5046_a3.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.fit5046_a3.R;
import com.example.fit5046_a3.databinding.MapFragmentBinding;
import android.graphics.Color;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import java.util.Objects;


public class Map extends Fragment {
    private String symbolIconId = "symbolIconId";

    public Map() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String token = getString(R.string.mapbox_access_token);
        Mapbox.getInstance(getContext(), token);
        // Inflate the View for this com.example.navigation.fragment using the binding
        MapFragmentBinding addBinding = MapFragmentBinding.inflate(inflater, container, false);
        View view = addBinding.getRoot();
        MapView mapView = addBinding.mapView;

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        //initiate camera position
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(41.885, -87.679)) // set the camera's center position
                                .zoom(12)  // set the camera's zoom level
                                .tilt(20)  // set the camera's tilt
                                .build();

                        // Move the camera to that position
                        mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        // initiate the auto complete search
                        PlaceAutocompleteFragment autocompleteFragment;
                        if (savedInstanceState == null) {
                            autocompleteFragment = PlaceAutocompleteFragment.newInstance(getString(R.string.mapbox_access_token), PlaceOptions.builder()
                                    .backgroundColor(Color.parseColor("#EEEEEE"))
                                    .limit(1)
                                    .build(PlaceOptions.MODE_CARDS));

                            final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                            transaction.add(R.id.searchBar, autocompleteFragment, PlaceAutocompleteFragment.TAG);
                            transaction.commit();
                        } else {
                            autocompleteFragment = (PlaceAutocompleteFragment) getChildFragmentManager().findFragmentByTag(PlaceAutocompleteFragment.TAG);
                        }

                        // initialize style and sybolManager
                        style.addImage(symbolIconId,
                                Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.ic_baseline_location_on_24))),
                                true);
                        SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, style);
                        symbolManager.setIconAllowOverlap(true);
                        symbolManager.setTextAllowOverlap(true);



                        // move camera to the place searched
                        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                            @Override
                            public void onPlaceSelected(CarmenFeature carmenFeature) {
                                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                                        new CameraPosition.Builder()
                                                .target(new LatLng(((Point) carmenFeature.geometry()).latitude(),
                                                        ((Point) carmenFeature.geometry()).longitude()))
                                                .zoom(12)
                                                .build()), 4000);

                                // clean the former marker
                                symbolManager.deleteAll();

                                // Create a symbol at the specified location.
                                SymbolOptions symbolOptions = new SymbolOptions()
                                        .withLatLng(new LatLng(((Point) carmenFeature.geometry()).latitude(), ((Point) carmenFeature.geometry()).longitude()))
                                        .withIconImage(symbolIconId)
                                        .withIconSize(1.3f)
                                        .withIconColor("#DE4A00");

                                // Use the manager to draw the symbol.
                                symbolManager.create(symbolOptions);
                            }

                            @Override
                            public void onCancel() {
                            }
                        });
                    }
                });
            }
        });
        return view;
    }
}


