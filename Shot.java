package client;

import java.awt.Point;
import java.awt.image.BufferedImage;

import java.util.Random;

public class Shot {
    private Individual Attacker;
    private Individual Target;
    private Weapon Weapon;
    private double Distance;

    public Shot(Individual i1, Individual i2, Weapon w) {
        super();
        this.Attacker = i1;
        this.Target = i2;
        this.Weapon = w;
        long now = System.currentTimeMillis();
        if (now >= w.getLastShotTime() + w.getMinFireDelay()) {
            w.setLastShotTime(now);
            Point p1 = i1.getScreenpoint();
            //Point p2 = i2.getScreenpoint();
            for (Player p : i1.getOwner().getGame().getPlayers()) {
                boolean seesShot = false;
                //Test if any of their members can see the shot
                for (Individual i : p.getIndividuals()) {
                    if (i.CanSee(i1.getScreenpoint()) && i.CanSee(i2.getScreenpoint())) {
                        seesShot = true;
                    }
                }
                if (seesShot == true) {
                    //Show the player the shot
                    BufferedImage terrain = p.getTerrain();
                    if (this.Weapon.getIsDirect() == true) {
                        this.Attacker.getMap().getGame().getCommunicator().SendShotAnimation(p,
                                                                                             Attacker.getScreenpoint(),
                                                                                             Attacker.getAngle());
                    }
                    //Play sound
                    this.Attacker.getOwner().getGame().getCommunicator().SendSound(p, p1, this.Weapon.getSoundRef());
                }
            }
            //Calculate hit success
            if (this.HitsTarget()) {
                //System.out.println("Successful hit");
                if (this.Weapon.getIsExplosive() == false) {
                    //send blood splatter animation
                    Point p3 = null;
                    Point p4 = null;
                    p3 = this.Attacker.getScreenpoint();
                    p4 = this.Target.getScreenpoint();
                    for (Player p : this.Attacker.getOwner().getGame().getPlayers()) {
                        Find f = new Find();
                        int a = (int) Math.round(f.Angle(p3, p4));
                        String as = "" + a;
                        this.Attacker.getOwner().getGame().getCommunicator().SendBloodSplatter(p,
                                                                                               this.Target.getScreenpoint(),
                                                                                               as);
                    }
                    //Calculate damage
                    this.Target.setHealth(this.Target.getHealth() - this.DamageToTarget());
                    if (this.Target.getHealth() <= 0) {
                        //Kill individual
                        this.Target.Die();
                        for (Individual i : this.Attacker.getOwner().getIndividuals()) {
                            if (i.getCurrentTarget() == this.Target) {
                                i.setCurrentTarget(null);
                            }
                        }
                    }
                }
                if (this.Weapon.getIsExplosive() == true) {
                    Explosion e =
                        new Explosion(this.Target.getScreenpoint(), this.Weapon.getExplosiveRadius(),
                                      this.Attacker.getMap());
                }
            } else {
                if (this.Weapon.getIsExplosive() == true) {
                    //Scatter explosion
                    Random rnd = new Random();
                    Node n1 = this.Target.getPosition();
                    int nbr = rnd.nextInt(n1.getNeighbours().length);
                    Node n2 = n1.getNeighbours()[nbr];
                    Explosion e =
                        new Explosion(n2.getScreenpoint(), this.Weapon.getExplosiveRadius(), this.Attacker.getMap());
                }
                //System.out.println("Miss");
                //scatter explosive projectile
            }
        }
    }

    public boolean HitsTarget() {
        boolean success = false;
        double madj = 1;
        if (this.Attacker.getCurrentMovement() != null) {
            madj = this.Weapon.getAccuracyMoving();
        }
        double badj = 1;
        if (this.Attacker.CanSee(this.Target.getScreenpoint()) == false) {
            badj = this.Attacker.getAccuracyBlind();
        }
        Find f = new Find();
        double cadj = 1;
        Node n1 = this.Target.getPosition();
        Point p1 = this.Attacker.getScreenpoint();
        Point p2 = this.Target.getScreenpoint();
        double angl = f.Angle(p2, p1);
        int a = f.AngleCode(p2, p1);
        int cvr = n1.getCoverDescription()[a];
        if (this.Attacker.getWeapons().get(0).getIsDirect() == true) {
            if (cvr == 1) {
                //low vegetation
                cadj = 0.9;
            }
            if (cvr == 2) {
                //low bank
                cadj = 0.8;
            }
            if (cvr == 3) {
                //medium vegetation
                cadj = 0.7;
            }
            if (cvr == 4) {
                //medium wall
                cadj = 0.5;
            }
            if (cvr == 5) {
                cadj = 0.4;
            }
            if (cvr == 6) {
                cadj = 0;
            }
        }
        double dist = f.Distance(this.Attacker.getScreenpoint(), this.Target.getScreenpoint());
        this.Distance = dist;
        double rng = this.Weapon.getRange() * this.Attacker.getTraining();
        Random rnd = new Random();
        double th = dist / rng;
        double d = rnd.nextDouble();
        double t2 = d * madj * badj;
        double t3 = d * madj * badj * cadj;
        if (t3 >= th) {
            success = true;
        }
        else{
            if (t2 >= th){
                System.out.println("Shot missed due to target's cover");
            }
        }
        return success;
    }

    public int DamageToTarget() {
        //Set sample distance in metres
        double sampleLength = 1;
        //double sampleLength = this.Distance;
        //Find number of sample iterations
        int itrs = (int) Math.floor(this.Distance / sampleLength);
        //Initial velocity
        double vlct = this.Weapon.getMuzzleVelocity();
        //Air density
        double dnst = 1.2;
        //Arbitrary adjustment
        double adj = 0.00001;
        //Calculate final velocity
        Bullet ammo = this.Weapon.getAmmo();
        double dc = ammo.getDragCoefficient();
        double m = ammo.getWeight();
        while (itrs > 0) {
            //Find force on bullet due to drag
            double f = 0.5 * dnst * Math.pow(vlct, 2) * dc * adj;
            //Find acceleration
            double a = f / m;
            //Find time taken to travel sample length
            double t = sampleLength / vlct;
            //Find velocity change
            double chg = (a * t);
            //Find current velocity
            vlct -= chg;
            //Report
            //System.out.println(chg);
            //Prepare to repeat
            itrs -= 1;
        }
        System.out.println("Bullet velocity " + vlct);
        //Square of velocity
        double vsq = Math.pow(vlct, 2);
        //Kinetic energy at target
        double nrg = (this.Weapon.getProjectileWeight() * vsq) / 2;
        //Adjustment constant
        double cnst = 0.01;
        //Damage
        int dmg = (int) Math.ceil(nrg * cnst);
        //Adjust for armoured targets
        if (this.Target.getIsArmoured() == true) {
            dmg = 1;
        }
        System.out.println("Individual did " + dmg + " damage");
        return dmg;
    }
    /* public void DrawBullet2(){
        Individual i1 = this.Attacker;
        Weapon w = this.Weapon;
        Point p2 = this.Target.getScreenpoint();
        MoveableObject bullet = new MoveableObject(i1.getMap());
        bullet.setIsTransient(true);
        bullet.setAngle(0);
        bullet.setStepSize(Math.ceil(w.getMuzzleVelocity() / (double) 20));
        BufferedImage img = new BufferedImage(2, 2, 2);
        img.setRGB(0, 0, 0xFFFDFDFD);
        img.setRGB(1, 0, 0xFFFDFDFD);
        img.setRGB(0, 1, 0xFFFDFDFD);
        img.setRGB(1, 1, 0xFFFDFDFD);
        BufferedImage[] list1 = new BufferedImage[1];
        list1[0] = img;
        bullet.setStationaryImages(list1);
        BufferedImage[][] list2 = new BufferedImage[6][];
        for (int a = 0; a < 6; a++) {
            list2[a] = list1;
        }
        bullet.setMovementImages(list2);
        bullet.setImage(img);
        bullet.setImgRef(ref);
        bullet.setScreenpoint(i1.getScreenpoint());
        Movement m = new Movement(bullet, p2);
    } */
}
