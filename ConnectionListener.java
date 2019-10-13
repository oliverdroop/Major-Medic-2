package client;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionListener implements Runnable {
    private Communicator Communicator;
    private ServerSocket skt1;
    private Socket skt3 = null;
    private ServerSocket skt5;
    private Socket skt7 = null;
    private int BasePort = 27000;
    public ConnectionListener(Communicator communicator) {
        super();
        this.Communicator = communicator;
        int port = this.BasePort;
        try{
            this.skt1 = new ServerSocket(port);
            System.out.println("Created server socket");
        }
        catch(Exception e){
            System.out.println("Problem with ServerSocket at ConnectionListener");
            e.getMessage();
        }
    }

    @Override
    public void run() {
        try {
            int pnum = 0;
            Player n00b = null;
            while (this.skt3 == null) {
                skt3 = this.skt1.accept();
                //skt1.close();
                Player p = new Player(this.Communicator.getGame());
                p.setSocket(skt3);
                //p.getTimerFOW().start();
                //p.startTimerFOW();
                pnum = this.Communicator.getGame().getPlayers().indexOf(p);
                System.out.println("Player " + pnum + " joined");
                n00b = p;
                //Set port number
                int newp = BasePort + pnum + 1;
                //Set up new ServerSocket just for this player.
                skt5 = new ServerSocket(newp);
                //Send details of port migration
                System.out.println("Sending secondary socket port number: " + newp);
                this.Communicator.RequestPortMigration(n00b, newp);
                //p.getTimerGraphics().start();
                while (this.skt7 == null) {
                    this.skt7 = skt5.accept();
                    skt5.close();
                    System.out.println("Accepted secondary socket from player " + pnum);
                    n00b.setSocket(skt7);

                }
                this.skt7 = null;
                this.skt3.close();
                this.skt3 = null;
            }
            
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        //this.Communicator.ListenForNewSocket();
    }
}
