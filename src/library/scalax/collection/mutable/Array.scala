/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2002-2009, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

// $Id: Array.scala 17000 2009-01-29 13:05:53Z odersky $


package scalax.collection.mutable

import scalax.collection.generic._
import compat.Platform.arraycopy

/** This object contains utility methods operating on arrays.
 *
 *  @author Martin Odersky
 *  @version 1.0
 */
object Array extends SequenceFactory[Array] {

  /** Copy one array to another.
   *  Equivalent to
   *    <code>System.arraycopy(src, srcPos, dest, destPos, length)</code>,
   *  except that this works also for polymorphic and boxed arrays.
   *
   *  @param src     ...
   *  @param srcPos  ...
   *  @param dest    ...
   *  @param destPos ...
   *  @param length  ...
   */
  def copy(src: AnyRef, srcPos: Int, dest: AnyRef, destPos: Int, length: Int) {
    src match {
      case xs: runtime.BoxedArray[_] =>
        xs.copyTo(srcPos, dest, destPos, length)
      case _ =>
        dest match {
          case xs: runtime.BoxedArray[_] =>
            xs.copyFrom(src, srcPos, destPos, length)
          case _ =>
            def fillDest[T](da: Array[T], sa: Int=>T) {
              var d = destPos
              for (s <- srcPos to srcPos+length-1) {
                da(d) = sa(s); d += 1
              }
            }
            if (dest.isInstanceOf[Array[Any]]) {
              def fill(sa: Int=>Any) = fillDest(dest.asInstanceOf[Array[Any]], sa)
              src match {
                case sa:Array[Int]     => fill(s=>Int.box(sa(s)))
/*!!!
                case sa:Array[Long]    => fill(s=>Long.box(sa(s)))
                case sa:Array[Char]    => fill(s=>Char.box(sa(s)))
                case sa:Array[Boolean] => fill(s=>Boolean.box(sa(s)))
                case sa:Array[Byte]    => fill(s=>Byte.box(sa(s)))
                case sa:Array[Short]   => fill(s=>Short.box(sa(s)))
                case sa:Array[Double]  => fill(s=>Double.box(sa(s)))
                case sa:Array[Float]   => fill(s=>Float.box(sa(s)))
*/ 
                case _ => arraycopy(src, srcPos, dest, destPos, length)
              }
            } else if (dest.isInstanceOf[Array[AnyVal]]) {
              def fill(sa: Int=>AnyVal) = fillDest(dest.asInstanceOf[Array[AnyVal]], sa)
              src match {
                case sa:Array[Int]     => fill(sa(_))
/*!!!
                case sa:Array[Long]    => fill(sa(_))
                case sa:Array[Char]    => fill(sa(_))
                case sa:Array[Boolean] => fill(sa(_))
                case sa:Array[Byte]    => fill(sa(_))
                case sa:Array[Short]   => fill(sa(_))
                case sa:Array[Double]  => fill(sa(_))
                case sa:Array[Float]   => fill(sa(_))
*/ 
                case _ => arraycopy(src, srcPos, dest, destPos, length)
              }
            } else
              arraycopy(src, srcPos, dest, destPos, length)
        }
    }
  }

  /** Concatenate all argument sequences into a single array.
   *
   *  @param xs the given argument sequences
   *  @return   the array created from the concatenated arguments
   */
  def concat[T](xs: Seq[T]*): Array[T] = {
    var len = 0
    for (x <- xs) len += x.length
    val result = new Array[T](len)
    var start = 0
    for (x <- xs) {
      copy(x.toArray, 0, result, start, x.length)
      start += x.length
    }
    result
  }

  /** Create an array with given elements.
   *
   *  @param xs the elements to put in the array
   *  @return the array containing elements xs.
   */
  def apply[A](xs: A*): Array[A] = {
    val array = new Array[A](xs.length)
    var i = 0
    for (x <- xs.elements) { array(i) = x; i += 1 }
    array
  }
/*!!! enable when arrays in Scala
  def apply(xs: Boolean*): Array[Boolean] = {
    val array = new Array[Boolean](xs.length)
    var i = 0
    for (x <- xs.elements) { array(i) = x; i += 1 }
    array
  }

  def apply(xs: Byte*): Array[Byte] = {
    val array = new Array[Byte](xs.length)
    var i = 0
    for (x <- xs.elements) { array(i) = x; i += 1 }
    array
  }

  def apply(xs: Short*): Array[Short] = {
    val array = new Array[Short](xs.length)
    var i = 0
    for (x <- xs.elements) { array(i) = x; i += 1 }
    array
  }

  def apply(xs: Char*): Array[Char] = {
    val array = new Array[Char](xs.length)
    var i = 0
    for (x <- xs.elements) { array(i) = x; i += 1 }
    array
  }

  def apply(xs: Int*): Array[Int] = {
    val array = new Array[Int](xs.length)
    var i = 0
    for (x <- xs.elements) { array(i) = x; i += 1 }
    array
  }

  def apply(xs: Long*): Array[Long] = {
    val array = new Array[Long](xs.length)
    var i = 0
    for (x <- xs.elements) { array(i) = x; i += 1 }
    array
  }

  def apply(xs: Float*): Array[Float] = {
    val array = new Array[Float](xs.length)
    var i = 0
    for (x <- xs.elements) { array(i) = x; i += 1 }
    array
  }

  def apply(xs: Double*): Array[Double] = {
    val array = new Array[Double](xs.length)
    var i = 0
    for (x <- xs.elements) { array(i) = x; i += 1 }
    array
  }

  def apply(xs: Unit*): Array[Unit] = {
    val array = new Array[Unit](xs.length)
    var i = 0
    for (x <- xs.elements) { array(i) = x; i += 1 }
    array
  }
*/
  /** Create an array containing several copies of an element.
   *
   *  @param n    the length of the resulting array
   *  @param elem the element composing the resulting array
   *  @return     an array composed of n elements all equal to elem
   *  @deprecated use `Array.fill` instead.
   */
  @deprecated def make[A](n: Int, elem: A): Array[A] = {
    val a = new Array[A](n)
    var i = 0
    while (i < n) {
      a(i) = elem
      i += 1
    }
    a
  }

  /** Create an array containing the values of a given function <code>f</code> 
   *  over given range <code>[0..n)</code>
   *  @deprecated use `Array.tabulate` instead.
   */
  @deprecated def fromFunction[A](f: Int => A)(n: Int): Array[A] = {
    val a = new Array[A](n)
    var i = 0
    while (i < n) {
      a(i) = f(i)
      i += 1
    }
    a
  }
  
  /** Create an array containing the values of a given function <code>f</code> 
   *  over given range <code>[0..n1, 0..n2)</code>
   *  @deprecated use `Array.tabulate` instead.
   */
  @deprecated def fromFunction[A](f: (Int, Int) => A)(n1: Int, n2: Int): Array[Array[A]] =
    fromFunction(i => fromFunction(f(i, _))(n2))(n1)
  
  /** Create an array containing the values of a given function <code>f</code> 
   *  over given range <code>[0..n1, 0..n2, 0..n3)</code>
   *  @deprecated use `Array.tabulate` instead.
   */
  @deprecated def fromFunction[A](f: (Int, Int, Int) => A)(n1: Int, n2: Int, n3: Int): Array[Array[Array[A]]] = 
    fromFunction(i => fromFunction(f(i, _, _))(n2, n3))(n1)

  /** Create an array containing the values of a given function <code>f</code> 
   *  over given range <code>[0..n1, 0..n2, 0..n3, 0..n4)</code>
   *  @deprecated use `Array.tabulate` instead.
   */
  @deprecated def fromFunction[A](f: (Int, Int, Int, Int) => A)(n1: Int, n2: Int, n3: Int, n4: Int): Array[Array[Array[Array[A]]]] = 
    fromFunction(i => fromFunction(f(i, _, _, _))(n2, n3, n4))(n1)

  /** Create an array containing the values of a given function <code>f</code> 
   *  over given range <code>[0..n1, 0..n2, 0..n3, 0..n4, 0..n5)</code>
   *  @deprecated use `Array.tabulate` instead.
   */
  @deprecated def fromFunction[A](f: (Int, Int, Int, Int, Int) => A)(n1: Int, n2: Int, n3: Int, n4: Int, n5: Int): Array[Array[Array[Array[Array[A]]]]] = 
    fromFunction(i => fromFunction(f(i, _, _, _, _))(n2, n3, n4, n5))(n1)

  /** Create array with given dimensions */
  def ofDim[A](n1: Int): Array[A] = 
    new Array[A](n1)
  def ofDim[A](n1: Int, n2: Int): Array[Array[A]] = 
    tabulate(n1)(_ => ofDim[A](n2))
  def ofDim[A](n1: Int, n2: Int, n3: Int): Array[Array[Array[A]]] = 
    tabulate(n1)(_ => ofDim[A](n2, n3))
  def ofDim[A](n1: Int, n2: Int, n3: Int, n4: Int): Array[Array[Array[Array[A]]]] = 
    tabulate(n1)(_ => ofDim[A](n2, n3, n4))
  def ofDim[A](n1: Int, n2: Int, n3: Int, n4: Int, n5: Int): Array[Array[Array[Array[Array[A]]]]] = 
    tabulate(n1)(_ => ofDim[A](n2, n3, n4, n5))
}

/** This class represents polymorphic arrays. <code>Array[T]</code> is Scala's representation
 *  for Java's <code>T[]</code>.
 *
 *  @author Martin Odersky
 *  @version 1.0
 */
final class Array[A](_length: Int) extends Vector[A] with MutableVectorTemplate[Array, A] {

   /** Multidimensional array creation
    *  @deprecated use Array.ofDim instead
    */
   @deprecated def this(dim1: Int, dim2: Int) = {
     this(dim1)
     throw new Error()
   }

   /** Multidimensional array creation
    *  @deprecated use Array.ofDim instead */
   @deprecated def this(dim1: Int, dim2: Int, dim3: Int) = {
     this(dim1)
     throw new Error()
   }

   /** Multidimensional array creation
    *  @deprecated use Array.ofDim instead */
   @deprecated def this(dim1: Int, dim2: Int, dim3: Int, dim4: Int) = {
     this(dim1)
     throw new Error()
   }

   /** Multidimensional array creation
    *  @deprecated use Array.ofDim instead */
   @deprecated def this(dim1: Int, dim2: Int, dim3: Int, dim4: Int, dim5: Int) = {
     this(dim1);
     throw new Error()
   }

   /** Multidimensional array creation
    *  @deprecated use Array.ofDim instead */
   @deprecated def this(dim1: Int, dim2: Int, dim3: Int, dim4: Int, dim5: Int, dim6: Int) = {
     this(dim1)
     throw new Error()
   }

   /** Multidimensional array creation
    *  @deprecated use Array.ofDim instead */
   @deprecated def this(dim1: Int, dim2: Int, dim3: Int, dim4: Int, dim5: Int, dim6: Int, dim7: Int) = {
     this(dim1)
     throw new Error()
   }

   /** Multidimensional array creation
    *  @deprecated use Array.ofDim instead */
   @deprecated def this(dim1: Int, dim2: Int, dim3: Int, dim4: Int, dim5: Int, dim6: Int, dim7: Int, dim8: Int) = {
     this(dim1)
     throw new Error()
   }

   /** Multidimensional array creation
    *  @deprecated use Array.ofDim instead */
   @deprecated def this(dim1: Int, dim2: Int, dim3: Int, dim4: Int, dim5: Int, dim6: Int, dim7: Int, dim8: Int, dim9: Int) = {
     this(dim1)
     throw new Error()
   }

  /** The length of the array */
  def length: Int = throw new Error()

  /** The element at given index. 
   *  <p>
   *    Indices start a <code>0</code>; <code>xs.apply(0)</code> is the first 
   *    element of array <code>xs</code>.
   *  </p>
   *  <p>
   *    Note the indexing syntax <code>xs(i)</code> is a shorthand for
   *    <code>xs.apply(i)</code>.
   *  </p>
   *
   *  @param i   the index
   *  @throws ArrayIndexOutOfBoundsException if <code>i < 0</code> or
   *          <code>length <= i</code>
   */
  def apply(i: Int): A = throw new Error()

  /* Create a new array builder */
  def newBuilder[B]: Builder[Array, B] = throw new Error()

  /** <p>
   *    Update the element at given index. 
   *  </p>
   *  <p>
   *    Indices start a <code>0</code>; <code>xs.apply(0)</code> is the first 
   *    element of array <code>xs</code>.
   *  </p>
   *  <p>
   *    Note the indexing syntax <code>xs(i) = x</code> is a shorthand 
   *    for <code>xs.update(i, x)</code>.
   *  </p>
   *
   *  @param i   the index
   *  @param x   the value to be written at index <code>i</code>
   *  @throws ArrayIndexOutOfBoundsException if <code>i < 0</code> or
   *          <code>length <= i</code>
   */
  override def update(i: Int, x: A) { throw new Error() }

  /**
   *  @return a deep string representation of this array.
   */
  def deepToString(): String = throw new Error()

  /** <p>
   *    Returns a string representation of this array object. The resulting string
   *    begins with the string <code>start</code> and is finished by the string
   *    <code>end</code>. Inside, the string representations of elements (w.r.t.
   *    the method <code>deepToString()</code>) are separated by the string
   *    <code>sep</code>. For example:
   *  </p>
   *  <p>
   *    <code>Array(Array(1, 2), Array(3)).deepMkString("[", "; ", "]") = "[[1; 2]; [3]]"</code>
   *  </p>
   *
   *  @param start starting string.
   *  @param sep separator string.
   *  @param end ending string.
   *  @return a string representation of this array object.
   */
  def deepMkString(start: String, sep: String, end: String): String =
    throw new Error()

  /** Returns a string representation of this array object. The string
   *  representations of elements (w.r.t. the method <code>deepToString()</code>)
   *  are separated by the string <code>sep</code>.
   *
   *  @param sep separator string.
   *  @return a string representation of this array object.
   */
  def deepMkString(sep: String): String = throw new Error()

  /** <p>
   *    Returns <code>true</code> if the two specified arrays are
   *    <em>deeply equal</em> to one another.
   *  </p>
   *  <p>
   *    Two array references are considered deeply equal if both are null,
   *    or if they refer to arrays that contain the same number of elements
   *    and all corresponding pairs of elements in the two arrays are deeply
   *    equal.
   *  </p>
   *  <p>
   *    See also method <code>deepEquals</code> in the Java class
   *    <a href="http://java.sun.com/javase/6/docs/api/java/util/Arrays.html"
   *    target="_top">java.utils.Arrays</a>
   *  </p>
   *
   *  @param that the second
   *  @return     <code>true</code> iff both arrays are deeply equal.
   */
  def deepEquals(that: Any): Boolean = throw new Error()

}