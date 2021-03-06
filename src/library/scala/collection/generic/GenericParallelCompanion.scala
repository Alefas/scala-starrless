package scala.collection.generic


import scala.collection.parallel.Combiner
import scala.collection.parallel.ParallelIterable
import scala.collection.parallel.ParallelMap



/** A template class for companion objects of parallel collection classes.
 *  They should be mixed in together with `GenericCompanion` type.
 *  @tparam CC   the type constructor representing the collection class
 *  @since 2.8
 */
trait GenericParallelCompanion[+CC[X] <: ParallelIterable[X]] {
  /** The default builder for $Coll objects.
   */
  def newBuilder[A]: Combiner[A, CC[A]]
  
  /** The parallel builder for $Coll objects.
   */
  def newCombiner[A]: Combiner[A, CC[A]]
}

trait GenericParallelMapCompanion[+CC[P, Q] <: ParallelMap[P, Q]] {
  def newCombiner[P, Q]: Combiner[(P, Q), CC[P, Q]]
}


