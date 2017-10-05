package dk.alexandra.fresco.suite.dummy.bool;

import dk.alexandra.fresco.framework.DRes;
import dk.alexandra.fresco.framework.network.SCENetwork;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.value.SBool;

/**
 * Implements logical NOT for the Dummy Boolean protocol suite, where all operations are done in the
 * clear.
 *
 */
public class DummyBooleanNotProtocol extends DummyBooleanNativeProtocol<SBool> {

  private DRes<SBool> operand;
  private DummyBooleanSBool out;

  /**
   * Constructs a protocol to NOT the result of a computation.
   * 
   * @param operand the operand
   */
  public DummyBooleanNotProtocol(DRes<SBool> operand) {
    super();
    this.operand = operand;
    this.out = null;
  }

  @Override
  public EvaluationStatus evaluate(int round, ResourcePool resourcePool, SCENetwork network) {

    out = new DummyBooleanSBool();
    this.out.setValue(!((DummyBooleanSBool) operand.out()).getValue());
    return EvaluationStatus.IS_DONE;
  }

  @Override
  public SBool out() {
    return out;
  }
}
