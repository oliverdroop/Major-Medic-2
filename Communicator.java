package client;


import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.Timer;

public class Communicator {
    private Game Game = null;
    private byte DepartureTimeCode = -128;
    //private ServerSocket skt1 = null;
    //private Socket skt3 = null;
    //private String output;
    //private Timer TimerSend = new Timer(100, new ActionListener() {
    //    public void actionPerformed(ActionEvent e) {
    //        Send();
    //        //Communicate1();
    //        //Communicate2();
    //    }
    //});
    private Timer TimerReceive = new Timer(10, new ActionListener() {
        //private byte count = 0;
        public synchronized void actionPerformed(ActionEvent e) {
            List<Player> players = Game.getPlayers();
            if (players.size() > 0) {
                try {
                    for (Player p : players) {
                        Receive(p);
                    }
                    AdvanceTimeCode();
                } catch (ConcurrentModificationException cme) {
                    cme.getMessage();
                }
            }
            //count += 1;
            //if (count > 9){
            //    count = 0;
            //    for (Player p : Game.getPlayers()){
            //        SendMyNodes(p);
            //        SendMyDrawables(p);
            //    }
            //}
        }
    });

    public Communicator(Game g) {
        super();
        this.Game = g;
        try {
            //this.skt1 = new ServerSocket(3742);
            //System.out.println("Created server socket");
            this.ListenForNewSocket();
            //InetAddress adrs = InetAddress.getLocalHost();

            //this.skt3 = new Socket(adrs, 3700);
            //this.skt2 = new Socket(adrs, 3700);
            //Socket bzb = skt1.accept();
            //this.skt3 = bzb;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //this.TimerSend.start();
        this.TimerReceive.start();
    }

    public void ListenForNewSocket() {
        ConnectionListener cl = new ConnectionListener(this);
        this.getGame().getExecutorService().submit(cl);
    }

    public void RequestPortMigration(Player p, int port) {
        this.SendPacket(p, (byte) 126, port, port, port);
    }

    public void Receive(Player p) {
        if (p != null && p.getSocket() != null) {
            if (p.getIsAIPlayer() == false) {
                try {
                    p.getSocket().setReceiveBufferSize(1024);
                    InputStream is = p.getSocket().getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader in = new BufferedReader(isr);
                    while (in.ready()) {
                        String s = in.readLine();
                        if (s.contains(",")) {
                            //Location data
                            if (s.startsWith("cl")) {
                                //click event
                                System.out.println("Received " + s);
                                //String[] s2 = s.split(",");
                                String[] s2 = s.substring(2).split(",");
                                JLabel pbl = p.getPictureBoxLabel();
                                int b = Integer.parseInt(s2[0]);
                                int x = Integer.parseInt(s2[1]) - pbl.getX();
                                int y = Integer.parseInt(s2[2]) - pbl.getY();
                                MouseEvent e = new MouseEvent(pbl, 0, System.currentTimeMillis(), 0, x, y, 1, false, b);
                                //System.out.println("Mouse click at " + x + "," + y + " received");
                                p.PBLMouseClicked(e);
                            }
                            if (s.startsWith("db")){
                                //drag box event
                                System.out.println("Received " + s);
                                String[] s2 = s.substring(2).split(",");
                                JLabel pbl = p.getPictureBoxLabel();
                                int b = Integer.parseInt(s2[0]);
                                int x1 = Integer.parseInt(s2[1]) - pbl.getX();
                                int y1 = Integer.parseInt(s2[2]) - pbl.getY();
                                int x2 = Integer.parseInt(s2[3]) - pbl.getX();
                                int y2 = Integer.parseInt(s2[4]) - pbl.getY();
                                this.getGame().getMap().SelectInBox(p, x1, y1, x2, y2);
                            }
                            if (s.startsWith("vf")) {
                                //viewfinder coordinates
                                String[] s2 = s.substring(2).split(",");
                                JLabel pbl = p.getPictureBoxLabel();
                                int x1 = Integer.parseInt(s2[0]);
                                int y1 = Integer.parseInt(s2[1]);
                                int x2 = Integer.parseInt(s2[2]);
                                int y2 = Integer.parseInt(s2[3]);
                                Point p1 = new Point(x1, y1);
                                Point p2 = new Point(x2, y2);
                                p.setViewfinderCorners(p1, p2);
                            }
                        } else {
                            //not a location event
                            //incoming new level request
                            if (s.startsWith("lv")) {
                                System.out.println("received level request " + s);
                                if (this.getGame().getLevel() == null) {
                                    try {
                                        String[] strs = s.split("%");
                                        strs[0] = strs[0].substring(2);
                                        Level lvl = new Level(this.getGame(), Integer.parseInt(strs[0]));
                                        this.getGame().setLevel(lvl);
                                        if (strs[1].contains("true")){
                                            lvl.GenerateAIPlayer();
                                        }
                                        for (Player p2 : this.getGame().getPlayers()) {
                                            p2.StartLevel();

                                            this.SendLevelReady(p2);
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Couldn't create new level: " + e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                                else{
                                    p.StartLevel();
                                    this.SendLevelReady(p);
                                }
                                
                            }
                            //incoming all nodes request
                            if (s.startsWith("an")) {
                                //System.out.println("Communicator received an");
                                this.SendAllNodes(p);
                            }
                            //incoming all drawables request
                            if (s.startsWith("ad")) {
                                this.SendAllDrawables(p);
                            }
                            //incoming all cover request
                            if (s.startsWith("ac")) {
                                this.SendAllCover(p);
                                System.out.println("Sent all cover");
                            }
                            //incoming my individuals request
                            if (s.startsWith("mi")) {
                                this.SendMyIndividuals(p);
                                //System.out.println("Sent my individuals");
                            }
                            //incoming my nodes request
                            if (s.startsWith("mn")) {
                                //System.out.println("Communicator received mn");
                                //System.out.println("Communicator.Receive(...) doesn't SendMyNodes() - may be ok");
                                this.SendMyNodes(p);
                                //this.AdvanceTimeCode();
                            }
                            //incoming my drawables request
                            if (s.startsWith("md")) {
                                //System.out.println("Communicator received md");
                                //System.out.println("Communicator.Receive(...) doesn't SendMyDrawables() - may be ok");
                                this.SendMyDrawables(p);
                            }
                            //incoming new unit request
                            if (s.startsWith("ml")) {
                                p.NewUnit("Militia");
                                System.out.println("New militia created");
                            }
                            //incoming new scout request
                            if (s.startsWith("sc")) {
                                p.NewUnit("Scout");
                                System.out.println("New scout unit created");
                            }
                            //incoming new sniper request
                            if (s.startsWith("sn")) {
                                p.NewUnit("Sniper");
                                System.out.println("New sniper unit created");
                            }
                            //incoming new machine gun request
                            if (s.startsWith("mg")) {
                                p.NewUnit("Machine Gun");
                                System.out.println("New machine gun unit created");
                            }
                            //incoming new riflemen request
                            if (s.startsWith("rf")) {
                                p.NewUnit("Riflemen");
                                System.out.println("New riflemen unit created");
                            }
                            //incoming new grenadiers request
                            if (s.startsWith("gr")) {
                                p.NewUnit("Grenadiers");
                                System.out.println("New grenadiers unit created");
                            }
                            //incoming new warrior tank request
                            if (s.startsWith("wa")) {
                                p.NewUnit("Warrior");
                                System.out.println("New Warrior tank created");
                            }
                            //incoming quit request
                            if (s.startsWith("qt")) {
                                int index = this.getGame().getPlayers().indexOf(p);
                                for (Individual i : p.getIndividuals()) {
                                    i.getTimerAI().stop();
                                    i.getTimerRoute().stop();
                                    this.getGame().getMap().removeDrawableObject(i);
                                    this.getGame().getMap().removeMoveableObject(i);
                                }
                                this.getGame().removePlayer(p);
                                p.getTimerGraphics2().stop();
                                p.getSocket().close();
                                p.setSocket(null);
                                p = null;
                                //p.getTimerFOW().stop();
                                //this.skt3 = null;
                                System.out.println("Player " + index + " left the game.");
                                this.ListenForNewSocket();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Game getGame() {
        return this.Game;
    }

    public void setGame(Game g) {
        this.Game = g;
    }

    public void SendPacket(Player p, byte prfx, int int1, int int2, int int3) {
        try {
            if (p.getSocket() != null) {
                OutputStream os = p.getSocket().getOutputStream();
                //2147483647
                p.getSocket().setSendBufferSize(2147483647);
                byte[] bs = new byte[14];
                byte[] pr = new byte[1];
                pr[0] = prfx;
                byte[] x = ByteBuffer.allocate(4).putInt(int1).array();
                byte[] y = ByteBuffer.allocate(4).putInt(int2).array();
                byte[] r = ByteBuffer.allocate(4).putInt(int3).array();
                byte[] tc = new byte[1];
                tc[0] = this.DepartureTimeCode;
                System.arraycopy(pr, 0, bs, 0, 1);
                System.arraycopy(x, 0, bs, 1, 4);
                System.arraycopy(y, 0, bs, 5, 4);
                System.arraycopy(r, 0, bs, 9, 4);
                System.arraycopy(tc, 0, bs, 13, 1);
                os.write(bs);
                os.flush();
            }
        } catch (IOException | ConcurrentModificationException e) {
            System.out.println(e.getMessage());
        }
    }

    public void SendLevelReady(Player p) {
        SendPacket(p, (byte) 125, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF);
        int ref = this.getGame().getLevel().getLevelNumber() * 1000 + 0001;
        this.SendPacket(p, (byte) -3, 0, 0, ref);
    }

    public void SendAllNodes(Player p) {
        //send all nodes
        try {
            List<Node> ands = new ArrayList<Node>();
            for (Node[] row : this.getGame().getMap().getNodeGrid()) {
                for (Node n : row) {
                    ands.add(n);
                }
            }
            for (Node n : ands) {
                this.SendPacket(p, (byte) 0, n.getScreenpoint().x, n.getScreenpoint().y,
                                Integer.parseInt(n.getImgRef()));
            }
            System.out.println("Sent all nodes " + ands.size());
        } catch (ConcurrentModificationException e) {
            System.out.println(e.getMessage());
        }
    }

    public void SendAllDrawables(Player p) {
        try {
            List<DrawableObject> ados = new ArrayList<DrawableObject>();
            for (List<DrawableObject> row : this.getGame().getMap().getDrawableObjects()) {
                for (DrawableObject dob : row) {
                    //Don't send enemies
                    if (dob.getClass() != Individual.class) {
                        ados.add(dob);
                    }
                }
            }
            for (DrawableObject dob : ados) {
                this.SendPacket(p, (byte) 1, dob.getScreenpoint().x, dob.getScreenpoint().y,
                                Integer.parseInt(dob.getImgRef()));
            }
            System.out.println("Sent all drawables " + ados.size());
        } catch (ConcurrentModificationException e) {
            System.out.println(e.getMessage());
        }
    }

    public synchronized void SendMyNodes(Player p) {
        try {
            List<Node> mnds = new ArrayList<Node>();
            for (Node n : p.getNodes()) {
                mnds.add(n);

            }
            for (Node n : mnds) {
                this.SendPacket(p, (byte) 2, n.getScreenpoint().x, n.getScreenpoint().y,
                                Integer.parseInt(n.getImgRef()));
            }
            //System.out.println("Sent my nodes " + mnds.size());
        } catch (ConcurrentModificationException e) {
            System.out.println(e.getMessage());
        }
    }

    public synchronized void SendMyIndividuals(Player p) {
        for (Individual i : p.getIndividuals()) {
            if (i.getScreenpoint() != null) {
                if (i.getVisualRange() != 0) {
                    int int1 = i.getScreenpoint().x;
                    int int2 = i.getScreenpoint().y;
                    int int3 = (int) Math.round(i.getVisualRange());
                    this.SendPacket(p, (byte) 3, int1, int2, int3);
                } else {
                    System.out.println("didn't send individual because visual range is 0");
                }
            } else {
                System.out.println("didn't send individual because screenpoint is null");
            }
        }
    }

    public synchronized void SendMyDrawables(Player p) {
        this.SendMyCompounds(p);
        try {
            List<DrawableObject> mdos = new ArrayList<DrawableObject>();
            for (List<DrawableObject> row : p.getDrawableObjects()) {
                for (DrawableObject dob : row) {
                    //the line below should never be reinstated: it lost me nearly two weeks
                    //if (mdos.contains(dob) == false) {
                    //Don't send invisible enemies
                    if (p.getAllEnemies().contains(dob) == false || p.getVisibleEnemies().contains(dob)) {
                        mdos.add(dob);
                    }
                    //}
                }
            }
            for (DrawableObject dob : mdos) {
                this.SendPacket(p, (byte) 4, dob.getScreenpoint().x, dob.getScreenpoint().y,
                                Integer.parseInt(dob.getImgRef()));
            }
            //System.out.println("Sent my drawables " + mdos.size());
        } catch (ConcurrentModificationException e) {
            System.out.println(e.getMessage());
        }
        //Send rank insignia after my drawables for simplicity's sake
        this.SendRankInsignia(p);
    }

    public synchronized void SendAllCover(Player p) {
        try {
            Map m = this.getGame().getMap();
            List<CoverSection> cvrs = m.getCover();
            for (CoverSection c : cvrs) {
                this.SendPacket(p, (byte) 5, c.getScreenpoint().x, c.getScreenpoint().y,
                                Integer.parseInt(c.getImgRef()));
            }
        } catch (ConcurrentModificationException cme) {
            System.out.println(cme.getMessage());
        }
    }

    public synchronized void SendMyCompounds(Player p) {
        try {
            Map m = this.getGame().getMap();
            List<Compound> cpds = m.getCompounds();
            for (Compound c : cpds) {
                if (c.getCentralNode() != null) {
                    int ri = 0;
                    if (c.getOwner() != null) {
                        if (c.getOwner() == p) {
                            //player owns compound
                            if (m.getObjectives().contains(c)) {
                                ri = (Integer.parseInt("1801"));
                            } else {
                                ri = (Integer.parseInt("1800"));
                            }
                        } else {
                            //find if compound's centre is visible to player
                            if (p.CanSee(c.getCentralNode().getScreenpoint())) {
                                //enemy owns compound
                                if (m.getObjectives().contains(c)) {
                                    ri = (Integer.parseInt("1901"));
                                } else {
                                    ri = (Integer.parseInt("1900"));
                                }
                            } else {
                                //compound is not owned by player
                                if (m.getObjectives().contains(c)) {
                                    ri = (Integer.parseInt("1501"));
                                } else {
                                    ri = (Integer.parseInt("1500"));
                                }
                            }
                        }
                    } else {
                        //compound is owned by no player
                        if (m.getObjectives().contains(c)) {
                            ri = (Integer.parseInt("1501"));
                        } else {
                            ri = (Integer.parseInt("1500"));
                        }
                    }
                    this.SendPacket(p, (byte) 7, c.getCentralNode().getScreenpoint().x,
                                    c.getCentralNode().getScreenpoint().y, ri);
                } else {
                    System.out.println("central node is null");
                }
                //}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void SendShotAnimation(Player p, Point startpoint, double angle) {
        try {
            this.SendPacket(p, (byte) 64, startpoint.x, startpoint.y, (int) Math.round(angle));
            //System.out.println("Sent shot animation");
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public synchronized void SendBloodSplatter(Player p, Point p1, String angle) {
        try {
            int ref = Integer.parseInt(angle);
            this.SendPacket(p, (byte) 65, p1.x, p1.y, ref);
            OutputStream os = p.getSocket().getOutputStream();
            //System.out.println("Sent blood splatter animation");
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public synchronized void SendExplosionAnimation(Player p, Point p1, int rad) {
        try {
            this.SendPacket(p, (byte) 66, p1.x, p1.y, rad);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public synchronized void SendSound(Player p, Point p1, String soundRef) {
        try {
            int ref = Integer.parseInt(soundRef);
            this.SendPacket(p, (byte) 127, p1.x, p1.y, ref);
            //System.out.println("Sent sound " + soundRef);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void SendRankInsignia(Player p) {
        //Send rank insignia
        try {
            for (Individual i : p.getIndividuals()) {
                if (p.getSelectedUnit().getMembers().contains(i) == false) {
                    //int xi = i.getScreenpoint().x - (i.getImage().getWidth() / 2);
                    //int yi = i.getScreenpoint().y - (i.getImage().getHeight() / 2);
                    int xi = i.getScreenpoint().x - 5;
                    int yi = i.getScreenpoint().y - ((i.getImage().getHeight() / 2) + 6);
                    String ref = "3" + i.getRank() + "01";
                    int ri = Integer.parseInt(ref);
                    this.SendPacket(p, (byte) 6, xi, yi, ri);
                }
            }
            for (Individual i : p.getSelectedUnit().getMembers()) {
                //int xi = i.getScreenpoint().x - (i.getImage().getWidth() / 2);
                //int yi = i.getScreenpoint().y - (i.getImage().getHeight() / 2);
                int xi = i.getScreenpoint().x - 5;
                int yi = i.getScreenpoint().y - ((i.getImage().getHeight() / 2) + 6);
                String ref = "3" + i.getRank() + "00";
                int ri = Integer.parseInt(ref);
                this.SendPacket(p, (byte) 6, xi, yi, ri);
            }
            for (Individual i : p.getVisibleEnemies()) {
                int xi = i.getScreenpoint().x - 5;
                int yi = i.getScreenpoint().y - ((i.getImage().getHeight() / 2) + 6);
                String ref = "3" + i.getRank() + "02";
                int ri = Integer.parseInt(ref);
                this.SendPacket(p, (byte) 6, xi, yi, ri);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        this.SendHUD(p);
    }

    public void SendHUD(Player p) {
        //Send HUD
        try {
            List<Individual> mbrs = p.getSelectedUnit().getMembers();
            for (Individual i : mbrs) {
                byte[] bs1 = new byte[4];
                bs1[0] = (byte) mbrs.indexOf(i);
                bs1[1] = (byte) i.getRank();
                bs1[2] = (byte) (i.getHealth() * 1.27);
                bs1[3] = 0x00;
                int r1 = Integer.parseInt(i.getWeapons().get(0).getImageRef());
                int r2 = Integer.parseInt(i.getImgRef());
                this.SendPacket(p, (byte) -1, ByteBuffer.wrap(bs1).getInt(), r1, r2);
            }
        } catch (Exception e) {
            System.out.println("Problem sending HUD for player");
        }
        //this.SendResources(p);
    }

    public void SendResources(Player p) {
        try {
            int r1 = p.getResource1();
            int r2 = p.getResource2();
            int r3 = p.getWinCondition();
            this.SendPacket(p, (byte) -2, r1, r2, r3);
        } catch (Exception e) {
            System.out.println("Problem sending resources for player");
        }
    }

    public void AdvanceTimeCode() {
        this.DepartureTimeCode += 1;
        if (this.DepartureTimeCode > 127) {
            this.DepartureTimeCode = -128;
        }
    }
    /* public void SendImageArray(Player p) {
        try {

            OutputStream out1 = p.getSocket().getOutputStream();
            ImageOutputStream ios = ImageIO.createImageOutputStream(out1);
            skt3.setSendBufferSize(1048576);
            BufferedImage[][][][] images = this.getGame().getImageHolder().getImages();
            BufferedImage img;
            for (int idx1 = 0; idx1 < 3; idx1++) {
                for (int idx2 = 0; idx2 < 8; idx2++) {
                    for (int idx3 = 0; idx3 < 7; idx3++) {
                        for (int idx4 = 0; idx4 < 6; idx4++) {
                            img = images[idx1][idx2][idx3][idx4];
                            if (img != null){
                                ImageIO.write(img, "png", ios);
                                ios.flush();
                            }
                            else{
                                ImageIO.write(new BufferedImage(1,1,2), "png", ios);
                                ios.flush();
                            }
                        }
                    }
                }
            }

        } catch (Exception e){

        }
    } */

    /* public void Communicate1() {
        if (this.skt3 != null & this.skt2 != null) {
            try {
                InputStream is = this.skt3.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader in1 = new BufferedReader(isr);
                //PrintWriter out1 = new PrintWriter(skt3.getOutputStream());
                OutputStream out1 = skt3.getOutputStream();
                //out1.println("Hello");
                //for(Player p : this.getGame().getPlayers()){
                //    for(Individual i: p.getIndividuals()){
                //        out1.println(p.getIndividuals().indexOf(i) + "(" + i.getScreenpoint().x + "," + i.getScreenpoint().y + ") ");
                //    }
                //}
                //For each player
                //Get area of viewfinder that has changed
                //Obtain image
                if (this.getGame().getPlayers().size() > 1) {
                    Player p = this.getGame().getPlayers().get(1);
                    if (p != null) {
                        BufferedImage trrn = p.getTerrain();
                        //if (trrn != null) System.out.println("trrn is not null");
                        JLabel pbl = p.getPictureBoxLabel();
                        //if (pbl != null) System.out.println("pbl is not null");
                        int w = p.getViewfinder().getWidth();
                        int h = p.getViewfinder().getHeight();
                        //if (p.getViewfinder() != null) System.out.println("viewfinder is not null");
                        trrn = trrn.getSubimage(-pbl.getX(), -pbl.getY(), w, h);
                        //Buffer into stream
                        ImageOutputStream ios = ImageIO.createImageOutputStream(out1);
                        skt3.setSendBufferSize(1048576);
                        System.out.println("writing" + System.currentTimeMillis());
                        ImageIO.write(trrn, "png", ios);
                        System.out.println("written" + System.currentTimeMillis());
                        ios.flush();
                        //skt3.close();
                        //ios.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void Communicate2() {
        try {
            if (this.getGame().getPlayers().size() > 1) {
                Player p = this.getGame().getPlayers().get(1);
                if (p != null) {
                    InputStream is = skt2.getInputStream();
                    BufferedImage bimg = null;
                    //while (is.available() > 0) {
                    //    System.out.println(is.available());
                    //
                    //}
                    System.out.println("reading" + System.currentTimeMillis());
                    bimg = ImageIO.read(ImageIO.createImageInputStream(is));
                    System.out.println("read" + System.currentTimeMillis());
                    Graphics g = p.getViewfinder().getGraphics();
                    g.drawImage(bimg, 0, 0, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Communicate3() {
        try {
            BufferedReader in2 = new BufferedReader(new InputStreamReader(skt2.getInputStream()));
            PrintWriter out2 = new PrintWriter(skt2.getOutputStream());
            String s = "";
            if (in2.ready()) {
                while (in2.ready()) {
                    s += in2.readLine();
                }
                output = s;
                System.out.println(output);
            } else {
                System.out.println("in2 is not ready");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } */

}
