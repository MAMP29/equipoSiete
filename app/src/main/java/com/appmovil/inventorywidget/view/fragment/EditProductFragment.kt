package com.appmovil.inventorywidget.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.appmovil.inventorywidget.R
import com.appmovil.inventorywidget.databinding.FragmentEditProductBinding
import com.appmovil.inventorywidget.model.Product
import com.appmovil.inventorywidget.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProductFragment : Fragment() {

    private var _binding: FragmentEditProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductViewModel by viewModels()
    private val args: EditProductFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.tvToolbarTitle.text = getString(R.string.editar_producto)
        binding.toolbar.toolbarRoot.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        val product = args.product
        setupFields(product)
        setupFormListener()
        binding.btnEditProduct.setOnClickListener {
            updateAction(product)
        }

    }

    private fun updateAction(product: Product) {
        // Log.d("Update","Entrando a funcion de actualizar")
        val name = binding.etProductNameEditProduct.text.toString().trim()
        val priceStr = binding.etProductPriceEditProduct.text.toString().trim().toInt()
        val quantityStr = binding.etProductQuantityEditProduct.text.toString().trim().toInt()

        try {
            val price = priceStr.toInt()
            val quantity = quantityStr.toInt()
            val updatedProduct = product.copy(
                name = name,
                price = price,
                quantity = quantity
            )
            viewModel.update(updatedProduct)
            // Log.d("Update","Accion de actualizar ejecutada")
            Toast.makeText(requireContext(), "Producto actualizado ✅", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_global_inventoryFragment)
        } catch (e: NumberFormatException) {
            Toast.makeText(
                requireContext(),
                "Error: Verifica que los números sean válidos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupFields(product: Product) {
        binding.apply {
            tvProductId.text = String.format(
                resources.getString(R.string.id_producto),
                product.code.toString()
            )
            etProductNameEditProduct.setText(product.name)
            etProductPriceEditProduct.setText(product.price.toString())
            etProductQuantityEditProduct.setText(product.quantity.toString())
        }
    }

    private fun setupFormListener() {
        val fields = listOf(
            binding.etProductNameEditProduct,
            binding.etProductPriceEditProduct,
            binding.etProductQuantityEditProduct
        )

        val button = binding.btnEditProduct

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val isValid = fields.all { it.text.toString().isNotEmpty() }
                button.isEnabled = isValid
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        fields.forEach { it.addTextChangedListener(watcher) }

        // Forzar comprobacion inicial
        button.isEnabled = fields.all { it.text.toString().trim().isNotEmpty() }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}