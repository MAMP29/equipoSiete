package com.appmovil.inventorywidget.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appmovil.inventorywidget.R
import com.appmovil.inventorywidget.databinding.FragmentAddProductBinding


/**
 * Fragment para  añadir un producto
 */
class addProduct : Fragment() {

    // Manejo explicito del biding, para mamenjo contra null
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
    }

    // Limpiando la referencia del binding
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}