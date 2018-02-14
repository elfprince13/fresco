package dk.alexandra.fresco.suite.marlin.synchronization;

import dk.alexandra.fresco.framework.NativeProtocol;
import dk.alexandra.fresco.framework.ProtocolCollection;
import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.builder.numeric.BuilderFactoryNumeric;
import dk.alexandra.fresco.framework.builder.numeric.ProtocolBuilderNumeric;
import dk.alexandra.fresco.framework.network.Network;
import dk.alexandra.fresco.framework.sce.evaluator.BatchedStrategy;
import dk.alexandra.fresco.framework.sce.evaluator.NetworkBatchDecorator;
import dk.alexandra.fresco.framework.sce.evaluator.ProtocolCollectionList;
import dk.alexandra.fresco.lib.field.integer.BasicNumericContext;
import dk.alexandra.fresco.suite.ProtocolSuite.RoundSynchronization;
import dk.alexandra.fresco.suite.marlin.MarlinBuilder;
import dk.alexandra.fresco.suite.marlin.datatypes.BigUInt;
import dk.alexandra.fresco.suite.marlin.datatypes.BigUIntFactory;
import dk.alexandra.fresco.suite.marlin.protocols.computations.MarlinMacCheckComputation;
import dk.alexandra.fresco.suite.marlin.protocols.natives.MarlinOutputProtocol;
import dk.alexandra.fresco.suite.marlin.resource.MarlinResourcePool;
import dk.alexandra.fresco.suite.marlin.resource.storage.MarlinOpenedValueStore;

public class MarlinRoundSynchronization<T extends BigUInt<T>> implements
    RoundSynchronization<MarlinResourcePool<T>> {

  private final int openValueThreshold;
  private final int batchSize;
  private boolean isCheckRequired;
  private final BigUIntFactory<T> factory;

  public MarlinRoundSynchronization(BigUIntFactory<T> factory) {
    this(factory, 100000, 128);
  }

  public MarlinRoundSynchronization(BigUIntFactory<T> factory, int openValueThreshold,
      int batchSize) {
    this.factory = factory;
    this.openValueThreshold = openValueThreshold;
    this.batchSize = batchSize;
    this.isCheckRequired = false;
  }

  private void doMacCheck(MarlinResourcePool<T> resourcePool, Network network) {
    NetworkBatchDecorator networkBatchDecorator =
        new NetworkBatchDecorator(
            resourcePool.getNoOfParties(),
            network);
    MarlinOpenedValueStore<T> openedValueStore = resourcePool.getOpenedValueStore();
    if (!openedValueStore.isEmpty()) {
      BasicNumericContext numericContext = new BasicNumericContext(
          resourcePool.getEffectiveBitLength(), resourcePool.getModulus(), resourcePool.getMyId(),
          resourcePool.getNoOfParties());
      BuilderFactoryNumeric builderFactory = new MarlinBuilder<>(factory, numericContext);
      ProtocolBuilderNumeric root = builderFactory.createSequential();
      new MarlinMacCheckComputation<>(resourcePool).buildComputation(root);
      ProtocolProducer macCheck = root.build();
      do {
        ProtocolCollectionList<MarlinResourcePool> protocolCollectionList =
            new ProtocolCollectionList<>(batchSize);
        macCheck.getNextProtocols(protocolCollectionList);
        new BatchedStrategy<MarlinResourcePool>()
            .processBatch(protocolCollectionList, resourcePool, networkBatchDecorator);
      } while (macCheck.hasNextProtocols());
    }
  }

  @Override
  public void finishedBatch(int gatesEvaluated, MarlinResourcePool<T> resourcePool,
      Network network) {
    if (isCheckRequired || resourcePool.getOpenedValueStore().size() > openValueThreshold) {
      doMacCheck(resourcePool, network);
      isCheckRequired = false;
    }
  }

  @Override
  public void finishedEval(MarlinResourcePool<T> resourcePool, Network network) {
    doMacCheck(resourcePool, network);
  }

  @Override
  public void beforeBatch(ProtocolCollection<MarlinResourcePool<T>> nativeProtocols,
      MarlinResourcePool<T> resourcePool, Network network) {
    for (NativeProtocol<?, ?> protocol : nativeProtocols) {
      if (protocol instanceof MarlinOutputProtocol) {
        isCheckRequired = true;
        break;
      }
    }
    if (isCheckRequired) {
      doMacCheck(resourcePool, network);
    }
  }

}
