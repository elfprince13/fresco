/*******************************************************************************
 * Copyright (c) 2016 FRESCO (http://github.com/aicis/fresco).
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
package dk.alexandra.fresco.suite.ninja.prepro;

import dk.alexandra.fresco.framework.network.SCENetwork;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.value.Value;
import dk.alexandra.fresco.lib.field.bool.XorProtocol;
import dk.alexandra.fresco.suite.ninja.NinjaProtocolSuite;
import dk.alexandra.fresco.suite.ninja.protocols.NinjaProtocol;
import dk.alexandra.fresco.suite.ninja.storage.NinjaStorage;
import dk.alexandra.fresco.suite.ninja.storage.PrecomputedNinja;

public class DummyPreproXORProtocol extends NinjaProtocol implements XorProtocol{

	private PreproNinjaSBool inLeft, inRight, out;
	
	public DummyPreproXORProtocol(int id, PreproNinjaSBool inLeft, PreproNinjaSBool inRight, PreproNinjaSBool out) {
		super();	
		this.id = id;
		this.inLeft = inLeft;
		this.inRight = inRight;
		this.out = out;
	}
	
	public DummyPreproXORProtocol() {
		super();
	}

	@Override
	public Value[] getInputValues() {
		return new Value[] {};
	}

	@Override
	public Value[] getOutputValues() {
		return new Value[] {};
	}

	@Override
	public EvaluationStatus evaluate(int round, ResourcePool resourcePool, SCENetwork network) {
		NinjaStorage storage1 = NinjaProtocolSuite.getInstance(1).getStorage();
		NinjaStorage storage2 = NinjaProtocolSuite.getInstance(2).getStorage();
		
		PrecomputedNinja ninja1 = new PrecomputedNinja(new byte[] {0, 1, 1, 0});
		PrecomputedNinja ninja2 = new PrecomputedNinja(new byte[] {0, 0, 0, 0});
		storage1.storeNinja(id, ninja1);
		storage2.storeNinja(id, ninja2);
		return EvaluationStatus.IS_DONE;
	}

}
