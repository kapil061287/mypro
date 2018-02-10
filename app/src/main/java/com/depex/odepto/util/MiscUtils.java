package com.depex.odepto.util;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class MiscUtils {
        public static final int HASH_SEED = 5381;

        public static boolean equals(Object a, Object b) {
            if (a == null) {
                return b == null;
            } else {
                return a.equals(b);
            }
        }

        public static boolean equalsNotNull(Object a, Object b) {
            return (a == null || b == null || !a.equals(b)) ? false : true;
        }

        public static boolean isNullOrEmpty(Object obj) {
            return obj == null || (((obj instanceof CharSequence) && ((CharSequence) obj).length() == 0) || (((obj instanceof Collection) && ((Collection) obj).isEmpty()) || ((obj instanceof Map) && ((Map) obj).isEmpty())));
        }



        public static String inputStreamToString(InputStream inputStream) {
            Scanner s = new Scanner(inputStream).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }

        public static boolean containsNotEquals(String stra, String strb) {
            return stra.contains(strb) && !stra.equals(strb);
        }

        public static double calculateDistance(float oldX, float oldY, float x, float y) {
            return Math.sqrt(Math.pow((double) (oldX - x), 2.0d) + Math.pow((double) (oldY - y), 2.0d));
        }

      /*  public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
            Map<String, String> query_pairs = new LinkedHashMap();
            for (String pair : url.getQuery().split("&")) {
                int idx = pair.indexOf(SimpleComparison.EQUAL_TO_OPERATION);
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
            return query_pairs;
        }*/

        public static boolean containsIgnoreCase(String str, String searchStr) {
            if (str == null || searchStr == null) {
                return false;
            }
            int len = searchStr.length();
            int max = str.length() - len;
            for (int i = 0; i <= max; i++) {
                if (str.regionMatches(true, i, searchStr, 0, len)) {
                    return true;
                }
            }
            return false;
        }

        public static CharSequence trim(CharSequence s) {
            if (s == null) {
                return null;
            }
            int len = s.length();
            int start = 0;
            while (start < len && s.charAt(start) <= ' ') {
                start++;
            }
            int end = len;
            while (end > start && s.charAt(end - 1) <= ' ') {
                end--;
            }
            return s.subSequence(start, end);
        }

        public static String multiply(String input, String delimiter, int times) {
            if (times < 0) {
                throw new IllegalArgumentException("times must be >=0, was " + times);
            } else if (times == 0) {
                return "";
            } else {
                if (times == 1) {
                    return input;
                }
                StringBuilder sb = new StringBuilder((input.length() * times) + (delimiter.length() * (times - 1)));
                sb.append(input);
                for (int a = 1; a < times; a++) {
                    sb.append(delimiter + input);
                }
                return sb.toString();
            }
        }
}
