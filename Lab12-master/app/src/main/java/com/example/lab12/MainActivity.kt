package com.example.lab12

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import okhttp3.*
import okhttp3.Request.Builder.build
import okhttp3.Request.Builder.url
import java.io.IOException

class MainActivity : AppCompatActivity() {
    internal inner class Data {
        var result: Result? = null

        internal inner class Result {
            lateinit var results: Array<Results>

            internal inner class Results {
                var Station: String? = null
                var Destination: String? = null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btn_query).setOnClickListener { v: View? ->
            val URL = "https://lab12-api.web.app/"
            val request: Request = Builder().url(URL).build()
            OkHttpClient().newCall(request).enqueue(object : Callback {
                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.code == 200) {
                        if (response.body == null) return@setOnClickListener
                        val data = Gson().fromJson(
                            response.body!!.string(), Data::class.java
                        )
                        val items = arrayOfNulls<String>(data.result!!.results.size)
                        for (i in items.indices) {
                            items[i] = """
列車即將進入：${data.result!!.results[i].Station}
 列車行駛目的地：${data.result!!.results[i].Destination}"""
                        }
                        runOnUiThread {
                            AlertDialog.Builder(this@MainActivity)
                                .setTitle("臺北捷運列車到站名")
                                .setItems(items, null)
                                .show()
                        }
                    } else if (!response.isSuccessful) Log.e(
                        "伺服器錯誤",
                        response.code.toString() + " " + response.message
                    ) else Log.e("其他錯誤", response.code.toString() + " " + response.message)
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.e("查詢失敗", e.message!!)
                }
            })
        }
    }
}