package Aufgabe2;

public class Distance {
  public static double Euclid(Transmitter a, Transmitter b) {
    if (a != null && b != null) {
      return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY() , 2));
    } else {
      return 0.0;
    }
  }
}
