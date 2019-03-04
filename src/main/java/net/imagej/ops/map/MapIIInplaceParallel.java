/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2014 - 2018 ImageJ developers.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imagej.ops.map;

import net.imagej.ops.thread.chunker.Chunk;
import net.imagej.ops.thread.chunker.CursorBasedChunk;
import net.imglib2.IterableInterval;

import org.scijava.Priority;
import org.scijava.ops.OpDependency;
import org.scijava.ops.core.Op;
import org.scijava.ops.core.inplace.BiInplaceFirst;
import org.scijava.ops.core.inplace.Inplace;
import org.scijava.ops.core.inplace.Inplace5First;
import org.scijava.param.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.struct.ItemIO;

/**
 * Parallelized {@link MapInplace} over an {@link IterableInterval}.
 * 
 * @author Christian Dietz (University of Konstanz)
 * @param <A> element type of inplace arguments
 */
@Plugin(type = Op.class, name = "map.parallel", priority = Priority.LOW + 5)
@Parameter(key = "mapped", type = ItemIO.BOTH)
@Parameter(key = "op")
public class MapIIInplaceParallel<A> implements
	BiInplaceFirst<IterableInterval<A>, Inplace<A>> 
{
	@OpDependency(name = "thread.chunker")
	private BiInplaceFirst<Chunk, Long> chunkerOp;
	
	@OpDependency(name = "map")
	private Inplace5First<IterableInterval<A>, Inplace<A>, Long, Long, Long> mapperOp;

	@Override
	public void mutate(final IterableInterval<A> arg, final Inplace<A> op) {
		chunkerOp.mutate(new CursorBasedChunk() {

			@Override
			public void execute(final long startIndex, final long stepSize,
				final long numSteps)
			{
				mapperOp.mutate(arg, op, startIndex, stepSize, numSteps);
			}
		}, arg.size());
	}

}
