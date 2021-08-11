package com.example.minecraft.favorites

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
import com.example.minecraft.adapter.FavRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_favorites.view.*

class FavoritesFragment : Fragment() {
    private lateinit var mModViewModel: ModViewModel
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)
        mModViewModel = ViewModelProvider(this).get(ModViewModel::class.java)

//        view.fragment_replace.setOnClickListener {7
//            val action = FavoritesFragmentDirections.actionFavoritesFragmentToMainFragment()
//            view.findNavController().navigate(action)
//        }

        val adapter = FavRecyclerAdapter(mModViewModel)
        recyclerView = view.fav_recycler_view
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)

        mModViewModel.readOnlyFavData.observe(viewLifecycleOwner, Observer { mod ->
            adapter.setData(mod)
        })

        return view
    }
}