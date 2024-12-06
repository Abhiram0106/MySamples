package com.example.mysamples.ai_text_load

import android.icu.text.BreakIterator
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.text.StringCharacterIterator

@Composable
fun AITextLoad(
    modifier: Modifier = Modifier,
    text: String,
) {
    // correctly handles emojis
    val breakIterator = remember(text) { BreakIterator.getCharacterInstance() }
    var streamText by remember {
        mutableStateOf("")
    }
    LaunchedEffect(text) {
        breakIterator.text = StringCharacterIterator(text)
        var nextIndex = breakIterator.next()
        while(nextIndex != BreakIterator.DONE) {
            streamText = text.subSequence(0, nextIndex).toString()
            nextIndex = breakIterator.next()
            delay(50)
        }
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10))
            .background(Color.Red)
            .padding(10.dp)
    ) {
        Text(
            text = streamText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.animateContentSize()
        )
    }
}