package metier.reseau;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import controleur.Controleur;
import metier.Metier;


public class Client 
{
    // Socket 
    private String ip;
    private int port;
    private Socket socket;
    private boolean connecte;

    private ClientServerHandler csh;
    private Controleur ctrl;
    private Thread t;
    
    public Client(String ip, Controleur ctrl)
    {
        this.ctrl = ctrl;
        this.ip = ip;
        this.port = 5000;
        this.connecte = false;
    }

    public void majPartie()
    {
        this.csh.majPartie();
    }

    public boolean passTest(String password) throws IOException
    {
        this.socket = new Socket(this.ip, this.port);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        out.writeUTF("PASS_TEST");
        out.writeUTF(password);
        out.flush();

        Boolean b = in.readUTF().equals("OK");

        socket.close();

        return b;
    }

    public void Disconnect()
    {
        this.csh.Disconnect();
        // stop thread
        t.interrupt();
        // close socket
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.ctrl.majIHM();
    }


    /*
     * Connexion au serveur
     * @return true si la connexion a réussi, false sinon
     */
    public boolean Connect(String password)
    {
        try
        {
            this.socket = new Socket(this.ip, this.port);
            this.connecte = true;
            this.csh = new ClientServerHandler(this.ctrl, this.socket, password);
            this.t = new Thread(csh);
            t.start();
            
        }
        catch(Exception e)
        {
            System.out.println("Erreur lors de la connexion au serveur");
        }
        return this.connecte;
    }

}
