package dan200.computercraft.core.apis;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.ThreeArgFunction;

import java.math.BigInteger;
import java.security.SecureRandom;
import dan200.computercraft.core.apis.RandomProvider;

public class BigIntegerValue extends LuaValue {
	private static final String NAME = "biginteger";

	private final BigInteger number;
	private LuaTable metatable;

	private BigIntegerValue(BigInteger number, LuaTable metatable) {
		this.number = number;
		this.metatable = metatable;
	}

	@Override
	public int type() {
		return TUSERDATA;
	}

	@Override
	public String typename() {
		return "userdata";
	}

	@Override
	public LuaValue getmetatable() {
		return metatable;
	}

	@Override
	public byte tobyte() {
		return number.byteValue();
	}

	@Override
	public double todouble() {
		return number.doubleValue();
	}

	@Override
	public float tofloat() {
		return number.floatValue();
	}

	@Override
	public int toint() {
		return number.intValue();
	}

	@Override
	public long tolong() {
		return number.longValue();
	}

	@Override
	public short toshort() {
		return number.shortValue();
	}

	@Override
	public LuaValue tonumber() {
		return valueOf(number.doubleValue());
	}

	@Override
	public double optdouble(double def) {
		return number.doubleValue();
	}

	@Override
	public int optint(int def) {
		return number.intValue();
	}

	@Override
	public LuaInteger optinteger(LuaInteger def) {
		return valueOf(number.intValue());
	}

	@Override
	public long optlong(long def) {
		return number.longValue();
	}

	@Override
	public LuaNumber optnumber(LuaNumber def) {
		return valueOf(number.doubleValue());
	}

	@Override
	public int checkint() {
		return number.intValue();
	}

	@Override
	public LuaInteger checkinteger() {
		return valueOf(number.intValue());
	}

	@Override
	public long checklong() {
		return number.longValue();
	}

	@Override
	public LuaNumber checknumber() {
		return valueOf(number.doubleValue());
	}

	@Override
	public LuaNumber checknumber(String msg) {
		return valueOf(number.doubleValue());
	}

	@Override
	public String checkjstring() {
		return number.toString();
	}

	@Override
	public LuaString checkstring() {
		return valueOf(number.toString());
	}

	@Override
	public boolean eq_b(LuaValue other) {
		if (this == other) {
			return true;
		} else if (type() != other.type()) {
			return false;
		} else {
			LuaValue tag = metatag(EQ);
			return !tag.isnil() && tag == other.metatag(EQ) && tag.call(this, other).toboolean();
		}
	}

	@Override
	public boolean equals(Object o) {
		return this == o || (o instanceof BigIntegerValue && number.equals(((BigIntegerValue) o).number));
	}

	@Override
	public int hashCode() {
		return number.hashCode();
	}

	@Override
	public LuaValue eq(LuaValue luaValue) {
		return eq_b(luaValue) ? TRUE : FALSE;
	}

	public static void setup(LuaValue env) {
		env.rawset(NAME, BigIntegerFunction.makeTable(env));
	}

	private static BigInteger getValue(LuaValue value) {
		if (value instanceof BigIntegerValue) {
			return ((BigIntegerValue) value).number;
		} else if (value.type() == TSTRING) {
			try {
				return new BigInteger(value.toString());
			} catch (NumberFormatException e) {
				throw new LuaError("bad argument: number expected, got " + value.typename());
			}
		} else {
			return BigInteger.valueOf(value.checklong());
		}
	}

	private static class BigIntegerFunction extends ThreeArgFunction {
		private static final String[] META_NAMES = new String[]{
			"unm", "add", "sub", "mul", "mod", "pow", "div", "idiv",
			"band", "bor", "bxor", "shl", "shr", "bnot",
			"eq", "lt", "le",
			"tostring", "tonumber",
		};

		private static final String[] MAIN_NAMES = new String[]{
			"new", "modinv", "gcd", "modpow", "abs", "min", "max",
			"isProbPrime", "nextProbPrime", "newProbPrime", "seed",
		};
		private final LuaTable metatable;
		private final RandomProvider random;

		private BigIntegerFunction(LuaTable metatable, RandomProvider random) {
			this.metatable = metatable;
			this.random = random;
		}

		@Override
		public LuaValue call(LuaValue left, LuaValue right, LuaValue third) {
			try {
				switch (opcode) {
					case 0: { // unm
						BigInteger leftB = getValue(left);
						return new BigIntegerValue(leftB.negate(), metatable);
					}
					case 1: { // add
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.add(rightNum), metatable);
					}
					case 2: { // sub
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.subtract(rightNum), metatable);
					}
					case 3: { // mul
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.multiply(rightNum), metatable);
					}
					case 4: { // mod
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.remainder(rightNum), metatable);
					}
					case 5: { // pow
						BigInteger leftNum = getValue(left);
						return new BigIntegerValue(leftNum.pow(right.checkint()), metatable);
					}
					case 6:
					case 7: { // div
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.divide(rightNum), metatable);
					}
					case 8: { // band
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.and(rightNum), metatable);
					}
					case 9: { // bor
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.or(rightNum), metatable);
					}
					case 10: { // bxor
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.xor(rightNum), metatable);
					}
					case 11: { // shl
						BigInteger leftNum = getValue(left);
						return new BigIntegerValue(leftNum.shiftLeft(right.checkint()), metatable);
					}
					case 12: { // shr
						BigInteger leftNum = getValue(left);
						return new BigIntegerValue(leftNum.shiftRight(right.checkint()), metatable);
					}
					case 13: { // bnot
						BigInteger leftNum = getValue(left);
						return new BigIntegerValue(leftNum.not(), metatable);
					}
					case 14: { // eq
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return leftNum.equals(rightNum) ? TRUE : FALSE;
					}
					case 15: { // lt
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return leftNum.compareTo(rightNum) < 0 ? TRUE : FALSE;
					}
					case 16: { // le
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return leftNum.compareTo(rightNum) <= 0 ? TRUE : FALSE;
					}
					case 17: { // tostring
						return valueOf(getValue(left).toString());
					}
					case 18: { // tonumber
						return valueOf(getValue(left).doubleValue());
					}
					case 19: { // new
						if (left instanceof BigIntegerValue) {
							return left;
						} else if (left.type() == TSTRING) {
							try {
								return new BigIntegerValue(new BigInteger(left.toString()), metatable);
							} catch (NumberFormatException e) {
								throw new LuaError("bad argument: number expected, got " + left.typename());
							}
						} else {
							return new BigIntegerValue(BigInteger.valueOf(left.checklong()), metatable);
						}
					}
					case 20: { // modinv
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.modInverse(rightNum), metatable);
					}
					case 21: { // gcd
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.gcd(rightNum), metatable);
					}
					case 22: { // modpow
						BigInteger leftNum = getValue(left), rightNum = getValue(right), thirdNum = getValue(third);
						return new BigIntegerValue(leftNum.modPow(rightNum, thirdNum), metatable);
					}
					case 23: { // abs
						BigInteger leftNum = getValue(left);
						return new BigIntegerValue(leftNum.abs(), metatable);
					}
					case 24: { // min TODO: Varargs version
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.min(rightNum), metatable);
					}
					case 25: { // max
						BigInteger leftNum = getValue(left), rightNum = getValue(right);
						return new BigIntegerValue(leftNum.max(rightNum), metatable);
					}
					case 26: { // isProbPrime
						BigInteger leftNum = getValue(left);
						if (leftNum.bitLength() > 2000) {
							throw new LuaError("prime is too large");
						}
						int rightProb = right.optint(100);
						return leftNum.isProbablePrime(rightProb) ? TRUE : FALSE;
					}
					case 27: { // nextProbPrime
						BigInteger leftNum = getValue(left);
						if (leftNum.bitLength() > 2000) {
							throw new LuaError("prime is too large");
						}
						return new BigIntegerValue(leftNum.nextProbablePrime(), metatable);
					}
					case 28: { // newProbPrime
						int length = left.checkint();
						if (length > 2000) {
							throw new LuaError("prime is too large");
						}
						SecureRandom seed;
						if (right.isnil()) {
							seed = random.get();
						} else {
							seed = RandomProvider.create();
							seed.setSeed(getValue(right).toByteArray());
						}
						return new BigIntegerValue(BigInteger.probablePrime(length, seed), metatable);
					}
					case 29: { // seed
						if (left.isnil()) {
							random.seed();
						} else {
							random.seed(getValue(left));
						}
						return NONE;
					}
					default:
						throw new LuaError("No such method " + opcode);
				}
			} catch (ArithmeticException e) {
				String message = e.getMessage() == null || e.getMessage().isEmpty() ? "bad arguments for " + name : e.getMessage();
				throw new LuaError(message);
			}
		}

		private static LuaTable makeTable(LuaValue env) {
			LuaTable meta = new LuaTable(0, META_NAMES.length + 2);
			LuaTable table = new LuaTable(0, META_NAMES.length + MAIN_NAMES.length);

			RandomProvider random = new RandomProvider();

			for (int i = 0; i < META_NAMES.length; i++) {
				BigIntegerFunction func = new BigIntegerFunction(meta, random);
				func.opcode = i;
				func.name = META_NAMES[i];
				func.env = env;
				table.rawset(META_NAMES[i], func);
				meta.rawset("__" + META_NAMES[i], func);
			}

			for (int i = 0; i < MAIN_NAMES.length; i++) {
				BigIntegerFunction func = new BigIntegerFunction(meta, random);
				func.opcode = i + META_NAMES.length;
				func.name = MAIN_NAMES[i];
				func.env = env;
				table.rawset(MAIN_NAMES[i], func);
			}

			meta.rawset("__index", table);
			meta.rawset("__type", valueOf(NAME));

			return table;
		}
	}
}

