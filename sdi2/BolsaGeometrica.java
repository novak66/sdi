/** HelloWorld.java **/
import java.rmi.*;

public interface BolsaGeometrica extends Remote {
   public void endClient() throws RemoteException;
   public Boolean findItem(String item, int quantidade) throws Exception;
}
