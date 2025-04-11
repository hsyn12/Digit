package com.tr.xyz.digit

/**
 * Defines a range.
 *
 * @property min minimum inclusive
 * @property max maximum exclusive
 */
interface Limited {
	val min: Long get() = 0
	val max: Long get() = Long.MAX_VALUE
}