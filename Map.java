package client;


import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import javax.swing.Timer;

public class Map {
    //private Game Game;
    private Level Level;
    private Node[][] NodeGrid;
    //private Collection<DrawableObject> DrawableObjects = new Collection<DrawableObject>();
    private List<List<DrawableObject>> DrawableObjects;
    private List<MoveableObject> MoveableObjects = new ArrayList<MoveableObject>();
    //private JLabel pictureBoxLabel;
    //private BufferedImage Terrain;
    private BufferedImage Terrain2;
    private int xgridmax;
    private int ygridmax;
    private int GraphicsLayerOffset = 6;
    //private List<Player> Players = new ArrayList<Player>();
    private Rectangle GameBoardDimensions;
    private List<Boundary> Boundaries = new ArrayList<Boundary>();
    private List<Compound> Compounds = new ArrayList<Compound>();
    private List<Compound> Objectives = new ArrayList<Compound>();
    private List<CoverSection> Cover = new ArrayList<CoverSection>();
    private int MovementThresholdHigh = 4;
    private int VisionThreshold = 5;
    private List<Point> PointsPerNode;
    private int MovementCounter = 0;
    private Timer TimerMovement = new Timer(100, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            UpdateMoveables();
        }
    });

    public Map(Rectangle r, Game g) {
        super();
        //this.setGame(g);
        g.getLevel().setMap(this);
        this.setLevel(g.getLevel());
        //this.setLevel(g.getLevel());
        //this.getLevel().setGame(g);
        this.GameBoardDimensions = r;
        this.GenerateTerrain2();
        this.setPointsPerNode();
        int nodesize = 18;
        int xgridmax = (int) Math.ceil(this.getTerrain2().getWidth() / nodesize);
        int ygridmax = (int) Math.round(this.getTerrain2().getHeight() / (nodesize * 5 / (double) 6));
        this.xgridmax = xgridmax;
        this.ygridmax = ygridmax;
        Node[][] Grid = new Node[xgridmax][ygridmax];
        for (int x = 0; x < xgridmax; x++) {
            for (int y = 0; y < ygridmax; y++) {
                Node n = new Node();
                n.setgridx(x);
                n.setgridy(y);
                n.setScreenpoint();
                n.setMap(this);
                n.setImage();
                Grid[x][y] = n;
                //Below line is removed from application completely
                //n.setPointsInside();
            }
        }
        this.NodeGrid = Grid;
        //this.Terrain2 = this.getTerrain2();
        this.setNeighbours();
        int value = this.Terrain2.getHeight() / this.GraphicsLayerOffset;
        this.DrawableObjects = new ArrayList<List<DrawableObject>>(value);
        for (int index = 0; index < value; index++) {
            List<DrawableObject> row = new ArrayList<DrawableObject>();
            this.DrawableObjects.add(row);
        }
        this.GeneratePatchiness();
        this.GenerateScrub();
        this.GenerateCover();
        this.CalculateCoverTotals();
        this.TimerMovement.start();
    }

    public Game getGame() {
        return this.getLevel().getGame();
    }

    public Level getLevel() {
        return this.Level;
    }

    public void setLevel(Level lvl) {
        this.Level = lvl;
    }
    /* public void addPlayer(Player p) {
        this.Players.add(p);
        int number = this.Players.indexOf(p);
        p.setPlayerNumber(number);
    } */

    public Rectangle getGameBoardDimensions() {
        return this.GameBoardDimensions;
    }

    public int getGraphicsLayerOffset() {
        return this.GraphicsLayerOffset;
    }

    /* public String getRouteDirectory() {
        Game g = this.getGame();
        return g.getRouteDirectory();
    } */

    public BufferedImage getTerrain2() {
        return this.Terrain2;
    }

    public void setTerrain2(BufferedImage trn2) {
        this.Terrain2 = trn2;
    }

    public Node[][] getNodeGrid() {
        return this.NodeGrid;
    }

    /* public List<Player> getPlayers() {
        return this.Players;
    } */

    public void setNodeGrid(Node[][] nodegrid) {
        this.NodeGrid = nodegrid;
    }

    public void GenerateTerrain2() {
        Rectangle gbrd = new Rectangle(0, 0, this.getGameBoardDimensions().width, this.getGameBoardDimensions().height);
        int gbrdx = (int) Math.round(gbrd.getWidth());
        int gbrdy = (int) Math.round(gbrd.getHeight());
        BufferedImage terrain4 = new BufferedImage(gbrdx, gbrdy, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < terrain4.getWidth(); x++) {
            for (int y = 0; y < terrain4.getHeight(); y++) {
                terrain4.setRGB(x, y, 0xFF000000);
            }
        }
        this.setTerrain2(terrain4);
    }

    public List<List<DrawableObject>> getDrawableObjects() {
        return this.DrawableObjects;
    }

    public List<DrawableObject> getDrawableObjectsInRange(Point p1, double range) {
        Find f = new Find();
        List<DrawableObject> list = new ArrayList<DrawableObject>();
        for (List<DrawableObject> DOL : this.getDrawableObjects()) {
            for (DrawableObject DO : DOL) {
                Point p2 = DO.getScreenpoint();
                if (f.Distance(p1, p2) <= range) {
                    list.add(DO);
                }
            }
        }
        return list;
    }

    public void setDrawableObjects(List<List<DrawableObject>> drawableobjects) {
        this.DrawableObjects = drawableobjects;
    }

    public List<Boundary> getBoundaries() {
        return this.Boundaries;
    }

    public void addBoundary(Boundary boundary) {
        this.Boundaries.add(boundary);
    }

    public List<Compound> getCompounds() {
        return this.Compounds;
    }

    public void addCompound(Compound c) {
        //c.setMap(this);
        this.Compounds.add(c);
    }

    public List<Compound> getObjectives() {
        return this.Objectives;
    }

    public void addObjective(Compound c) {
        this.Objectives.add(c);
    }

    public void removeObjective(Compound c) {
        while (this.Objectives.contains(c)) {
            this.Objectives.remove(c);
        }
    }

    public int getMovementThresholdHigh() {
        return this.MovementThresholdHigh;
    }

    public void setMovementThresholdHigh(int val) {
        this.MovementThresholdHigh = val;
    }

    public int getVisionThreshold() {
        return this.VisionThreshold;
    }

    public void setVisionThreshold(int val) {
        this.VisionThreshold = val;
    }

    public List<Point> getPointsPerNode() {
        return this.PointsPerNode;
    }
    public Timer getTimerMovement(){
        return this.TimerMovement;
    }

    private void setPointsPerNode() {
        this.PointsPerNode = new ArrayList<Point>();
        BufferedImage img = this.getGame().getFOWBlack();
        int width = img.getWidth();
        int height = img.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int c = img.getRGB(x, y);
                if (c != 0xFFFFFFFF) {
                    this.PointsPerNode.add(new Point(x, y));
                }
            }
        }
    }

    public void addDrawableObject(DrawableObject o) {
        if (this.DrawableObjects != null) {
            // remove current instances of this object
            boolean contains = false;
            for (int index = 0; index < this.DrawableObjects.size(); index++) {
                List<DrawableObject> rowlist = this.DrawableObjects.get(index);
                if (rowlist != null) {
                    if (contains == false) {
                        if (rowlist.contains(o)) {
                            contains = true;
                            this.DrawableObjects.get(index).remove(o);
                        }
                    }
                }
            }
            //add object to correct row of DrawableObjects
            int g = this.getGraphicsLayerOffset();
            int layer = (int) Math.floor(o.getScreenpoint().y / g);
            this.DrawableObjects.get(layer).add(o);
        }
    }

    public void removeDrawableObject(DrawableObject o) {
        //Remove from map
        for (List<DrawableObject> row : this.DrawableObjects) {
            while (row.contains(o)) {
                row.remove(o);
            }
        }
        //Remove from each player
        for (Player p : this.getGame().getPlayers()) {
            p.removeDrawableObject(o);
        }
    }

    public void Draw() {
        for (Node[] na : this.NodeGrid) {
            for (Node n : na) {
                for (Player p : this.getGame().getPlayers()) {
                    n.Draw(p);
                }
            }
        }
    }

    public void UpdateImage2() {
        for (List<DrawableObject> row : this.DrawableObjects) {
            for (DrawableObject o : row) {
                //o.Draw();







            }
        }
    }

    public Node getClosestNode(Point p) {
        double smallestDistance = this.Terrain2.getWidth();
        Node closestNode = this.NodeGrid[0][0];
        for (Node[] na : this.NodeGrid) {
            for (Node n : na) {
                Find f = new Find();
                double d = f.Distance(n.getScreenpoint(), p);
                double d2 = f.Distance(closestNode.getScreenpoint(), p);
                if (d < d2) {
                    closestNode = n;
                }
            }
        }
        //System.out.println(closestNode.getScreenpoint().toString());
        return closestNode;
    }

    public List<Node> getNodesInRange(Point p, double range) {
        Find f = new Find();
        Point p1 = new Point(p.x - (int) Math.round(range), p.y - (int) Math.round(range));
        Point p2 = new Point(p.x + (int) Math.round(range), p.y + (int) Math.round(range));
        List<Node> list = new ArrayList<Node>();
        for (Node n1 : this.getNodesInBox(p1, p2)) {
            double dist1 = f.Distance(p, n1.getScreenpoint());
            if (dist1 <= range) {
                list.add(n1);
            }
        }
        /* for (Node[] row : this.NodeGrid) {
            for (Node n1 : row) {

            }
        } */
        return list;
    }

    public List<Node> getCloseCoveredNodes(Node centre, double radius) {
        Point pcentre = centre.getScreenpoint();
        List<Node> rangenodes = this.getNodesInRange(pcentre, radius);
        List<Node> orderedlist = new ArrayList<Node>();
        //find max cover
        int maxcover = 0;
        for (Node n : rangenodes) {
            int covertotal = n.getCoverTotal();
            if (covertotal > maxcover) {
                maxcover = covertotal;
            }
        }
        //new bit
        //get own and neighbours' cover totals into arrays
        if (rangenodes.size() > 0) {
            int size = rangenodes.size();
            int cvrt = 0;
            int nbrs = 0;
            int[] pdct = new int[size];
            int max = 0;
            for (int indx = 0; indx < size; indx++) {
                Node n = rangenodes.get(indx);
                cvrt = n.getCoverTotal();
                int nbcv = 0;
                for (Node n2 : n.getNeighbours()) {
                    if (n2 != null) {
                        nbcv += n2.getCoverTotal();
                    }
                }
                nbrs = nbcv;
                pdct[indx] = cvrt * nbrs;
                if (pdct[indx] > max) {
                    max = pdct[indx];
                }
            }
            int curr = max;
            while (curr >= 0) {
                for (int indx = 0; indx < size; indx++) {
                    Node n = rangenodes.get(indx);
                    if (pdct[indx] >= curr) {
                        if (orderedlist.contains(n) == false) {
                            orderedlist.add(n);
                        }
                    }
                }
                curr -= 1;
            }
            //remove those not reachable from centre.
            List<Node> removables = new ArrayList<Node>();
            for (Node n : orderedlist) {
                if (this.LOMTest(centre, n) == false) {
                    removables.add(n);
                }
            }
            for (Node n : removables) {
                orderedlist.remove(n);
            }
        }
        return orderedlist;
    }

    public List<Node> getNodesInBox(Point p1, Point p2) {
        int minx = Math.min(p1.x, p2.x);
        int maxx = Math.max(p1.x, p2.x);
        int miny = Math.min(p1.y, p2.y);
        int maxy = Math.max(p1.y, p2.y);
        List<Node> nodelist = new ArrayList<Node>();
        for (Node[] na : this.getNodeGrid()) {
            for (Node n : na) {
                Point p3 = n.getScreenpoint();
                if (p3.x >= minx && p3.x <= maxx) {
                    if (p3.y >= miny && p3.y <= maxy) {
                        nodelist.add(n);
                    }
                }
            }
        }
        return nodelist;
    }

    public List<Node> getNodesOnLine(Point p1, Point p2) {
        return this.getNodesInBox(p1, p2);
    }

    public List<Node> getNodesOnLine2(Point p1, Point p2) {
        boolean vertical = false;
        boolean horizontal = false;
        if (p1.x == p2.x)
            vertical = true;
        if (p1.y == p2.y)
            horizontal = true;
        List<Point> pointsOnLine = new ArrayList<Point>();
        if (vertical == false) {
            int xdiff = p2.x - p1.x;
            int ydiff = p2.y - p1.y;
            float gradient1 = ydiff / (float) (xdiff);
            float c1 = p1.y - (p1.x * gradient1);
            int minx = Math.min(p1.x, p2.x);
            int maxx = Math.max(p1.x, p2.x);
            for (int x = minx; x <= maxx; x++) {
                float yval = (x * gradient1) + c1;
                int y = (int) Math.round(yval);
                pointsOnLine.add(new Point(x, y));
            }
        } else {
            //vertical
        }
        List<Node> nodesOnLine = new ArrayList<Node>();
        for (Node n1 : this.getNodesInBox(p1, p2)) {
            if (nodesOnLine.contains(n1) == false) {
                for (Point p3 : n1.getPointsInside()) {
                    if (pointsOnLine.contains(p3)) {
                        nodesOnLine.add(n1);
                    }
                }
            }
        }
        return nodesOnLine;
    }

    public int getCoverMaxOnLine(Point p1, Point p2) {
        int maximum = 0;
        List<Node> nodesonline = this.getNodesOnLine(p1, p2);
        for (Node n : nodesonline) {
            //System.out.println("Node:" + n.getScreenpoint() + n.getCoverDescription()[0] + n.getCoverDescription()[1] + n.getCoverDescription()[2] + n.getCoverDescription()[3] + n.getCoverDescription()[4] + n.getCoverDescription()[5]);
            List<CoverSection> coverCrossed = new ArrayList<CoverSection>();
            //coverCrossed = n.getCoverOnLine(p1, p2);
            coverCrossed = n.getCoverOnLine(p1, p2, maximum);
            for (CoverSection cs : coverCrossed) {
                //below assumes cover is the same on both sides
                int val = cs.getValues()[0];
                if (val > maximum) {
                    maximum = val;
                    //System.out.println("Maximum cover increased to " + maximum);
                }
            }
        }
        return maximum;
    }

    public List<CoverSection> getCover() {
        return this.Cover;
    }

    public void addCover(CoverSection c) {
        this.Cover.add(c);
    }

    public void removeCover(CoverSection c) {
        while (this.Cover.contains(c)) {
            this.Cover.remove(c);
        }
    }

    public boolean LOMTest(Node n1, Node n2) {
        Boolean outcome = false;
        Point p1 = n1.getScreenpoint();
        Point p2 = n2.getScreenpoint();
        int thresh1 = this.getMovementThresholdHigh();
        if (this.getCoverMaxOnLine(p1, p2) < thresh1) {
            outcome = true;
        }
        return outcome;
    }

    public boolean LOSTest(Point p1, Point p2) {
        Boolean outcome = false;
        int thresh1 = this.getVisionThreshold();
        if (this.getCoverMaxOnLine(p1, p2) < thresh1) {
            outcome = true;
        }
        return outcome;
    }

    public void setNeighbours() {
        Node[][] Grid = this.NodeGrid;
        for (int x = 0; x < this.xgridmax; x++) {
            for (int y = 0; y < this.ygridmax; y++) {
                //Grid[x][y].setNeighbours();
                Node n0 = Grid[x][y];
                Node n1 = Grid[x][y];
                Node n2 = Grid[x][y];
                Node n3 = Grid[x][y];
                Node n4 = Grid[x][y];
                Node n5 = Grid[x][y];
                if (x + 1 < this.xgridmax) {
                    n1 = Grid[x + 1][y];
                }
                if (x - 1 >= 0) {
                    n4 = Grid[x - 1][y];
                }

                if (y / 2 != y / (double) 2) {
                    if (x + 1 < this.xgridmax && y - 1 >= 0)
                        n0 = Grid[x + 1][y - 1];
                    if (x + 1 < this.xgridmax && y + 1 < this.ygridmax)
                        n2 = Grid[x + 1][y + 1];
                    if (y + 1 < this.ygridmax)
                        n3 = Grid[x][y + 1];
                    if (y - 1 >= 0)
                        n5 = Grid[x][y - 1];
                } else {
                    if (y - 1 >= 0)
                        n0 = Grid[x][y - 1];
                    if (y + 1 < this.ygridmax)
                        n2 = Grid[x][y + 1];
                    if (x - 1 >= 0 && y + 1 < this.ygridmax)
                        n3 = Grid[x - 1][y + 1];
                    if (x - 1 >= 0 && y - 1 >= 0)
                        n5 = Grid[x - 1][y - 1];
                }
                if (n0 != Grid[x][y]) {
                    Grid[x][y].setNeighbour(n0, 0);
                    n0.setNeighbour(Grid[x][y], 3);
                }
                if (n1 != Grid[x][y]) {
                    Grid[x][y].setNeighbour(n1, 1);
                    n1.setNeighbour(Grid[x][y], 4);
                }
                if (n2 != Grid[x][y]) {
                    Grid[x][y].setNeighbour(n2, 2);
                    n2.setNeighbour(Grid[x][y], 5);
                }
                if (n3 != Grid[x][y]) {
                    Grid[x][y].setNeighbour(n3, 3);
                    n3.setNeighbour(Grid[x][y], 0);
                }
                if (n4 != Grid[x][y]) {
                    Grid[x][y].setNeighbour(n4, 4);
                    n4.setNeighbour(Grid[x][y], 1);
                }
                if (n5 != Grid[x][y]) {
                    Grid[x][y].setNeighbour(n5, 5);
                    n5.setNeighbour(Grid[x][y], 2);
                }
            }
        }
    }

    public void setNeighbours2() {
        Find f = new Find();
        for (Node[] na : this.getNodeGrid()) {
            for (Node n : na) {
                Point p1 = n.getScreenpoint();
                List<Node> neighbours = this.getNodesInRange(p1, (n.getSize() + 1));
                for (Node n2 : neighbours) {
                    Point p2 = n2.getScreenpoint();
                    double a1 = f.Angle(p1, p2);
                    if (a1 > -29 && a1 < 31)
                        n.setNeighbour(n2, 0);
                    if (a1 > -89 && a1 < 91)
                        n.setNeighbour(n2, 1);
                    if (a1 > -149 && a1 < 151)
                        n.setNeighbour(n2, 2);
                    if (a1 > -209 && a1 < 211)
                        n.setNeighbour(n2, 3);
                    if (a1 > -269 && a1 < 271)
                        n.setNeighbour(n2, 4);
                    if (a1 > -329 && a1 < 331)
                        n.setNeighbour(n2, 5);
                }
            }
        }
    }

    public void GenerateScrub() {
        String path1 = this.getGame().getRouteDirectory() + "Images\\Node_Scrub1.bmp";
        String path2 = this.getGame().getRouteDirectory() + "Images\\Node_Scrub2.bmp";
        int size = this.NodeGrid[0][0].getSize();
        BufferedImage img1 = new BufferedImage(size, size, 1);
        BufferedImage img2 = new BufferedImage(size, size, 1);
        try {
            img1 = ImageIO.read(new File(path1));
            img2 = ImageIO.read(new File(path2));
        } catch (Exception e) {
            e.getMessage();
        }
        int count = 0;
        int maxcount = 100;
        while (count < maxcount) {
            Random rnd0 = new Random();
            int xmax = this.NodeGrid.length;
            int ymax = this.NodeGrid[0].length;
            int rnd1 = rnd0.nextInt(xmax);
            int rnd2 = rnd0.nextInt(ymax);
            Node n = this.NodeGrid[rnd1][rnd2];
            if (rnd0.nextBoolean() == true) {
                n.setImage(img2);
                n.setImgRef("1600");
            } else {
                n.setImage(img1);
                n.setImgRef("1700");
            }
            count += 1;
        }
    }

    public void GeneratePatchiness() {
        String path1 = this.getGame().getRouteDirectory() + "Images\\Node_Dust2.bmp";
        String path2 = this.getGame().getRouteDirectory() + "Images\\Node_Dust3.bmp";
        int count = 0;
        int maxcount = 1000;
        int size = this.NodeGrid[0][0].getSize();
        BufferedImage img1 = new BufferedImage(size, size, 1);
        BufferedImage img2 = new BufferedImage(size, size, 1);
        try {
            img1 = ImageIO.read(new File(path1));
        } catch (Exception e) {
            e.getMessage();
        }
        try {
            img2 = ImageIO.read(new File(path2));
        } catch (Exception e) {
            e.getMessage();
        }
        while (count < maxcount) {
            Random rnd = new Random();
            int xmax = this.NodeGrid.length;
            int ymax = this.NodeGrid[0].length;
            int rnd1 = rnd.nextInt(xmax);
            int rnd2 = rnd.nextInt(ymax);
            Node n = this.NodeGrid[rnd1][rnd2];
            if (rnd.nextBoolean() == true) {
                n.addImage(img1);
                n.setImgRef("1300");
            } else {
                n.addImage(img2);
                n.setImgRef("1400");
            }
            count += 1;
        }
    }

    public void GenerateCover() {
        Random rnd = new Random();
        int lnum = this.getLevel().getLevelNumber();
        if (lnum < 3) {
            {
                Compound f0 = new Compound(this.getNodeGrid()[32][20], 4, "Farm2", this);
                Compound f1 = new Compound(this.getNodeGrid()[45][5], 1, "Farm1", this);
                Compound f2 = new Compound(this.getNodeGrid()[70][5], 1, "Farm2", this);
                //
                Compound f3 = new Compound(this.getNodeGrid()[13][25], 1, "Farm1", this);
                Compound f4 = new Compound(this.getNodeGrid()[83][40], 4, "Farm1", this);
                //
                Compound f5 = new Compound(this.getNodeGrid()[75][60], 4, "Farm1", this);
                Compound f6 = new Compound(this.getNodeGrid()[50][60], 4, "Farm1", this);
                Compound f7 = new Compound(this.getNodeGrid()[28][60], 4, "Farm2", this);
                //
                Compound y0 = new Compound(this.getNodeGrid()[55][25], 1, "Yard1", this);
                Compound y1 = new Compound(this.getNodeGrid()[55][35], 1, "Yard1", this);
                //
                Compound y2 = new Compound(this.getNodeGrid()[35][30], 0, "Yard2", this);
                Compound y3 = new Compound(this.getNodeGrid()[40][30], 0, "Yard2", this);
                Compound y4 = new Compound(this.getNodeGrid()[45][30], 0, "Yard2", this);
                //
                Compound y5 = new Compound(this.getNodeGrid()[14][3], 0, "Yard3", this);
                Compound y6 = new Compound(this.getNodeGrid()[13][16], 3, "Yard3", this);
                Compound y7 = new Compound(this.getNodeGrid()[9][24], 3, "Yard3", this);
                //
                Compound y8 = new Compound(this.getNodeGrid()[11][52], 3, "Yard2", this);
                Compound y9 = new Compound(this.getNodeGrid()[5][52], 3, "Yard2", this);
                //
                //Set farms as leavable
                f0.setLeavable(true);
                f1.setLeavable(true);
                f2.setLeavable(true);
                f3.setLeavable(true);
                f4.setLeavable(true);
                f5.setLeavable(true);
                f6.setLeavable(true);
                f7.setLeavable(true);
                //Add objectives to map
                this.addObjective(f0);
                this.addObjective(f1);
                this.addObjective(f2);
                this.addObjective(f3);
                this.addObjective(f4);
                this.addObjective(f5);
                this.addObjective(f6);
                this.addObjective(f7);
            }
        }
        if (lnum == 3) {
            {
                int wdth = 3;
                int lgth = 7;
                List<List<Compound>> str1 = new ArrayList<List<Compound>>(wdth);
                int x1 = 6;
                int y1 = 26;
                for (int indx = 0; indx < wdth; indx++) {
                    List<Compound> row = new ArrayList<Compound>(lgth);
                    for (int idx2 = 0; idx2 < lgth; idx2++) {
                        int x2 = x1 + (idx2 * 6) + (indx * 3);
                        int y2 = y1 + (indx * 6);
                        if (indx == 0) {
                            if (idx2 != 3) {
                                Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard5", this);
                            } else {
                                Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard4", this);
                                this.addObjective(c1);
                            }
                        }
                        if (indx == 1) {
                            //Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard4", this);

                        }
                        if (indx == 2) {
                            if (idx2 != 3) {
                                Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard7", this);
                            } else {
                                Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard6", this);
                            }
                        }
                    }
                }
                List<List<Compound>> str2 = new ArrayList<List<Compound>>(wdth);
                x1 = 32;
                y1 = 6;
                lgth = 3;
                for (int indx = 0; indx < wdth; indx++) {
                    List<Compound> row = new ArrayList<Compound>(lgth);
                    for (int idx2 = 0; idx2 < lgth; idx2++) {
                        int x2 = x1 + (indx * 6) + (idx2 * 3);
                        int y2 = y1 + (idx2 * 6);
                        if (indx == 0) {
                            if (idx2 != 3) {
                                Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard5", this);
                            } else {
                                Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard4", this);
                            }
                        }
                        if (indx == 1) {
                            //Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard4", this);


                        }
                        if (indx == 2) {
                            if (idx2 != 3) {
                                Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard7", this);
                            } else {
                                Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard6", this);
                            }
                        }
                    }
                }
                List<List<Compound>> str3 = new ArrayList<List<Compound>>(wdth);
                x1 = 51;
                y1 = 44;
                wdth = 4;
                lgth = 3;
                for (int indx = 0; indx < wdth; indx++) {
                    List<Compound> row = new ArrayList<Compound>(lgth);
                    for (int idx2 = 0; idx2 < lgth; idx2++) {
                        int x2 = x1 + (indx * 6) + (idx2 * 3);
                        int y2 = y1 + (idx2 * 6);
                        if (indx == 0) {
                            if (idx2 != 3) {
                                Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard5", this);
                            } else {
                                Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard4", this);
                            }
                        }
                        //if (indx == 1) {
                        //Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard4", this);


                        //}
                        if (indx == 3) {
                            if (idx2 != 0 && idx2 != 3) {
                                Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard7", this);
                            } else {
                                if (idx2 == 3) {
                                    Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard6", this);
                                }
                            }
                        }
                    }
                }
                wdth = 3;
                lgth = 5;
                List<List<Compound>> str4 = new ArrayList<List<Compound>>(wdth);
                x1 = 76;
                y1 = 18;
                for (int indx = 0; indx < wdth; indx++) {
                    List<Compound> row = new ArrayList<Compound>(lgth);
                    for (int idx2 = 0; idx2 < lgth; idx2++) {
                        int x2 = x1 + (idx2 * 6) + (indx * 3);
                        int y2 = y1 + (indx * 6);
                        if (indx == 0) {
                            if (idx2 != 1) {
                                Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard5", this);
                            } else {
                                Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard4", this);
                            }
                        }
                        if (indx == 1) {
                            //Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard4", this);


                        }
                        if (indx == 2) {
                            if (idx2 != 3) {
                                Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard7", this);
                            } else {
                                Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard6", this);
                            }
                        }
                    }
                }
                wdth = 3;
                lgth = 4;
                List<List<Compound>> str5 = new ArrayList<List<Compound>>(wdth);
                x1 = 86;
                y1 = 38;
                for (int indx = 0; indx < wdth; indx++) {
                    List<Compound> row = new ArrayList<Compound>(lgth);
                    for (int idx2 = 0; idx2 < lgth; idx2++) {
                        int x2 = x1 + (idx2 * 6) + (indx * 3);
                        int y2 = y1 + (indx * 6);
                        if (indx == 0) {
                            if (idx2 != 1) {
                                Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard5", this);
                            } else {
                                Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard4", this);
                            }
                        }
                        if (indx == 1) {
                            //Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard4", this);



                        }
                        if (indx == 2) {
                            if (idx2 != 3) {
                                Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard7", this);
                            } else {
                                Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard6", this);
                            }
                        }
                    }
                }

                List<List<Compound>> grd1 = new ArrayList<List<Compound>>(3);
                x1 = 53;
                y1 = 12;
                for (int indx = 0; indx < 3; indx++) {
                    List<Compound> row = new ArrayList<Compound>(5);
                    for (int idx2 = 0; idx2 < 5; idx2++) {
                        int x2 = x1 + (indx * 6) + (idx2 * 3);
                        int y2 = y1 + (idx2 * 6);
                        if (idx2 == 2 || idx2 == 3) {
                            if (idx2 == 3) {
                                Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard4", this);
                            }
                        } else {
                            if (idx2 == 4) {
                                Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard5", this);
                            } else {
                                Compound c1 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard4", this);
                            }
                        }
                    }
                }
                Compound c1 = new Compound(this.getNodeGrid()[83][50], 1, "Yard2", this);
                this.addObjective(c1);
                x1 = 51;
                y1 = 25;
                for (int indx = 0; indx < 7; indx++) {
                    if (indx != 1) {
                        int x2 = x1 + (indx * 3);
                        int y2 = y1 + (indx * 6);
                        Compound c2 = new Compound(this.getNodeGrid()[x2][y2], 1, "Yard2", this);
                        if (indx == 0) {
                            this.addObjective(c2);
                        }
                        //Compound c3 = new Compound(this.getNodeGrid()[56][30], 1, "Yard2", this);
                    }
                }
                x1 = 10;
                y1 = 10;
                for (int indx = 0; indx < 3; indx++) {
                    int x2 = x1 + (indx * 8);
                    int y2 = y1;
                    Compound c2 = new Compound(this.getNodeGrid()[x2][y2], 1, "Farm3", this);
                    c2.setLeavable(true);
                }
                x1 = 27;
                y1 = 47;
                for (int indx = 0; indx < 3; indx++) {
                    int x2 = x1 + (indx * 8);
                    int y2 = y1;
                    Compound c2 = new Compound(this.getNodeGrid()[x2][y2], 1, "Farm3", this);
                    c2.setLeavable(true);
                }
                x1 = 78;
                y1 = 5;
                Compound c2 = new Compound(this.getNodeGrid()[x1][y1], 1, "Farm4", this);
                c2.setLeavable(true);
                x1 = 90;
                y1 = 5;
                Compound c3 = new Compound(this.getNodeGrid()[x1][y1], 1, "Farm4", this);
                c3.setLeavable(true);
                x1 = 102;
                y1 = 5;
                Compound c4 = new Compound(this.getNodeGrid()[x1][y1], 1, "Farm4", this);
                c4.setLeavable(true);
                x1 = 90;
                y1 = 58;
                Compound c5 = new Compound(this.getNodeGrid()[x1][y1], 1, "Farm4", this);
                c5.setLeavable(true);
                x1 = 102;
                y1 = 58;
                Compound c6 = new Compound(this.getNodeGrid()[x1][y1], 1, "Farm4", this);
                c6.setLeavable(true);
            }
        }
    }

    public void GenerateCover2() {
        //List<Compound> Compounds = new ArrayList<Compound>();
        int w = this.Terrain2.getWidth();
        int h = this.Terrain2.getHeight();
        Point p1;
        Point p2;
        Point p3;
        Point p4;
        Compound c;
        p1 = new Point(216, 294);
        p2 = new Point(580, 288);
        p3 = new Point(760, 600);
        p4 = new Point(580, 912);
        c = new Compound(p1, p2, p3, p4, 5, 6, 3, 2, this);
        this.addCompound(c);
        p1.setLocation(w - 216, h - 294);
        p2.setLocation(w - 580, h - 288);
        p3.setLocation(w - 760, h - 600);
        p4.setLocation(w - 580, h - 912);
        c = new Compound(p1, p2, p3, p4, 5, 6, 3, 2, this);
        this.addCompound(c);
        p1.setLocation(828, 444);
        p2.setLocation(684, 174);
        p3.setLocation(837, 219);
        p4.setLocation(909, 339);
        c = new Compound(p1, p2, p3, p4, 1, 5, 1, 4, this);
        this.addCompound(c);
        p1.setLocation(w - 828, h - 444);
        p2.setLocation(w - 684, h - 174);
        p3.setLocation(w - 837, h - 219);
        p4.setLocation(w - 909, h - 339);
        c = new Compound(p1, p2, p3, p4, 1, 5, 1, 4, this);
        this.addCompound(c);
        p1.setLocation(711, 849);
        p2.setLocation(873, 579);
        p3.setLocation(999, 579);
        p4.setLocation(1161, 849);
        c = new Compound(p1, p2, p3, p4, 6, 6, 1, 4, this);
        this.addCompound(c);
        p1.setLocation(w - 711, h - 849);
        p2.setLocation(w - 873, h - 579);
        p3.setLocation(w - 999, h - 579);
        p4.setLocation(w - 1161, h - 849);
        c = new Compound(p1, p2, p3, p4, 6, 6, 1, 4, this);
        this.addCompound(c);
        p1.setLocation(603, 159);
        p2.setLocation(252, 114);
        p3.setLocation(297, 39);
        p4.setLocation(567, 39);
        c = new Compound(p1, p2, p3, p4, 6, 6, 1, 4, this);
        this.addCompound(c);
        p1.setLocation(w - 603, h - 159);
        p2.setLocation(w - 252, h - 114);
        p3.setLocation(w - 297, h - 39);
        p4.setLocation(w - 567, h - 39);
        c = new Compound(p1, p2, p3, p4, 6, 6, 1, 4, this);
        this.addCompound(c);
        p1.setLocation(171, 369);
        p2.setLocation(324, 624);
        p3.setLocation(252, 744);
        p4.setLocation(27, 369);
        c = new Compound(p1, p2, p3, p4, 6, 6, 3, 4, this);
        this.addCompound(c);
        p1.setLocation(w - 171, h - 369);
        p2.setLocation(w - 324, h - 624);
        p3.setLocation(w - 252, h - 744);
        p4.setLocation(w - 27, h - 369);
        c = new Compound(p1, p2, p3, p4, 6, 6, 3, 4, this);
        this.addCompound(c);
        p1.setLocation(999, 459);
        p2.setLocation(954, 519);
        p3.setLocation(855, 519);
        p4.setLocation(945, 369);
        c = new Compound(p1, p2, p3, p4, 6, 6, 3, 4, this);
        this.addCompound(c);
        p1.setLocation(w - 999, h - 459);
        p2.setLocation(w - 954, h - 519);
        p3.setLocation(w - 855, h - 519);
        p4.setLocation(w - 945, h - 369);
        c = new Compound(p1, p2, p3, p4, 6, 6, 3, 4, this);
        this.addCompound(c);

        //for(Compound c1 : Compounds){
        //    Point[] points = new Point[4];
        //    for (int index = 0; index < c1.getBoundaries().size(); index++){
        //        points[index] = c1.getBoundaries().get(index).getStartNode().getScreenpoint();
        //    }
        //    for(Point p5 : points){
        //        p5.setLocation(w - p5.x, h - p5.y);
        //    }
        //    //Compound c2 = new Compound(points[0],points[1],points[2],points[3],this);
        //}
    }

    public void CalculateCoverTotals() {
        //System.out.print("Calculating cover totals.....");
        for (int x = 0; x < this.xgridmax; x++) {
            for (int y = 0; y < this.ygridmax; y++) {
                this.NodeGrid[x][y].CalculateCoverTotal();
            }
        }
        //System.out.println("Done");
    }

    public List<Node> getMovableNodes(Node n, double radius) {
        List<Node> testNodes = new ArrayList<Node>();
        if (radius == 0) {
            for (Node[] na : this.getNodeGrid()) {
                for (Node n1 : na) {
                    testNodes.add(n1);
                }
            }
        } else {
            testNodes = this.getNodesInRange(n.getScreenpoint(), radius);
        }
        List<Node> movableList = new ArrayList<Node>();
        for (Node n1 : testNodes) {
            if (this.LOMTest(n, n1) == true) {
                movableList.add(n1);
            }
        }
        return movableList;
    }

    public synchronized List<Node> getRoute(Node start, Node end) {
        //This is the A* search algorithm
        double maxReach = 40;
        Find f = new Find();
        List<Node> closedList = new ArrayList<Node>();
        List<Node> openList = new ArrayList<Node>();
        openList.add(start);
        start.setGScore(0);
        start.setFScore(f.Distance(start.getScreenpoint(), end.getScreenpoint()));
        Node current = null;
        boolean endFound = false;
        while (endFound == false) {
            current = this.getBestNode(openList);
            if (current == end) {
                endFound = true;
            }
            for (Node link : this.getMovableNodes(current, maxReach)) {
                if (closedList.contains(link) == false) {
                    double tentativeG = this.getGScore(link, current);
                    if (openList.contains(link) == false || tentativeG < link.getGScore()) {
                        link.setPreviousNode(current);
                        double g = this.getGScore(link, current);
                        link.setGScore(g);
                        //openList.add(link);
                        double h = f.Distance(link.getScreenpoint(), end.getScreenpoint());
                        double F = g + h;
                        link.setFScore(F);
                        if (openList.contains(link) == false) {
                            openList.add(link);
                        }
                    }
                }
            }
            openList.remove(current);
            closedList.add(current);
        }
        List<Node> routeNodes = new ArrayList<Node>();
        if (endFound == true) {
            Node previous = current.getPreviousNode();
            routeNodes.add(current);
            while (previous != start) {
                routeNodes.add(0, previous);
                //Below needs to be short step proofed
                previous = previous.getPreviousNode();
            }
        }
        return routeNodes;
    }

    public Node getBestNode(List<Node> nodelist) {
        //Find f = new Find();
        double lowestF = 999999999;
        Node best1 = null;
        for (Node n1 : nodelist) {
            double F = n1.getFScore();
            if (F < lowestF) {
                best1 = n1;
                lowestF = F;
            }
        }
        return best1;
    }

    public double getGScore(Node scrutinised, Node previous) {
        Find f = new Find();
        double g = 0;
        g += previous.getGScore();
        g += f.Distance(previous.getScreenpoint(), scrutinised.getScreenpoint());
        return g;
    }

    public void UpdateMoveables() {
        //long t1 = System.currentTimeMillis();
        for (Player p : this.getGame().getPlayers()) {
            p.clearDrawableObjects();
        }
        for (Player p : this.getGame().getPlayers()) {
            p.UpdateMoveables();
        }
        for (Player p : this.getGame().getPlayers()) {
            p.UpdateVisibleEnemies();
        }
        if (this.MovementCounter >= 50) {
            this.UpdateCompounds();
            this.MovementCounter = 0;
        }
        this.MovementCounter += 1;
        //long t2 = System.currentTimeMillis();
        //long t3 = t2 - t1;
        //System.out.println("Map.UpdateMoveables() took " + t3 + " milliseconds");
    }

    public void UpdateCompounds() {
        for (Compound c : this.getCompounds()) {
            //Test if compound only has 1 player inside
            if (c.getPlayersIn().size() == 1) {
                Player p = c.getPlayersIn().get(0);
                if (p.getCompounds().contains(c) == false && c.getOwner() != p) {
                    p.addCompound(c);
                    c.setOwner(p);
                    if (c.IsObjective()) {
                        this.getGame().getCommunicator().SendPacket(p, (byte) -3, 0, 0, 0006);
                    }
                    for (Player p2 : this.getGame().getPlayers()) {
                        if (p2 != p) {
                            if (p2.getCompounds().contains(c)) {
                                p2.removeCompound(c);
                                if (c.IsObjective()) {
                                    this.getGame().getCommunicator().SendPacket(p2, (byte) -3, 0, 0, 0007);
                                }
                            }
                        }
                    }
                }
            } else {
                boolean dclr = true;
                if (c.getPlayersIn().size() <= 0) {
                    //no current control at all
                    if (c.getLeavable()) {
                        //don't discolour
                        dclr = false;
                    }
                }
                if (dclr == true) {
                    //decolour for all players
                    for (Player p : this.getGame().getPlayers()) {
                        if (p.getCompounds().contains(c)) {
                            p.removeCompound(c);
                            if (c.IsObjective()) {
                                this.getGame().getCommunicator().SendPacket(p, (byte) -3, 0, 0, 0007);
                            }
                        }
                    }
                    c.setOwner(null);
                }
            }
        }
        this.getGame().UpdateResources();
        this.getGame().UpdateObjectives();
    }

    public void addMoveableObject(MoveableObject mo) {
        this.MoveableObjects.add(mo);
    }

    public void removeMoveableObject(MoveableObject mo) {
        while (this.MoveableObjects.contains(mo)) {
            this.MoveableObjects.remove(mo);
        }
    }

    public List<MoveableObject> getMoveableObjects() {
        return this.MoveableObjects;
    }

    public void SelectInBox(Player p, int x1, int y1, int x2, int y2) {
        Unit best = null;
        int high = 0;
        int maxx = Math.max(x1, x2);
        int minx = Math.min(x1, x2);
        int maxy = Math.max(y1, y2);
        int miny = Math.min(y1, y2);
        for (Unit u : p.getUnits()) {
            int indi = 0;
            for (Individual i : u.getMembers()) {
                if (i.getScreenpoint().x >= minx && i.getScreenpoint().x <= maxx) {
                    if (i.getScreenpoint().y >= miny && i.getScreenpoint().y <= maxy) {
                        indi += 1;
                    }
                }
            }
            if (indi > high) {
                high = indi;
                best = u;
            }
        }
        if (best != null) {
            best.Select();
        }
        //p.setSelectedUnit(best);
    }
    /* public double getGScore(Node start, Node scrutinised, List<Node> visited){
        Find f = new Find();
        double g = 0;
        List<Node> routeNodes = visited;
        Node n2 = scrutinised;
        if (routeNodes.size() > 0) {
            g += f.Distance(start.getScreenpoint(), routeNodes.get(0).getScreenpoint());
            if (routeNodes.size() > 1) {
                for (int index = 1; index < routeNodes.size(); index++) {
                    Point p1 = routeNodes.get(index - 1).getScreenpoint();
                    Point p2 = routeNodes.get(index).getScreenpoint();
                    g += f.Distance(p1, p2);
                }
            }
            Point p1 = routeNodes.get(routeNodes.size() - 1).getScreenpoint();
            Point p2 = n2.getScreenpoint();
            g += f.Distance(p1, p2);
        } else {
            g += f.Distance(start.getScreenpoint(), n2.getScreenpoint());
        }
        return g;
    } */
    /* public List<Node> getRoute2(Node start, Node end){
        Find f = new Find();
        //setting as zero below includes all nodes in search
        double maxReach = 40;
        List<Node> routeNodes = new ArrayList<Node>();
        List<Node> studiedNodes = new ArrayList<Node>();
        studiedNodes.add(start);
        Node n1 = start;
        boolean reachable = true;
        while (routeNodes.contains(end) == false && reachable == true){
            double lowestF = 999999999;
            Node best1 = null;
            for (Node n2 : this.getMovableNodes(n1, maxReach)){
                if (n2 == end){
                    best1 = n2;
                    //routeNodes.add(n2);
                    //System.out.println("map.getRoute added node to routeNodes");
                }
                else{
                    if (routeNodes.contains(n2) == false) {
                        if (studiedNodes.contains(n2) == false) {
                            double g = this.getGScore(start, n2, routeNodes);
                            Point p1 = n2.getScreenpoint();
                            Point p2 = end.getScreenpoint();
                            double h = f.Distance(p1, p2);
                            double F = g + h;
                            if (F < lowestF) {
                                lowestF = F;
                                best1 = n2;
                            }
                            studiedNodes.add(n2);
                        }
                        else {
                            //see if it is more efficient to
                            //remove nodes from routeNodes after earliest
                            //instance where n2 is added to studiedNodes.
                        }
                    }
                }
            }
            if (best1 != null) {
                routeNodes.add(best1);
                System.out.println("map.getRoute added node to routeNodes");
                n1 = best1;
            }
            else{
                System.out.println("map.getRoute added null Node");
                reachable = false;
            }
        }
        return routeNodes;
    } */
    /* public Node getBestConnectedNode(Node start, Node end, double radius){
        Find f = new Find();
        double lowestF = 999999999;
        Node best1 = null;
        for (Node n2 : this.getMovableNodes(start, radius)){
            List<Node> noNodes = new ArrayList<Node>();
            double g = this.getGScore(start, n2, noNodes);
            Point p1 = n2.getScreenpoint();
            Point p2 = end.getScreenpoint();
            double h = f.Distance(p1, p2);
            double F = g + h;
            if (F < lowestF){
                lowestF = F;
                best1 = n2;
            }
        }
        return best1;
    } */

    /* public List<Node> getRoute2(Node start, Node end){
        List<Node> routeNodes = new ArrayList<Node>();
        //routeNodes.add(start);
        if (this.LOMTest(start, end)){
            routeNodes.add(end);
        }
        else{
            Node best = this.getCommonlyMovableNode(start, end);
            if (best != null){
                routeNodes.add(best);
                routeNodes.add(end);
            }
            else
            {
                //probe close to end
                routeNodes.add(end);
                Node n1 = end;
                int count = 0;
                int max = 100;
                while(this.LOMTest(start, routeNodes.get(0)) == false && count < max){
                    n1 = this.getClosestEndMovableNode(start, n1);
                    routeNodes.add(0, n1);
                    Node n2 = this.getCommonlyMovableNode(start, n1);
                    if (n2 != null) routeNodes.add(0, n2);
                    count += 1;
                    if (count >= max){
                        System.out.println("map.getRoute couldn't find route with fewer than " + max + " nodes");
                    }
                }
            }
        }
        return routeNodes;
    }
    public Node getCommonlyMovableNode(Node start, Node end){
        Find f = new Find();
        Point p1 = start.getScreenpoint();
        Point p2 = end.getScreenpoint();
        int max = 0;
        for(Node[] na : this.getNodeGrid()){
            for (Node n : na){
                max += 1;
            }
        }
        Node best = null;
        double totalDist = 999999999;
        int radius = start.getSize();
        int range = (int)Math.round(f.Distance(p1, p2));
        List<Node> triedNodes = new ArrayList<Node>();
        while (best == null && triedNodes.size() < max) {
            List<Node> newNodes = this.getNodesInRange(start.getScreenpoint(), range);
            for (Node n : triedNodes){
                newNodes.remove(n);
            }
            for (Node n : newNodes) {
                //test if movable from start and end
                if (this.LOMTest(start, n) == true && this.LOMTest(n, end) == true) {
                    //System.out.println("point is movable from start and end");
                    Point p3 = n.getScreenpoint();
                    double dist2 = f.Distance(p1, p3) + f.Distance(p3, p2);
                    if (dist2 < totalDist) {
                        best = n;
                        totalDist = dist2;
                    }
                }
                triedNodes.add(n);
            }
            range += radius;
        }
        return best;
    }
    public Node getClosestEndMovableNode(Node start, Node end){
        int radius = start.getSize();
        int range = radius;
        int max = 0;
        for (Node[] na : this.getNodeGrid()) {
            for (Node n : na) {
                max += 1;
            }
        }
        List<Node> triedNodes = new ArrayList<Node>();
        Node best = null;
        Point p1 = start.getScreenpoint();
        Find f = new Find();
        while (triedNodes.size() < max && best == null){
            List<Node> newNodes = this.getNodesInRange(p1, range);
            for (Node n : triedNodes){
                newNodes.remove(n);
            }
            Node best1 = null;
            double dist1 = 999999999;
            for (Node n : newNodes){
                if (this.LOMTest(n, end) == true){
                    Point p3 = n.getScreenpoint();
                    Point p4 = end.getScreenpoint();
                    double dist2 = f.Distance(p3, p4);
                    if (dist2 < dist1){
                        dist1 = dist2;
                        best1 = n;
                    }
                }
                triedNodes.add(n);
            }
            if (best1 != null) best = best1;
            range += radius;
            if (triedNodes.size() >= max){
                System.out.println("map.getClosestEndMovableNode reached maximum and returned " + best);
            }
        }
        return best;
    } */
    /* public List<Node> getNodesOnLine3(Point p1, Point p2, int resolution){
        Find f = new Find();
        List<Node> nodelist = new ArrayList<Node>();
        Node n1 = this.getClosestNode(p1);
        Node n2 = this.getClosestNode(p2);
        Node n3 = n1;
        nodelist.add(n3);
        int count = 0;
        int max = 1000;
        while (nodelist.contains(n2) == false && count < max) {
            double sdist1 = 99999999;
            Node closest = n3;
            for (Node n4 : n3.getNeighbours()) {
                if (n4 != null) {
                    Point p3 = n3.getScreenpoint();
                    Point p4 = n4.getScreenpoint();
                    double sdist2 = f.Distance(p4, p2);
                    if (sdist2 < sdist1) {
                        sdist1 = sdist2;
                        closest = n4;
                    }
                    nodelist.add(closest);
                }
            }
            count += 1;
        }
        return nodelist;
    }
    public List<Node> getNodesOnLine2(Point p1, Point p2, int resolution){
        List<Node> nodelist = new ArrayList<Node>();
        int res = resolution;
        Point p3 = p1;
        Point p4 = p2;
        int xdiff = p4.x - p3.x;
        int ydiff = p4.y - p3.y;
        double gradient1 = 0;
        double gradient2 = 0;
        if (xdiff != 0 && ydiff != 0){
            gradient1 = ydiff / xdiff;
            gradient2 = xdiff / ydiff;
            // x direction
            for (int x = 0; x < xdiff; x += res){
                int y = (int)Math.round(x * gradient1);
                Point p5 = new Point(p3.x + x, p3.y + y);
                Node n = this.getClosestNode(p5);
                if (nodelist.contains(n) == false){
                    nodelist.add(n);
                }
            }
            // y direction
            for (int y = 0; y < ydiff; y += res){
                int x = (int)Math.round(y * gradient2);
                Point p5 = new Point(p3.x + x, p3.y + y);
                Node n = this.getClosestNode(p5);
                if (nodelist.contains(n) == false){
                    nodelist.add(n);
                }
            }
        }
        else{
            //System.out.println("getNodesOnLine: xdiff or ydiff is 0");
            Node n1 = this.getClosestNode(p3);
            Node n2 = this.getClosestNode(p4);
            if (xdiff == 0){
                //vertical
                int dir1;
                int dir2;
                if (ydiff >= 0){
                    dir1 = 2;
                    dir2 = 3;
                }
                else{
                    dir1 = 5;
                    dir2 = 0;
                }
                Node n3 = n1;
                Node n4 = n1.getNeighbours()[dir1];
                nodelist.add(n3);
                nodelist.add(n4);
                int count = 0;
                int max = 32;
                while (n3 != n2 && count < max){
                    //nodelist.add(n3);
                    n4 = n3.getNeighbours()[dir1];
                    n3 = n4.getNeighbours()[dir2];
                    nodelist.add(n4);
                    nodelist.add(n3);
                    count += 1;
                }
                //end vertical
            }
            if (ydiff == 0){
                //horizontal
                int dir;
                if (xdiff >= 0){
                    dir = 1;
                }
                else{
                    dir = 4;
                }
                Node n3 = n1;
                nodelist.add(n3);
                int count = 0;
                int max = 64;
                while (n3 != n2 && count < max){
                    //nodelist.add(n3);
                    n3 = n1.getNeighbours()[dir];
                    nodelist.add(n3);
                    count += 1;
                }
                //end horizontal
            }
        }
        //report back
        /* for (Node n : nodelist){
            Point p = n.getScreenpoint();
            System.out.print("(" + p.x + "," + p.y + "),");
        }
        System.out.println("are NodesOnLine");
        return nodelist;
    } */
}
