/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2003-2010, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

package scala.concurrent

/** A class to provide safe concurrent access to a mutable cell.
 *  All methods are synchronized.
 *
 *  @author  Martin Odersky, Stepan Koltsov
 *  @version 1.0, 10/03/2003
 */
class SyncVar[A] {
  private var isDefined: Boolean = false
  private var value: A = _
  private var exception: Option[Throwable] = None

  def get = synchronized {
    while (!isDefined) wait()
    if (exception.isEmpty) value
    else throw exception.get
  }

  def get(timeout: Long): Option[A] = synchronized {
    if (!isDefined) {
      try wait(timeout)
      catch { case _: InterruptedException => () }
    }
    if (exception.isDefined) throw exception.get
    else if (isDefined) Some(value)
    else None
  }

  def take() = synchronized {
    try get
    finally unset()
  }

  def set(x: A) = synchronized {
    value = x
    isDefined = true
    exception = None
    notifyAll()
  }

  def put(x: A) = synchronized {
    while (isDefined) wait()
    set(x)
  }
  
  def isSet: Boolean = synchronized {
    isDefined
  }

  def unset(): Unit = synchronized {
    isDefined = false
    notifyAll()
  }
}

