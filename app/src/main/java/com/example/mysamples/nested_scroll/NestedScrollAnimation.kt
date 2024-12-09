package com.example.mysamples.nested_scroll

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Tab
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.mysamples.R
import kotlinx.coroutines.launch

@Composable
fun NestedScrollAnimation(modifier: Modifier = Modifier) {

    val maxSize by remember {
        mutableFloatStateOf(250F)
    }
    var topContentHeight by remember {
        mutableFloatStateOf(maxSize)
    }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                return super.onPostFling(consumed, available)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                return super.onPostScroll(consumed, available, source)
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                return super.onPreFling(available)
            }

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y.toInt()
                val newTopContentHeight = topContentHeight + delta
                val previousTopContentHeight = topContentHeight
                topContentHeight = newTopContentHeight.coerceIn(maxSize / 3, maxSize)
                val consumed = topContentHeight - previousTopContentHeight

                return Offset(0f, consumed)
//                return super.onPreScroll(available, source)
            }
        }
    }
    val listOneItems = remember {
        mutableStateListOf(generateListItems(seed = 1))
    }
    val listTwoItems = remember {
        mutableStateListOf(generateListItems(seed = 2))
    }
    val listThreeItems = remember {
        mutableStateListOf(generateListItems(seed = 3))
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        TopContent(maxImageHeight = topContentHeight)
        Spacer(modifier = Modifier.height(10.dp))
        BottomContentSimple(list = listOneItems[0])
//        BottomContent(
//            listOne = listOneItems[0],
//            listTwo = listTwoItems[0],
//            listThree = listThreeItems[0]
//        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun TopContent(modifier: Modifier = Modifier, maxImageHeight: Float) {

    val density = LocalDensity.current
    val imageSize = remember(maxImageHeight) { with(density) { maxImageHeight.toDp() } }
    val rowThreshold by remember { mutableFloatStateOf(150F) }
    val isRow by remember(maxImageHeight) { mutableStateOf(maxImageHeight < rowThreshold) }

    SharedTransitionLayout {
        AnimatedContent(targetState = isRow, label = "NestedScroll_Col_to_Row") {
            if (it) {
                with(this@SharedTransitionLayout) {
                    Row(
                        modifier = modifier
                            .sharedElement(
                                rememberSharedContentState(key = "test"),
                                animatedVisibilityScope = this@AnimatedContent
                            )
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(imageSize)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "first ".repeat(3),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "second ".repeat(3),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                }
            } else {
                with(this@SharedTransitionLayout) {
                    Column(
                        modifier = modifier
                            .sharedElement(
                                rememberSharedContentState(key = "test"),
                                animatedVisibilityScope = this@AnimatedContent,
                                boundsTransform = { inital, target ->
                                    spring(0.8f, 380f)
                                }
                            )
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(imageSize)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "first ".repeat(3),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "second ".repeat(3),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "make this fade away",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                }
            }
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
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BottomContent(
    modifier: Modifier = Modifier,
    listOne: List<String>,
    listTwo: List<String>,
    listThree: List<String>
) {
    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }
    val tabItems = remember {
        mutableStateListOf("One", "Two", "Three")
    }
    val pagerState = rememberPagerState { tabItems.size }
    val scope = rememberCoroutineScope()
    MyTabRow(
        selectedTabIndex = selectedTabIndex,
        tabItems = tabItems,
        onClick = {
            scope.launch {
                pagerState.scrollToPage(it)
                selectedTabIndex = it
            }
        }
    )

    HorizontalPager(
        state = pagerState, verticalAlignment = Alignment.Top
    ) { page ->
        when (page) {
            0 -> {
                LazyColumn(modifier = modifier.fillMaxWidth()) {
                    items(listOne) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            1 -> {
                LazyColumn(modifier = modifier.fillMaxWidth()) {
                    items(listTwo) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            2 -> {
                LazyVerticalGrid(
                    state = rememberLazyGridState(),
                    columns = GridCells.Fixed(2),
                    modifier = modifier.fillMaxWidth()
                ) {
                    items(listThree) {
                        Image(
                            painter = painterResource(R.drawable.ic_launcher_background),
                            contentDescription = null,
                            modifier = modifier.padding(bottom = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MyTabRow(
    modifier: Modifier = Modifier,
    selectedTabIndex: Int,
    tabItems: List<String>,
    onClick: (index: Int) -> Unit,
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.background,
        divider = {
            HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.onBackground)
        },
        modifier = modifier
    ) {
        tabItems.forEachIndexed { index, title ->
            Tab(
                selected = index == selectedTabIndex,
                onClick = { onClick(index) },
                text = { Text(text = title, style = MaterialTheme.typography.bodyLarge) },
                unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .clip(CircleShape)
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