package com.example.networktest

import Client
import Server
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tcpClient = Client("localhost", 53212)

        val textfeldMatrikelnummer: EditText = findViewById(R.id.textfeldMatrikelnummer)
        val textViewServerAntwort: TextView = findViewById(R.id.textviewServerAntwort)
        val button: Button = findViewById(R.id.buttonAbschicken)

        tcpClient.setResponseListener { response ->
            runOnUiThread {
                textViewServerAntwort.text = response
            }
        }

        GlobalScope.launch(Dispatchers.IO) {
            val tcpServer = Server(53212)
            tcpServer.start()
        }

        GlobalScope.launch(Dispatchers.IO) {
            tcpClient.connect()
            while (!tcpClient.isConnected()) {
                delay(100)
            }
        }

        button.setOnClickListener {
            if (textfeldMatrikelnummer.text.toString().isNotEmpty()) {
                GlobalScope.launch(Dispatchers.IO) {
                    tcpClient.send(textfeldMatrikelnummer.text.toString())
                }
            } else {
                Toast.makeText(this@MainActivity, "ENTER NUMBER!!!!!!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}