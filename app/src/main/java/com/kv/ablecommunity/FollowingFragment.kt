package com.kv.ablecommunity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.kv.ablecommunity.adapter.FollowersAdapter
import com.kv.ablecommunity.databinding.FragmentFollowingBinding
import com.kv.ablecommunity.firebase.FirestoreClass
import com.kv.ablecommunity.models.User


class FollowingFragment : Fragment() {

    private lateinit var binding : FragmentFollowingBinding
    private val following = ArrayList<User>()
    private lateinit var currentUser : User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentFollowingBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        currentUser = (activity as FollowersActivity).currentUser
        FirestoreClass().getfollowings(this,currentUser,true)

        return binding.root
    }
    fun onGettingFollowingSuccess(followers : ArrayList<User>){
        following.clear()
        following.addAll(followers)
        val adapter = FollowersAdapter(requireContext(),following)
        binding.rvFollowing.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFollowing.adapter = adapter

    }

}