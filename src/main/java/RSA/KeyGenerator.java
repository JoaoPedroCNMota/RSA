package RSA;

import RSA.utils.Util;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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

    public List<String> readKeyFromFile(boolean isPublic){
        List<String> content = new ArrayList<>();
        File file = new File(isPublic ? FILE_PATH_PUBLIC_KEY : FILE_PATH_PRIVATE_KEY);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while(line != null){
                content.add(line);
                line = reader.readLine();
            }
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
