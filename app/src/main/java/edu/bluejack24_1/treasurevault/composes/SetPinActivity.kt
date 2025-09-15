package edu.bluejack24_1.treasurevault.composes

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import edu.bluejack24_1.treasurevault.composes.components.PinScreenTemplate
import edu.bluejack24_1.treasurevault.composes.components.onAuthenticationSuccessful
import edu.bluejack24_1.treasurevault.composes.ui.theme.TreasureVaultTheme
import kotlinx.coroutines.delay

class SetPinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TreasureVaultTheme {
                SetPinScreen()
            }
        }
    }
}

@Composable
fun SetPinScreen() {
    val inputPin = remember { mutableStateListOf<Int>() }
    val inputFirstPin = remember { mutableStateOf("") }
    val inputSecondPin = remember { mutableStateOf("") }
    val error = remember { mutableStateOf("") }
    val command = remember { mutableStateOf("Set Your Pin") }
    val context = LocalContext.current
    val pinSharedPrefs = context.getSharedPreferences("PinPrefs", Context.MODE_PRIVATE)
    val newPinSize = 6

    val currentPin = pinSharedPrefs.getString("PinKey", null)
    LaunchedEffect(inputPin.size) {
        if (inputPin.size == newPinSize) {
            delay(300)

            if (inputFirstPin.value == "") {
                inputFirstPin.value = inputPin.joinToString("")

                if (currentPin != null && currentPin == inputFirstPin.value) {
                    Toast.makeText(context, "New Pin Cannot Be Same as Old Pin!", Toast.LENGTH_SHORT).show()
                    inputPin.clear()
                    inputFirstPin.value = ""
                    command.value = "Set Your Pin"
                } else {
                    inputPin.clear()
                    command.value = "Confirm Your Pin"
                }
            } else {
                inputSecondPin.value = inputPin.joinToString("")

                if (inputFirstPin.value == inputSecondPin.value) {
                    with(pinSharedPrefs.edit()) {
                        putString("PinKey", inputFirstPin.value)
                        apply()
                    }
                    inputPin.clear()
                    inputFirstPin.value = ""
                    inputSecondPin.value = ""
                    Toast.makeText(context, "Pin Set Successfully!", Toast.LENGTH_SHORT).show()
                    onAuthenticationSuccessful(context)
                } else {
                    inputPin.clear()
                    inputFirstPin.value = ""
                    inputSecondPin.value = ""
                    Toast.makeText(context, "Pin Does Not Match!", Toast.LENGTH_SHORT).show()
                    command.value = "Set Your Pin"
                }
            }
        }
    }

    PinScreenTemplate(
        title = command.value,
        pinSize = newPinSize,
        currentPinSize = inputPin.size,
        error = error.value,
        onPinAdd = { inputPin.add(it) },
        onPinRemove = { if (inputPin.isNotEmpty()) inputPin.removeLast() },
        isBiometricAvailable = false
    )
}