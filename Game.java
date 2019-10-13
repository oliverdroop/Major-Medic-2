package client;

import java.awt.image.BufferedImage;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;


public class Game {
    //private Map Map;
    private Level Level = null;
    private volatile List<Player> Players = new ArrayList<Player>();
    private String RouteDirectory;
    private BufferedImage FOWBlack = new BufferedImage(1, 1, 2);
    private BufferedImage FOWGrey = new BufferedImage(1, 1, 2);
    //private int Level = 0;
    private SoundHolder SoundHolder;
    private ImageHolder ImageHolder;
    private ExecutorService ExecutorService = Executors.newCachedThreadPool();
    private Communicator Communicator;
    private int WinCondition = 6;
    public Game() {
        super();
        this.setRouteDirectory("D:\\Program Files\\Major Medic 2\\");
        Communicator c = new Communicator(this);
        this.Communicator = c;
        System.out.println("New communicator");
        this.LoadFOWImages();
        SoundHolder sh = new SoundHolder(this);
        this.setSoundHolder(sh);
        ImageHolder ih = new ImageHolder(this.RouteDirectory);
        this.setImageHolder(ih);
        //new Level(this, 0);
        //this.setLevel(levl);
        //
        //
        //this.GenerateEnemies();
    }
    
    public ExecutorService getExecutorService(){
        return this.ExecutorService;
    }

    public Map getMap() {
        return this.getLevel().getMap();
    }

    //public void setMap(Map m) {
    //    this.getLevel().setMap(m);
    //}
    public Level getLevel(){
        return this.Level;
    }
    public void setLevel(Level lvl){
        this.Level = lvl;
    }

    public synchronized List<Player> getPlayers() {
        return this.Players;
    }

    public synchronized Player getPlayer(int index) {
        return this.Players.get(index);
    }

    public synchronized void addPlayer(Player p) {
        this.Players.add(p);
    }
    public synchronized void removePlayer(Player p) {
        this.Players.remove(p);
    }

    public String getRouteDirectory() {
        return this.RouteDirectory;
    }

    public void setRouteDirectory(String s) {
        this.RouteDirectory = s;
    }
    
    public SoundHolder getSoundHolder(){
        return this.SoundHolder;
    }
    public void setSoundHolder(SoundHolder sh){
        this.SoundHolder = sh;
    }
    
    public int getWinCondition(){
        return this.WinCondition;
    }
    public BufferedImage getFOWBlack() {
        return this.FOWBlack;
    }

    public BufferedImage getFOWGrey() {
        return this.FOWGrey;
    }
    public void LoadFOWImages() {
        String routeDirectory = this.getRouteDirectory();
        String pathB = "Images\\Fog_Of_War_Black.bmp";
        String pathG = "Images\\Fog_Of_War_Grey.bmp";
        try {
            BufferedImage imgB;
            BufferedImage imgG;
            //BufferedImage imgB = new BufferedImage(1, 1, 2);
            //BufferedImage imgG = new BufferedImage(1, 1, 2);
            imgB = ImageIO.read(new File(routeDirectory + pathB));
            BufferedImage imgB2 = new BufferedImage(imgB.getWidth(), imgB.getHeight(), 2);
            for (int x = 0; x < imgB.getWidth(); x++) {
                for (int y = 0; y < imgB.getHeight(); y++) {
                    int c = imgB.getRGB(x, y);
                    if (c != 0xFFFFFFFF) {
                        imgB2.setRGB(x, y, c);
                    } else {
                        imgB2.setRGB(x, y, 0x00000000);
                    }
                }
            }
            imgB = imgB2;

            imgG = ImageIO.read(new File(routeDirectory + pathG));
            BufferedImage imgG2 = new BufferedImage(imgG.getWidth(), imgG.getHeight(), 2);
            for (int x = 0; x < imgG.getWidth(); x++) {
                for (int y = 0; y < imgG.getHeight(); y++) {
                    int c = imgG.getRGB(x, y);
                    if (c != 0xFFFFFFFF) {
                        imgG2.setRGB(x, y, c);
                    } else {
                        imgG2.setRGB(x, y, 0x00000000);
                    }
                }
            }
            imgG = imgG2;

            this.FOWBlack = imgB;
            this.FOWGrey = imgG;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public void GenerateEnemies() {
        Player p = new Player(this);
        try {
            p.setSocket(new Socket(InetAddress.getLocalHost(), 3705));
        } catch (IOException ioe) {
            ioe.getMessage();
        }
        if (this.Level.getLevelNumber() > 0){
            p.setStartNode(this.getMap().getNodeGrid()[20][20]);
            Unit u = new Unit(p);
            Individual i0 = new Individual(this.getMap(), u, "MP5A3");
            Individual i1 = new Individual(this.getMap(), u, "MP5A3");
            u.setCommander(i0);
            //i0.setPosition(this.getMap().getNodeGrid()[20][20]);
            //i1.setPosition(this.getMap().getNodeGrid()[21][20]);
            i0.setScreenpoint(this.getMap().getNodeGrid()[20][20].getScreenpoint());
            i1.setScreenpoint(this.getMap().getNodeGrid()[21][20].getScreenpoint());
            //p.getTimerGraphics().start();
        }
    }
    */
    public ImageHolder getImageHolder(){
        return this.ImageHolder;
    }
    public void setImageHolder(ImageHolder ih){
        this.ImageHolder = ih;
    }
    public Communicator getCommunicator(){
        return this.Communicator;
    }

    public void UpdateObjectives() {
        for (int indx = 0; indx < this.getPlayers().size(); indx++) {
            Player p = this.getPlayers().get(indx);
            int cnt = 0;
            int win = this.getMap().getObjectives().size();
            for (Compound c : this.getMap().getObjectives()) {
                if (c.getOwner() == p) {
                    cnt += 1;
                }
            }
            if (cnt >= win) {
                int num1 = p.getWinCondition();
                if (num1 > 0) {
                    num1 -= 1;
                    p.setWinCondition(num1);
                    System.out.println("player " + indx + " is " + p.getWinCondition() + " from winning");
                    this.getCommunicator().SendPacket(p, (byte) -3, 0, 0, 0003);
                    for(Player p2 : this.getPlayers()){
                        if (p2 != p){
                            this.getCommunicator().SendPacket(p2, (byte) -3, 0, 0, 0002);
                        }
                    }
                } else {
                    //player has won the game
                    System.out.println("player " + indx + " has won the game");
                    this.getCommunicator().SendPacket(p, (byte) -3, 0, 0, 0005);
                    for (Player p2 : this.getPlayers()) {
                        if (p2 != p) {
                            this.getCommunicator().SendPacket(p2, (byte) -3, 0, 0, 0004);
                        }
                    }
                }
            }
            this.getCommunicator().SendResources(p);
        }
    }
    public void UpdateResources() {
        if (this.getLevel() != null) {
            if (this.getMap() != null) {
                for (Compound c : this.getMap().getCompounds()) {
                    if (c.getOwner() != null) {
                        Player p = c.getOwner();
                        int res1 = p.getResource1();
                        int res2 = p.getResource2();
                        p.setResource1(res1 + c.getResource1Val());
                        p.setResource2(res2 + c.getResource2Val());
                    }
                }
            }
        }
    }

}
