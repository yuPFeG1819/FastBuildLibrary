package com.yupfeg.sample.data

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 *
 * @author yuPFeG
 * @date
 */
data class MainViewState(
    val score: Int = 0,
    val previousHighScore: Int = 150
) {

}

class TestMainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {

        }
    }
}