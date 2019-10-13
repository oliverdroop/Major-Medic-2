package client;

import java.awt.Point;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.List;

public class FOWDrawer implements Runnable {
    private final Player Player;

    public FOWDrawer(Player p) {
        super();
        this.Player = p;
    }

    @Override
    public void run() {

        //long t1 = System.currentTimeMillis();
        Player p = this.Player;
        if (p.getHasFogOfWar() == true) {
            //BufferedImage terrain = p.getTerrain();
            //Graphics g1 = terrain.createGraphics();
            Game game = p.getGame();
            Map m = game.getMap();
            BufferedImage FOWB = game.getFOWBlack();
            BufferedImage FOWG = game.getFOWGrey();
            //int left = 999999999;
            //int right = 0;
            //int top = 999999999;
            //int bottom = 0;
            List<Node> nodesToBrighten = new ArrayList<Node>();
            //List<DrawableObject> DOsToBrighten = new ArrayList<DrawableObject>();
            //Also needs drawable objects
            for (Individual i : p.getIndividuals()) {
                Point centre = i.getScreenpoint();
                double range = i.getVisualRange();
                for (Node n1 : m.getNodesInRange(centre, range)) {
                    if (nodesToBrighten.contains(n1) == false) {
                        Point p1 = n1.getScreenpoint();
                        if (i.CanSee(p1)) {
                            nodesToBrighten.add(n1);
                        }
                    }
                }
                //for (DrawableObject DO : m.getDrawableObjectsInRange(centre, range)){
                //    if (DOsToBrighten.contains(DO) == false){
                //        Point p1 = DO.getScreenpoint();
                //        if (i.CanSee(p1)){
                //            DOsToBrighten.add(DO);
                //        }
                //    }
                //}
            }
            //System.out.println("nodesToBrighten is " + nodesToBrighten.size() + " long");
            Node[][] grid = m.getNodeGrid();
            int offset = grid[0][0].getSize() / 2;
            for (Node[] na : grid) {
                for (Node n : na) {
                    //p.addNode(n);
                    if (nodesToBrighten.contains(n) == false) {
                        p.removeNode(n);
                        int x = n.getScreenpoint().x - offset;
                        int y = n.getScreenpoint().y - offset;
                        //g1.drawImage(FOWG, x, y, null);
                        for (CoverSection cvs : n.getCoverSections()) {
                            boolean visible = false;
                            for (Node n2 : cvs.getNodes()) {
                                if (nodesToBrighten.contains(n2)) {
                                    visible = true;
                                }
                            }
                            if (visible == false) {
                                p.removeDrawableObject(cvs);
                            }
                        }
                    } else {
                        //n.Draw(p);
                        if (this.Player.getViewfinderCorners() != null) {
                            Point p1 = this.Player.getViewfinderCorners()[0];
                            Point p2 = this.Player.getViewfinderCorners()[1];
                            if (n.getScreenpoint().x >= p1.x && n.getScreenpoint().y >= p1.y) {
                                if (n.getScreenpoint().x < p2.x && n.getScreenpoint().y < p2.y) {
                                    //
                                    //
                                    //p.addNode(n);
                                    //p.addDrawableObject(n);
                                    for (CoverSection cvs : n.getCoverSections()) {
                                        //cvs.Draw(this.Player);
                                        //if (p.getDrawableObjects().contains(cvs) == false) {
                                        //p.addDrawableObject(cvs);
                                        //}




                                    }
                                    //
                                    //
                                }
                            }
                        }
                    }
                }
            }
            //for (DrawableObject DO : DOsToBrighten){
            //    DO.Draw(this);
            //}
        }
        /* long t2 = System.currentTimeMillis();
            long t3 = t2 - t1;
            if (t3 > 500){
                System.out.println("FOWDrawer took " + t3);
            }
            else{
                System.out.println("FOWDrawer took " + t3);
            } */
    }

    public void run2() {
        //long t1 = System.currentTimeMillis();
        Player p = this.Player;
        if (p.getHasFogOfWar() == true) {
            //BufferedImage terrain = p.getTerrain();
            //Graphics g1 = terrain.createGraphics();
            Game game = p.getGame();
            Map m = game.getMap();
            BufferedImage FOWB = game.getFOWBlack();
            BufferedImage FOWG = game.getFOWGrey();
            //int left = 999999999;
            //int right = 0;
            //int top = 999999999;
            //int bottom = 0;
            List<Node> nodesToBrighten = new ArrayList<Node>();
            //List<DrawableObject> DOsToBrighten = new ArrayList<DrawableObject>();
            //Also needs drawable objects
            for (Individual i : p.getIndividuals()) {
                Point centre = i.getScreenpoint();
                double range = i.getVisualRange();
                for (Node n1 : m.getNodesInRange(centre, range)) {
                    if (nodesToBrighten.contains(n1) == false) {
                        Point p1 = n1.getScreenpoint();
                        if (i.CanSee(p1)) {
                            nodesToBrighten.add(n1);
                        }
                    }
                }
                //for (DrawableObject DO : m.getDrawableObjectsInRange(centre, range)){
                //    if (DOsToBrighten.contains(DO) == false){
                //        Point p1 = DO.getScreenpoint();
                //        if (i.CanSee(p1)){
                //            DOsToBrighten.add(DO);
                //        }
                //    }
                //}
            }
            //System.out.println("nodesToBrighten is " + nodesToBrighten.size() + " long");
            Node[][] grid = m.getNodeGrid();
            int offset = grid[0][0].getSize() / 2;
            for (Node[] na : grid) {
                for (Node n : na) {
                    //p.addNode(n);
                    if (nodesToBrighten.contains(n) == false) {
                        p.removeNode(n);
                        int x = n.getScreenpoint().x - offset;
                        int y = n.getScreenpoint().y - offset;
                        //g1.drawImage(FOWG, x, y, null);
                        for (CoverSection cvs : n.getCoverSections()) {
                            boolean visible = false;
                            for (Node n2 : cvs.getNodes()) {
                                if (nodesToBrighten.contains(n2)) {
                                    visible = true;
                                }
                            }
                            if (visible == false) {
                                p.removeDrawableObject(cvs);
                            }
                        }
                    } else {
                        //n.Draw(p);
                        if (this.Player.getViewfinderCorners() != null) {
                            Point p1 = this.Player.getViewfinderCorners()[0];
                            Point p2 = this.Player.getViewfinderCorners()[1];
                            if (n.getScreenpoint().x >= p1.x && n.getScreenpoint().y >= p1.y) {
                                if (n.getScreenpoint().x < p2.x && n.getScreenpoint().y < p2.y) {
                                    //
                                    //
                                    p.addNode(n);
                                    //p.addDrawableObject(n);
                                    for (CoverSection cvs : n.getCoverSections()) {
                                        //cvs.Draw(this.Player);
                                        //if (p.getDrawableObjects().contains(cvs) == false) {
                                        p.addDrawableObject(cvs);
                                        //}




                                    }
                                    //
                                    //
                                }
                            }
                        }
                    }
                }
            }
            //for (DrawableObject DO : DOsToBrighten){
            //    DO.Draw(this);
            //}
        }
        /* long t2 = System.currentTimeMillis();
        long t3 = t2 - t1;
        if (t3 > 500){
            System.out.println("FOWDrawer took " + t3);
        }
        else{
            System.out.println("FOWDrawer took " + t3);
        } */
    }
    public void run3(){
        Player p = this.Player;
        for(CoverSection cs : p.getGame().getMap().getCover()){
            p.removeDrawableObject(cs);
        }
    }
}
