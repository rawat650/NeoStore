package com.example.neostorefinal.fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.neostorefinal.R
import com.example.neostorefinal.activities.MyAccount
import com.example.neostorefinal.storage.SharedPreferenceManager
import kotlinx.android.synthetic.main.fragment_my_account.*
import kotlinx.android.synthetic.main.fragment_my_account.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyAccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyAccountFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myAccount = activity as MyAccount
        val view = inflater.inflate(R.layout.fragment_my_account, container, false)

        val AccFirstName = SharedPreferenceManager.getInstance(myAccount).data.first_name
        val AccLastName = SharedPreferenceManager.getInstance(myAccount).data.last_name
        val AccEmail = SharedPreferenceManager.getInstance(myAccount).data.email
        val AccPhoneNumber = SharedPreferenceManager.getInstance(myAccount).data.phone_no
        val AccDob = SharedPreferenceManager.getInstance(myAccount).data.dob
        val AccProfilePic = SharedPreferenceManager.getInstance(myAccount).data.profile_pic

        view.txtAccFirstName.text = AccFirstName
        view.txtAccLastName.text = AccLastName
        view.txtAccEmail.text = AccEmail
        view.txtAccPhonenumber.text = AccPhoneNumber.toString()
        view.txtAccDOB.text = AccDob
        Log.d(TAG, "onCreateView: pic $AccProfilePic")
        Glide.with(activity as MyAccount).load(AccProfilePic).placeholder(R.drawable.profileavtar)
            .into(view.profileImage)

          view.btnEditProfile.setOnClickListener(){
            val editProfileFragemnt :Fragment = EditProfileFragment()
            myAccount.supportFragmentManager.beginTransaction().replace(R.id.frameLayout,editProfileFragemnt,"myAccoutnfrgament")
                .addToBackStack("Edit_Profile")
                .commit()
        }

        view.myAccountTollbarBack.setOnClickListener(){
            (activity as MyAccount).onBackPressed()
        }

        view.btnResetPassword.setOnClickListener(){
            val resetPassword :Fragment = ResetPasswordFragment()
            myAccount.supportFragmentManager.beginTransaction().replace(R.id.frameLayout,resetPassword,"resetPasswordFragment")
                .addToBackStack("Reset_Password")
                .commit()
        }
        return view
    }


    }


