package com.rarnu.tophighlight.market

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.GridView
import android.widget.PopupWindow
import android.widget.Toast
import com.rarnu.tophighlight.R
import com.rarnu.tophighlight.api.LocalApi
import com.rarnu.tophighlight.api.Theme
import com.rarnu.tophighlight.api.ThemeINI
import com.rarnu.tophighlight.api.WthApi
import com.rarnu.tophighlight.util.Constants.REQUEST_CODE_PAY
import com.rarnu.tophighlight.util.UIUtils
import com.rarnu.tophighlight.xposed.XpConfig
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread


/**
 * getThemeList page=1 pageSize=2 sort=date
 * theme_get_download_url id=15
 * errorcode url
 * http://rarnu.xyz/wth/theme/url
 */
class ThemeListActivity : BaseMarkerActivity() {

    companion object {
        val MENUID_PROFILE = 1;
        val BASEURL = "http://diy.ourocg.cn/wth/theme/";
    }

    private var miProfile: MenuItem? = null
    private var listTheme: MutableList<ThemeINI>? = null
    private var gvTheme: GridView? = null
    private var adapterTheme: ThemeListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalApi.ctx = this
        setContentView(R.layout.activity_themelist)


        Toast.makeText(this, "点击放大浏览主题", Toast.LENGTH_LONG).show()

        val fDir = File(XpConfig.BASE_FILE_PATH + "/down")
        if (!fDir.exists()) { fDir.mkdirs() }
        actionBar.setTitle(R.string.view_themes)
        gvTheme = findViewById(R.id.gvTheme) as GridView?

        listTheme = arrayListOf()
        adapterTheme = ThemeListAdapter(this, listTheme)
        gvTheme?.adapter = adapterTheme
        gvTheme?.setOnItemClickListener { adapterView, view, i, l ->
            val popup = PopupWindow(view, UIUtils.dip2px(100), UIUtils.dip2px(160))
            popup.setBackgroundDrawable(ColorDrawable(0x00000000));
            popup.isOutsideTouchable = true
            popup.showAtLocation(gvTheme, Gravity.CENTER, 0, 0)
        }

        loadThemeList()
    }

    private fun loadLocalThemeList() {
        //listTheme?.add(LocalTheme.themePurple)
        //adapterTheme?.setList(listTheme)
    }

    private fun loadThemeList() {
        val hTheme = object : Handler() {
            override fun handleMessage(msg: Message?) {
                adapterTheme?.setList(listTheme)
                findViewById(R.id.theme_load).visibility = View.INVISIBLE
                super.handleMessage(msg)
            }
        }

        var wht = Environment.getExternalStorageDirectory().absolutePath
        Log.e("wht", "wht: " + wht)
        thread {
            listTheme?.clear()
            val list = WthApi.themeGetList(1, 10, "date") as List<Theme>
            println("list = $list")
            for(theme : Theme in list) {
                Log.e("wht", "theme => $theme")
                var url = WthApi.themeGetDownloadUrl(theme.id)

                //val ini = WthApi.readThemeFromINI(wht + "/theme.ini")
                val filename = downloadFile(BASEURL + url)
                Log.e("themelist", "filename: " + filename)
                if (!filename.equals("")) {
                    val ini = WthApi.readThemeFromINI(filename)
                    if (ini != null) {
                        ini.localPath = filename
                        listTheme?.add(ini)
                    }
                }
            }
            // TODO: download theme file and make preview
            hTheme.sendEmptyMessage(0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        miProfile = menu?.add(0, MENUID_PROFILE, 1, R.string.menu_profile)
        miProfile?.setIcon(android.R.drawable.ic_menu_myplaces)
        miProfile?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            MENUID_PROFILE -> {
                if (LocalApi.userId == 0) {
                    // login / register
                    startActivityForResult(Intent(this, UserLoginRegisterActivity::class.java), 0)
                } else {
                    // user profile
                    startActivity(Intent(this, UserProfileActivity::class.java))
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return
        when(requestCode) {
            0 -> {
                // TODO: login or register callback
            }
            REQUEST_CODE_PAY -> {
                Toast.makeText(this, "重启本软件及微信可生效", Toast.LENGTH_LONG).show()
                XpConfig.ini = WthApi.readThemeFromINI(listTheme!![adapterTheme!!.getSelectedPos()].localPath)
                XpConfig.themePath = XpConfig.ini!!.localPath
                XpConfig.save(this)
            }
        }
    }

    fun downloadFile(iniUrl: String) : String{
        var lists = iniUrl.split("/")
        var localFile = XpConfig.BASE_FILE_PATH + "/down/"+ lists.last() + ".ini"
        val fTmp = File(localFile)
        if (fTmp.exists()) {
            //fTmp.delete()
            return localFile
        }
        var url: URL?
        var filesize = 0
        try {
            url = URL(iniUrl)
            val con = url.openConnection() as HttpURLConnection
            var ins = con.inputStream
            filesize = con.contentLength
            //val fileOut = File(localFile)// + ".tmp"
            val out = FileOutputStream(fTmp)//fileOut
            val buffer = ByteArray(1024)
            var count: Int
            while (true) {
                count = ins.read(buffer)
                if (count != -1) {
                    out.write(buffer, 0, count)
                } else {
                    break
                }
            }
            ins.close()
            out.close()
            return localFile
            //fileOut.renameTo(fTmp)
        } catch (e: Exception) {
            Log.e("", "下载文件失败")
            return ""
        }
    }
}