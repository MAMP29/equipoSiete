package com.appmovil.inventorywidget.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.appmovil.inventorywidget.R
import com.appmovil.inventorywidget.databinding.FragmentInventoryBinding
import com.appmovil.inventorywidget.view.adapter.ProductAdapter
import com.appmovil.inventorywidget.viewmodel.ProductUiState
import com.appmovil.inventorywidget.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InventoryFragment : Fragment() {

    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!
    private val productViewModel: ProductViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        binding.fbagregar.setOnClickListener {
            findNavController().navigate(R.id.action_inventoryFragment_to_addProductFragment)
        }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { selectedProduct ->
            val action = InventoryFragmentDirections
                .actionInventoryFragmentToDetailProductFragment(selectedProduct)
            findNavController().navigate(action)
        }

        binding.recyclerview.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }


    private fun observeViewModel() {
        productViewModel.productsUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ProductUiState.Loading -> {
                    // Estado de Carga
                    binding.progress.visibility = View.VISIBLE
                    binding.recyclerview.visibility = View.GONE
                }
                is ProductUiState.Success -> {
                    // Estado de Éxito
                    binding.progress.visibility = View.GONE
                    binding.recyclerview.visibility = View.VISIBLE

                    // Envía la lista de productos al adaptador
                    productAdapter.submitList(state.products)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}