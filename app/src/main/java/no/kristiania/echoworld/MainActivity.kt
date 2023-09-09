package no.kristiania.echoworld

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.kristiania.echoworld.ui.theme.EchoWorldTheme
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Request.Builder
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString


class MainActivity : ComponentActivity() {
    var client = OkHttpClient()
    var webSocket: WebSocket? = null
    var userInput by mutableStateOf("")

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val wss_url = "ws://192.168.0.109:8765"
        run(wss_url)


        setContent {
            var isButtonClicked by remember { mutableStateOf(false) }

            EchoWorldTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                isButtonClicked = true
                            }
                        ) {
                            Text("Send msg server")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                        ) {
                            TextField(
                                value = userInput,
                                onValueChange = {
                                    userInput = it
                                    isButtonClicked = false // Reset on text change
                                },
                                label = {
                                    Text("Enter a message")
                                },
                                modifier = Modifier.fillMaxWidth().padding(16.dp)
                            )

                            // Display the text when the button is clicked
                            if (isButtonClicked) {
                                Text(
                                    text = userInput,
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                webSocket?.send(userInput)
                            }
                        }
                    }
                }
            }
        }
    }

    fun run(url: String) {
        val request = Builder().url(url).build()
        val listener = EchoWebSocketListener()
        webSocket = OkHttpClient().newWebSocket(request, listener)
    }
}

private class EchoWebSocketListener() : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        // Send the userInput to the WebSocket server

    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        output("Receiving : " + text!!)
    }

    // This will be unused in this assignment, but we'll leave it here
    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        output("Receiving bytes : " + bytes!!.hex())
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket!!.close(NORMAL_CLOSURE_STATUS, null)
        output("Closing : $code / $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        output("Error : " + t.message)
    }

    companion object {
        private val NORMAL_CLOSURE_STATUS = 1000
    }

    private fun output(txt: String) {
        Log.v("WSS", txt)
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "$name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EchoWorldTheme {
        Greeting("Echo, World!")
    }
}
