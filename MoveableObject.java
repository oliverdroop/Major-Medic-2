package client;

import java.awt.image.BufferedImage;

public class MoveableObject extends DrawableObject {
    public MoveableObject() {
        super();
        //this.MovementImages = new BufferedImage[6][];
        //this.StationaryImages = new BufferedImage[6];
    }

    public MoveableObject(Map mainmap) {
        this.setMap(mainmap);
        //System.out.println(mainmap.getTerrain().getWidth());
        this.getMap().addDrawableObject(this);
        this.getMap().addMoveableObject(this);
    }
    private double StepSize;
    private double Angle = 0;
    private BufferedImage[][] MovementImages;
    private BufferedImage[] StationaryImages;
    //Below only works if there are 6 directions and 4 movement images per direction
    private String[][] MvtImgsRefs = new String[6][4];
    private String[] StnryImgsRefs = new String[6];
    private int MovementImageNumber = 0;
    private Movement CurrentMovement = null;
    private boolean IsTransient = false;
    private MoveableObject LinkedObject = null;
    private boolean IsLinkedObject = false;

    public double getStepSize() {
        return this.StepSize;
    }

    public void setStepSize(double stepsize) {
        this.StepSize = stepsize;
    }

    public BufferedImage[][] getMovementImages() {
        return this.MovementImages;
    }

    public void setMovementImages(BufferedImage[][] movementimages) {
        this.MovementImages = movementimages;
    }

    public BufferedImage[] getStationaryImages() {
        return this.StationaryImages;
    }

    public void setStationaryImages(BufferedImage[] stationaryimages) {
        this.StationaryImages = stationaryimages;
    }

    public int getMovementImageNumber() {
        return this.MovementImageNumber;
    }

    public void setMovementImageNumber(int num) {
        this.MovementImageNumber = num;
    }
    public String[][] getMvtImgRefs(){
        return this.MvtImgsRefs;
    }
    public void setMvtImgRef(int dir, int num, String ref){
        this.MvtImgsRefs[dir][num] = ref;
    }
    public String[] getStnryImgRefs(){
        return this.StnryImgsRefs;
    }
    public void setStnryImgRef(int dir, String ref){
        this.StnryImgsRefs[dir] = ref;
    }
    public boolean getIsTransient(){
        return this.IsTransient;
    }
    public void setIsTransient(boolean bool){
        this.IsTransient = bool;
    }
    public MoveableObject getLinkedObject(){
        return this.LinkedObject;
    }
    public void setLinkedObject(MoveableObject mo){
        this.LinkedObject = mo;
    }
    public boolean getIsLinkedObject(){
        return this.IsLinkedObject;
    }
    public void setIsLinkedObject(boolean val){
        this.IsLinkedObject = val;
    }
    public void CycleMovementImages() {
        int directionIndex = (int) Math.floor(this.Angle / 60);
        //BufferedImage img = this.MovementImages[directionIndex][this.MovementImageNumber];
        //BufferedImage img2 = new BufferedImage(img.getWidth(), img.getHeight(), 2);
        //Graphics g = img2.createGraphics();
        //g.drawImage(img, 0, 0, null);
        //
        //this.setImage(img2);
        //MoveableObject mo2 = this.LinkedObject;
        this.setImgRef(this.getMvtImgRefs()[directionIndex][this.MovementImageNumber]);
        //if (mo2 != null){
        //    mo2.setImgRef(mo2.getMvtImgRefs()[directionIndex][mo2.MovementImageNumber]);
        //}
        this.MovementImageNumber += 1;
        if (this.MovementImageNumber >= this.MovementImages[directionIndex].length) {
            this.MovementImageNumber = 0;
        }
    }

    public void ShowStationaryImage() {
        int directionIndex = (int) Math.floor(this.Angle / 60);
        while (directionIndex > 5){
            directionIndex -= 6;
        }
        while (directionIndex < 0){
            directionIndex += 6;
        }
        //BufferedImage img = this.StationaryImages[directionIndex];
        //BufferedImage img2 = new BufferedImage(img.getWidth(), img.getHeight(), 2);
        //Graphics g = img2.createGraphics();
        //g.drawImage(img, 0, 0, null);
        //this.setImage(img2);
        this.setImgRef(this.getStnryImgRefs()[directionIndex]);
    }

    public double getAngle() {
        return this.Angle;
    }

    public void setAngle(double a) {
        this.Angle = a;
    }

    public Movement getCurrentMovement() {
        return this.CurrentMovement;
    }

    public void setCurrentMovement(Movement m) {
        this.CurrentMovement = m;
    }

    public Individual getIndividual() {
        try {
            Individual i = (Individual) this;
            //System.out.println(this.getScreenpoint().toString());
            return i;
        } catch (Exception e) {
            //System.out.println("mo!=Individual");
            return null;
        }
    }
}
