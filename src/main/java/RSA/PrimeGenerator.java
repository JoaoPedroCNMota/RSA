package RSA;

import RSA.utils.Util;

import java.math.BigInteger;
import java.util.Random;

public class PrimeGenerator {

    private static final int MIN_BIT_LEN = 1024;
//    private static final int MAX_ERATOSTHENES_SIEVE_PRIMES = 100;
    private static final int MAX_RABIN_MILLER_TRIALS = 10;
    private static Random random = new Random();

    //https://acervolima.com/como-gerar-grandes-numeros-primos-para-algoritmo-rsa/
    //https://www.geeksforgeeks.org/how-to-generate-large-prime-numbers-for-rsa-algorithm/

    public BigInteger getRandomPrimeNumber(){
        BigInteger probablePrime = BigInteger.probablePrime(MIN_BIT_LEN, random);

        boolean isPrime = millerRabin(probablePrime.intValue());
        while (!isPrime){
            probablePrime = BigInteger.probablePrime(MIN_BIT_LEN, random);
            isPrime = millerRabin(probablePrime.intValue());
            System.out.println("NAO PRIMO WHILE");
        }

        return probablePrime;
    }

    /*Teste de primalidade de baixo nível,
    * verifica se provavel primo eh divisivel por seus N primeiros primos antecessores*/
//    private boolean lowLevelPrimeTest(BigInteger primeCandidate){
//        List<Integer> firstPrimes = eratosthenesSieve(primeCandidate);
//        for (Integer prime: firstPrimes) {
//            if (primeCandidate % prime == 0 && Math.pow(prime, 2) <= primeCandidate)
//                return false;
//        }
//        return true;
//    }

//    private List<Integer> eratosthenesSieve(BigInteger num){
//        boolean[] primes = new boolean[num.intValue() + 1];
//        for (int i = 0; i <= num.intValue(); i++) primes[i] = true;
//
//        for (int i = 2; i <= Math.sqrt(num.intValue()); i++){
//            // verifica se 'i' é primo
//            if (primes[i]){
//                // múltiplos de 'i' não são primos
//                for (int j = 2; i * j <= num.intValue(); j++) {
//                    primes[i * j] = false;
//                }
//            }
//        }
//
//        List<Integer> firstPrimeNumbers = new ArrayList<>();
//        for (int i = 2; i <= num.intValue() && i <= MAX_ERATOSTHENES_SIEVE_PRIMES; i++){
//            if (primes[i]) {
//                firstPrimeNumbers.add(i);
//            }
//        }
//        return firstPrimeNumbers;
//    }

    private boolean millerRabin(int prime){
        long until = prime - 1;

        while (until % 2 == 0) until /= 2;

        for (int i = 0; i < MAX_RABIN_MILLER_TRIALS; i++) {

            long r = Math.abs(random.nextLong());
            long a = r % (prime - 1) + 1;
            long temp = until;
            long mod = Util.modPow(a, temp, prime);

            while (temp != prime - 1 && mod != 1 && mod != prime - 1){
                mod = Util.mulMod(mod, mod, prime);
                temp *= 2;
            }
            if (mod != prime - 1 && temp % 2 == 0) return false;
        }
        return true;
    }

}

