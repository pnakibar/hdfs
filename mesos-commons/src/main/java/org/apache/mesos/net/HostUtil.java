package org.apache.mesos.net;

import org.apache.commons.lang.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 */
public class HostUtil {

  public static String getHostIP() throws UnknownHostException {
    return getHostIP("");
  }

  /**
   * provides compatability with previous code where the LIBPROCESS is request,
   * if it doesn't exist the defaultIP is used, if it is blank we will try to
   * get it from the InetAddress.
   *
   * @param defaultIP
   * @return
   * @throws UnknownHostException
   */
  public static String getHostIP(String defaultIP) throws UnknownHostException {

    // Mesos specific IP on agent nodes
    String hostAddress = System.getenv("LIBPROCESS_IP");
    if (StringUtils.isBlank(hostAddress)) {
      hostAddress = defaultIP;
    }

    // if unavailable acquire it the old fashion way
    if (StringUtils.isBlank(hostAddress)) {
      hostAddress = InetAddress.getLocalHost().getHostAddress();
    }
    return hostAddress;
  }
}
