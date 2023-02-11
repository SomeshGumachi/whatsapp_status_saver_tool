package com.example.statussaver.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.statussaver.R
import com.hbb20.CountryCodePicker


class DirectChatFragment: Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.fragment_direct_chat,container,false)
        return view.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val countryCodePicker: CountryCodePicker = view.findViewById(R.id.country_code)
        val phone: EditText = view.findViewById(R.id.edt_phone)
        val message: EditText = view.findViewById(R.id.edt_msg)
        val sendbtn: Button = view.findViewById(R.id.btn_send)

        sendbtn.setOnClickListener {
            val messageStr = message.text.toString()
            var phoneStr = phone.text.toString()

            if (phoneStr.isNotEmpty()) {
                countryCodePicker.registerCarrierNumberEditText(phone)
                phoneStr = countryCodePicker.fullNumber
                    val i = Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            "https://api.whatsapp.com/send?phone=" + phoneStr +
                                    "&text=" + messageStr
                        )
                    )
                    startActivity(i)
                    message.setText("")
                    phone.setText("")
            } else {
                Toast.makeText(
                    requireContext(),
                    "Whatsapp is not installed",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }
}