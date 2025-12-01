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
        observeAuthState()

        // Quitar foco y teclado al tocar fuera
        binding.root.setOnClickListener {
            binding.edtEmail.clearFocus()
            binding.edtPassword.clearFocus()
            hideKeyboard()
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun setupListeners() {

        // EMAIL
        binding.edtEmail.doOnTextChanged { text, _, _, _ ->
            viewModel.onEmailChange(text.toString())
            updateLoginButtonState()
        }

        // PASSWORD (validación en tiempo real)
        binding.edtPassword.doOnTextChanged { text, _, _, _ ->
            val pwd = text.toString()
            viewModel.onPasswordChange(pwd)

            val isValid = pwd.length >= 6

            // Mensaje error
            binding.txtPasswordError.visibility = if (!isValid) View.VISIBLE else View.GONE

            // Borde rojo o blanco
            val color = if (isValid) R.color.white else android.R.color.holo_red_light
            binding.inputPassword.boxStrokeColor = ContextCompat.getColor(requireContext(), color)

            updateLoginButtonState()
        }

        // Fuerza teclado numérico
        binding.edtPassword.inputType = InputType.TYPE_CLASS_NUMBER
        binding.edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()

        // BOTÓN LOGIN
        binding.btnLogin.setOnClickListener {
            viewModel.login(
                email = binding.edtEmail.text.toString(),
                pass = binding.edtPassword.text.toString()
            )
        }

        // BOTÓN REGISTRARSE
        binding.btnGoRegister.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val pass = binding.edtPassword.text.toString()

            if (email.isBlank() || pass.isBlank()) {
                Toast.makeText(requireContext(), "Email y contraseña son obligatorios.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.register(email, pass)
        }

        // OJO VER CONTRASEÑA
        binding.inputPassword.setStartIconOnClickListener {

            viewModel.togglePasswordVisibility()
            val visible = viewModel.passwordVisible.value

            if (visible) {
                binding.edtPassword.transformationMethod = null
            } else {
                binding.edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }

            binding.edtPassword.setSelection(binding.edtPassword.text?.length ?: 0)

            val iconRes = if (visible) R.drawable.ic_eye_open else R.drawable.ic_eye_close
            binding.inputPassword.startIconDrawable = ContextCompat.getDrawable(requireContext(), iconRes)
        }
    }

    // CAMBIOS DE COLORES SEGÚN ESTADO DE VALIDACIÓN
    private fun updateLoginButtonState() {
        val emailFilled = binding.edtEmail.text?.isNotEmpty() == true
        val passwordValid = binding.edtPassword.text?.length ?: 0 >= 6
        val canEnable = emailFilled && passwordValid

        // LOGIN
        binding.btnLogin.isEnabled = canEnable
        if (canEnable) {
            binding.btnLogin.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.shinywhite))
            binding.btnLogin.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
        } else {
            binding.btnLogin.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange))
            binding.btnLogin.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        }

        // REGISTRARSE
        binding.btnGoRegister.isEnabled = canEnable
        if (canEnable) {

            binding.btnGoRegister.setTextColor(ContextCompat.getColor(requireContext(), R.color.shinywhite))
        } else {
            binding.btnGoRegister.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        }
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            viewModel.authState.collect { state ->

                when (state) {
                    is AuthState.Loading -> {
                        binding.btnLogin.isEnabled = false
                    }

                    is AuthState.Error -> {
                        Toast.makeText(requireContext(), "Login incorrecto", Toast.LENGTH_LONG).show()
                    }

                    is AuthState.Success -> {
                        findNavController().navigate(
                            R.id.action_loginFragment_to_inventoryFragment
                        )
                    }

                    else -> Unit
                }
            }
        }
    }
}





