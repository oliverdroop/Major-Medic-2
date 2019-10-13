package client;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import java.io.File;

import java.util.Date;

import javax.imageio.ImageIO;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Frame1 extends JFrame {
    public Frame1() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private JButton ButtonStartGame = new JButton();
    private JButton ButtonQuit = new JButton();
    private JPanel Panel1 = new JPanel();
    private JPanel PanelUnitInfo = new JPanel();
    private JTextField TextFieldRouteDirectory = new JTextField();
    private transient BufferedImage terrain;
    private transient BufferedImage terrain2;
    private static Map MainMap;
    private static Game MainGame;
    private Date RecentMouseMove;
    //private transient Individual SelectedIndividual;
    private static Player PlayerServed;
    private String RouteDirectory = "D:\\Program Files\\Node Base 2\\";
    private static BufferedImage AntStanding;
    private static BufferedImage AntWalking_1;
    private static BufferedImage AntWalking_2;
    private static BufferedImage AntWalking_3;
    private static BufferedImage AntWalking_4;
    //private Timer timerMouse = new Timer(100, new ActionListener() {
    //    public void actionPerformed(ActionEvent e) {
    //        timerMouse_Tick();
    //    }
    //});

    private void jbInit() throws Exception {
        this.getContentPane().setLayout(null);
        //this.setSize(new Dimension(524, 368));
        //this.setLocation(499,399);
        this.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        this.setUndecorated(true);
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        TextFieldRouteDirectory.setBounds(new Rectangle(500, 500, 512, 30));
        TextFieldRouteDirectory.setText("D:\\Program Files\\Major Medic 2\\");
        TextFieldRouteDirectory.setVisible(true);
        TextFieldRouteDirectory.setLayout(null);
        ButtonStartGame.setText("Start Game");
        ButtonStartGame.setBounds(new Rectangle(screensize.width - 64, screensize.height - 78, 64, 26));
        ButtonStartGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ButtonStartGame_actionPerformed(e);
            }
        });
        ButtonQuit.setText("Quit");
        ButtonQuit.setBounds(screensize.width - 64, screensize.height - 26, 64, 26);
        ButtonQuit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ButonQuit_actionPerformed(e);
            }
        });
        Panel1.setBounds(new Rectangle(4, screensize.height - 78, 256, 78));
        Panel1.setLayout(null);
        Panel1.setVisible(false);
        PanelUnitInfo.setBounds(new Rectangle(260, screensize.height - 78, 294, 78));
        PanelUnitInfo.setLayout(null);
        PanelUnitInfo.setVisible(false);
        this.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {

            }

            public void mouseMoved(MouseEvent e) {
                RecentMouseMove = new Date();
                //TryScroll(e.getLocationOnScreen());
            }
        });
        this.getContentPane().add(ButtonStartGame, null);
        this.getContentPane().add(ButtonQuit, null);
        this.getContentPane().add(Panel1, null);
        this.getContentPane().add(PanelUnitInfo);
        this.getContentPane().add(TextFieldRouteDirectory);
        //this.ButtonStartGame_actionPerformed(null);
    }

    private void ButtonStartGame_actionPerformed(ActionEvent e) {
        ButtonStartGame.setVisible(false);
        System.out.println("New game");
        Game g = new Game();
        g.setRouteDirectory(this.TextFieldRouteDirectory.getText());
        this.TextFieldRouteDirectory.setVisible(false);
        Frame1.MainGame = g;
        //Player p = new Player(this, g);
        //p.setCurrentPlayer(p);
        //MainMap = g.getMap();
        //MainMap.Draw();
        //p.getTimerGraphics().start();
        try {
            Thread.sleep(25);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        //p.getTimerFOW().start();
        Panel1.setVisible(true);
        //timerMouse.start();
    }

    private void ButonQuit_actionPerformed(ActionEvent e) {
        if (MainGame != null) {
            for (Player p : MainGame.getPlayers()) {
                //p.getTimerGraphics().stop();
                //p.getTimerFOW().stop();
                p.getTimerGraphics2().stop();
            }
            MainGame.getMap().getTimerMovement().stop();
        }
        //timerMouse.stop();
        this.dispose();
    }

    /* private void ButtonNewUnit_actionPerformed(ActionEvent e) {
        //Player p = new Player(this);
        
        /* Player p = this.getPlayerServed();
        Unit u = new Unit(p);
        u.Select();
        Individual i1 = new Individual(MainMap, u);
        Individual i2 = new Individual(MainMap, u);
        Individual i3 = new Individual(MainMap, u);
        Individual i4 = new Individual(MainMap, u);
        //Individual i5 = new Individual(MainMap, u);
        //Individual i6 = new Individual(MainMap, u);
        //Individual i7 = new Individual(MainMap, u);
        //Individual i8 = new Individual(MainMap, u);
        //Individual i9 = new Individual(MainMap, u);
        //Individual i10 = new Individual(MainMap, u);
        //Individual i11 = new Individual(MainMap, u);
        //Individual i12 = new Individual(MainMap, u);
        u.setMaximumRadius(36);
        u.setCommander(i1);
        i1.setRank(1);
        i1.setRankInsignia();
        //SelectedIndividual = i1;
    } */

    /* private void timerGraphics_Tick(ActionEvent e) {

    } */

    /* private void timerMouse_Tick() {
        int scrollwait = 1000;
        Date d = new Date();
        Date r = RecentMouseMove;
        Date d2 =
            new Date(r.getYear(), r.getMonth(), r.getDate(), r.getHours(), r.getMinutes(),
                     r.getSeconds() + (scrollwait / 1000));
        //Date d2 = System.currentTimeMillis();
        //if ((RecentMouseMove.getSeconds() + 1).after)
        if (d.after(d2)) {
            Point p = MouseInfo.getPointerInfo().getLocation();
            //TryScroll(p);
        }
        //
        /* Map m = MainMap;
        Player p = this.getCurrentPlayer();
        JLabel jl = p.getPictureBoxLabel();
        Point mp1 = MouseInfo.getPointerInfo().getLocation();
        Point mp2 = new Point(mp1.x - jl.getHorizontalAlignment(), mp1.y - jl.getVerticalAlignment());
        Node n = m.getClosestNode(mp2);
        BufferedImage bi = new BufferedImage(18, 18, 1);
        /* for(int x = 0; x < 18; x++){
            for (int y = 0; y < 18; y++){
                bi.setRGB(x, y, 0x000000);
            }
        } */
        /* if (p.getSelectedUnit() != null) {
            Unit u = p.getSelectedUnit();
            Individual i = u.getCommander();
            if (i.CanMove(n)) {
                n.addImage(bi);
                n.Draw(p);
            }
        }
        //
    } */

    /* private void TryScroll(Point mouseLocation) {
        int scrollrate = 12;
        Player p = this.getPlayerServed();
        JLabel pblabel = p.getPictureBoxLabel();
        int x = pblabel.getLocation().x;
        int y = pblabel.getLocation().y;
        int maxscrollx = pblabel.getWidth() - p.getViewfinder().getWidth();
        int maxscrolly = pblabel.getHeight() - p.getViewfinder().getHeight();
        //System.out.println(e.getYOnScreen());
        //scroll picture right
        if (mouseLocation.x <= 0) {
            if (x + scrollrate <= 0) {
                pblabel.setLocation(x + scrollrate, y);
                x += scrollrate;
            } else {
                pblabel.setLocation(0, y);
                x = 0;
            }
        }
        //scroll picture left
        if (mouseLocation.x >= Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 1) {
            if (x - scrollrate > -maxscrollx) {
                pblabel.setLocation(x - scrollrate, y);
                x -= scrollrate;
            } else {
                pblabel.setLocation(-maxscrollx, y);
                x = -maxscrollx;
            }
        }
        //scroll picture down
        if (mouseLocation.y <= 0) {
            if (y + scrollrate <= 0) {
                pblabel.setLocation(x, y + scrollrate);
                y += scrollrate;
            } else {
                pblabel.setLocation(x, 0);
                y = 0;
            }
        }
        //scroll picture up
        if (mouseLocation.y >= Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 1) {
            if (y - scrollrate > -maxscrolly) {
                pblabel.setLocation(x, y - scrollrate);
                y -= scrollrate;
            } else {
                pblabel.setLocation(x, -maxscrolly);
                y = -maxscrolly;
            }
        }
    } */


    /* public Player getCurrentPlayer() {
        return this.CurrentPlayer;
    }

    public void setCurrentPlayer(Player p) {
        this.CurrentPlayer = p;
    } */

    public Map getMainMap() {
        return Frame1.MainMap;
    }

    private void UpdateUnitInfo(Unit u1) {
        final Unit u = u1;
        for (Component c : PanelUnitInfo.getComponents()) {
            PanelUnitInfo.remove(c);
        }
        PanelUnitInfo.setVisible(true);
        for (final Individual i : u.getMembers()) {
            int index = u.getMembers().indexOf(i);
            JLabel indiPBox1 = new JLabel() {
                public void paint(Graphics g) {
                    g.drawImage(i.getStationaryImages()[1], 0, 0, this);
                }
            };
            JLabel indiPBox2 = new JLabel() {
                public void paint(Graphics g) {
                    try {
                        String path2 = "";
                        if (i.getWeapons().get(0) != null) {
                            path2 =
                                MainMap.getGame().getRouteDirectory() + "\\Images\\Weapons\\Weapon_" +
                                i.getWeapons().get(0).getName() + ".bmp";
                        } else {
                            path2 = MainMap.getGame().getRouteDirectory() + "\\Images\\Weapons\\Weapon_0.bmp";
                        }
                        BufferedImage img2 = ImageIO.read(new File(path2));
                        g.drawImage(img2, 0, 0, this);
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            };
            //put picture boxes in correct place
            int level = index * i.getImage().getHeight();
            int column = 0;
            if (index > 7) {
                column = 196;
                level = (index - 8) * i.getImage().getHeight();
            }
            if (index > 3 && index < 8) {
                column = 98;
                level = (index - 4) * i.getImage().getHeight();
            }
            indiPBox1.setBounds(0 + column, level, i.getImage().getWidth(), i.getImage().getHeight());
            indiPBox2.setBounds(indiPBox1.getWidth() + 4 + column, level, 72, i.getImage().getHeight());
            //indiPBox1.setText("Indi");
            indiPBox1.setVisible(true);
            indiPBox2.setVisible(true);
            PanelUnitInfo.add(indiPBox1);
            PanelUnitInfo.add(indiPBox2);
            indiPBox1.repaint();
            indiPBox2.repaint();
        }
    }
    public Player getPlayerServed(){
        return Frame1.PlayerServed;
    }
    public void setPlayerServed(Player p){
        Frame1.PlayerServed = p;
    }
}
