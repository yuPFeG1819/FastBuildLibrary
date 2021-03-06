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
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
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
import com.yupfeg.logger.ext.logd
import com.yupfeg.result.*
import com.yupfeg.result.permission.RequestPermissionLauncher
import com.yupfeg.result.permission.dialog.DefaultRationaleDialogFragment
import com.yupfeg.sample.R
import com.yupfeg.sample.TestWindowInsetActivity
import com.yupfeg.sample.databinding.ActivityMainBinding
import com.yupfeg.sample.ui.list.TestListActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    private val mViewModel : MainViewModel by viewModels()

    private val mRationalDialogFragment : DefaultRationaleDialogFragment
        by lazy(LazyThreadSafetyMode.NONE){
            DefaultRationaleDialogFragment(
                reason = "?????????????????????????????????",
                positiveText = "??????",
                negativeText = "??????",
                reasonTextColor = ContextCompat.getColor(this,android.R.color.black),
                tintColor = getThemeColor(R.attr.colorPrimary)
            )
        }

    private val mNaviSettingsTipDialogFragment : DefaultRationaleDialogFragment
        by lazy(LazyThreadSafetyMode.NONE){
            DefaultRationaleDialogFragment(
                reason = "????????????????????????????????????????????????",
                positiveText = "??????",
                negativeText = "??????",
                reasonTextColor = ContextCompat.getColor(this,android.R.color.black),
                tintColor = getThemeColor(R.attr.colorPrimary)
            )
        }

    private val mTestDialog : Dialog by lazy(LazyThreadSafetyMode.NONE){
        TestBindingDialog(this,this.lifecycle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(mViewModel.testUseCase)
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

    override fun onStop() {
        super.onStop()
        logd("onStop")
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

    /**????????????id*/
    override val layoutId: Int
        get() = R.layout.activity_main

    /**
     * ???????????????
     * * tip : ??????????????????????????????????????????????????????[setContentView]???
     * ??????????????????DataBinding?????????by??????????????????`bindingActivity`????????????DataBinding??????
     * ????????????`DataBindingUtil.setContentView<T>(this, layoutId)`
     * */
    override fun initView(savedInstanceState: Bundle?) {
        mBinding.apply {
            config = BindingConfig()
            viewModel = mViewModel
        }
        fitImmersiveStatusBar(isDarkText = false)
        //??????????????????????????????????????????padding
        mBinding.toolbarMain.fitToSystemStatusBar(true)
        this.window.decorView.post {
            logd("?????????????????????${this.getStatusBarHeight()} ?????????????????????${isNavigationBarVisible}")

        }
    }

    /**???????????????*/
    override fun initData() {
        mViewModel.eventSharedFlow.flowWithLifecycle(this.lifecycle,Lifecycle.State.STARTED)
            .onEach {
                showShortToast("show ${it}")
            }
            .launchIn(lifecycleScope)
    }

    /**
     * [Context]????????????????????????????????????????????????
     * @param attr ????????????
     */
    private fun Context.getThemeColor(vararg attr: Int): Int {
        val array: TypedArray = theme.obtainStyledAttributes(attr)
        val color = array.getColor(0, Color.TRANSPARENT)
        array.recycle()
        return color
    }

    inner class BindingConfig{

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
                showShortToast("????????????????????????")
            }
        }

        fun testResultApiStartActivity(){
            //?????????????????????????????????
            lifecycleScope.launch(Dispatchers.Main){
                val resultIntent = mTestResultActivityLauncher.launchAwaitIntentOrNull<TestResultApiActivity>()
                resultIntent?.extras?.also {
                    Toast.makeText(this@MainActivity,"???????????????${it["key"]}",Toast.LENGTH_SHORT).show()
                }
            }
            Toast.makeText(this@MainActivity,"???????????????????????????",Toast.LENGTH_SHORT).show()

            //??????????????????
//            mTestResultActivityLauncher.launch<TestResultApiActivity>{ resultIntent->
//                resultIntent?.extras?.also {
//                    Toast.makeText(this@MainActivity,"???????????????${it["key"]}",Toast.LENGTH_SHORT).show()
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

        /**???????????????????????????????????????*/
        fun selectImageFromGallery(){
            performSelectImage()
        }

        /**
         * ??????????????????
         * */
        fun naviToTestNestedScroll(){
            startActivity(Intent(this@MainActivity,TestNestedScrollActivity::class.java))
        }

        /**???????????????View*/
        fun naviToCustomView(){
            startActivity(Intent(this@MainActivity,TestCustomViewActivity::class.java))
        }

        /**?????????????????????*/
        fun naviTestList(){
            startActivity(Intent(this@MainActivity,TestListActivity::class.java))
        }

        fun showTestDialog(){
            mViewModel.sendSharedEvent("showDialog")
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
                    showShortToast("????????????")
                    return@launch
                }
                showShortToast("????????????$filePath")
                val photoFile = File(filePath)
                if (!photoFile.exists()){
                    showShortToast("????????????")
                    return@launch
                }
                //????????????????????????
                ImageLoader.updateSystemGallery(
                    this, photoFile.toString()
                )
                val fileUri = getUriFromFile(this,photoFile)
                cropImage(fileUri)
            }
        }
    }

    /**
     * ????????????????????????
     * @param uri ??????????????????uri
     * */
    private fun cropImage(uri: Uri){
        mCropImageLauncher.launch {
            cropFileUri = uri
            callBack = {clipFile->
                clipFile?.also {
                    showShortToast("????????????${it.absoluteFile}")
                } ?: run{
                    showShortToast("????????????")
                }
            }
        }
    }

    private fun performPickImage(){
        requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE){
            mPickContentLauncher.launchPickImage{uri->
                uri?:return@launchPickImage
//                showShortToast("?????????????????????$uri")
                cropImage(uri)
//                    val localFilePath = getRealFilePathFromFileUri(this@MainActivity,uri)
//                    logd("pick content????????????????????? \n uri : ${uri}\n file : $localFilePath")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun performPickContact(){
        requestPermissions(Manifest.permission.READ_CONTACTS){
            mPickContentLauncher.launchPickContact{ uri->
                uri?:return@launchPickContact
                val pair = ContactsTools.queryContactPhoneFromUri(this@MainActivity,uri)
                showShortToast("?????????${pair?.first?:""}")
            }
        }
    }

    private fun performSelectImage(){
        requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE){
            mGetContentLauncher.launchImage{uriList->
                if (uriList.isNullOrEmpty()) return@launchImage
                showShortToast("?????????${uriList.size}?????????")
            }
        }
    }

    /***
     * ????????????
     * @param permission
     * @param onSuccess ????????????????????????
     */
    private fun requestPermissions(vararg permission : String, onSuccess : ()->Unit){
        mRequestPermissionLauncher.launchRequest {
            permissions = arrayOf(*permission)
            isShowRationalDialogAfterDefined = true
            rationaleDialogFragment = mRationalDialogFragment
            forwardSettingDialogFragment = mNaviSettingsTipDialogFragment
            onAllGrantedAction = onSuccess
            onDeniedAction = {list->
                showShortToast("?????????????????????????????????")
            }
        }
    }
}