package com.antoniosj.doges.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antoniosj.doges.model.DogBreed
import com.antoniosj.doges.model.DogDatabase
import kotlinx.coroutines.launch

class DetailViewModel(val app: Application): ViewModel() {

    var dogBreed = MutableLiveData<DogBreed>()

    fun fetch(uuid: Int) {
        viewModelScope.launch {
            val dog = DogDatabase(app).dogDao().getDog(uuid)
            dogBreed.value = dog
        }
    }
}