package com.android.mylibrary.sm4;

import android.util.Base64;
import android.util.Log;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * created by：yang22
 * on 2023/8/4 18:59
 */
public class Sm4Util {

    private static final String ALGORITHM_NAME = "SM4";
    public static final String KEY = "wukong";
    private static final String ALGORITHM_ECB_PKCS5PADDING = "SM4/ECB/PKCS5Padding";

    /**
     * SM4算法目前只支持128位（即密钥16字节）
     */
    private static final int DEFAULT_KEY_SIZE = 128;

    static {
        // 防止内存中出现多次BouncyCastleProvider的实例
        if (null == Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }


    /**
     * 生成密钥
     * <p>建议使用org.bouncycastle.util.encoders.Hex将二进制转成HEX字符串</p>
     *
     * @return 密钥16位
     * @throws Exception 生成密钥异常
     */
    public static byte[] generateKey() throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM_NAME, BouncyCastleProvider.PROVIDER_NAME);
        kg.init(DEFAULT_KEY_SIZE, new SecureRandom());
        return kg.generateKey().getEncoded();
    }

    /**
     * Hex转byte[]，两种情况，Hex长度为奇数最后一个字符会被舍去
     */
    public static byte[] hexTobytes(String hex) {
        if (hex.length() < 1) {
            return null;
        } else {
            byte[] result = new byte[hex.length() / 2];
            int j = 0;
            for (int i = 0; i < hex.length(); i += 2) {
                result[j++] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
            }
            return result;
        }
    }

    /**
     * 加密，SM4-ECB-PKCS5Padding
     *
     * @param data 要加密的明文
     * @param key  密钥16字节，使用Sm4Util.generateKey()生成
     * @return 加密后的密文
     * @throws Exception 加密异常
     */
    public static byte[] encryptEcbPkcs5Padding(byte[] data, byte[] key) throws Exception {
        return sm4(data, key, ALGORITHM_ECB_PKCS5PADDING, null, Cipher.ENCRYPT_MODE);
    }

    /**
     * 解密，SM4-ECB-PKCS5Padding
     *
     * @param data 要解密的密文
     * @param key  密钥16字节，使用Sm4Util.generateKey()生成
     * @return 解密后的明文
     * @throws Exception 解密异常
     */
    public static byte[] decryptEcbPkcs5Padding(byte[] data, byte[] key) throws Exception {
        return sm4(data, key, ALGORITHM_ECB_PKCS5PADDING, null, Cipher.DECRYPT_MODE);
    }

    /**
     * SM4对称加解密
     *
     * @param input   明文（加密模式）或密文（解密模式）
     * @param key     密钥
     * @param sm4mode sm4加密模式
     * @param iv      初始向量(ECB模式下传NULL)
     * @param mode    Cipher.ENCRYPT_MODE - 加密；Cipher.DECRYPT_MODE - 解密
     * @return 密文（加密模式）或明文（解密模式）
     * @throws Exception 加解密异常
     */
    private static byte[] sm4(byte[] input, byte[] key, String sm4mode, byte[] iv, int mode)
            throws Exception {
        IvParameterSpec ivParameterSpec = null;
        if (null != iv) {
            ivParameterSpec = new IvParameterSpec(iv);
        }
        SecretKeySpec sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
        Cipher cipher = Cipher.getInstance(sm4mode, new BouncyCastleProvider());
        if (null == ivParameterSpec) {
            cipher.init(mode, sm4Key);
        } else {
            cipher.init(mode, sm4Key, ivParameterSpec);
        }
        return cipher.doFinal(input);
    }

    public static byte[] getKey(String key) {
        byte[] keyData = key.getBytes();
        int p = 16 - key.length() % 16;
        String inputHex = Util.bytesToHexString(keyData) + "00";
        StringBuffer stringBuffer = new StringBuffer(inputHex);
        for (int i = 0; i < p - 1; i++) {
            stringBuffer.append("00");
        }
        int length3 = stringBuffer.toString().length();
        byte[] ret = Util.hexToByte(stringBuffer.toString());
        return ret;
    }

    public static String decryptData(String value, String key) {
        String decryptValue = "";
        byte[] decode = Base64.decode(value, Base64.DEFAULT);
        String s2 = ByteUtils.toHexString(decode);
        // 解密
        byte[] input = Hex.decode(s2);
        byte[] output = new byte[0];
        try {
            output = Sm4Util.decryptEcbPkcs5Padding(input, Sm4Util.getKey(key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        decryptValue = new String(output, StandardCharsets.UTF_8);
        Log.e("s==", decryptValue);
        return decryptValue;
    }
}
