package com.Azhe.ghs.gshschedule.schedule_import

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.BaseDialogFragment
import androidx.fragment.app.activityViewModels
import com.Azhe.ghs.gshschedule.R
import com.Azhe.ghs.gshschedule.databinding.FragmentImportSettingBinding

class ImportSettingFragment : BaseDialogFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_import_setting

    private var _binding: FragmentImportSettingBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<ImportViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentImportSettingBinding.inflate(layoutInflater)
        val cardView = root!!.findViewById<com.google.android.material.card.MaterialCardView>(R.id.base_card_view)
        cardView.removeAllViews()
        // Binding was inflated without a parent, so XML layout_* attrs (280dp, gravity=center)
        // were lost.  Re-apply them as FrameLayout.LayoutParams.
        val width = (280 * resources.displayMetrics.density).toInt()
        val lp = FrameLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER
        cardView.addView(binding.root, lp)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvCover.setOnClickListener {
            viewModel.importId = activity!!.intent.extras!!.getInt("tableId", -1)
            viewModel.newFlag = false
            dismiss()
        }

        binding.tvNew.setOnClickListener {
            launch {
                viewModel.importId = viewModel.getNewId()
                viewModel.newFlag = true
                dismiss()
            }
        }

        binding.tvCancel.setOnClickListener {
            dismiss()
            activity!!.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
