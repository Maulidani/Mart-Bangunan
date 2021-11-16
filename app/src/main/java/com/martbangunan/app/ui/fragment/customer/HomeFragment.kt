package com.martbangunan.app.ui.fragment.customer

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import com.martbangunan.app.R
import com.martbangunan.app.ui.activity.customer.ProductActivity

class HomeFragment : Fragment() {
    private val search: TextView by lazy { requireActivity().findViewById(R.id.tvSearch) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        search.setOnClickListener {
            startActivity(Intent(this.context, ProductActivity::class.java))
        }
    }

}