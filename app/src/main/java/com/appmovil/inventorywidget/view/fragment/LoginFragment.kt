package com.appmovil.inventorywidget.view.fragment

import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.appmovil.inventorywidget.R
import com.appmovil.inventorywidget.databinding.FragmentLoginBinding
import com.appmovil.inventorywidget.viewmodel.AuthState
import com.appmovil.inventorywidget.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()

        // Ocultar teclado
        binding.root.setOnClickListener {
            hideKeyboard()
            binding.edtEmail.clearFocus()
            binding.edtPassword.clearFocus()
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun setupListeners() {

        // ⭐ EMAIL
        binding.edtEmail.doOnTextChanged { text, _, _, _ ->
            viewModel.onEmailChange(text.toString())
        }

        // ⭐ PASSWORD + VALIDACIÓN VISUAL
        binding.edtPassword.doOnTextChanged { text, _, _, _ ->
            val pwd = text.toString()
            viewModel.onPasswordChange(pwd)

            val isValid = pwd.length >= 6

            binding.txtPasswordError.visibility = if (isValid) View.GONE else View.VISIBLE

            val borderColor = if (isValid) R.color.white else android.R.color.holo_red_light
            binding.inputPassword.boxStrokeColor =
                ContextCompat.getColor(requireContext(), borderColor)
        }

        // ⭐ Teclado numérico
        binding.edtPassword.inputType = InputType.TYPE_CLASS_NUMBER
        binding.edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()

        // ⭐ LOGIN
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            viewModel.login(email, password)
        }

        // ⭐ REGISTRO
        binding.btnGoRegister.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val pass = binding.edtPassword.text.toString()

            if (email.isBlank() || pass.isBlank()) {
                Toast.makeText(requireContext(), "Email y contraseña son obligatorios.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.register(email, pass)
        }

        // ⭐ Mostrar / Ocultar contraseña
        binding.inputPassword.setStartIconOnClickListener {
            viewModel.togglePasswordVisibility()

            lifecycleScope.launch {
                val visible = viewModel.passwordVisible.value

                binding.edtPassword.transformationMethod =
                    if (visible) null else PasswordTransformationMethod.getInstance()

                binding.edtPassword.setSelection(binding.edtPassword.text?.length ?: 0)

                val iconRes = if (visible) R.drawable.ic_eye_open else R.drawable.ic_eye_close
                binding.inputPassword.startIconDrawable =
                    ContextCompat.getDrawable(requireContext(), iconRes)
            }
        }
    }

    // -------------------------------
    //   OBSERVAR STATEFLOW CORRECTO
    // -------------------------------
    private fun observeViewModel() {

        // ⭐ FORM VALIDATION
        lifecycleScope.launch {
            viewModel.isFormValid.collect { valid ->
                updateButtonStates(valid)
            }
        }

        // ⭐ AUTH STATE
        lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {

                    AuthState.Idle -> {
                        binding.btnLogin.isEnabled = true
                    }

                    AuthState.Loading -> {
                        binding.btnLogin.isEnabled = false
                    }

                    is AuthState.Success -> {
                        binding.btnLogin.isEnabled = true
                        findNavController().navigate(R.id.action_loginFragment_to_inventoryFragment)
                    }

                    is AuthState.Error -> {
                        binding.btnLogin.isEnabled = true

                        val message = if (state.isRegisterError)
                            "Error en el registro"
                        else
                            "Login incorrecto"

                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    }

                }
            }
        }
    }

    // -------------------------------
    //       ESTADOS DE BOTONES
    // -------------------------------
    private fun updateButtonStates(canEnable: Boolean) {

        // LOGIN
        binding.btnLogin.isEnabled = canEnable
        if (canEnable) {
            binding.btnLogin.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.shinywhite)
            )
            binding.btnLogin.setTextColor(
                ContextCompat.getColor(requireContext(), android.R.color.black)
            )
            binding.btnLogin.alpha = 1f
        } else {
            binding.btnLogin.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.orange)
            )
            binding.btnLogin.setTextColor(
                ContextCompat.getColor(requireContext(), android.R.color.white)
            )
            binding.btnLogin.alpha = 0.4f
        }

        // REGISTRO
        binding.btnGoRegister.isEnabled = canEnable
        binding.btnGoRegister.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (canEnable) R.color.shinywhite else android.R.color.darker_gray
            )
        )
    }
}











