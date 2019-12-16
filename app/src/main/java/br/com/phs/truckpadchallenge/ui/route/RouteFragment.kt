package br.com.phs.truckpadchallenge.ui.route

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import br.com.phs.truckpadchallenge.R
import br.com.phs.truckpadchallenge.framework.api.model.truckpad.CalculateRouteResultModel

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calculateRoute = arguments?.getParcelable("calculateRout")
    }

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

        this.setupRow()

    }

    private fun setupRow() {

        with(calculateRoute) {
            // Route Info
            this?.calculateRouteModel.let {
                originRow.text = it?.origin
                destinyRow.text = it?.destiny
                axis.text = it?.axis.toString()
                distanceRow.text = "${it?.distance} Km"
                durationRow.text = "${it?.duration} Hora"
                tollCostRow.text = "R$ ${it?.tollPrice}"
                fuelUsageRow.text = "${it?.necessaryFuel} L"
                fuelTotalRow.text = "R$ ${it?.fuelTotal}"
                totalRow.text = "R$ ${it?.totalCost}"
            }
            // Antt Table Info
            this?.calculatePrices.let {
                geralRow.text = "R$${it?.geral} + pedagio"
                granelRow.text = "R$${it?.granel} + pedagio"
                neoGranelRow.text = "R$${it?.neoGranel} + pedagio"
                frigorificaRow.text = "R$${it?.frigorificada} + pedagio"
                perigosaRow.text = "R$${it?.perigosa} + pedagio"
            }
        }
    }

}