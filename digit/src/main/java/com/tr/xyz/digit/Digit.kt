package com.tr.xyz.digit

import com.tr.xyz.digit.Digit.Companion.MAX
import com.tr.xyz.digit.Digit.Companion.MIN
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Defines a digit that has a range like all other built-in numerical types. But `Digit` is mutable
 * and the range is definable by the user. This is the main concept of this interface. And with
 * this, a digit can be defined like this:
 * ```
 * val digitZeroToOne = Digit.of(min = 0, max = 1, value = 0L)
 * ```
 *
 * All arguments are optional. If so, the default values are used.
 *
 * ```
 * val defaultDigit = Digit.of()
 * ```
 *
 * This definition is equivalent to:
 * ```
 * val defaultDigit = Digit.of(min = MIN, max = MAX, value = min)
 * ```
 *
 * [MIN] and [MAX] are the minimum and maximum values of the [Digit]. In this case, the digit value
 * is set to the [MIN] value.
 *
 * Look at this:
 * ```
 * val digitZeroToOne = Digit.of(min = 0, max = 1, value = 0L)
 * val digitOne = digitZeroToOne + 1
 * // All true
 * Assertions.assertEquals(1, digitOne.value)
 * Assertions.assertEquals(0, digitOne.cycleCount)
 * Assertions.assertEquals(0, digitOne.min)
 * Assertions.assertEquals(1, digitOne.max)
 * Assertions.assertEquals(2, digitOne.range)
 * ```
 *
 * Same definition maybe written like this:
 * ```
 * val digitZeroToOne = (0..1L).toDigit()
 * ```
 *
 * `0..1L` is a [LongRange].
 *
 * Or like this:
 * ```
 * val digitZeroToOne = Digit.of(0, 1)
 * ```
 *
 * And also can be using a delegate like this:
 * ```
 * var digitZeroToOne: Long by Digit.delegate(0, 1)
 * digitZeroToOne = 1
 * ```
 *
 * But in this case, you cannot see [cycleCount] and [range] and the [min] and [max] properties. For
 * example, if you set [value] to `2`, the digit is cycled to the [min] side and [cycleCount] is set
 * to `1`. But you cannot see it with delegate.
 *
 * ```
 * var digitZeroToOne: Long by Digit.delegate(0, 1)
 * digitZeroToOne = 2 // cycled to 0
 * Assertions.assertEquals(0, digitZeroToOne) // true
 * ```
 *
 * The delegate provides simple access and change of the [value] like a [Long] variable.
 *
 * ```
 * var digitZeroToOne: Long by Digit.delegate(0, 1)
 * digitZeroToOne++
 * Assertions.assertEquals(1, digitZeroToOne)
 * digitZeroToOne++
 * Assertions.assertEquals(0, digitZeroToOne)
 * digitZeroToOne++
 * Assertions.assertEquals(1, digitZeroToOne)
 * ```
 *
 * In such situations, you need to define the variable with `var`.
 *
 * Actually, `Digit` is more than that.
 *
 * ```
 * val minute = Digit.of(0, 59, value = 59)
 * val hour = Digit.of(0, 23)
 *
 * // Look at this
 * minute.leftDigit = hour
 * minute += 1
 *
 * Assertions.assertEquals(0, minute.value)
 * Assertions.assertEquals(1, hour.value)
 * ```
 *
 * Did you see that? You can capture the cycle with the [cycleCount] property and sent the value
 * to the left digit. All `Digit` has a [leftDigit] property, [cycleCount]s are sent to the left
 * digit when a cycle is occurred. There is no such property in built-in types. And no mechanism to
 * capture the cycle count. This is an event and its name is `overflow` or `underflow`. But if you
 * catch it, its name is `cycle`.
 *
 * `Digit` also defines some operator functions.
 *
 * ```
 * val digit1 = Digit.of(value = 10)
 * val digit2 = Digit.of(value = 10)
 *
 * Assertions.assertTrue(digit1 == digit2)
 * Assertions.assertEquals(Digit.of(value = 20), digit1 + digit2)
 * Assertions.assertEquals(Digit.of(value = 20), digit1 + 10)
 * ```
 *
 * Operator `plus` is defined both for `Digit` and `Long` and it returns a new `Digit` with the
 * value of the sum. But as I said, `Digit` is mutable. And it defines also `plusAssign` operator
 * (and other arithmetic operators).
 *
 * ```
 * digit1 += 10
 * Assertions.assertEquals(Digit.of(value = 20), digit1)
 * ```
 *
 * Also, operator `inc` and `dec` are defined. But they are not used in some other operator
 * combinations because of the `Kotlin` ambiguity.
 *
 * [KotlinAmbiguity](https://stackoverflow.com/questions/44558663/overloading-and-operators-for-number-classes)
 *
 * According to this, all operators cannot be used all together. For example, this is not work:
 * ```
 * var digit3 = Digit.of(value = 10)
 * digit3 += 10 // Ambiguity between assign operator candidates
 * digit3++
 * ```
 *
 * If you define it with `val`:
 * ```
 * val digit3 = Digit.of(value = 10)
 * digit3 += 10
 * digit3++ // val cannot be reassigned.
 * ```
 *
 * This is understandable because both `plus` and `plusAssign` have the same signature. So `Kotlin`
 * cannot decide which one to use. Occasionally, you should use `val` to avoid ambiguity.
 *
 * But this does not mean you cannot use `inc` and `dec`.
 *
 * ```
 * val digit3 = Digit.of(value = 10)
 * digit3 += 10
 * digit3.inc()
 * ```
 *
 * You cal always use them with function names.
 *
 * In conclusion, a `Digit` can be used a normal [Long] variable (but less limited range [MIN] and
 * [MAX]) or abnormal digit variable. It is up to you.
 */
interface Digit {
	
	/**
	 * Value of the [Digit].
	 */
	var value: Long
	
	/**
	 * Cycle count indicates how many times the [value] has been cycled. If it is positive, this
	 * means the value is cycled from the [max] side to the [min] side. If it is negative, this means
	 * the value is cycled from the [min] side to the [max] side.
	 */
	val cycleCount: Long
	
	/**
	 * Range of [min] and [max]. Indicates how many numbers are in the range.
	 */
	val range: Long
	
	/**
	 * Minimum value of the [Digit]. The [value] cannot be less than this value. If so, the [value]
	 * is cycled to the [max] side.
	 */
	val min: Long
	
	/**
	 * Maximum value of the [Digit]. The [value] cannot be greater than this value. If so, the
	 * [value] is cycled to the [min] side.
	 */
	val max: Long
	
	/**
	 * Left [Digit] is a bound [Digit] of this [Digit] that is informed when the [cycleCount] is set
	 * to other than `0`. All numbers that are overflow or underflow impress to the left number.
	 *
	 * ```
	 *
	 * val year = Digit.of(value = 1981)
	 * var month = Digit.of(value = 12, min = 1, max = 12)
	 * month.leftDigit = year
	 *
	 * month +=1
	 *
	 * Assertions.assertTrue(year.value == 1982L)
	 * Assertions.assertTrue(month.value == 1L)
	 * Assertions.assertTrue(month.cycle == 1L)
	 *
	 * month -= 1
	 *
	 * Assertions.assertTrue(year.value == 1981L)
	 * Assertions.assertTrue(month.value == 12L)
	 * Assertions.assertTrue(month.cycle == -1L)
	 * ```
	 */
	var leftDigit: Digit?
	
	/**
	 * Called when [cycle] is set.
	 *
	 * @param cycle cycle count. This number can be negative.
	 */
	fun onCycle(cycle: Long)
	
	/**
	 * Increments the digit value by one.
	 *
	 * @return this [Digit]
	 */
	operator fun inc(): Digit {
		this.value++
		return this
	}
	
	/**
	 * Decrements the digit value by one.
	 *
	 * @return this [Digit]
	 */
	operator fun dec(): Digit {
		value--
		return this
	}
	
	/**
	 * Returns a new [Digit] with the added value.
	 *
	 * @param value the value to add.
	 * @return new [Digit] with the added value.
	 */
	operator fun plus(value: Long): Digit = SimpleDigit(this.value + value, this.min, this.max)
	
	/**
	 * Returns a new [Digit] with the added value.
	 *
	 * @param value the value to add.
	 * @return new [Digit] with the added value.
	 */
	operator fun plus(value: Digit): Digit =
		SimpleDigit(this.value + value.value, this.min, this.max)
	
	/**
	 * Adds the given value to the digit value.
	 *
	 * @param digit the value to add.
	 * @return new [Digit] with the added value.
	 */
	operator fun plusAssign(digit: Digit) {
		this.value += digit.value
	}
	
	/**
	 * Adds the given value to the digit value.
	 *
	 * @param value the value to add.
	 * @return new [Digit] with the added value.
	 */
	operator fun plusAssign(value: Long) {
		this.value += value
	}
	
	/**
	 * Subtracts the given value from the digit value.
	 *
	 * @param value the value to subtract.
	 * @return new [Digit] with the subtracted value.
	 */
	operator fun minus(value: Long): Digit = SimpleDigit(this.value - value, this.min, this.max)
	
	/**
	 * Subtracts the given value from the digit value.
	 *
	 * @param digit the value to subtract.
	 * @return new [Digit] with the subtracted value.
	 */
	operator fun minus(value: Digit): Digit =
		SimpleDigit(this.value - value.value, this.min, this.max)
	
	/**
	 * Subtracts the given value from the digit value.
	 *
	 * @param digit the value to subtract.
	 * @return new [Digit] with the subtracted value.
	 */
	operator fun minusAssign(digit: Digit) {
		this.value -= digit.value
	}
	
	/**
	 * Subtracts the given value from the digit value.
	 *
	 * @param value the value to subtract.
	 * @return new [Digit] with the subtracted value.
	 */
	operator fun minusAssign(value: Long) {
		this.value -= value
	}
	
	/**
	 * Multiplies the digit value with the given value.
	 *
	 * @param value the value to multiply.
	 * @return new [Digit] with the multiplied value.
	 */
	operator fun times(value: Long): Digit = SimpleDigit(this.value * value, this.min, this.max)
	
	/**
	 * Multiplies the digit value with the given value.
	 *
	 * @param value the value to multiply.
	 * @return new [Digit] with the multiplied value.
	 */
	operator fun times(value: Digit): Digit =
		SimpleDigit(this.value * value.value, this.min, this.max)
	
	/**
	 * Multiplies the digit value with the given value.
	 *
	 * @param value the value to multiply.
	 * @return new [Digit] with the multiplied value.
	 */
	operator fun timesAssign(digit: Digit) {
		this.value *= digit.value
	}
	
	/**
	 * Multiplies the digit value with the given value.
	 *
	 * @param value the value to multiply.
	 * @return new [Digit] with the multiplied value.
	 */
	operator fun timesAssign(value: Long) {
		this.value *= value
	}
	
	/**
	 * Divides the digit value with the given value.
	 *
	 * @param value the value to divide.
	 * @return new [Digit] with the divided value.
	 */
	operator fun div(value: Long): Digit = SimpleDigit(this.value / value, this.min, this.max)
	
	/**
	 * Divides the digit value with the given value.
	 *
	 * @param value the value to divide.
	 * @return new [Digit] with the divided value.
	 */
	operator fun div(value: Digit): Digit =
		SimpleDigit(this.value / value.value, this.min, this.max)
	
	/**
	 * Divides the digit value with the given value.
	 *
	 * @param digit the value to divide.
	 * @return new [Digit] with the divided value.
	 */
	operator fun divAssign(digit: Digit) {
		this.value /= digit.value
	}
	
	/**
	 * Divides the digit value with the given value.
	 *
	 * @param value the value to divide.
	 * @return new [Digit] with the divided value.
	 */
	operator fun divAssign(value: Long) {
		this.value /= value
	}
	
	operator fun compareTo(other: Digit): Int = value.compareTo(other.value)
	
	operator fun compareTo(other: Long): Int = value.compareTo(other)
	
	/**
	 * Delegates a [Digit] to a property as a [Long].
	 *
	 * ```
	 * var digit : Long by Digit.delegate(1)
	 * digit = 3
	 * ```
	 *
	 * @param digit the initial value.
	 * @param min the minimum value.
	 * @param max the maximum value.
	 * @constructor Creates a new [DigitDelegate].
	 */
	class DigitDelegate<T>(digit: Long, min: Long = MIN, max: Long = Digit.MAX) : ReadWriteProperty<T?, Long> {
		private val _digit = SimpleDigit(digit, min, max)
		
		override fun getValue(thisRef: T?, property: KProperty<*>): Long = _digit.value
		
		override fun setValue(thisRef: T?, property: KProperty<*>, value: Long) {
			_digit.value = value
		}
	}
	
	companion object {
		/**
		 * The maximum value of the [Digit]
		 */
		const val MIN = (Long.MIN_VALUE + 1) / 2
		
		/**
		 * The minimum value of the [Digit]
		 */
		const val MAX = (Long.MAX_VALUE - 1) / 2
		
		/**
		 * Creates a new [Digit] with the given value.
		 *
		 * @param min the minimum value.
		 * @param max the maximum value.
		 * @param value the initial value.
		 * @return the new [Digit].
		 */
		fun of(min: Long = MIN, max: Long = MAX, value: Long = min): Digit =
			SimpleDigit(value, min, max)
		fun of(value: Long, range: LongRange): Digit = SimpleDigit(value, range)
		
		/**
		 * Creates a new [Digit] with the given range.
		 *
		 * @param range the range of the [Digit].
		 * @param digit the initial value.
		 * @return the new [Digit].
		 */
		fun of(range: LongRange = MIN..Digit.MAX, digit: Long = range.first): Digit =
			SimpleDigit(digit, range.first, range.last)
		
		/**
		 * Delegates a [Digit] to a property as a [Long].
		 *
		 * ```
		 * var digit : Long by Digit.delegate(1)
		 * digit = 3
		 * ```
		 *
		 * @param min the minimum value.
		 * @param max the maximum value.
		 * @param value the initial value.
		 * @constructor Creates a new [DigitDelegate].
		 */
		fun <T> delegate(min: Long = MIN, max: Long = MAX, value: Long = MIN) = DigitDelegate<T>(value, min, max)
	}
}

/**
 * Converts the given [Long] to a [Digit].
 *
 * @param min the minimum value.
 * @param max the maximum value.
 * @return the new [Digit].
 */
fun Long.toDigit(min: Long = MIN, max: Long = MAX): Digit = SimpleDigit(this, min, max)

/**
 * Converts the given [LongRange] to a [Digit].
 *
 * @param range the range of the [Digit].
 * @return the new [Digit].
 */
fun Long.toDigit(range: LongRange = MIN..MAX): Digit = SimpleDigit(this, range.first, range.last)

/**
 * Converts the given [LongRange] to a [Digit].
 *
 * @param min the minimum value.
 * @param max the maximum value.
 * @return the new [Digit].
 */
fun LongRange.toDigit(min: Long = first, max: Long = last): Digit = SimpleDigit(min, min, max)
