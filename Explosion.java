package client;

import java.awt.Point;

import java.util.ArrayList;
import java.util.List;

public class Explosion {
    private Map MainMap;
    private Point Screenpoint;
    private int Radius;
    public Explosion(Point p, int r, Map m) {
        super();
        this.Screenpoint = p;
        this.Radius = r;
        this.MainMap = m;
        this.SendAnimation();
        this.Damage();
    }
    public Point getScreenpoint(){
        return this.Screenpoint;
    }
    public void setScreenpoint(Point p){
        this.Screenpoint = p;
    }
    public int getRadius(){
        return this.Radius;
    }
    public void setRadius(int r){
        this.Radius = r;
    }
    public Map getMap(){
        return this.MainMap;
    }
    public void SendAnimation(){
        for(Player p : this.MainMap.getGame().getPlayers()){
            this.getMap().getGame().getCommunicator().SendExplosionAnimation(p, this.Screenpoint, this.Radius);
        }
    }
    public void Damage(){
        for(Player p : this.MainMap.getGame().getPlayers()){
            List<Individual> inds = new ArrayList<Individual>();
            for(Individual i : p.getIndividuals()){
                inds.add(i);
            }
            for(Individual i : inds){
                Point p1 = i.getScreenpoint();
                Point p2 = this.Screenpoint;
                Find f = new Find();
                double dist = f.Distance(p1, p2);
                int mxdm = 50;
                if (dist <= 1 * this.Radius){
                    //succesful hit
                    double val = 1;
                    if(dist != 0){
                        val = this.Radius / dist;
                    }
                    int dmg = (int)Math.round(Math.pow((mxdm * val), 2));
                    i.setHealth(i.getHealth() - dmg);
                    if (i.getHealth() <= 0){
                        i.Die();
                    }
                }
                
            }
        }
    }
}
