package ro.pub.cs.systems.eim.practicaltest02v10.network

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import ro.pub.cs.systems.eim.practicaltest02v10.utils.Constants
import ro.pub.cs.systems.eim.practicaltest02v10.utils.Utils
import java.io.IOException
import java.net.Socket

class ClientThread(
    val context: Context,
    val address: String,
    val port: Int,
    val prefix: String
) : Thread() {
    override fun run() {
        try {
            val socket = Socket(address, port)
            val writer = Utils.getWriter(socket)
            val reader = Utils.getReader(socket)
            writer.println(prefix)
            writer.flush()
            var line = reader.readLine()
            var result = ""
            while (true) {
                if (line == null) {
                    break
                }
                result += line + "\n"
                Log.d(Constants.TAG, "line readed: ${line}")
                line = reader.readLine()
            }
            (context as Activity).runOnUiThread {
                Toast.makeText(context, result, Toast.LENGTH_LONG).show()
            }
            socket.close()
        } catch (ioException: IOException) {
            Log.e(Constants.TAG, "An exception has occurred: ${ioException.message}")
        }
    }
}