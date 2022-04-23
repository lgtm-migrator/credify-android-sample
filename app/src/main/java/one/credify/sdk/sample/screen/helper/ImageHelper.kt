package one.credify.sdk.sample.screen.helper

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Size
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import one.credify.sdk.sample.R

object ImageHelper {
    fun load(
        iv: ImageView,
        url: String,
        roundingRadius: Int,
        reset: Boolean = false,
        diskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.AUTOMATIC,
        imageHolder: Int = R.drawable.ic_place_holder_broken_image_32,
        isCircleCrop: Boolean = false,
        showLoading: Boolean = false
    ) {
        if (reset) {
            iv.setImageResource(android.R.color.transparent)
        }

        Glide
            .with(iv.context)
            .load(url)
            .transform(RoundedCorners(roundingRadius))
            .placeholder(ColorDrawable(Color.TRANSPARENT))
            .apply(
                initRequestOptions(
                    iv = iv,
                    diskCacheStrategy = diskCacheStrategy,
                    imageHolder = imageHolder,
                    isCircleCrop = isCircleCrop,
                    showLoading = showLoading
                )
            )
            .into(iv)
    }

    fun load(
        iv: ImageView,
        url: String,
        reset: Boolean = false,
        diskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.AUTOMATIC,
        imageHolder: Int = R.drawable.ic_place_holder_broken_image_32,
        isCircleCrop: Boolean = false,
        showLoading: Boolean = false
    ) {
        if (reset) {
            iv.setImageResource(android.R.color.transparent)
        }

        Glide
            .with(iv.context)
            .load(url)
            .placeholder(ColorDrawable(Color.TRANSPARENT))
            .apply(
                initRequestOptions(
                    iv = iv,
                    diskCacheStrategy = diskCacheStrategy,
                    imageHolder = imageHolder,
                    isCircleCrop = isCircleCrop,
                    showLoading = showLoading
                )
            )
            .into(iv)
    }

    fun load(iv: ImageView, @DrawableRes resId: Int, reset: Boolean = false) {
        if (reset) {
            iv.setImageResource(android.R.color.transparent)
        }

        Glide
            .with(iv.context)
            .load(resId)
            .placeholder(ColorDrawable(Color.TRANSPARENT))
            .into(iv)
    }

    fun load(tv: TextView, url: String, roundingRadius: Int, size: Size) {
        Glide
            .with(tv.context)
            .asBitmap()
            .load(url)
            .transform(RoundedCorners(roundingRadius))
            .apply(RequestOptions().override(size.width, size.height))
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    tv.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        BitmapDrawable(tv.resources, resource),
                        null,
                        null
                    )
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Do nothing
                }
            })
    }

    @SuppressLint("CheckResult")
    private fun initRequestOptions(
        iv: ImageView,
        diskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.AUTOMATIC,
        imageHolder: Int = R.drawable.ic_place_holder_broken_image_32,
        isCircleCrop: Boolean = false,
        showLoading: Boolean = true
    ): RequestOptions {
        return RequestOptions().apply {
            error(imageHolder)
            diskCacheStrategy(diskCacheStrategy)

            // https://github.com/hdodenhof/CircleImageView/blob/master/README.md
            if (!showLoading) {
                dontAnimate()
            } else {
                placeholder(
                    CircularProgressDrawable(iv.context).apply {
                        strokeWidth = 5f
                        centerRadius = 30f
                        setColorSchemeColors(Color.parseColor("#470075"))
                        start()
                    }
                )
            }

            if (isCircleCrop) {
                circleCrop()
            }
        }
    }
}