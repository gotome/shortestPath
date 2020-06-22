//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package Aufgabe1;

import java.util.Objects;

public class Vertex {
  private String designation;
  private VertexDefinition definition;
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
    return this.designation;
  }

  public void setDesignation(String designation) {
    this.designation = designation;
  }

  public VertexDefinition getDefinition() {
    return this.definition;
  }

  public void setDefinition(VertexDefinition definition) {
    this.definition = definition;
  }

  public int getStatus() {
    return this.status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getStatusColor() {
    String color = "";
    switch(this.definition) {
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
    }

    return color;
  }

  public String toString() {
    return this.designation;
  }

  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o != null && this.getClass() == o.getClass()) {
      Vertex vertex = (Vertex)o;
      return this.status == vertex.status && Objects.equals(this.designation, vertex.designation) && this.definition == vertex.definition;
    } else {
      return false;
    }
  }

  public int hashCode() {
    return Objects.hash(new Object[]{this.designation, this.definition, this.status});
  }
}
