package com.cbsd.scan

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cbsd.scan.cache.ACache
import com.cbsd.scan.http.CodeService
import com.cbsd.scan.http.RetrofitManager
import com.cbsd.scan.http.TransformUtils
import com.cbsd.zxing.activity.CaptureFragment
import com.cbsd.zxing.activity.CodeUtils
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.gyf.immersionbar.ktx.immersionBar
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        immersionBar {
            transparentStatusBar()
            navigationBarColor(R.color.blue)
            navigationBarColor(R.color.black)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ScanApplication.getRefWatcher().watch(this)
        requestPermission()
    }

    private fun requestPermission() {
        RxPermissions(this).request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            .subscribe({
                when (it) {
                    true -> {
                        initView()
                    }
                    else -> Toast.makeText(this, "请允许相机权限，以免不能正常使用", Toast.LENGTH_SHORT).show()
                }
            }, {
                Toast.makeText(this, "请允许相机权限，以免不能正常使用", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "权限异常:${it.message}")
            })
    }

    private fun initView() {
        val captureFragment = CaptureFragment()
        captureFragment.analyzeCallback = analyzeCallback
        supportFragmentManager.beginTransaction().replace(R.id.mainContainer, captureFragment)
            .commit()
//        captureFragment.setCameraInitCallBack {
//
//        }

        mainAddUrlBtn.setOnClickListener {
            val urlDialog = UrlDialog(this)
            urlDialog.setTitle("请求URL")
            urlDialog.setOnBottomDialogEditChangedListener({
                ACache.get(this@MainActivity).put("url", it)
            }, {
                testUrl(it!!)
            })
            urlDialog.show()
        }
    }

    private val analyzeCallback = object : CodeUtils.AnalyzeCallback {
        override fun onAnalyzeSuccess(mBitmap: Bitmap?, result: String?) {
            Log.d(TAG, "扫描结果:$result")
            val url = ACache.get(this@MainActivity).getAsString("url")
            if (!TextUtils.isEmpty(url)) {
                RetrofitManager.getInstance().api(CodeService::class.java)
                    .addResult(url!!, result!!)
                    .compose(TransformUtils.defaultSchedulers())
                    .subscribe({
                        Log.d(TAG, "添加数据成功:${Gson().toJson(it)}")
                    }, {
                        Log.d(TAG, "添加数据失败:${it.message}")
                    })
            }else{
                Toast.makeText(this@MainActivity, "请求地址为空", Toast.LENGTH_LONG).show()
            }
        }

        override fun onAnalyzeFailed() {
            Log.d(TAG, "扫描失败")
        }
    }

    private fun testUrl(url: String){
        RetrofitManager.getInstance().api(CodeService::class.java)
            .addResult(url, "test")
            .compose(TransformUtils.defaultSchedulers())
            .subscribe({
                Log.d(TAG, "添加数据成功:${Gson().toJson(it)}")
                Toast.makeText(this@MainActivity, "success", Toast.LENGTH_LONG).show()
            }, {
                Log.d(TAG, "添加数据失败:${it.message}")
                Toast.makeText(this@MainActivity, "failed: ${it.message}", Toast.LENGTH_LONG).show()
            })
    }
}