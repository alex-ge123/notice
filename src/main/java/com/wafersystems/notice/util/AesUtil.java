package com.wafersystems.notice.util;

import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

/**
 * ClassName: AESUtil Description: 加密解密算法.
 * 
 * @author gelin
 */
@Slf4j
public final class AesUtil {

  private static final int OXFF = 0xFF;

  private static final int RADIX_HEX = 16;

  private static final String CHARSET = "utf-8";

  private AesUtil() {}

  /**
   * .加密.
   * 
   * @param content -
   * @param key -
   * @return -
   */
  public static String encrypt(String content, String key) {
    try {
      if (key == null) {
        log.error("Key为空null");
        return content;
      }
      // 判断Key是否为16位
      if (key.length() != 16) {
        log.error("sKey=" + key + "长度为：" + key.length() + "&Key长度不是16位");
        return content;
      }
      byte[] raw = key.getBytes(ConfConstant.DEFAULT_ENCODING);
      SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// "算法/模式/补码方式"
      IvParameterSpec iv =
          new IvParameterSpec("0102030405060708".getBytes(ConfConstant.DEFAULT_ENCODING));// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
      cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
      byte[] encrypted = cipher.doFinal(content.getBytes(ConfConstant.DEFAULT_ENCODING)); // parseByte2HexStr(result)
      return parseByte2HexStr(encrypted);
    } catch (UnsupportedEncodingException uex) {
      return null;
    } catch (Exception ex) {
      return null;
    }
  }

  /**
   * .解密.
   * 
   * @param content -
   * @param key -
   * @throws Exception 异常
   */
  public static String decrypt(String content, String key) throws Exception {
    // 判断Key是否正确
    if (key == null) {
      log.error("Key为空null");
      return content;
    }
    // 判断Key是否为16位
    if (key.length() != 16) {
      log.error("sKey=" + key + "长度为：" + key.length() + "&Key长度不是16位");
      return content;
    }
    byte[] raw = key.getBytes("ASCII");
    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    IvParameterSpec iv =
        new IvParameterSpec("0102030405060708".getBytes(ConfConstant.DEFAULT_ENCODING));
    cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
    byte[] encrypted1 = parseHexStr2Byte(content);
    byte[] original = cipher.doFinal(encrypted1);
    return new String(original, ConfConstant.DEFAULT_ENCODING);
  }

  /**
   * Title: parseByte2HexStr Description: 将二进制转换成16进制.
   * 
   * @param buf 二进制数组
   * @return String
   */
  public static String parseByte2HexStr(byte[] buf) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < buf.length; i++) {
      String hex = Integer.toHexString(buf[i] & OXFF);
      if (hex.length() == 1) {
        hex = '0' + hex;
      }
      sb.append(hex.toUpperCase());
    }
    return sb.toString();
  }

  /**
   * Title: parseHexStr2Byte Description: 将16进制转换为二进制.
   * 
   * @param hexStr 16进制数
   * @return byte[]
   */
  public static byte[] parseHexStr2Byte(String hexStr) {
    if (hexStr.length() < 1) {
      return null;
    }
    byte[] result = new byte[hexStr.length() / 2];
    for (int i = 0; i < hexStr.length() / 2; i++) {
      int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), RADIX_HEX);
      int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), RADIX_HEX);
      result[i] = (byte) (high * RADIX_HEX + low);
    }
    return result;
  }

  /**
   * Title: getKey Description: getkey.
   * 
   * @param strKey -
   * @return SecretKey
   */
  private static SecretKey getKey(String strKey) {
    try {
      KeyGenerator generator = KeyGenerator.getInstance("AES");
      SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
      secureRandom.setSeed(strKey.getBytes(ConfConstant.DEFAULT_ENCODING));
      generator.init(128, secureRandom);
      return generator.generateKey();
    } catch (UnsupportedEncodingException uex) {
      throw new RuntimeException("初始化密钥出现异常 ");
    } catch (Exception ex) {
      throw new RuntimeException("初始化密钥出现异常 ");
    }
  }

  /**
   * 用于安全生产获取用户ID认证.
   * 
   * @param content 用户ID
   * @param password 密码
   */
  public static String encrypt4Old(String content, String password) {
    try {
      SecretKey secretKey = getKey(password);
      byte[] enCodeFormat = secretKey.getEncoded();
      SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
      Cipher cipher = Cipher.getInstance("AES"); // 创建密码器
      byte[] byteContent = content.getBytes(CHARSET);
      cipher.init(Cipher.ENCRYPT_MODE, key); // 初始化
      byte[] result = cipher.doFinal(byteContent);
      return parseByte2HexStr(result); // 加密
    } catch (Exception exception) {
      log.error("encrypt4Old error", exception);
    }
    return null;
  }

  /**
   * BASE64加密.
   * 
   * @param content -
   * @return -
   */
  public static String encryptBase64(String content) throws Exception {
    try {
      return new BASE64Encoder().encode(content.getBytes(CHARSET));
    } catch (Exception exception) {
      log.error("加密失败", exception);
      throw exception;
    }
  }

  /**
   * BASE64解密.
   * 
   * @param content -
   * @return -
   */
  public static String decryptBase64(String content) {
    try {
      return new String(new BASE64Decoder().decodeBuffer(content), CHARSET);
    } catch (Exception exception) {
      log.error("解密失败", exception);
      return content;
    }
  }
  
//  /**
//   *测试主函数.
//   */
//  public static void main(String[] args) {
//    String test = decryptBase64("5oKo5aW9IFvnpZ0g5bCP5biFXSzmj5DphpLmgqjmnInkuIDku73mnInlhbNb"
//        + "5b6q546v5Lya6K6u5rWL6K+VMl3nmoTlj5bmtojpgJrnn6XjgILkvJrorq7lnLDngrnvvJpb5rWL6K+V"
//        + "XeS7peWPiuaXtumXtO+8mlsyMDE3LTAzLTE3IDE1OjAwIH4gMTU6MTVd44CC");
//    System.out.println(test);
//  }
}
