package Aufgabe2;

public class Transmitter {
  private double x, y;
  private double distance = 0.0;
  private String name;

  Transmitter(String name, double x, double y) {
    this.name = name;
    this.x = x;
    this.y = y;
  }

  Transmitter(String name, double x, double y, double distance) {
    this.name = name;
    this.x = x;
    this.y = y;
    this.distance = distance;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getX() {
    return x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getY() {
    return y;
  }

  public void setY(double y) {
    this.y = y;
  }

  public double getDistance() {
    return distance;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  @Override
  public String toString() {
    return  '{' + "" +
            name + " " +
            "x=" + x +
            ", y=" + y +
            '}';
  }
}
