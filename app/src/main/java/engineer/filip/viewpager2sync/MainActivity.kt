package engineer.filip.viewpager2sync

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val icons = listOf(
        IconItem(R.drawable.ic_book, R.drawable.ic_book_blue, "1"),
        IconItem(R.drawable.ic_home, R.drawable.ic_home_blue, "2"),
        IconItem(R.drawable.ic_profile, R.drawable.ic_profile_blue, "3")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setMainViewPager()
        setIconsViewPager()
        syncViewPagers()
    }

    private fun setMainViewPager() {
        main_view_pager.apply {
            adapter = MainPagerAdapter(this@MainActivity)
            currentItem = 1
            offscreenPageLimit = 3
        }
    }

    private fun setIconsViewPager() {
        icons_view_pager.apply {
            adapter = MainIconsAdapter(icons)
            currentItem = 1
            offscreenPageLimit = 3
            setPageTransformer(MainIconsPageTransformer())
        }
    }

    private fun syncViewPagers() {
        main_view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            var lastPositionOffsetPixels = 0
            var lastPosition = -1
            var wasPageSettledDown = false

            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager2.SCROLL_STATE_DRAGGING -> {
                        icons_view_pager.beginFakeDrag()
                    }
                    ViewPager2.SCROLL_STATE_SETTLING -> {
                        // do nothing
                    }
                    ViewPager2.SCROLL_STATE_IDLE -> {
                        icons_view_pager.endFakeDrag()
                    }
                }
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (lastPosition == -1) {
                    lastPosition = position
                }

                var fakeDragByOffset = lastPositionOffsetPixels - positionOffsetPixels

                if (lastPosition != position) {
                    if (wasPageSettledDown) {
                        // page was settled down, user drags to left page, lastPositionOffset is 0
                        // offset results in -positionOffsetPixels which is almost ViewPager width
                        // fakeDragging by -positionOffsetPixels while on mid screen results
                        // in icons dragged to max left; aka 0th icon appears in the middle while on mid screen
                        // need to fakeDrag icons equal to the difference between ViewPager.width and positionOffsetPixels
                        // Logs/Video: https://gist.github.com/luchfilip/f8eb9d6bfe134558c1b736ec6944b7c7/edit
                        fakeDragByOffset = main_view_pager.width - positionOffsetPixels
                        wasPageSettledDown = false
                    } else {
                        // when on mid page, peek left page, then drag to right and peek right page
                        // result: first icon appears in the middle because lastPositionOffsetPixels
                        // is close to VP.width and current positionOffsetPixels  is quite close to 0
                        // which results in fakeDragging by almost VP.width which is equal to a page
                        // thus dragging to next page. We're in between changing pages so no need to fakeDrag
                        // Logs/Video: https://gist.github.com/luchfilip/98d7c06ecc72d82c08596946bac8e1a1
                        fakeDragByOffset = 0
                    }

                    lastPosition = position
                }

                Log.d(
                    "ViewPagerSync",
                    "onPageScrolled position: $position " +
                            "positionOffset: $positionOffset " +
                            "positionOffsetPixels: $positionOffsetPixels " +
                            "fakeDragBy: $fakeDragByOffset"
                )

                if (positionOffsetPixels == 0) {
                    // reset lastPositionOffsetPixels and skip dragging when page was settled down else
                    // page will be dragged by total width which means by a page.
                    //gist with logs/video: https://gist.github.com/luchfilip/03c9a36388337c3c6d808496d349faeb
                    lastPositionOffsetPixels = 0

                    // page is settling down by itself. We could use SCROLL_STATE_SETTLING event
                    // but that might happen at a slightly different moment which would break the fakeDrag
                    wasPageSettledDown = true
                    Log.d("ViewPagerSync", "resetting lastPositionOffsetPixels to 0")
                    return
                }

                if (!icons_view_pager.isFakeDragging) {
                    icons_view_pager.beginFakeDrag()
                }

                icons_view_pager.fakeDragBy(fakeDragByOffset.toFloat())

                lastPositionOffsetPixels = positionOffsetPixels
            }

            override fun onPageSelected(position: Int) {
                Log.d("ViewPagerSync", "onPageSelected: $position")
            }
        })
    }
}
