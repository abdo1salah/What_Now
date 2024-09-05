package com.example.whatnow

import com.google.gson.annotations.SerializedName

data class News(val articles:ArrayList<Article>)

data class Article(val title:String,
                   val url:String,
                   @SerializedName("urlToImage")
                   val image:String,
                   val source:Source ,var isChecked:Boolean = false)


data class Source(val name:String)