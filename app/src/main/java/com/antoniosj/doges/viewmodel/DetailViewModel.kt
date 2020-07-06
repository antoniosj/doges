package com.antoniosj.doges.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antoniosj.doges.model.DogBreed

class DetailViewModel: ViewModel() {

    var dogBreed = MutableLiveData<DogBreed>()

    fun fetch() {
        val dog1 = DogBreed("1", "labrador", "10 years",
            "breedGroup", "bredFor", "temperament", "")
        dogBreed.value = dog1
    }
}