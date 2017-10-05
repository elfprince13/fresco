package dk.alexandra.fresco.framework.sce.evaluator;

import dk.alexandra.fresco.framework.MPCException;
import dk.alexandra.fresco.framework.NativeProtocol;
import dk.alexandra.fresco.framework.NativeProtocol.EvaluationStatus;
import dk.alexandra.fresco.framework.ProtocolCollection;
import dk.alexandra.fresco.framework.ProtocolEvaluator;
import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.builder.ProtocolBuilder;
import dk.alexandra.fresco.framework.network.Network;
import dk.alexandra.fresco.framework.network.SCENetworkImpl;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.suite.ProtocolSuite;
import dk.alexandra.fresco.suite.ProtocolSuite.RoundSynchronization;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic Evaluator for doing simple gate-by-gate (or protocol by protocol in Practice terms). It
 * is super sequential since each gate is evaluated completely (including network communication).
 *
 * @author Kasper Damgaard
 */
public class SequentialEvaluator<ResourcePoolT extends ResourcePool, Builder extends ProtocolBuilder>
    implements ProtocolEvaluator<ResourcePoolT, Builder> {

  private static final int DEFAULT_CHANNEL = 0;

  /**
   * Quit if more than this amount of empty batches are returned in a row from the protocol
   * producer.
   *
   * This is just to avoid an infinite loop if there is an error in the protocol producer.
   */
  private static final int MAX_EMPTY_BATCHES_IN_A_ROW = 10;
  private static Logger logger = LoggerFactory.getLogger(SequentialEvaluator.class);

  private int maxBatchSize;

  private ProtocolSuite<ResourcePoolT, Builder> protocolSuite;

  public SequentialEvaluator() {
    maxBatchSize = 4096;
  }

  @Override
  public void setProtocolInvocation(ProtocolSuite<ResourcePoolT, Builder> pii) {
    this.protocolSuite = pii;
  }


  /**
   * Sets the maximum amount of gates evaluated in each batch.
   *
   * @param maxBatchSize the maximum batch size.
   */
  @Override
  public void setMaxBatchSize(int maxBatchSize) {
    this.maxBatchSize = maxBatchSize;
  }


  private int doOneRound(ProtocolProducer protocolProducer, ResourcePoolT resourcePool,
      RoundSynchronization<ResourcePoolT> roundSynchronization) throws IOException {
    ProtocolCollectionList<ResourcePoolT> protocols = new ProtocolCollectionList<>(maxBatchSize);
    protocolProducer.getNextProtocols(protocols);
    int size = protocols.size();

    processBatch(protocols, resourcePool);
    roundSynchronization.finishedBatch(size, resourcePool,
        createSceNetwork(resourcePool.getNoOfParties()));
    return size;
  }

  public void eval(ProtocolProducer protocolProducer, ResourcePoolT resourcePool)
      throws IOException {
    int batch = 0;
    int totalProtocols = 0;
    int totalBatches = 0;
    int zeroBatches = 0;
    RoundSynchronization<ResourcePoolT> roundSynchronization =
        protocolSuite.createRoundSynchronization();
    while (protocolProducer.hasNextProtocols()) {
      int numOfProtocolsInBatch = doOneRound(protocolProducer, resourcePool, roundSynchronization);
      logger.trace("Done evaluating batch: " + batch++ + " with " + numOfProtocolsInBatch
          + " native protocols");
      if (numOfProtocolsInBatch == 0) {
        logger.debug("Batch " + batch + " is empty");
      }
      totalProtocols += numOfProtocolsInBatch;
      totalBatches += 1;
      if (numOfProtocolsInBatch == 0) {
        zeroBatches++;
      } else {
        zeroBatches = 0;
      }
      if (zeroBatches > MAX_EMPTY_BATCHES_IN_A_ROW) {
        throw new MPCException("Number of empty batches in a row reached "
            + MAX_EMPTY_BATCHES_IN_A_ROW + "; probably there is a bug in your protocol producer.");
      }
    }
    roundSynchronization.finishedEval(resourcePool,
        createSceNetwork(resourcePool.getNoOfParties()));
    logger.debug("Sequential evaluator done. Evaluated a total of " + totalProtocols
        + " native protocols in " + totalBatches + " batches.");
  }

  /*
   * As soon as this method finishes, it may be called again with a new batch -- ie to process more
   * than one batch at a time, simply return before the first one is finished
   */
  private void processBatch(ProtocolCollection<ResourcePoolT> protocols,
      ResourcePoolT resourcePool) throws IOException {
    Network network = resourcePool.getNetwork();
    SCENetworkImpl sceNetwork = createSceNetwork(resourcePool.getNoOfParties());
    for (NativeProtocol<?, ResourcePoolT> protocol : protocols) {
      int round = 0;
      EvaluationStatus status;
      do {
        status = protocol.evaluate(round, resourcePool, sceNetwork);
        // send phase
        Map<Integer, byte[]> output = sceNetwork.getOutputFromThisRound();
        for (int pId : output.keySet()) {
          // send array since queue is not serializable
          network.send(DEFAULT_CHANNEL, pId, output.get(pId));
        }

        // receive phase
        Map<Integer, ByteBuffer> inputForThisRound = new HashMap<>();
        for (int pId : sceNetwork.getExpectedInputForNextRound()) {
          byte[] messages = network.receive(DEFAULT_CHANNEL, pId);
          inputForThisRound.put(pId, ByteBuffer.wrap(messages));
        }
        sceNetwork.setInput(inputForThisRound);
        sceNetwork.nextRound();
        round++;
      } while (status.equals(EvaluationStatus.HAS_MORE_ROUNDS));
    }
  }

  private SCENetworkImpl createSceNetwork(int noOfParties) {
    return new SCENetworkImpl(noOfParties);
  }
}
