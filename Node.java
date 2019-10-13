package client;

import java.awt.Point;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class Node{
    public Node() {
        super();
        this.gridx = 50;
        this.gridy = 50;
        this.setScreenpoint(new Point(this.gridx, this.gridy));
        this.CoverDescription[0] = 0;
        this.CoverDescription[1] = 0;
        this.CoverDescription[2] = 0;
        this.CoverDescription[3] = 0;
        this.CoverDescription[4] = 0;
        this.CoverDescription[5] = 0;
    }
    //private String RouteDirectory = "D:\\Program Files\\Major Medic 2\\";
    private int gridx;
    private int gridy;
    private Point Screenpoint;
    private int Size = 18;
    private Map Map;
    private BufferedImage Image;
    private String ImgRef;
    private Node[] Neighbours = new Node[6];
    private int[] CoverDescription = new int[6];
    private int CoverTotal;
    private List<CoverSection> CoverSections = new ArrayList<CoverSection>();
    private double GScore;
    private double FScore;
    private Node PreviousNode;
    private List<Point> PointsInside;

    public int getSize() {
        return this.Size;
    }

    public int getgridx() {
        return this.gridx;
    }

    public int getgridy() {
        return this.gridy;
    }

    public void setgridx(int val) {
        this.gridx = val;
    }

    public void setgridy(int val) {
        this.gridy = val;
    }

    public Point getScreenpoint() {
        return this.Screenpoint;
    }

    public void setScreenpoint() {
        int xoffset1;
        if (this.gridy / 2 == this.gridy / (double) 2) {
            xoffset1 = 0;
        } else {
            xoffset1 = (this.Size / 2);
        }
        int x = (this.gridx * this.Size) + xoffset1 + (this.Size / 2);
        int y = (int) Math.round(this.gridy * (this.Size * (5 / (double) 6)) + (this.Size / 2));
        //this.Screenpoint = new Point(x, y);
        this.setScreenpoint(new Point(x, y));
    }

    public void setScreenpoint(Point p) {
        this.Screenpoint = p;
    }

    public Map getMap() {
        return this.Map;
    }

    public void setMap(Map m) {
        this.Map = m;
        //m.addDrawableObject(this);
    }

    public double getGScore() {
        return this.GScore;
    }

    public void setGScore(double value) {
        this.GScore = value;
    }

    public double getFScore() {
        return this.FScore;
    }

    public void setFScore(double value) {
        this.FScore = value;
    }

    public Node getPreviousNode() {
        return this.PreviousNode;
    }

    public void setPreviousNode(Node n) {
        this.PreviousNode = n;
    }

    public void Draw(Player p) {
        int pixelx = this.getScreenpoint().x;
        int pixely = this.getScreenpoint().y;
        BufferedImage terrain = p.getTerrain();
        BufferedImage terrain2 = this.Map.getTerrain2();
        //Graphics g = terrain.getGraphics();
        int w = this.Image.getWidth();
        int h = this.Image.getHeight();
        for (int x = -(w / 2); x < (w / 2); x++) {
            for (int y = -(h / 2); y < (h / 2); y++) {
                int xdraw = pixelx + x;
                int ydraw = pixely + y;
                if (xdraw >= 0 && xdraw < terrain.getWidth() && ydraw >= 0 && ydraw < terrain.getHeight()) {
                    int c = this.Image.getRGB(x + (w / 2), y + (h / 2));
                    if (c != 0xFFFFFFFF) {
                        //Color color = new Color(c);
                        terrain.setRGB(xdraw, ydraw, c);
                        terrain2.setRGB(xdraw, ydraw, c);
                        //g.setColor(color);
                        //g.drawRect(xdraw, ydraw, 0, 0);
                    }
                }
            }
        }
    }

    public void setImage() {
        String RouteDirectory = this.getMap().getGame().getRouteDirectory();
        String path = RouteDirectory + "Images - Copy\\1200.bmp";
        try {
            BufferedImage img = ImageIO.read(new File(path));
            this.Image = img;
            this.setImgRef("1200");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setImage(BufferedImage img) {
        this.Image = img;
    }
    public String getImgRef(){
        return this.ImgRef;
    }
    public void setImgRef(String ref){
        this.ImgRef = ref;
    }
    public void addImage(BufferedImage image) {
        for (int x = 0; x < this.Image.getWidth(); x++) {
            for (int y = 0; y < this.Image.getWidth(); y++) {
                int c = image.getRGB(x, y);
                if (c != 0xFFFFFFFF) {
                    this.Image.setRGB(x, y, c);
                }
            }
        }
    }

    public BufferedImage getImage() {
        return this.Image;
    }

    public void Darken(Player p) {
        Game g = this.getMap().getGame();
        String routeDirectory = g.getRouteDirectory();
        String path = routeDirectory + "Images\\Node_Grey1.bmp";
        try {
            BufferedImage img = ImageIO.read(new File(path));
            this.setImage(img);
            this.setImgRef("Node_Grey1");
            this.Draw(p);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public Node[] getNeighbours() {
        return this.Neighbours;
    }

    public void setNeighbour(Node n, int index) {
        this.Neighbours[index] = n;
    }
    public boolean getIsAdjacent(Node n){
        boolean output = false;
        for(Node n2 : this.Neighbours){
            if (n2 == n){
                output = true;
            }
        }
        return output;
    }

    public int[] getCoverDescription() {
        return this.CoverDescription;
    }

    public void addCoverDescription(int direction, int value) {
        this.CoverDescription[direction] = value;
        //this.addToCoverTotal(value);
    }

    public int getCoverTotal() {
        this.CalculateCoverTotal();
        return this.CoverTotal;
    }

    public void CalculateCoverTotal() {
        int covertotal = 0;
        for (int cover : this.getCoverDescription()) {
            covertotal += cover;
        }
        this.CoverTotal = covertotal;
    }

    public void addToCoverTotal(int value) {
        this.CoverTotal += value;
    }

    public void subtractFromCoverTotal(int value) {
        this.CoverTotal -= value;
    }

    public List<CoverSection> getCoverSections() {
        return this.CoverSections;
    }

    public void addCoverSection(CoverSection cs) {
        this.CoverSections.add(cs);
    }

    public List<CoverSection> getCoverOnLine(Point p1, Point p2) {

        Map m = this.getMap();
        List<CoverSection> cvsecs = this.getCoverSections();
        List<CoverSection> blocklist = new ArrayList<CoverSection>();
        for (CoverSection cs : cvsecs) {
            if (cs.Obstructs(p1, p2, m.getMovementThresholdHigh())) {
                blocklist.add(cs);
            }
        }
        return blocklist;
    }

    public List<CoverSection> getCoverOnLine(Point p1, Point p2, int minimumCoverValue) {

        Map m = this.getMap();
        List<CoverSection> cvsecs = this.getCoverSections();
        List<CoverSection> blocklist = new ArrayList<CoverSection>();
        for (CoverSection cs : cvsecs) {
            if (cs.getValues()[0] > minimumCoverValue) {
                if (cs.Obstructs(p1, p2, m.getMovementThresholdHigh())) {
                    blocklist.add(cs);
                }
            }
        }
        return blocklist;
    }

    public List<Point> getPointsInside() {
        return this.PointsInside;
    }

    public void setPointsInside() {
        List<Point> pointsPerNode = this.getMap().getPointsPerNode();
        int listSize = pointsPerNode.size();
        List<Point> pointsInside = new ArrayList<Point>();
        if (listSize > 0) {
            int offset = this.getSize() / 2;
            Point p2 = this.getScreenpoint();
            for (Point p1 : pointsPerNode) {
                pointsInside.add(new Point(p1.x + p2.x - offset, p1.y + p2.y - offset));
            }
        } else {
            System.out.println("Node.getMap.getPointsPerNode returned empty list");
        }
        //this.PointsInside = new ArrayList<Point>();
        this.PointsInside = pointsInside;
    }
    /* public int[] getEdgesOnLine2(Point p1, Point p2){
        int[] edgesonline = new int[6];
        for(int val : edgesonline){
            val = 0;
        }
        double angle;
        int halfsize = (int)Math.round(this.Size / (double)2);
        double offset = halfsize + Math.sqrt(2);
        angle = 0;
        Point p3 = new Point((int)Math.round(this.Screenpoint.x + (offset * Math.cos(angle))), (int)Math.round(this.Screenpoint.y - (offset * Math.sin(angle))));
        angle = (1 / (double)3) * (Math.PI);
        Point p4 = new Point((int)Math.round(this.Screenpoint.x + (offset * Math.cos(angle))), (int)Math.round(this.Screenpoint.y - (offset * Math.sin(angle))));
        angle = (2 / (double)3) * (Math.PI);
        Point p5 = new Point((int)Math.round(this.Screenpoint.x + (offset * Math.cos(angle))), (int)Math.round(this.Screenpoint.y - (offset * Math.sin(angle))));
        angle = (3 / (double)3) * (Math.PI);
        Point p6 = new Point((int)Math.round(this.Screenpoint.x + (offset * Math.cos(angle))), (int)Math.round(this.Screenpoint.y - (offset * Math.sin(angle))));
        angle = (4 / (double)3) * (Math.PI);
        Point p7 = new Point((int)Math.round(this.Screenpoint.x + (offset * Math.cos(angle))), (int)Math.round(this.Screenpoint.y - (offset * Math.sin(angle))));
        angle = (5 / (double)3) *( Math.PI);
        Point p8 = new Point((int)Math.round(this.Screenpoint.x + (offset * Math.cos(angle))), (int)Math.round(this.Screenpoint.y - (offset * Math.sin(angle))));
        Find f = new Find();
        if (f.IsCross(p1, p2, p3, p4)){
            edgesonline[0] = this.getCoverDescription()[0];
            //edgesonline[0] = 6;
        }
        if (f.IsCross(p1, p2, p4, p5)){
            edgesonline[1] = this.getCoverDescription()[1];
            //edgesonline[1] = 6;
        }
        if (f.IsCross(p1, p2, p5, p6)){
            edgesonline[2] = this.getCoverDescription()[2];
            //edgesonline[2] = 6;
            }
        if (f.IsCross(p1, p2, p6, p7)){
            edgesonline[3] = this.getCoverDescription()[3];
            //edgesonline[3] = 6;
        }
        if (f.IsCross(p1, p2, p7, p8)){
            edgesonline[4] = this.getCoverDescription()[4];
            //edgesonline[4] = 6;
        }
        if (f.IsCross(p1, p2, p8, p3)){
            edgesonline[5] = this.getCoverDescription()[5];
            //edgesonline[5] = 6;
        }
        return edgesonline; */
    /* } */
}
