package com.antoniosj.doges.view


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager

import com.antoniosj.doges.R
import com.antoniosj.doges.util.viewModelFactory
import com.antoniosj.doges.viewmodel.ListViewModel
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment() {

    private val viewModel: ListViewModel by viewModels {
        viewModelFactory { ListViewModel(requireActivity().application) }
    }
    private val dogsListAdapter = DogListAdapter(arrayListOf())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // quando for criada, pega do db/remote
        setHasOptionsMenu(true)
        viewModel.refresh()

        rv_dogList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dogsListAdapter
        }

        swipeLayout.setOnRefreshListener {
            rv_dogList.visibility = View.GONE
            tv_listError.visibility = View.GONE
            pb_loadingView.visibility = View.VISIBLE
            // se der refresh, pega do remote
            viewModel.refreshBypassCache()
            swipeLayout.isRefreshing = false
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.dogs.observe(viewLifecycleOwner, Observer { dogs ->
            dogs?.let {
                rv_dogList.visibility = View.VISIBLE
                dogsListAdapter.updateDogList(dogs)
            }
        })

        viewModel.dogsLoadError.observe(viewLifecycleOwner, Observer { isError ->
            isError?.let {
                tv_listError.visibility = if (it) View.VISIBLE else View.GONE
            }
        })

        viewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            isLoading?.let {
                pb_loadingView.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    tv_listError.visibility = View.GONE
                    rv_dogList.visibility = View.GONE
                }
            }
        })
    }

    /*
    1. criar o fragment (blank)
    2. extends de PreferenceFragmentCompat()
    3. apagar o xml do fragment e criar meu proprio na pasta xml
    4. criar 1 menu
    5. esses 2 overrides a baixo
    6. colocar meu fragment no nav_graph
    7. criar 1 funcao que retorne o valor que tá definido como key na preferences.xml e pegar do sharedprefs *funciona*
    8. atualiza o valor na funcao checkCacheDuration()
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuActionSettings -> {
                view?.let {
                    Navigation.findNavController(it).navigate(ListFragmentDirections.actionToSettings())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
