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
import androidx.compose.ui.res.stringResource
import edu.bluejack24_1.treasurevault.R
import edu.bluejack24_1.treasurevault.composes.components.PinScreenTemplate
import edu.bluejack24_1.treasurevault.composes.components.onAuthenticationSuccessful
import edu.bluejack24_1.treasurevault.composes.ui.theme.TreasureVaultTheme
import edu.bluejack24_1.treasurevault.models.Setting
import edu.bluejack24_1.treasurevault.utilities.BiometricUtility
import edu.bluejack24_1.treasurevault.utilities.NavigationUtility

class EnterPinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isPinSet()) {
            NavigationUtility.navigateTo(this, SetPinActivity::class.java)
            return
        }

        setContent {
            TreasureVaultTheme {
                EnterPinScreen()
            }
        }
    }

    private fun isPinSet(): Boolean {
        return getSharedPreferences("PinPrefs", Context.MODE_PRIVATE)
            .getString("PinKey", null) != null
    }
}

@Composable
fun EnterPinScreen() {
    val inputPin = remember { mutableStateListOf<Int>() }
    val error = remember { mutableStateOf("") }
    val context = LocalContext.current
    val pinSharedPrefs = context.getSharedPreferences("PinPrefs", Context.MODE_PRIVATE)
    val savedPin = pinSharedPrefs.getString("PinKey", null)
    val pinSize = 6
    val isBiometricAvailable = BiometricUtility.isBiometricAvailable(context) && Setting.useBiometric

    if (inputPin.size == pinSize) {
        LaunchedEffect(true) {
            if (inputPin.joinToString("") == savedPin) {
                onAuthenticationSuccessful(context)
            } else {
                inputPin.clear()
                Toast.makeText(context, "Wrong Pin! Please Try Again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(isBiometricAvailable) {
        if (isBiometricAvailable) {
            BiometricUtility.showBiometricPrompt(context, onSuccess = {
                onAuthenticationSuccessful(context)
            })
        }
    }

    PinScreenTemplate(
        title = stringResource(R.string.enter_your_pin),
        pinSize = pinSize,
        currentPinSize = inputPin.size,
        error = error.value,
        onPinAdd = { inputPin.add(it) },
        onPinRemove = { if (inputPin.isNotEmpty()) inputPin.removeLast() },
        isBiometricAvailable,
    )
}
