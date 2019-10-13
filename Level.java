package client;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Random;

import javax.swing.Timer;


public class Level {
    private int LevelNumber = 0;
    private Game Game;
    private Map Map;
    //private Player AIPlayer;
    private Timer TimerAI = new Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            UpdateAI();
        }
    });

    public Level(Game g, int lnum) {
        super();
        this.setGame(g);
        this.getGame().setLevel(this);
        this.setLevelNumber(lnum);
        //
        Map m = new Map(new Rectangle(2048, 1024), this.getGame());
        this.setMap(m);
        //this.getGame().setMap(m);
        //
        if (this.getLevelNumber() > 0) {
            //this.GenerateAIPlayer();
            this.GenerateStartingEnemies();
            this.TimerAI.start();
        }
    }

    public int getLevelNumber() {
        return this.LevelNumber;
    }

    public void setLevelNumber(int lvl) {
        this.LevelNumber = lvl;
    }

    public Game getGame() {
        return this.Game;
    }

    public void setGame(Game g) {
        this.Game = g;
    }

    public Map getMap() {
        return this.Map;
    }

    public void setMap(Map m) {
        this.Map = m;
    }

    public Player getAIPlayer() {
        Player p1 = null;
        for (Player p : this.getGame().getPlayers()) {
            if (p.getIsAIPlayer() == true) {
                p1 = p;
            }
        }
        return p1;
    }
    //public void setAIPlayer(Player p){
    //    this.AIPlayer = p;
    //}

    public void GenerateAIPlayer() {
        Player p = new Player(this.getGame());
        p.setIsAIPlayer(true);
        p.setSocket(null);
    }

    public void GenerateStartingEnemies() {

        for (Player p : this.getGame().getPlayers()) {
            if (p.getIsAIPlayer() == true) {
                if (this.getLevelNumber() == 1) {
                    //Enemies start in centre of map
                    int hlfx = this.getMap().getNodeGrid().length / 2;
                    int hlfy = this.getMap().getNodeGrid()[hlfx].length / 2;
                    p.setStartNode(this.getMap().getNodeGrid()[hlfx][hlfy]);
                    //
                    for (Compound c : this.getMap().getObjectives()) {
                        //if (c.getResource2Val() > 3) {
                        Unit u = new Unit(p);
                        Individual i0 = new Individual(this.getMap(), u, "AK47");
                        i0.setTraining(0.9);
                        u.setCommander(i0);
                        i0.setScreenpoint(c.getCentralNode().getScreenpoint());
                        //}
                    }
                    //Unit u = new Unit(p);
                    //Individual i0 = new Individual(this.getMap(), u, "MP5A3");
                    //Individual i1 = new Individual(this.getMap(), u, "MP5A3");
                    //u.setCommander(i0);
                    //i0.setPosition(this.getMap().getNodeGrid()[20][20]);
                    //i1.setPosition(this.getMap().getNodeGrid()[21][20]);
                    //i0.setScreenpoint(this.getMap().getNodeGrid()[20][20].getScreenpoint());
                    //i1.setScreenpoint(this.getMap().getNodeGrid()[21][20].getScreenpoint());
                    //p.getTimerGraphics().start();
                }
                if (this.getLevelNumber() == 2) {
                    int hlfx = this.getMap().getNodeGrid().length / 2;
                    int hlfy = this.getMap().getNodeGrid()[hlfx].length / 2;
                    p.setStartNode(this.getMap().getNodeGrid()[hlfx][hlfy]);
                    //
                    for (Compound c : this.getMap().getObjectives()) {
                        //if (c.getResource2Val() > 3) {
                        Unit u = new Unit(p);
                        Individual i0 = new Individual(this.getMap(), u, "AK47");
                        Individual i1 = new Individual(this.getMap(), u, "AK47");
                        u.setCommander(i0);
                        i0.setScreenpoint(c.getCentralNode().getScreenpoint());
                        i1.setScreenpoint(c.getCentralNode().getNeighbours()[0].getScreenpoint());
                        //}
                    }
                }
                if (this.getLevelNumber() == 3) {
                    p.setStartNode(this.getMap().getNodeGrid()[102][50]);
                    Random rnd = new Random();
                    int count = 3;
                    while (count > 0) {
                        int indx = rnd.nextInt(this.getMap().getCompounds().size());
                        Compound c = this.getMap().getCompounds().get(indx);
                        
                        Unit u = new Unit(p);
                        Individual i0 = new Individual(this.getMap(), u, "AK47");
                        Individual i1 = new Individual(this.getMap(), u, "AK47");
                        Individual i2 = new Individual(this.getMap(), u, "AK47");
                        Individual i3 = new Individual(this.getMap(), u, "AK47");
                        u.setCommander(i0);
                        i0.setScreenpoint(c.getCentralNode().getScreenpoint());
                        i1.setScreenpoint(c.getCentralNode().getNeighbours()[0].getScreenpoint());
                        i2.setScreenpoint(c.getCentralNode().getNeighbours()[5].getScreenpoint());
                        i3.setScreenpoint(c.getCentralNode().getNeighbours()[4].getScreenpoint());
                        count -= 1;
                    }
                }
            }
        }

    }

    public void GenerateEnemies() {
        if (this.getLevelNumber() > 1) {
            int minu = 2;
            if (this.getLevelNumber() == 2) {
                if (this.getAIPlayer().getUnits().size() < minu) {
                    this.getAIPlayer().NewUnit("Mujahideen");
                    System.out.println("AI: Created new Mujahideen unit");
                }
            }
            if (this.getLevelNumber() == 3) {
                minu = 3;
                if (this.getAIPlayer().getUnits().size() < minu) {
                    this.getAIPlayer().NewUnit("Mujahideen");
                    System.out.println("AI: Created new Mujahideen unit");
                }
            }
        }
    }

    public void UpdateAI() {
        Player p = this.getAIPlayer();
        if (this.LevelNumber > 0) {
            //Pick one unheld objective
            Compound c = this.getNextObjective();
            if (c != null) {
                //Move towards compound with some units
                {
                    //find a unit to move
                    Unit u = this.getClosestUnit(c);
                    if (u != null) {
                        if (u.getIsPlanningMove() == false) {
                            //System.out.println("AI: Unit to move chosen");
                            //test if all of unit's individuals are free
                            //if all are free, move
                            if (u.getIsMoving() == false) {
                                //System.out.println("AI: Moving");
                                //move towards compound
                                u.PlanMove(c.getCentralNode());
                            }
                        }
                    }
                }
            }
            //Create new unit if necessary
            this.GenerateEnemies();
            this.AIAttack();
        }
    }

    public void AIAttack() {
        if (this.getLevelNumber() > 0) {
            for (Unit u : this.getAIPlayer().getUnits()) {
                if (this.getLevelNumber() > 1) {
                    u.TryAttack();
                }
            }
        }
    }

    public Compound getNextObjective() {
        Player p = this.getAIPlayer();
        //Look for unheld objectives
        Compound c1 = null;
        for (Compound c : this.getMap().getObjectives()) {
            if (p.CanSee(c.getCentralNode().getScreenpoint()) == false) {
                //System.out.println("AI: Objective not visible");
                if (p.getCompounds().contains(c) == false) {
                    //System.out.println("AI: Objective not held");
                    c1 = c;
                }
            }
        }
        //Pick one unheld objective
        Compound c = c1;
        return c;
    }

    public Unit getClosestUnit(Compound c) {
        Player p = this.getAIPlayer();
        //find a unit to move
        Unit u1 = null;
        double dist = 999999999;
        Find f = new Find();
        //Find closest unit
        for (Unit u : p.getUnits()) {
            double d2 = f.Distance(u.getCommander().getScreenpoint(), c.getCentralNode().getScreenpoint());
            if (d2 < dist) {
                //if (u.getIsPlanningMove() == false) {
                dist = d2;
                u1 = u;
                //}

            }
        }
        Unit u = u1;
        return u;
    }
}
