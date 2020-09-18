package com.cbsd.scan

import android.app.Application
import com.cbsd.zxing.activity.ZXingLibrary
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure

class ScanApplication : Application() {

    companion object {
        private lateinit var refWatcher: RefWatcher
        fun getRefWatcher(): RefWatcher {
            return refWatcher
        }
    }

    override fun onCreate() {
        super.onCreate()
        refWatcher = LeakCanary.install(this)
        ZXingLibrary.initDisplayOpinion(this)
        UMConfigure.init(this, "5f617c55b473963242a018ee",  "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "")
        UMConfigure.setLogEnabled(true)
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
    }
}