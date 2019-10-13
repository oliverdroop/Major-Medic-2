package client;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class DrawableObject {
    public DrawableObject() {
        super();
    }

    public DrawableObject(Map mainmap) {
        this.Map = mainmap;
        this.Map.addDrawableObject(this);
    }
    public DrawableObject(BufferedImage img, Point p){
        this.Image = img;
        this.Screenpoint = p;
    }
    private Point Screenpoint = new Point(0, 0);
    private BufferedImage Image;
    private String ImgRef;
    private Map Map;

    public void Draw(Player p){
        
    }
    public void Hide(Player p){
        
    }
    public void Draw2(Player p) {
        //BufferedImage img1 = this.getImage();
        Map m = this.getMap();
        Game gm = m.getGame();
        ImageHolder ih = gm.getImageHolder();
        String ref = this.getImgRef();
        BufferedImage img1 = ih.getImageByRef(ref);
        //BufferedImage img1 = this.getMap().getGame().getImageHolder().getImageByRef(this.getImgRef());
        int offx = img1.getWidth() / 2;
        int offy = img1.getHeight() / 2;
        int x = this.getScreenpoint().x - offx;
        int y = this.getScreenpoint().y - offy;
        BufferedImage terrain = p.getTerrain();
        Graphics g = terrain.createGraphics();
        g.drawImage(img1, x, y, null);
        //Drawer d = new Drawer(img1, terrain, new Point(x, y));
        //this.getMap().getGame().getExecutorService().submit(d);
    }
    /* public void Draw(ClientDrawer cd){
        BufferedImage img1 = this.getImage();
        int offx = img1.getWidth() / 2;
        int offy = img1.getHeight() / 2;
        int x = this.getScreenpoint().x - offx;
        int y = this.getScreenpoint().y - offy;
        BufferedImage terrain = cd.getTerrain();
        Graphics g = terrain.createGraphics();
        g.drawImage(img1, x, y, null);
    } */
    public void Draw() {
        for (Player p : this.Map.getGame().getPlayers()) {
            this.Draw(p);
        }
    }
    
    public void Hide2(Player p) {
        //BufferedImage img1 = this.getImage();
        BufferedImage img1 = this.getMap().getGame().getImageHolder().getImageByRef(this.getImgRef());
        int width = img1.getWidth();
        int height = img1.getHeight();
        int offx = width / 2;
        int offy = height / 2;
        int x1 = this.getScreenpoint().x - offx;
        int y1 = this.getScreenpoint().y - offy;
        int x2 = x1 + width;
        int y2 = y1 + height;
        BufferedImage terrain = p.getTerrain();
        BufferedImage terrain2 = this.Map.getTerrain2();
        Graphics g = terrain.createGraphics();
        g.drawImage(terrain2, x1, y1, x2, y2, x1, y1, x2, y2, null);
    }

    /* public void Hide(ClientDrawer cd) {
        BufferedImage img1 = this.getImage();
        int width = img1.getWidth();
        int height = img1.getHeight();
        int offx = width / 2;
        int offy = height / 2;
        int x1 = this.getScreenpoint().x - offx;
        int y1 = this.getScreenpoint().y - offy;
        int x2 = x1 + width;
        int y2 = y1 + height;
        BufferedImage terrain = cd.getTerrain();
        BufferedImage terrain2 = cd.getTerrain2();
        Graphics g = terrain.createGraphics();
        g.drawImage(terrain2, x1, y1, x2, y2, x1, y1, x2, y2, null);
    } */
    public void Hide(){
        for(Player p : this.Map.getGame().getPlayers()){
            this.Hide(p);
        }
    }

    public Point getScreenpoint() {
        return this.Screenpoint;
    }

    public void setScreenpoint(Point p) {
        //System.out.println(p.toString());
        this.Screenpoint.setLocation(p);
    }

    public BufferedImage getImage() {
        return this.Image;
    }

    public void setImage(BufferedImage i) {
        this.Image = i;
    }

    public String getImgRef() {
        return this.ImgRef;
    }

    public void setImgRef(String ref) {
        this.ImgRef = ref;
    }

    public void addImage(BufferedImage image) {
        for (int x = 0; x < this.Image.getWidth(); x++) {
            if (image.getWidth() <= x) {
                for (int y = 0; y < this.Image.getHeight(); y++) {
                    if (image.getHeight() <= y) {
                        int c = image.getRGB(x, y);
                        if (c != 0xFFFFFFFF) {
                            this.Image.setRGB(x, y, c);
                        }
                    }
                }
            }
        }
    }

    public Map getMap() {
        return this.Map;
    }

    public void setMap(Map m) {
        this.Map = m;
    }
    /* public boolean IsVisibleBy(Player p){

    } */
    /* public void Draw2(Player p) {
        int pixelx = (int) Math.round(this.Screenpoint.getX());
        int pixely = (int) Math.round(this.Screenpoint.getY());
        BufferedImage terrain = p.getTerrain();
        //Graphics g = terrain.getGraphics();
        int w = this.Image.getWidth();
        int h = this.Image.getHeight();
        for (int x = -(w / 2); x < (w / 2); x++) {
            for (int y = -(h / 2); y < (h / 2); y++) {
                int xdraw = pixelx + x;
                int ydraw = pixely + y;
                if (xdraw >= 0 && xdraw < terrain.getWidth() && ydraw >= 0 && ydraw < terrain.getHeight()) {
                    int c = this.Image.getRGB(x + (w / 2), y + (h / 2));
                    if (c != 0xFFFFFFFF) {
                        //Color color = new Color(c);
                        terrain.setRGB(xdraw, ydraw, c);
                        //g.setColor(color);
                        //g.drawRect(xdraw, ydraw, 0, 0);

                    }
                }
            }
        }
    }
    public void Hide2(Player p) {
        int pixelx = (int) Math.round(this.Screenpoint.getX());
        int pixely = (int) Math.round(this.Screenpoint.getY());
        BufferedImage terrain = p.getTerrain();
        BufferedImage terrain2 = this.Map.getTerrain2();
        //Graphics g = terrain.getGraphics();
        int w = this.Image.getWidth();
        int h = this.Image.getHeight();
        for (int x = -(w / 2); x < (w / 2); x++) {
            for (int y = -(h / 2); y < (h / 2); y++) {
                int xdraw = pixelx + x;
                int ydraw = pixely + y;
                if (xdraw >= 0 && xdraw < terrain.getWidth() && ydraw >= 0 && ydraw < terrain.getHeight()) {
                    int c = this.Image.getRGB(x + (w / 2), y + (h / 2));
                    if (c != 0xFFFFFFFF) {
                        int c2 = terrain2.getRGB(xdraw, ydraw);
                        terrain.setRGB(xdraw, ydraw, c2);
                    }
                }
            }
        }
    } */
}
