/** HelloClient.java **/
import java.io.*;
import java.util.*;
import java.rmi.registry.*;
import java.net.InetAddress;

public class Client {

  public static void main(String[] args) {

    String host = (args.length < 1) ? null : args[0];
    String[] clie1 = new String[0];
    int qtdclie1 = 0;
    String[] clie2 = new String[0];
    int qtdclie2 = 0;

    try {
      Scanner sc = new Scanner(System.in);
      String sCurrentLine = sc.nextLine();
      BufferedReader inFromUser
              = new BufferedReader(new InputStreamReader(System.in));

      String[] word = sCurrentLine.split(" ");

      switch (word[0]) {
        case "pCli1":
          clie1 = word;
          break;
        case "QtdCli1":
          qtdclie1 = Integer.parseInt(word[2]);
          break;
        case "QtdCli2":
          qtdclie2 = Integer.parseInt(word[2]);
          break;
        case "pCli2":
          clie2 = word;
          break;

        default:
          //System.out.println("Ignorado: ("+word[0]+")");
      }


      Registry registry = LocateRegistry.getRegistry(host,6600);

      // Obtém a stub do servidor
      BolsaGeometrica stub= (BolsaGeometrica) registry.lookup("myRMIBG");

      InetAddress addr = InetAddress.getLocalHost();
      String hostname = addr.getHostName();
      System.out.println("##  Cliente ("+hostname+") "+args[1]+"  ##");

      Boolean achou = false;
      if(clie1.length > 0) {
        achou = stub.findItem(clie1[2], qtdclie1);
      }

      if(clie2.length > 0) {
       achou = stub.findItem(clie2[2], qtdclie2);
      }


      if (!achou) {
        System.out.println("Status: nao atendido ");
        System.out.print("pCli: ");
        if(clie1.length > 0) {
          System.out.println(clie1[2]);
        } else {
          System.out.println(clie2[2]);
        }

      } else {
        System.out.print("Status: atendido ");
        System.out.println(hostname);
        System.out.print("pCli: ");
        if(clie1.length > 0) {
          System.out.println(clie1[2]);
        } else {
          System.out.println(clie2[2]);
        }
      }

      System.out.println("###########");

      // Chama o método do servidor e imprime a mensagem
      stub.endClient();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
