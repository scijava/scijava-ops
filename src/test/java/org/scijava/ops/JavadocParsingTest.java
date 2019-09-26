package org.scijava.ops;

import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.github.therapi.runtimejavadoc.RetainJavadoc;
import com.github.therapi.runtimejavadoc.RuntimeJavadoc;

import org.junit.Test;
import org.scijava.ops.core.OpCollection;
import org.scijava.ops.core.computer.Computer;
import org.scijava.param.Mutable;
import org.scijava.plugin.Plugin;

@Plugin(type = OpCollection.class)
@RetainJavadoc
public class JavadocParsingTest {

	@Test
	public void parseOpFieldJavadoc() {
		ClassJavadoc clazzJavadoc = RuntimeJavadoc.getJavadoc(JavadocParsingTest.class);
		System.out.println(clazzJavadoc);
		assert(clazzJavadoc.getMethods().size() != 0);
	}

}

@RetainJavadoc
class TestOp implements Computer<Double, Double> {

	/**
	 * Adds one {@link Double} to another
	 * 
	 * @param in1
	 *            - the input to the method
	 * @param out
	 *            - the result of the method
	 */
	@Override
	public void compute(Double in1, @Mutable Double out) {
		out = in1;
	}

}
