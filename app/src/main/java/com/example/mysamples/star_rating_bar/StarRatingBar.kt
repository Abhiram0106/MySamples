package com.example.mysamples.star_rating_bar

import androidx.annotation.FloatRange
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath

@Composable
fun TestingStarBar(modifier: Modifier = Modifier) {

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "some text here",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.error
        )
        StarRatingBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            starSize = 50.dp,
            selectedRating = {
                println(it)
            }
        )
    }
}

@Composable
fun StarRatingBar(
    modifier: Modifier = Modifier,
    starSize: Dp,
    noOfStars: Int = 5,
    selectedRating: (Float) -> Unit,
) {
    var progress by remember {
        mutableFloatStateOf(0F)
    }
    val maxWidth = starSize * noOfStars
    val filledWidth = maxWidth * progress

    LaunchedEffect(progress) {
        selectedRating(progress * noOfStars)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(starSize)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        val totalWidth = size.width
                        val touchX = offset.x
                        progress = (touchX / totalWidth).coerceIn(0f, 1f)
                    },
                    onHorizontalDrag = { change, _ ->
                        val totalWidth = size.width
                        val touchX = change.position.x
                        progress = (touchX / totalWidth).coerceIn(0f, 1f)
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        val totalWidth = size.width
                        val touchX = offset.x
                        progress = (touchX / totalWidth).coerceIn(0f, 1f)

                    }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
//                .background(MaterialTheme.colorScheme.primary)
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..5) {
                val starOffset = (i - 1) * starSize.asPx() // Calculate each star's position
                val overlapWidth = (filledWidth.asPx() - starOffset).coerceAtLeast(0f)

                Star(
                    modifier = Modifier.size(starSize),
                    fillFraction = (overlapWidth / starSize.asPx())
                        .coerceIn(0F, 1F),
                    starBorderColor = MaterialTheme.colorScheme.primary,
                    starFillColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun Star(
    modifier: Modifier = Modifier,
    @FloatRange(
        from = 0.0,
        to = 1.0
    ) fillFraction: Float, // How much of the star should be filled
    starBorderColor: Color,
    starFillColor: Color,
) {
    Box(
        modifier = modifier
            .drawWithCache {
                val roundedPolygon = RoundedPolygon.star(
                    numVerticesPerRadius = 5,
                    radius = size.minDimension / 2,
                    innerRadius = size.minDimension / 4,
                    centerX = size.width / 2,
                    centerY = size.height / 2,
                    rounding = CornerRounding(
                        radius = size.minDimension / 25F,
                        smoothing = 1F
                    )
                )
                val roundedPolygonPath = roundedPolygon
                    .toPath()
                    .asComposePath()

                onDrawBehind {
                    rotate(-18F) {
                        drawPath(
                            path = roundedPolygonPath,
                            color = starBorderColor,
                            style = Stroke(
                                width = 15F
                            )
                        )
                    }

                    if (fillFraction > 0f) {
                        val clipRectWidth = size.width * fillFraction
                        clipRect(0f, 0f, clipRectWidth, size.height) {
                            rotate(-18F) {
                                drawPath(
                                    path = roundedPolygonPath,
                                    color = starFillColor
                                )
                            }
                        }
                    }
                }
            }
            .fillMaxSize()
    )
}

@Preview
@Composable
private fun StarPreview() {
    Star(
        starBorderColor = MaterialTheme.colorScheme.primary,
        fillFraction = 0.5F,
        starFillColor = Color.Red
    )
}

@Preview
@Composable
private fun StarRatingBarPreview() {
    StarRatingBar(starSize = 50.dp, selectedRating = {})
}

@Preview
@Composable
private fun TestingPreview() {
    TestingStarBar()
}

@Composable
fun Dp.asPx(): Float {
    return LocalDensity.current.run {
        this@asPx.toPx()
    }
}

