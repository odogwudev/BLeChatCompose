package com.example.blechatcompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue


@Composable
fun Chats() {
    val message by ChatServer.messages.observeAsState()

    val inputvalue = remember { mutableStateOf(TextFieldValue()) }

    val messageList = remember {
        mutableStateListOf<Message>()
    }

    if (message != null && !messageList.contains(message)) {
        messageList.add(message)
    }



    if (messageList.isNotEmpty()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Chat Now with ${ChatServer.currentDevice?.name ?: "Someone"}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Surface(modifier = Modifier
                .padding(all = Dp(5f))
                .fillMaxHeight(fraction = 0.85f)) {
                ChatsList(messageList)
            }


            InputField(inputvalue)
        }
    } else {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(fraction = 0.85f)
            ) {
                Text(text = "No Chat History")
            }

            InputField(inputvalue = inputvalue)
        }
    }
}