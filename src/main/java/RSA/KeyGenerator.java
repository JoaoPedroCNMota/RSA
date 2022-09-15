package RSA;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KeyGenerator {

    private static final int MIN_BIT_LEN = 1024;
    private static final int MAX_ERATOSTHENES_SIEVE_PRIMES = 100;
    private static final int MAX_RABIN_MILLER_TRIALS = 10;

    private static Random random = new Random();

    //https://acervolima.com/como-gerar-grandes-numeros-primos-para-algoritmo-rsa/
    //https://www.geeksforgeeks.org/how-to-generate-large-prime-numbers-for-rsa-algorithm/

    public BigInteger generateRandomPrimeNumber(){
        BigInteger probablePrime = BigInteger.probablePrime(MIN_BIT_LEN, random);

        lowLevelPrimeTest(probablePrime.intValue());
        highLevelPrimeTest(probablePrime.intValue());
        return probablePrime;
    }

    /*Teste de primalidade de baixo nível,
    * verifica se provavel primo eh divisivel por seus N primeiros primos antecessores*/
    private boolean lowLevelPrimeTest(int primeCandidate){
        List<Integer> firstPrimes = eratosthenesSieve(primeCandidate);
        for (Integer prime: firstPrimes) {
            if (primeCandidate % prime == 0 && Math.pow(prime, 2) <= primeCandidate)
                return false;
        }
        return true;
    }

    /*Teste de primalidade de alto nível,
     **/
    private boolean highLevelPrimeTest(int primeCandidate){
        return true;
    }

    private List<Integer> eratosthenesSieve(int num){
        boolean[] primes = new boolean[num + 1];
        for (int i = 0; i <= num; i++) primes[i] = true;

        for (int i = 2; i <= Math.sqrt(num); i++){
            // verifica se 'i' é primo
            if (primes[i] == true){
                // múltiplos de 'i' não são primos
                for (int j = 2; i * j <= num; j++) {
                    primes[i * j] = false;
                }
            }
        }

        List<Integer> firstPrimeNumbers = new ArrayList<>();
        for (int i = 2; i <= num && i <= MAX_ERATOSTHENES_SIEVE_PRIMES; i++){
            if (primes[i] == true) {
                firstPrimeNumbers.add(i);
            }
        }
        return firstPrimeNumbers;
    }

    private List<Integer> millerRabin(){

    }

}
