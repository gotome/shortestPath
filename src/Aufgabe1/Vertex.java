package Aufgabe1;

import java.util.Objects;

public class Vertex {
  private String designation;

  public Vertex(String designation) {
    this.designation = designation;
  }

  @Override
  public String toString() {
    return "Vertex{" +
            "designation='" + designation + '\'' +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Vertex vertex = (Vertex) o;
    return Objects.equals(designation, vertex.designation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(designation);
  }
}
