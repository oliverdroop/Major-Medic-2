package client;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;

import javax.swing.Timer;

public class Unit {
    private Player Owner;
    private List<Individual> Members = new ArrayList<Individual>();
    private Individual Commander;
    private double CommandRange = 90;
    private double MaximumRadius = 18;
    private Node Destination;
    private int MinMvtDelay = 1000;
    private int NextMover = 0;
    private int VoiceReference = 0;
    private boolean IsBound = false;
    private int Formation = 0;
    private boolean IsPlanningMove = false;
    private Timer TimerMovement = new Timer(MinMvtDelay, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            NextMove(NextMover);
            NextMover += 1;
            if (NextMover >= Members.size()) {
                NextMover = 0;
                TimerMovement.stop();
            }
        }
    });
    public Unit(Player owner) {
        super();
        this.Owner = owner;
        owner.getUnits().add(this);
        int vr = this.getOwner().getUnits().size() - 1;
        while(vr > 1){
            vr -= 2;
        }
        this.VoiceReference = vr;
    }

    public Player getOwner() {
        return this.Owner;
    }

    public List<Individual> getMembers() {
        return this.Members;
    }

    public void addMember(Individual i) {
        this.Members.add(i);
    }

    public void removeMember(Individual i){
        this.Members.remove(i);
    }
    
    public Individual getCommander() {
        return this.Commander;
    }

    public void setCommander(Individual i) {
        this.Commander = i;
    }

    public double getMaximumRadius() {
        return this.MaximumRadius;
    }

    public void setMaximumRadius(double value) {
        this.MaximumRadius = value;
    }

    public int getMinMvtDelay() {
        return this.MinMvtDelay;
    }

    public void setMinMvtDelay(int val) {
        this.MinMvtDelay = val;
    }

    public Node getDestination() {
        return this.Destination;
    }

    public void setDestination(Node destination) {
        this.Destination = destination;
    }
    public int getVoiceReference(){
        return this.VoiceReference;
    }
    public void setVoiceReference(int num){
        this.VoiceReference = num;
    }
    public int getFormation(){
        return this.Formation;
    }
    public void setFormation(int code){
        this.Formation = code;
    }
    public boolean getIsPlanningMove(){
        return this.IsPlanningMove;
    }
    public void setIsPlanningMove(boolean val){
        this.IsPlanningMove = val;
    }
    public void CalculateSetSpeed(boolean uniformSpeed){
        double lowest = 999999999;
        for(Individual i : this.getMembers()){
            i.CalculateSetSpeed();
        }
        if (uniformSpeed == true) {
            for (Individual i : this.getMembers()) {
                if (i.getStepSize() < lowest) {
                    lowest = i.getStepSize();
                }
            }
            for (Individual i : this.getMembers()) {
                i.setStepSize(lowest);
            }
        }
    }
    

    public void PlanMove(Node destination) {
        //
        this.setIsPlanningMove(true);
        this.setDestination(destination);
        RouteSetter rs = new RouteSetter(this, this.getDestination());
        ExecutorService es = this.getOwner().getGame().getExecutorService();
        es.submit(rs);
    }
    public void AttackMove(Individual enemy){
        Individual target = enemy;
        Unit u = this;
        Find f = new Find();
        //Find shortest weapon range
        double shrt = 999999999;
        for (Individual i2 : u.getMembers()) {
            double rng = i2.getWeapons().get(0).getRange();
            if (rng < shrt) {
                shrt = rng;
            }
        }
        //Move within weapon range
        double a = f.Angle(target.getScreenpoint(), this.getCommander().getScreenpoint());
        a = a * Math.PI / 180;
        Point p = new Point(target.getScreenpoint().x, target.getScreenpoint().y);
        p.translate((int) Math.round(Math.sin(a) * (shrt)), (int) -Math.round(Math.cos(a) * (shrt)));
        u.PlanMove(this.getCommander().getMap().getClosestNode(p));
        u.StartMove();
        //System.out.println("Attack-Moving");
        Random rnd = new Random();
        String ref = "0" + this.getVoiceReference() + "02" + rnd.nextInt(2);
        //this.getOwner().getGame().getSoundHolder().Play("0" + this.getVoiceReference() + "02" + rnd.nextInt(2));
        Point p1 = this.getCommander().getScreenpoint();
        Player player = this.getOwner();
        this.getOwner().getGame().getCommunicator().SendSound(player, p1, ref);
    }

    public void StartMove() {
        this.TimerMovement.start();
    }

    public void NextMove(int index) {
        Individual i = this.getMembers().get(index);
        i.TimerRouteStart();
        //new Movement(i, i.getDestination().getScreenpoint());
    }
    public void Select(){
        
        Random rnd = new Random();
        int num = this.getVoiceReference();
        Player p = this.getOwner();
        String ref = "0" + num + "00" + rnd.nextInt(5);
        //this.getOwner().getGame().getSoundHolder().Play("0" + num + "00" + rnd.nextInt(5));
        Point p1 = new Point(0,0);
        this.getOwner().getGame().getCommunicator().SendSound(p, p1, ref);
        //
        p.setSelectedUnit(this);
        for(Individual i :this.getOwner().getIndividuals()){
            i.Hide(p);
            if (i.getCurrentMovement() == null){
                i.ShowStationaryImage();
            }
            i.Draw(p);
        }
        //UpdateUnitInfo(this);
    }
    public void Attack(Individual target){
        Unit u = this;
        for (Individual i1 : u.getMembers()) {
            i1.Attack(target, i1.getWeapons().get(0));
        }
    }
    public void setTimerMovementInterval(int interval){
        this.TimerMovement.setDelay(interval);
    }
    public boolean getIsMoving(){
        boolean mvng = false;
        for (Individual i : this.getMembers()) {
            //if (i.getCurrentTarget() != null) {
            //    trgt = true;
            //    System.out.println("AI: Chosen unit already has target");
            //}
            if (i.getCurrentMovement() != null) {
                mvng = true;
                //System.out.println("AI: Chosen unit already moving");
            
            }
        }
        return mvng;
    }
    public void TryAttack() {
        for (Individual i : this.getMembers()) {
            //test for visible enemies and stop if necessary
            for (Individual i2 : i.getOwner().getVisibleEnemies()) {
                if (i.CanAttack(i2.getScreenpoint())) {
                    i.setDestination(i.getPosition());
                    
                    i.setCurrentTarget(i2);
                }
            }
        }
    }

    /* public void PlanMove2(Node destination) {
        Individual com = this.getCommander();
        Node dest = destination;
        Map m = com.getMap();
        if (com.CanMove(dest) == false) {
            dest = com.getClosestMoveableGoal(dest);
            System.out.println("recalculating destination to " + dest.getScreenpoint());
        }
        List<Node> nodelist1 = m.getCloseCoveredNodes(dest, this.getMaximumRadius());
        List<Node> moveablelist = new ArrayList<Node>();
        for (Node n : nodelist1) {
            if (com.CanMove(n)) {
                if (m.LOMTest(dest, n)) {
                    moveablelist.add(n);
                }
            }
        }
        System.out.println("moveablelist is " + moveablelist.size() + " long.");
        int count = 0;
        while (count < this.getMembers().size()) {
            Individual i = this.getMembers().get(count);
            Node n = moveablelist.get(0);
            i.setDestination(n);
            //i.setRoute(m.getRoute(i.getPosition(), i.getDestination()));
            if (moveablelist.size() > 1) {
                moveablelist.remove(n);
            }
            count += 1;
        }
        this.StartMove();
        //below tests Compound.Contains
        Point p = this.getCommander().getDestination().getScreenpoint();
        List<Compound> comps = m.getCompounds();
        if (comps.size() > 0) {
            for (int index = 0; index < comps.size(); index++) {
                Compound c = comps.get(index);
                if (c.Contains(p)) {
                    System.out.println("Commander is heading into compound " + index);
                }
            }
        }
        //
    }
     public Node getClosestMoveableGoal(Node idealNode){
        Find f = new Find();
        Individual com = this.getCommander();
        Map m = com.getMap();
        Point p1 = com.getScreenpoint();
        Node goal = idealNode;
        if (com.CanMove(goal)){
            //do nothing
        }
        else{
            while (com.CanMove(goal) == false){
                Point p2 = goal.getScreenpoint();
                Point mp = f.Middle(p1, p2);
                Node n = m.getClosestNode(mp);
                goal = n;
            }
        }
        return goal;
    } */
    /* public Node getClosestMoveableGoal2(Node idealNode){
        Find f = new Find();
        Individual com = this.getCommander();
        Node pos = com.getPosition();
        Node goal = idealNode;
        if (com.CanMove(goal)){
            //do nothing
        }
        else{
            Point start = com.getScreenpoint();
            while (com.CanMove(goal) == false){
                int closest = 0;
                double dist1 = f.Distance(start, goal.getScreenpoint());
                for (int index = 0; index < 6; index++){
                    Node nn = goal.getNeighbours()[index];
                    Point pnn = nn.getScreenpoint();
                    Double dist2 = f.Distance(start, pnn);
                    if (dist2 < dist1){
                        closest = index;
                    }
                }
                goal = goal.getNeighbours()[closest];
            }
        }
        return goal;
    } */
    /* public void PlanMove2(Node destination){
        this.setDestination(destination);
        Map m = this.Owner.getMap();
        Node goal = this.getDestination();
        Individual i1 = this.getCommander();
        if (i1.CanMove(goal) == false) {
            System.out.println("Commander can't move to that location.");
            Node n = this.getClosestMoveableGoal(goal);
            goal = n;
        }
        List<Node> orderedList;
        orderedList = m.getCloseCoveredNodes(goal, this.getMaximumRadius());
        // Check to see where is moveable in orderedList
        List<Node> moveableList = new ArrayList<Node>();
        int index = 0;
        while (index < this.getMembers().size()){
            Individual i = this.getMembers().get(index);
            if (orderedList.size() > 0){
                Node n = orderedList.get(0);
                if (i.CanMove(n)){
                    orderedList.remove(n);
                    moveableList.add(n);
                    //System.out.println("added node to moveable list");
                }
            }
            else{
                System.out.println("error: orderedList has no more nodes");
            }
            index += 1;
        }
        // Assign destinations to individuals
        for(int index2 = 0; index2 < this.getMembers().size(); index2++){
            Individual i = this.getMembers().get(index2);
            if (moveableList.size() > 0) {
                Node dest = moveableList.get(0);
                i.setDestination(dest);
                //System.out.print("Individual " + index2 + " can move = " + i.CanMove(dest));
                if (moveableList.size() > 1) {
                    moveableList.remove(dest);
                }
            } else {
                System.out.println("Unit's moveableList has no nodes to move to");
            }
        }
        //System.out.println();
    } */
}
