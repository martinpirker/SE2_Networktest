import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class Client(private val host: String, private val port: Int) {
    private var socket: Socket? = null
    private var inputStream: BufferedReader? = null
    private var outputStream: PrintWriter? = null
    private var responseListener: ((String) -> Unit)? = null
    private var connected: Boolean = true

    fun setResponseListener(listener: (String) -> Unit) {
        responseListener = listener
    }

    fun connect() {
        GlobalScope.launch(Dispatchers.IO) {
            socket = Socket(host, port)
            inputStream = BufferedReader(InputStreamReader(socket!!.getInputStream()))
            outputStream = PrintWriter(socket!!.getOutputStream(), true)
            listenForResponses()
        }
    }

    suspend fun send(message: String) {
        outputStream?.println(message)
    }

    fun disconnect() {
        inputStream?.close()
        outputStream?.close()
        socket?.close()
    }

    private suspend fun listenForResponses() {
        while (true) {
            val response = inputStream?.readLine() ?: break
            responseListener?.invoke(response)
        }
    }

    fun isConnected():Boolean{
        return this.connected
    }
}
