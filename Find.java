package client;

import java.awt.Point;

public class Find {
    public Find() {
        super();
    }

    Double Distance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
    }

    Double Angle(Point p1, Point p2) {
        double A = (Math.atan((double) (p2.x - p1.x) / (double) (p1.y - p2.y)) / (2 * Math.PI)) * 360;
        if ((double) (p2.x - p1.x) >= 0 && (double) (p2.y - p1.y) > 0) {
            A += 180;
        }
        if ((double) (p2.x - p1.x) < 0 && (double) (p2.y - p1.y) > 0) {
            A += 180;
        }
        if ((double) (p2.x - p1.x) >= 0 && (double) (p2.y - p1.y) <= 0) {
            //do nothing



        }
        if ((double) (p2.x - p1.x) < 0 && (double) (p2.y - p1.y) <= 0) {
            A += 360;
        }
        return (A);
    }
    int AngleCode(Point p1, Point p2){
        return (int) Math.floor(this.Angle(p1, p2) / 60);
    }

    Point Middle(Point p1, Point p2) {
        int x = (int) Math.round((p1.x + p2.x) / (double) 2);
        int y = (int) Math.round((p1.y + p2.y) / (double) 2);
        return new Point(x, y);
    }

    boolean IsCross(Point p1, Point p2, Point p3, Point p4) {
        boolean iscross = false;
        boolean straddle1 = false;
        boolean straddle2 = false;
        int xdiff1 = p2.x - p1.x;
        int ydiff1 = p2.y - p1.y;
        int xdiff2 = p4.x - p3.x;
        int ydiff2 = p4.y - p3.y;
        if (xdiff1 != 0) {
            //if (ydiff1 != 0) {
            double gradient1 = ydiff1 / (double) (xdiff1);
            double c1 = p1.y - (p1.x * gradient1);
            //
            double yval1 = (p3.x * gradient1) + c1;
            double yval2 = (p4.x * gradient1) + c1;
            if (yval1 - p3.y <= 1 && yval2 - p4.y >= -1) {
                straddle1 = true;
                //System.out.println("Straddle1 is true");
            }
            if (yval1 - p3.y >= -1 && yval2 - p4.y <= 1) {
                straddle1 = true;
                //System.out.println("Straddle1 is true");
            }
            //}
        } else {
            if (ydiff1 != 0) {
                double gradient1 = xdiff1 / (double) (ydiff1);
                double c1 = p1.x - (p1.y * gradient1);
                double xval1 = (p3.y * gradient1) + c1;
                double xval2 = (p4.y * gradient1) + c1;
                if (xval1 - p3.x <= 1 && xval2 - p4.x >= -1) {
                    straddle1 = true;
                    //System.out.println("Straddle1 is true");
                }
                if (xval1 - p3.x >= -1 && xval2 - p4.x <= 1) {
                    straddle1 = true;
                    //System.out.println("Straddle1 is true");
                }
            }
        }
        if (straddle1 == true) {
            if (xdiff2 != 0) {
                //if (ydiff2 != 0) {
                double gradient2 = ydiff2 / (double) (xdiff2);
                double c2 = p3.y - (p3.x * gradient2);
                //
                double yval3 = (p1.x * gradient2) + c2;
                double yval4 = (p2.x * gradient2) + c2;
                if (yval3 - p1.y <= 1 && yval4 - p2.y >= -1) {
                    straddle2 = true;
                    //System.out.println("Straddle2 is true");
                }
                if (yval3 - p1.y >= -1 && yval4 - p2.y <= 1) {
                    straddle2 = true;
                    //System.out.println("Straddle2 is true");
                }
                //}
            } else {
                if (ydiff2 != 0) {
                    double gradient2 = xdiff2 / (double) (ydiff2);
                    double c2 = p3.x - (p3.y * gradient2);
                    double xval3 = (p1.y * gradient2) + c2;
                    double xval4 = (p2.y * gradient2) + c2;
                    if (xval3 - p1.x <= 1 && xval4 - p2.x >= -1) {
                        straddle2 = true;
                        //System.out.println("Straddle2 is true");
                    }
                    if (xval3 - p1.x >= -1 && xval4 - p2.x <= 1) {
                        straddle2 = true;
                        //System.out.println("Straddle2 is true");
                    }
                } else {
                    System.out.println("error:" + p1.toString() + p2.toString() + p3.toString() + p4.toString());
                }
            }
        }
        if (straddle1 == true && straddle2 == true) {
            iscross = true;
            //System.out.println("cross detected");
        }
        //System.out.println(iscross);
        return iscross;
    }

    boolean IsCross2(Point p1, Point p2, Point p3, Point p4) {
        boolean iscross = false;
        boolean straddle1 = false;
        boolean straddle2 = false;
        int xdiff1 = p2.x - p1.x;
        int ydiff1 = p2.y - p1.y;
        int xdiff2 = p4.x - p3.x;
        int ydiff2 = p4.y - p3.y;
        if (xdiff1 != 0) {
            //if (ydiff1 != 0) {
            double gradient1 = ydiff1 / (double) (xdiff1);
            int c1 = p1.y - (int) Math.round(p1.x * gradient1);
            //
            int yval1 = (int) Math.round(p3.x * gradient1) + c1;
            int yval2 = (int) Math.round(p4.x * gradient1) + c1;
            if (yval1 - p3.y <= 0 && yval2 - p4.y >= 0) {
                straddle1 = true;
                //System.out.println("Straddle1 is true");
            }
            if (yval1 - p3.y >= 0 && yval2 - p4.y <= 0) {
                straddle1 = true;
                //System.out.println("Straddle1 is true");
            }
            //}
        } else {
            if (ydiff1 != 0) {
                double gradient1 = xdiff1 / (double) (ydiff1);
                int c1 = p1.x - (int) Math.round(p1.y * gradient1);
                int xval1 = (int) Math.round(p3.y * gradient1) + c1;
                int xval2 = (int) Math.round(p4.y * gradient1) + c1;
                if (xval1 - p3.x <= 0 && xval2 - p4.x >= 0) {
                    straddle1 = true;
                    //System.out.println("Straddle1 is true");
                }
                if (xval1 - p3.x >= 0 && xval2 - p4.x <= 0) {
                    straddle1 = true;
                    //System.out.println("Straddle1 is true");
                }
            }
        }
        if (xdiff2 != 0) {
            //if (ydiff2 != 0) {
            double gradient2 = ydiff2 / (double) (xdiff2);
            int c2 = p3.y - (int) Math.round(p3.x * gradient2);
            //
            int yval3 = (int) Math.round(p1.x * gradient2) + c2;
            int yval4 = (int) Math.round(p2.x * gradient2) + c2;
            if (yval3 - p1.y <= 0 && yval4 - p2.y >= 0) {
                straddle2 = true;
                //System.out.println("Straddle2 is true");
            }
            if (yval3 - p1.y >= 0 && yval4 - p2.y <= 0) {
                straddle2 = true;
                //System.out.println("Straddle2 is true");
            }
            //}
        } else {
            if (ydiff2 != 0) {
                double gradient2 = xdiff2 / (double) (ydiff2);
                int c2 = p3.x - (int) Math.round(p3.y * gradient2);
                int xval3 = (int) Math.round(p1.y * gradient2) + c2;
                int xval4 = (int) Math.round(p2.y * gradient2) + c2;
                if (xval3 - p1.x <= 0 && xval4 - p2.x >= 0) {
                    straddle2 = true;
                    //System.out.println("Straddle2 is true");
                }
                if (xval3 - p1.x >= 0 && xval4 - p2.x <= 0) {
                    straddle2 = true;
                    //System.out.println("Straddle2 is true");
                }
            } else {
                System.out.println("error:" + p1.toString() + p2.toString() + p3.toString() + p4.toString());
            }
        }
        if (straddle1 == true && straddle2 == true) {
            iscross = true;
            //System.out.println("cross detected");
        }
        //System.out.println(iscross);
        return iscross;
    }

    boolean IsBetween(Point centre, Point left, Point right, Point input) {
        boolean output = false;
        double aleft = this.Angle(centre, left);
        double aright = this.Angle(centre, right);
        if (aleft > aright) {
            aleft -= 360;
        }
        double ainput = this.Angle(centre, input);
        if (ainput >= aleft && ainput <= aright) {
            output = true;
        } else {
            ainput -= 360;
            if (ainput >= aleft && ainput <= aright) {
                output = true;
            }
        }
        return output;
    }
}
