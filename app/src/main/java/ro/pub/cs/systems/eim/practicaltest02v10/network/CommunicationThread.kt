package ro.pub.cs.systems.eim.practicaltest02v10.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import ro.pub.cs.systems.eim.practicaltest02v10.utils.Constants
import ro.pub.cs.systems.eim.practicaltest02v10.utils.Utils
import java.io.IOException
import java.net.Socket

class CommunicationThread(
    private val socket: Socket,
) : Thread() {

    override fun run() {
        try {
            Log.v(Constants.TAG, "Connection opened with ${socket.inetAddress}:${socket.localPort}")

            val writer = Utils.getWriter(socket)
            var reader = Utils.getReader(socket)

            val prefix = reader.readLine()

            if (prefix == null) {
                writer.println("400 Bad Request")
                writer.flush()
            } else {
                val client = OkHttpClient()
                val request: Request? = Request.Builder()
                    .url("https://www.google.com/complete/search?client=chrome&q=${prefix}")
                    .build()
                try {
                    client.newCall(request!!).execute().use { response ->
                        if (response.isSuccessful && response.body != null) {
                            val content = response.body!!.string()
                            Log.d(Constants.TAG, "Response after request: $content")
                            val jsonArray = JSONArray(content)
                            val secondElement = jsonArray.getJSONArray(1)
                            writer.println(secondElement.toString().replace("[", "").replace("]", "")
                                .replace("\"", "").replace(",", ",\n"))
                            writer.flush()
                        } else {
                            Log.e(
                                Constants.TAG,
                                "Cererea nu a avut succes. Cod: " + response.code
                            )
                        }
                    }
                } catch (exception: Exception) {
                    Log.e(Constants.TAG, "An exception has occurred: ${exception.message}")
                    if (Constants.DEBUG) {
                        exception.printStackTrace()
                    }
                }
            }
        } catch (ioException: IOException) {
            Log.e(Constants.TAG, "An exception has occurred: ${ioException.message}")
            if (Constants.DEBUG) {
                ioException.printStackTrace()
            }
        } finally {
            socket.close()
            Log.v(Constants.TAG, "Connection closed")
        }
    }
}
