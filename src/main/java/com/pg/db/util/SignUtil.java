package com.pg.db.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by zah on 2018/8/29.
 */
public class SignUtil {
    public static String MD5(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    strBuf.append("0").append(
                            Integer.toHexString(0xff & encryption[i]));
                } else {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }
            String md = strBuf.toString();
            System.out.println("md5 is " + md);
            return strBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String sign(Map<String, String> map, String dwbkey) {

        ArrayList<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(
                map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> mapping1,
                               Map.Entry<String, String> mapping2) {
                return mapping1.getKey().compareTo(mapping2.getKey());
            }
        });
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> mapping : list) {
            if ("sign".equals(mapping.getKey())) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(mapping.getKey());
            sb.append("=");
            sb.append(mapping.getValue());
        }

        return MD5(sb.toString() + dwbkey);
    }
}
