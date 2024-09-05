package com.example.whatnow


import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.whatnow.databinding.ListItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class NewsAdapter(
    val a: Activity,
    val articles: ArrayList<Article>,
    val country: String,
    val currentUser: FirebaseAuth,
) : Adapter<NewsAdapter.NewsViewHolder>() {
    val db = Firebase.firestore

    class NewsViewHolder(val binding: ListItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val b = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(b)
    }

    override fun getItemCount() = articles.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        if (country == "eg")
            holder.binding.titleTv.textDirection = 2
        holder.binding.titleTv.text = articles[position].title
        holder.binding.sourceTv.text = articles[position].source.name
        Glide
            .with(holder.binding.articleImage.context)
            .load(articles[position].image)
            .error(R.drawable.broken_image)
            .transition(DrawableTransitionOptions.withCrossFade(1000))
            .into(holder.binding.articleImage)
        val url = articles[position].url
        holder.binding.articleContainer.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, url.toUri())
            a.startActivity(i)
        }
        holder.binding.shareFab.setOnClickListener {
            ShareCompat
                .IntentBuilder(a)
                .setType("text/plain")
                .setChooserTitle("Share article with: ")
                .setText(url)
                .startChooser()
        }
        holder.binding.favouriteFab.setOnClickListener {
            if (!articles[position].isChecked) {
                db.collection(currentUser.uid.toString()).document(articles[position].title)
                    .set(articles[position])
                    .addOnSuccessListener {
                        holder.binding.favouriteFab.setImageDrawable(ContextCompat.getDrawable(a, R.drawable.baseline_favorite_turnedon))
                        Toast.makeText(a, "Added to your favorites", Toast.LENGTH_SHORT).show()
                        articles[position].isChecked = true
                    }
            } else {
                db.collection(currentUser.uid.toString()).document(articles[position].title).delete()
                    .addOnSuccessListener {
                        holder.binding.favouriteFab.setImageDrawable(ContextCompat.getDrawable(a, R.drawable.baseline_favorite_turnedoff))
                        holder.binding.favouriteFab.setImageResource(R.drawable.baseline_favorite_turnedoff)
                        Toast.makeText(a, "Removed from your favorites", Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }
}
