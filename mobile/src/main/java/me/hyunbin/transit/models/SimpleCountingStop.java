package me.hyunbin.transit.models;

/**
 * Created by Hyunbin on 2/25/17.
 */

public class SimpleCountingStop {
  private final String stopId;
  private final String stopName;

  public SimpleCountingStop(String stopId, String stopName) {
    this.stopId = stopId;
    this.stopName = stopName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SimpleCountingStop that = (SimpleCountingStop) o;

    if (stopId != null ? !stopId.equals(that.stopId) : that.stopId != null) return false;
    return stopName != null ? stopName.equals(that.stopName) : that.stopName == null;
  }

  @Override
  public int hashCode() {
    int result = stopId != null ? stopId.hashCode() : 0;
    result = 31 * result + (stopName != null ? stopName.hashCode() : 0);
    return result;
  }

  public String getStopId() {
    return stopId;
  }

  public String getStopName() {
    return stopName;
  }
}
