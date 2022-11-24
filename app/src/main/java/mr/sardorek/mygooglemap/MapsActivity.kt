package mr.sardorek.mygooglemap

import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import mr.sardorek.mygooglemap.databinding.ActivityMapsBinding
import java.util.jar.Manifest

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,LocationListener,
GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    private  var map: GoogleMap?= null
    private lateinit var LastLocation:Location
    private var locationMarker: Marker? = null
    private var GoogleApiClean:GoogleApiClient? = null
    private var adress : Address? = null
    private lateinit var locationRequest : LocationRequest
    private val binding by lazy { ActivityMapsBinding.inflate(layoutInflater) }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
editSearch()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnFab.setOnClickListener {
adress?.let {
    map?.animateCamera(CameraUpdateFactory.newLatLng(LatLng(it.latitude,it.longitude)))
}
        }
    }
    private fun  editSearch(){
        binding.editSearch.setOnEditorActionListener { _, actionId, _ ->
if (actionId==EditorInfo.IME_ACTION_DONE){
searchLocation()
}
            true
        }

    }



    override fun onMapReady(p0: GoogleMap) {
        map = p0
        map?.setOnMapClickListener { ll->
onMapClick(ll)
        }
        val location = LatLng(40.74762215685205, 72.3595826268698)
val markerOptions=MarkerOptions().position(location).title("Digital City")
map?.addMarker(markerOptions)
map?.moveCamera(CameraUpdateFactory.newLatLng(location))
if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            buildGoogleApiClient()
            map?.isMyLocationEnabled = true
        }
    } else {
        buildGoogleApiClient()
        map?.isMyLocationEnabled = true
    }
}

    private fun buildGoogleApiClient() {
        var googleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        googleApiClient?.connect()
    }



    override fun onLocationChanged(p0: Location) {
LastLocation = p0
    if (locationMarker != null){
locationMarker?.remove()
    }
val latLng = LatLng(p0.latitude,p0.longitude)
        val markerOptions =MarkerOptions()
        markerOptions.apply {
            position(latLng)
            title("Current Location")
icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        }
locationMarker= map?.addMarker(markerOptions)
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,20f))

GoogleApiClean?.let {
LocationServices.getFusedLocationProviderClient(this)

}
    }


    override fun onConnected(p0: Bundle?) {
locationRequest = LocationRequest ()
    locationRequest.interval = 1000
    locationRequest.fastestInterval =1000
    locationRequest.priority= PRIORITY_BALANCED_POWER_ACCURACY

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.getFusedLocationProviderClient(this)
        }

    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }
    private fun searchLocation(){
        searchLocation()
val loation:String = binding.editSearch.text.toString().trim()
        var adressList:List<android.location.Address> =ArrayList()
if (loation.isNotEmpty()){
map?.clear()
    val geocoder= Geocoder(this)
    try {
adressList=geocoder.getFromLocationName(loation,1)
    } catch (e:java.lang.Exception){
e.printStackTrace()
    }
adress = adressList[0]
val latLng= LatLng(adress?.latitude!!,adress?.longitude!!)
    map?.addMarker(MarkerOptions().position(latLng).title(loation))
map?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
}



    }
private fun onMapClick(ll:LatLng){
map?.clear()
map?.addMarker(MarkerOptions().position(ll))
map?.animateCamera(CameraUpdateFactory.newLatLng(ll))

}
    private fun hideKeyboard(){
val imm = getSystemService(Context.INPUT_METHOD_SERVICE)as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editSearch.windowToken,0)
    }
}