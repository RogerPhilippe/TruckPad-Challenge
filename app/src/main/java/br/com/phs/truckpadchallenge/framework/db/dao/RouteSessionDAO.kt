package br.com.phs.truckpadchallenge.framework.db.dao

import android.content.ContentValues
import br.com.phs.domain.RouteSessionDBEnum
import br.com.phs.domain.RouteSessionDBModel
import br.com.phs.truckpadchallenge.framework.db.DatabaseHandler

class RouteSessionDAO(var dbHandler: DatabaseHandler) {

    fun save(model: RouteSessionDBModel) {

        val db = dbHandler.writableDatabase
        val values = ContentValues()
        values.put(RouteSessionDBEnum.ORIGIN_LOCATION_JSON.value, model.originLocationJson)
        values.put(RouteSessionDBEnum.DESTINY_LOCATION_JSON.value, model.destinyLocationJson)
        values.put(RouteSessionDBEnum.DATE_TIME.value, model.dateTime)
        values.put(RouteSessionDBEnum.CURRENT_ROUTE.value, model.currentRoute)
        values.put(RouteSessionDBEnum.CALCULATE_ROUTE_JSON.value, model.calculateRouteAnttCostJson)
        values.put(RouteSessionDBEnum.STATUS.value, model.status)
        val status = db?.run { insert(RouteSessionDBEnum.TABLE_NAME.value, null, values) }
        db?.close()
    }

    fun findAll(): MutableList<RouteSessionDBModel> {

        val routeSessions: MutableList<RouteSessionDBModel> = mutableListOf()
        val db = dbHandler.readableDatabase
        val selectAllQuery = "SELECT * FROM ${RouteSessionDBEnum.TABLE_NAME.value} " +
                "WHERE ${RouteSessionDBEnum.STATUS.value} = 1"
        val cursor = db?.run { rawQuery(selectAllQuery, null) }
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val routeSession = RouteSessionDBModel(
                    cursor.getInt(cursor.getColumnIndex(RouteSessionDBEnum.ID.value)),
                    cursor.getLong(cursor.getColumnIndex(RouteSessionDBEnum.DATE_TIME.value)),
                    cursor.getString(cursor.getColumnIndex(RouteSessionDBEnum.ORIGIN_LOCATION_JSON.value)),
                    cursor.getString(cursor.getColumnIndex(RouteSessionDBEnum.DESTINY_LOCATION_JSON.value)),
                    cursor.getString(cursor.getColumnIndex(RouteSessionDBEnum.CALCULATE_ROUTE_JSON.value)),
                    cursor.getInt(cursor.getColumnIndex(RouteSessionDBEnum.CURRENT_ROUTE.value)).toByte(),
                    cursor.getInt(cursor.getColumnIndex(RouteSessionDBEnum.STATUS.value)).toByte()
                )
                routeSessions.add(routeSession)
            } while (cursor.moveToNext())

            cursor.close()
        }

        db?.close()

        return routeSessions
    }

    fun findCurrentRoute(): RouteSessionDBModel {

        var routeSession = RouteSessionDBModel()
        val db = dbHandler.readableDatabase
        val selectAllQuery = "SELECT * FROM ${RouteSessionDBEnum.TABLE_NAME.value} " +
                "WHERE ${RouteSessionDBEnum.CURRENT_ROUTE.value} = 1 " +
                "AND ${RouteSessionDBEnum.STATUS.value} = 1"

        val cursor = db?.run { rawQuery(selectAllQuery, null) }
        if (cursor != null && cursor.moveToFirst()) {
            routeSession = RouteSessionDBModel(
                cursor.getInt(cursor.getColumnIndex(RouteSessionDBEnum.ID.value)),
                cursor.getLong(cursor.getColumnIndex(RouteSessionDBEnum.DATE_TIME.value)),
                cursor.getString(cursor.getColumnIndex(RouteSessionDBEnum.ORIGIN_LOCATION_JSON.value)),
                cursor.getString(cursor.getColumnIndex(RouteSessionDBEnum.DESTINY_LOCATION_JSON.value)),
                cursor.getString(cursor.getColumnIndex(RouteSessionDBEnum.CALCULATE_ROUTE_JSON.value)),
                cursor.getInt(cursor.getColumnIndex(RouteSessionDBEnum.CURRENT_ROUTE.value)).toByte(),
                cursor.getInt(cursor.getColumnIndex(RouteSessionDBEnum.STATUS.value)).toByte()
            )
            cursor.close()
        }

        db?.close()

        return routeSession
    }

    fun findById(id: Int): RouteSessionDBModel {

        var routeSession = RouteSessionDBModel()
        val db = dbHandler.readableDatabase
        val selectAllQuery = "SELECT * FROM ${RouteSessionDBEnum.TABLE_NAME.value} " +
                "WHERE ${RouteSessionDBEnum.ID.value} = $id AND ${RouteSessionDBEnum.STATUS.value} = 1"

        val cursor = db?.run { rawQuery(selectAllQuery, null) }
        if (cursor != null && cursor.moveToFirst()) {
            routeSession = RouteSessionDBModel(
                    cursor.getInt(cursor.getColumnIndex(RouteSessionDBEnum.ID.value)),
                    cursor.getLong(cursor.getColumnIndex(RouteSessionDBEnum.DATE_TIME.value)),
                    cursor.getString(cursor.getColumnIndex(RouteSessionDBEnum.ORIGIN_LOCATION_JSON.value)),
                    cursor.getString(cursor.getColumnIndex(RouteSessionDBEnum.DESTINY_LOCATION_JSON.value)),
                    cursor.getString(cursor.getColumnIndex(RouteSessionDBEnum.CALCULATE_ROUTE_JSON.value)),
                    cursor.getInt(cursor.getColumnIndex(RouteSessionDBEnum.CURRENT_ROUTE.value)).toByte(),
                    cursor.getInt(cursor.getColumnIndex(RouteSessionDBEnum.STATUS.value)).toByte()
                )
            cursor.close()
        }

        db?.close()

        return routeSession
    }

    fun delete(id: Int) {

        val db = dbHandler.readableDatabase
        val deleteQuery = "UPDATE ${RouteSessionDBEnum.TABLE_NAME.value} " +
                "SET ${RouteSessionDBEnum.STATUS.value} = 0 " +
                "WHERE ${RouteSessionDBEnum.ID.value} = $id"
        val cursor = db?.run { rawQuery(deleteQuery, null) }
        cursor?.moveToNext()
        cursor?.close()
        db?.close()
    }

    fun finishCurrentRoute(id: Int) {

        val db = dbHandler.readableDatabase
        val finishRouteQuery = "UPDATE ${RouteSessionDBEnum.TABLE_NAME.value} " +
                "SET ${RouteSessionDBEnum.CURRENT_ROUTE.value} = 0 " +
                "WHERE ${RouteSessionDBEnum.ID.value} = $id"
        val cursor = db?.run { rawQuery(finishRouteQuery, null) }
        cursor?.moveToNext()
        cursor?.close()
        db?.close()
    }

}