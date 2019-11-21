package net.imagej.ops.morphology;

import java.util.List;

import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.morphology.Opening;
import net.imglib2.algorithm.neighborhood.Shape;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

import org.scijava.ops.OpField;
import org.scijava.ops.core.OpCollection;
import org.scijava.ops.function.Computers;
import org.scijava.ops.function.Computers;
import org.scijava.ops.function.Functions;
import org.scijava.ops.function.Functions;
import org.scijava.ops.function.Inplaces;
import org.scijava.ops.function.Inplaces;
import org.scijava.param.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.struct.ItemIO;

@Plugin(type = OpCollection.class)
public class Opens<T extends RealType<T> & Comparable<T>, R extends RealType<R>> {

	@OpField(names = "morphology.open", params = "x")
	@Parameter(key = "source")
	@Parameter(key = "strels")
	@Parameter(key = "numThreads")
	@Parameter(key = "result", itemIO = ItemIO.OUTPUT)
	public final Functions.Arity3<Img<R>, List<Shape>, Integer, Img<R>> openImgList = Opening::open;

	@OpField(names = "morphology.open", params = "x")
	@Parameter(key = "source")
	@Parameter(key = "strel")
	@Parameter(key = "numThreads")
	@Parameter(key = "result", itemIO = ItemIO.OUTPUT)
	public final Functions.Arity3<Img<R>, Shape, Integer, Img<R>> openImgSingle = Opening::open;

	@OpField(names = "morphology.open", params = "x")
	@Parameter(key = "source")
	@Parameter(key = "strels")
	@Parameter(key = "minValue")
	@Parameter(key = "maxValue")
	@Parameter(key = "numThreads")
	@Parameter(key = "result", itemIO = ItemIO.OUTPUT)
	public final Functions.Arity5<Img<T>, List<Shape>, T, T, Integer, Img<T>> openImgListMinMax = Opening::open;

	@OpField(names = "morphology.open", params = "x")
	@Parameter(key = "source")
	@Parameter(key = "strel")
	@Parameter(key = "minValue")
	@Parameter(key = "maxValue")
	@Parameter(key = "numThreads")
	@Parameter(key = "result", itemIO = ItemIO.OUTPUT)
	public final Functions.Arity5<Img<T>, Shape, T, T, Integer, Img<T>> openImgSingleMinMax = Opening::open;

	@OpField(names = "morphology.open", params = "x")
	@Parameter(key = "source")
	@Parameter(key = "strels")
	@Parameter(key = "numThreads")
	@Parameter(key = "target", itemIO = ItemIO.BOTH)
	public final Computers.Arity3<RandomAccessible<R>, List<Shape>, Integer, IterableInterval<R>> openImgListComputer = (in1, in2, in3, out) -> Opening.open(in1, out, in2, in3);

	@OpField(names = "morphology.open", params = "x")
	@Parameter(key = "source")
	@Parameter(key = "strels")
	@Parameter(key = "minVal")
	@Parameter(key = "maxVal")
	@Parameter(key = "numThreads")
	@Parameter(key = "target", itemIO = ItemIO.BOTH)
	public final Computers.Arity5<RandomAccessible<T>, List<Shape>, T, T, Integer, IterableInterval<T>> openImgListMinMaxComputer = (in1, in2, in3, in4, in5, out) -> Opening.open(in1, out, in2, in3, in4, in5);

	@OpField(names = "morphology.open", params = "x")
	@Parameter(key = "source")
	@Parameter(key = "strel")
	@Parameter(key = "numThreads")
	@Parameter(key = "target", itemIO = ItemIO.BOTH)
	public final Computers.Arity3<RandomAccessible<R>, Shape, Integer, IterableInterval<R>> openImgComputer = (in1, in2, in3, out) -> Opening.open(in1, out, in2, in3);

	@OpField(names = "morphology.open", params = "x")
	@Parameter(key = "source")
	@Parameter(key = "strel")
	@Parameter(key = "minVal")
	@Parameter(key = "maxVal")
	@Parameter(key = "numThreads")
	@Parameter(key = "target", itemIO = ItemIO.BOTH)
	public final Computers.Arity5<RandomAccessible<T>, Shape, T, T, Integer, IterableInterval<T>> openImgMinMaxComputer = (in1, in2, in3, in4, in5, out) -> Opening.open(in1, out, in2, in3, in4, in5);

	@OpField(names = "morphology.open", params = "x")
	@Parameter(key = "source", itemIO = ItemIO.BOTH)
	@Parameter(key = "interval")
	@Parameter(key = "strels")
	@Parameter(key = "numThreads")
	public final Inplaces.Arity4_1<RandomAccessibleInterval<R>, Interval, List<Shape>, Integer> openImgListInPlace = Opening::openInPlace;

	@OpField(names = "morphology.open", params = "x")
	@Parameter(key = "source", itemIO = ItemIO.BOTH)
	@Parameter(key = "interval")
	@Parameter(key = "strels")
	@Parameter(key = "minVal")
	@Parameter(key = "maxVal")
	@Parameter(key = "numThreads")
	public final Inplaces.Arity6_1<RandomAccessibleInterval<T>, Interval, List<Shape>, T, T, Integer> openImgListMinMaxInplace = Opening::openInPlace;

	@OpField(names = "morphology.open", params = "x")
	@Parameter(key = "source", itemIO = ItemIO.BOTH)
	@Parameter(key = "interval")
	@Parameter(key = "strel")
	@Parameter(key = "numThreads")
	public final Inplaces.Arity4_1<RandomAccessibleInterval<R>, Interval, Shape, Integer> openImgSingleInPlace = Opening::openInPlace;

	@OpField(names = "morphology.open", params = "x")
	@Parameter(key = "source", itemIO = ItemIO.BOTH)
	@Parameter(key = "interval")
	@Parameter(key = "strel")
	@Parameter(key = "minVal")
	@Parameter(key = "maxVal")
	@Parameter(key = "numThreads")
	public final Inplaces.Arity6_1<RandomAccessibleInterval<T>, Interval, Shape, T, T, Integer> openImgSingleMinMaxInplace = Opening::openInPlace;
}
