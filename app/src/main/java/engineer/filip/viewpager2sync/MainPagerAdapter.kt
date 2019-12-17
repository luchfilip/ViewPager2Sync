package engineer.filip.viewpager2sync

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return BookFragment()
            1 -> return HomeFragment()
            2 -> return ProfileFragment()
        }
        return HomeFragment()
    }
}