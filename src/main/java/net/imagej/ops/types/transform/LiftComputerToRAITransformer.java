package net.imagej.ops.types.transform;

import net.imagej.ops.types.transform.util.Maps;
import net.imglib2.RandomAccessibleInterval;

import org.scijava.ops.OpService;
import org.scijava.ops.function.Computers;
import org.scijava.ops.matcher.OpRef;
import org.scijava.ops.transform.OpMapper;
import org.scijava.ops.transform.OpRefTransformUtils;
import org.scijava.ops.transform.OpTransformer;
import org.scijava.plugin.Plugin;

/**
 * @author David Kolb
 */
@Plugin(type = OpTransformer.class)
public class LiftComputerToRAITransformer<I, O> implements
	OpMapper<Computers.Arity1<I, O>, Computers.Arity1<RandomAccessibleInterval<I>, RandomAccessibleInterval<O>>>
{

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class<Computers.Arity1<I, O>> srcClass() {
		return (Class) Computer.class;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class<Computers.Arity1<RandomAccessibleInterval<I>, RandomAccessibleInterval<O>>> targetClass() {
		return (Class) Computer.class;
	}

	@Override
	public Computers.Arity1<RandomAccessibleInterval<I>, RandomAccessibleInterval<O>> transformTypesafe(final OpService opService, final Computers.Arity1<I, O> src,
		final OpRef targetRef)
	{
		return Maps.Computers.RAIs.liftBoth(src);
	}

	@Override
	public OpRef getRefTransformingTo(final OpRef targetRef) {
		return OpRefTransformUtils.unliftTransform(targetRef, Computer.class, RandomAccessibleInterval.class, new Integer[] {0, 1},
			new Integer[] { 0, 1 }, new Integer[] { 0 });
	}
}
