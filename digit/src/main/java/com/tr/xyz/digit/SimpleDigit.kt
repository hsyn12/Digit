package com.tr.xyz.digit

import java.util.Objects


open class SimpleDigit(digitValue: Long = 0, final override val min: Long = Digit.MIN, final override val max: Long = Digit.MAX)
	: Digit {
	
	constructor(digitValue: Long = 0, range: LongRange = Digit.MIN..Digit.MAX) : this(digitValue, range.first, range.last)
	
	override val range: Long = (max - min) + 1
	
	final override var cycleCount: Long = 0
		set(value) {
			field = value
			if (value != 0L) leftDigit?.onCycle(value)
		}
	
	
	final override var value: Long = 0
		set(value) {
			cycleCount = 0L
			if (value in min..max) {
				field = value
				return
			}
			
			// less
			if (value < min) {
				val interval = min - value;
				if (interval <= range) {
					field = (max - interval) + 1
					cycleCount = -1
				}
				else {
					field = ((max + 1) - range % interval)
					cycleCount = -(interval / range)
				}
				// or greater
			}
			else {
				val interval = value - max
				if (interval <= range) {
					field = (min + interval) - 1
					cycleCount = 1
				}
				else {
					field = (min + interval % range) - 1
					cycleCount = value / range
				}
			}
		}
	
	override var leftDigit: Digit? = null
	
	init {
		require(max >= min) { "Max value must be equal or greater than min value, but [max : $max, min : $min]" }
		this.value = digitValue
	}
	
	override fun onCycle(cycle: Long) {
		value += cycle
	}
	
	override fun toString(): String = "Digit(value=$value, min=$min, max=$max, range=$range, cycle=$cycleCount)"
	
	/**
	 * Checks if this digit is equal to the given digit.
	 *
	 * @param other the digit to compare.
	 * @return the result of the comparison.
	 */
	override fun equals(other: Any?): Boolean = other is SimpleDigit && value == other.value
	
	override fun hashCode(): Int = Objects.hash(value)
}

