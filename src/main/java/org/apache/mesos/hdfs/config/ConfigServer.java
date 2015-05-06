package org.apache.mesos.hdfs.config;

import com.floreysoft.jmte.Engine;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import org.apache.mesos.hdfs.config.health.*;
import org.apache.mesos.hdfs.state.LiveState;
import org.apache.mesos.hdfs.state.PersistentState;
import org.apache.mesos.hdfs.util.HDFSConstants;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ConfigServer {

  private Server server;
  private Engine engine;
  private SchedulerConf schedulerConf;
  private PersistentState persistentState;
  private LiveState liveState;

  @Inject
  public ConfigServer(SchedulerConf schedulerConf, LiveState liveState) throws Exception {
    this(schedulerConf, new PersistentState(schedulerConf), liveState);
  }

  public ConfigServer(SchedulerConf schedulerConf, PersistentState persistentState,
      LiveState liveState)
      throws Exception {
    this.schedulerConf = schedulerConf;
    this.persistentState = persistentState;
    this.liveState = liveState;
    engine = new Engine();
    server = new Server(schedulerConf.getConfigServerPort());
    ResourceHandler resourceHandler = new ResourceHandler();
    resourceHandler.setResourceBase(schedulerConf.getExecutorPath());
    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[]{
        resourceHandler, new HealthHandler(), new ServeHdfsConfigHandler()});
    server.setHandler(handlers);
    server.start();
  }

  public void stop() throws Exception {
    server.stop();
  }

  private class HealthHandler extends AbstractHandler {

    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse) throws IOException, ServletException {
      if (!Strings.isNullOrEmpty(s) && s.startsWith("/health")) {

        BaseHealth health = new BaseHealth();
        if (s.endsWith("/health") || s.endsWith("/scheduler")) {
          health.setSchedulerHealth(getSchedulerHealth());
        }
        if (s.endsWith("/health") || s.endsWith("/namenodes")) {
          health.setNameNodesHealth(getNameNodesHealth());
        }
        if (s.endsWith("/health") || s.endsWith("/datanodes")) {
          health.setDataNodesHealth(getDataNodesHealth());
        }
        if (s.endsWith("/health") || s.endsWith("/journalnodes")) {
          health.setJournalNodesHealth(getJournalNodesHealth());
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        String content = mapper.writeValueAsString(health);

        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.setHeader("Content-Length", Integer.toString(content.length()));

          // TODO: Take care of scenarios when atleast one of the sub-components is not healthy, by returning a 5XX.
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        request.setHandled(true);
        httpServletResponse.getWriter().println(content);
      } else {
        request.setHandled(false);
      }
    }

    private DataNodesHealth getDataNodesHealth() {
      DataNodesHealth health = new DataNodesHealth();

      return health;
    }

    private JournalNodesHealth getJournalNodesHealth() {
      JournalNodesHealth health = new JournalNodesHealth();
      health.setNumJournalNodes(liveState.getJournalNodeSize());
      return health;
    }

    private NameNodesHealth getNameNodesHealth() {
      NameNodesHealth health = new NameNodesHealth();
      health.setNameNode1Initialized(liveState.isNameNode1Initialized());
      health.setNameNode2Initialized(liveState.isNameNode2Initialized());
      health.setNumNameNodes(liveState.getNameNodeSize());
      return health;
    }

    private SchedulerHealth getSchedulerHealth() {
      SchedulerHealth health = new SchedulerHealth();
      health.setState("Running");
      return health;
    }

  }

  private class ServeHdfsConfigHandler extends AbstractHandler {
    public synchronized void handle(String target, Request baseRequest, HttpServletRequest request,
                                        HttpServletResponse response) throws IOException {
            if (!Strings.isNullOrEmpty(target) && target.endsWith(HDFSConstants.HDFS_CONFIG_FILE_NAME)) {
                File confFile = new File(schedulerConf.getConfigPath());

                if (!confFile.exists()) {
                    throw new FileNotFoundException("Couldn't file config file: " + confFile.getPath()
                            + ". Please make sure it exists.");
                }

                String content = new String(Files.readAllBytes(Paths.get(confFile.getPath())));

                Set<String> nameNodes = new TreeSet<>();
                nameNodes.addAll(persistentState.getNameNodes().keySet());

                Set<String> journalNodes = new TreeSet<>();
                journalNodes.addAll(persistentState.getJournalNodes().keySet());

                Map<String, Object> model = new HashMap<>();
                Iterator<String> iter = nameNodes.iterator();
                if (iter.hasNext()) {
                    model.put("nn1Hostname", iter.next());
                }
                if (iter.hasNext()) {
                    model.put("nn2Hostname", iter.next());
                }

                String journalNodeString = "";
                for (String jn : journalNodes) {
                    journalNodeString += jn + ":8485;";
                }
                if (!journalNodeString.isEmpty()) {
                    // Chop the trailing ,
                    journalNodeString = journalNodeString.substring(0, journalNodeString.length() - 1);
                }

                model.put("journalnodes", journalNodeString);
                model.put("frameworkName", schedulerConf.getFrameworkName());
                model.put("dataDir", schedulerConf.getDataDir());
                model.put("haZookeeperQuorum", schedulerConf.getHaZookeeperQuorum());

                content = engine.transform(content, model);

                response.setContentType("application/octet-stream;charset=utf-8");
                response.setHeader("Content-Disposition", "attachment; filename=\"" +
                        HDFSConstants.HDFS_CONFIG_FILE_NAME + "\" ");
                response.setHeader("Content-Transfer-Encoding", "binary");
                response.setHeader("Content-Length", Integer.toString(content.length()));

                response.setStatus(HttpServletResponse.SC_OK);
                baseRequest.setHandled(true);
                response.getWriter().println(content);
            } else {
                baseRequest.setHandled(false);
            }
        }
  }

}
