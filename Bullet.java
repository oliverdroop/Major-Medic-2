package client;


public class Bullet {
    private String Name;
    private double Weight;
    private double Width;
    private double Length;
    private double FormCoefficient;
    private double BallisticCoefficient;
    private double Density;
    private double DragCoefficient;
    public Bullet(String type) {
        super();
        if (type == "9mm NATO"){
            this.Name = type;
            this.Weight = 0.00804;
            this.Width = 0.009;
            this.Length = 0.019;
            this.FormCoefficient = 0.609;
            this.BallisticCoefficient = 0.163;
        }
        if (type == "5.56 NATO"){
            this.Name = type;
            this.Weight = 0.004;
            this.Width = 0.00556;
            this.Length = 0.045;
            this.FormCoefficient = 0.426;
            this.BallisticCoefficient = 0.304;
        }
        if (type == "7.62 NATO"){
            this.Name = type;
            this.Weight = 0.011;
            this.Width = 0.00762;
            this.Length = 0.051;
            this.FormCoefficient = 0.482;
            this.BallisticCoefficient = 0.393;
        }
        if (type == "7.62*39mm"){
            this.Name = type;
            this.Weight = 0.0079;
            this.Width = 0.00762;
            this.Length = 0.039;
            this.FormCoefficient = 0.495;
            this.BallisticCoefficient = 0.275;
        }
        this.Density = this.Weight / ((Math.PI * this.Width) * this.Length);
        this.DragCoefficient = (this.Density * this.Length) / this.BallisticCoefficient;
    }
    public void CalculateBallisticCoefficient(){
        this.BallisticCoefficient = (this.Weight) / (Math.pow((this.Width), 2) * this.FormCoefficient);
    }
    public String getName(){
        return this.Name;
    }
    public double getWeight(){
        return this.Weight;
    }
    public double getWidth(){
        return this.Width;
    }
    public double getBallisticCoefficient(){
        return this.BallisticCoefficient;
    }
    public double getDragCoefficient(){
        return this.DragCoefficient;
    }
    public double getDensity(){
        return this.Density;
    }
}
