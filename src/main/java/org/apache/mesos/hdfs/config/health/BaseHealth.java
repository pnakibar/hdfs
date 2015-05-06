package org.apache.mesos.hdfs.config.health;

/**
 * Created by mesosphere on 5/4/15.
 */
public class BaseHealth {
  private SchedulerHealth schedulerHealth;
  private NameNodesHealth nameNodesHealth;
  private DataNodesHealth dataNodesHealth;
  private JournalNodesHealth journalNodesHealth;

  public DataNodesHealth getDataNodesHealth() {
    return dataNodesHealth;
  }

  public void setDataNodesHealth(DataNodesHealth dataNodesHealth) {
    this.dataNodesHealth = dataNodesHealth;
  }

  public JournalNodesHealth getJournalNodesHealth() {
    return journalNodesHealth;
  }

  public void setJournalNodesHealth(JournalNodesHealth journalNodesHealth) {
    this.journalNodesHealth = journalNodesHealth;
  }

  public SchedulerHealth getSchedulerHealth() {
    return schedulerHealth;
  }

  public void setSchedulerHealth(SchedulerHealth schedulerHealth) {
    this.schedulerHealth = schedulerHealth;
  }

  public NameNodesHealth getNameNodesHealth() {
    return nameNodesHealth;
  }

  public void setNameNodesHealth(NameNodesHealth nameNodesHealth) {
    this.nameNodesHealth = nameNodesHealth;
  }
}
