package com.Azhe.ghs.gshschedule.schedule

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.BaseDialogFragment
import com.Azhe.ghs.gshschedule.DonateActivity
import com.Azhe.ghs.gshschedule.R
import es.dmoral.toasty.Toasty
import splitties.activities.start

class DonateFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_donate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    private fun initEvent() {
        view?.findViewById<View>(R.id.ib_close)?.setOnClickListener {
            dismiss()
        }

        view?.findViewById<View>(R.id.tv_star)?.setOnClickListener {
            try {
                val uri = Uri.parse("market://details?id=com.Azhe.ghs.gshschedule")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                activity!!.startActivity(intent)
            } catch (e: Exception) {
                Toasty.info(context!!.applicationContext, "没有检测到应用商店").show()
            }
        }

        view?.findViewById<View>(R.id.tv_feedback)?.setOnClickListener {
            // 本地联系方式
            Toasty.info(context!!.applicationContext, "如有问题请联系开发者 QQ：你的QQ号", Toast.LENGTH_LONG).show()
        }

        view?.findViewById<View>(R.id.tv_donate)?.setOnClickListener {
            showThanksDialog()
        }

        view?.findViewById<View>(R.id.tv_donate_list)?.setOnClickListener {
            activity!!.start<DonateActivity>()
        }
    }

    private fun showThanksDialog() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_donate_thanks, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
        view.findViewById<View>(R.id.btn_ok).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = DonateFragment()
    }
}
