package com.appmovil.inventorywidget.view.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.appmovil.inventorywidget.R
import com.appmovil.inventorywidget.databinding.FragmentDetailProductBinding
import com.appmovil.inventorywidget.model.Product
import com.appmovil.inventorywidget.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailProductFragment : Fragment() {

    private var _binding: FragmentDetailProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductViewModel by viewModels()
    private val args: DetailProductFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailProductBinding.inflate(inflater, container, false)

        val product = args.product
        setupUI(product)

        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar producto")
                .setMessage("¿Deseas eliminar este producto?")
                .setPositiveButton("Sí") { _, _ ->
                    viewModel.delete(product)
                    Toast.makeText(requireContext(), "Producto eliminado ✅", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_detailProductFragment_to_inventoryFragment)
                }
                .setNegativeButton("No", null)
                .show()
        }

        binding.fabEdit.setOnClickListener {
            val action = DetailProductFragmentDirections
                .actionDetailProductFragmentToAddProduct(product)
            findNavController().navigate(action)
        }

        return binding.root
    }

    private fun setupUI(product: Product) {
        binding.apply {
            txtName.text = product.name
            txtPrice.text = "Precio: $${product.price}"
            txtQuantity.text = "Cantidad: ${product.quantity}"
            txtTotal.text = "Total: $${"%.2f".format(product.price * product.quantity)}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
