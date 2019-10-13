package client;

import java.awt.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RouteSetter implements Runnable {
    private final Unit Movers;
    private final Node Destination;
    //private final Clip Clip;
    public RouteSetter(Unit u, Node dest) {
        super();
        this.Movers = u;
        this.Destination = dest;
        //play "uh"
        //this.Movers.getOwner().getGame().getSoundHolder().Play("0" + this.Movers.getVoiceReference() + "030");
    }

    @Override
    public void run() {
        Individual com = this.Movers.getCommander();
        Node dest = this.Destination;
        Map m = com.getMap();
        List<Node> nodelist1 = m.getCloseCoveredNodes(dest, this.Movers.getMaximumRadius());
        List<Node> moveablelist = new ArrayList<Node>();
        for (Node n : nodelist1) {
            if (m.LOMTest(dest, n)) {
                if (this.Movers.getFormation() == 0) {
                    //fully attatched formation
                    //is the final list empty?
                    if (moveablelist.size() > 0) {
                        //send individual to node adjacent to last
                        if (moveablelist.get(moveablelist.size() - 1).getIsAdjacent(n) == true) {
                            moveablelist.add(n);
                        }
                    } else {
                        //send individual to node next to destination
                        if (dest.getIsAdjacent(n) == true) {
                            moveablelist.add(n);
                        }
                        if (dest == n){
                            moveablelist.add(n);
                        }
                    }
                } 
                if (this.Movers.getFormation() == 1) {
                    //fully detatched formation
                    //is the final list empty?
                    //if (moveablelist.size() > 0) {
                    boolean adjc = false;
                    for (Node n2 : moveablelist) {
                        if (n2.getIsAdjacent(n) == true) {
                            //don't add n to moveablelist
                            adjc = true;
                        }
                        if (n2 == n){
                            adjc = true;
                        }
                    }
                    if (adjc == false) {
                        moveablelist.add(n);
                    }
                    if (dest == n) {
                        if (this.Movers.getCommander().getIsArmoured() == true) {
                            moveablelist.add(n);
                        }
                    }
                    //}
                }
            }
        }
        System.out.println("moveablelist is " + moveablelist.size() + " long.");
        int count = 0;
        //Play sound
        SoundHolder sh = this.Movers.getOwner().getGame().getSoundHolder();
        sh.StopAll();
        Random rnd = new Random();
        String ref = "0" + this.Movers.getVoiceReference() + "01" + rnd.nextInt(6);
        //sh.Play(ref);
        Player p = this.Movers.getOwner();
        Point p1 = this.Movers.getCommander().getScreenpoint();
        this.Movers.getOwner().getGame().getCommunicator().SendSound(p, p1, ref);
        //
        while (count < this.Movers.getMembers().size()) {
            Individual i = this.Movers.getMembers().get(count);
            Node n = null;
            if (i == this.Movers.getCommander()) {
                i.setDestination(this.Destination);
                n = this.Destination;
            } else {
                n = moveablelist.get(0);
                i.setDestination(n);
            }
            if (moveablelist.size() > 1) {
                if (moveablelist.contains(n)) {
                    moveablelist.remove(n);
                }
            }
            Node start = i.getPosition();
            List<Node> route = m.getRoute(start, n);
            i.setRoute(route);
            count += 1;
        }
        this.Movers.StartMove();
        this.Movers.setIsPlanningMove(false);
    }
}
