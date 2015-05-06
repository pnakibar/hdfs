package org.apache.mesos.hdfs.config.health;

/**
 * Created by mesosphere on 5/4/15.
 */
public class NameNodesHealth {
  private boolean nameNode1Initialized;
  private boolean nameNode2Initialized;
  private Integer numNameNodes;

  public boolean isNameNode1Initialized() {
    return nameNode1Initialized;
  }

  public Integer getNumNameNodes() {
    return numNameNodes;
  }

  public void setNumNameNodes(Integer numNameNodes) {
    this.numNameNodes = numNameNodes;
  }

  public void setNameNode1Initialized(boolean nameNode1Initialized) {
    this.nameNode1Initialized = nameNode1Initialized;
  }

  public boolean isNameNode2Initialized() {
    return nameNode2Initialized;
  }

  public void setNameNode2Initialized(boolean nameNode2Initialized) {
    this.nameNode2Initialized = nameNode2Initialized;
  }
}
