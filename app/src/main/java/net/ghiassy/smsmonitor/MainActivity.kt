package net.ghiassy.smsmonitor


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.ghiassy.smsmonitor.ui.theme.SMSMonitorTheme
import java.util.UUID

class MainActivity : ComponentActivity() {

    private val REQUEST_CODE_PERMISSIONS = 1
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission is granted
                // Proceed with your application logic
            } else {
                // Permission is not granted
                // Close the application
                Toast.makeText(this, "Permission is not granted - Required", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }

    private lateinit var serviceIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selfCheckPermission()
        serviceIntent = Intent(this, SMSService::class.java)
        setContent {
            SMSMonitorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent()
                }
            }
        }
    }

    private fun selfCheckPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is already granted
            Log.d("MainActivity---Receive------>", "Permission already granted")
        } else {
            // Permission is not granted
            //Toast.makeText(this, "Permission is not granted", Toast.LENGTH_SHORT).show()
            requestPermissionLauncher.launch(Manifest.permission.RECEIVE_SMS)
        }
    }


    fun startService() {
        val context = this@MainActivity
        Toast.makeText(context, "Starting Service...", Toast.LENGTH_SHORT).show()
        context.startService(serviceIntent)
    }

    fun stopService() {
        val context = this@MainActivity
        Toast.makeText(context, "Stopping Service...", Toast.LENGTH_SHORT).show()
        context.stopService(serviceIntent)
    }

    private fun openBatteryOptimizationSettings() {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        startActivity(intent)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent() {
    val context = LocalContext.current
    val textState = remember { mutableStateOf(TextFieldValue("saeed@****.com")) }

    val coroutineScope = rememberCoroutineScope()
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher


    val mySingleton = MySingleton.getInstance()
    mySingleton.email = textState.value.text



    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            TextField(
                value = textState.value,
                onValueChange = { newValue ->
                    textState.value = newValue
                },
                label = { Text(text = "Receiver Email Address:") }
            )
        }
        Row {
            Button(onClick = {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        sendEmail(
                            textState.value.text,
                            "Test Email",
                            "Random Content: ${UUID.randomUUID()}"
                        )

                    }
                }
                Toast.makeText(
                    context,
                    "Test Email sent to: ${textState.value.text}",
                    Toast.LENGTH_LONG
                ).show()
                mySingleton.email = textState.value.text

            }, Modifier.padding(8.dp)) {
                Text(text = "Send Test Email!")
            }
        }
        Row {
            Button(onClick = {

                (context as? MainActivity)?.startService()

                coroutineScope.launch {
                    onBackPressedDispatcher?.onBackPressed()
                    (context as? Activity)?.moveTaskToBack(true)
                }

            }, Modifier.padding(8.dp)) {
                Text(text = "Start Service")
            }
            Button(onClick = {

                (context as? MainActivity)?.stopService()


            }, Modifier.padding(8.dp)) {
                Text(text = "Stop Service")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainContentPreview() {
    SMSMonitorTheme {
        MainContent()
    }
}
