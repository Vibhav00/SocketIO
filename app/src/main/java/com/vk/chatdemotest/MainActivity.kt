package com.vk.chatdemotest

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vk.chatdemotest.databinding.ActivityMainBinding
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding

    // socket instance
    private lateinit var socket: Socket
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setIO()
    }

    override fun onStart() {
        super.onStart()
        val userId = (100..999).random() // creating random userId for user
        socket.emit("register", userId)  // sending it to the server

    }

    private fun setIO() {

        // connecting socket to the server
        socket = IO.socket("http://192.168.144.99:3000")
        socket.connect()

        // on message receive
        socket.on("receiveMessage") { args ->
            runOnUiThread {
                val data = args[0] as JSONObject
                val sender = data.getString("userId")
                val msg = data.getString("message")
                activityMainBinding.message.setText("\n$sender: $msg")
            }
        }
        // on btn click
        activityMainBinding.btnMain.setOnClickListener {
            var sendToAll = false
            sendToAll = activityMainBinding.sendEveryone.isChecked
            val userId = activityMainBinding.etui.text.toString().toInt()
            val message = activityMainBinding.messageEt.text.toString()
            val data = JSONObject().apply {
                put("userId", userId)
                put("message", message)
                put("toAll", sendToAll)
            }
            socket.emit("sendMessage", data)
        }
    }
}