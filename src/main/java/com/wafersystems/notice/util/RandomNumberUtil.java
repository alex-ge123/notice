package com.wafersystems.notice.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * ClassName: RandomNumberUtil Description: 随机数工具类.
 * 
 * @author harvoo
 */
public class RandomNumberUtil {

  /**
   * Title: getRandomNumber Description: 产生数据数字.
   * 
   * @param number 位数
   * @return 随机数
   */
  public static String getRandomNumber(int number) {
    StringBuilder temp = new StringBuilder();
    // 使用SET以此保证写入的数据不重复
    Set<Integer> set = new HashSet<Integer>();
    // 随机数
    Random random = new Random();
    while (set.size() < number) {
      // nextInt返回一个伪随机数，它是取自此随机数生成器序列的、在 0（包括）
      // 和指定值（不包括）之间均匀分布的 int 值。
      set.add(random.nextInt(10));
    }
    // 使用迭代器
    Iterator<Integer> iterator = set.iterator();
    // 临时记录数据
    while (iterator.hasNext()) {
      temp = temp.append(iterator.next());
    }
    return temp.toString();
  }

  /**
   * Title: getRandom Description: 获取两个数之间的随机数.
   * 
   * @param min 最小数
   * @param max 最大数
   * @return 随机数
   */
  public static int getRandom(int min, int max) {
    return (int) (Math.random() * (max - min) + min);
  }

  /**
   * @param length -
   * @return string
   */
  public static String getCharAndNumr(int length) {
    StringBuilder val = new StringBuilder();

    Random random = new Random();
    for (int i = 0; i < length; i++) {
      String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字

      if ("char".equalsIgnoreCase(charOrNum)) { // 字符串
        int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; // 取得大写字母还是小写字母
        val = val.append(choice + random.nextInt(26));
      } else if ("num".equalsIgnoreCase(charOrNum)) { // 数字
        val = val.append(random.nextInt(10));
      }
    }
    return val.toString();
  }
}
