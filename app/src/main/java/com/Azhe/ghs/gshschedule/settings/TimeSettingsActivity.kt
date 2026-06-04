package com.Azhe.ghs.gshschedule.settings

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.Azhe.ghs.gshschedule.R
import com.Azhe.ghs.gshschedule.utils.ViewUtils
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import splitties.dimensions.dip
import splitties.resources.color
import splitties.resources.styledColor

class TimeSettingsActivity : AppCompatActivity() {

    private val viewModel by viewModels<TimeSettingsViewModel>()
    private lateinit var navController: NavController
    private var isExit: Boolean = false
    private lateinit var mainTitle: AppCompatTextView

    fun launch(block: suspend CoroutineScope.() -> Unit): Job = lifecycleScope.launch {
        lifecycle.whenStarted(block)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_settings)

        // Add title bar
        val root = findViewById<LinearLayoutCompat>(R.id.ll_root)
        root.addView(createTitleBar(), 0)

        initView()
    }

    private fun createTitleBar() = LinearLayoutCompat(this).apply {
        setBackgroundColor(styledColor(R.attr.colorSurface))
        setPadding(0, ViewUtils.getStatusBarHeight(this@TimeSettingsActivity), 0, 0)
        val outValue = TypedValue()
        theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true)

        addView(AppCompatImageButton(context).apply {
            setImageResource(R.drawable.ic_back)
            setBackgroundResource(outValue.resourceId)
            setPadding(dip(8))
            setColorFilter(styledColor(R.attr.colorOnBackground))
            setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }, LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, dip(48)))

        mainTitle = AppCompatTextView(context).apply {
            text = title
            gravity = Gravity.CENTER_VERTICAL
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
        }
        addView(mainTitle, LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, dip(48)).apply {
            weight = 1f
        })

        // Save button
        addView(AppCompatTextView(context).apply {
            text = "保存"
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundResource(outValue.resourceId)
            setPadding(dip(24), 0, dip(24), 0)
            setTextColor(color(R.color.colorAccent))
            setOnClickListener {
                when (navController.currentDestination?.id) {
                    R.id.timeTableFragment -> {
                        setResult(Activity.RESULT_OK, Intent().putExtra("selectedId", viewModel.selectedId))
                        finish()
                    }
                    R.id.timeSettingsFragment -> {
                        launch {
                            try {
                                viewModel.saveDetailData(viewModel.entryPosition)
                                navController.navigateUp()
                                Toasty.success(applicationContext, "保存成功").show()
                            } catch (e: Exception) {
                                Toasty.error(applicationContext, "出现错误>_<${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }, LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, dip(48)))
    }

    private fun initView() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        val navGraph = navHostFragment.navController.navInflater.inflate(R.navigation.nav_time_settings)
        val fragDestination = navGraph.findNode(R.id.timeTableFragment)!!
        fragDestination.addArgument("selectedId", NavArgument.Builder()
                .setType(NavType.IntType).setIsNullable(false).setDefaultValue(intent.extras?.getInt("selectedId") ?: 1).build())
        navHostFragment.navController.graph = navGraph
        navController = Navigation.findNavController(this, R.id.nav_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            mainTitle.text = destination.label
        }
    }

    private fun exitBy2Click() {
        if (!isExit) {
            isExit = true
            lifecycleScope.launch {
                Toasty.info(applicationContext, "真的不保存吗？再按一次退出", Toast.LENGTH_LONG).show()
                delay(2000)
                isExit = false
            }
        } else {
            when (navController.currentDestination?.id) {
                R.id.timeTableFragment -> finish()
                R.id.timeSettingsFragment -> navController.navigateUp()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        exitBy2Click()
    }
}
