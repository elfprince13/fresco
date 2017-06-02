/*******************************************************************************
 * Copyright (c) 2015, 2016 FRESCO (http://github.com/aicis/fresco).
 *
 * This file is part of the FRESCO project.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * FRESCO uses SCAPI - http://crypto.biu.ac.il/SCAPI, Crypto++, Miracl, NTL,
 * and Bouncy Castle. Please see these projects for any further licensing issues.
 *******************************************************************************/
package dk.alexandra.fresco.lib.field.integer;

import dk.alexandra.fresco.framework.ProtocolFactory;
import dk.alexandra.fresco.framework.value.OIntFactory;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.framework.value.SIntFactory;
import dk.alexandra.fresco.lib.field.integer.generic.AddProtocolFactory;
import dk.alexandra.fresco.lib.field.integer.generic.IOIntProtocolFactory;
import dk.alexandra.fresco.lib.math.integer.NumericBitFactory;
import java.math.BigInteger;

/**
 * A factory that produces protocols that operate on elements in a finite field.
 *
 */
public interface BasicNumericFactory extends SIntFactory, OIntFactory,
		AddProtocolFactory, SubtractProtocolFactory, MultProtocolFactory,
		ProtocolFactory, IOIntProtocolFactory, NumericBitFactory, RandomFieldElementFactory {

	/**
	 * Returns the maximum number of bits a number in the field can contain.
	 * 
	 * @return
	 */
  int getMaxBitLength();

	/**
	 * Returns the largest possible value containable in the field that we can
	 * still multiply with and get no overflow.
	 * 
	 * @return
	 */
  SInt getSqrtOfMaxValue();

	/**
	 * Returns the modulus used in the underlying arithmetic protocol suite.
	 * 
	 * @return The modulus used.
	 */
  BigInteger getModulus();
}
