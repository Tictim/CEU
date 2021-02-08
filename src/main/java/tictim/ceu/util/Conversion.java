package tictim.ceu.util;

import com.google.common.math.LongMath;

import java.math.BigInteger;
import java.util.Objects;

public final class Conversion{
	private Conversion(){}

	public static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
	public static final BigInteger MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);

	/**
	 * Calculates and returns maximum amount of energy able to be converted without loss.
	 *
	 * @param ratio         input:output ratio to be used during conversion
	 * @param providedInput Currently provided input. 0 or greater.
	 * @param maxOutput     Maximum output. 0 or greater.<Br>
	 *                      Note that the actual maximum return value could be slightly lesser than {@code maxOutput} value
	 *                      in order to satisfy {@code (function output) modulo (ratio out) = 0}.
	 * @return Converted energy, in range between 0 and {@code maxOutput}.
	 */
	public static long convert(Ratio ratio, long providedInput, long maxOutput){
		Objects.requireNonNull(ratio);
		if(providedInput==0) return 0;
		if(ratio.isEquivalent()) return Math.min(providedInput, maxOutput);

		long maxOutput2 = maxOutput-maxOutput%ratio.out;  // Prevent loss caused by output threshold
		long provided2 = providedInput/ratio.in;

		if(provided2>=maxOutput2){
			// No need for checking, since the value only goes up afterwards
			return maxOutput2;
		}

		// Do actual work
		return Math.min(maxOutput2, LongMath.saturatedMultiply(provided2, ratio.out));
	}
}
