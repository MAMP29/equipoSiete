package com.appmovil.inventorywidget.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.appmovil.inventorywidget.R
import java.util.concurrent.Executor

class LoginFragment : Fragment() {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflamos tu layout original (no se toca)
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Configuración del executor
        executor = ContextCompat.getMainExecutor(requireContext())

        // Configuración del prompt biométrico
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(requireContext(), "Autenticación exitosa ✅", Toast.LENGTH_SHORT).show()
                    // Navegar al fragmento principal de inventario
                    findNavController().navigate(R.id.action_loginFragment_to_inventoryFragment)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(requireContext(), "Error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(requireContext(), "Huella no reconocida ❌", Toast.LENGTH_SHORT).show()
                }
            })

        // Configuración del mensaje del diálogo
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación con Biometría")
            .setSubtitle("Ingrese su huella digital")
            .setNegativeButtonText("CANCELAR")
            .build()

        // Listener para tu animación Lottie
        val fingerprintAnim = view.findViewById<LottieAnimationView>(R.id.fingerprintAnimation)
        fingerprintAnim.setOnClickListener {
            Toast.makeText(requireContext(), "Huella tocada 👆", Toast.LENGTH_SHORT).show()
            biometricPrompt.authenticate(promptInfo)
        }

        return view
    }
}
