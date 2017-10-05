package dk.alexandra.fresco.demo;

import dk.alexandra.fresco.demo.cli.CmdLineUtil;
import dk.alexandra.fresco.demo.helpers.ResourcePoolHelper;
import dk.alexandra.fresco.framework.builder.numeric.ProtocolBuilderNumeric;
import dk.alexandra.fresco.framework.configuration.NetworkConfiguration;
import dk.alexandra.fresco.framework.sce.SecureComputationEngine;
import dk.alexandra.fresco.framework.sce.SecureComputationEngineImpl;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.suite.ProtocolSuite;
import java.io.IOException;
import java.math.BigInteger;

public class InputSumExample {

  public static <ResourcePoolT extends ResourcePool> void runApplication(
      SecureComputationEngine<ResourcePoolT, ProtocolBuilderNumeric> sce,
      ResourcePoolT resourcePool) throws IOException {
    InputApplication inputApp;

    int myId = resourcePool.getMyId();
    int[] inputs = new int[] {1, 2, 3, 7, 8, 12, 15, 17};
    if (myId == 1) {
      // I input
      inputApp = new InputApplication(inputs);
    } else {
      // I do not input
      inputApp = new InputApplication(inputs.length);
    }

    SumAndOutputApplication app = new SumAndOutputApplication(inputApp);

    BigInteger result = sce.runApplication(app, resourcePool);

    int sum = 0;
    for (int i : inputs) {
      sum += i;
    }
    System.out.println("Expected result: " + sum + ", Result was: " + result);
  }

  public static <ResourcePoolT extends ResourcePool> void main(String[] args) throws IOException {
    CmdLineUtil<ResourcePoolT, ProtocolBuilderNumeric> util = new CmdLineUtil<>();
    NetworkConfiguration networkConfiguration;

    util.parse(args);
    networkConfiguration = util.getNetworkConfiguration();

    ProtocolSuite<ResourcePoolT, ProtocolBuilderNumeric> psConf =
        (ProtocolSuite<ResourcePoolT, ProtocolBuilderNumeric>) util.getProtocolSuite();

    SecureComputationEngine<ResourcePoolT, ProtocolBuilderNumeric> sce =
        new SecureComputationEngineImpl<ResourcePoolT, ProtocolBuilderNumeric>(psConf,
            util.getEvaluator());

    ResourcePoolT resourcePool = ResourcePoolHelper.createResourcePool(psConf,
        util.getNetworkStrategy(), networkConfiguration);
    runApplication(sce, resourcePool);
    resourcePool.getNetwork().close();
  }

}
