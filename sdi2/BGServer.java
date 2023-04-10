/**
 * HelloServer.java
 **/

import java.io.*;
import java.rmi.*;
import java.net.*;
import java.util.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.concurrent.TimeUnit;

public class BGServer implements BolsaGeometrica {
    public BGServer() {
    }

    private static int NClientes;
    private static List<String> entregues;
    private static List<String> rejeitadas;
    private static String[] prateleiras;
    private static List<String> pecas;
    private static Registry registry;
    private static BolsaGeometrica stub;

    public static void setNroClient(int nclients) throws Exception {
        NClientes = nclients;
    }

    public static void setRejeitadas(String item) {
        if (rejeitadas == null) {
            rejeitadas = new ArrayList<>();
        }

        rejeitadas.add(item);
    }

    public static void setEntregues(String item) {
        if (entregues == null) {
            entregues = new ArrayList<>();
        }

        entregues.add(item);
    }

    private static void setPrateleiras(String[] prateleira) throws Exception {
        prateleiras = prateleira;
    }

    public static int getNroClient() throws Exception {
        return (NClientes);
    }

    public static void setPecas(String[] lpecas) throws Exception {
        pecas = new ArrayList<>(Arrays.asList(lpecas));
    }

    public static List<String> getPeca(int posicao) throws Exception {
        return (pecas.subList(2, pecas.size()));
    }

    public static String getPrateleiras(int index) throws Exception {
        return (prateleiras[index + 2]);
    }

    public static int getNroPecas() throws Exception {
        return (pecas.size() - 2);
    }

    public static void setServer() throws Exception {
        Scanner sc = new Scanner(System.in);
        boolean lendo = true;
        while (lendo) {
            if (!sc.hasNextLine()) {
                break;
            }
            String sCurrentLine = sc.nextLine();
            BufferedReader inFromUser
                    = new BufferedReader(new InputStreamReader(System.in));
            String[] word = sCurrentLine.split(" ");
            switch (word[0]) {
                case "NClientes":
                    setNroClient(Integer.parseInt(word[2]));
                    break;
                case "pecas":
                    setPecas(word);
                    break;
                case "prateleiras":
                    lendo = false;
                    break;
                default:
                    //System.out.println("Ignorado: ("+word[0]+")");
            }
        }

        sc.close();
    }

    public static void stopServer() {
        try {
            registry.unbind("myRMIBG");
            UnicastRemoteObject.unexportObject(registry, true);
        } catch (NoSuchObjectException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (RemoteException ex) {
        }

    }


    static void printLista(List<String> lista) {
        for(String s : lista) {
            if (s.matches("[A-Za-z]") && s.length() == 1) {
                System.out.print(s);
                System.out.print(" ");
            }
        }

    }

    static void printReport() {
        Collections.sort(pecas);

        System.out.println("##  Servidor  ##");
        System.out.println("Status: finalizado");
        System.out.print("estoque: ");
        printLista(pecas);
        System.out.println(' ');
        System.out.print("entregas: ");
        printLista(entregues);
        System.out.println(' ');
        System.out.print("rejeitadas: ");
        printLista(rejeitadas);
    }

    public Boolean findItem(String item, int quantidade) throws Exception {

        int qtd = 0;
        int j = 0;
        for (String peca : pecas) {
            if (peca.equals(item)) {
                pecas.set(j, "0");
                qtd++;
            }
            j++;
        }

        if (qtd == 0) {
            setRejeitadas(item);

            return false;
        }

        for (int i = 0; i < qtd; i++) {
            setEntregues(item);
        }

        setNroClient(getNroClient() - 1);

        return true;
    }

    public static void main(String[] args) {
        try {

            //System.out.println("Configurando servidor ...");
            setServer();
            // Instancia o objeto servidor e a sua stub
            BGServer server = new BGServer();
            //BolsaGeometrica stub = (BolsaGeometrica) UnicastRemoteObject.exportObject(server, 0);
            stub = (BolsaGeometrica) UnicastRemoteObject.exportObject(server, 0);
            // Registra a stub no RMI Registry para que ela seja obtAida pelos clientes
            registry = LocateRegistry.createRegistry(6600);
            //Registry registry = LocateRegistry.createRegistry(6600);
            //Registry registry = LocateRegistry.getRegistry(9999);
            registry.bind("myRMIBG", stub);
            //System.out.println("Servidor pronto:\n\t\tNroClientes:"+getNroClient()+"\n\t\tPecas:"+getNroPecas()+"\n");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Boolean flag = true;
        while (flag) { // Pooling aguardando clientes

            try {
                TimeUnit.SECONDS.sleep(1);
                if (getNroClient() <= 0) {
                    flag = false;
                    printReport();
                    stopServer();
                    break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        System.exit(0);

    }

    // Métodos disponíveis via RMI

    public void endClient() throws RemoteException {
        try {
            setNroClient(getNroClient() - 1);
            //System.out.println("Clientes ativos = "+this.getNroClient());
            if (this.getNroClient() == 0) {
                this.stopServer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
