package com.example.neostorefinal.fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.neostorefinal.R
import com.example.neostorefinal.activities.MyAccount
import com.example.neostorefinal.api.RetrofitClient
import com.example.neostorefinal.modelClass.UpdateProfileClass
import com.example.neostorefinal.storage.SharedPreferenceManager
import kotlinx.android.synthetic.main.fragment_reset_password.*
import kotlinx.android.synthetic.main.fragment_reset_password.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ResetPasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResetPasswordFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_reset_password, container, false)
        view.btnResetPasswordSubmit.setOnClickListener(){
            validatePassWordField()
        }
        view.resetPasswprdTollbarBack.setOnClickListener(){
            (activity as MyAccount).supportFragmentManager.popBackStack("Reset_Password",
                FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        return view
    }

    private fun validatePassWordField() {
        if (!currentPassword() or !NewPassword() or !confirmedPassword())
            return
        else
            resetPassWord()
    }

    private fun resetPassWord() {
        val currentPassword = txtInputCurrentPassword.editText?.text.toString()
        val newPassword = txtInputNewPassword.editText?.text.toString()
        val confirmedPassword = txtInputConPassword.editText?.text.toString()
        val accessToken = SharedPreferenceManager.getInstance(activity as MyAccount).data.access_token

        RetrofitClient.getClient.changePassword(accessToken,currentPassword,newPassword,confirmedPassword).enqueue(object : Callback<UpdateProfileClass?> {
            override fun onResponse(
                call: Call<UpdateProfileClass?>,
                response: Response<UpdateProfileClass?>
            ) {
                if(response.body()!= null){
                    Toast.makeText((activity as MyAccount).applicationContext,"${response.body()?.user_msg}",Toast.LENGTH_LONG).show()
                    
                }else{
                    Toast.makeText((activity as MyAccount).applicationContext,"${response.errorBody()}",Toast.LENGTH_LONG).show()

                }
                
            }

            override fun onFailure(call: Call<UpdateProfileClass?>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun confirmedPassword(): Boolean {
        if(txtInputConPassword.editText?.text.toString().isEmpty()){
            txtInputConPassword.apply {
                error="Field cannot be blank"
                isExpandedHintEnabled = false
                requestFocus()
            }
            return false
        }else if(txtInputConPassword.editText?.text.toString() == txtInputNewPassword.editText?.text.toString()){
            txtInputConPassword.apply {
                error = null
                isExpandedHintEnabled = true

            }
            txtInputConPassword.requestFocus()
            return true
        }
        else{
            txtInputConPassword.apply{
                error = "Confirmed password doesn't match"
                isExpandedHintEnabled = false
                requestFocus()
            }

            return false

        }

    }

    private fun currentPassword(): Boolean {
        val password = txtInputCurrentPassword.editText?.text.toString()
        if (password.isEmpty()){
            txtInputCurrentPassword.apply {
                error = "Field cannot be blank"
                isExpandedHintEnabled = false
                requestFocus()
            }
            return false
        }else{
            txtInputCurrentPassword.apply {
                error = null
                isExpandedHintEnabled = true
            }
            txtInputNewPassword.requestFocus()
            return true
        }
    }

    private fun NewPassword():Boolean  {

        val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        val mPassWord = txtInputNewPassword.editText?.text.toString()
            if(mPassWord.isEmpty()){
                txtInputNewPassword.apply {
                    error="Field cannot be blank"
                    isExpandedHintEnabled = false
                    requestFocus()
                }
                return false
            }else if(Pattern.compile(passwordPattern).matcher(mPassWord).matches() && mPassWord.length >= 8){
                txtInputNewPassword.apply {
                    error = null
                    isExpandedHintEnabled = true
                }
                txtInputNewPassword.requestFocus()
                return true
            }
            else{

                txtInputNewPassword.apply{
                    error = "Password should be alphanumeric and 8 character long"
                    isExpandedHintEnabled = false
                    requestFocus()
                }
                return false


            }
        }




}