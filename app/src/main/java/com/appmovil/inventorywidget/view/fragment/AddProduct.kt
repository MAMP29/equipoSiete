package com.appmovil.inventorywidget.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.appmovil.inventorywidget.R
import com.appmovil.inventorywidget.databinding.FragmentAddProductBinding
import com.appmovil.inventorywidget.model.Product
import com.appmovil.inventorywidget.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.log


/**
 * Fragment para  añadir un producto
 */
@AndroidEntryPoint
class AddProduct : Fragment() {

    private val productViewModel: ProductViewModel by viewModels()
    // Manejo explicito del biding, para evitar problemas con null
    // https://developer.android.com/topic/libraries/view-binding?hl=es-419#fragments
    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate convierte el XML a vistas reales, biding hace el inflate de forma automatica
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)

        // Vista raiz
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inyectamos el texto al toolbar, esto es logica de la GUI, este texto no cambia
        // según el modelo
        binding.toolbar.tvToolbarTitle.text = getString(R.string.agregar_producto)
        binding.toolbar.toolbarRoot.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        setupListeners()
        observeProducts()
    }

    // Limpiando la referencia del binding
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupListeners() {
        validateData()

        binding.btnSave.setOnClickListener {
            saveProduct()
        }
    }

    private fun validateData() {
        val listEditText = listOf(
            binding.etProductCode,
            binding.etProductName,
            binding.etproductPrice,
            binding.etProductQuantity
        )

        for (editText in listEditText) {
            editText.addTextChangedListener {
                val isFullList = listEditText.all {
                    it.text?.isNotEmpty() == true
                }
                binding.btnSave.isEnabled = isFullList
            }
        }
    }

    private fun saveProduct() {
        try {
            val code = binding.etProductCode.text.toString().toInt()
            val name = binding.etProductName.text.toString()
            val price = binding.etproductPrice.text.toString().toInt()
            val quantity = binding.etProductQuantity.text.toString().toInt()
            val product = Product(code = code, name = name, price = price, quantity = quantity)
            Log.d("test", product.toString())
            binding.btnSave.isEnabled = false
            productViewModel.save(product)
            Toast.makeText(context, "Artículo guardado !!", Toast.LENGTH_SHORT).show()
            clearFields()
            findNavController().popBackStack()
        } catch (e: NumberFormatException) {
            Toast.makeText(
                requireContext(),
                "Error: Verifica que los números sean válidos",
                Toast.LENGTH_SHORT
            ).show()
            binding.btnSave.isEnabled = true
        }
    }

    private fun observeProducts() {
        productViewModel.allProducts.observe(viewLifecycleOwner) { products ->
            Log.d("AddProduct", "Total de productos: ${products.size}")
        }
    }

    private fun clearFields() {
        binding.etProductCode.text?.clear()
        binding.etProductName.text?.clear()
        binding.etproductPrice.text?.clear()
        binding.etProductQuantity.text?.clear()

        // Opcional: poner el foco en el primer campo
        binding.etProductCode.requestFocus()
    }


}