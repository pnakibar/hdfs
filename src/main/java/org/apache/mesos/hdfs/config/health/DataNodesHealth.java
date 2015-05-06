package org.apache.mesos.hdfs.config.health;

/**
 * Created by mesosphere on 5/4/15.
 */
public class DataNodesHealth {
  private Integer numDataNodes;

  public Integer getNumDataNodes() {
    return numDataNodes;
  }

  public void setNumDataNodes(Integer numDataNodes) {
    this.numDataNodes = numDataNodes;
  }
}
