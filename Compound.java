package client;


import java.awt.Point;

import java.util.ArrayList;
import java.util.List;

public class Compound {
    private Map Map;
    private List<Boundary> Boundaries = new ArrayList<Boundary>();
    private Player Owner = null;
    private boolean Leavable = false;
    private Node CentralNode;
    private int Resource1Val;
    private int Resource2Val;
    public Compound(Point p0, Point p1, Point p2, Point p3, int coverval0, int coverval1, int coverval2, int coverval3,
                    Map mainmap) {
        super();
        //this.Map = mainmap;
        Node[] nodearray = new Node[4];
        nodearray[0] = this.Map.getClosestNode(p0);
        nodearray[1] = this.Map.getClosestNode(p1);
        nodearray[2] = this.Map.getClosestNode(p2);
        nodearray[3] = this.Map.getClosestNode(p3);
        Find f = new Find();
        int[] covervalues = new int[4];
        covervalues[0] = coverval0;
        covervalues[1] = coverval1;
        covervalues[2] = coverval2;
        covervalues[3] = coverval3;
        for (int count = 0; count < 4; count++) {
            int plus1 = count + 1;
            if (plus1 >= 4) {
                plus1 = 0;
            }
            Double a1 = f.Angle(nodearray[count].getScreenpoint(), nodearray[plus1].getScreenpoint());
            //System.out.println(a1.toString());
            int angle = (int) Math.round(a1);
            //System.out.println(angle);
            if (angle < 0)
                angle += 360;
            if (angle >= 360)
                angle -= 360;
            int Direction1 = (int) Math.floor(angle / (double) 60);
            //System.out.println(Direction1);
            List<Node> possiblenodes1 = new ArrayList<Node>();
            boolean endfound = false;
            possiblenodes1.add(nodearray[count]);
            while (endfound == false) {
                Node last = possiblenodes1.get(possiblenodes1.size() - 1);
                Node next = last.getNeighbours()[Direction1];
                Point tryend = nodearray[plus1].getScreenpoint();
                if (f.Distance(next.getScreenpoint(), tryend) < f.Distance(last.getScreenpoint(), tryend)) {
                    possiblenodes1.add(next);
                } else {
                    endfound = true;
                }
            }
            Node startnode = nodearray[count];
            Node endnode = possiblenodes1.get(possiblenodes1.size() - 1);
            //System.out.println(startnode.getScreenpoint().toString() + endnode.getScreenpoint().toString());
            Boundary b = new Boundary(startnode, endnode, covervalues[count]);
            this.Boundaries.add(b);
        }
        this.setCentralNode();
    }
    public Compound(Node n, int direction, String s, Map mainmap){
        int d0 = direction;
        int d1 = direction + 1;
        if (d1 > 5) d1 -= 6;
        int d2 = direction + 2;
        if (d2 > 5) d2 -= 6;
        int d3 = direction + 3;
        if (d3 > 5) d3 -= 6;
        int d4 = direction + 4;
        if (d4 > 5) d4 -= 6;
        int d5 = direction + 5;
        if (d5 > 5) d5 -= 6;
        Boundary b0 = null;
        Boundary b1 = null;
        Boundary b2 = null;
        Boundary b3 = null;
        Node current = n;
        Node n0 = current;
        Node n1 = current;
        Node n2 = current;
        Node n3 = current;
        if (s == "Farm1") {
            this.Resource1Val = 1;
            this.Resource2Val = 5;
            int count = 20;
            while(count > 0){
                if (current.getNeighbours()[d0] != null){
                    current = current.getNeighbours()[d0];
                    n1 = current;
                }
                count -= 1;
            }
            count = 15;
            while (count > 0) {
                if (current.getNeighbours()[d2] != null) {
                    current = current.getNeighbours()[d2];
                    n2 = current;
                }
                count -= 1;
            }
            count = 20;
            while (count > 0) {
                if (current.getNeighbours()[d3] != null) {
                    current = current.getNeighbours()[d3];
                    n3 = current;
                }
                count -= 1;
            }

            b0 = new Boundary(n0, d0, 20, 6);
            b1 = new Boundary(n1, d2, 15, 0);
            b2 = new Boundary(n2, d3, 20, 0);
            b3 = new Boundary(n3, d5, 15, 3);
            //Fill in with minor boundaries and add them directly to the map
            count = 14;
            current = n0;
            while (count > 0){
                if (current.getNeighbours()[d2] != null) {
                    current = current.getNeighbours()[d2];
                    if (current != null) {
                        Boundary b = new Boundary(current, d0, 18, 1);
                        mainmap.addBoundary(b);
                    }
                }
                count -= 1;
            }
        }
        if (s == "Farm2") {
            this.Resource1Val = 1;
            this.Resource2Val = 5;
            int count = 20;
            while (count > 0) {
                if (current.getNeighbours()[d0] != null) {
                    current = current.getNeighbours()[d0];
                    n1 = current;
                }
                count -= 1;
            }
            count = 15;
            while (count > 0) {
                if (current.getNeighbours()[d2] != null) {
                    current = current.getNeighbours()[d2];
                    n2 = current;
                }
                count -= 1;
            }
            count = 20;
            while (count > 0) {
                if (current.getNeighbours()[d3] != null) {
                    current = current.getNeighbours()[d3];
                    n3 = current;
                }
                count -= 1;
            }

            b0 = new Boundary(n0, d0, 20, 3);
            b1 = new Boundary(n1, d2, 15, 0);
            b2 = new Boundary(n2, d3, 20, 4);
            b3 = new Boundary(n3, d5, 15, 6);
            //Fill in with minor boundaries and add them directly to the map
            count = 19;
            current = n0;
            while (count > 0) {
                if (current.getNeighbours()[d0] != null) {
                    current = current.getNeighbours()[d0];
                    if (current != null) {
                        Boundary b = new Boundary(current, d2, 15, 1);
                        mainmap.addBoundary(b);
                    }
                }
                count -= 1;
            }
        }
        if (s == "Farm3") {
            this.Resource1Val = 1;
            this.Resource2Val = 5;
            int count = 12;
            while (count > 0) {
                if (current.getNeighbours()[d1] != null) {
                    current = current.getNeighbours()[d1];
                    n1 = current;
                }
                count -= 1;
            }
            count = 7;
            while (count > 0) {
                if (current.getNeighbours()[d3] != null) {
                    current = current.getNeighbours()[d3];
                    n2 = current;
                }
                count -= 1;
            }
            count = 12;
            while (count > 0) {
                if (current.getNeighbours()[d4] != null) {
                    current = current.getNeighbours()[d4];
                    n3 = current;
                }
                count -= 1;
            }
            b0 = new Boundary(n0, d1, 11, 4);
            b1 = new Boundary(n1, d3, 6, 4);
            b2 = new Boundary(n2, d4, 11, 4);
            b3 = new Boundary(n3, d0, 6, 4);
            count = 6;
            current = n0.getNeighbours()[2];
            while (count > 0) {
                if (current.getNeighbours()[d0] != null) {
                    current = current.getNeighbours()[d3];
                    if (current != null) {
                        Boundary b = new Boundary(current, d1, 12, 1);
                        mainmap.addBoundary(b);
                    }
                }
                count -= 1;
            }
        }
        if (s == "Farm4") {
            this.Resource1Val = 1;
            this.Resource2Val = 5;
            int count = 7;
            while (count > 0) {
                if (current.getNeighbours()[d1] != null) {
                    current = current.getNeighbours()[d1];
                    n1 = current;
                }
                count -= 1;
            }
            count = 11;
            while (count > 0) {
                if (current.getNeighbours()[d3] != null) {
                    current = current.getNeighbours()[d3];
                    n2 = current;
                }
                count -= 1;
            }
            count = 7;
            while (count > 0) {
                if (current.getNeighbours()[d4] != null) {
                    current = current.getNeighbours()[d4];
                    n3 = current;
                }
                count -= 1;
            }
            b0 = new Boundary(n0, d1, 6, 4);
            b1 = new Boundary(n1, d3, 10, 4);
            b2 = new Boundary(n2, d4, 6, 4);
            b3 = new Boundary(n3, d0, 10, 4);
            count = 10;
            current = n0.getNeighbours()[2];
            while (count > 0) {
                if (current.getNeighbours()[d3] != null) {
                    current = current.getNeighbours()[d3];
                    if (current != null) {
                        Boundary b = new Boundary(current, d1, 7, 1);
                        mainmap.addBoundary(b);
                    }
                }
                count -= 1;
            }
        }
        if (s == "Yard1"){
            this.Resource1Val = 6;
            this.Resource2Val = 1;
            int count = 10;
            while (count > 0) {
                if (current.getNeighbours()[d0] != null) {
                    current = current.getNeighbours()[d0];
                    n1 = current;
                }
                count -= 1;
            }
            count = 7;
            while (count > 0) {
                if (current.getNeighbours()[d2] != null) {
                    current = current.getNeighbours()[d2];
                    n2 = current;
                }
                count -= 1;
            }
            count = 10;
            while (count > 0) {
                if (current.getNeighbours()[d3] != null) {
                    current = current.getNeighbours()[d3];
                    n3 = current;
                }
                count -= 1;
            }

            b0 = new Boundary(n0, d0, 7, 6);
            b1 = new Boundary(n1, d2, 6, 6);
            b2 = new Boundary(n2, d3, 7, 6);
            b3 = new Boundary(n3, d5, 8, 6);
        }
        if (s == "Yard2"){
            this.Resource1Val = 4;
            this.Resource2Val = 1;
            int count = 4;
            while (count > 0) {
                if (current.getNeighbours()[d1] != null) {
                    current = current.getNeighbours()[d1];
                    n1 = current;
                }
                count -= 1;
            }
            count = 7;
            while (count > 0) {
                if (current.getNeighbours()[d3] != null) {
                    current = current.getNeighbours()[d3];
                    n2 = current;
                }
                count -= 1;
            }
            count = 4;
            while (count > 0) {
                if (current.getNeighbours()[d4] != null) {
                    current = current.getNeighbours()[d4];
                    n3 = current;
                }
                count -= 1;
            }

            b0 = new Boundary(n0, d1, 4, 6);
            b1 = new Boundary(n1, d3, 7, 0);
            b2 = new Boundary(n2, d4, 4, 6);
            b3 = new Boundary(n3, d0, 8, 6);
        }
        if (s == "Yard3"){
            this.Resource1Val = 5;
            this.Resource2Val = 1;
            int count = 5;
            while (count > 0) {
                if (current.getNeighbours()[d1] != null) {
                    current = current.getNeighbours()[d1];
                    n1 = current;
                }
                count -= 1;
            }
            count = 5;
            while (count > 0) {
                if (current.getNeighbours()[d3] != null) {
                    current = current.getNeighbours()[d3];
                    n2 = current;
                }
                count -= 1;
            }
            count = 5;
            while (count > 0) {
                if (current.getNeighbours()[d4] != null) {
                    current = current.getNeighbours()[d4];
                    n3 = current;
                }
                count -= 1;
            }

            b0 = new Boundary(n0, d1, 5, 6);
            b1 = new Boundary(n1, d3, 6, 6);
            b2 = new Boundary(n2, d4, 5, 6);
            b3 = new Boundary(n3, d0, 3, 6);
        }
        if (s == "Yard4"){
            this.Resource1Val = 5;
            this.Resource2Val = 1;
            int count = 5;
            while (count > 0) {
                if (current.getNeighbours()[d1] != null) {
                    current = current.getNeighbours()[d1];
                    n1 = current;
                }
                count -= 1;
            }
            count = 5;
            while (count > 0) {
                if (current.getNeighbours()[d3] != null) {
                    current = current.getNeighbours()[d3];
                    n2 = current;
                }
                count -= 1;
            }
            count = 5;
            while (count > 0) {
                if (current.getNeighbours()[d4] != null) {
                    current = current.getNeighbours()[d4];
                    n3 = current;
                }
                count -= 1;
            }
            b0 = new Boundary(n0, d1, 5, 6);
            b1 = new Boundary(n1, d3, 4, 6);
            b2 = new Boundary(n2, d4, 6, 0);
            b3 = new Boundary(n3, d0, 6, 0);
        }
        if (s == "Yard5") {
            this.Resource1Val = 5;
            this.Resource2Val = 1;
            int count = 5;
            while (count > 0) {
                if (current.getNeighbours()[d1] != null) {
                    current = current.getNeighbours()[d1];
                    n1 = current;
                }
                count -= 1;
            }
            count = 5;
            while (count > 0) {
                if (current.getNeighbours()[d3] != null) {
                    current = current.getNeighbours()[d3];
                    n2 = current;
                }
                count -= 1;
            }
            count = 5;
            while (count > 0) {
                if (current.getNeighbours()[d4] != null) {
                    current = current.getNeighbours()[d4];
                    n3 = current;
                }
                count -= 1;
            }
            b0 = new Boundary(n0, d1, 6, 6);
            b1 = new Boundary(n1, d3, 6, 6);
            b2 = new Boundary(n2, d4, 6, 0);
            b3 = new Boundary(n3, d0, 6, 0);
        }
        if (s == "Yard6") {
            this.Resource1Val = 5;
            this.Resource2Val = 1;
            int count = 5;
            while (count > 0) {
                if (current.getNeighbours()[d1] != null) {
                    current = current.getNeighbours()[d1];
                    n1 = current;
                }
                count -= 1;
            }
            count = 5;
            while (count > 0) {
                if (current.getNeighbours()[d3] != null) {
                    current = current.getNeighbours()[d3];
                    n2 = current;
                }
                count -= 1;
            }
            count = 5;
            while (count > 0) {
                if (current.getNeighbours()[d4] != null) {
                    current = current.getNeighbours()[d4];
                    n3 = current;
                }
                count -= 1;
            }
            b0 = new Boundary(n0, d1, 6, 0);
            b1 = new Boundary(n1, d3, 6, 0);
            b2 = new Boundary(n2, d4, 4, 6);
            b3 = new Boundary(n3, d0, 4, 6);
        }
        if (s == "Yard7") {
            this.Resource1Val = 5;
            this.Resource2Val = 1;
            int count = 5;
            while (count > 0) {
                if (current.getNeighbours()[d1] != null) {
                    current = current.getNeighbours()[d1];
                    n1 = current;
                }
                count -= 1;
            }
            count = 5;
            while (count > 0) {
                if (current.getNeighbours()[d3] != null) {
                    current = current.getNeighbours()[d3];
                    n2 = current;
                }
                count -= 1;
            }
            count = 5;
            while (count > 0) {
                if (current.getNeighbours()[d4] != null) {
                    current = current.getNeighbours()[d4];
                    n3 = current;
                }
                count -= 1;
            }
            b0 = new Boundary(n0, d1, 6, 0);
            b1 = new Boundary(n1, d3, 6, 0);
            b2 = new Boundary(n2, d4, 6, 6);
            b3 = new Boundary(n3, d0, 6, 6);
        }
        this.Boundaries.add(b0);
        this.Boundaries.add(b1);
        this.Boundaries.add(b2);
        this.Boundaries.add(b3);
        this.setMap(mainmap);
        this.setOwner(null);
        this.setCentralNode();
    }

    public List<Boundary> getBoundaries() {
        return this.Boundaries;
    }
    public void setMap(Map m){
        this.Map = m;
        m.addCompound(this);
        for (Boundary b : this.getBoundaries()){
            m.addBoundary(b);
        }
    }
    public Player getOwner(){
        return this.Owner;
    }
    public void setOwner(Player p){
        this.Owner = p;
    }
    public boolean getLeavable(){
        return this.Leavable;
    }
    public void setLeavable(boolean bool){
        this.Leavable = bool;
    }
    public int getResource1Val(){
        return this.Resource1Val;
    }
    public int getResource2Val(){
        return this.Resource2Val;
    }
    public List<Individual> getIndividualsIn(){
        List<Individual> output = new ArrayList<Individual>();
        for (Player p : this.getMap().getGame().getPlayers()) {
            for (Individual i : p.getIndividuals()) {
                if (output.contains(i) == false) {
                    if (this.Contains(i.getScreenpoint())) {
                        if (i.getHealth() > 0) {
                            output.add(i);
                        }
                    }
                }
            }
        }
        return output;
    }
    public Map getMap(){
        return this.Boundaries.get(0).getStartNode().getMap();
    }
    public List<Player> getPlayersIn(){
        List<Player> output = new ArrayList<Player>();
        for (Individual i : this.getIndividualsIn()){
            if (output.contains(i.getOwner()) == false){
                output.add(i.getOwner());
            }
        }
        return output;
    }
    public void ColourForPlayer(Player p){
        Node n = this.getCentralNode();
        if (this.getOwner() == p) {
            n.setImgRef("1800");
        } else {
            if (this.getOwner() != null) {
                n.setImgRef("1900");
            } else {
                n.setImgRef("1200");
            }
        }
    }
    public boolean Contains(Point p) {
        Point p0 = this.getBoundaries().get(0).getStartNode().getScreenpoint();
        Point p1 = this.getBoundaries().get(1).getStartNode().getScreenpoint();
        Point p2 = this.getBoundaries().get(2).getStartNode().getScreenpoint();
        Point p3 = this.getBoundaries().get(3).getStartNode().getScreenpoint();
        boolean b0 = false;
        boolean b1 = false;
        boolean b2 = false;
        boolean b3 = false;
        Find f = new Find();
        if (f.IsBetween(p0, p1, p3, p))
            b0 = true;
        if (f.IsBetween(p1, p2, p0, p))
            b1 = true;
        if (f.IsBetween(p2, p3, p1, p))
            b2 = true;
        if (f.IsBetween(p3, p0, p2, p))
            b3 = true;
        boolean output = false;
        if (b0 == true && b1 == true && b2 == true && b3 == true)
            output = true;
        return output;
    }
    public Node getCentralNode(){
        return this.CentralNode;
    }
    public void setCentralNode(){
        Node n = this.getBoundaries().get(0).getStartNode();
        Find f = new Find();
        Point p1 = this.getBoundaries().get(0).getStartNode().getScreenpoint();
        Point p2 = this.getBoundaries().get(2).getStartNode().getScreenpoint();
        Point p3 = f.Middle(p1, p2);
        n = n.getMap().getClosestNode(p3);
        this.CentralNode = n;
        System.out.println("central node set");
    }
    public boolean IsObjective(){
        boolean rslt = false;
        Map m = this.getMap();
        if (m.getObjectives().contains(this)){
            rslt = true;
        }
        return rslt;
    }
}
