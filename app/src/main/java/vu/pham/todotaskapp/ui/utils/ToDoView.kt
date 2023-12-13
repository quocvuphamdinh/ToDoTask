package vu.pham.todotaskapp.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vu.pham.todotaskapp.ui.theme.BlackColor
import vu.pham.todotaskapp.ui.theme.BlackLight
import vu.pham.todotaskapp.ui.theme.PrimaryColor
import vu.pham.todotaskapp.ui.theme.PrimaryWithGreyColor
import vu.pham.todotaskapp.ui.theme.TextColor
import vu.pham.todotaskapp.ui.theme.WhiteColor
import vu.pham.todotaskapp.utils.width

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoTextField(
    onTextChanged: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit)?,
    hintText: String?,
    modifier: Modifier
) {
    var textValue by remember {
        mutableStateOf("")
    }
    TextField(
        value = textValue, onValueChange = {
            textValue = it
            onTextChanged(textValue)
        },
        textStyle = TextStyle(
            color = TextColor,
            fontSize = 18.sp
        ),
        singleLine = true,
        modifier = modifier,
        leadingIcon = leadingIcon,
        shape = RoundedCornerShape(20),
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = WhiteColor,
            textColor = WhiteColor,
            disabledTextColor = WhiteColor,
            containerColor = BlackLight,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(
                text = hintText ?: "", fontSize = 18.sp,
                color = TextColor
            )
        }
    )
}

@Composable
fun ToDoProgressBar(progress: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(15.dp))
                .height(18.dp)
                .background(PrimaryWithGreyColor)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(15.dp))
                    .height(18.dp)
                    .background(PrimaryColor)
                    .width((width() * progress / 100).dp)
            )
        }

    }
}

@Composable
fun ToDoFAB(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() },
        shape = CircleShape,
        containerColor = PrimaryColor
    ) {
        Icon(Icons.Filled.Add, "Create To Do Task", tint = BlackColor)
    }
}