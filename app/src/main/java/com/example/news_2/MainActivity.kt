package com.example.news_2

import android.content.Context
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.eclipsesource.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.example.news_2.HttpUtil
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

//    val URL = "http://weather.livedoor.com/forecast/webservice/json/v1?city=400040" //サンプルとしてライブドアのお天気Webサービスを利用します
    val URL = "http://52.68.215.201/weather.json"
    var result = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val names = listOf(
            "あじさい",
            "蓮",
            "ネモフィラ",
            "バラ",
            "ふじ"
        )

        val descriptions = listOf(
            "アジサイ（紫陽花、学名 Hydrangea macrophylla）は、アジサイ科アジサイ属の落葉低木の一種である。広義には「アジサイ」の名はアジサイ属植物の一部の総称でもある。",
            "ハス（蓮、学名：Nelumbo nucifera）は、インド原産のハス科多年性水生植物。",
            "ネモフィラはムラサキ科ネモフィラ属（Nemophila）に分類される植物の総称。または、ルリカラクサ（瑠璃唐草、学名：Nemophila menziesii）のこと。",
            "バラ（薔薇）は、バラ科バラ属の総称である。あるいは、そのうち特に園芸種（園芸バラ・栽培バラ）を総称する。ここでは、後者の園芸バラ・栽培バラを扱うこととする。 バラ属の成形は、灌木、低木、または木本性のつる植物で、葉や茎に棘を持つものが多い。",
            "フジ（藤、学名: Wisteria floribunda）は、マメ科フジ属のつる性落葉木本。一般名称としての藤には、つるが右巻き（上から見て時計回り）と左巻きの二種類がある。"
        )

        val images = listOf(
            R.drawable.hydrangea,
            R.drawable.lotus,
            R.drawable.nemophila,
            R.drawable.rose,
            R.drawable.wisteria
        )

        val flowers = List(names.size){i->FlowerData(names[i], descriptions[i], images[i])}
        val adapter = FlowerListAdapter(this, flowers)
        myListView.adapter = adapter

        myListView.setOnItemClickListener { adapterView, view, position, id ->
            val name = view.findViewById<TextView>(R.id.nameTextView).text
            Toast.makeText(this, "clicked: $name", Toast.LENGTH_LONG).show()
        }

//        val getButton = findViewById(R.id.button) as Button
//        getButton.setOnClickListener(object : View.OnClickListener {
//            override
//            fun onClick(view: View) {
//                onParallelGetButtonClick()
//            }
//        })
    }

    data class FlowerData(val name: String, val desc: String, val imageId: Int)

    data class ViewHolder(val nameTextView: TextView, val descTextView: TextView, val flowerImgView: ImageView)

    class FlowerListAdapter(context: Context, flowers: List<FlowerData>) : ArrayAdapter<FlowerData>(context, 0, flowers) {
        private val layoutInflater = LayoutInflater.from(context)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            var view = convertView
            var holder: ViewHolder

            if(view == null) {
                view = layoutInflater.inflate(R.layout.list_item, parent, false)
                holder = ViewHolder(
                    view.findViewById(R.id.nameTextView)!!,
                    view.findViewById(R.id.descTextView),
                    view.findViewById(R.id.flowerImgView)
                )
                view.tag = holder
            } else {
                holder = view.tag as ViewHolder
            }

            val flower = getItem(position) as FlowerData
            holder.nameTextView.text = flower.name
            holder.descTextView.text = flower.desc
            holder.flowerImgView.setImageBitmap(BitmapFactory.decodeResource(context.resources, flower.imageId))

            return view!!

        }
    }

    fun onParallelGetButtonClick() = GlobalScope.launch(Dispatchers.Main) {
        val http = HttpUtil()
        //Mainスレッドでネットワーク関連処理を実行するとエラーになるためBackgroundで実行
        async(Dispatchers.Default) { http.httpGET1(URL) }.await().let {
            //minimal-jsonを使って　jsonをパース
            val result = Json.parse(it).asObject()
            val textView = findViewById(R.id.text) as TextView
//            textView.setText(result.get("description").asObject().get("text").asString())
            textView.setText(result.get("pinpointLocations").asArray().get(0).asObject().get("name").asString())
        }
    }
}
