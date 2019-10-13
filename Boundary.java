package client;


import java.util.Arrays;


public class Boundary {
    public Boundary(Node start, Node end, int covervalue) {
        super();
        this.StartNode = start;
        this.EndNode = end;
        Find f = new Find();
        Double a = f.Angle(start.getScreenpoint(), end.getScreenpoint());
        Double a1 = a + 60;
        Double a2 = a + 120;
        if (a1 >= 360)
            a1 -= 360;
        if (a2 >= 360)
            a2 -= 360;
        int tolerance = 5;
        //Find out which neighbours this wall blocks.
        for (Node n : start.getNeighbours()) {
            if (n != null) {
                Double a3 = f.Angle(start.getScreenpoint(), n.getScreenpoint());
                if ((a3 >= a1 - tolerance && a3 < a1 + tolerance) || (a3 >= a2 - tolerance && a3 < a2 + tolerance)) {
                    int index;
                    Node[] ar1 = start.getNeighbours();
                    index = Arrays.asList(ar1).indexOf(n);
                    this.NeighboursBlocked = Arrays.copyOf(this.NeighboursBlocked, this.NeighboursBlocked.length + 1);
                    this.NeighboursBlocked[this.NeighboursBlocked.length - 1] = index;
                }
                if (a3 >= a - tolerance && a3 < a + tolerance) {
                    this.NeighbourInLine = Arrays.asList(start.getNeighbours()).indexOf(n);
                }
            }
        }
        int Size = this.StartNode.getSize();
        this.LengthByNodes = (int) Math.round(f.Distance(start.getScreenpoint(), end.getScreenpoint()) / Size);
        this.ExtendWall(covervalue);
        Map m = this.StartNode.getMap();
        m.addBoundary(this);
    }
    public Boundary (Node start, int direction, int nodecount, int coverval){
        //System.out.println("Creating boundary");
        this.StartNode = start;
        Node current = start;
        int count = nodecount;
        int dplus4 = direction + 4;
        int dplus5 = direction + 5;
        if (dplus4 > 5) dplus4 -= 6;
        if (dplus5 > 5) dplus5 -= 6;
        while(count > 0) {
            //System.out.println("Extending boundary");
            if (current != null) {
                Node ndp1 = current.getNeighbours()[dplus4];
                Node ndp2 = current.getNeighbours()[dplus5];
                if (ndp1 != null) {
                    CoverSection cs1 = new CoverSection(current, current.getNeighbours()[dplus4], coverval, coverval);
                }
                if (ndp2 != null) {
                    CoverSection cs2 = new CoverSection(current, current.getNeighbours()[dplus5], coverval, coverval);
                }
                this.EndNode = current;
                Node nd = current.getNeighbours()[direction];
                if (nd != null) {
                    current = nd;
                }
            }
            count -= 1;
        }
    }
    private Node StartNode;
    private Node EndNode;
    private int[] NeighboursBlocked = new int[0];
    private int NeighbourInLine;
    private int LengthByNodes;

    public Node getStartNode() {
        return this.StartNode;
    }

    public Node getEndNode() {
        return this.EndNode;
    }

    public void ExtendWall(int covervalue) {
        Node n1 = this.StartNode;
        int count = 0;
        while (n1 != this.getEndNode() && count < this.LengthByNodes) {
            for (int i : this.NeighboursBlocked) {
                Node n2 = n1.getNeighbours()[i];
                CoverSection cs = new CoverSection(n1, n2, covervalue, covervalue);
            }
            Node n3 = n1.getNeighbours()[this.NeighbourInLine];
            if (n3 != null)
                n1 = n3;
            count += 1;
        }
    }
}
