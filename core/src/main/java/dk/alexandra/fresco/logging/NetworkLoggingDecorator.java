package dk.alexandra.fresco.logging;

import dk.alexandra.fresco.framework.network.Network;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

public class NetworkLoggingDecorator implements Network, PerformanceLogger, Closeable {

  public static final String ID = "My ID";
  public static final String NETWORK_PARTY_BYTES = "Amount of bytes received pr. party";
  public static final String NETWORK_TOTAL_BYTES = "Total amount of bytes received";
  public static final String NETWORK_TOTAL_BATCHES = "Total amount of batches received";
  public static final String NETWORK_MAX_BYTES = "Maximum amount of bytes received";
  public static final String NETWORK_MIN_BYTES = "Minimum amount of bytes received";

  private Network delegate;
  private Map<Integer, PartyStats> partyStatsMap = new HashMap<>();
  private int minBytesReceived = Integer.MAX_VALUE;
  private int maxBytesReceived = 0;

  public NetworkLoggingDecorator(Network network) {
    this.delegate = network;
  }

  @Override
  public byte[] receive(int partyId) {
    byte[] res = this.delegate.receive(partyId);
    int noBytes = res.length;
    partyStatsMap.computeIfAbsent(partyId, (i) -> new PartyStats()).recordTransmission(noBytes);
    minBytesReceived = Math.min(noBytes, minBytesReceived);
    maxBytesReceived = Math.max(noBytes, maxBytesReceived);
    return res;
  }

  @Override
  public int getNoOfParties() {
    return delegate.getNoOfParties();
  }

  @Override
  public void send(int partyId, byte[] data) {
    this.delegate.send(partyId, data);
  }
   
  @Override
  public void printToLog(Logger log, int myId) {
    log.info(makeLogString(myId));
  }

  @Override
  public void reset() {
    partyStatsMap.clear();
    minBytesReceived = Integer.MAX_VALUE;
    maxBytesReceived = 0;
  }

  @Override
  public void close() throws IOException {
    if (delegate instanceof Closeable) {
      ((Closeable) delegate).close();
    }
  }

  private class PartyStats {
    private int count;
    private int noBytes;

    public void recordTransmission(int noBytes) {
      this.count++;
      this.noBytes += noBytes;
    }
  }

  @Override
  public Map<String, Object> getLoggedValues(int myId) {
    Map<String, Object> values = new HashMap<>();
    values.put(ID, myId);
    
    long totalNoBytes = 0;
    int noNetworkBatches = 0;
    Map<Integer, Integer> partyBytes = new HashMap<>();
    for (Integer partyId : partyStatsMap.keySet()) {
      PartyStats partyStats = partyStatsMap.get(partyId);
      partyBytes.put(partyId, partyStats.noBytes);
      totalNoBytes += partyStats.noBytes;
      noNetworkBatches += partyStats.count;
    }
    values.put(NETWORK_PARTY_BYTES, partyBytes);
    values.put(NETWORK_TOTAL_BYTES, totalNoBytes);
    values.put(NETWORK_TOTAL_BATCHES, noNetworkBatches);
    values.put(NETWORK_MAX_BYTES, this.maxBytesReceived);
    values.put(NETWORK_MIN_BYTES, this.minBytesReceived);
    return values;
  }

}
