package br.com.phs.truckpadchallenge

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import br.com.phs.data.RouteSessionRepository
import br.com.phs.truckpadchallenge.framework.api.services.IBGEApiService
import br.com.phs.truckpadchallenge.framework.db.DatabaseHandler
import br.com.phs.truckpadchallenge.framework.db.RouteSessionPersisteDBSource
import br.com.phs.truckpadchallenge.framework.session.IBGESession
import br.com.phs.truckpadchallenge.framework.session.RouteSession
import br.com.phs.truckpadchallenge.framework.session.RouteSessionModel
import br.com.phs.truckpadchallenge.framework.session.routeSessionAux
import br.com.phs.usecases.route.InvokeRouteSessionCurrentRouteSaved
import com.google.android.material.navigation.NavigationView
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_historic, R.id.nav_route
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        this.buildRouteSessionHasAvailable()

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun buildRouteSessionHasAvailable() {

        // Load cities from IBGE API and set on session
        IBGESession.citiesJson = IBGEApiService.getCities()

        // DB handler
        val dbHandler = DatabaseHandler(this)
        val routeSessionDBSource = RouteSessionPersisteDBSource(dbHandler)
        val routeSessionRepository = RouteSessionRepository(routeSessionDBSource)
        val invokeRouteSessionCurrentRouteSaved = InvokeRouteSessionCurrentRouteSaved(routeSessionRepository)

        // Set session if available
        routeSessionAux(invokeRouteSessionCurrentRouteSaved())

    }
}
