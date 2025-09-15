package edu.bluejack24_1.treasurevault.composes.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import edu.bluejack24_1.treasurevault.activities.MainActivity
import edu.bluejack24_1.treasurevault.models.User
import edu.bluejack24_1.treasurevault.utilities.BiometricUtility

@Composable
fun PinScreenTemplate(
    title: String,
    pinSize: Int,
    currentPinSize: Int,
    error: String,
    onPinAdd: (Int) -> Unit,
    onPinRemove: () -> Unit,
    isBiometricAvailable: Boolean
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Text(text = title, modifier = Modifier.padding(16.dp), color = Color.Black)
        Spacer(modifier = Modifier.height(30.dp))

        Row {
            (0 until pinSize).forEach {
                Icon(
                    imageVector = if (it < currentPinSize) Icons.Default.Circle else Icons.Outlined.Circle,
                    contentDescription = it.toString(),
                    modifier = Modifier
                        .padding(8.dp)
                        .size(30.dp),
                    tint = Color.Black
                )
            }
        }

        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(50.dp))

        Keypad(
            onPinAdd = onPinAdd,
            onPinRemove = onPinRemove,
            isBiometricAvailable = isBiometricAvailable,
            context = context
        )
    }
}

@Composable
fun Keypad(
    onPinAdd: (Int) -> Unit,
    onPinRemove: () -> Unit,
    isBiometricAvailable: Boolean,
    context: Context
) {
    Column(
        modifier = Modifier
            .padding(bottom = 20.dp),
        horizontalAlignment = Alignment.End
    ) {
        (1..9).chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { number ->
                    PinKeyItem(onClick = { onPinAdd(number) }) {
                        Text(text = number.toString(), color = Color.Black)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isBiometricAvailable) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = "Fingerprint",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            BiometricUtility.showBiometricPrompt(context, onSuccess = {
                                onAuthenticationSuccessful(context)
                            })
                        }
                )
            } else {
                Spacer(modifier = Modifier.size(30.dp))
            }

            PinKeyItem(onClick = { onPinAdd(0) }) {
                Text(text = "0", color = Color.Black)
            }

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Backspace,
                contentDescription = "Backspace",
                modifier = Modifier
                    .size(23.dp)
                    .clickable { onPinRemove() }
            )
        }
    }
}

@Composable
fun PinKeyItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
    backgroundColor: Color = MaterialTheme.colorScheme.onPrimary,
    contentColor: Color = contentColorFor(backgroundColor = backgroundColor),
    elevation: Dp = 1.dp,
    content: @Composable () -> Unit
) {
    modifier.padding(8.dp)

    Surface(
        modifier = modifier
            .shadow(elevation = elevation, shape = shape)
            .background(color = backgroundColor, shape = shape)
            .clickable(
                onClick = onClick,
                role = Role.Button,
            ),
        shape = shape,
        color = backgroundColor,
        contentColor = contentColor,
        tonalElevation = elevation,
    ) {
        Box(
            modifier = Modifier
                .defaultMinSize(minWidth = 64.dp, minHeight = 64.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

fun onAuthenticationSuccessful(context: Context) {
    User.isAuthenticated = true
    val intent = Intent(context, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
}