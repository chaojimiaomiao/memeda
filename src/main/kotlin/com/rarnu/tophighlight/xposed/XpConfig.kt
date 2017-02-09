package com.rarnu.tophighlight.xposed

import android.content.Context
import android.os.Build
import de.robv.android.xposed.XSharedPreferences

/**
 * Created by rarnu on 2/5/17.
 */
object XpConfig {

    // 不同的进程空间，需要不同的配置加载
    fun xposedload() {

        val prefs = XSharedPreferences(PKGNAME, PREF)
        prefs.makeWorldReadable()
        prefs.reload()
        // load
        statusBarColor = prefs.getInt(KEY_STATUBAR_COLOR, defaultStatusBarColor)
        showDivider = prefs.getBoolean(KEY_SHOW_DIVIDER, defaultShowDivider)
        dividerColor = prefs.getInt(KEY_DIVIDER_COLOR, defaultDividerColor)
        darkerStatusBar = prefs.getBoolean(KEY_DARKER_STATUSBAR, defaultDarkerStatusBar)
        darkStatusBarText = prefs.getBoolean(KEY_DARK_STATUSBAR_TEXT, defaultDarkStatusBarText)
        macColor = prefs.getInt(KEY_MAC_COLOR, defaultMacColor)
        macPressColor = prefs.getInt(KEY_MAC_PRESS_COLOR, defaultMacPressColor)
        readerColor = prefs.getInt(KEY_TOP_READER_COLOR, defaultReaderColor)
        readerPressColor = prefs.getInt(KEY_TOP_READER_PRESS_COLOR, defaultReaderPressColor)
        (1..3).forEach {
            topColors[it] = prefs.getInt("$KEY_DING$it", defaultTopColors[it])
            topPressColors[it] = prefs.getInt("$KEY_PRESS_DING$it", defaultTopPressColors[it])
        }
    }

    fun load(ctx: Context) {
        val prefs = ctx.getSharedPreferences(XpConfig.PREF, if (Build.VERSION.SDK_INT < 24) 1 else 0)
        statusBarColor = prefs.getInt(KEY_STATUBAR_COLOR, defaultStatusBarColor)
        showDivider = prefs.getBoolean(KEY_SHOW_DIVIDER, defaultShowDivider)
        dividerColor = prefs.getInt(KEY_DIVIDER_COLOR, defaultDividerColor)
        darkerStatusBar = prefs.getBoolean(KEY_DARKER_STATUSBAR, defaultDarkerStatusBar)
        darkStatusBarText = prefs.getBoolean(KEY_DARK_STATUSBAR_TEXT, defaultDarkStatusBarText)
        macColor = prefs.getInt(KEY_MAC_COLOR, defaultMacColor)
        macPressColor = prefs.getInt(KEY_MAC_PRESS_COLOR, defaultMacPressColor)
        readerColor = prefs.getInt(KEY_TOP_READER_COLOR, defaultReaderColor)
        readerPressColor = prefs.getInt(KEY_TOP_READER_PRESS_COLOR, defaultReaderPressColor)
        (1..3).forEach {
            topColors[it] = prefs.getInt("$KEY_DING$it", defaultTopColors[it])
            topPressColors[it] = prefs.getInt("${KEY_PRESS_DING}$it", defaultTopPressColors[it])
        }
    }

    fun save(ctx: Context) {
        val prefs = ctx.getSharedPreferences(XpConfig.PREF, if (Build.VERSION.SDK_INT < 24) 1 else 0)
        prefs.edit()
                .putInt(KEY_STATUBAR_COLOR, statusBarColor)
                .putBoolean(KEY_DARKER_STATUSBAR, darkerStatusBar)
                .putBoolean(KEY_DARK_STATUSBAR_TEXT, darkStatusBarText)
                .putInt(KEY_MAC_COLOR, macColor)
                .putInt(KEY_MAC_PRESS_COLOR, macPressColor)
                .putInt(KEY_TOP_READER_COLOR, readerColor)
                .putInt(KEY_TOP_READER_PRESS_COLOR, readerPressColor)
                .apply()
    }

    fun saveGroup(ctx: Context) {
        val prefs = ctx.getSharedPreferences(XpConfig.PREF, if (Build.VERSION.SDK_INT < 24) 1 else 0)
        var editor = prefs.edit()
        (1..3).forEach {
            editor.putInt("${XpConfig.KEY_DING}$it", topColors[it])
        }
        editor.apply()
    }

    val PKGNAME = "com.rarnu.tophighlight"
    val PREF = "settings"

    private val KEY_STATUBAR_COLOR = "status_bar_color"
    private val KEY_SHOW_DIVIDER = "show_divider"
    private val KEY_DIVIDER_COLOR = "divider_color"
    private val KEY_DARKER_STATUSBAR = "darker_status_bar"
    private val KEY_DARK_STATUSBAR_TEXT = "dark_status_bar_text"
    //private val KEY_TOP_COLOR = "top_color_"
    //private val KEY_TOP_PRESS_COLOR = "top_press_color_"

    val KEY_MAC_COLOR = "mac_color"
    val KEY_MAC_PRESS_COLOR = "mac_press_color"
    val KEY_TOP_READER_COLOR = "top_reader_color"
    val KEY_TOP_READER_PRESS_COLOR = "top_reader_press_color"

    val KEY_DING = "ding"

    val KEY_PRESS_DING = "ding_press"

    // 状态栏颜色
    val defaultStatusBarColor = 0xffffc7c8.toInt()
    var statusBarColor = defaultStatusBarColor

    // 是否显示ActionBar与ListView之间的分隔线，可以修改
    val defaultShowDivider = false
    var showDivider = defaultShowDivider

    // ActionBar与ListView之间的分隔线颜色，当showDivider为true时生效，可以修改
    val defaultDividerColor = 0xffff8f8e.toInt()
    var dividerColor = defaultDividerColor

    // 是否使用深色的状态栏，可以修改
    val defaultDarkerStatusBar = true
    var darkerStatusBar = defaultDarkerStatusBar

    // 状态栏文字和图标颜色是否采用黑色，黑色为true，白色为false
    val defaultDarkStatusBarText = false
    var darkStatusBarText = defaultDarkStatusBarText

    // Mac 和 置顶的reader颜色
    var defaultMacColor = 0xfff5f5f5.toInt()
    var macColor = defaultMacColor
    var defaultMacPressColor = 0xffd9d9d9.toInt()
    var macPressColor = defaultMacPressColor

    var defaultReaderColor = 0xfff5f5f5.toInt()
    var readerColor = defaultReaderColor
    var defaultReaderPressColor = 0xffd9d9d9.toInt()
    var readerPressColor = defaultReaderPressColor

    // 置顶的群或者个人
    val defaultTopColors = arrayOf(
            0xffffc7c8.toInt(),
            0xfffeeac7.toInt(),
            0xffffffc9.toInt(),
            0xffc9fec6.toInt(),
            0xffc8fefe.toInt(),
            0xffc7c8ff.toInt(),
            0xffedc7ff.toInt(),
            0xffeeeeee.toInt(),
            0xffeeeeee.toInt(),
            0xffeeeeee.toInt())
    var topColors = defaultTopColors

    // 默认的置顶项点击时的高亮颜色，可以修改
    val defaultTopPressColors = arrayOf(
            0xffff8f8e.toInt(),
            0xffffd38f.toInt(),
            0xffffff8d.toInt(),
            0xff8efe8e.toInt(),
            0xff8dffff.toInt(),
            0xff8f8fff.toInt(),
            0xffdb8eff.toInt(),
            0xffd9d9d9.toInt(),
            0xffd9d9d9.toInt(),
            0xffd9d9d9.toInt())
    var topPressColors = defaultTopPressColors
}