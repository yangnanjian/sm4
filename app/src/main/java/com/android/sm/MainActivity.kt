package com.android.sm

import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.sm.device_id.DeviceUtils
import com.android.sm.sm4.Sm4Util
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils
import org.bouncycastle.util.encoders.Hex
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {
    private var data: String = "qTzdNF/kPdZNTbMVLQSbsw=="
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DeviceUtils.getUniqueDeviceId()
        DeviceUtils.getAndroidID()
        val decode: ByteArray = Base64.decode(data, Base64.DEFAULT)
        val s2 = ByteUtils.toHexString(decode)
        // 解密
        val input = Hex.decode(s2)
        var output: ByteArray? = ByteArray(0)
        try {
            output = Sm4Util.decryptEcbPkcs5Padding(input, Sm4Util.getKey(
                Sm4Util.KEY))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val s = String(output!!, StandardCharsets.UTF_8)
        Log.e("s==", s)
    }

    private fun decryptData(value: String, key: String): String {
        var decryptValue = ""
        val decode: ByteArray = Base64.decode(value, Base64.DEFAULT)
        val s2 = ByteUtils.toHexString(decode)
        // 解密
        val input = Hex.decode(s2)
        var output: ByteArray? = ByteArray(0)
        try {
            output = Sm4Util.decryptEcbPkcs5Padding(input, Sm4Util.getKey(key))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        decryptValue = String(output!!, StandardCharsets.UTF_8)
        return decryptValue

    }
}