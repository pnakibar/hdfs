package org.apache.mesos.hdfs.config.health;

/**
 * Created by mesosphere on 5/4/15.
 */
public class JournalNodesHealth {
  private Integer numJournalNodes;

  public Integer getNumJournalNodes() {
    return numJournalNodes;
  }

  public void setNumJournalNodes(Integer numJournalNodes) {
    this.numJournalNodes = numJournalNodes;
  }
}
