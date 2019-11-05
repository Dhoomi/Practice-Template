package io.dhoom.util;

import io.kipes.*;
import java.util.*;

public class UtilString
{
    public static String[] splitString(final String s) {
        return s.split(kPractice.splitPattern.pattern());
    }
    
    public static String romanNumerals(int Int) {
        final LinkedHashMap<String, Integer> roman_numerals = new LinkedHashMap<String, Integer>();
        roman_numerals.put("M", 1000);
        roman_numerals.put("CM", 900);
        roman_numerals.put("D", 500);
        roman_numerals.put("CD", 400);
        roman_numerals.put("C", 100);
        roman_numerals.put("XC", 90);
        roman_numerals.put("L", 50);
        roman_numerals.put("XL", 40);
        roman_numerals.put("X", 10);
        roman_numerals.put("IX", 9);
        roman_numerals.put("V", 5);
        roman_numerals.put("IV", 4);
        roman_numerals.put("I", 1);
        String res = "";
        for (final Map.Entry entry : roman_numerals.entrySet()) {
            final int matches = Int / entry.getValue();
            res += repeat(entry.getKey(), matches);
            Int %= entry.getValue();
        }
        return res;
    }
    
    private static String repeat(final String s, final int n) {
        if (s == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; ++i) {
            sb.append(s);
        }
        return sb.toString();
    }
}
