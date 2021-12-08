package com.codelab.layoutinjetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.*
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberImagePainter
import com.codelab.layoutinjetpackcompose.ui.theme.LayoutinJetpackComposeTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // modifier를 생성하는 방법
    @Stable
    fun Modifier.padding(all: Dp) =
        this.then(
            PaddingModifier(start = all, top = all, end = all, bottom = all, rtlAware = true)
        )

    // 세부내용 구현
    private class PaddingModifier(
        val start: Dp = 0.dp,
        val top: Dp = 0.dp,
        val end: Dp = 0.dp,
        val bottom: Dp = 0.dp,
        val rtlAware: Boolean,
    ) : LayoutModifier {

        override fun MeasureScope.measure(
            measurable: Measurable,
            constraints: Constraints
        ): MeasureResult {

            val horizontal = start.roundToPx() + end.roundToPx()
            val vertical = top.roundToPx() + bottom.roundToPx()

            val placeable = measurable.measure(constraints.offset(-horizontal, -vertical))

            val width = constraints.constrainWidth(placeable.width + horizontal)
            val height = constraints.constrainHeight(placeable.height + vertical)
            return layout(width, height) {
                if (rtlAware) {
                    placeable.placeRelative(start.roundToPx(), top.roundToPx())
                } else {
                    placeable.place(start.roundToPx(), top.roundToPx())
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LayoutinJetpackComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {


                    /*Row() {
                        TextWithNormalPaddingPreview()
                        TextWithPaddingToBaselinePreview()
                    }*/

//                    PhotographerCard()
//                    LayoutsCodelab()
//                    ScrollingList()

//                    ChipPreview()

//                    ConstraintLayoutContent()
//                    LargeConstraintLayoutPreview()
//                    DecoupledConstraintLayout()
                    TwoTextsPreview()
                }
            }
        }
    }


    @Composable
    fun TwoTexts(modifier: Modifier = Modifier, text1: String, text2: String) {
        Row(modifier = modifier.height(IntrinsicSize.Min)) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
                    .wrapContentWidth(Alignment.Start),
                text = text1
            )

            Divider(color = Color.Black, modifier = Modifier
                .fillMaxHeight()
                .width(1.dp))
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
                    .wrapContentWidth(Alignment.End),

                text = text2
            )
        }
    }

    @Preview
    @Composable
    fun TwoTextsPreview() {
        LayoutinJetpackComposeTheme {
            Surface {
                TwoTexts(text1 = "Hi", text2 = "there")
            }
        }
    }


    @Preview
    @Composable
    fun DecoupledConstraintLayout() {
        BoxWithConstraints {
            val constraints = if (maxWidth < maxHeight) {
                decoupledConstraints(margin = 16.dp) // Portrait constraints
            } else {
                decoupledConstraints(margin = 32.dp) // Landscape constraints
            }

            ConstraintLayout(constraints) {
                Button(
                    onClick = { /* Do something */ },
                    modifier = Modifier.layoutId("button")
                ) {
                    Text("Button")
                }

                Text("Text", Modifier.layoutId("text"))
            }
        }
    }

    private fun decoupledConstraints(margin: Dp): ConstraintSet {
        return ConstraintSet {
            val button = createRefFor("button")
            val text = createRefFor("text")

            constrain(button) {
                top.linkTo(parent.top, margin= margin)
            }
            constrain(text) {
                top.linkTo(button.bottom, margin)
            }
        }
    }


    @Composable
    fun LargeConstraintLayout() {
        ConstraintLayout {
            val text = createRef()

            val guideline = createGuidelineFromStart(fraction = 0.5f)
            Text(
                "This is a very very very very very very very long text",
                Modifier.constrainAs(text) {
                    linkTo(start = guideline, end = parent.end)
                    width = Dimension.preferredWrapContent

                }
            )
        }
    }

    @Preview
    @Composable
    fun LargeConstraintLayoutPreview() {
        LayoutinJetpackComposeTheme {
            LargeConstraintLayout()
        }
    }


    @Composable
    fun ConstraintLayoutContent() {
        ConstraintLayout {

            // ConstraintLayout내에 3가지 컴포저블들을 위한 레퍼런스들을 생성한다.
            val (button1, button2, text) = createRefs()

            Button(
                onClick = { /* Do something */ },
                modifier = Modifier.constrainAs(button1) {
                    top.linkTo(parent.top, margin = 16.dp)
                }
            ) {
                Text("Button 1")
            }

            Text("Text", Modifier.constrainAs(text) {
                top.linkTo(button1.bottom, margin = 16.dp)
                centerAround(button1.end)
            })

            val barrier = createEndBarrier(button1, text)
            Button(
                onClick = { /* Do something */ },
                modifier = Modifier.constrainAs(button2) {
                    top.linkTo(parent.top, margin = 16.dp)
                    start.linkTo(barrier)
                }
            ) {
                Text("Button 2")
            }
        }
    }



    @Composable
    fun StaggeredGrid(
        modifier: Modifier = Modifier,
        rows: Int = 3,
        content: @Composable () -> Unit
    ) {


        Layout(
            modifier = modifier,
            content = content
        ) { measurables, constraints ->

            // 각 행에 대한 너비를 추적한다.
            val rowWidths = IntArray(rows) { 0 }
            // 각 행에 대한 최대 높이를 추적한다.
            val rowHeights = IntArray(rows) { 0 }

            // 하위 view들을 제한하지 않고, 주어진 제약조건들과 함께 측정한다.
            // List of measured children
            val placeables = measurables.mapIndexed { index, measurable ->

                // 각 하위 요소를 측정 한다.
                val placeable = measurable.measure(constraints)

                // Track the width and max height of each row
                val row = index % rows
                rowWidths[row] += placeable.width
                rowHeights[row] = Math.max(rowHeights[row], placeable.height)

                placeable
            }


            // Grid의 너비는 가장 넓은 행이다.
            val width = rowWidths.maxOrNull()
                ?.coerceIn(constraints.minWidth.rangeTo(constraints.maxWidth))
                ?: constraints.minWidth

            // Grid의 높이는 높이 제약조건으로 인해 강제로 변한된 각 행의 가장 높은 요소의 합이다.
            val height = rowHeights.sumOf { it }
                .coerceIn(constraints.minHeight.rangeTo(constraints.maxHeight))


            // 이전 행들의 누적된 높이를 기반한, 각 행의 Y
            val rowY = IntArray(rows) { 0 }
            for (i in 1 until rows) {
                rowY[i] = rowY[i - 1] + rowHeights[i - 1]
            }


            // 상위 레이아웃의 사이즈를 설정하자.
            layout(width, height) {

                val rowX = IntArray(rows) { 0 }

                placeables.forEachIndexed { index, placeable ->
                    val row = index % rows
                    placeable.placeRelative(
                        x = rowX[row],
                        y = rowY[row]
                    )
                    rowX[row] += placeable.width
                }

            }

        }

    }

    @Composable
    fun Chip(modifier: Modifier = Modifier, text: String) {
        Card(
            modifier = modifier,
            border = BorderStroke(color = Color.Black, width = Dp.Hairline),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(
                    start = 8.dp,
                    top = 4.dp,
                    end = 8.dp,
                    bottom = 4.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp, 16.dp)
                        .background(color = MaterialTheme.colors.secondary)
                )
                Spacer(Modifier.width(4.dp))
                Text(text = text)
            }
        }
    }

    val topics = listOf(
        "Arts & Crafts", "Beauty", "Books", "Business", "Comics", "Culinary",
        "Design", "Fashion", "Film", "History", "Maths", "Music", "People", "Philosophy",
        "Religion", "Social sciences", "Technology", "TV", "Writing"
    )

    @Preview
    @Composable
    fun ChipPreview() {
        LayoutinJetpackComposeTheme {
            Chip(text = "Hi there")
        }
    }


    @Composable
    fun MyOwnColumn(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        Layout(
            modifier = modifier,
            content = content
        ) { measurables, constraints ->

            // 하위 뷰들을 제한하지 말고, 주어진 constraints로 측정하자.
            // 측정된 하위 요소 목록들
            val placeables = measurables.map { measurable ->
                // Measure each child
                measurable.measure(constraints)
            }

            // 수직으로 배치하기 위해 하위 요소들의 y 좌표값을 추적
            var yPosition = 0

            // 가능한 레이아웃의 사이즈를 크게 설정
            layout(constraints.maxWidth, constraints.maxHeight) {
                // 상위 레이아웃내 하위 요소들을 배치
                placeables.forEach { placeable ->
                    // 화면상에 항목들을 배치한다
                    placeable.placeRelative(x = 0, y = yPosition)

                    // y 좌표값을 기록한다.
                    yPosition += placeable.height
                }
            }
        }
    }

    @Preview
    @Composable
    fun BodyContent(modifier: Modifier = Modifier) {

        Row(
            modifier = modifier
                .background(color = Color.LightGray, shape = RectangleShape)
                .padding(16.dp)
                .size(200.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            StaggeredGrid {
                for (topic in topics) {
                    Chip(modifier = Modifier.padding(8.dp), text = topic)
                }
            }
        }


    }

    fun Modifier.firstBaselineToTop(
        firstBaselineToTop: Dp
    ) = this.then(
        layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)

            // Check the composable has a first baseline
            check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
            val firstBaseline = placeable[FirstBaseline]


            // 여백이 있는 컴포저블의 높이 - 첫번째 베이스라인
            val placeableY = firstBaselineToTop.roundToPx() // - firstBaseline
            val height = placeable.height + placeableY
            layout(placeable.width, height) {
                // 컴포저블이 자리잡을 위치
                placeable.placeRelative(0, placeableY)
            }
        }
    )

    @Preview
    @Composable
    fun TextWithPaddingToBaselinePreview() {
        LayoutinJetpackComposeTheme {
            Text("Hi there!", Modifier.firstBaselineToTop(32.dp))
        }
    }

    @Preview
    @Composable
    fun TextWithNormalPaddingPreview() {
        LayoutinJetpackComposeTheme {
            Text("Hi there!", Modifier.padding(top = 32.dp))
        }
    }

    @Preview
    @Composable
    fun ScrollingList() {
        val listSize = 100
        // We save the scrolling position with this state
        val scrollState = rememberLazyListState()
        // We save the coroutine scope where our animated scroll will be executed
        val coroutineScope = rememberCoroutineScope()

        Column {
            Row {
                Button(onClick = {
                    coroutineScope.launch {
                        // 0 is the first item index
                        scrollState.animateScrollToItem(0)
                    }
                }) {
                    Text("Scroll to the top")
                }

                Button(onClick = {
                    coroutineScope.launch {
                        // listSize - 1 is the last index of the list
                        scrollState.animateScrollToItem(listSize - 1)
                    }
                }) {
                    Text("Scroll to the end")
                }
            }

            LazyColumn(state = scrollState) {
                items(listSize) {
                    ImageListItem(it)
                }
            }
        }
    }


    @Composable
    fun ImageListItem(index: Int) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberImagePainter(
                    data = "https://developer.android.com/images/brand/Android_Robot.png"
                ),
                contentDescription = "Android Logo",
                modifier = Modifier.size(50.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text("Item #$index", style = MaterialTheme.typography.subtitle1)
        }
    }

    @Composable
    fun LayoutsCodelab() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "LayoutsCodelab")
                    },
                    actions = {
                        IconButton(onClick = { /* doSomething() */ }) {
                            Icon(Icons.Filled.Close, contentDescription = null)
                        }
                        IconButton(onClick = { /* doSomething() */ }) {
                            Icon(Icons.Filled.Favorite, contentDescription = null)
                        }
                        IconButton(onClick = { /* doSomething() */ }) {
                            Icon(Icons.Filled.Menu, contentDescription = null)
                        }
                    }

                )
            }
        ) { innerPadding ->
            BodyContent(Modifier.padding(innerPadding))
//        ScrollingList()
        }
    }


    @Composable
    fun PhotographerCard(modifier: Modifier = Modifier) {
        Row(
            modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colors.surface)
                .clickable(onClick = { /* Ignoring onClick */ })
                .padding(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
            ) {
                // Image goes here
            }
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text("Alfred Sisley", fontWeight = FontWeight.Bold)
                // LocalContentAlpha 는 자식들의 투명도를 정의한다.
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text("3 minutes ago", style = MaterialTheme.typography.body2)
                }
            }
        }
    }

    @Preview
    @Composable
    fun PhotographerCardPreview() {
        LayoutinJetpackComposeTheme {
            PhotographerCard()
        }
    }

}
