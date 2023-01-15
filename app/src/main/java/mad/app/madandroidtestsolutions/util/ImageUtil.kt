package mad.app.madandroidtestsolutions.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import mad.app.madandroidtestsolutions.R

fun ImageView.downloadImage(imageUrl: String?) {
    val options: RequestOptions =
        RequestOptions().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.ic_place_holder).error(R.drawable.ic_baseline_error)
    Glide.with(this).load(imageUrl).apply(options).into(this)
}