package com.bungaedu.donotbelate.presentation.components

import android.widget.NumberPicker
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun NumberPickerComposable(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange
) {
    // 1) Coerce para que nunca intentes poner 0 (o fuera de rango)
    val coerced = value.coerceIn(range)

    AndroidView(
        factory = { context ->
            NumberPicker(context).apply {
                minValue = range.first
                maxValue = range.last
                // 2) Aplica el valor “seguro”
                this.value = coerced
                descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                wrapSelectorWheel = true

                setOnValueChangedListener { _, _, newVal ->
                    // 3) Notifica solo si realmente cambia
                    if (newVal != value) onValueChange(newVal)
                }

                // 4) Si el padre venía con 0 (u otro fuera de rango), sincroniza hacia arriba una vez
                if (coerced != value) {
                    // Postea al siguiente loop para evitar conflictos de inicialización
                    post { onValueChange(coerced) }
                }
            }
        },
        update = { picker ->
            val safe = value.coerceIn(range)
            // 5) Evita “pelear” con el scroll del usuario: solo asigna si es distinto
            if (picker.value != safe) {
                picker.value = safe
            }
        }
    )
}
