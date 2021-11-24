package com.martbangunan.app.ui.fragment.customer

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.martbangunan.app.R
import com.martbangunan.app.ui.activity.customer.ProductActivity

class CategoryFragment : Fragment() {
    private val all: TextView by lazy { requireActivity().findViewById(R.id.tvCategoryAll) }
    private val alami: TextView by lazy { requireActivity().findViewById(R.id.tvCategoryAlami) }
    private val pabrik: TextView by lazy { requireActivity().findViewById(R.id.tvCategoryPabrik) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        all.setOnClickListener {
            startActivity(
                Intent(requireContext(), ProductActivity::class.java)
                    .putExtra("category", "all")
            )
        }
        alami.setOnClickListener {
            startActivity(
                Intent(requireContext(), ProductActivity::class.java)
                    .putExtra("category", "Material bangunan alami")
            )
        }
        pabrik.setOnClickListener {
            startActivity(
                Intent(requireContext(), ProductActivity::class.java)
                    .putExtra("category", "Material bangunan pabrik")
            )
        }
    }
}