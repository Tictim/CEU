package tictim.ceu.util;

import com.google.common.math.LongMath;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Ratio{
	/**
	 * One-to-one ratio.
	 */
	public static final Ratio ONE_TO_ONE = new Ratio(1, 1);
	/**
	 * 4:1 ratio used in default settings.
	 */
	public static final Ratio FOUR_TO_ONE = new Ratio(4, 1);

	public static Ratio of(long in, long out){
		if(in<=0) throw new IllegalArgumentException("in");
		if(out<=0) throw new IllegalArgumentException("out");
		if(in==out) return ONE_TO_ONE;
		long gcd = LongMath.gcd(in, out);
		if(gcd!=1){
			in /= gcd;
			out /= gcd;
		}
		if(in==4&&out==1) return FOUR_TO_ONE;
		if(in==1&&out==4) return FOUR_TO_ONE.reverse();
		return new Ratio(in, out);
	}

	@Nullable public static Ratio deserialize(NBTTagCompound nbt){
		long in = nbt.getLong("I");
		long out = nbt.getLong("O");
		return in<=0||out<=0 ? null : of(in, out);
	}

	private static final Pattern PATTERN = Pattern.compile("\\s*(\\d+)\\s*:\\s*(\\d+)\\s*");

	/**
	 * Parses string into ratio.
	 *
	 * @param ratio String to parse
	 * @return Ratio parsed from string, or {@code null}, if parsing fails
	 */
	@Nullable public static Ratio tryParse(String ratio){
		Matcher m = PATTERN.matcher(ratio);
		if(m.matches()){
			String left = m.group(1);
			String right = m.group(2);
			try{
				return of(Long.parseLong(left), Long.parseLong(right));
			}catch(NumberFormatException ex){
				return null;
			}
		}else return null;
	}

	public final long in;
	public final long out;

	private Ratio(long in, long out){
		this.in = in;
		this.out = out;
	}

	public boolean isEquivalent(){
		return in==out;
	}

	public int convertToInt(long providedInput){
		return convertToInt(providedInput, Integer.MAX_VALUE);
	}
	public int convertToInt(long providedInput, int outputThreshold){
		return (int)convert(providedInput, outputThreshold);
	}

	public long convertToLong(long providedInput){
		return convert(providedInput, Long.MAX_VALUE);
	}

	/**
	 * Calculates maximum amount of units able to be converted without loss.
	 *
	 * @param providedInput Currently provided input. 0 or greater.
	 * @param maxOutput     Hard cap for result. 0 or greater.<Br>
	 *                      Note that the actual maximum result possible could be slightly lesser than {@code maxOutput} value
	 *                      in order to satisfy {@code <RESULT> modulo <RATIO OUT> == 0}.
	 * @return Input converted by specified ratio, in range between 0 and {@code maxOutput}.
	 */
	public long convert(long providedInput, long maxOutput){
		if(providedInput==0) return 0;
		if(isEquivalent()) return Math.min(providedInput, maxOutput);

		long maxOutput2 = maxOutput-maxOutput%out;  // Prevent loss caused by output threshold
		long provided2 = providedInput/in;

		if(provided2>=maxOutput2){
			// No need for checking, since the value only goes up afterwards
			return maxOutput2;
		}

		// Do actual work
		return Math.min(maxOutput2, LongMath.saturatedMultiply(provided2, out));
	}


	@Nullable private Ratio reverse;

	public Ratio reverse(){
		if(isEquivalent()) return this;
		if(reverse==null){
			reverse = new Ratio(out, in);
			reverse.reverse = this;
		}
		return reverse;
	}

	public void serialize(NBTTagCompound nbt){
		nbt.setLong("I", in);
		nbt.setLong("O", out);
	}

	@Override public boolean equals(Object o){
		if(this==o) return true;
		if(o==null||getClass()!=o.getClass()) return false;
		Ratio ratio = (Ratio)o;
		return in==ratio.in&&out==ratio.out;
	}
	@Override public int hashCode(){
		return Objects.hash(in, out);
	}
	@Override public String toString(){
		return in+":"+out;
	}
}
