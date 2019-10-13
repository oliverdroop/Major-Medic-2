package client;

import java.awt.image.BufferedImage;


public class Weapon extends EquipmentItem {
    private String Name;
    private int Range;
    private int RateOfFire;
    private int MuzzleVelocity;
    private int Weight;
    private Bullet Ammo;
    //private double ProjectileWeight;
    //private double ProjectileWidth;
    //private double ProjFormCoef;
    //private double BallisticCoefficient;
    private BufferedImage Image;
    private String SoundRef;
    private boolean IsDirect = true;
    private boolean IsExplosive = false;
    private int ExplosiveRadius = 0;
    private String ImageRef = "4000";
    private boolean FiresMoving = true;
    private double AccuracyMoving = 0.5;
    private long LastShotTime;
    private int MinFireDelay = 0;

    public Weapon() {
        super();
    }

    public Weapon(Individual user, String weaponName) {
        this.setUser(user);
        this.setProperties(weaponName);
        this.Name = weaponName;
        //this.Image =
        //try {
            //String path2 = "";
            //String routeDirectory = this.getUser().getOwner().getGame().getRouteDirectory();
            //path2 = routeDirectory + "\\Images\\Weapons\\Weapon_" + weaponName + ".bmp";
            //BufferedImage img2 = ImageIO.read(new File(path2));
            //this.Image = img2;
        //} catch (Exception e) {
        //    e.getMessage();
        //}
    }
    private void setProperties(String weaponName) {
        //Divide real life ranges by 2.
        //MP5
        if (weaponName == "MP5A3") {
            this.Range = 100;
            this.RateOfFire = 800;
            this.MuzzleVelocity = 400;
            this.Weight = 3100;
            this.Ammo = this.getUser().getOwner().getBullet("9mm NATO");
            this.SoundRef = "00101";
            this.ImageRef = "4100";
            this.AccuracyMoving = 0.8;
        }
        //SA80
        if (weaponName == "SA80") {
            this.Range = 200;
            this.RateOfFire = 700;
            this.MuzzleVelocity = 940;
            this.Weight = 3820;
            this.Ammo = this.getUser().getOwner().getBullet("5.56 NATO");
            this.SoundRef = "00102";
            this.ImageRef = "4200";
        }
        //FAMAS
        if (weaponName == "FAMAS") {
            this.Range = 225;
            this.RateOfFire = 1000;
            this.MuzzleVelocity = 925;
            this.Weight = 3800;
            this.Ammo = this.getUser().getOwner().getBullet("5.56 NATO");
            this.SoundRef = "00102";
            this.ImageRef = "4300";
        }
        //M4A1
        if (weaponName == "M4A1") {
            this.Range = 250;
            this.RateOfFire = 825;
            this.MuzzleVelocity = 880;
            this.Weight = 3400;
            this.Ammo = this.getUser().getOwner().getBullet("5.56 NATO");
            this.SoundRef = "00103";
            this.ImageRef = "4400";
        }
        //M16
        if (weaponName == "M16") {
            this.Range = 275;
            this.RateOfFire = 825;
            this.MuzzleVelocity = 948;
            this.Weight = 4000;
            this.Ammo = this.getUser().getOwner().getBullet("5.56 NATO");
            this.SoundRef = "00103";
            this.ImageRef = "4600";
        }
        //AK47
        if (weaponName == "AK47") {
            this.Range = 200;
            this.RateOfFire = 600;
            this.MuzzleVelocity = 715;
            this.Weight = 4290;
            this.Ammo = this.getUser().getOwner().getBullet("7.62*39mm");
            this.SoundRef = "00104";
            this.ImageRef = "4700";
        }
        //M40A5
        if (weaponName == "M40A5"){
            this.Range = 400;
            this.RateOfFire = 100;
            this.MuzzleVelocity = 777;
            this.Weight = 7500;
            this.Ammo = this.getUser().getOwner().getBullet("7.62 NATO");
            this.SoundRef = "00103";
            this.ImageRef = "4800";
            this.FiresMoving = false;
        }
        //M240B
        if (weaponName == "M240B"){
            this.Range = 400;
            this.RateOfFire = 950;
            this.MuzzleVelocity = 853;
            this.Weight = 12500;
            this.Ammo = this.getUser().getOwner().getBullet("7.62 NATO");
            this.SoundRef = "00101";
            this.ImageRef = "4900";
            this.FiresMoving = false;
        }
        //M249
        if (weaponName == "M249P"){
            this.Range = 350;
            this.RateOfFire = 800;
            this.MuzzleVelocity = 915;
            this.Weight = 7100;
            this.Ammo = this.getUser().getOwner().getBullet("5.56 NATO");
            this.SoundRef = "00101";
            this.ImageRef = "4910";
            //this.AccuracyMoving = 0.3;
            this.FiresMoving = false;
        }
        //M79 Grenade launcher
        if (weaponName == "M79"){
            this.Range = 175;
            this.RateOfFire = 6;
            this.Weight = 2930;
            this.SoundRef = "00110";
            this.ImageRef = "4920";
            this.IsExplosive = true;
            this.ExplosiveRadius = 10;
            this.IsDirect = false;
        }
        //Warrior Tank base
        if (weaponName == "WRRB"){
            this.Range = 100;
            this.RateOfFire = 800;
            this.MuzzleVelocity = 400;
            this.Weight = 3100;
            this.Ammo = this.getUser().getOwner().getBullet("9mm NATO");
            this.SoundRef = "00101";
            this.ImageRef = "4940";
            this.AccuracyMoving = 0.8;
        }
        //Warrior Tank turret
        if (weaponName == "L21A1"){
            this.Range = 400;
            this.RateOfFire = 10;
            this.SoundRef = "00120";
            this.ImageRef = "4930";
            this.IsExplosive = true;
            this.ExplosiveRadius = 10;
            this.IsDirect = false;
            this.AccuracyMoving = 0.5;
        }
        this.setMinFireDelay();
    }

    public String getName() {
        return this.Name;
    }
    public Bullet getAmmo(){
        return this.Ammo;
    }
    public void setAmmo(Bullet b){
        this.Ammo = b;
    }

    public int getRange() {
        return this.Range;
    }

    public int getRateOfFire() {
        return this.RateOfFire;
    }

    public int getMuzzleVelocity() {
        return this.MuzzleVelocity;
    }

    public int getWeight() {
        return this.Weight;
    }

    public double getProjectileWeight() {
        return this.getAmmo().getWeight();
    }
    public double getProjectileWidth(){
        return this.getAmmo().getWidth();
    }
    public double getBallisticCoefficient(){
        return this.getAmmo().getBallisticCoefficient();
    }
    public BufferedImage getImage(){
        return this.Image;
    }
    public void setImage(BufferedImage img){
        this.Image = img;
    }
    public String getSoundRef(){
        return this.SoundRef;
    }
    public void setSoundRef(String ref){
        this.SoundRef = ref;
    }
    public boolean getIsDirect(){
        return this.IsDirect;
    }
    public void setIsDirect(boolean val){
        this.IsDirect = val;
    }
    public String getImageRef(){
        return this.ImageRef;
    }
    public boolean getFiresMoving(){
        return this.FiresMoving;
    }
    public void setFiresMoving(boolean val){
        this.FiresMoving = val;
    }
    public double getAccuracyMoving(){
        return this.AccuracyMoving;
    }
    public void setAccuracyMoving(double val){
        this.AccuracyMoving = val;
    }
    public boolean getIsExplosive(){
        return this.IsExplosive;
    }
    public void setIsExplosive(boolean val){
        this.IsExplosive = val;
    }
    public int getExplosiveRadius(){
        return this.ExplosiveRadius;
    }
    public void setExplosiveRadius(int rad){
        this.ExplosiveRadius = rad;
    }
    public long getLastShotTime(){
        return this.LastShotTime;
    }
    public void setLastShotTime(long time){
        this.LastShotTime = time;
    }
    public int getMinFireDelay(){
        return this.MinFireDelay;
    }
    public void setMinFireDelay(){
        this.MinFireDelay = 60000 / this.RateOfFire;
    }
}
