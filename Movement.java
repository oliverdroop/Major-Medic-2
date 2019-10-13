package client;

import java.awt.Point;

import java.util.ArrayList;
import java.util.List;

public class Movement {
    public Movement() {
        super();
    }

    public Movement(MoveableObject mover, Point destination) {
        Point startpoint = mover.getMap().getClosestNode(mover.getScreenpoint()).getScreenpoint();
        Point destpoint = mover.getMap().getClosestNode(destination).getScreenpoint();
        Find f = new Find();
        double angle = f.Angle(startpoint, destpoint);
        mover.setAngle(angle);
        if (mover.getIsLinkedObject() == false) {
            this.Movers.add(mover);
            MoveableObject link = mover.getLinkedObject();
            if (link != null) {
                this.Movers.add(link);
            }
            if (startpoint != destpoint) {
                double distance = new Find().Distance(startpoint, destpoint);
                int steps = (int) Math.ceil(distance / mover.getStepSize());
                this.mvtPoints = new Point[steps];
                for (int index = 0; index < mvtPoints.length; index++) {
                    double xdiff = (destpoint.x - startpoint.x) / (double) (steps - 1);
                    int x = (int) Math.round(startpoint.x + (index * xdiff));
                    double ydiff = (destpoint.y - startpoint.y) / (double) (steps - 1);
                    int y = (int) Math.round(startpoint.y + (index * ydiff));
                    Point p = new Point(x, y);
                    //System.out.println("mvtpoint" + index + ": " + p.toString());
                    this.mvtPoints[index] = new Point(x, y);
                }
                //Movement m2 = this.Mover.getCurrentMovement();
                //if (m2 != null) {
                //    m2.Timer.stop();
                //}
                for (MoveableObject mo : this.Movers) {
                    mo.setCurrentMovement(this);
                }
                //this.Timer.start();
            }
        }
    }
    //private Timer Timer = new Timer(100, new ActionListener() {
    //    public void actionPerformed(ActionEvent e) {
    //        Update();
    //    }
    //});
    private List<MoveableObject> Movers = new ArrayList<MoveableObject>();
    //private List<MoveableObject> Movers = new ArrayList<MoveableObject>();
    private int PositionIndex = 0;
    private Point[] mvtPoints;

    public void Update() {
        if (this.Movers.size() > 0) {
            Point op = this.mvtPoints[this.PositionIndex];
            this.PositionIndex += 1;
            for (MoveableObject mo : this.Movers) {
                mo.Hide();
                for (Player p : mo.getMap().getGame().getPlayers()) {
                    p.removeDrawableObject(mo);
                }
                boolean removing = false;
                //this.Mover.Hide(this.Mover.getIndividual().getOwner());
                if (this.PositionIndex < this.mvtPoints.length) {
                    Point np = this.mvtPoints[this.PositionIndex];
                    mo.setScreenpoint(np);
                    //if (mo.getIsLinkedObject() == false) {
                    if (np != op) {
                        Find f = new Find();
                        mo.setAngle(f.Angle(op, np));
                    }
                    //}
                    mo.CycleMovementImages();
                    //Find if movement has reached its end
                    if (this.PositionIndex >= this.mvtPoints.length - 1) {
                        mo.setCurrentMovement(null);
                        //Update visibility
                        mo.Hide();
                        if (mo.getIsTransient()) {
                            mo.getMap().removeDrawableObject(mo);
                            removing = true;
                        } else {
                            mo.ShowStationaryImage();
                        }
                    }
                }
                if (removing == false) {
                    if (mo.getIndividual() != null) {
                        mo.getIndividual().getOwner().addDrawableObject(mo);
                    } else {
                        for (Player p : mo.getMap().getGame().getPlayers()) {
                            if (p.CanSee(mo.getScreenpoint())) {
                                p.addDrawableObject(mo);
                            }
                        }
                    }
                }
            }
        }
    }
    //public void addMover(MoveableObject mo){
    //    this.Movers.add(mo);
    //}
}
