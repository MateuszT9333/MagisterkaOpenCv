package pl.mateusz.agerecognition.utils;

public class Coordinates {
    public int x;
    public int y;

    public Coordinates() {
    }

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     *
     * @param c1
     * @param c2
     * @return Distanse between c1 and c2 coordinates
     */
    public static int getDistance(Coordinates c1, Coordinates c2){
        return (int) Math.sqrt(Math.pow((c1.x-c2.x), 2) +Math.pow((c1.y-c2.y), 2));
    }
}
