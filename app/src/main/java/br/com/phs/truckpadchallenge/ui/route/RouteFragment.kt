package br.com.phs.truckpadchallenge.ui.route

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import br.com.phs.truckpadchallenge.R
import br.com.phs.truckpadchallenge.framework.api.model.truckpad.CalculateRouteResultModel
import br.com.phs.truckpadchallenge.framework.session.RouteSession
import br.com.phs.truckpadchallenge.framework.session.haveRouteSession
import br.com.phs.truckpadchallenge.util.convertMinuteDoubleToHourString

class RouteFragment : Fragment() {

    private var calculateRoute: CalculateRouteResultModel? = null

    // Screen Components
    private lateinit var originRow: TextView
    private lateinit var destinyRow: TextView
    private lateinit var axis: TextView
    private lateinit var distanceRow: TextView
    private lateinit var durationRow: TextView
    private lateinit var tollCostRow: TextView
    private lateinit var fuelUsageRow: TextView
    private lateinit var fuelTotalRow: TextView
    private lateinit var totalRow: TextView
    private lateinit var geralRow: TextView
    private lateinit var granelRow: TextView
    private lateinit var neoGranelRow: TextView
    private lateinit var frigorificaRow: TextView
    private lateinit var perigosaRow: TextView
    private lateinit var tableRowTollCost: TableRow
    private lateinit var navController: NavController
    private var routeSession = RouteSession
    private lateinit var resultRouteLabel: TextView
    private lateinit var routeResultMainContaint: ScrollView
    private lateinit var startGoogleMapsNavigationBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_route, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.originRow = view.findViewById(R.id.originRow)
        this.destinyRow = view.findViewById(R.id.destinyRow)
        this.axis = view.findViewById(R.id.axisRow)
        this.distanceRow = view.findViewById(R.id.distanceRow)
        this.durationRow = view.findViewById(R.id.durationRow)
        this.tollCostRow = view.findViewById(R.id.tollCostRow)
        this.fuelUsageRow = view.findViewById(R.id.fuelUsageRow)
        this.fuelTotalRow = view.findViewById(R.id.fuelTotalRow)
        this.totalRow = view.findViewById(R.id.totalRow)
        this.geralRow = view.findViewById(R.id.geralRow)
        this.granelRow = view.findViewById(R.id.granelRow)
        this.neoGranelRow = view.findViewById(R.id.neoGranelRow)
        this.frigorificaRow = view.findViewById(R.id.frigorificaRow)
        this.perigosaRow = view.findViewById(R.id.perigosaRow)
        this.resultRouteLabel = view.findViewById(R.id.resultRouteLabel)
        this.routeResultMainContaint = view.findViewById(R.id.routeResultMainContaint)
        this.startGoogleMapsNavigationBtn = view.findViewById(R.id.startGoogleMapsNavigationBtn)
        this.tableRowTollCost = view.findViewById(R.id.tableRowTollCost)
        this.navController = Navigation.findNavController(view)

        this.startGoogleMapsNavigationBtn.setOnClickListener { this.startGMapsNavigation() }

        if (haveRouteSession()) {
            this.resultRouteLabel.visibility = View.GONE
            this.routeResultMainContaint.visibility = View.VISIBLE
            this.calculateRoute = this.routeSession.routeSessionModel?.calculateRouteAnttCost
            // Button
            this.startGoogleMapsNavigationBtn.isVisible = true
            this.fillRows()
        } else {
            this.resultRouteLabel.visibility = View.VISIBLE
            this.routeResultMainContaint.visibility = View.GONE
            this.startGoogleMapsNavigationBtn.isVisible = false
            genericOkDialog(msg = "Você não tem rota no momento.") {
                this.navController.navigate(R.id.action_nav_route_to_nav_home)
            }
        }

    }

    private fun fillRows() {

        with(calculateRoute) {

            // Route Info
            this?.calculateRouteModel.let {

                // Enable tollCost Row
                val visibility = if (it!!.hasToll)
                { View.VISIBLE } else { View.GONE }
                tableRowTollCost.visibility = visibility

                originRow.text = it.origin
                destinyRow.text = it.destiny
                axis.text = it.axis.toString()
                distanceRow.text = "${it.distance} Km"
                durationRow.text = "${convertMinuteDoubleToHourString(it.duration)}"
                tollCostRow.text = "R$ ${it.tollPrice}"
                fuelUsageRow.text = "${it.necessaryFuel} L"
                fuelTotalRow.text = "R$ ${it.fuelTotal}"
                totalRow.text = "R$ ${it.totalCost}"
            }
            // Antt Table Info
            this?.calculatePrices.let {
                geralRow.text = "R$${it?.geral} + pedágio"
                granelRow.text = "R$${it?.granel} + pedágio"
                neoGranelRow.text = "R$${it?.neoGranel} + pedágio"
                frigorificaRow.text = "R$${it?.frigorificada} + pedágio"
                perigosaRow.text = "R$${it?.perigosa} + pedágio"
            }
        }
    }

    /**
     * Start navigation in Google Maps if available
     */
    private fun startGMapsNavigation() {

        val latLng = "${calculateRoute?.latLngRoute?.lat},${calculateRoute?.latLngRoute?.lng}"

        if (latLng.isBlank() || latLng.isEmpty()) {
            genericOkDialog(msg = "Erro ao capturar coordenadas do destino!", function = {})
            return
        }

        val gmmIntentUri = Uri.parse("google.navigation:q=$latLng")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(context!!.packageManager) != null) {
            startActivity(mapIntent)
        } else {
            genericOkDialog(msg = "Você precisa instalar o Google Maps para essa funcionalidade.",
                function = {})
        }
    }

    /**
     * Generic OK dialog
     */
    private fun genericOkDialog(title: String = "TruckPad", msg: String, function: () -> Unit ) {

        val alertDialog = AlertDialog.Builder(context!!)
        alertDialog.setTitle(title)
        alertDialog.setMessage(msg)
        alertDialog.setNegativeButton("OK") { _, _ -> function() }
        alertDialog.show()
    }

}