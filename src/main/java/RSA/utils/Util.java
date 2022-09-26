package RSA.utils;

import java.math.BigInteger;

public class Util {

    //(a ^ b) % c
    public static long modPow(long a, long b, long c){
        long res = 1;
        for (int i = 0; i < b; i++){
            res *= a;
            res %= c;
        }
        return res % c;
    }

    //(a * b) % c
    public static long mulMod(long a, long b, long mod){
        return BigInteger.valueOf(a)
                .multiply(BigInteger.valueOf(b))
                .mod(BigInteger.valueOf(mod))
                .longValue();
    }

    //MDC para BigInteger
    public static BigInteger gcdEuclides(BigInteger a, BigInteger b){
        if( b.equals(BigInteger.ZERO) ){
            return a;
        }
        return gcdEuclides(b, a.mod(b));
    }
}
