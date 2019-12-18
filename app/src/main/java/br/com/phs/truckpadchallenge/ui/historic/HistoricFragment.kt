package br.com.phs.truckpadchallenge.ui.historic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.phs.data.RouteSessionRepository
import br.com.phs.truckpadchallenge.R
import br.com.phs.truckpadchallenge.framework.db.DatabaseHandler
import br.com.phs.truckpadchallenge.framework.db.RouteSessionPersisteDBSource
import br.com.phs.usecases.route.InvokeRouteSessionsSaved

class HistoricFragment : Fragment() {

    private lateinit var historicRecyclerView: RecyclerView
    private var mAdapter = CalculateRoutesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_historic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.historicRecyclerView = view.findViewById(R.id.historicRecyclerView)
        this.historicRecyclerView.adapter = mAdapter
        this.historicRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, true)

        // DB handler
        val dbHandler = DatabaseHandler(context!!)
        val routeSessionDBSource = RouteSessionPersisteDBSource(dbHandler)
        val routeSessionRepository = RouteSessionRepository(routeSessionDBSource)
        val invokeRouteSessionsSaved = InvokeRouteSessionsSaved(routeSessionRepository)

        val routeSessionDBModel = invokeRouteSessionsSaved()

        if (routeSessionDBModel.isNotEmpty()) {
            mAdapter.items = routeSessionDBModel
            // Envia o recyclerView para o topo
            val listSize = mAdapter.items.size
            historicRecyclerView.scrollToPosition(listSize-1)
        }

    }
}