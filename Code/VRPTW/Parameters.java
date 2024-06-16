package VRPTW;

public class Parameters {
    public double[] coordX; // Coordinate X
    public double[] coordY; // Coordinate Y
    public double[][] dist; // Distance between i,j

    public void setParam(double[] param1,double[] param2) {
        coordX = param1;
        coordY = param2;
    }

    public double[] getParam1() {
        return coordX;
    }


    public double[] getParam2() {
        return coordY;
    }

    public double[][] getDist() {
        return dist;
    }
    public void calculateParam() {
        if (coordX.length != 0 && coordX.length == coordY.length){
            dist = new double[coordX.length][coordX.length];
        }
            for (int i = 0; i < coordX.length; i++) {
            for (int j = 0; j < coordX.length; j++) {
                dist[i][j] = Math.sqrt((coordX[i]-coordX[j])*(coordX[i]-coordX[j]) + (coordY[i]-coordY[j])*(coordY[i]-coordY[j]));
            }
        }
    }
}
