package Aufgabe2;

public class Transmitter {
  public Double x, y;
  public Distance transmittingPower;

  Transmitter(Double x, Double y) {
    this.x = x;
    this.y = y;
  }

  Transmitter(Double x, Double y, Distance transmittingPower) {
    this.x = x;
    this.y = y;
    this.transmittingPower = transmittingPower;
  }

  public Double getX() {
    return x;
  }

  public void setX(Double x) {
    this.x = x;
  }

  public Double getY() {
    return y;
  }

  public void setY(Double y) {
    this.y = y;
  }

  public Double getTransmittingPower() {
    return transmittingPower.getDistance();
  }

  public void setTransmittingPower(Distance transmittingPower) {
    this.transmittingPower = transmittingPower;
  }
}
