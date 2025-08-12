package com.bungaedu.donotbelate.presentation.components

import android.widget.NumberPicker
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.Composable

@Composable
fun NumberPickerComposable(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange
) {
    AndroidView(
        factory = { context ->
            NumberPicker(context).apply {
                minValue = range.first
                maxValue = range.last
                this.value = value
                descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                setOnValueChangedListener { _, _, newVal ->
                    onValueChange(newVal)
                }
            }
        },
        update = { picker ->
            picker.value = value
        }
    )
}