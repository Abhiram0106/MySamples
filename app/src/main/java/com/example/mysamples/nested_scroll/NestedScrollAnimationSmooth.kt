package com.example.mysamples.nested_scroll

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp

@Composable
fun NestedScrollAnimationSmooth(modifier: Modifier = Modifier) {
//
//
//    val nestedScrollConnection = remember {
//        object : NestedScrollConnection {
//            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
//                val delta = available.y.toInt()
//                val newTopContentHeight = topContentHeight + delta
//                val previousTopContentHeight = topContentHeight
//                topContentHeight = newTopContentHeight.coerceIn(maxSize / 3, maxSize)
//                val consumed = topContentHeight - previousTopContentHeight
//
//                return Offset(0f, consumed)
//            }
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .nestedScroll(nestedScrollConnection)
//    ) {
//        BoxWithConstraints {
//            TopContent()
//        }
//        Spacer(modifier = Modifier.height(10.dp))
//        BottomContentSimple(list = generateListItems(1))
//    }
}

@Composable
private fun TopContent(modifier: Modifier = Modifier, scrollOffset: Float) {

    val iconSize by animateDpAsState(
        targetValue = if (scrollOffset > 150f) 48.dp else 108.dp,
        label = "iconSize_anim"
    )
    val iconAlignment by animateFloatAsState(
        targetValue = if (scrollOffset > 150f) 0f else 0.5f,
        label = "iconAlignment_anim"
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (scrollOffset > 150f) 0f else 1f,
        label = "textAlpha_anim"
    )

    val isRow = scrollOffset > 150f

    Box() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .alpha(if (isRow) 1F else 0F)
            ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(iconSize)
            )
        }
        Column(
            modifier = modifier
                .fillMaxWidth()
                .alpha(if (isRow) 0F else 1F)
            ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(108.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "first",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "second",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun BottomContentSimple(
    modifier: Modifier = Modifier,
    list: List<String>,
) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(list) {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

private fun generateListItems(seed: Int): List<String> {
    val list = mutableListOf<String>()
    repeat(50) {
        list.add("seed $seed item $it")
    }
    return list.toList()
}
