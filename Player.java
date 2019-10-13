package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import java.net.Socket;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;


public class Player {
    private Socket Socket = null;
    private Game Game;
    private boolean IsAIPlayer = false;
    private Player CurrentPlayer;
    //private Map Map;
    private List<Unit> Units = new ArrayList<Unit>();
    private List<Individual> Individuals = new ArrayList<Individual>();
    //private int PlayerNumber;
    private Node StartNode;
    private JLabel Viewfinder;
    private Point[] ViewfinderCorners = new Point[2];
    private JLabel PictureBoxLabel;
    private Frame1 GameFrame;
    private Unit SelectedUnit;
    private BufferedImage Terrain;
    private List<Node> Nodes = new ArrayList<Node>();
    private List<List<DrawableObject>> DrawableObjects;
    private List<Node> NodesHistory;
    private List<DrawableObject> DrawablesHistory;
    private List<Individual> VisibleEnemies = new ArrayList<Individual>();
    private List<Compound> Compounds = new ArrayList<Compound>();
    private int Resource1 = 0;
    private int Resource2 = 0;
    private List<Bullet> Bullets = new ArrayList<Bullet>();
    private Timer TimerGraphics2 = new Timer(100, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (GraphicsTick > 4) {
                DrawFOW();
                GraphicsTick -= 5;
            }
            UpdateVisibleEnemies();
            GraphicsTick += 1;
        }
    });
    //private Timer TimerGraphics = new Timer(100, new ActionListener() {
    //    public void actionPerformed(ActionEvent e) {
    //        timerGraphics_Tick();
    //    }
    //});
    private int GraphicsTick = 0;
    /* private Timer TimerFOW = new Timer(500, new ActionListener() {
        //TimerFOW seems to get switched on but doesn't tick????
        public void actionPerformed(ActionEvent e) {
            //timerFOW_Tick();
            DrawFOW();
        }
    }); */
    private int WinCondition;
    private JPanel PanelUnitInfo;
    private JLabel LabelUnitInfo;
    private boolean HasFogOfWar = true;

    public Player(Game g) {
        this.setGame(g);
        this.getGame().addPlayer(this);
        this.setWinCondition(g.getWinCondition());
        //this.StartLevel();
        //this.TimerFOW.start();
    }

    public void StartLevel() {
        this.Viewfinder = new JLabel();
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        this.Viewfinder.setBounds(new Rectangle(4, 4, screensize.width - 8, screensize.height - 86));
        this.GameFrame = null;
        this.PictureBoxLabel = new JLabel();
        this.PictureBoxLabel.setBounds(this.getGame().getLevel().getMap().getGameBoardDimensions());
        this.Terrain = new BufferedImage(this.PictureBoxLabel.getWidth(), this.PictureBoxLabel.getHeight(), 1);
        for (int x = 0; x < this.Terrain.getWidth(); x++) {
            for (int y = 0; y < this.Terrain.getHeight(); y++) {
                this.Terrain.setRGB(x, y, 0xFF000000);
            }
        }
        this.StartNode = this.getGame().getMap().getClosestNode(new Point(0, 200));
        if (this.getPlayerNumber() / 2 != this.getPlayerNumber() / (double) 2) {
            int x = this.getTerrain().getWidth() - this.StartNode.getScreenpoint().x;
            int y = this.getTerrain().getHeight() - this.StartNode.getScreenpoint().y;
            Point p = new Point(x, y);
            this.StartNode = this.getGame().getMap().getClosestNode(p);
        }
        this.Viewfinder.add(this.PictureBoxLabel);
        //this.GameFrame.getContentPane().add(this.Viewfinder);
        int value = this.Terrain.getHeight() / this.getGame().getMap().getGraphicsLayerOffset();
        this.DrawableObjects = new ArrayList<List<DrawableObject>>(value);
        for (int index = 0; index < value; index++) {
            List<DrawableObject> row = new ArrayList<DrawableObject>();
            this.DrawableObjects.add(row);
        }
        this.InitializeTerrain();
        this.addBullet(new Bullet("9mm NATO"));
        this.addBullet(new Bullet("5.56 NATO"));
        this.addBullet(new Bullet("7.62 NATO"));
        this.addBullet(new Bullet("7.62*39mm"));
        //this.TimerGraphics.start();
        //this.TimerGraphics2.start();
    }

    public void DrawFOW() {
        //if (this.getIsAIPlayer() == false) {
        //FOWDrawer fowd = new FOWDrawer(this);
        //ExecutorService es = this.getGame().getExecutorService();
        //if (es == null) {
        //    System.out.println("Executor Service is null");
        //}
        //es.submit(fowd);
        //}
    }

    public Socket getSocket() {
        return this.Socket;
    }

    public void setSocket(Socket skt) {
        this.Socket = skt;
    }

    public Game getGame() {
        return this.Game;
    }

    public void setGame(Game g) {
        this.Game = g;
    }

    public boolean getIsAIPlayer() {
        return this.IsAIPlayer;
    }

    public void setIsAIPlayer(boolean val) {
        this.IsAIPlayer = val;
    }

    public Player getCurrentPlayer() {
        return this.CurrentPlayer;
    }

    public void setCurrentPlayer(Player p) {
        this.CurrentPlayer = p;
    }

    public Timer getTimerGraphics2() {
        return this.TimerGraphics2;
    }

    /* public Timer getTimerFOW() {
        return this.TimerFOW;
    }
    public void startTimerFOW(){
        this.TimerFOW.start();
    } */
    /* public Map getMap() {
        return this.Map;
    } */

    public List<Unit> getUnits() {
        return this.Units;
    }

    public Unit getSelectedUnit() {
        return this.SelectedUnit;
    }

    public void setSelectedUnit(Unit u) {
        this.SelectedUnit = u;
    }

    public List<Individual> getIndividuals() {
        return this.Individuals;
    }

    public void addUnit(Unit u) {
        this.Units.add(u);
    }

    public void removeUnit(Unit u) {
        this.Units.remove(u);
    }

    public void addIndividual(Individual i) {
        this.Individuals.add(i);
    }

    public void removeIndividual(Individual i) {
        this.Individuals.remove(i);
    }

    public List<Compound> getCompounds() {
        return this.Compounds;
    }

    public void addCompound(Compound c) {
        this.Compounds.add(c);
    }

    public void removeCompound(Compound c) {
        this.Compounds.remove(c);
    }

    public int getPlayerNumber() {
        int num = 999;
        Game g = this.getGame();
        List<Player> players = g.getPlayers();
        if (players.contains(this)) {
            for (int index = 0; index < players.size(); index++) {
                if (players.get(index) == this) {
                    num = index;
                }
            }
        }
        return num;
    }

    public int getWinCondition() {
        return this.WinCondition;
    }

    public void setWinCondition(int val) {
        this.WinCondition = val;
    }
    //public void setPlayerNumber(int number) {
    //    this.PlayerNumber = number;
    //}

    public Node getStartNode() {
        return this.StartNode;
    }

    public void setStartNode(Node n) {
        this.StartNode = n;
    }

    public JLabel getViewfinder() {
        return this.Viewfinder;
    }

    public Point[] getViewfinderCorners() {
        return this.ViewfinderCorners;
    }

    public void setViewfinderCorners(Point p1, Point p2) {
        Point[] pa = new Point[2];
        pa[0] = p1;
        pa[1] = p2;
        this.ViewfinderCorners = pa;
    }

    public JLabel getPictureBoxLabel() {
        return this.PictureBoxLabel;
    }

    public BufferedImage getTerrain() {
        return this.Terrain;
    }

    public int getResource1() {
        return this.Resource1;
    }

    public void setResource1(int val) {
        this.Resource1 = val;
    }

    public int getResource2() {
        return this.Resource2;
    }

    public void setResource2(int val) {
        this.Resource2 = val;
    }

    public List<Bullet> getBullets() {
        return this.Bullets;
    }

    public void addBullet(Bullet b) {
        this.Bullets.add(b);
    }

    public Bullet getBullet(String name) {
        Bullet b1 = null;
        for (Bullet b2 : this.Bullets) {
            if (b2.getName() == name) {
                b1 = b2;
            }
        }
        return b1;
    }

    public boolean getHasFogOfWar() {
        return this.HasFogOfWar;
    }

    public List<Individual> getVisibleEnemies() {
        //System.out.println("Visible enemies is " + this.VisibleEnemies.size() + " long.");
        return this.VisibleEnemies;
    }

    public void addVisibleEnemy(Individual i) {
        this.VisibleEnemies.add(i);
    }

    public void UpdateVisibleEnemies() {
        List<Individual> visibles = new ArrayList<Individual>();
        for (Individual i1 : this.getIndividuals()) {
            for (Individual i2 : this.getAllEnemies()) {
                if (visibles.contains(i2) == false) {
                    if (i1.CanSee(i2.getScreenpoint())) {
                        visibles.add(i2);
                    }
                }
            }
        }
        this.VisibleEnemies = visibles;
        //Add visible enemies to drawable objects

        //this.DrawableObjects.clear();
        for (Individual i : this.VisibleEnemies) {
            this.addDrawableObject(i);
        }
    }

    public List<Individual> getAllEnemies() {
        List<Individual> enemies = new ArrayList<Individual>();
        for (Player p : this.getGame().getPlayers()) {
            if (p != this) {
                for (Individual i1 : p.getIndividuals()) {
                    enemies.add(i1);
                }
            }
        }
        return enemies;
    }

    public void InitializeTerrain() {
        //Copy Drawable objects from map
        Map m = this.getGame().getMap();
        for (List<DrawableObject> row : m.getDrawableObjects()) {
            int index1 = m.getDrawableObjects().indexOf(row);
            for (DrawableObject o : row) {
                this.DrawableObjects.get(index1).add(o);
            }
        }
    }

    public boolean CanSee(Point p) {
        boolean canSee = false;
        for (Individual i : this.getIndividuals()) {
            if (i.CanSee(p)) {
                canSee = true;
            }
        }
        return canSee;
    }
    //public List<DrawableObject>
    public synchronized List<List<DrawableObject>> getDrawableObjects() {
        return this.DrawableObjects;
    }

    public synchronized List<DrawableObject> getDrawablesHistory() {
        return this.DrawablesHistory;
    }

    public synchronized void setDrawableHistory(List<DrawableObject> dos) {
        this.DrawablesHistory = dos;
    }

    public synchronized void removeDrawableObject(DrawableObject o) {
        // remove current instances of this object
        if (this.DrawableObjects != null) {
            for (int index1 = 0; index1 < this.DrawableObjects.size(); index1++) {
                if (this.DrawableObjects.get(index1) != null) {
                    while (this.DrawableObjects.get(index1).contains(o)) {
                        this.DrawableObjects.get(index1).remove(o);
                    }
                }

            }
        }
        /* boolean contains = false;
        for (int index = 0; index < this.DrawableObjects.size(); index++) {
            List<DrawableObject> rowlist = this.DrawableObjects.get(index);
            if (rowlist != null) {
                //if (contains == false) {
                    if (rowlist.contains(o)) {
                        contains = true;
                        this.DrawableObjects.get(index).remove(o);
                    }
                //}
            }
        } */
    }

    public synchronized void addDrawableObject(DrawableObject o) {
        //this.removeDrawableObject(o);
        //add object to correct row of DrawableObjects
        Map m = this.getGame().getMap();
        int g = m.getGraphicsLayerOffset();
        int layer = (int) Math.floor(o.getScreenpoint().y / g);
        if (this.DrawableObjects.size() > layer) {
            List<DrawableObject> lyr = this.DrawableObjects.get(layer);
            if (lyr != null) {
                if (lyr.contains(o) == false) {
                    lyr.add(o);
                }
            }
        }
    }

    public synchronized void clearDrawableObjects() {
        for (List<DrawableObject> dobl : this.getDrawableObjects()) {
            dobl.clear();
        }
        //this.DrawableObjects.clear();
    }

    public synchronized void addNode(Node n) {
        //this.removeNode(n);
        if (this.Nodes.contains(n) == false) {
            this.Nodes.add(n);
        }
    }

    public synchronized void removeNode(Node n) {
        while (this.Nodes.contains(n)) {
            this.Nodes.remove(n);
        }
    }

    public synchronized List<Node> getNodes() {
        //System.out.println("getNodes is " + this.Nodes.size() + " long");
        return this.Nodes;
    }

    public synchronized List<Node> getNodesHistory() {
        return this.NodesHistory;
    }

    public synchronized void setNodesHistory(List<Node> ns) {
        this.NodesHistory = ns;
    }

    public synchronized void UpdateTerrain() {
        Unit u = this.getSelectedUnit();
        boolean selected = false;
        if (u != null) {
            selected = true;
        }
        for (Node n : this.Nodes) {
            n.Draw(this);
        }
        for (List<DrawableObject> row : this.DrawableObjects) {
            for (DrawableObject o : row) {
                //Check object is not an invisible enemy
                if (this.getAllEnemies().contains(o) == false || this.getVisibleEnemies().contains(o)) {
                    o.Draw(this);
                }
                //Draw rank insignia
                if (selected == true) {
                    if (u.getMembers().contains(o)) {
                        Individual i = (Individual) o;
                        i.DrawRankInsignia();
                    }
                }
            }
        }
        //long time3 = System.currentTimeMillis();
        //long diff2 = time3 - time2;
        //System.out.println("o took " + diff2);
    }

    public void UpdateMoveables() {
        for (Individual i : this.getIndividuals()) {
            if (i.getCurrentMovement() != null) {
                i.getCurrentMovement().Update();
            } else {
                this.addDrawableObject(i);
                for (Player p2 : this.getGame().getPlayers()) {
                    if (p2 != this) {
                        if (p2.CanSee(i.getScreenpoint())) {
                            p2.addDrawableObject(i);
                        }
                    }
                }
            }
        }
    }

    private void UpdateUnitInfo() {
        Unit u = this.getSelectedUnit();
        PanelUnitInfo.setVisible(true);
        LabelUnitInfo.setVisible(true);
        BufferedImage mainImage = new BufferedImage(100, 100, 2);
        Graphics g = mainImage.getGraphics();
        if (u != null) {
            if (u.getMembers() != null) {
                for (Individual i : u.getMembers()) {
                    int index = u.getMembers().indexOf(i);
                    int y = (index * 19);
                    g.setColor(Color.white);
                    g.fillRect(0, y, 18, 18);
                    g.drawImage(i.getImage(), 0, y, null);
                    g.drawImage(i.getWeapons().get(0).getImage(), 19, y, null);
                }
                Graphics g2 = LabelUnitInfo.getGraphics();
                g2.drawImage(mainImage, 0, 0, null);
            }
        }
    }

    public void PBLMouseClicked(MouseEvent e) {
        System.out.println("Mouse clicked successfully");
        Node n;
        Find f = new Find();
        n = this.getGame().getMap().getClosestNode(e.getPoint());
        if (e.getButton() == MouseEvent.BUTTON3) {
            //Check if targetable
            Unit u = this.getSelectedUnit();
            Individual target = null;
            //Get all enemies
            for (Individual i : this.getAllEnemies()) {
                //Find if enemy was clicked on
                if (this.getGame().getMap().getClosestNode(e.getPoint()) == i.getPosition()) {
                    //Aquire target
                    target = i;
                    //Test if movement is needed
                    boolean mvng = true;
                    for (Individual i2 : u.getMembers()) {
                        if (i2.CanAttack(target.getScreenpoint())) {
                            mvng = false;
                        }
                    }
                    if (mvng == true) {
                        //Atack Move
                        u.AttackMove(target);
                    } else {
                        //Shoot straight away
                        u.Attack(target);
                    }
                }
            }
            if (u != null) {
                if (target == null) {
                    //Move
                    Point p1 = u.getCommander().getScreenpoint();
                    Point p2 = n.getScreenpoint();
                    int a = (int) Math.round(f.Angle(p1, p2));
                    System.out.print(a + " ");
                    System.out.println(n.getScreenpoint().toString());
                    u.PlanMove(n);
                    u.StartMove();
                }
            } else {
                System.out.println("SelectedUnit is null");
            }
        }
        if (e.getButton() == MouseEvent.BUTTON1) {
            //Select Unit
            Map m = this.getGame().getMap();
            for (Unit u : this.getUnits()) {
                for (Individual i : u.getMembers()) {
                    if (i.getPosition() == m.getClosestNode(e.getPoint())) {
                        u.Select();
                    }
                }
            }
        }
        //Below tests map.getRoute();
        //if (e.getButton() == MouseEvent.BUTTON1) {
        //    Unit u = this.getSelectedUnit();
        //    Map m = this.getGame().getMap();
        //    if (u != null) {
        //        Individual i = u.getCommander();
        //        Node n1 = i.getPosition();
        //        List<Node> nodelist = m.getRoute(n1, n);
        //        for (Node n3 : nodelist) {
        //            n3.Darken();
        //        }
        //    }
        //}
    }

    public void PBLMousePressed(MouseEvent e) {

    }

    public void PBLMouseReleased(MouseEvent e) {

    }

    public void PBLMouseEntered(MouseEvent e) {

    }

    public void PBLMouseExited(MouseEvent e) {

    }

    public void NewUnit(String type) {
        Unit u = new Unit(this);
        u.Select();
        //
        if (type == "Militia") {
            Individual i1 = new Individual(this.getGame().getMap(), u, "MP5A3");
            Individual i2 = new Individual(this.getGame().getMap(), u, "MP5A3");
            Individual i3 = new Individual(this.getGame().getMap(), u, "MP5A3");
            Individual i4 = new Individual(this.getGame().getMap(), u, "MP5A3");
            i1.setFitness(0.5);
            i2.setFitness(0.4);
            i3.setFitness(0.3);
            i4.setFitness(0.4);
            u.setMaximumRadius(18);
            u.setCommander(i1);
            i1.setRank(1);
            u.CalculateSetSpeed(false);
        }
        if (type == "Scout") {
            Individual i1 = new Individual(this.getGame().getMap(), u, "SA80");
            Individual i2 = new Individual(this.getGame().getMap(), u, "SA80");
            Individual i3 = new Individual(this.getGame().getMap(), u, "SA80");
            Individual i4 = new Individual(this.getGame().getMap(), u, "SA80");
            u.setMaximumRadius(60);
            u.setFormation(1);
            u.setCommander(i1);
            i1.setRank(2);
            i2.setRank(1);
            i3.setRank(1);
            i4.setRank(1);
            u.CalculateSetSpeed(true);
        }
        if (type == "Sniper") {
            Individual i1 = new Individual(this.getGame().getMap(), u, "M40A5");
            i1.setVisualRange(400);
            Individual i2 = new Individual(this.getGame().getMap(), u, "SA80");
            i2.setVisualRange(300);
            u.setMaximumRadius(36);
            u.setCommander(i1);
            i1.setRank(1);
            i2.setRank(1);
            u.CalculateSetSpeed(true);
        }
        if (type == "Machine Gun") {
            Individual i1 = new Individual(this.getGame().getMap(), u, "M240B");
            Individual i2 = new Individual(this.getGame().getMap(), u, "SA80");
            i2.setVisualRange(300);
            u.setMaximumRadius(18);
            u.setCommander(i1);
            i1.setRank(1);
            i2.setRank(1);
            i2.getWeapons().get(0).setFiresMoving(false);
            u.CalculateSetSpeed(true);
        }
        if (type == "Riflemen") {
            Individual i1 = new Individual(this.getGame().getMap(), u, "M249P");
            Individual i2 = new Individual(this.getGame().getMap(), u, "M16");
            Individual i3 = new Individual(this.getGame().getMap(), u, "M16");
            Individual i4 = new Individual(this.getGame().getMap(), u, "M16");
            u.setCommander(i1);
            u.setMaximumRadius(36);
            i1.setRank(2);
            i2.setRank(1);
            i3.setRank(1);
            i4.setRank(1);
            u.CalculateSetSpeed(true);
        }
        if (type == "Grenadiers") {
            Individual i1 = new Individual(this.getGame().getMap(), u, "M79");
            Individual i2 = new Individual(this.getGame().getMap(), u, "M79");
            Individual i3 = new Individual(this.getGame().getMap(), u, "M79");
            u.setCommander(i1);
            u.setMaximumRadius(36);
            i1.setRank(2);
            i2.setRank(1);
            i3.setRank(1);
            u.CalculateSetSpeed(true);
        }
        if (type == "Warrior") {
            Individual i1 = new Individual(this.getGame().getMap(), u, "WRRB");
            Individual i2 = new Individual(this.getGame().getMap(), u, "L21A1");
            u.setCommander(i2);
            u.setMaximumRadius(4);
            i1.setRank(1);
            i2.setRank(3);
            i1.setStepSize(4);
            i2.setStepSize(4);
            i1.setLinkedObject(i2);
            i2.setIsLinkedObject(true);
            i1.setIsArmoured(true);
            i2.setIsArmoured(true);
            for (int d = 0; d < 6; d++) {
                for (int n = 0; n < 4; n++) {
                    i1.setMvtImgRef(d, n, "020" + d);
                }
                i1.setStnryImgRef(d, "020" + d);
            }
            for (int d = 0; d < 6; d++) {
                for (int n = 0; n < 4; n++) {
                    i2.setMvtImgRef(d, n, "030" + d);
                }
                i2.setStnryImgRef(d, "030" + d);
            }
        }
        if (type == "Mujahideen") {
            Individual i1 = new Individual(this.getGame().getMap(), u, "AK47");
            Individual i2 = new Individual(this.getGame().getMap(), u, "AK47");
            Individual i3 = new Individual(this.getGame().getMap(), u, "AK47");
            u.setCommander(i1);
            u.setMaximumRadius(36);
            i1.setRank(2);
            i2.setRank(1);
            i3.setRank(1);
            u.CalculateSetSpeed(true);
        }
        //
        //u.PlanMove(u.getCommander().getPosition().getNeighbours()[1]);
    }
    /* public void NewMilitia(){
        Unit u = new Unit(this);
        u.Select();
        Individual i1 = new Individual(this.getGame().getMap(), u, "MP5A3");
        Individual i2 = new Individual(this.getGame().getMap(), u, "MP5A3");
        Individual i3 = new Individual(this.getGame().getMap(), u, "MP5A3");
        Individual i4 = new Individual(this.getGame().getMap(), u, "MP5A3");
        u.setMaximumRadius(36);
        u.setCommander(i1);
        i1.setRank(1);
        u.CalculateSetSpeed(false);
        //Slow down the militia because they are fat
        for(Individual i : u.getMembers()){
            i.setStepSize(i.getStepSize() * 0.6);
        }
        //i1.setRankInsignia();
    }
    public void NewScout(){
        Unit u = new Unit(this);
        u.Select();
        Individual i1 = new Individual(this.getGame().getMap(), u, "FAMAS");
        Individual i2 = new Individual(this.getGame().getMap(), u, "FAMAS");
        Individual i3 = new Individual(this.getGame().getMap(), u, "FAMAS");
        Individual i4 = new Individual(this.getGame().getMap(), u, "FAMAS");
        u.setMaximumRadius(60);
        u.setCommander(i1);
        i1.setRank(2);
        i2.setRank(1);
        i3.setRank(1);
        i4.setRank(1);
        u.CalculateSetSpeed(true);
    }
    public void NewSniper(){
        Unit u = new Unit(this);
        u.Select();
        Individual i1 = new Individual(this.getGame().getMap(), u, "M40A3");
        i1.setVisualRange(400);
        Individual i2 = new Individual(this.getGame().getMap(), u, "M4A1");
        u.setMaximumRadius(36);
        u.setCommander(i1);
        i1.setRank(2);
        i2.setRank(1);
        u.CalculateSetSpeed(true);
    } */
    /* public Player(Frame1 frame1, Game g) {
        super();
        //this.Map = frame1.getMainMap();
        this.setGame(g);
        this.getGame().addPlayer(this);
        this.Viewfinder = new JLabel();
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        this.Viewfinder.setBounds(new Rectangle(4, 4, screensize.width - 8, screensize.height - 86));
        //this.Viewfinder.setBounds(new Rectangle(4, 4, 800, 800));
        this.GameFrame = frame1;
        this.setCurrentPlayer(this);
        this.GameFrame.setPlayerServed(this);
        this.PictureBoxLabel = new JLabel();
        this.PictureBoxLabel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                PBLMouseClicked(e);
            }

            public void mousePressed(MouseEvent e) {
                PBLMousePressed(e);
            }

            public void mouseReleased(MouseEvent e) {
                PBLMouseReleased(e);
            }

            public void mouseEntered(MouseEvent e) {
                PBLMouseEntered(e);
            }

            public void mouseExited(MouseEvent e) {
                PBLMouseExited(e);
            }
        });
        this.PictureBoxLabel.setBounds(this.getGame().getMap().getGameBoardDimensions());
        this.Terrain = new BufferedImage(this.PictureBoxLabel.getWidth(), this.PictureBoxLabel.getHeight(), 1);
        for (int x = 0; x < this.Terrain.getWidth(); x++) {
            for (int y = 0; y < this.Terrain.getHeight(); y++) {
                this.Terrain.setRGB(x, y, 0xFF000000);
            }
        }
        this.StartNode = this.getGame().getMap().getClosestNode(new Point(0, 200));
        if (this.getPlayerNumber() / 2 == this.getPlayerNumber() / (double) 2) {
            int x = this.getTerrain().getWidth() - this.StartNode.getScreenpoint().x;
            int y = this.getTerrain().getHeight() - this.StartNode.getScreenpoint().y;
            Point p = new Point(x, y);
            this.StartNode = this.getGame().getMap().getClosestNode(p);
        }
        this.Viewfinder.add(this.PictureBoxLabel);
        //ClientCommunicator cc = new ClientCommunicator(this.GameFrame, this.Game);
        this.GameFrame.getContentPane().add(this.Viewfinder);
        //
        this.PanelUnitInfo = new JPanel();
        this.PanelUnitInfo.setBounds(new Rectangle(260, screensize.height - 78, 294, 78));
        this.PanelUnitInfo.setLayout(null);
        this.PanelUnitInfo.setVisible(false);
        this.GameFrame.add(this.PanelUnitInfo);
        this.LabelUnitInfo = new JLabel();
        this.LabelUnitInfo.setBounds(0, 0, 100, 100);
        this.PanelUnitInfo.add(this.LabelUnitInfo);
        //
        int value = this.Terrain.getHeight() / this.getGame().getMap().getGraphicsLayerOffset();
        this.DrawableObjects = new ArrayList<List<DrawableObject>>(value);
        for (int index = 0; index < value; index++) {
            List<DrawableObject> row = new ArrayList<DrawableObject>();
            this.DrawableObjects.add(row);
        }
        this.InitializeTerrain();
    } */

    /*  public void timerGraphics_Tick() {
        System.out.println("Timer graphics ticked");
        this.UpdateVisibleEnemies();
        //this.UpdateTerrain();
        //this.UpdateUnitInfo();
        //this.DrawFOW();
        //this.DrawVisibleTerrain();
        JLabel pblabel = this.PictureBoxLabel;
        Point p = pblabel.getLocation();
        BufferedImage terrain = this.Terrain.getSubimage(0, 0, this.Terrain.getWidth(), this.Terrain.getHeight());
        BufferedImage bi = terrain.getSubimage(-p.x, -p.y, this.Viewfinder.getWidth(), this.Viewfinder.getHeight());
        //Graphics g = pblabel.getGraphics();
        Graphics g2 = this.Viewfinder.getGraphics();
        //g.drawImage(terrain, 0, 0, pictureBoxLabel);
        //g.drawImage(bi, -p.x, -p.y, pictureBoxLabel);
        //
        g2.drawImage(bi, 0, 0, this.Viewfinder);
    } */
}
