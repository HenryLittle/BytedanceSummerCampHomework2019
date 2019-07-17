package com.example.day7

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import java.util.ArrayList

class ImagePagerAdapter() : PagerAdapter() {
    private var datas: List<View> = ArrayList()


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = datas[position]
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(datas[position])
    }

    fun setDatas(list: List<View>) {
        datas = list
        notifyDataSetChanged()
    }

    override fun getCount(): Int = datas.size
}