package client;

import java.awt.Point;

import java.io.IOException;
import java.io.OutputStream;

import java.net.Socket;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

public class ServerSender implements Runnable {
    private final List<Node> Nodes;
    private final List<List<DrawableObject>> DrawableObjects;
    private final Player Player;
    private final Socket skt3;
    public ServerSender(Player p, Socket skt3) {
        super();
        this.Player = p;
        this.skt3 = skt3;
        this.Nodes = this.Player.getNodes();
        this.DrawableObjects = this.Player.getDrawableObjects();
    }

    @Override
    public void run() {
        long time1 = System.currentTimeMillis();
        try{
            if (this.getGame().getPlayers().size() > 1) {
                //System.out.println("Starting send");
                Player p = this.Player;
                OutputStream os = this.skt3.getOutputStream();
                this.skt3.setSendBufferSize(2147483647);
                //Find which nodes and drawables are in the viewfinder
                JLabel pbl = p.getPictureBoxLabel();
                Point p1 = new Point(-pbl.getX(), -pbl.getY());
                JLabel vfdr = p.getViewfinder();
                Point p2 = new Point(p1.x + vfdr.getWidth(), p1.y + vfdr.getHeight());
                //all nodes (in viewfinder)
                List<Node> ands = this.getGame().getMap().getNodesInBox(p1, p2);
                //all drawable objects (in viewfinder)
                List<DrawableObject> ados = new ArrayList<DrawableObject>();
                //Get all map's drawable objects
                for (List<DrawableObject> dor : this.getGame().getMap().getDrawableObjects()) {
                    for (DrawableObject do1 : dor) {
                        //Check object is whithin box
                        if (do1.getScreenpoint().x >= p1.x && do1.getScreenpoint().x < p2.x) {
                            if (do1.getScreenpoint().y >= p1.y && do1.getScreenpoint().y < p2.y) {
                                //Check object is not invisible enemy
                                if (p.getAllEnemies().contains(do1) == false){
                                    //add to drawable list
                                    ados.add(do1);
                                }
                            }
                        }
                    }
                }
                //my nodes (in viewfinder)
                List<Node> mnds = new ArrayList<Node>();
                for (Node n : p.getNodes()) {
                    if (ands.contains(n)) {
                        mnds.add(n);
                    }
                }
                //my drawable objects (in viewfinder)
                List<DrawableObject> mdos = new ArrayList<DrawableObject>();
                //Get all player's drawable objects
                for (List<DrawableObject> dor : p.getDrawableObjects()) {
                    for (DrawableObject do1 : dor) {
                        //Check object is whithin box
                        if (do1.getScreenpoint().x >= p1.x && do1.getScreenpoint().x < p2.x) {
                            if (do1.getScreenpoint().y >= p1.y && do1.getScreenpoint().y < p2.y) {
                                //Check object is visible to player
                                //if (p.CanSee(do1.getScreenpoint())){
                                //add to drawable list
                                mdos.add(do1);
                                //}

                            }
                        }
                    }
                }
                //System.out.println("ands is " + ands.size() + " long");
                //System.out.println("ados is " + ados.size() + " long");
                //System.out.println("mnds is " + mnds.size() + " long");
                //System.out.println("mdos is " + mdos.size() + " long");
                for (Node n : ands) {
                    byte[] bs = new byte[13];
                    byte[] prfx = new byte[1];
                    prfx[0] = 0;
                    byte[] x = ByteBuffer.allocate(4).putInt(n.getScreenpoint().x).array();
                    byte[] y = ByteBuffer.allocate(4).putInt(n.getScreenpoint().y).array();
                    byte[] r = ByteBuffer.allocate(4).putInt(Integer.parseInt(n.getImgRef())).array();
                    System.arraycopy(prfx, 0, bs, 0, 1);
                    System.arraycopy(x, 0, bs, 1, 4);
                    System.arraycopy(y, 0, bs, 5, 4);
                    System.arraycopy(r, 0, bs, 9, 4);
                    //baos.write(bs);
                    //baos.writeTo(os);
                    //baos.flush();
                    os.write(bs);
                    os.flush();
                    //String s1 = "ands";
                    //s1 += n.getScreenpoint().x + "," + n.getScreenpoint().y + "," + n.getImgRef();
                    //out.println(s1);
                    //out.flush();
                }
                for (DrawableObject do1 : ados) {
                    byte[] bs = new byte[13];
                    byte[] prfx = new byte[1];
                    prfx[0] = 1;
                    byte[] x = ByteBuffer.allocate(4).putInt(do1.getScreenpoint().x).array();
                    byte[] y = ByteBuffer.allocate(4).putInt(do1.getScreenpoint().y).array();
                    byte[] r = ByteBuffer.allocate(4).putInt(Integer.parseInt(do1.getImgRef())).array();
                    System.arraycopy(prfx, 0, bs, 0, 1);
                    System.arraycopy(x, 0, bs, 1, 4);
                    System.arraycopy(y, 0, bs, 5, 4);
                    System.arraycopy(r, 0, bs, 9, 4);
                    //baos.write(bs);
                    //baos.writeTo(os);
                    //baos.flush();
                    os.write(bs);
                    os.flush();
                    //String s2 = "ados";
                    //s2 += do1.getScreenpoint().x + "," + do1.getScreenpoint().y + "," + do1.getImgRef();
                    //out.println(s2);
                    //out.flush();
                }
                for (Node n : mnds) {
                    byte[] bs = new byte[13];
                    byte[] prfx = new byte[1];
                    prfx[0] = 2;
                    byte[] x = ByteBuffer.allocate(4).putInt(n.getScreenpoint().x).array();
                    byte[] y = ByteBuffer.allocate(4).putInt(n.getScreenpoint().y).array();
                    byte[] r = ByteBuffer.allocate(4).putInt(Integer.parseInt(n.getImgRef())).array();
                    System.arraycopy(prfx, 0, bs, 0, 1);
                    System.arraycopy(x, 0, bs, 1, 4);
                    System.arraycopy(y, 0, bs, 5, 4);
                    System.arraycopy(r, 0, bs, 9, 4);
                    //baos.write(bs);
                    //baos.writeTo(os);
                    //baos.flush();
                    os.write(bs);
                    os.flush();
                    //String s3 = "mnds";
                    //s3 += n.getScreenpoint().x + "," + n.getScreenpoint().y + "," + n.getImgRef();
                    //out.println(s3);
                    //out.flush();
                }
                for (DrawableObject do1 : mdos) {
                    byte[] bs = new byte[13];
                    byte[] prfx = new byte[1];
                    prfx[0] = 3;
                    byte[] x = ByteBuffer.allocate(4).putInt(do1.getScreenpoint().x).array();
                    byte[] y = ByteBuffer.allocate(4).putInt(do1.getScreenpoint().y).array();
                    byte[] r = ByteBuffer.allocate(4).putInt(Integer.parseInt(do1.getImgRef())).array();
                    System.arraycopy(prfx, 0, bs, 0, 1);
                    System.arraycopy(x, 0, bs, 1, 4);
                    System.arraycopy(y, 0, bs, 5, 4);
                    System.arraycopy(r, 0, bs, 9, 4);
                    //baos.write(bs);
                    //baos.writeTo(os);
                    //baos.flush();
                    os.write(bs);
                    os.flush();
                    //String s4 = "mdos";
                    //s4 += do1.getScreenpoint().x + "," + do1.getScreenpoint().y + "," + do1.getImgRef();
                    //out.println(s4);
                    //out.flush();
                }
                //out.close();
                //os.close();
                //skt3.close();
                //System.out.println("Closed output stream");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long time2 = System.currentTimeMillis();
        long diff = time2 - time1;
        System.out.println("ServerSender took " + diff + " milliseconds");
    }
    public Game getGame(){
        return this.Player.getGame();
    }
}
