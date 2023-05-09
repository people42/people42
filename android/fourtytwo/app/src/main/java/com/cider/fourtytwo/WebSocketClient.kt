package com.cider.fourtytwo
import android.content.ContentValues.TAG
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

class WebSocketClient(private val userUrl: String) {
    private lateinit var webSocket: WebSocket
    fun start() {
        Log.i(TAG, "웹소켓: $userUrl")
        val client = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()

        val request = Request.Builder()
            .url(userUrl)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                Log.i(TAG, "웹소켓: open")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                println("웹소켓 Received message: $text")
//                val gson = Gson()
//                val jsonObject = gson.fromJson(text, JsonObject::class.java)
//                val json = gson.toJson(jsonObject)
//                val message = Gson().fromJson(text, Message::class.java)

                // 변환된 데이터 클래스를 MainActivity로 전달
//                webSocketListener.onDataReceived(message)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                println("Received bytes: ${bytes.hex()}")
                Log.i(TAG, "웹소켓: onMessage")

            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                webSocket.close(1000, null)
                println("Closing: $code / $reason")
                Log.i(TAG, "웹소켓: onClosing")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                println("Error: ${t.message}")
            }
        })
    }

    fun stop() {
        Log.i(TAG, "웹소켓: stop")
        webSocket.close(1000, null)

    }

    fun sendMessage(message: String) {
        Log.i(TAG, "웹소켓: sendMessage $message")
        webSocket.send(message)
    }

}

//    private val client = OkHttpClient()
//    private val request = Request.Builder().url(url).build()
//    private var webSocket: WebSocket? = null
//
//    fun connect() {
//        webSocket = client.newWebSocket(request, createListener())
//    }
//
//    fun disconnect() {
//        webSocket?.close(1000, null)
//        webSocket = null
//    }
//
//    private fun createListener(): WebSocketListener {
//        return object : WebSocketListener() {
//            override fun onOpen(webSocket: WebSocket, response: Response) {
//                println("WebSocket connection opened")
//            }
//
//            override fun onMessage(webSocket: WebSocket, text: String) {
//                println("Received message: $text")
//            }
//
//            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
//                println("Received bytes: ${bytes.hex()}")
//            }
//
//            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
//                println("WebSocket closing")
//            }
//
//            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
//                println("WebSocket failed: ${t.message}")
//            }
//        }
//    }
//
//    suspend fun sendMessage(message: String) {
//        webSocket?.send(message) ?: throw IllegalStateException("WebSocket is not connected")
//    }
//
//    fun receiveMessages(): Flow<String> {
//        return flow {
//            webSocket?.let { socket ->
////                while (true) {
//                    socket.receive()?.let { message ->
//                        if (message is WebSocket.Text) {
//                            emit(message.text)
//                        }
//                    }
////                }
//            } ?: throw IllegalStateException("WebSocket is not connected")
//        }.catch { e ->
//            // Handle exceptions thrown by the flow
//            println("WebSocket error: ${e.message}")
//        }
//    }
//}