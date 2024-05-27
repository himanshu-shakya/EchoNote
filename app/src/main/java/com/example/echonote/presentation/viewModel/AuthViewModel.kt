package com.example.echonote.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echonote.core.utils.Result
import com.example.echonote.core.utils.UiState
import com.example.echonote.domain.model.User
import com.example.echonote.domain.repository.AuthRepository
import com.example.echonote.domain.utils.AuthAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    //flows
    private val _loginFlow = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val loginFlow = _loginFlow.asStateFlow()
    private val _createAccountFlow = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val createAccount = _createAccountFlow.asStateFlow()
    private val _forgotPasswordFlow = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val forgotPasswordFlow = _forgotPasswordFlow.asStateFlow()
    private val _userFlow = MutableStateFlow<UiState<User>>(UiState.Idle)
    val userFlow = _userFlow.asStateFlow()
    private val _logoutFlow = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val logout = _logoutFlow.asStateFlow()
    private val _deleteAccountFlow = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val deleteAccount = _deleteAccountFlow.asStateFlow()
    private val _updateUserFlow  = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val updateUser = _updateUserFlow.asStateFlow()


    //create account
    private val _createAccountEmailText = MutableStateFlow("")
    val createAccountEmailText = _createAccountEmailText.asStateFlow()
    private val _createAccountPasswordText = MutableStateFlow("")
    val createAccountPasswordText = _createAccountPasswordText
    private val _createAccountName = MutableStateFlow("")
    val createAccountName = _createAccountName.asStateFlow()

    private val _createAccountEmailError = MutableStateFlow("")
    val createAccountEmailError = _createAccountEmailError.asStateFlow()
    private val _createAccountPasswordError = MutableStateFlow("")
    val createAccountPasswordError = _createAccountPasswordError.asStateFlow()
    private val _createAccountNameError = MutableStateFlow("")
    val createAccountNameError = _createAccountNameError.asStateFlow()


    // login
    private val _loginEmailText = MutableStateFlow("")
    val loginEmailText = _loginEmailText.asStateFlow()
    private val _loginPasswordText = MutableStateFlow("")
    val loginPasswordText = _loginPasswordText.asStateFlow()

    private val _loginEmailError = MutableStateFlow("")
    val loginEmailError = _loginEmailError.asStateFlow()
    private val _loginPasswordError = MutableStateFlow("")
    val loginPasswordError = _loginPasswordError.asStateFlow()

    //forgot password
    private val _fgPassEmail = MutableStateFlow("")
    val fgPassEmail = _fgPassEmail.asStateFlow()
    private val _fgPassEmailError = MutableStateFlow("")
    val fgPassEmailError = _fgPassEmailError.asStateFlow()

    fun onEvent(authAction: AuthAction) {
        when (authAction) {
            is AuthAction.CreateAccount -> {
                createAccount(authAction.email, authAction.password, authAction.userName)

            }

            is AuthAction.Login -> {
                login(authAction.email, authAction.password)
            }

            is AuthAction.ForgotPassword -> {
                forgotPassword(email = authAction.email)
            }

            is AuthAction.GetUser -> {
                getUser()
            }
            is AuthAction.Logout -> {
                logout()
            }

            is AuthAction.DeleteAccount -> {
               deleteAccount(authAction.email, authAction.password)
            }

            is AuthAction.UpdateUser -> {
                updateUser(authAction.user)
            }
        }
    }

    private fun updateUser(user:User){
        _updateUserFlow.update { UiState.Loading }
        viewModelScope.launch {
            authRepository.updateUser(user).collectLatest { result->
                when(result){
                    is Result.Error -> {
                        _updateUserFlow.update { UiState.Error(result.error) }
                    }
                    is Result.Success -> {
                        _updateUserFlow.update { UiState.Success(result.data) }
                    }
                }
            }
        }
    }
    private fun deleteAccount(email: String, password: String) {
        _deleteAccountFlow.update { UiState.Loading }
        viewModelScope.launch {
            authRepository.deleteAccount(email,password).collectLatest {result->
                when(result){
                    is Result.Error -> { _deleteAccountFlow.update { UiState.Error(result.error) } }
                    is Result.Success -> { _deleteAccountFlow.update { UiState.Success(result.data) } }
                }

            }
        }
    }
    private fun logout(){
        _logoutFlow.update { UiState.Loading }
        viewModelScope.launch {
            authRepository.logout().collectLatest {result->
                when(result){
                    is Result.Error -> { _logoutFlow.update { UiState.Error(result.error) } }
                    is Result.Success -> { _logoutFlow.update { UiState.Success(result.data) } }
                }

            }
        }
    }
    private fun getUser() {
     _userFlow.update { UiState.Loading }
        viewModelScope.launch {
            authRepository.getUser().collectLatest {result->
                when(result){
                    is Result.Error -> { _userFlow.update { UiState.Error(result.error) } }
                    is Result.Success -> { _userFlow.update { UiState.Success(result.data) } }
                }
            }
        }
    }

    private fun login(email: String, password: String) {
        _loginFlow.update { UiState.Loading }
        viewModelScope.launch {
            _loginFlow.update { UiState.Loading }
            authRepository.login(email = email, password = password).collectLatest { result ->
                when (result) {
                    is Result.Error -> {
                        _loginFlow.update { UiState.Error(result.error) }
                    }

                    is Result.Success -> {
                        _loginFlow.update { UiState.Success(result.data) }
                    }
                }

            }
        }
    }

    private fun createAccount(email: String, password: String, userName: String) {
        _createAccountFlow.update { UiState.Loading }
        viewModelScope.launch {
            authRepository.createAccount(userName = userName, email = email, password = password)
                .collectLatest { result ->
                    when (result) {
                        is Result.Error -> {
                            _createAccountFlow.update { UiState.Error(result.error) }
                        }

                        is Result.Success -> {
                            _createAccountFlow.update { UiState.Success(result.data) }
                        }
                    }

                }
        }
    }

    private fun forgotPassword(email: String) {
        _forgotPasswordFlow.update { UiState.Loading }
        viewModelScope.launch {
            authRepository.forgotPassword(email).collectLatest { result ->
                when (result) {
                    is Result.Error -> {
                        _forgotPasswordFlow.update { UiState.Error(result.error) }
                    }

                    is Result.Success -> {
                        _forgotPasswordFlow.update { UiState.Success(result.data) }
                    }
                }

            }
        }
    }

    // create account text change
    fun createAccountEmailTextChange(newText: String) {
        _createAccountEmailText.update { newText }
        if (isValidEmail(newText) || newText.isEmpty()) {
            _createAccountEmailError.update { "" }
        } else {
            _createAccountEmailError.update { "Please enter valid email." }
        }
    }

    fun createAccountPasswordTextChange(newText: String) {
        _createAccountPasswordText.update { newText }
        if (isValidPassword(newText) || newText.isEmpty()) {
            _createAccountPasswordError.update { "" }
        } else {
            _createAccountPasswordError.update { "Password should be at least 8 characters and include uppercase, lowercase, number, and special character" }
        }

    }

    fun createAccountNameTextChange(newText: String) {
        _createAccountName.update { newText }
        if (isNameValid(newText)) {
            _createAccountNameError.update { "" }
        } else {
            _createAccountNameError.update { "Username length should be greater than 5 character." }
        }
    }

    // login text change
    fun loginEmailTextChange(newText: String) {
        _loginEmailText.update { newText }
        if (isValidEmail(newText) || newText.isEmpty()) {
            _loginEmailError.update { "" }
        } else {
            _loginEmailError.update { "Please enter valid email." }
        }
    }

    fun loginPasswordTextChange(newText: String) {
        _loginPasswordText.update { newText }
        if (isValidPassword(newText) || newText.isEmpty()) {
            _loginPasswordError.update { "" }
        } else {
            _loginPasswordError.update { "Password should be at least 8 characters and include uppercase, lowercase, number, and special character" }
        }

    }

    fun fgPassEmailTextChange(newText: String) {
        _fgPassEmail.update { newText }
        if (isValidEmail(newText) || newText.isEmpty()) {
            _fgPassEmailError.update { "" }
        } else {
            _fgPassEmailError.update { "Please enter valid email." }
        }
    }

    private fun isNameValid(name: String): Boolean {
        return name.isNotEmpty() && name.length >= 5
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return email.matches(emailRegex.toRegex())
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+\$).{8,}\$"
        return password.matches(passwordRegex.toRegex())
    }

    fun resetForgotPasswordState() {
        _forgotPasswordFlow.update { UiState.Idle }
        _fgPassEmail.update { "" }
        _fgPassEmailError.update { "" }
    }

    fun resetLoginState() {
        _loginFlow.update { UiState.Idle }
        _loginEmailText.update { "" }
        _loginEmailError.update { "" }
        _loginPasswordText.update { "" }
    }

    fun resetLogoutState(){
        _logoutFlow.update { UiState.Idle }
    }
    fun resetDeleteAccountState(){
        _loginEmailText.update { "" }
        _loginPasswordText.update { "" }
        _deleteAccountFlow.update { UiState.Idle }
    }
    fun resetCreateAccountState() {
        _createAccountFlow.update { UiState.Idle }
        _createAccountEmailError.update { "" }
        _createAccountName.update { "" }
        _createAccountPasswordError.update { "" }
        _createAccountEmailText.update { "" }
        _createAccountNameError.update { "" }
        _createAccountPasswordError.update { "" }
    }
    fun resetUpdateUserState(){
        _updateUserFlow.update { UiState.Idle }
        _createAccountEmailError.update { "" }
        _createAccountName.update { "" }

    }

}