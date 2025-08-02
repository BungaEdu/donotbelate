package com.example.donotbelate_v3.presentation.components

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