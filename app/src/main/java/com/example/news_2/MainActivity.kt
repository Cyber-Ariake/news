package com.example.news_2

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

//    val URL = "http://weather.livedoor.com/forecast/webservice/json/v1?city=400040" //サンプルとしてライブドアのお天気Webサービスを利用します
    val URL = "http://52.68.215.201/news.json"
    var result = ""
    var jarray: JsonArray = JsonArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        onParallelGetButtonClick()
        val news = List(jarray.size()){i->NewsData(jarray.get(i).asObject().getString("title","title"),
            jarray.get(i).asObject().getString("href","href"),
            jarray.get(i).asObject().getString("time","time"))}
        Log.d("TAG","ここおおおおおおおおおおおおおおおおおおおおお")
        Log.d("TAG", Integer.toString(jarray.size()))

        val adapter = NewsListAdapter(this, news)
        myListView.adapter = adapter

//        val names = listOf(
//            "あじさい",
//            "蓮",
//            "ネモフィラ",
//            "バラ",
//            "ふじ"
//        )
//
//        val descriptions = listOf(
//            "アジサイ（紫陽花、学名 Hydrangea macrophylla）は、アジサイ科アジサイ属の落葉低木の一種である。広義には「アジサイ」の名はアジサイ属植物の一部の総称でもある。",
//            "ハス（蓮、学名：Nelumbo nucifera）は、インド原産のハス科多年性水生植物。",
//            "ネモフィラはムラサキ科ネモフィラ属（Nemophila）に分類される植物の総称。または、ルリカラクサ（瑠璃唐草、学名：Nemophila menziesii）のこと。",
//            "バラ（薔薇）は、バラ科バラ属の総称である。あるいは、そのうち特に園芸種（園芸バラ・栽培バラ）を総称する。ここでは、後者の園芸バラ・栽培バラを扱うこととする。 バラ属の成形は、灌木、低木、または木本性のつる植物で、葉や茎に棘を持つものが多い。",
//            "フジ（藤、学名: Wisteria floribunda）は、マメ科フジ属のつる性落葉木本。一般名称としての藤には、つるが右巻き（上から見て時計回り）と左巻きの二種類がある。"
//        )
//
//        val images = listOf(
//            R.drawable.hydrangea,
//            R.drawable.lotus,
//            R.drawable.nemophila,
//            R.drawable.rose,
//            R.drawable.wisteria
//        )
//
//        val flowers = List(names.size){i->FlowerData(names[i], descriptions[i], images[i])}
//
//
//
//        val adapter = FlowerListAdapter(this, flowers)
//        myListView.adapter = adapter
//
//        myListView.setOnItemClickListener { adapterView, view, position, id ->
//            val name = view.findViewById<TextView>(R.id.nameTextView).text
//            Toast.makeText(this, "clicked: $name", Toast.LENGTH_LONG).show()
//        }


//        val getButton = findViewById(R.id.button) as Button
//        getButton.setOnClickListener(object : View.OnClickListener {
//            override
//            fun onClick(view: View) : JsonArray {
//                val jarray : JsonArray = onParallelGetButtonClick()
//
//            }
//        })
//        var jarray : JsonArray? = onParallelGetButtonClick()
//        val getButton = findViewById(R.id.button) as Button
//        getButton.setOnClickListener(object : View.OnClickListener {
//            override
//            fun onClick(view: View) {
//                val textView = findViewById(R.id.text) as TextView
//                textView.setText(Integer.toString(jarray!!.size()))
//            }
//        })
//    }
//    data class FlowerData(val name: String, val desc: String, val imageId: Int)
//
//    data class ViewHolder(val nameTextView: TextView, val descTextView: TextView, val flowerImgView: ImageView)
//
//    class FlowerListAdapter(context: Context, flowers: List<FlowerData>) : ArrayAdapter<FlowerData>(context, 0, flowers) {
//        private val layoutInflater = LayoutInflater.from(context)
//
//        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//
//            var view = convertView
//            var holder: ViewHolder
//
//            if(view == null) {
//                view = layoutInflater.inflate(R.layout.list_item, parent, false)
//                holder = ViewHolder(
//                    view.findViewById(R.id.nameTextView)!!,
//                    view.findViewById(R.id.descTextView),
//                    view.findViewById(R.id.flowerImgView)
//                )
//                view.tag = holder
//            } else {
//                holder = view.tag as ViewHolder
//            }
//
//            val flower = getItem(position) as FlowerData
//            holder.nameTextView.text = flower.name
//            holder.descTextView.text = flower.desc
//            holder.flowerImgView.setImageBitmap(BitmapFactory.decodeResource(context.resources, flower.imageId))
//
//            return view!!
//
//        }
//    }
//
    }

    class NewsListAdapter(context: Context, news: List<NewsData>) : ArrayAdapter<NewsData>(context, 0, news) {
        private val layoutInflater = LayoutInflater.from(context)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            var view = convertView
            var holder: ViewHolder

            if(view == null) {
                view = layoutInflater.inflate(R.layout.list_item, parent, false)
                holder = ViewHolder(
                    view.findViewById(R.id.titleTextView)!!,
                    view.findViewById(R.id.hrefTextView),
                    view.findViewById(R.id.timeTexiView)
                )
                view.tag = holder
            } else {
                holder = view.tag as ViewHolder
            }

            val news = getItem(position) as NewsData
            holder.titleTextView.text = news.title
            holder.hrefTextView.text = news.href
            holder.timeTextView.text = news.time

            return view!!

        }
    }

    data class NewsData(val title: String, val href: String, val time: String)
    data class ViewHolder(val titleTextView: TextView, val hrefTextView: TextView, val timeTextView: TextView)

    fun onParallelGetButtonClick() {
        runBlocking{
            val http = HttpUtil()
            //Mainスレッドでネットワーク関連処理を実行するとエラーになるためBackgroundで実行
            async(Dispatchers.Default) { http.httpGET1(URL) }.await().let  {
                //minimal-jsonを使って　jsonをパース
                val result = Json.parse(it).asObject()
//                val textView = findViewById(R.id.text) as TextView
                jarray = result.get("news").asArray()
                Log.d("TAG","ここだぜえええええええええええええ")
                Log.d("TAG",Integer.toString(jarray.size()))
//                textView.setText(result.get("pinpointLocations").asArray().get(0).asObject().get("name").asString())
            }
        }
    }
}
