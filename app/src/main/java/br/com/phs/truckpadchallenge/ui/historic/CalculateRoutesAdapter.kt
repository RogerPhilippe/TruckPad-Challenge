package br.com.phs.truckpadchallenge.ui.historic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.phs.domain.RouteSessionDBModel
import br.com.phs.truckpadchallenge.R
import br.com.phs.truckpadchallenge.framework.api.model.truckpad.CalculateRouteResultModel
import br.com.phs.truckpadchallenge.framework.api.utils.dateTimeHistoricFormatter
import com.google.gson.Gson
import java.util.*
import kotlin.properties.Delegates

class CalculateRoutesAdapter: RecyclerView.Adapter<CalculateRoutesAdapter.ViewHolder>() {

    var items: MutableList<RouteSessionDBModel> by Delegates.observable(mutableListOf()) {
        _, _, _ -> notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.calculate_routes_historic, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun removeAt(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(calculateRoute: RouteSessionDBModel) {

            with(calculateRoute) {

                val tmp = Gson().fromJson(
                    calculateRouteAnttCostJson, CalculateRouteResultModel::class.java)

                val calculateModel = tmp.calculateRouteModel

                val finished = if (calculateRoute.currentRoute == 0.toByte())
                    "Finalizada" else "Atual"

                (itemView.findViewById(R.id.originAndDestinyItem) as TextView).text =
                    "${calculateModel.origin} -> ${calculateModel.destiny}"

                (itemView.findViewById(R.id.dateAndFinishItem) as TextView).text =
                    "Data: ${dateTimeHistoricFormatter(Date(dateTime))}"

                (itemView.findViewById(R.id.statusListLabel) as TextView).text = "Rota: $finished"
            }
        }

    }

}
