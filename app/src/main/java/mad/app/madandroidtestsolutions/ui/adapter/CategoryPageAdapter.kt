package mad.app.madandroidtestsolutions.ui.adapter

import android.content.Context
import androidx.fragment.app.Fragment

class CategoryPageAdapter() {

    private var context: Context? = null
    private val pagerFragments: MutableList<Fragment> = ArrayList()
    private val pagerTitles: MutableList<String> = ArrayList()

    fun addTab(fragment: Fragment, title: String) {
        pagerFragments.add(fragment)
        pagerTitles.add(title)
    }

    fun getItem(position: Int): Fragment? {
        return pagerFragments[position]
    }

    fun getPageTitle(position: Int): CharSequence? {
        return pagerTitles[position]
    }

    fun getCount(): Int {
        return pagerFragments.size
    }

}