package dk.alexandra.fresco.fixedpoint.basic;

import org.junit.Test;

import dk.alexandra.fresco.framework.sce.evaluator.EvaluationStrategy;
import dk.alexandra.fresco.suite.dummy.arithmetic.AbstractDummyArithmeticTest;


public class TestDummyArithmeticProtocolSuite extends AbstractDummyArithmeticTest {

  @Test
  public void test_Input_Sequential() throws Exception {
    runTest(new BasicFixedPointTests.TestInput<>(), EvaluationStrategy.SEQUENTIAL,
        2);
  }

  @Test
  public void test_Open_to_party_Sequential() throws Exception {
    runTest(new BasicFixedPointTests.TestOpenToParty<>(), EvaluationStrategy.SEQUENTIAL,
        2);
  }
  
  @Test
  public void test_Known() throws Exception {
    runTest(new BasicFixedPointTests.TestKnown<>(), EvaluationStrategy.SEQUENTIAL,
        2);
  }
  
  @Test
  public void test_AddKnown() throws Exception {
    runTest(new BasicFixedPointTests.TestAddKnown<>(), EvaluationStrategy.SEQUENTIAL,
        2);
  }
  
  @Test
  public void test_AddSecret() throws Exception {
    runTest(new BasicFixedPointTests.TestAdd<>(), EvaluationStrategy.SEQUENTIAL,
        2);
  }
  
  @Test
  public void test_SubtractSecret() throws Exception {
    runTest(new BasicFixedPointTests.TestSubtractSecret<>(), EvaluationStrategy.SEQUENTIAL,
        2);
  }
  
  @Test
  public void test_SubKnown() throws Exception {
    runTest(new BasicFixedPointTests.TestSubKnown<>(), EvaluationStrategy.SEQUENTIAL,
        2);
  }
  
  @Test
  public void test_SubKnown2() throws Exception {
    runTest(new BasicFixedPointTests.TestSubKnown2<>(), EvaluationStrategy.SEQUENTIAL,
        2);
  }

  @Test
  public void test_MultSecret() throws Exception {
    runTest(new BasicFixedPointTests.TestMult<>(), EvaluationStrategy.SEQUENTIAL,
        2);
  }
  
  @Test
  public void test_MultKnown() throws Exception {
    runTest(new BasicFixedPointTests.TestMultKnown<>(), EvaluationStrategy.SEQUENTIAL,
        2);
  }

  @Test
  public void test_Mults() throws Exception {
    runTest(new BasicFixedPointTests.TestMult<>(), EvaluationStrategy.SEQUENTIAL,
        2);
  }
  
  @Test
  public void test_DivisionSecretDivisor() throws Exception {
    runTest(new BasicFixedPointTests.TestDiv<>(), EvaluationStrategy.SEQUENTIAL,
        2);
  }
  
  @Test
  public void test_DivisionKnownDivisor() throws Exception {
    runTest(new BasicFixedPointTests.TestDivisionKnownDivisor<>(), EvaluationStrategy.SEQUENTIAL,
        2);
  }
  
  @Test
  public void test_RandomElement() throws Exception {
    runTest(new BasicFixedPointTests.TestRandom<>(), EvaluationStrategy.SEQUENTIAL,
        2);
  }
  
  @Test
  public void test_closeFixedMatrix() throws Exception {
    runTest(new LinearAlgebraTests.TestCloseFixedMatrix<>(), EvaluationStrategy.SEQUENTIAL, 2);
  }

  @Test
  public void test_closeAndOpenFixedMatrix() throws Exception {
    runTest(new LinearAlgebraTests.TestCloseAndOpenMatrix<>(), EvaluationStrategy.SEQUENTIAL, 2);
  }

  @Test
  public void test_matrixAddition() throws Exception {
    runTest(new LinearAlgebraTests.TestMatrixAddition<>(), EvaluationStrategy.SEQUENTIAL, 2);
  }
  
  @Test
  public void test_matrixMultiplication() throws Exception {
    runTest(new LinearAlgebraTests.TestMatrixMultiplication<>(), EvaluationStrategy.SEQUENTIAL, 2);
  }
  
  @Test
  public void test_matrixScale() throws Exception {
    runTest(new LinearAlgebraTests.TestMatrixScale<>(), EvaluationStrategy.SEQUENTIAL, 2);
  }

  @Test
  public void test_exp() throws Exception {
    runTest(new MathTests.TestExp<>(), EvaluationStrategy.SEQUENTIAL, 2);
  }
}
