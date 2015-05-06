package org.apache.mesos.hdfs.config.health;

/**
 * Created by mesosphere on 5/4/15.
 */
public class SchedulerHealth {
  private String state;

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }
}
