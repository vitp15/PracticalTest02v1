package ro.pub.cs.systems.eim.practicaltest02v10.network

import android.util.Log
import ro.pub.cs.systems.eim.practicaltest02v10.utils.Constants
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

class ServerThread(private val port: Int) : Thread() {

    var isRunning = false
    private var serverSocket: ServerSocket? = null

    fun startServer() {
        isRunning = true
        start()
        Log.v(Constants.TAG, "startServer() method was invoked")
    }

    fun stopServer() {
        isRunning = false
        try {
            serverSocket?.close()
        } catch (ioException: IOException) {
            Log.e(Constants.TAG, "An exception has occurred: ${ioException.message}")
            if (Constants.DEBUG) {
                ioException.printStackTrace()
            }
        }
        Log.v(Constants.TAG, "stopServer() method was invoked")
    }

    override fun run() {
        try {
            serverSocket = ServerSocket(this.port)
            while (isRunning) {
                val socket: Socket? = serverSocket?.accept()
                if (socket != null) {
                    val communicationThread = CommunicationThread(socket)
                    communicationThread.start()
                }
            }
        } catch (ioException: IOException) {
            Log.e(Constants.TAG, "An exception has occurred: ${ioException.message}")
            if (Constants.DEBUG) {
                ioException.printStackTrace()
            }
        }
    }
}

