package RSA;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class RSA_Launcher {

    private static Scanner scanner = new Scanner(System.in);
    private static KeyGenerator keyGenerator = new KeyGenerator();

    public static void main(String[] args) {
        int input;
        scanner.useLocale(Locale.US);
        do {
            init();
            input = scanner.nextInt();
            scanner.nextLine(); //clear scanner buffer;
            getUserOption(input);
        }while(input != 0);
    }

    private static void init(){
        System.out.println("=====================================================================");
        System.out.println("""
                 ___   ___     _       _   _   _  _   ___\s
                | _ \\ / __|   /_\\     | | | | | \\| | | _ )
                |   / \\__ \\  / _ \\    | |_| | | .` | | _ \\
                |_|_\\ |___/ /_/ \\_\\    \\___/  |_|\\_| |___/
                """);
        System.out.println("=====================================================================");
        System.out.println("1 - GERAR CHAVES");
        System.out.println("2 - CIFRAR");
        System.out.println("3 - DECIFRAR");
        System.out.println("0 - FECHAR");
        System.out.println("=====================================================================");
    }

    private static void clearConsole(){
        for (int i = 0; i < 30; i++) {
            System.out.println();
        }
    }

    private static void getUserOption(int option) {
        String userNavOption = "1";
        switch (option) {
            case 1:
                while (userNavOption.equalsIgnoreCase("1")) {
                    keyGenerator.generateKeys();
                    System.out.println("=====================================================================");
                    System.out.println("    [ 1 ]           - GERAR NOVAS CHAVES");
                    System.out.println("[QUALQUER TECLA]    - VOLTAR");
                    System.out.println("=====================================================================");
                    userNavOption = !scanner.next().equalsIgnoreCase("1") ? "0" : "1";
                    scanner.nextLine();
                    clearConsole();
                }
                break;

            case 2:
                while (userNavOption.equalsIgnoreCase("1")) {
                    System.out.println("LENDO CHAVE DOS ARQUIVOS GERADOS...");
                    List<String> publicKeyContent = keyGenerator.readKeyFromFile(true);
                    List<String> privateKeyContent = keyGenerator.readKeyFromFile(false);

                    System.out.println("=====================================================================");
                    System.out.println("    [ 1 ]           - CIFRAR NOVA MENSAGEM");
                    System.out.println("[QUALQUER TECLA]    - VOLTAR");
                    System.out.println("=====================================================================");
                    userNavOption = !scanner.next().equalsIgnoreCase("1") ? "0" : "1";
                    scanner.nextLine();
                    clearConsole();
                }
                break;

            case 0:
                scanner.close();
                break;

            default:
                init();
        }
    }

}
