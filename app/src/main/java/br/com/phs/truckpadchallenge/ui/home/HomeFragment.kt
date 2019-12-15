package br.com.phs.truckpadchallenge.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import br.com.phs.data.LocationRepository
import br.com.phs.domain.CitiesModel
import br.com.phs.domain.LocationModel
import br.com.phs.truckpadchallenge.R
import br.com.phs.truckpadchallenge.framework.api.model.google.GeoCordingLocalityModel
import br.com.phs.truckpadchallenge.framework.api.model.truckpad.*
import br.com.phs.truckpadchallenge.framework.api.services.GoogleMapsApiService
import br.com.phs.truckpadchallenge.framework.api.services.IBGEApiService
import br.com.phs.truckpadchallenge.framework.api.services.TruckPadApiServicce
import br.com.phs.truckpadchallenge.framework.api.utils.formatLocation
import br.com.phs.truckpadchallenge.framework.api.utils.getCities
import br.com.phs.truckpadchallenge.framework.api.utils.getLocationName
import br.com.phs.truckpadchallenge.framework.location.CurrentLocationSource
import br.com.phs.usecases.InvokeLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment(), OnMapReadyCallback {

    // Screen Components
    private lateinit var mMainMap: MapView
    private lateinit var routeCalculatorContainer: LinearLayout
    private lateinit var addRouteCalculatorContainer: FloatingActionButton
    // Calculator Route Container
    private lateinit var calculateRouteClose: LinearLayout
    private lateinit var originEdit: AutoCompleteTextView
    private lateinit var destinyEdit: AutoCompleteTextView
    private lateinit var axisEdit: EditText
    private lateinit var fuelUsage: EditText
    private lateinit var fuelCost: EditText
    private lateinit var axisAdd: TextView
    private lateinit var axisSub: TextView
    private lateinit var calculateRouteButton: Button
    // General purpose variables
    private lateinit var mCurrentLocation: LocationModel
    private var mOriginLocation: LocationModel? = null
    private var mDestinyLocation: LocationModel? = null
    private lateinit var mMap: GoogleMap
    private lateinit var mCurrentLocationSource: CurrentLocationSource
    private lateinit var mLocationRepository: LocationRepository
    private lateinit var mInvokeLocation: InvokeLocation
    private var mCities = mutableListOf<CitiesModel>()
    // Adapters for Origin and Destiny Edits
    private lateinit var simpleArrayListCities: ArrayList<String>
    private lateinit var citiesAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_maps, container, false)

        // Get Components
        this.mMainMap = root.findViewById(R.id.mainMap)
        // Components Setup
        this.routeCalculatorContainer = root.findViewById(R.id.routeCalculatorContainerInclude)
        this.addRouteCalculatorContainer = root.findViewById(R.id.openRouteCalculatorContainer)
        // Calculator Route Content
        this.calculateRouteClose = root.findViewById(R.id.calculatorRouteClose)
        this.originEdit = root.findViewById(R.id.calcRouteOriginEdit)
        this.destinyEdit = root.findViewById(R.id.calcRouteDestinyEdit)
        this.axisEdit = root.findViewById(R.id.calcRouteAxisEdit)
        this.axisEdit.inputType = InputType.TYPE_NULL
        this.fuelUsage = root.findViewById(R.id.calcRouteFuelUsageEdit)
        this.fuelCost = root.findViewById(R.id.calcRouteFuelCostEdit)
        this.axisAdd = root.findViewById(R.id.axiesAdd)
        this.axisSub = root.findViewById(R.id.axisSub)
        this.calculateRouteButton = root.findViewById(R.id.calculateRouteBtn)

        this.setupMap(savedInstanceState)
        this.setupLocation()
        this.setupListener()
        this.loadCities()
        this.setupAdapters()

        return root
    }

    private fun setupMap(savedInstanceState: Bundle?) {

        mMainMap.getMapAsync(this)
        mMainMap.onCreate(savedInstanceState)
        mMainMap.onResume()
    }

    /**
     * Map ready
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        this.initialLocation()
    }

    /**
     * Setup Screen Components
     */
    private fun setupLocation() {
        mCurrentLocationSource =
            CurrentLocationSource(context!!)
        mLocationRepository = LocationRepository(mCurrentLocationSource)
        mInvokeLocation = InvokeLocation(mLocationRepository)
    }

    /**
     * Setup all listeners
     */
    private fun setupListener() {

        this.addRouteCalculatorContainer.setOnClickListener { this.addRouteClickEvent() }
        // Calculator Route Container
        this.calculateRouteClose.setOnClickListener { this.calculateRouteCloseClickEvent() }
        this.axisAdd.setOnClickListener { this.axisAddSub(0) }
        this.axisSub.setOnClickListener { this.axisAddSub(1) }
        this.calculateRouteButton.setOnClickListener { this.calculateRouteClickEvent() }
    }

    /**
     * Call IBGE api to request cities and states
     */
    private fun loadCities() {

        val json = IBGEApiService.getCities()
        this.mCities = getCities(json)
        this.simpleArrayListCities = arrayListOf()
        this.mCities.forEach {
            this.simpleArrayListCities.add("${it.cityName} - ${it.stateAcronym}")
        }
    }

    /**
     * Setup Origin Edit and Destiny edit adapter for the autocomplete
     */
    private fun setupAdapters() {

        // Setup adapter
        this.citiesAdapter =
            ArrayAdapter(context!!, android.R.layout.simple_gallery_item, simpleArrayListCities)
        this.originEdit.setAdapter(citiesAdapter)
        this.destinyEdit.setAdapter(citiesAdapter)
    }

    /**
     * Handle axis edit value
     */
    @SuppressLint("SetTextI18n")
    private fun axisAddSub(op: Int) {

        val axisCurrentValue = this.axisEdit.text.toString().toInt()
        if (op == 0 && axisCurrentValue != 6) {
            this.axisEdit.setText("${axisCurrentValue+1}")
        }
        else if (op == 1 && axisCurrentValue != 2) {
            this.axisEdit.setText("${axisCurrentValue-1}")
        }
        else { genericOkDialog(msg =  "Minimo = 2\nMaximo 6") }
    }

    /**
     * Call Google Maps API (coordinates from location name) and return json
     */
    private fun getCoordinatesFromLocationName(locality: String): String =
        GoogleMapsApiService.getCoordinatesFromLocationName(locality, context!!)

    // ***********************************************
    // ************** Listener Methods ***************
    // ***********************************************

    /**
     * ADD FloatingButton Click Event
     */
    private fun addRouteClickEvent() {

        this.routeCalculatorContainer.visibility = View.VISIBLE
        this.addRouteCalculatorContainer.isVisible = false

        this.generalDialogMake(
            msg = "Deseja Iniciar a rota a partir da sua localizacao?"
        ) { this.includeCurrentLocationAsOrigin() }
    }

    private fun calculateRouteCloseClickEvent() {

        generalDialogMake(msg = "Deseja cancelar o calculo atual?") { this.closeCalculateRouteContainer() }
    }

    /**
     * Check fields, get locations and call TruckPad API
     */
    private fun calculateRouteClickEvent() {

        var status = true

        // Get fields values and check if all filled
        val originCityName = this.originEdit.text.toString().substringBefore(" -")
        val destinyCityName = this.destinyEdit.text.toString().substringBefore(" -")
        val axisNumber = this.axisEdit.text.toString().toInt()
        val fuelUsage = this.fuelUsage.text.toString().toDouble()
        val fuelCost = this.fuelCost.text.toString().toDouble()

        // Get origin location if not fill
        if (mOriginLocation == null) {
            val jsonOriginStr: String = getCoordinatesFromLocationName(originCityName.toLowerCase())
            val originLocation = Gson().fromJson(jsonOriginStr, GeoCordingLocalityModel::class.java)
            originLocation.results[0].geometry.location.let {
                mOriginLocation = LocationModel(lat = it.lat, lng = it.lng, status = 1)
            }
        }

        // Get destiny location
        val jsonDestinyStr: String = getCoordinatesFromLocationName(destinyCityName.toLowerCase())
        val destinyLocation = Gson().fromJson(jsonDestinyStr, GeoCordingLocalityModel::class.java)
        destinyLocation.results[0].geometry.location.let {
            mDestinyLocation = LocationModel(lat = it.lat, lng = it.lng, status = 1)
        }

        // Get Calculate Route from TruckPad api
        if (status) {
            // Origin
            val placesArrayOrigin = ArrayList<Double>()
            placesArrayOrigin.add(formatLocation(mOriginLocation!!.lng))
            placesArrayOrigin.add(formatLocation(mOriginLocation!!.lat))
            val placeOrigin = Places(placesArrayOrigin)
            // Destiny
            val placesArrayDestiny = ArrayList<Double>()
            placesArrayDestiny.add(formatLocation(mDestinyLocation!!.lng))
            placesArrayDestiny.add(formatLocation(mDestinyLocation!!.lat))
            val placeDestiny = Places(placesArrayDestiny)
            // Add places and make calculate request json
            val places = ArrayList<Places>()
            places.add(placeOrigin)
            places.add(placeDestiny)
            val calculateRouteRequest = CalculateRouteRequestModel(places, fuelUsage, fuelCost)
            // Json to request
            val routeRequestJsonStr = Gson().toJson(calculateRouteRequest)
            // Json from response
            val routeResponseJsonStr = TruckPadApiServicce.getCalculateRoute(routeRequestJsonStr)
            // Model
            val routeTruckPadApiModel = Gson().fromJson(routeResponseJsonStr, CalculateRouteResponseModel::class.java)
            // Build prices table values
            with(routeTruckPadApiModel) {
                val anttPricesRequestModel = AnttPricesRequestModel(
                    axisNumber, this.distance/1000, true)
                val anttRequestJsonStr = Gson().toJson(anttPricesRequestModel)
                // Request Prices From api
                val anttResponseJsonStr = TruckPadApiServicce.getAnttPrices(anttRequestJsonStr)
                // Model
                val anttPricesResponseModdel = Gson().fromJson(anttResponseJsonStr, AnttPricesResponseModdel::class.java)
                if (anttPricesResponseModdel != null) {}
            }

        }
    }

    // ***********************************************
    // ********** Calculator Route Content ***********
    // ***********************************************

    /**
     * Close Calculator Route
     */
    private fun closeCalculateRouteContainer() {

        this.routeCalculatorContainer.visibility = View.GONE
        this.addRouteCalculatorContainer.isVisible = true
        this.mOriginLocation = null
        this.mDestinyLocation = null
        this.setDefaultFields()
    }

    /**
     * Metodo para inserir cidade de origem confome a localizacao do usuario
     */
    private fun includeCurrentLocationAsOrigin() {

        // Disable OriginAutocomplete adapter
        this.originEdit.setAdapter(null)

        // Find location name from location
        val result = GoogleMapsApiService.getLocationName(this.mCurrentLocation, context!!)

        // Set name
        this.originEdit.setText(getLocationName(result))

        // Set Origin location with the current captured location
        this.mOriginLocation = mCurrentLocation
    }

    /**
     * Get current location and set on map
     */
    private fun initialLocation() = GlobalScope.launch(Dispatchers.Main) {

        mCurrentLocation = withContext(Dispatchers.IO) { mInvokeLocation() }
        val cameraPosition = CameraPosition.Builder()
        val currentLatLng = LatLng(mCurrentLocation.lat, mCurrentLocation.lng)
        cameraPosition.target(currentLatLng).zoom(8f).build()
        mMap.addMarker(MarkerOptions().position(currentLatLng).title("Meu Local"))
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition.build()))
    }

    //
    /**
     * Metodo para limpar os campos do dialogo da calculadora de rota
     */
    private fun setDefaultFields() {

        this.originEdit.setText("")
        this.destinyEdit.setText("")
        this.axisEdit.setText("2")
        this.fuelUsage.setText("7.5")
        this.fuelCost.setText("4.5")
    }

    /**
     * Generic yes no dialog
     */
    private fun generalDialogMake(title: String = "TruckPAd", msg: String, function: () -> Unit ) {

        val alertDialog = AlertDialog.Builder(context!!)
        alertDialog.setTitle(title)
        alertDialog.setMessage(msg)
        alertDialog.setPositiveButton("Sim") { _, _ -> function() }
        alertDialog.setNegativeButton("Não") { _, _ -> }
        alertDialog.show()
    }

    /**
     * Generic OK dialog
     */
    private fun genericOkDialog(title: String = "TruckPad", msg: String) {

        val alertDialog = AlertDialog.Builder(context!!)
        alertDialog.setTitle(title)
        alertDialog.setMessage(msg)
        alertDialog.setNegativeButton("OK") { _, _ -> }
        alertDialog.show()
    }

}