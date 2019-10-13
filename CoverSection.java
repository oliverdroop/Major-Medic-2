package client;

import java.awt.Point;
import java.awt.image.BufferedImage;

import java.io.File;

import java.util.Arrays;

import javax.imageio.ImageIO;

public class CoverSection extends DrawableObject {
    public CoverSection(Node n1, Node n2, int value1, int value2) {
        super();
        this.setMap(n1.getMap());
        this.Nodes[0] = (n1);
        this.Nodes[1] = (n2);
        this.Values[0] = value1;
        this.Values[1] = value2;
        this.setDescription();
        Find f = new Find();
        Point midpoint = f.Middle(n1.getScreenpoint(), n2.getScreenpoint());
        this.setScreenpoint(midpoint);
        this.LoadImage();
        this.getMap().addDrawableObject(this);
        this.getMap().addCover(this);
        int direction1 = Arrays.asList(n1.getNeighbours()).indexOf(n2);
        int direction2 = Arrays.asList(n2.getNeighbours()).indexOf(n1);
        this.Nodes[0].addCoverDescription(direction1, value1);
        this.Nodes[1].addCoverDescription(direction2, value2);
        this.Initiate();
        //this.CalculateEnds();
        this.Nodes[0].addCoverSection(this);
        this.Nodes[1].addCoverSection(this);
        //this.Draw();
    }
    private Node[] Nodes = new Node[2];
    private int[] Values = new int[2];
    private String Description = "";
    private Point EndA;
    private Point EndB;

    public Node[] getNodes() {
        return this.Nodes;
    }

    public int[] getValues() {
        return this.Values;
    }

    public Point getEndA() {
        return this.EndA;
    }

    public Point getEndB() {
        return this.EndB;
    }

    public void setEndA(Point p) {
        this.EndA = p;
    }

    public void setEndB(Point p) {
        this.EndB = p;
    }

    public void setDescription() {
        int index = 0;
        while (index < 2) {
            if (this.Values[index] == 0)
                this.Description = "open";
            if (this.Values[index] == 1)
                this.Description = "low vegetation";
            if (this.Values[index] == 2)
                this.Description = "low bank";
            if (this.Values[index] == 3)
                this.Description = "medium vegetation";
            if (this.Values[index] == 4)
                this.Description = "medium wall";
            if (this.Values[index] == 5)
                this.Description = "high vegetation";
            if (this.Values[index] == 6)
                this.Description = "high wall";
            index += 1;
            if (index == 1)
                this.Description += " to ";
        }
    }

    public void LoadImage() {
        Node n1 = this.Nodes[0];
        Node n2 = this.Nodes[1];
        int neighbour1 = Arrays.asList(n1.getNeighbours()).indexOf(n2);
        int neighbour2 = Arrays.asList(n2.getNeighbours()).indexOf(n1);
        neighbour1 = Math.min(neighbour1, neighbour2);
        String path1 =
            n1.getMap().getGame().getRouteDirectory() + "Images\\Cover_Type" + this.Values[0] + "_" + neighbour1 +
            ".bmp";
        try {
            BufferedImage img = ImageIO.read(new File(path1));
            //
            //Colour white pixels transparent
            int width = img.getWidth();
            int height = img.getHeight();
            BufferedImage img2 = new BufferedImage(width, height, 2);
            for (int w = 0; w < width; w++) {
                for (int h = 0; h < height; h++) {
                    int c = img.getRGB(w, h);
                    if (c != 0xFFFFFFFF) {
                        img2.setRGB(w, h, c);
                    } else {
                        img2.setRGB(w, h, 0x00000000);
                    }
                }
            }
            //
            //this.setImage(img2);
            this.setImgRef("20" + this.Values[0] + neighbour1);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void Initiate() {
        Find f = new Find();
        Point nc1 = this.getNodes()[0].getScreenpoint();
        Point nc2 = this.getNodes()[1].getScreenpoint();
        Double a = f.Angle(nc1, nc2);
        int sec1;
        int sec2;
        sec1 = (int) Math.floor((a / (double) 360) * 6);
        if (sec1 >= 6)
            sec1 -= 6;
        //infer sec2 from sec1.
        sec2 = sec1 - 3;
        if (sec2 < 0)
            sec2 += 6;
        //end infer
        int[] cvrdesc1 = this.getNodes()[0].getCoverDescription();
        this.Values[0] = cvrdesc1[sec1];
        int[] cvrdesc2 = this.getNodes()[1].getCoverDescription();
        this.Values[1] = cvrdesc2[sec2];
        this.CalculateEnds(nc1, nc2);
    }

    public void CalculateEnds(Point nc1, Point nc2) {
        Find f = new Find();
        Point mid = f.Middle(nc1, nc2);
        double dist = f.Distance(nc1, nc2);
        int am1 = (int) Math.round(f.Angle(nc1, mid));
        int Size = this.getNodes()[0].getSize();
        double halfdist = dist / (double) 2;
        double sq1 = Math.pow(halfdist, 2);
        double sq2 = Math.pow((Size / (double) 4), 2);
        double Radius = Math.sqrt(sq1 + sq2);
        //35 adjusts for fitting into square grid
        //int ae1 = (am1 - 30);
        //int ae2 = (am1 + 30);
        double ae3d = (am1 + 90) * (Math.PI / (double) 180);
        double ae4d = (am1 - 90) * (Math.PI / (double) 180);
        //double ae1d = ae1 * (Math.PI / 180);
        //int endax = nc1.x + (int)Math.round((Math.sin(ae1d)) * Radius);
        //int enday = nc1.y - (int)Math.round((Math.cos(ae1d)) * Radius);
        //double ae2d = ae2 * (Math.PI / 180);
        //int endbx = nc1.x + (int)Math.round((Math.sin(ae2d)) * Radius);
        //int endby = nc1.y - (int)Math.round((Math.cos(ae2d)) * Radius);
        //
        int endax = mid.x + (int) Math.round((Math.sin(ae3d)) * ((Radius / 2) + 1));
        int enday = mid.y - (int) Math.round((Math.cos(ae3d)) * ((Radius / 2) + 1));
        int endbx = mid.x + (int) Math.round((Math.sin(ae4d)) * ((Radius / 2) + 1));
        int endby = mid.y - (int) Math.round((Math.cos(ae4d)) * ((Radius / 2) + 1));
        //
        Point enda;
        Point endb;
        enda = new Point(endax, enday);
        endb = new Point(endbx, endby);
        this.EndA = enda;
        this.EndB = endb;
    }

    public boolean Obstructs(Point p1, Point p2, int Threshold) {
        boolean outcome = false;
        Find f = new Find();
        Point p3 = this.getEndA();
        Point p4 = this.getEndB();
        if (f.IsCross(p1, p2, p3, p4)) {
            //below assumes both values are the same.
            if (this.getValues()[0] >= Threshold) {
                outcome = true;
            }
        }
        return outcome;
    }

    //public boolean getHasUniqueLocation(Point scrp) {
    //    boolean unq = true;
    //    for (CoverSection cs : this.getMap().getCover()) {
    //        if (cs.getScreenpoint() == scrp) {
    //            unq = false;
    //        }
    //    }
    //    return unq;
    //}
}
