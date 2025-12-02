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
    fun `dado un registro fallido en Auth, el estado debe ser Error y no se debe crear el usuario`() = runTest {
        val email = "test@test.com"
        val password = "123456"
        val errorMsg = "El email ya está en uso"

        // Arrange
        Mockito.`when`(authRepository.register(email, password))
            .thenReturn(AuthResult(isSuccess = false, message = errorMsg))

        // Act
        authViewModel.register(email, password)

        // Assert
        val currentState = authViewModel.authState.value
        assertTrue(currentState is AuthState.Error)
        assertEquals(errorMsg, (currentState as AuthState.Error).message)
        assertTrue(currentState.isRegisterError) // Verificamos el flag

        // Verificamos que nunca se interactuó con el userRepository
        Mockito.verify(userRepository, Mockito.never()).createUser(any(User::class.java))
        // Verificamos que nunca se intentó hacer login
        Mockito.verify(authRepository, Mockito.never()).login(email, password)
    }

    @Test
    fun `dado un registro exitoso pero fallo al crear usuario en Firestore, el estado debe ser Error`() = runTest {
        val email = "test@test.com"
        val password = "123456"
        val userId = "user-id-123"
        val firestoreError = RuntimeException("Permiso denegado en Firestore")

        // Arrange
        Mockito.`when`(authRepository.register(email, password))
            .thenReturn(AuthResult(isSuccess = true, userId = userId))
        // Simulamos que createUser lanza una excepción
        Mockito.`when`(userRepository.createUser(any(User::class.java)))
            .thenThrow(firestoreError)

        // Act
        authViewModel.register(email, password)

        // Assert
        val currentState = authViewModel.authState.value
        assertTrue(currentState is AuthState.Error)
        assertTrue((currentState as AuthState.Error).message.contains("falló guardar el perfil"))

        // Verificamos que se intentó crear el usuario
        Mockito.verify(userRepository).createUser(any(User::class.java))
        // Verificamos que nunca se intentó hacer login porque el flujo se interrumpió
        Mockito.verify(authRepository, Mockito.never()).login(email, password)
    }
}