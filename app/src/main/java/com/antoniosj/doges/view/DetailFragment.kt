package com.antoniosj.doges.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation

import com.antoniosj.doges.R
import com.antoniosj.doges.databinding.FragmentDetailBinding
import com.antoniosj.doges.util.getProgressDrawable
import com.antoniosj.doges.util.loadImage
import com.antoniosj.doges.util.viewModelFactory
import com.antoniosj.doges.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_list.*

class DetailFragment : Fragment() {

    var dogUuid = 0
    private lateinit var dataBinding: FragmentDetailBinding

    private val viewModel: DetailViewModel by viewModels {
        viewModelFactory { DetailViewModel(requireActivity().application) }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_detail, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            dogUuid = DetailFragmentArgs.fromBundle(it).dogUuid
        }
        viewModel.fetch(dogUuid)
        viewModel.dogBreed.observe(viewLifecycleOwner, Observer { dog ->
            dog?.let {
//                tv_dogNameDetail.text = dog.dogBreed
//                tv_dogLifespanDetail.text = dog.lifeSpan
//                tv_dogPurposeDetail.text = dog.bredFor
//                tv_dogTemperamentDetail.text = dog.temperament
//                context?.let {
//                    iv_dogDetail.loadImage(dog.imageUrl, getProgressDrawable(it))
//                }
                dataBinding.dog = dog
            }
        })


    }

}
