package Aufgabe1;

import java.util.Objects;

public class Vertex {
  private String designation;
  //
  private VertexDefinition definition;
  //status -> 0...free, 1...paused, 2...in process, 3...not available
  private int status;

  public Vertex(String designation, VertexDefinition definition) {
    this.designation = designation;
    this.definition = definition;
  }

  public Vertex(String designation, VertexDefinition definition, int status) {
    this.designation = designation;
    this.definition = definition;
    this.status = status;
  }

  public String getDesignation() {
    return designation;
  }

  public void setDesignation(String designation) {
    this.designation = designation;
  }

  public VertexDefinition getDefinition() {
    return definition;
  }

  public void setDefinition(VertexDefinition definition) {
    this.definition = definition;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getStatusColor() {
    String color = "";
    switch (this.definition) {
      case ACCIDENT:
        color = "RED";
        break;
      case HOSPITAL:
        color = "GREEN";
        break;
      case AMBULANCE:
        color = "ORANGE";
        break;
      case STANDARD:
        color = "GRAY";
        break;
    }
    return color;
  }

  @Override
  public String toString() {
    return this.designation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Vertex vertex = (Vertex) o;
    return status == vertex.status &&
            Objects.equals(designation, vertex.designation) &&
            definition == vertex.definition;
  }

  @Override
  public int hashCode() {
    return Objects.hash(designation, definition, status);
  }
}
