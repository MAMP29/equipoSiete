package com.appmovil.inventorywidget

import com.appmovil.inventorywidget.model.AuthResult
import com.appmovil.inventorywidget.repository.AuthRepository
import com.appmovil.inventorywidget.repository.UserRepository
import com.appmovil.inventorywidget.viewmodel.AuthState
import com.appmovil.inventorywidget.viewmodel.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var userRepository: UserRepository
    private lateinit var authViewModel: AuthViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = Mockito.mock(AuthRepository::class.java)
        userRepository = Mockito.mock(UserRepository::class.java)
        authViewModel = AuthViewModel(authRepository, userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    // Pruebas, validacion de formulario
    @Test
    fun `dado un email y contraseña válidos, isFormValid debe ser true`() {
        authViewModel.onEmailChange("test@example.com")
        authViewModel.onPasswordChange("123456")
        assertTrue(authViewModel.isFormValid.value)
    }

    @Test
    fun `dada una contraseña inválida, isFormValid debe ser false`() {
        authViewModel.onEmailChange("test@example.com")
        authViewModel.onPasswordChange("123")
        assertFalse(authViewModel.isFormValid.value)
    }

    @Test
    fun `dado un email inválido, isFormValid debe ser false`() {
        authViewModel.onEmailChange("")
        authViewModel.onPasswordChange("123456")
        assertFalse(authViewModel.isFormValid.value)
    }

    @Test
    fun `dado un login exitoso, el estado debe terminar en Success`() = runTest {
        val email = "test@test.com"
        val password = "123456"

        // Simulamos una respuesta de exito
        Mockito.`when`(authRepository.login(email, password))
            .thenReturn(AuthResult(isSuccess = true))

        // Verificando estado inicial
        assertTrue(authViewModel.authState.value is AuthState.Idle)

        authViewModel.login(email, password)

        assertTrue(authViewModel.authState.value is AuthState.Success)
        Mockito.verify(authRepository).login(email, password)
    }

    @Test
    fun `dado un login fallido, el estado debe terminar en Error`() = runTest {
        val email = "error@example.com"
        val password = "bad"
        val errorMsg = "Usuario no encontrado"

        Mockito.`when`(authRepository.login(email, password))
            .thenReturn(AuthResult(isSuccess = false, message = errorMsg))

        authViewModel.login(email, password)

        val currentState = authViewModel.authState.value
        assertTrue(currentState is AuthState.Error)
        assertEquals(errorMsg, (currentState as AuthState.Error).message)
        assertFalse(currentState.isRegisterError)
    }

    @Test
    fun `al llamar a logout despues de un login, el estado debe volver a Idle`() = runTest {
        val email = "test@test.com"
        val password = "123456"

        Mockito.`when`(authRepository.login(email, password))
            .thenReturn(AuthResult(isSuccess = true))

        Mockito.doNothing().`when`(authRepository).logout()

        authViewModel.login(email, password)
        authViewModel.logout()

        val currentState = authViewModel.authState.value

        assertTrue(currentState is AuthState.Idle)
        Mockito.verify(authRepository).logout()
    }

    @Test
    fun registerUser_thenFail_thenAssertAuthError() = runTest {
        val email = "testr@example.com"
        val password = "contra"
        val errorMsg = "Contraseña no valida"

        Mockito.`when`(authRepository.register(email, password))
            .thenReturn(AuthResult(isSuccess = false, message = errorMsg))

        authViewModel.register(email, password)

        val currentState = authViewModel.authState.value

        // Verificamos tipo de error y mensaje
        assertTrue(currentState is AuthState.Error)
        assertEquals(errorMsg, (currentState as AuthState.Error).message)
    }

    @Test
    fun registerUser_thenAssertIfLoginWasCalled() = runTest {
        val email = "test@test.com"
        val password = "123456"

        Mockito.`when`(authRepository.register(email, password))
            .thenReturn(AuthResult(isSuccess = true))

        Mockito.`when`(authRepository.login(email, password))
            .thenReturn(AuthResult(isSuccess = true))

        // Verificando estado inicial
        assertTrue(authViewModel.authState.value is AuthState.Idle)

        authViewModel.register(email, password)

        val currentState = authViewModel.authState.value

        assertTrue(currentState is AuthState.Success)

        // Verificamos que se llamó el login en el repositorio
        Mockito.verify(authRepository).login(email, password)
    }

    @Test
    fun registerSuccess_loginFails_shouldEndWithErrorState() = runTest {
        val email = "test@test.com"
        val password = "123456"
        val loginErrorMessage = "Credenciales de sesión no válidas después del registro."

        // Configura el registro para que simule ser exitoso
        Mockito.`when`(authRepository.register(email, password))
            .thenReturn(AuthResult(isSuccess = true, message = null))

        // Configura el registro para que simule fallar
        Mockito.`when`(authRepository.login(email, password))
            .thenReturn(AuthResult(isSuccess = false, message = loginErrorMessage))

        assertTrue(authViewModel.authState.value is AuthState.Idle)

        authViewModel.register(email, password)

        Mockito.verify(authRepository).login(email, password)

        val currentState = authViewModel.authState.value

        assertTrue(currentState is AuthState.Error)

        assertEquals(loginErrorMessage, (currentState as AuthState.Error).message)
    }
}