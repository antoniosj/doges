package com.antoniosj.doges.view

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.telephony.SmsManager
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.palette.graphics.Palette

import com.antoniosj.doges.R
import com.antoniosj.doges.databinding.FragmentDetailBinding
import com.antoniosj.doges.databinding.SendSmsDialogBinding
import com.antoniosj.doges.model.DogBreed
import com.antoniosj.doges.model.DogPalette
import com.antoniosj.doges.model.SmsInfo
import com.antoniosj.doges.util.getProgressDrawable
import com.antoniosj.doges.util.loadImage
import com.antoniosj.doges.util.viewModelFactory
import com.antoniosj.doges.viewmodel.DetailViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_list.*

class DetailFragment : Fragment() {

    var dogUuid = 0
    private lateinit var dataBinding: FragmentDetailBinding
    private var sendSmsStarted = false
    private var currentDog: DogBreed? = null

    private val viewModel: DetailViewModel by viewModels {
        viewModelFactory { DetailViewModel(requireActivity().application) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
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

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.dogBreed.observe(viewLifecycleOwner, Observer { dog ->
            currentDog = dog
            dog?.let {
//                tv_dogNameDetail.text = dog.dogBreed
//                tv_dogLifespanDetail.text = dog.lifeSpan
//                tv_dogPurposeDetail.text = dog.bredFor
//                tv_dogTemperamentDetail.text = dog.temperament
//                context?.let {
//                    iv_dogDetail.loadImage(dog.imageUrl, getProgressDrawable(it))
//                }
                dataBinding.dog = dog
                it.imageUrl?.let { url ->
                    setupBackgroundColor(url)
                }
            }
        })
    }

    private fun setupBackgroundColor(url: String) {
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource)
                        .generate { palette ->
                            val intColor = palette?.vibrantSwatch?.rgb ?: 0
                            val myPalette = DogPalette(intColor)
                            dataBinding.palette = myPalette
                        }
                }

            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_send_sms -> {
                sendSmsStarted = true
                (activity as MainActivity).checkSmsPermission()
            }
            R.id.action_share -> {
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "check out this dog breed")
                    putExtra(Intent.EXTRA_TEXT, "${currentDog?.dogBreed} bred for ${currentDog?.bredFor}")
                    putExtra(Intent.EXTRA_STREAM, currentDog?.imageUrl) // send image via stream
                    // user choose what app from list of whose can handle itgi
                    startActivity(Intent.createChooser(this, "Share With"))
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // PERMISSION cont
    fun onPermissionResult(permissionGranted: Boolean) {
        if (sendSmsStarted && permissionGranted) {
            context?.let {
                val smsInfo = SmsInfo(
                    "",
                    "${currentDog?.dogBreed} bred for ${currentDog?.bredFor}",
                    currentDog?.imageUrl
                )

                val dialogBinding = DataBindingUtil.inflate<SendSmsDialogBinding>(
                    LayoutInflater.from(it), R.layout.send_sms_dialog, null, false
                )

                AlertDialog.Builder(it)
                    .setView(dialogBinding.root)
                    .setPositiveButton("send SMS") { dialog, which ->
                        if (!dialogBinding.etSmsDestination.text.isNullOrEmpty()) {
                            smsInfo.to = dialogBinding.etSmsDestination.text.toString()
                            sendSms(smsInfo)
                        }
                    }
                    .setNegativeButton("Cancel") { dialog, which ->

                    }
                    .show()

                dialogBinding.smsInfo = smsInfo
            }
        }
    }

    private fun sendSms(smsInfo: SmsInfo) {
        val intent = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivity(context, 0, intent, 0)
        val sms = SmsManager.getDefault()
        sms.sendTextMessage(smsInfo.to, null, smsInfo.text, pi, null)
    }
    // permission end


}
