package vu.pham.todotaskapp.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vu.pham.todotaskapp.R
import vu.pham.todotaskapp.ui.theme.BackgroundColor
import vu.pham.todotaskapp.ui.theme.BlackLight
import vu.pham.todotaskapp.ui.theme.OrangeLight
import vu.pham.todotaskapp.ui.theme.PrimaryColor
import vu.pham.todotaskapp.ui.theme.TextColor
import vu.pham.todotaskapp.ui.theme.ToDoTaskAppTheme
import vu.pham.todotaskapp.ui.theme.WhiteColor
import vu.pham.todotaskapp.ui.theme.WhiteColor2
import vu.pham.todotaskapp.ui.theme.WhiteColor3
import vu.pham.todotaskapp.ui.utils.ToDoFAB
import vu.pham.todotaskapp.ui.utils.ToDoProgressBar
import vu.pham.todotaskapp.ui.utils.ToDoTextField
import vu.pham.todotaskapp.utils.width

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoTaskAppTheme {
                MainPage()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainPage() {
    Scaffold(
        floatingActionButton = {
            ToDoFAB {

            }
        },
        content = {
            CompositionLocalProvider(
                LocalOverscrollConfiguration provides null,
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BackgroundColor)
                            .padding(start = 10.dp, end = 10.dp, top = 20.dp, bottom = 10.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            TextTitle()
                            Image(
                                painterResource(id = R.drawable.ic_to_do_list),
                                contentDescription = null,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                        ToDoTextField(modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                            hintText = "Search Task Here", leadingIcon = {
                                Icon(
                                    Icons.Outlined.Search, contentDescription = null,
                                    tint = WhiteColor
                                )
                            },
                            onTextChanged = {

                            })
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp, bottom = 16.dp)
                        ) {
                            Text(text = "Progress", fontSize = 20.sp, color = TextColor)
                            Text(text = "See All", fontSize = 16.sp, color = PrimaryColor)
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(),
                            color = BlackLight,
                            shape = RoundedCornerShape(5)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 20.dp)
                            ) {
                                Text(
                                    text = "Daily Task", color = TextColor, fontSize = 16.sp,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                Text(
                                    text = "2/3 Task Completed",
                                    color = WhiteColor2,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "You are almost done go ahead",
                                        color = WhiteColor3,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "50%", color = TextColor, fontSize = 16.sp,
                                    )
                                }
                                ToDoProgressBar(50)
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp, bottom = 16.dp)
                        ) {
                            Text(text = "Today's Task", fontSize = 20.sp, color = TextColor)
                            Text(text = "See All", fontSize = 16.sp, color = PrimaryColor)
                        }
                        Column {
                            repeat(3) { i ->
                                TaskItem(i)
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp, bottom = 16.dp)
                        ) {
                            Text(text = "Tommorrow's Task", fontSize = 20.sp, color = TextColor)
                            Text(text = "See All", fontSize = 16.sp, color = PrimaryColor)
                        }
                        Column {
                            repeat(3) { i ->
                                TaskItem(i)
                            }
                        }
                    }
                }
            )
        }
    )

}

@Composable
fun TaskItem(index: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
            .padding(bottom = 10.dp),
        color = BlackLight,
        shape = RoundedCornerShape(5)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(OrangeLight)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp)
                    .background(BlackLight)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Mobile App Research",
                        fontSize = 16.sp,
                        color = TextColor
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.DateRange,
                            contentDescription = null,
                            tint = WhiteColor2,
                            modifier = Modifier.padding(end = 5.dp)
                        )
                        Text(text = "4 Oct", fontSize = 12.sp, color = WhiteColor2)
                    }
                }
                Image(
                    painterResource(id = if (index % 2 == 0) R.drawable.ic_completed else R.drawable.ic_not_completed),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun TextTitle() {
    val inlineContent = mapOf(
        Pair(
            "inlineContent",
            InlineTextContent(
                Placeholder(
                    width = 25.sp,
                    height = 25.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.AboveBaseline
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_pencil),
                    "",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(50.dp)
                )
            }
        )
    )
    val text = buildAnnotatedString {
        append("You have got 5 tasks today to complete")
        appendInlineContent("inlineContent", "[icon]")
    }
    Text(
        text = text,
        inlineContent = inlineContent,
        modifier = Modifier.width((width() * 0.7).dp),
        color = TextColor,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        style = LocalTextStyle.current.copy(lineHeight = 35.sp)
    )
}

@Preview(showBackground = true)
@Composable
fun ToDoPreview() {
    ToDoTaskAppTheme {
        MainPage()
    }
}