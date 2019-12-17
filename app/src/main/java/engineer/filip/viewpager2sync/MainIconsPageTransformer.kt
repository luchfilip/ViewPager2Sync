package engineer.filip.viewpager2sync

import android.view.View
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.absoluteValue

class MainIconsPageTransformer : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        val absPos = abs(position)

        val viewPager = page.parent.parent as ViewPager2

        page.apply {
            translationY = absPos * -50f
            scaleY = (0.7f - 1f) * absPos + 1f
            scaleX = (0.7f - 1f) * absPos + 1f
            alpha = (0.5f - 1f) * absPos + 1f

            //Timber.d("scaleY: $scaleY scaleX: $scaleX Alpha: $alpha translationY: $translationY postion: $position currentItem: ${viewPager.currentItem}")

            // compute alpha for default(white) icon; color icon would be the opposite
            val colorRatio = (alpha - 0.5f).absoluteValue / 0.5f

            val icon = findViewById<ImageView>(R.id.icon)
            val colorIcon = findViewById<ImageView>(R.id.blue_icon)

            when (icon.tag) {
                // for first and last page icons haven to transition from white to color
                "1", "3" -> {
                    icon.alpha = 1-colorRatio
                    colorIcon.alpha = colorRatio
                }
                // mid page; transition icons from blue to white
                "2" -> {
                    icon.alpha = colorRatio
                    colorIcon.alpha = 1-colorRatio
                }
            }
        }

        // makes all 3 icons visible in mid page
        val pageMarginPx = page.resources.getDimensionPixelOffset(R.dimen.pageMargin)
        val offsetPx = page.resources.getDimensionPixelOffset(R.dimen.offset)

        val offset = position * -(2 * offsetPx + pageMarginPx)
        if (viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
            if (ViewCompat.getLayoutDirection(viewPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                page.translationX = -offset
            } else {
                page.translationX = offset
            }
        } else {
            page.translationY = offset
        }
    }
}