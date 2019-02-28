package com.taras_bekhta.doittest.views


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.taras_bekhta.doittest.R
import com.taras_bekhta.doittest.retrofit.AuthToken
import com.taras_bekhta.doittest.retrofit.IIdentityService
import com.taras_bekhta.doittest.retrofit.RetrofitClient
import com.taras_bekhta.doittest.util.AnimationUtil
import kotlinx.android.synthetic.main.fragment_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun getTitle(): String {
        return getString(R.string.log_in)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO: remove this before release
        emailEditText.setText("taras.bekhta@gmail.com")
        passWordEditText.setText("123456")

        loginRegisterSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                loginButton.text = getString(R.string.register)
            } else {
                loginButton.text = getString(R.string.log_in)
            }
        }

        loginButton.setOnClickListener {
            loginClickListener()
        }
    }

    private fun loginClickListener() {
        val isLogin = !loginRegisterSwitch.isChecked
        if (isValidEmail(emailEditText.text.toString())) {
            showLoader()
            val processUser = if (isLogin)
                RetrofitClient().getRetrofit().create(IIdentityService::class.java).loginUser(
                    emailEditText.text.toString(),
                    passWordEditText.text.toString()
                )
            else
                RetrofitClient().getRetrofit().create(IIdentityService::class.java).registerUser(
                    emailEditText.text.toString(),
                    passWordEditText.text.toString()
                )
            processUser.enqueue(
                object : Callback<AuthToken> {
                    override fun onResponse(call: Call<AuthToken>, response: Response<AuthToken>) {
                        if (response.isSuccessful) {
                            parentActivity.authToken = response.body()!!.token
                            val dir = LoginFragmentDirections.actionLoginFragmentToMainFragment()
                            findNavController().navigate(dir)
                        } else {
                            showErrorSnackBar(if(isLogin) getString(R.string.login_failed) else getString(R.string.registration_failed), loginButton)
                        }
                        hideLoader()
                    }

                    override fun onFailure(call: Call<AuthToken>, t: Throwable) {
                        hideLoader()
                        showErrorSnackBar(if(isLogin) getString(R.string.login_failure) else getString(R.string.register_failure), loginButton)
                    }
                }
            )
        } else {
            showErrorSnackBar(getString(R.string.email_invalid), loginButton)
        }
    }

    private fun showLoader() {
        loginLayout.alpha = 0.5f
        loginButton.isEnabled = false
        AnimationUtil.animateViewShow(loginProgressBar)
    }

    private fun hideLoader() {
        loginLayout.alpha = 1f
        loginButton.isEnabled = true
        AnimationUtil.animateViewHide(loginProgressBar)
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return if (target.isEmpty()) {
            false
        } else {
            android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }
}
