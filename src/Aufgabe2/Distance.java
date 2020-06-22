package Aufgabe2;

import java.util.NoSuchElementException;

public class Distance {
  public static double Euclid(Transmitter a, Transmitter b) throws NoSuchElementException {
    if (a == null && b == null) {
      throw new NoSuchElementException("nullpointer exception check transmiter objects");
    }

    return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY() , 2));
  }
}
