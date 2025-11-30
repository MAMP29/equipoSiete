package com.appmovil.inventorywidget.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    }

    private fun setupListeners() {

        // EMAIL CHANGES
        binding.edtEmail.doOnTextChanged { text, _, _, _ ->
            viewModel.onEmailChange(text.toString())
        }

        // PASSWORD CHANGES
        binding.edtPassword.doOnTextChanged { text, _, _, _ ->
            viewModel.onPasswordChange(text.toString())
        }

        // LOGIN
        binding.btnLogin.setOnClickListener {
            viewModel.login(
                email = binding.edtEmail.text.toString(),
                pass = binding.edtPassword.text.toString()
            )
        }

        // REGISTRARSE
        binding.btnGoRegister.setOnClickListener {
            viewModel.register(
                email = binding.edtEmail.text.toString(),
                pass = binding.edtPassword.text.toString()
            )
        }

        // OJO DE PASSWORD
        binding.inputPassword.setStartIconOnClickListener {
            viewModel.togglePasswordVisibility()
        }
    }

    private fun observeAuthState() {

        lifecycleScope.launch {
            viewModel.authState.collect { state ->

                when (state) {
                    is AuthState.Loading -> {
                        binding.btnLogin.isEnabled = false
                        binding.btnLogin.alpha = 0.4f
                    }

                    is AuthState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }

                    is AuthState.Success -> {
                        findNavController().navigate(
                            R.id.action_loginFragment_to_inventoryFragment
                        )
                    }

                    else -> Unit
                }

                // Mostrar u ocultar error de password
                binding.txtPasswordError.visibility =
                    if (viewModel.isPasswordValid()) View.GONE else View.VISIBLE

                // Habilitar login
                val canLogin = viewModel.canLogin()
                binding.btnLogin.isEnabled = canLogin
                binding.btnLogin.alpha = if (canLogin) 1f else 0.4f

                // Cambiar el ícono del ojo
                val icon = if (viewModel.passwordVisible.value)
                    R.drawable.ic_eye_open
                else
                    R.drawable.ic_eye_close

                binding.inputPassword.startIconDrawable =
                    ContextCompat.getDrawable(requireContext(), icon)
            }
        }
    }
}
