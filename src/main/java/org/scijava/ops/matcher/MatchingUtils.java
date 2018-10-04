/*
 * #%L
 * SciJava Common shared library for SciJava software.
 * %%
 * Copyright (C) 2009 - 2017 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, Max Planck
 * Institute of Molecular Cell Biology and Genetics, University of
 * Konstanz, and KNIME GmbH.
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

package org.scijava.ops.matcher;

import com.google.common.base.Objects;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.scijava.types.Nil;
import org.scijava.util.Types;
import org.scijava.util.Types.TypeVarInfo;

public final class MatchingUtils {

	private MatchingUtils() {
		// prevent instantiation of utility class
	}

	/**
	 * Checks for raw assignability. TODO This method is not yet fully
	 * implemented. The correct behavior should be as follows. Suppose we have a
	 * generic typed method like:
	 * 
	 * <pre>
	 *public static &lt;N&gt; List&lt;N&gt; foo(N in) {
	 *	...
	 *}
	 * </pre>
	 * 
	 * This method should discern if the following assignments would be legal,
	 * possibly using predetermined {@link TypeVariable} assignments:
	 * 
	 * <pre>
	 *List&lt;Integer&gt; listOfInts = foo(new Integer(0)) //legal
	 *List&lt;Number&gt; listOfNumbers = foo(new Integer(0)) //legal
	 *List&lt;? extends Number&gt; listOfBoundedWildcards = foo(new Integer(0)) //legal
	 * </pre>
	 * 
	 * The corresponding calls to this method would be:
	 * 
	 * <pre>
	 * Nil&lt;List&lt;N&gt;&gt; nilN = new Nil&lt;List&lt;N&gt;&gt;(){}
	 * Nil&lt;List&lt;Integer&gt;&gt; nilInteger = new Nil&lt;List&lt;Integer&gt;&gt;(){}
	 * Nil&lt;List&lt;Number&gt;&gt; nilNumber = new Nil&lt;List&lt;Number&gt;&gt;(){}
	 * Nil&lt;List&lt;? extends Number&gt;&gt; nilWildcardNumber = new Nil&lt;List&lt;? extends Number&gt;&gt;(){}
	 * 
	 * checkGenericOutputsAssignability(nilN.getType(), nilInteger.getType, ...)
	 * checkGenericOutputsAssignability(nilN.getType(), nilNumber.getType, ...)
	 * checkGenericOutputsAssignability(nilN.getType(), nilWildcardNumber.getType, ...)
	 * </pre>
	 * 
	 * Using a map where N was already bound to Integer (N -> Integer.class).
	 * This method is useful for the following scenario: During ops matching, we
	 * first check if the arguments (inputs) of the requested op are applicable
	 * to the arguments of an op candidate. During this process, possible type
	 * variables may be inferred. The can then be used with this method to find
	 * out if the outputs of the op candidate would be assignable to the output
	 * of the requested op.
	 * 
	 * @param froms
	 * @param tos
	 * @param typeBounds
	 * @return
	 */
	public static int checkGenericOutputsAssignability(Type[] froms, Type[] tos,
			HashMap<TypeVariable<?>, TypeVarInfo> typeBounds) {
		for (int i = 0; i < froms.length; i++) {
			Type from = froms[i];
			Type to = tos[i];

			if (!Types.isAssignable(Types.raw(from), Types.raw(to)))
				return i;
		}
		return -1;
	}

	/**
	 * Checks whether it would be legal to assign the {@link Type} source to the
	 * specified {@link ParameterizedType} destination (which could possibly be
	 * a supertype of the source type). Thereby, possible {@link TypeVariable}s
	 * contained in the parameters of the source are tried to be inferred in the
	 * sense of empty angle brackets when a new object is created:
	 * 
	 * <pre>
	 * List&lt;Integer&gt; listOfInts = new ArrayList&lt;&gt;();
	 * </pre>
	 * 
	 * Hence, the types to put between the brackets are tried to be determined.
	 * Inference will be done by simple matching of an encountered
	 * {@link TypeVariable} in the source to the corresponding type in the
	 * parameters of the destination. If an {@link TypeVariable} is encountered
	 * more than once, the corresponding type in the destination needs to
	 * perfectly match. Else, false will be returned.</br>
	 * </br>
	 * Examples:
	 * <ul>
	 * If we have a class:
	 * <li>
	 * 
	 * <pre>
	 * class NumberSupplier&lt;M extends Number&gt; implements Supplier&lt;M&gt;
	 * </li>
	 * </ul>
	 * <ul>
	 * The following check will return true:
	 * <li>
	 * 
	 * <pre>
	 * checkGenericAssignability(NumberSupplier.class, new
	 * Nil&lt;Supplier&lt;Double&gt;&gt;() {}.getType())</li>
	 * </ul>
	 * </ul>
	 * <ul>
	 * Which will check if the following assignment would be legal:
	 * <li>
	 * 
	 * <pre>
	 * Supplier&lt;Double&gt; list = new NumberSupplier&lt;&gt;()</li>
	 * </ul>
	 * </ul>
	 * <ul>
	 * Here, the parameter {@code <M extends Number>} can be inferred to be of
	 * type {@code Double} from the type {@code Supplier<Double>}
	 * </ul>
	 * <ul>
	 * Consequently the following will return false:
	 * <li>
	 * 
	 * <pre>
	 * checkGenericAssignability(NumberSupplier.class, new
	 * Nil&lt;Supplier&lt;String&gt;&gt;() {}.getType())</li>
	 * </ul>
	 * <ul>
	 * {@code <M extends Number>} can't be inferred, as type {@code String} is
	 * not within the bounds of {@code M}.
	 * </ul>
	 * <ul>
	 * Furthermore, the following will return false for:
	 * {@code class NumberFunc<M extends Number> implements Function<M, M>}:
	 * <li>
	 * 
	 * <pre>
	 * checkGenericAssignability(NumberSupplier.class, new
	 * Nil&lt;Function&lt;Double, Integer&gt;&gt;() {}.getType())</li>
	 * </ul>
	 * <ul>
	 * {@code <M extends Number>} can't be inferred, as types {@code Double} and
	 * {@code Integer} are ambiguous for {@code M}.
	 * </ul>
	 * 
	 * @param src
	 *            the type for which assignment should be checked from
	 * @param dest
	 *            the parameterized type for which assignment should be checked
	 *            to
	 * @return whether and assignment of source to destination would be a legal
	 *         java statement
	 */
	public static boolean checkGenericAssignability(Type src, ParameterizedType dest) {
		// check raw assignability
		if (!Types.isAssignable(Types.raw(src), Types.raw(dest)))
			return false;

		Type[] destTypes;
		Type[] srcTypes;

		if (src instanceof Class || src instanceof ParameterizedType) {
			destTypes = dest.getActualTypeArguments();
			// get type arguments of raw src for common (possible supertype)
			// dest
			srcTypes = getParams(Types.raw(src), Types.raw(dest));
		} else {
			return Types.isAssignable(src, dest);
		}

		return checkGenericAssignability(srcTypes, destTypes, dest);
	}

	private static boolean checkGenericAssignability(Type[] srcTypes, Type[] destTypes, Type dest) {
		// if the number of type arguments does not match, the types can't be
		// assignable
		if (srcTypes.length != destTypes.length) {
			return false;
		}

		Type[] mappedSrcTypes = null;
		try {
			Map<TypeVariable<?>, Type> typeAssigns = new HashMap<TypeVariable<?>, Type>();
			// Try to infer type variables contained in the type arguments of
			// sry
			inferTypeVariables(srcTypes, destTypes, typeAssigns);
			// Map the vars to the inferred types
			mappedSrcTypes = mapVarToTypes(srcTypes, typeAssigns);
		} catch (TypeInferenceException e) {
			// types can't be inferred
			return false;
		}

		// Build a new parameterized type from inferred types and check
		// assignability
		Class<?> matchingRawType = Types.raw(dest);
		Type inferredSrcType = Types.parameterize(matchingRawType, mappedSrcTypes);
		if (!Types.isAssignable(inferredSrcType, dest)) {
			return false;
		}
		return true;
	}

	/**
	 * Exception indicating that type vars could not be inferred.
	 */
	private static class TypeInferenceException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7147530827546663700L;
	}

	/**
	 * Map type vars in specified type list to types using the specified map. In
	 * doing so, type vars mapping to other type vars will not be followed but
	 * just repalced.
	 * 
	 * @param typesToMap
	 * @param typeAssigns
	 * @return
	 */
	private static Type[] mapVarToTypes(Type[] typesToMap, Map<TypeVariable<?>, Type> typeAssigns) {
		return Arrays.stream(typesToMap).map(type -> Types.unrollVariables(typeAssigns, type, false))
				.toArray(Type[]::new);
	}

	private static <M> boolean containsNull(M[] arr) {
		return !Arrays.stream(arr).noneMatch(m -> m == null);
	}

	/**
	 * Tries to infer type vars contained in types from corresponding types from
	 * inferFrom, putting them into the specified map.
	 * 
	 * @param types
	 * @param inferFrom
	 * @param typeAssigns
	 * @throws TypeInferenceException
	 */
	private static void inferTypeVariables(Type[] types, Type[] inferFrom, Map<TypeVariable<?>, Type> typeAssigns)
			throws TypeInferenceException {
		if (typeAssigns == null)
			throw new IllegalArgumentException();
		// Check all pairs of types
		for (int i = 0; i < types.length; i++) {
			if (types[i] instanceof TypeVariable) {
				TypeVariable<?> varType = (TypeVariable<?>) types[i];
				Type from = inferFrom[i];

				// If current type var is absent put it to the map. Otherwise,
				// we already encountered that var.
				// Hence, we require them to be exactly the same.
				Type current = typeAssigns.putIfAbsent(varType, from);
				if (current != null) {
					if (!Objects.equal(from, current)) {
						throw new TypeInferenceException();
					}
				}

				// Bounds could also contain type vars, hence possibly go into
				// recursion
				for (Type bound : varType.getBounds()) {
					if (bound instanceof TypeVariable && typeAssigns.get((TypeVariable<?>) bound) != null) {
						// If the bound of the current var (let's call it A) to
						// infer is also a var (let's call it B):
						// If we already encountered B, we check if the current
						// type to infer from is assignable to
						// the already inferred type for B. In this case we do
						// not require equality as one var is
						// bounded by another and it is not the same. E.g.
						// assume we want to infer the types of vars:
						// - - - A extends Number, B extends A
						// From types:
						// - - - Number, Double
						// First A is bound to Number, next B to Double. Then we
						// check the bounds for B. We encounter A,
						// for which we already inferred Number. Hence, it
						// suffices to check whether Double can be assigned
						// to Number, it does not have to be equal as it is just
						// a transitive bound for B.
						Type typeAssignForBound = typeAssigns.get((TypeVariable<?>) bound);
						if (!Types.isAssignable(from, typeAssignForBound)) {
							throw new TypeInferenceException();
						}
					} else {
						// Else go into recursion as we encountered a new var.
						inferTypeVariables(new Type[] { bound }, new Type[] { from }, typeAssigns);
					}
				}
			} else if (types[i] instanceof ParameterizedType) {
				// Recursively follow parameterized types
				if (!(inferFrom[i] instanceof ParameterizedType)) {
					throw new TypeInferenceException();
				}
				ParameterizedType paramType = (ParameterizedType) types[i];
				ParameterizedType paramInferFrom = (ParameterizedType) inferFrom[i];
				inferTypeVariables(paramType.getActualTypeArguments(), paramInferFrom.getActualTypeArguments(),
						typeAssigns);

			} else if (types[i] instanceof WildcardType) {
				// TODO Do we need to specifically handle Wildcards? Or are they
				// sufficiently handled by Types.satisfies below?
			}
		}
		// Check if the inferred types satisfy their bounds
		if (!Types.typesSatisfyVariables(typeAssigns)) {
			throw new TypeInferenceException();
		}
	}

	/**
	 * Finds the type parameters of the most specific super type of the
	 * specified subType whose erasure is the specified superErasure. Hence,
	 * will return the type parameters of superErasure possibly narrowed down by
	 * subType. If superErasure is not raw or not a super type of subType, an
	 * empty array will be returned.
	 * 
	 * @param subType
	 *            the type to narrow down type parameters
	 * @param superErasure
	 *            the erasure of an super type of subType to get the parameters
	 *            from
	 * @return type parameters of superErasure possibly narrowed down by
	 *         subType, or empty type array if no exists or superErasure is not
	 *         a super type of subtype
	 */
	public static Type[] getParams(Class<?> subType, Class<?> superErasure) {
		Type pt = Types.parameterizeRaw(subType);
		Type superType = Types.getExactSuperType(pt, superErasure);
		if (superType != null && superType instanceof ParameterizedType) {
			return ((ParameterizedType) superType).getActualTypeArguments();
		}
		return new Type[0];
	}

	/**
	 * Gets the "useful" class information carries on the given object, which
	 * depends on the actual type of the object.
	 */
	public static Class<?> getClass(final Object obj) {
		if (obj == null)
			return null;
		if (obj instanceof Nil)
			return getClass(((Nil<?>) obj).getType());
		if (obj instanceof Class)
			return (Class<?>) obj;
		if (obj instanceof ParameterizedType)
			return (Class<?>) ((ParameterizedType) obj).getRawType();
		return obj.getClass();
	}

	/**
	 * Finds the levels of casting between <code>origin</code> and
	 * <code>dest</code>. Returns 0 if dest and origin are the same. Returns -1
	 * if dest is not assignable from origin.
	 */
	public static int findCastLevels(final Class<?> dest, final Class<?> origin) {
		if (dest.equals(origin))
			return 0;

		int level = 1;
		Class<?> currType = origin;
		// BFS if dest is an interface
		if (dest.isInterface()) {
			final HashSet<String> seen = new HashSet<>();
			final ArrayList<Type> currIfaces = new ArrayList<>(Arrays.asList(currType.getGenericInterfaces()));
			do {
				final ArrayList<Type> nextIfaces = new ArrayList<>();
				for (final Type iface : currIfaces) {
					if (seen.contains(iface.getTypeName()))
						continue;

					final Class<?> cls = getClass(iface);
					if (cls.equals(dest))
						return level;
					seen.add(iface.getTypeName());
					nextIfaces.addAll(Arrays.asList(cls.getGenericInterfaces()));
				}
				currIfaces.clear();
				currIfaces.addAll(nextIfaces);
				if (currType.getSuperclass() != null) {
					currType = currType.getSuperclass();
					currIfaces.addAll(Arrays.asList(currType.getGenericInterfaces()));
				}
				level++;
			} while (!currIfaces.isEmpty() || currType.getSuperclass() != null);
		}
		// otherwise dest is a class, so search the list of ancestors
		else {
			while (currType.getSuperclass() != null) {
				currType = currType.getSuperclass();
				if (currType.equals(dest))
					return level;
				level++;
			}
		}
		return -1;
	}
}
