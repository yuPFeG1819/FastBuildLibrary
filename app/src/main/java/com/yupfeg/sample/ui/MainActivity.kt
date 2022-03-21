package com.yupfeg.sample.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.yupfeg.base.tools.ContactsTools
import com.yupfeg.base.tools.databinding.proxy.bindingActivity
import com.yupfeg.base.tools.ext.showShortToast
import com.yupfeg.base.tools.file.getUriFromFile
import com.yupfeg.base.tools.image.ImageLoader
import com.yupfeg.base.tools.window.fitImmersiveStatusBar
import com.yupfeg.base.tools.window.fitToSystemStatusBar
import com.yupfeg.base.tools.window.getStatusBarHeight
import com.yupfeg.base.tools.window.isNavigationBarVisible
import com.yupfeg.base.view.activity.BaseActivity
import com.yupfeg.base.viewmodel.ext.viewModelDelegate
import com.yupfeg.logger.ext.logd
import com.yupfeg.result.*
import com.yupfeg.result.permission.RequestPermissionLauncher
import com.yupfeg.result.permission.dialog.DefaultRationaleDialogFragment
import com.yupfeg.sample.R
import com.yupfeg.sample.TestWindowInsetActivity
import com.yupfeg.sample.databinding.ActivityMainBinding
import com.yupfeg.sample.ui.list.TestListActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : BaseActivity() {

    private val mBinding : ActivityMainBinding by bindingActivity(layoutId){
        it?.config = null
    }

    private val mTestResultActivityLauncher = StartActivityResultLauncher(this)
    private val mRequestPermissionLauncher = RequestPermissionLauncher(this)

    private val mTakePictureLauncher = TakePictureLauncher(this)
    private val mCropImageLauncher = CropImageLauncher(this)

    private val mPickContentLauncher = PickContentLauncher(this)
    private val mGetContentLauncher = GetMultiContentsLauncher(this)

    private val mViewModel : MainViewModel by viewModelDelegate()

    private val mRationalDialogFragment : DefaultRationaleDialogFragment
        by lazy(LazyThreadSafetyMode.NONE){
            DefaultRationaleDialogFragment(
                reason = "您需要允许权限才能继续",
                positiveText = "允许",
                negativeText = "拒绝",
                reasonTextColor = ContextCompat.getColor(this,android.R.color.black),
                tintColor = getThemeColor(R.attr.colorPrimary)
            )
        }

    private val mNaviSettingsTipDialogFragment : DefaultRationaleDialogFragment
        by lazy(LazyThreadSafetyMode.NONE){
            DefaultRationaleDialogFragment(
                reason = "您需要到系统设置开启权限才能继续",
                positiveText = "确认",
                negativeText = "取消",
                reasonTextColor = ContextCompat.getColor(this,android.R.color.black),
                tintColor = getThemeColor(R.attr.colorPrimary)
            )
        }

    private val mTestDialog : Dialog by lazy(LazyThreadSafetyMode.NONE){
        TestBindingDialog(this,this.lifecycle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logd("onCreate")
    }

    override fun onStart() {
        super.onStart()
        logd("onStart")
    }

    override fun onResume() {
        super.onResume()
        logd("onResume")
    }

    override fun onRestart() {
        super.onRestart()
        logd("onRestart")
    }

    override fun onPause() {
        super.onPause()
        logd("onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        logd("onDestroy")
        if (mRationalDialogFragment.isAdded){
            mRationalDialogFragment.dismiss()
        }

        if (mNaviSettingsTipDialogFragment.isAdded){
            mNaviSettingsTipDialogFragment.dismiss()
        }
    }

    /**布局文件id*/
    override val layoutId: Int
        get() = R.layout.activity_main

    /**
     * 初始化控件
     * * tip : 如果使用原始的布局方式，需要注意设置[setContentView]，
     * 否则推荐使用DataBinding，利用by关键字，通过`bindingActivity`函数代理DataBinding类，
     * 或者利用`DataBindingUtil.setContentView<T>(this, layoutId)`
     * */
    override fun initView(savedInstanceState: Bundle?) {
        mBinding.apply {
            config = BindingConfig()
            viewModel = mViewModel
        }
        fitImmersiveStatusBar(isDarkText = false)
        //延伸视图到状态栏，并添加额外padding
        mBinding.toolbarMain.fitToSystemStatusBar(true)
        this.window.decorView.post {
            logd("当前状态栏高度${this.getStatusBarHeight()} 是否存在导航栏${isNavigationBarVisible}")

        }
    }

    /**初始化数据*/
    override fun initData() {}

    /**
     * [Context]的拓展函数，获取当前主题颜色属性
     * @param attr 颜色属性
     */
    private fun Context.getThemeColor(vararg attr: Int): Int {
        val array: TypedArray = theme.obtainStyledAttributes(attr)
        val color = array.getColor(0, Color.TRANSPARENT)
        array.recycle()
        return color
    }

    inner class BindingConfig{

        fun naviBack(){
            finish()
        }

        fun testRequestPermission(){
            requestPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ){
                showShortToast("所有权限请求成功")
            }
        }

        fun testResultApiStartActivity(){
            //在协程中接收页面返回值
            lifecycleScope.launch(Dispatchers.Main){
                val resultIntent = mTestResultActivityLauncher.launchAwaitIntentOrNull<TestResultApiActivity>()
                resultIntent?.extras?.also {
                    Toast.makeText(this@MainActivity,"接收返回值${it["key"]}",Toast.LENGTH_SHORT).show()
                }
            }
            Toast.makeText(this@MainActivity,"发起页面跳转等待值",Toast.LENGTH_SHORT).show()

            //测试跳转页面
//            mTestResultActivityLauncher.launch<TestResultApiActivity>{ resultIntent->
//                resultIntent?.extras?.also {
//                    Toast.makeText(this@MainActivity,"接收返回值${it["key"]}",Toast.LENGTH_SHORT).show()
//                }
//            }
        }

        fun naviToTestWindowInset(){
            startActivity(Intent(this@MainActivity, TestWindowInsetActivity::class.java))
        }

        fun takeSystemPicture(){
            performTakePictureAndCrop()
        }

        fun pickGallery(){
            performPickImage()
        }

        fun pickContact(){
            performPickContact()
        }

        /**在系统图片选择器内选择图片*/
        fun selectImageFromGallery(){
            performSelectImage()
        }

        /**
         * 测试嵌套滑动
         * */
        fun naviToTestNestedScroll(){
            startActivity(Intent(this@MainActivity,TestNestedScrollActivity::class.java))
        }

        /**测试自定义View*/
        fun naviToCustomView(){
            startActivity(Intent(this@MainActivity,TestCustomViewActivity::class.java))
        }

        /**跳转测试列表页*/
        fun naviTestList(){
            startActivity(Intent(this@MainActivity,TestListActivity::class.java))
        }

        fun showTestDialog(){
            if (!mTestDialog.isShowing){
                mTestDialog.show()
            }
        }
    }

    private fun performTakePictureAndCrop(){
        requestPermissions(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ){
            mTakePictureLauncher.launch{filePath->
                filePath?:run {
                    showShortToast("拍照失败")
                    return@launch
                }
                showShortToast("拍照完成$filePath")
                val photoFile = File(filePath)
                if (!photoFile.exists()){
                    showShortToast("拍照失败")
                    return@launch
                }
                //更新系统相册显示
                ImageLoader.updateSystemGallery(
                    this, photoFile.toString()
                )
                val fileUri = getUriFromFile(this,photoFile)
                cropImage(fileUri)
            }
        }
    }

    /**
     * 执行剪裁图片操作
     * @param uri 原始图片文件uri
     * */
    private fun cropImage(uri: Uri){
        mCropImageLauncher.launch {
            cropFileUri = uri
            callBack = {clipFile->
                clipFile?.also {
                    showShortToast("剪裁成功${it.absoluteFile}")
                } ?: run{
                    showShortToast("剪裁失败")
                }
            }
        }
    }

    private fun performPickImage(){
        requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE){
            mPickContentLauncher.launchPickImage{uri->
                uri?:return@launchPickImage
//                showShortToast("选择了图片文件$uri")
                cropImage(uri)
//                    val localFilePath = getRealFilePathFromFileUri(this@MainActivity,uri)
//                    logd("pick content方式选择了图片 \n uri : ${uri}\n file : $localFilePath")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun performPickContact(){
        requestPermissions(Manifest.permission.READ_CONTACTS){
            mPickContentLauncher.launchPickContact{ uri->
                uri?:return@launchPickContact
                val pair = ContactsTools.queryContactPhoneFromUri(this@MainActivity,uri)
                showShortToast("选择了${pair?.first?:""}")
            }
        }
    }

    private fun performSelectImage(){
        requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE){
            mGetContentLauncher.launchImage{uriList->
                if (uriList.isNullOrEmpty()) return@launchImage
                showShortToast("选择了${uriList.size}张图片")
            }
        }
    }

    /***
     * 请求权限
     * @param permission
     * @param onSuccess 请求权限成功回调
     */
    private fun requestPermissions(vararg permission : String, onSuccess : ()->Unit){
        mRequestPermissionLauncher.launchRequest {
            permissions = arrayOf(*permission)
            isShowRationalDialogAfterDefined = true
            rationaleDialogFragment = mRationalDialogFragment
            forwardSettingDialogFragment = mNaviSettingsTipDialogFragment
            onAllGrantedAction = onSuccess
            onDeniedAction = {list->
                showShortToast("您需要允许权限才能继续")
            }
        }
    }
}