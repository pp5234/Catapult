package com.example.catapult.core.compose

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun TextToChips (modifier: Modifier = Modifier, text : String, delim : Char, onClick : () -> Unit = {}, amount : Int? = null) {
    val strings: List<String> = text.split(delim).map { it.trim() }
    for (string in strings.take(amount ?: strings.size)) {
        if (string.isNotEmpty()) {
            SuggestionChip(
                colors = SuggestionChipDefaults.suggestionChipColors
                    (
                    labelColor = Color.White,
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = modifier,
                onClick = onClick,
                label = { Text(string) }
            )
        }
    }
}