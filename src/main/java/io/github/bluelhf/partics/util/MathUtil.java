package io.github.bluelhf.partics.util;

import java.util.stream.IntStream;

public class MathUtil {
    public static int gcd(int a, int b) {
        int result = 0;
        while (b > 0) {
            int temp = b;
            b = a % b;
            a = temp;

            result = a;
        }
        return result;
    }
    public static int gcd(int... ints) {
        if (ints.length == 0) return 0;
        if (ints.length == 1) return ints[0];
        int result = gcd(ints[0], ints[1]);
        for (int i = 2; i < ints.length; i++) {
            result = gcd(result, ints[i]);
        }
        return result;
    }

    public static int gcd(IntStream stream) {
        return gcd(stream.toArray());
    }

    public static double round(double a, int places) {
        return Math.round(a * Math.pow(10, places)) / Math.pow(10, places);
    }

}
