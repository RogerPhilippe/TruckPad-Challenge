package br.com.phs.truckpadchallenge.framework.api

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

object HttpHelper {

    private val JSON = MediaType.parse("application/json; charset=utf-8")
    var client = OkHttpClient()

    // POST com JSon
    fun post(url: String, json: String): String {
        val body = RequestBody.create(JSON, json)
        val request = Request.Builder().url(url).post(body).build()
        return getJson(request)
    }

    // GET
    fun get(url: String): String {
        val request = Request.Builder().url(url).get().build()
        return getJson(request)
    }

    private fun getJson(request: Request): String {
        val response = client.newCall(request).execute()
        val responseBody = response.body()
        if (responseBody != null) {
            return responseBody.string()
        }
        throw IOException("Erro ao fazer a requisição")
    }

    fun hasConnectivity() {

        Thread {
            try {
                val sock = Socket()
                sock.connect(InetSocketAddress("8.8.8.8", 53), 2000)
                sock.close()
            } catch (e: IOException) { }
        }.start()
    }

}