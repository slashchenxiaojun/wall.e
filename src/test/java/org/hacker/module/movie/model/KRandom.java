package org.hacker.module.movie.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class KRandom {
  public static String randomStr(int size) {
    return UUID.randomUUID().toString().replace("-", "").substring(0, size);
  }
  
  public static Integer randomInt(int size) {
    Random random = new Random();
    Integer irandom = Integer.parseInt((random.nextInt() + "").substring(1, size + 1));
    return irandom;
  }
  
  public static BigDecimal randomBigDecimal(String pattern) {
    Random random = new Random();
    BigDecimal bd = new BigDecimal(random.nextDouble() * 10000);
    DecimalFormat df = new DecimalFormat(pattern);
    bd = BigDecimal.valueOf(Double.parseDouble(df.format(bd)));
    return bd;
  }
  
  public static Date randomDate() {
    Random random = new Random();
    Long lrandom = Long.parseLong((random.nextLong() + "").substring(1, 14)); 
    return new Date(lrandom);
  }
  
  public static Boolean randomBoolean() {
    Random random = new Random();
    return random.nextInt() > 0;
  }
  
//  public static void main(String[] args) {
//    for(int i = 0; i < 10; i++) {
//      System.out.println(KRandom.randomStr(8));
//      System.out.println(KRandom.randomInt(6));
//      System.out.println(DateKit.Format(KRandom.randomDate(), "yyyy-MM-dd HH:mm:ss"));
//      System.out.println(KRandom.randomBigDecimal("#.###"));
//      System.out.println(KRandom.randomBoolean());
//    }
//  }
}
