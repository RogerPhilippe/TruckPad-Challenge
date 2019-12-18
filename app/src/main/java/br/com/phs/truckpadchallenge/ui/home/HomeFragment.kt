package br.com.phs.truckpadchallenge.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import br.com.phs.data.LocationRepository
import br.com.phs.data.RouteSessionRepository
import br.com.phs.domain.CitiesModel
import br.com.phs.domain.LocationModel
import br.com.phs.truckpadchallenge.R
import br.com.phs.truckpadchallenge.framework.api.utils.getCities
import br.com.phs.truckpadchallenge.framework.db.DatabaseHandler
import br.com.phs.truckpadchallenge.framework.db.RouteSessionPersisteDBSource
import br.com.phs.truckpadchallenge.framework.location.CurrentLocationSource
import br.com.phs.truckpadchallenge.framework.session.IBGESession
import br.com.phs.truckpadchallenge.framework.session.RouteSession
import br.com.phs.truckpadchallenge.framework.session.haveRouteSession
import br.com.phs.truckpadchallenge.ui.calcroute.CalcRouteFragment
import br.com.phs.usecases.location.InvokeLocation
import br.com.phs.usecases.route.InvokeRouteSessionCurrentFinish
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.jar.Manifest


class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMainMap: MapView
    private lateinit var routeCalculatorContainer: LinearLayout
    private lateinit var addRouteCalculatorContainer: FloatingActionButton
    private lateinit var mCurrentLocation: LocationModel
    private lateinit var mMap: GoogleMap
    private lateinit var mCurrentLocationSource: CurrentLocationSource
    private lateinit var mLocationRepository: LocationRepository
    private lateinit var mInvokeLocation: InvokeLocation
    private var mCities = mutableListOf<CitiesModel>()
    private lateinit var mSimpleArrayListCities: ArrayList<String>
    private val routeSession = RouteSession
    private lateinit var invokeRouteSessionFinish: InvokeRouteSessionCurrentFinish

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get Components
        this.mMainMap = view.findViewById(R.id.mainMap)
        // Components Setup
        this.routeCalculatorContainer = view.findViewById(R.id.routeCalculatorContainerInclude)
        this.addRouteCalculatorContainer = view.findViewById(R.id.openRouteCalculatorContainer)
        // Calculator Route Content

        this.setupMap(savedInstanceState)
        this.setupLocation()
        this.setupListener()
        this.loadCities()

        // Persist
        val dbHandler = DatabaseHandler(context!!)
        val routeSessionDBSource = RouteSessionPersisteDBSource(dbHandler)
        val routeSessionRepository = RouteSessionRepository(routeSessionDBSource)
        // Invokes
        this.invokeRouteSessionFinish = InvokeRouteSessionCurrentFinish(routeSessionRepository)

        // Check if has current route, in the case true, change floating button
        if (haveRouteSession()) { this.changeFloatingButtonToCancel() }

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
        mCurrentLocationSource = CurrentLocationSource(context!!)
        mLocationRepository = LocationRepository(mCurrentLocationSource)
        mInvokeLocation =
            InvokeLocation(mLocationRepository)
    }

    /**
     * Setup all listeners
     */
    private fun setupListener() {

        this.addRouteCalculatorContainer.setOnClickListener { this.addRouteClickEvent() }
    }

    /**
     * Call cities and states on session
     */
    private fun loadCities() {

        this.mSimpleArrayListCities = arrayListOf()

        val json = IBGESession.citiesJson

        if (json.isNotEmpty()) {
            this.mCities = getCities(json)
            this.mCities.forEach {
                this.mSimpleArrayListCities.add("${it.cityName} - ${it.stateAcronym}")
            }
        }

    }

    // ***********************************************
    // ************** Listener Methods ***************
    // ***********************************************

    /**
     * ADD FloatingButton Click Event
     */
    private fun addRouteClickEvent() {

        if (haveRouteSession()) {
            generalDialogMake(msg = "Deseja finalizar a rota atual?") { this.finishCurrentRoute() }
        } else {
            this.routeCalculatorContainer.visibility = View.VISIBLE
            this.addRouteCalculatorContainer.isVisible = false
            invokeCalculateRouteFragment()
        }

    }

    fun closeRouteCalculator() {
        this.routeCalculatorContainer.visibility = View.GONE
        this.addRouteCalculatorContainer.isVisible = true
    }

    fun changeFloatingButtonToCancel() {
        this.addRouteCalculatorContainer.setImageResource(R.drawable.baseline_clear_white_36)
    }

    /**
     * Invoke Calculator Route Fragment
     */
    private fun invokeCalculateRouteFragment() {

        val transactionFragment = childFragmentManager.beginTransaction()
        val childFragment = CalcRouteFragment()
        childFragment.setFragmentAttributes(mCurrentLocation, mSimpleArrayListCities)
        transactionFragment.replace(R.id.routeCalculatorFragment, childFragment).commit()
    }

    private fun finishCurrentRoute() {

        this.invokeRouteSessionFinish(routeSession.routeSessionModel!!.idCurrentRoute)
        this.addRouteCalculatorContainer.setImageResource(R.drawable.baseline_add_white_36)
        this.routeSession.routeSessionModel!!.hasCurrentRoute = false
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

    /**
     * Generic yes no dialog
     */
    private fun generalDialogMake(title: String = "TruckPad", msg: String, function: () -> Unit ) {

        val alertDialog = AlertDialog.Builder(context!!)
        alertDialog.setTitle(title)
        alertDialog.setMessage(msg)
        alertDialog.setPositiveButton("Sim") { _, _ -> function() }
        alertDialog.setNegativeButton("Não") { _, _ -> }
        alertDialog.show()
    }

}