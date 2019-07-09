package com.example.myapplication

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlin.random.Random


class TopListFragment : Fragment() {

    private val list: ArrayList<HotEvent> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        list.add(HotEvent("Ant Man", Random.nextDouble(500.0)))
        list.add(HotEvent("Spider Man", Random.nextDouble(500.0)))
        list.add(HotEvent("Iron Man", Random.nextDouble(500.0)))
        list.add(HotEvent("Mysterio", Random.nextDouble(500.0)))
        list.add(HotEvent("Captian America", Random.nextDouble(500.0)))
        list.add(HotEvent("Thanos", Random.nextDouble(500.0)))
        list.add(HotEvent("Nebula", Random.nextDouble(500.0)))
        list.add(HotEvent("Camora", Random.nextDouble(500.0)))
        list.add(HotEvent("Star Lord", Random.nextDouble(500.0)))
        list.add(HotEvent("Black Widow", Random.nextDouble(500.0)))
        list.add(HotEvent("Hulk", Random.nextDouble(500.0)))
        list.add(HotEvent("Thor", Random.nextDouble(500.0)))
        list.add(HotEvent("Odin", Random.nextDouble(500.0)))
        list.add(HotEvent("Dr. Strange", Random.nextDouble(500.0)))
        list.add(HotEvent("Rocket", Random.nextDouble(500.0)))
        list.add(HotEvent("Groot", Random.nextDouble(500.0)))
        list.add(HotEvent("Drax", Random.nextDouble(500.0)))
        list.add(HotEvent("Happy", Random.nextDouble(500.0)))
        list.add(HotEvent("Batman", Random.nextDouble(500.0)))
        list.add(HotEvent("Superman", Random.nextDouble(500.0)))
        list.add(HotEvent("Aquaman", Random.nextDouble(500.0)))

        list.sortByDescending { it.hotVal }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_top_rank, container, false)
        view.findViewById<RecyclerView>(R.id.fragment_top_rank_recycler_view)?.apply {
            layoutManager = LinearLayoutManager(activity?.baseContext)
            adapter = TopAdapter(list)
        }

        return view
    }

}
