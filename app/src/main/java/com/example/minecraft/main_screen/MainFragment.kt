package com.example.minecraft.main_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minecraft.ModViewModel
import com.example.minecraft.R
import com.example.minecraft.adapter.RecyclerAdapter
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainFragment : Fragment()  {
    private lateinit var mModViewModel: ModViewModel
    private lateinit var recycler_view: RecyclerView

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        mModViewModel = ViewModelProvider(this).get(ModViewModel::class.java)

        val adapter = RecyclerAdapter(mModViewModel)
        recycler_view = view.recycler_view
        recycler_view.adapter = adapter
        recycler_view.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)

        // ModViewModel
        mModViewModel.readAllData.observe(viewLifecycleOwner, Observer { mod ->
            adapter.setData(mod)
        })

        return view
    }
}