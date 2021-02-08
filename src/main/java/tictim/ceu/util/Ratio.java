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
	public static final Ratio EQ = new Ratio(1, 1);
	/**
	 * 4:1 ratio used in default setting.
	 */
	public static final Ratio DEFAULT = new Ratio(4, 1);

	public static Ratio of(long in, long out){
		if(in<=0) throw new IllegalArgumentException("in");
		if(out<=0) throw new IllegalArgumentException("out");
		if(in==out) return EQ;
		long gcd = LongMath.gcd(in, out);
		if(gcd!=1){
			in /= gcd;
			out /= gcd;
		}
		if(in==4&&out==1) return DEFAULT;
		if(in==1&&out==4) return DEFAULT.reverse();
		return new Ratio(in, out);
	}

	@Nullable public static Ratio deserialize(NBTTagCompound nbt){
		long in = nbt.getLong("inRatio");
		long out = nbt.getLong("outRatio");
		return in<=0||out<=0 ? null : of(in, out);
	}

	private static final Pattern PATTERN = Pattern.compile("\\s*(\\d+)\\s*:\\s*(\\d+)\\s*");

	/**
	 * Parses string into ratio.
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
	 * Calculates and returns maximum amount of energy able to be converted without loss.
	 * @param providedInput Currently provided input. 0 or greater.
	 * @param maxOutput     Maximum output. 0 or greater.<Br>
	 *                      Note that the actual maximum return value could be slightly lesser than {@code maxOutput} value
	 *                      in order to satisfy {@code (function output) modulo (ratio out) = 0}.
	 * @return Converted energy, in range between 0 and {@code maxOutput}.
	 * @see Conversion#convert(Ratio, long, long) 
	 */
	public long convert(long providedInput, long maxOutput){
		return Conversion.convert(this, providedInput, maxOutput);
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
		nbt.setLong("inRatio", in);
		nbt.setLong("outRatio", out);
	}

	@Override public boolean equals(Object o){
		if(this==o) return true;
		if(o==null||getClass()!=o.getClass()) return false;
		Ratio ratio = (Ratio)o;
		return in==ratio.in&&
				out==ratio.out;
	}
	@Override public int hashCode(){
		return Objects.hash(in, out);
	}
	@Override public String toString(){
		return in+":"+out;
	}
}
