package br.com.phs.truckpadchallenge.framework.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import br.com.phs.domain.RouteSessionDBEnum

class DatabaseHandler(context: Context): SQLiteOpenHelper(
    context, DB_NAME, null, DB_VERSION
) {

    private val createRouteSessionTable =
            "CREATE TABLE ${RouteSessionDBEnum.TABLE_NAME.value} (" +
                    "${RouteSessionDBEnum.ID.value} INTEGER PRIMARY KEY, " +
                    "${RouteSessionDBEnum.DATE_TIME.value} BIGINT, " +
                    "${RouteSessionDBEnum.ORIGIN_LOCATION_JSON.value} TEXT, " +
                    "${RouteSessionDBEnum.DESTINY_LOCATION_JSON.value} TEXT, " +
                    "${RouteSessionDBEnum.CALCULATE_ROUTE_JSON.value} TEXT, " +
                    "${RouteSessionDBEnum.CURRENT_ROUTE.value} INTEGER, " +
                    "${RouteSessionDBEnum.STATUS.value} INTEGER" +
                    ")"

    private var sqlCreateTablesArray = mutableListOf<String>()

    override fun onCreate(db: SQLiteDatabase?) {

        sqlCreateTablesArray.add(createRouteSessionTable)

        sqlCreateTablesArray.forEach { db?.execSQL(it) }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private const val DB_NAME = "phs_thuckpaddb.db"
        private const val DB_VERSION = 1
    }

}
