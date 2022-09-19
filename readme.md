
João Pedro Correia Nogueira Mota – 170106144

Pedro Henrique Batista Nunes – 170153959 

## Especificações de Desenvolvimento e Execução
O trabalho foi desenvolvido em Java,
nas versões mais recentes do JDK e JRE 17,
utilizando o IntelliJ como IDE para desenvolvimento.

Para rodar o programa, é recomendado instalar o gerenciador de pacotes de 
projeto Maven, além de ter o Java 17 instalado e utilizar alguma 
IDE (Geralmente as IDEs possuem um comando Run para rodar arquivos java sem precisar de comandos via terminal.
Seguindo todas as recomendações acima, execute a classe RSA_Launcher para 
acessar a interface de usuário. 

## Classes

1 - Geração de chaves: A classe KeyGenerator.java gera as chaves públicas e privadas e tem como classe auxiliar a PrimeGenerator.java, que gera os pares de primos aleatórios e executa o teste de Miller-Rabin. 

2 - Cifração Simétrica de Mensagem e decifração, podem ser efetuadas pela classe AES.java. 

3 - Geração da Assinatura => Oaep.java, MaskFunction.java: realizam o cálculo de hash. da mensagem e aplicam a cifração do hash gerado (assinatura). 

4 - Verificação: Apenas a decifração do hash foi implementada na classe Oaep.java.