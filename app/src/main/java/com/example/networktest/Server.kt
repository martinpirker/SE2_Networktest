import kotlinx.coroutines.*
import java.net.*

class Server(private val port: Int) {
    private var server: ServerSocket? = null
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    fun start() {
        server = ServerSocket(port, 0, InetAddress.getByName("localhost"))
        println("Server started on port $port")

        while (true) {
            val socket = server!!.accept()
            scope.launch {
                handleClient(socket)
            }
        }
    }

    fun stop() {
        job.cancel()
        server?.close()
        println("Server stopped")
    }

    private suspend fun handleClient(socket: Socket) {
        println("Client connected: ${socket.inetAddress.hostAddress}")

        val input = socket.getInputStream().bufferedReader()
        val output = socket.getOutputStream().bufferedWriter()

        while (true) {
            val message = input.readLine() ?: break
            println("Received message from ${socket.inetAddress.hostAddress}: $message")

            val processedMessage = toLetters(message)

            output.write("$processedMessage\n")
            output.flush()
        }

        socket.close()
        println("Client disconnected: ${socket.inetAddress.hostAddress}")
    }

    private fun toLetters(input: String): String{
        println("MARTIIIIIINNNNNNNNN $input")
        val processedMessage = StringBuilder()
        for ((i, c) in input.withIndex()) {
            if (i % 2 == 0) {
                processedMessage.append(c)
            } else {
                val asciiCode = c.toString().toInt() + 96
                processedMessage.append(asciiCode.toChar())
            }
        }
        return processedMessage.toString()
    }
}
