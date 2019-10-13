package client;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import javax.swing.Timer;

public class Individual extends MoveableObject {
    private Player Owner;
    private Unit Unit;
    private List<Weapon> Weapons = new ArrayList<Weapon>();
    //private int Confidence = 60;
    private Node Destination;
    private Individual CurrentTarget = null;
    private List<Node> Route;
    private Timer TimerRoute = new Timer(10, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            FollowRoute();
        }
    });
    private Timer TimerAI = new Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            TryAttack();
            //System.out.println("Timer AI ticked for " + Unit.getMembers().indexOf(Individual.this));
        }
    });
    private double VisualRange = 200;
    private int Rank = 0;
    private BufferedImage RankInsignia = new BufferedImage(5, 11, 2);
    private int Health = 100;
    private double Fitness = 1;
    private double Training = 1;
    private boolean IsArmoured = false;
    private double AccuracyBlind = 0.6;

    public Individual(Map mainmap, Unit u, String weaponName) {
        super();
        this.setMap(mainmap);
        this.getMap().addMoveableObject(this);
        this.Unit = u;
        u.addMember(this);
        u.getOwner().addIndividual(this);
        this.Owner = u.getOwner();
        String RouteDirectory = this.getOwner().getGame().getRouteDirectory();
        try {
            this.setMovementImages(new BufferedImage[6][4]);
            this.setStationaryImages(new BufferedImage[6]);
            for (int index1 = 0; index1 < 6; index1++) {
                for (int index2 = 0; index2 < 4; index2++) {
                    String path1 = RouteDirectory + "Images\\IndiRun_" + index2 + "_" + index1 + ".bmp";
                    this.setMvtImgRef(index1, index2, "01" + index2 + index1);
                    BufferedImage img = ImageIO.read(new File(path1));
                    //Colour white pixels transparent
                    ImageObserver o = this.getOwner().getPictureBoxLabel();
                    int width = img.getWidth(o);
                    int height = img.getHeight(o);
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
                    this.getMovementImages()[index1][index2] = img2;
                }
                String path2 = RouteDirectory + "Images\\IndiCrouch_0_" + index1 + ".bmp";
                this.setStnryImgRef(index1, "000" + index1);
                BufferedImage img2 = ImageIO.read(new File(path2));
                //Colour white pixels transparent
                ImageObserver o = this.getOwner().getPictureBoxLabel();
                int width = img2.getWidth(o);
                int height = img2.getHeight(o);
                BufferedImage img3 = new BufferedImage(width, height, 2);
                for (int w = 0; w < width; w++) {
                    for (int h = 0; h < height; h++) {
                        int c = img2.getRGB(w, h);
                        if (c != 0xFFFFFFFF) {
                            img3.setRGB(w, h, c);
                        } else {
                            img3.setRGB(w, h, 0x00000000);
                        }
                    }
                }
                //
                this.getStationaryImages()[index1] = img3;
            }
            this.addWeapon(new Weapon(this, weaponName));
            //this.setRankInsignia();
            //this.DrawRankInsignia();
        } catch (Exception e) {
            e.getMessage();
        }
        this.setAngle((this.Owner.getPlayerNumber() * 180) + 90);
        this.setImage(new BufferedImage(18, 18, 1));
        this.ShowStationaryImage();
        this.getMap().addDrawableObject(this);
        this.setStepSize(4);
        this.setScreenpoint(this.Owner.getStartNode().getScreenpoint());
        this.Draw(this.Owner);
        this.TimerAI.start();
    }

    //public void Draw(Player p){
    //
    //}
    public Player getOwner() {
        return this.Owner;
    }

    public Unit getUnit() {
        return this.Unit;
    }

    public List<Weapon> getWeapons() {
        return this.Weapons;
    }

    public void setWeapon(int index, String weaponName) {
        Weapon w = new Weapon(this, weaponName);
        this.Weapons.set(index, w);
    }

    public void addWeapon(Weapon w) {
        this.Weapons.add(w);
    }

    public double getTraining() {
        return this.Training;
    }
    public void setTraining(double d){
        this.Training = d;
    }

    //public void setConfidence(double trng) {
    //    this.Training = trng;
    //}

    public Node getDestination() {
        return this.Destination;
    }

    public void setDestination(Node destination) {
        this.Destination = destination;
    }
    //public void setPosition(Node n){
    //    this
    //}
    public Node getPosition() {
        Map m = this.getMap();
        Point scrp = this.getScreenpoint();
        Node pos = m.getClosestNode(scrp);
        return pos;
    }

    public Timer getTimerAI() {
        return this.TimerAI;
    }

    public Timer getTimerRoute() {
        return this.TimerRoute;
    }

    public double getVisualRange() {
        return this.VisualRange;
    }

    public void setVisualRange(double range) {
        this.VisualRange = range;
    }

    public int getHealth() {
        return this.Health;
    }

    public void setHealth(int health) {
        this.Health = health;
    }

    public double getFitness() {
        return this.Fitness;
    }

    public void setFitness(double d) {
        this.Fitness = d;
    }
    public double getAccuracyBlind(){
        return this.AccuracyBlind;
    }
    public void setAccuracyBlind(double acc){
        this.AccuracyBlind = acc;
    }

    public void Die() {
        //remove as target from everyone
        for (Player p : this.getMap().getGame().getPlayers()) {
            for (Individual i : p.getIndividuals()) {
                if (i.getCurrentTarget() == this) {
                    i.setCurrentTarget(null);
                }
            }
        }
        Unit u = this.getUnit();
        if (u.getCommander() == this) {
            if (u.getMembers().size() > 1) {
                u.removeMember(this);
                u.setCommander(u.getMembers().get(0));
            } else {
                u.removeMember(this);
                this.getOwner().removeUnit(u);
            }
        } else {
            u.removeMember(this);
        }
        this.getOwner().removeIndividual(this);
        this.getOwner().removeDrawableObject(this);
        this.getOwner().getGame().getMap().removeDrawableObject(this);
        this.getOwner().getGame().getMap().removeMoveableObject(this);
    }

    public boolean CanMove(Node destination) {
        boolean canmove = false;
        Map m = this.getMap();
        Node n1 = this.getPosition();
        Node n2 = destination;
        if (m.LOMTest(n1, n2)) {
            canmove = true;
        }
        return canmove;
    }

    public boolean CanSee(Point p) {
        boolean cansee = false;
        Map m = this.getMap();
        Point p1 = this.getScreenpoint();
        Point p2 = p;
        Find f = new Find();
        if (f.Distance(p1, p2) < this.getVisualRange()) {
            if (m.LOSTest(p1, p2)) {
                cansee = true;
            }
        }
        return cansee;
    }

    public boolean CanAttack(Point p) {
        boolean cntk = false;
        Point p1 = this.getScreenpoint();
        Point p2 = p;
        //int mdfr = 2;
        Find f = new Find();
        if (f.Distance(p1, p2) <= this.getWeapons().get(0).getRange()) {
            if (this.getMap().LOSTest(p1, p2) == true) {
                cntk = true;
            }
            if (this.getWeapons().get(0).getIsDirect() == false) {
                cntk = true;
            }
        }
        //System.out.println("can attack");
        return cntk;
    }

    public Node getClosestMoveableGoal(Node idealNode) {
        Find f = new Find();
        Individual i = this;
        Map m = i.getMap();
        Point p1 = i.getScreenpoint();
        Node goal = idealNode;
        if (i.CanMove(goal)) {
            //do nothing
        } else {
            boolean isNeighbour = false;
            while (i.CanMove(goal) == false && isNeighbour == false) {
                Point p2 = goal.getScreenpoint();
                Point mp = f.Middle(p1, p2);
                Node n = m.getClosestNode(mp);
                goal = n;
                //Make safe for when goal becomes unmovable neighbour
                Node[] neighbours = i.getPosition().getNeighbours();
                for (Node n2 : neighbours) {
                    if (n2 == goal) {
                        isNeighbour = true;
                        goal = i.getPosition();
                    }
                }
            }
        }
        return goal;
    }

    public Node getClosestMoveableGoal2(Node idealNode) {
        int stepsize = 10;
        Find f = new Find();
        //int steps = 0;
        Point p1 = this.getScreenpoint();
        Point p2 = idealNode.getScreenpoint();
        double dist = f.Distance(p1, p2);
        //steps = (int)Math.round(dist / stepsize);
        double a = f.Angle(p1, p2);
        double xd = (Math.sin(a) * stepsize);
        double yd = -(Math.cos(a) * stepsize);
        Node best = idealNode;
        Point trypoint = idealNode.getScreenpoint();
        Map m = this.getMap();
        int count = 1;
        while (this.CanMove(best) == false) {
            int xm = (int) Math.round(xd * count);
            int ym = (int) Math.round(yd * count);
            int x = trypoint.x - xm;
            int y = trypoint.y - ym;
            trypoint = new Point(x, y);
            best = m.getClosestNode(trypoint);
            count += 1;
        }
        return best;
    }

    public List<Node> getRoute() {
        return this.Route;
    }

    public void setRoute(List<Node> route) {
        this.Route = route;
    }

    public void InsertRouteNodes(List<Node> nodes) {
        List<Node> totalList = new ArrayList<Node>();
        for (Node n : nodes) {
            totalList.add(n);
        }
        for (Node n : this.getRoute()) {
            totalList.add(n);
        }
        this.setRoute(totalList);
    }

    public void FollowRoute() {
        //Map m = this.getMap();
        if (this.getRoute() != null) {
            if (this.getRoute().size() > 0) {
                Node n = this.getRoute().get(0);
                if (this.getPosition() == n || this.getCurrentMovement() == null) {
                    if (this.CanMove(n)) {
                        new Movement(this, n.getScreenpoint());
                        this.Route.remove(n);
                    } else {
                        this.TimerRoute.stop();
                    }
                }
            } else {
                this.TimerRoute.stop();
            }
        }
    }

    public void TimerRouteStart() {
        this.TimerRoute.start();
    }

    public void Attack(Individual i2, Weapon w) {
        //System.out.println("Individual attacked");
        Find f = new Find();
        if (this.getOwner().getVisibleEnemies().contains(i2)) {
            this.setCurrentTarget(i2);
            this.setAngle(f.Angle(this.getScreenpoint(), i2.getScreenpoint()));
            this.ShowStationaryImage();
            if (this.CanAttack(i2.getScreenpoint())) {
                //double dist = f.Distance(this.getScreenpoint(), i2.getScreenpoint());
                //if ((w.getRange() / (double)(2)) >= dist){
                if (this.getUnit().getCommander().getRank() > -1){
                    if (i2.getCurrentMovement() != null){
                        i2.setCurrentMovement(null);
                    }
                }
                Shot s = new Shot(this, i2, w);
                //System.out.println("Shot created");
            }
        }
    }

    public void TryAttack() {
        //System.out.println("Tried attack " + this.getOwner().getPlayerNumber());
        if (this.getWeapons() != null && this.getWeapons().size() > 0) {
            if (this.getWeapons().get(0).getFiresMoving() == true || this.getCurrentMovement() == null) {
                //Adjust weapon timer tick rate to match rate of fire
                int adjust = 2;
                int Delay = 500;
                Random rnd = new Random();
                int rof =
                    this.getWeapons().get(0).getMinFireDelay() +
                    (int) Math.round(rnd.nextInt(Delay) * (1 / this.getTraining()));
                if (this.getTimerTickRate(this.TimerAI) != rof) {
                    this.setTimerTickRate(this.TimerAI, (rof) * adjust);
                }
                // Find target
                Find f = new Find();
                Individual closest = null;
                double d1 = 999999999;
                if (this.getCurrentTarget() == null) {
                    //Look for enemies
                    Player p = this.getOwner();
                    List<Individual> ens = p.getVisibleEnemies();
                    //Look for closest visible enemy
                    for (Individual i : ens) {
                        //if (p.getVisibleEnemies().contains(i)) {
                        double d2 = f.Distance(this.getScreenpoint(), i.getScreenpoint());
                        if (d2 < d1) {
                            d1 = d2;
                            closest = i;
                        }
                        //}
                    }
                    //if (this.getMap().getNodesInRange(this.getScreenpoint(), (this.getWeapon().getRange() / (double)2)).contains(closest.getPosition())){
                    if (closest != null) {
                        this.setCurrentTarget(closest);
                    }
                }
                Individual t = this.getCurrentTarget();
                if (t != null) {
                    Weapon w = this.getWeapons().get(0);
                    if (w.getRange() >= f.Distance(this.getScreenpoint(), t.getScreenpoint())) {
                        this.Attack(t, w);
                        //System.out.println("Attacking");
                    }
                }
            }
        }
        //Count drawable objects
        //int members1 = 0;
        //for (List<DrawableObject> row : this.getMap().getDrawableObjects()){
        //    members1 += row.size();
        //}
        //int members2 = 0;
        //for (List<DrawableObject> row : this.getOwner().getDrawableObjects()){
        //    members2 += row.size();
        //}
        //System.out.println(members1 + ", " + members2);
        //System.out.println(this.getMap().getDrawableObjects().size());
        //System.out.println(this.getOwner().getDrawableObjects().size());
    }

    public int getTimerTickRate(Timer timer) {
        return timer.getDelay();
    }

    public void setTimerTickRate(Timer timer, int interval) {
        timer.setDelay(interval);
    }

    public Individual getCurrentTarget() {
        return this.CurrentTarget;
    }

    public void setCurrentTarget(Individual i) {
        this.CurrentTarget = i;
    }

    public int getRank() {
        return this.Rank;
    }

    public void setRank(int r) {
        this.Rank = r;
    }

    public BufferedImage getRankInsignia() {
        return this.RankInsignia;
    }
    public boolean getIsArmoured(){
        return this.IsArmoured;
    }
    public void setIsArmoured(boolean val){
        this.IsArmoured = val;
    }
    /* public void setRankInsignia(){
        String path =
            this.getOwner().getGame().getRouteDirectory() + "Images\\Rank Insignia\\Rank Insignia_" + this.getRank() +
            ".bmp";
        BufferedImage insg = new BufferedImage(5, 11, 2);
        try {
            insg = ImageIO.read(new File(path));
            this.RankInsignia = insg;
        } catch (Exception e) {
            e.printStackTrace();
        }
    } */
    public void DrawRankInsignia() {
        //Colour insignia
        int c = 0xFF007FFF;
        BufferedImage img = this.getImage();
        BufferedImage insg = this.getRankInsignia();
        for (int w = 0; w < insg.getWidth(); w++) {
            for (int h = 0; h < insg.getHeight(); h++) {
                if (insg.getRGB(w, h) == 0xFF000000) {
                    //int fx = x + w;
                    //int fy = y + h;
                    if (w >= 0 && h >= 0) {
                        if (w < img.getWidth() && h < img.getHeight()) {
                            img.setRGB(w, h, c);
                        }
                    }
                }
            }
        }
        this.setImage(img);
        //this.setImgRef(ref);
    }

    public void CalculateSetSpeed() {
        if (this.getWeapons().get(0) != null) {
            double wght = this.getWeapons().get(0).getWeight();
            //Increase or decrease speed relative to standard weight
            double strd = 4000;
            this.setStepSize((strd / wght) * this.getStepSize() * this.getFitness());
        }
    }
}
