package com.study.cookie.home

import android.os.Bundle
import android.view.View
import com.study.cookie.core.base.BaseFragment
import com.study.cookie.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.d("HomeFragment onViewCreated")
    }
}