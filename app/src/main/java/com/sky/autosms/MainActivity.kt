package com.sky.autosms

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.sky.autosms.data.model.SMS
import com.sky.autosms.ui.theme.AutoSmsTheme
import com.sky.autosms.ui.theme.view_models.SMSViewModel
import com.sky.autosms.utils.PermissionUtil

class MainActivity : ComponentActivity() {
    private lateinit var launcher: ActivityResultLauncher<String>
    private val viewModel: SMSViewModel by viewModels()
    private val TAG: String = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //1. 注册权限申请回调
        launcher = registerForActivityResult(RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                //权限申请通过，继续执行
                Toast.makeText(this, "权限申请通过", Toast.LENGTH_SHORT).show()
            } else {
                //权限申请未通过
                Toast.makeText(this, "你小子不给我权限", Toast.LENGTH_SHORT).show()
            }
        }


        setContent {
            AutoSmsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        var value by remember { mutableStateOf("暂无内容") }
                        val smsList by viewModel.smsList.observeAsState(emptyList())


                        val READ_SMS = Manifest.permission.READ_SMS
                        val ACCESS_BACKGROUND_LOCATION = Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        val permission = READ_SMS


                        SmsList(smsList = smsList, modifier = Modifier
                            .fillMaxWidth() // 占据整个宽度
                            .weight(1f) // 高度占据除按钮外的所有高度
                            .padding(10.dp) // 设置外边距
                            )


                        CheckPermission(modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min) //适应内容的最小值
                            , clickListener = {
                                when {
                                    PermissionUtil.check(
                                        permission,
                                        this@MainActivity
                                    ) -> {
                                        //permission already have
                                        Toast.makeText(
                                            this@MainActivity,
                                            "有权限了",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        value = "有权限了"
                                        viewModel.getSMS()
                                    }

                                    ActivityCompat.shouldShowRequestPermissionRationale(
                                        this@MainActivity,
                                        permission
                                    ) -> {
                                        //show request permission ui to user
                                        Toast.makeText(
                                            this@MainActivity,
                                            "不给权限不让用",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        value = "不给权限不让用"
                                    }

                                    else -> {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "在申请权限",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        value = "在申请权限"
                                        launcher.launch(permission)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CheckPermission(
    clickListener: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = clickListener,
        modifier = modifier
    ) {
        Text(text = "Update")
    }
}

@Composable
fun SmsList(smsList: List<SMS>, modifier: Modifier) {
    LazyColumn (modifier = modifier){
        items(smsList.size) { index ->
            SmsItem(smsList[index])
        }
    }
}


@Composable
fun SmsItem(smsData: SMS) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text("From: ${smsData.sender}", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(smsData.body)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AutoSmsTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SmsList(smsList = arrayListOf(
                SMS(sender = "xxx", body = "ahhhhhhh"),
                SMS(sender = "xxx", body = "ahhhhhhh"),
                SMS(sender = "xxx", body = "ahhhhhhh"),
                SMS(sender = "xxx", body = "ahhhhhhh")
            ),
                modifier = Modifier
                    .fillMaxWidth() // 占据整个宽度
                    .weight(1f) // 高度占据除按钮外的所有高度
                    .padding(10.dp) // 设置外边距
//                    .padding(10.dp), // 设置内边距
            )
//            Text(
//                text = "Hello Android!",
//                modifier = Modifier
//                    .fillMaxWidth() // 占据整个宽度
//                    .weight(1f) // 高度占据除按钮外的所有高度
//                    .padding(10.dp) // 设置外边距
//                    .background(
//                        color = Color.Gray,
//                        shape = MaterialTheme.shapes.medium.copy(
//                            topStart = CornerSize(10.dp),
//                            topEnd = CornerSize(10.dp),
//                            bottomEnd = CornerSize(10.dp),
//                            bottomStart = CornerSize(10.dp)
//                        )
//                    )
//                    .padding(10.dp), // 设置内边距
//            )
            CheckPermission(modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min) //适应内容的最小值
                , clickListener = {

                })
        }
    }
}