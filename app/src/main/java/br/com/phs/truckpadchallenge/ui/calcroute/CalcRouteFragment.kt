package br.com.phs.truckpadchallenge.ui.calcroute


import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import br.com.phs.data.RouteSessionRepository
import br.com.phs.domain.LocationModel
import br.com.phs.domain.RouteSessionDBModel
import br.com.phs.truckpadchallenge.R
import br.com.phs.truckpadchallenge.framework.api.model.google.GeoCordingLocalityModel
import br.com.phs.truckpadchallenge.framework.api.model.truckpad.*
import br.com.phs.truckpadchallenge.framework.api.services.GoogleMapsApiService
import br.com.phs.truckpadchallenge.framework.api.services.TruckPadApiServicce
import br.com.phs.truckpadchallenge.framework.api.utils.formatLocation
import br.com.phs.truckpadchallenge.framework.api.utils.getLocationName
import br.com.phs.truckpadchallenge.framework.db.DatabaseHandler
import br.com.phs.truckpadchallenge.framework.db.RouteSessionPersisteDBSource
import br.com.phs.truckpadchallenge.framework.session.routeSessionAux
import br.com.phs.truckpadchallenge.ui.home.HomeFragment
import br.com.phs.usecases.route.InvokeRouteSessionCurrentRouteSaved
import br.com.phs.usecases.route.InvokeRouteSessionSave
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class CalcRouteFragment : Fragment() {

    private lateinit var calculateRouteClose: LinearLayout
    private lateinit var originEdit: AutoCompleteTextView
    private lateinit var destinyEdit: AutoCompleteTextView
    private lateinit var axisEdit: EditText
    private lateinit var fuelUsage: EditText
    private lateinit var fuelCost: EditText
    private lateinit var axisAdd: TextView
    private lateinit var axisSub: TextView
    private lateinit var calculateRouteButton: Button
    private lateinit var mSimpleArrayListCities: ArrayList<String>
    private lateinit var citiesAdapter: ArrayAdapter<String>
    private lateinit var navController: NavController
    private lateinit var mCurrentLocation: LocationModel
    private var mOriginLocation: LocationModel? = null
    private var mDestinyLocation: LocationModel? = null
    private lateinit var invokeRouteSessionSave: InvokeRouteSessionSave
    private lateinit var invokeRouteSessionCurrentRouteSaved: InvokeRouteSessionCurrentRouteSaved

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calc_route, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.calculateRouteClose = view.findViewById(R.id.calculatorRouteClose)
        this.originEdit = view.findViewById(R.id.calcRouteOriginEdit)
        this.destinyEdit = view.findViewById(R.id.calcRouteDestinyEdit)
        this.axisEdit = view.findViewById(R.id.calcRouteAxisEdit)
        this.axisEdit.inputType = InputType.TYPE_NULL
        this.fuelUsage = view.findViewById(R.id.calcRouteFuelUsageEdit)
        this.fuelCost = view.findViewById(R.id.calcRouteFuelCostEdit)
        this.axisAdd = view.findViewById(R.id.axiesAdd)
        this.axisSub = view.findViewById(R.id.axisSub)
        this.calculateRouteButton = view.findViewById(R.id.calculateRouteBtn)
        this.navController = Navigation.findNavController(view)

        // Persist
        val dbHandler = DatabaseHandler(context!!)
        val routeSessionDBSource = RouteSessionPersisteDBSource(dbHandler)
        val routeSessionRepository = RouteSessionRepository(routeSessionDBSource)
        // Invokes
        this.invokeRouteSessionSave = InvokeRouteSessionSave(routeSessionRepository)
        this.invokeRouteSessionCurrentRouteSaved = InvokeRouteSessionCurrentRouteSaved(routeSessionRepository)

        this.setupAdapters()
        this.setupListener()

        this.insertUserLocation()

    }

    /**
     *
     */
    private fun insertUserLocation() {

        this.generalDialogMake(
            msg = "Deseja Iniciar a rota a partir da sua localizacao?",
            yesFunction = { this.includeCurrentLocationAsOrigin() }, noFunction = {})
    }

    // ***********************************************
    // ******************* Setups ********************
    // ***********************************************

    fun setFragmentAttributes(
        currentLocation: LocationModel, simpleArrayListCities: ArrayList<String>) {

        this.mCurrentLocation = currentLocation
        this.mSimpleArrayListCities = simpleArrayListCities
    }

    /**
     * Setup Origin Edit and Destiny edit adapter for the autocomplete
     */
    private fun setupAdapters() {

        // Setup adapter
        this.citiesAdapter =
            ArrayAdapter(context!!, android.R.layout.simple_gallery_item, mSimpleArrayListCities)
        this.originEdit.setAdapter(citiesAdapter)
        this.destinyEdit.setAdapter(citiesAdapter)
    }

    /**
     * Setup all listeners
     */
    private fun setupListener() {

        // Calculator Route Container
        this.calculateRouteClose.setOnClickListener { this.calculateRouteCloseClickEvent() }
        this.axisAdd.setOnClickListener { this.axisAddSubClickEvent(0) }
        this.axisSub.setOnClickListener { this.axisAddSubClickEvent(1) }
        this.calculateRouteButton.setOnClickListener { this.calculateRouteClickEvent() }
    }

    // ***********************************************
    // **************** Listeners ********************
    // ***********************************************

    /**
     * Handle axis edit value
     */
    private fun axisAddSubClickEvent(op: Int) {

        val axisCurrentValue = this.axisEdit.text.toString().toInt()
        if (op == 0 && axisCurrentValue != 9) {
            this.axisEdit.setText("${axisCurrentValue+1}")
        }
        else if (op == 1 && axisCurrentValue != 2) {
            this.axisEdit.setText("${axisCurrentValue-1}")
        }
        else { genericOkDialog(msg =  "O valores permitidos para o eixo sao:\nMinimo: 2, maximo 9.") }
    }

    /**
     * Cancel and close route calculator
     */
    private fun calculateRouteCloseClickEvent() {

        generalDialogMake(msg = "Deseja cancelar o calculo atual?",
            yesFunction = { this.setDefaultFields(false) }, noFunction = {})
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

        /**
         * Get calculate route and antt price table from TruckPd API
         */
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

            // Model - Calculate Route Result
            val routeTruckPadApiModel = Gson().fromJson(routeResponseJsonStr, CalculateRouteResponseModel::class.java)
            with(routeTruckPadApiModel) {
                val anttPricesRequestModel = AnttPricesRequestModel(axisNumber,
                    this.distance/1000, true)
                val anttRequestJsonStr = Gson().toJson(anttPricesRequestModel)
                // Request Prices From api
                val anttResponseJsonStr = TruckPadApiServicce.getAnttPrices(anttRequestJsonStr)

                // Model - Antt Table Prices
                val anttPricesResponseModel = Gson().fromJson(anttResponseJsonStr, AnttPricesResponseModel::class.java)
                if (anttPricesResponseModel != null) {

                    var anttTableCost: CalculatePrices
                    var calculateRouteAnttCost: CalculateRouteResultModel

                    // Route Calculate
                    val routeResult = CalculateRouteModel(
                            originCityName,
                            destinyCityName,
                            axisNumber,
                            this.distance / 1000,
                            this.duration,
                            this.hasTolls,
                            this.tollCost,
                            this.tollCost,
                            this.fuelUsage,
                            this.fuelCost,
                            this.totalCost
                        )

                    // Antt Table Cost
                    with(anttPricesResponseModel) {

                        anttTableCost =
                            CalculatePrices(
                                this.geral, this.granel,
                                this.neogranel, this.frigorificada, this.perigosa
                            )

                        calculateRouteAnttCost =
                            CalculateRouteResultModel(
                                routeResult,
                                anttTableCost
                            )
                    }

                    persistInDB(calculateRouteAnttCost)

                    generalDialogMake(msg = "Rota calculada com sucesso.\nVer resultados?",
                        yesFunction = { openCalculateRouteResultFragment() },
                        noFunction = { setDefaultFields(true) })
                }
            }

        }
    }

    /**
     * Persist on dataBase and set on session.
     */
    private fun persistInDB(calculateRouteAnttCost: CalculateRouteResultModel) {

        val originJson = Gson().toJson(mOriginLocation)
        val destinyJson = Gson().toJson(mDestinyLocation)
        val calculateRouteJson = Gson().toJson(calculateRouteAnttCost)
        val routeSessionDBModel = RouteSessionDBModel(-1,
            Calendar.getInstance().timeInMillis, originJson!!, destinyJson!!,
            calculateRouteJson, 1, 1)
        invokeRouteSessionSave(routeSessionDBModel)
        // Invoke and set on session
        routeSessionAux(invokeRouteSessionCurrentRouteSaved())
    }

    /**
     * Call Route Info Fragment passing calculateRout
     */
    private fun openCalculateRouteResultFragment() {
        navController.navigate(R.id.action_nav_home_to_nav_route)
    }

    /**
     * Metodo para limpar os campos do dialogo da calculadora de rota
     */
    private fun setDefaultFields(mustChangeFloatingButton: Boolean) {

        this.originEdit.setText("")
        this.destinyEdit.setText("")
        this.axisEdit.setText("2")
        this.fuelUsage.setText("7.5")
        this.fuelCost.setText("4.5")
        (this.parentFragment as HomeFragment).closeRouteCalculator()
        if (mustChangeFloatingButton) {
            (this.parentFragment as HomeFragment).changeFloatingButtonToCancel()
        }
    }

    /**
     * Call Google Maps API (coordinates from location name) and return json
     */
    private fun getCoordinatesFromLocationName(locality: String): String =
        GoogleMapsApiService.getCoordinatesFromLocationName(locality, context!!)

    /**
     * Metodo para inserir cidade de origem confome a localizacao do usuario
     */
    private fun includeCurrentLocationAsOrigin() {

        val result = GoogleMapsApiService.getLocationName(this.mCurrentLocation, context!!)

        // Set name
        this.originEdit.setText(getLocationName(result))
    }

    /**
     * Generic yes no dialog
     */
    private fun generalDialogMake(title: String = "TruckPad", msg: String, yesFunction: () -> Unit,
                                  noFunction: () -> Unit) {

        val alertDialog = AlertDialog.Builder(context!!)
        alertDialog.setTitle(title)
        alertDialog.setMessage(msg)
        alertDialog.setPositiveButton("Sim") { _, _ -> yesFunction() }
        alertDialog.setNegativeButton("Não") { _, _ -> noFunction() }
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
