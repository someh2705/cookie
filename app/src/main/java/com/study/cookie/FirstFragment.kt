package com.study.cookie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.study.cookie.databinding.FragmentFirstBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FirstFragment : Fragment() {
    private lateinit var binding: FragmentFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstBinding.inflate(inflater, container, false)

        binding.textView.setOnClickListener {
            val directions =
                FirstFragmentDirections.actionFirstFragmentToSecondFragment(
                    message = "navigate",
                    something = 10
                )

            it.findNavController().navigate(directions)
        }

        return binding.root
    }
}