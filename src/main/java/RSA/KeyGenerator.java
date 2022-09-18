package RSA;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;

public class KeyGenerator {

    private static final String FILE_PATH_PUBLIC_KEY = "public_key.txt";
    private static final String FILE_PATH_PRIVATE_KEY = "private_key.txt";
    private static final PrimeGenerator primeGenerator = new PrimeGenerator();

    public void generateKeys(){
        BigInteger p = primeGenerator.getRandomPrimeNumber();
        BigInteger q = primeGenerator.getRandomPrimeNumber();

        BigInteger n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        BigInteger e;
        do{
             e = primeGenerator.getRandomPrimeNumber();
        }while(e.compareTo(phi) >= 0 || !Util.gcdEuclides(e, phi).equals(BigInteger.ONE));
        BigInteger d = e.modInverse(phi);


        writeKeyInFile(n, e, FILE_PATH_PUBLIC_KEY); //geracao de chave publica
        writeKeyInFile(n, d, FILE_PATH_PRIVATE_KEY); //geracao de chave privada

        System.out.println("CHAVES CRIADAS COM SUCESSO.");
    }

    private void writeKeyInFile(BigInteger n, BigInteger key, String path) {
        generateFiles();
        try{
            PrintWriter writer = new PrintWriter(path);
            writer.println(n);
            writer.println(key);
            writer.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateFiles(){
        try{
            new File(FILE_PATH_PUBLIC_KEY).createNewFile();
            new File(FILE_PATH_PRIVATE_KEY).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
