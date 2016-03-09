package parisdescartes.appmob;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import parisdescartes.appmob.Item.Event;
import parisdescartes.appmob.Item.Participation;
import parisdescartes.appmob.Item.User;
import parisdescartes.appmob.Retrofit.KissorService;
import parisdescartes.appmob.Retrofit.ResponseEvents;
import parisdescartes.appmob.database.DatabaseHelper;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private static String TAG = MapsActivity.class.getSimpleName();

    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    SharedPreferences sharedPreferences;
    private DatabaseHelper db;
    private User user;
    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    KissorService kissorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kissorService = new RestAdapter.Builder().
                setEndpoint(KissorService.ENDPOINT).
                build().
                create(KissorService.class);
        sharedPreferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
        db = ((Application)getApplication()).getDb();
        user = db.getUser(sharedPreferences.getLong("idUser", 0));
        setContentView(R.layout.activity_maps);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        setUpMapIfNeeded();
        setUpMarker();

        mNavItems.add(new NavItem("Map", "Trouve les soirées près de toi", R.drawable.ic_local_activity_black_48dp));
        mNavItems.add(new NavItem("Créer une soirée", "Créer ta soirée", R.drawable.ic_add_location_black_48dp));
        mNavItems.add(new NavItem("Paramètres", "Modifie tes paramètres", R.drawable.ic_settings_black_48dp));
        mNavItems.add(new NavItem("About", "A propos de nous", R.drawable.ic_info_black_48dp));

        TextView userName = (TextView) findViewById(R.id.userName);
        ImageView avatar = (ImageView) findViewById(R.id.avatar);
        Picasso.with(this).load(user.getPhoto_url()).into(avatar);

        userName.setText(user.getFirst_name());

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d(TAG, "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle
        // If it returns true, then it has handled
        // the nav drawer indicator touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void selectItemFromDrawer(int position) {
        System.out.println(mNavItems.get(position).mTitle);
        switch (mNavItems.get(position).mTitle){
            case "Créer une soirée":
                createEventPopup();
                break;
            case "Paramères":
                //TODO
                break;
            case "About":
                //TODO
                break;
        }
        mDrawerList.setItemChecked(position, true);

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        setUpMarker();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setMyLocationEnabled(true);

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                @Override
                public boolean onMarkerClick(Marker arg0) {
                    Intent intent = new Intent(getContext(), EventActivity.class);
                    System.out.println(arg0.getTitle());
                    intent.putExtra("idEvent", arg0.getTitle());
                    startActivity(intent);
                    return true;
                }
            });
        }
    }

    public static void ZoomLocMap(GoogleMap mMap, double latitude, double longitude, float nivelZoom) {

        try
        {
            LatLng latLng = new LatLng(latitude, longitude);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, nivelZoom);
            mMap.animateCamera(cameraUpdate);

        }catch (NullPointerException e)
        {
            Log.i("LogsAndroid", "NullPointerException");
        }
    }

    public Context getContext(){
        return this;
    }

    public void setUpMarker(){
        final ProgressDialog progress = ProgressDialog.show(getContext(), "Connexion en cours",
                "Veuillez patienter....", true);
        progress.setCancelable(true);
        progress.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    progress.dismiss();
                    return true;
                }
                return true;
            }
        });
        kissorService.getEvent(AccessToken.getCurrentAccessToken().getToken(), new Callback<ResponseEvents>() {
            @Override
            public void success(ResponseEvents responseEvents, Response response) {
                List<Event> events = responseEvents.getEvents();
                for (Event e : events) {
                    if(e.getCreated_by() == user.getUserid()){
                        System.out.println("Present !!!");
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(e.getLatitude(), e.getLongitude()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                .title(String.valueOf(e.get_id())));
                    }
                    else if (db.participe(e.get_id())) {

                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(e.getLatitude(), e.getLongitude()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                .title(String.valueOf(e.get_id())));
                    } else {
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(e.getLatitude(), e.getLongitude()))
                                .title(String.valueOf(e.get_id())));
                    }

                    db.addEvent(e);
                }
                progress.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
                errorDialog(error.toString());
            }
        });

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            ZoomLocMap(mMap, mLastLocation.getLatitude(), mLastLocation.getLongitude(), 15);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mMap.clear();
        MarkerOptions mp = new MarkerOptions();
        mp.position(new LatLng(location.getLatitude(), location.getLongitude()));
        mp.title("my position");
        mp.flat(true);
        mMap.addMarker(mp);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void errorDialog(String message){
        AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
        alertDialog.setTitle("Erreur");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    class NavItem {
        String mTitle;
        String mSubtitle;
        int mIcon;

        public NavItem(String title, String subtitle, int icon) {
            mTitle = title;
            mSubtitle = subtitle;
            mIcon = icon;
        }
    }

    class DrawerListAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<NavItem> mNavItems;

        public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
            mContext = context;
            mNavItems = navItems;
        }

        @Override
        public int getCount() {
            return mNavItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mNavItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.drawer_item, null);
            }
            else {
                view = convertView;
            }

            TextView titleView = (TextView) view.findViewById(R.id.title);
            TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
            ImageView iconView = (ImageView) view.findViewById(R.id.icon);

            titleView.setText( mNavItems.get(position).mTitle );
            subtitleView.setText( mNavItems.get(position).mSubtitle );
            iconView.setImageResource(mNavItems.get(position).mIcon);

            return view;
        }
    }

    public void createEventPopup(){
        final AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
        alertDialog.setTitle("Erreur");
        Geocoder geo = new Geocoder(MapsActivity.this.getApplicationContext(), Locale.getDefault());
        String addresse;
        try {
            List<Address> addresses = geo.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
            if (addresses.isEmpty()) {
                errorDialog("Veuillez activer votre lacalisation");
            }
            else{
                addresse = addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getPostalCode() +
                        ", " + addresses.get(0).getCountryName();
                alertDialog.setMessage("Voulez vous créer un événement maintenant à cette adresse :\n" +addresse);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Oui",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Date date = Calendar.getInstance().getTime();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
                                String dateString = sdf.format(date).toString();
                                Event event = new Event("", user.getUserid(), mLastLocation.getLatitude(), mLastLocation.getLongitude(), dateString);
                                kissorService.createEvent(AccessToken.getCurrentAccessToken().getToken(), event, new Callback<Event>() {
                                    @Override
                                    public void success(Event event, Response response) {
                                        alertDialog.dismiss();
                                        errorDialog("Evénement crée !");
                                        finish();
                                        startActivity(getIntent());
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        errorDialog("Une erreur est survenue :\n"+error.toString());
                                    }
                                });
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Non",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        } catch (IOException e) {
            errorDialog("Veuillez activer votre lacalisation");
        }

    }

    @Override
    public void onBackPressed() {}

}
