package com.Azhe.ghs.gshschedule.widget

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.BaseDialogFragment
import com.google.android.material.chip.Chip
import com.Azhe.ghs.gshschedule.R
import com.Azhe.ghs.gshschedule.databinding.FragmentEditDetailBinding

class EditDetailFragment : BaseDialogFragment() {

    private var _binding: FragmentEditDetailBinding? = null
    private val binding get() = _binding!!

    private val detailData: ArrayList<String>? by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.getStringArrayList("data")
    }

    private val title: String by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.getString("title") ?: "编辑"
    }

    private val value: String by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.getString("value") ?: ""
    }

    var listener: OnSaveClickedListener? = null

    override val layoutId: Int
        get() = R.layout.fragment_edit_detail

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentEditDetailBinding.inflate(layoutInflater)
        val cardView = root!!.findViewById<com.google.android.material.card.MaterialCardView>(R.id.base_card_view)
        cardView.removeAllViews()
        cardView.addView(binding.root)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle.text = title
        binding.etDetail.setText(value)
        val hasData = detailData?.any {
            it.isNotBlank()
        } ?: false
        if (!hasData) {
            binding.svDetails.visibility = View.GONE
            binding.etDetail.hint = "请输入…"
        }
        detailData?.forEach {
            if (it.isEmpty()) return@forEach
            val chip = layoutInflater.inflate(R.layout.chip_group_item_choice, binding.cgDetails, false) as Chip
            chip.text = it
            binding.cgDetails.addView(chip)
        }
        binding.cgDetails.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId < 0) return@setOnCheckedChangeListener
            val t = group.findViewById<Chip>(checkedId).text
            binding.etDetail.setText(t)
        }
        binding.tvSave.setOnClickListener {
            listener?.save(binding.etDetail, dialog!!)
        }
    }

    interface OnSaveClickedListener {
        fun save(editText: AppCompatEditText, dialog: Dialog)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(title: String, data: ArrayList<String>, str: String) =
                EditDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString("title", title)
                        putStringArrayList("data", data)
                        putString("value", str)
                    }
                }
    }
}
