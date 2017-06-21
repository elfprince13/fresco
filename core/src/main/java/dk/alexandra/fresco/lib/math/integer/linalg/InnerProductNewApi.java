package dk.alexandra.fresco.lib.math.integer.linalg;

import dk.alexandra.fresco.framework.BuilderFactoryNumeric;
import dk.alexandra.fresco.framework.Computation;
import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.builder.ProtocolBuilder;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.helper.SimpleProtocolProducer;
import java.util.ArrayList;
import java.util.List;

public class InnerProductNewApi extends SimpleProtocolProducer implements Computation<SInt> {

  private BuilderFactoryNumeric<SInt> bnf;
  private SInt[] a;
  private SInt[] b;
  private Computation<SInt> c;

  InnerProductNewApi(BuilderFactoryNumeric<SInt> bnf, SInt[] a, SInt[] b) {
    super();
    this.a = a;
    this.b = b;
    this.bnf = bnf;
  }

  @Override
  protected ProtocolProducer initializeProtocolProducer() {
    // Root sequential scope ... makes sense   
    ProtocolBuilder<SInt> pb = ProtocolBuilder.createRoot(bnf, seq -> {
      // Parallel scope for multiplication ... makes sense
      Computation<List<Computation<SInt>>> products =
          seq.createParallelSubFactoryReturning(par -> {
            List<Computation<SInt>> temp = new ArrayList<>();
            for (int i = 0; i < a.length; i++) {
              temp.add(par.numeric().mult(a[i], b[i]));
            }
            return () -> temp;
          });
      // A sub scope is needed - otherwise we will build the add protocol without
      // having populated the list of values to be added - SumSIntList would have done the trick
      // neatly
      c = seq.createSequentialSubFactoryReturning(subSeq -> {
        // Not sure how to do this correctly using the bnf1.get(0, bnf1.getSInt()) Computation?
        // PFF - neither am I - hence the old API
        Computation<SInt> c = subSeq.getSIntFactory().getSInt(0);
        List<Computation<SInt>> addents = products.out();
        for (Computation<SInt> aTemp : addents) {
          // Not sure how I would do this using Computations? The AddList seems overkill.
          // PFF - no it is not...
          c = subSeq.numeric().add(c, aTemp);
        }
        return c;
      });
    });
    return pb.build();
  }

  @Override
  public SInt out() {
    return c.out();
  }
}
