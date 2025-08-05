package com.example.donotbelate.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.donotbelate.ui.theme.Purple40
import com.example.donotbelate.ui.theme.White
import com.example.donotbelate.R

//TODO verificar el warning que no tiene sentido
@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Imagen ocupa exactamente el 25% del ancho
                Image(
                    painter = painterResource(id = R.drawable.reloj),
                    contentDescription = "Icono de reloj",
                    modifier = Modifier
                        .weight(0.25f)
                        .aspectRatio(1f)
                        .padding(end = 8.dp)
                )

                // Texto ocupa el 75% restante - Se ajusta automáticamente para que quepa completo
                BoxWithConstraints(
                    modifier = Modifier.weight(0.75f)
                ) {
                    val density = LocalDensity.current
                    val textMeasurer = rememberTextMeasurer()
                    val text = "NO LLEGUES TARDE!"

                    // Calculamos el tamaño de fuente que permita que el texto completo quepa
                    val fontSize = remember(maxWidth) {
                        // Empezamos con un tamaño grande y vamos bajando hasta que quepa
                        var testSize = 20.sp
                        val availableWidthPx = with(density) { (maxWidth - 16.dp).toPx() } // restamos padding

                        while (testSize.value > 8) { // mínimo 8sp
                            val textStyle = TextStyle(fontSize = testSize)
                            val textLayout = textMeasurer.measure(
                                text = text,
                                style = textStyle
                            )

                            if (textLayout.size.width <= availableWidthPx) {
                                break
                            }
                            testSize = (testSize.value - 0.5).sp
                        }
                        testSize
                    }

                    Text(
                        text = text,
                        fontSize = fontSize,
                        color = White,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Purple40,
            titleContentColor = White,
            actionIconContentColor = White
        )
    )
}