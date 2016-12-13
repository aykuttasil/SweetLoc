package com.aykuttasil.sweetloc.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.aykuttasil.androidbasichelperlib.UiHelper;
import com.aykuttasil.sweetloc.R;
import com.aykuttasil.sweetloc.db.DbManager;
import com.aykuttasil.sweetloc.model.ModelLocation;
import com.aykuttasil.sweetloc.model.ModelUser;
import com.aykuttasil.sweetloc.model.ModelUserTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.onesignal.OneSignal;
import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import hugo.weaving.DebugLog;

@EActivity(R.layout.activity_maps)
public class MapsActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {

    @FragmentById(R.id.map)
    SupportMapFragment mMapFragment;

    @ViewById(R.id.Toolbar)
    Toolbar mToolbar;
    //
    GoogleMap mGoogleMap;
    HashMap<Object, Object> mapMarker = new HashMap();

    private LatLngBounds TURKEY = new LatLngBounds(
            new LatLng(36.299172, 26.248221),//Güney Batı
            new LatLng(41.835412, 44.781357) //Kuzey Doğu
    );


    @DebugLog
    @AfterViews
    public void initializeAfterViews() {
        initToolbar();
        permissionControl();
    }

    private void permissionControl() {
        RxPermissions.getInstance(this)
                .request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(result -> {
                    if (result) {
                        mMapFragment.getMapAsync(this);
                    } else {
                        MaterialDialog dialog = UiHelper.UiDialog.newInstance(this).getOKDialog("Uyarı", "Haritanın doğru çalışması için tüm izinleri vermelisiniz.", null);
                        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(view -> {
                            permissionControl();
                        });
                    }
                }, error -> {
                    Logger.e(error, "HATA");
                });
    }


    @DebugLog
    @Override
    void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Harita");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_indigo_300_24dp);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    void updateUi() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Logger.i("Permissinon is not Granted !");

            return;
        }

        this.mGoogleMap = googleMap;
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(TURKEY, 0));
        googleMap.setInfoWindowAdapter(this);
        googleMap.setOnInfoWindowClickListener(this);

        setMap();
    }

    @DebugLog
    private void setMap() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Query queryUser = databaseReference.child(ModelUser.class.getSimpleName());

        queryUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ModelUser modelUser = dataSnapshot1.getValue(ModelUser.class);

                    // Aynı token a sahip diğer kullanıcılar buraya girer
                    if (!modelUser.getUUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) &&
                            modelUser.getToken().equals(DbManager.getModelUser().getToken())) {


                        ModelUserTracker modelUserTracker = new ModelUserTracker();
                        //modelUserTracker modelUser.getAd();

                        Logger.i(modelUser.getEmail() + " konum dinleniyor.");

                        databaseReference.child(ModelLocation.class.getSimpleName())
                                .child(modelUser.getUUID())
                                .limitToLast(2)
                                .addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        Logger.i(s);
                                        Logger.i(new Gson().toJson(dataSnapshot.getValue()));
                                        ModelLocation modelLocation = dataSnapshot.getValue(ModelLocation.class);
                                        addMarker(modelUser, modelLocation);
                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                        ModelLocation modelLocation = dataSnapshot.getValue(ModelLocation.class);
                                        Logger.d(modelLocation);

                                        addMarker(modelUser, modelLocation);
                                    }

                                    @Override
                                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @DebugLog
    private void addMarker(ModelUser modelUser, ModelLocation modelLocation) {
        LatLng latLng = new LatLng(modelLocation.getLatitude(), modelLocation.getLongitude());

        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(modelUser.getEmail())
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_check_light))
                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                //.title(title)
                //.snippet(snippet)
        );

        mapMarker.put(marker, modelLocation);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 10.0f));
        marker.showInfoWindow();
    }

    @DebugLog
    private void setPolyLine() {
        /*
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(-18.142, 178.431), 2));

        // Polylines are useful for marking paths and routes on the map.
        mGoogleMap.addPolyline(new PolylineOptions().geodesic(true)
                .add(new LatLng(-33.866, 151.195))  // Sydney
                .add(new LatLng(-18.142, 178.431))  // Fiji
                .add(new LatLng(21.291, -157.821))  // Hawaii
                .add(new LatLng(37.423, -122.091))  // Mountain View
        );
        */

        /*
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.geodesic(true);
        for (int a = 0; a < modelRotaPointList.size(); a++) //ModelRotaPoint modelRotaPoint : modelRotaPointList) {
        {
            polylineOptions.add(new LatLng(Double.parseDouble(modelRotaPointList.get(a).getEnlem()),
                    Double.parseDouble(modelRotaPointList.get(a).getBoylam())));
        }
        mGoogleMap.addPolyline(polylineOptions);
        */
    }

    /**
     * InfoWindow için custom view oluştururken ilk buraya firer.Eğer null dönerse getInfoContents e girer.
     *
     * @param marker
     * @return
     */
    @DebugLog
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    /**
     * InfoWindow customview oluşturulması için bu fonksiyon düzenlenir..
     *
     * @param marker
     * @return
     */
    @DebugLog
    @Override
    public View getInfoContents(Marker marker) {
        View vi = LayoutInflater.from(this).inflate(R.layout.custom_infowindow_layout, null, false);
        try {
            TextView userMail = (TextView) vi.findViewById(R.id.TextView_UserMail);
            TextView userLocTime = (TextView) vi.findViewById(R.id.TextView_UserLocTime);
            TextView userLocAccuracy = (TextView) vi.findViewById(R.id.TextView_UserLocAccuracy);
            TextView userSpeed = (TextView) vi.findViewById(R.id.TextView_UserSpeed);

            ModelLocation modelLocation = (ModelLocation) mapMarker.get(marker);

            userMail.setText(Html.fromHtml("<b>Email: </b>" + marker.getTitle()));
            userLocTime.setText(Html.fromHtml("<b>Zaman: </b>" + modelLocation.getFormatTime()));
            userLocAccuracy.setText(Html.fromHtml("<b>Sapma: </b>" + String.valueOf(modelLocation.getAccuracy()) + " m"));
            userSpeed.setText(Html.fromHtml("<b>Hız: </b>" + modelLocation.getLocSpeed()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return vi;
    }


    @DebugLog
    @Override
    public void onInfoWindowClick(Marker marker) {
        LatLng markerLatLng = marker.getPosition();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng, 15.0f));
    }

    @DebugLog
    @Click(R.id.FabMap)
    public void FabMapClick() {

        OneSignal.promptLocation();

        String userId = "428ef398-76d3-4ca9-ab4c-60d591879365";
        //OneSignal.postNotification();
        try {
            OneSignal.postNotification(new JSONObject("{'contents': {'en':'Test Message'}, 'include_player_ids': ['" + userId + "']}"), null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
