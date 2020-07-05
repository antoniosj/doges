package com.antoniosj.doges.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antoniosj.doges.model.DogBreed

class ListViewModel: ViewModel() {

    val dogs = MutableLiveData<List<DogBreed>>()
    val dogsLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        val dog1 = DogBreed("1", "labrador", "10 years",
            "breedGroup", "bredFor", "temperament", "")
        val dog2 = DogBreed("2", "poodle", "12 years",
            "breedGroup", "bredFor", "temperament", "")
        val dog3 = DogBreed("3", "dalmata", "101 years",
            "breedGroup", "bredFor", "temperament", "")
        val dogList = arrayListOf<DogBreed>(dog1, dog2, dog3)

        dogs.value = dogList
        dogsLoadError.value = false
        loading.value = false
    }
}