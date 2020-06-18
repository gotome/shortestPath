package Aufgabe2;

public class Distance {
  private Double distance;

  public Distance(Transmitter a, Transmitter b) {
    if (a != null && b != null) {
      distance = Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY() , 2));
    } else {
      distance = 0.0;
    }
  }

  public Double getDistance() {
    return distance;
  }

  public void setDistance(Double distance) {
    this.distance = distance;
  }
}
