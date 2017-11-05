package dan200.computercraft.core.apis;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

/**
 * Reimplementation of the bitop library
 *
 * http://bitop.luajit.org/api.html
 */
public class BitOpLib {
	private static final String[] names = new String[]{
		"tobit", "bnot", "bswap",
		"tohex", "lshift", "rshift", "arshift", "rol", "ror",
		"band", "bor", "bxor",
	};

	private static class BitOneArg extends OneArgFunction {
		@Override
		public LuaValue call(LuaValue luaValue) {
			switch (opcode) {
				case 0: // tobit
					return luaValue.checkinteger();
				case 1: // bnot
					return valueOf(~luaValue.checkint());
				case 2: // bswap
				{
					int i = luaValue.checkint();
					return valueOf((i & 0xff) << 24 | (i & 0xff00) << 8 | (i & 0xff0000) >> 8 | (i >> 24) & 0xff);
				}
				default:
					return NIL;
			}
		}

		private static void bind(LuaTable table, LuaTable env) {
			for (int i = 0; i < 3; i++) {
				BitOneArg func = new BitOneArg();
				func.opcode = i;
				func.name = names[i];
				func.env = env;
				table.rawset(names[i], func);
			}
		}
	}

	private static final byte[] lowerHexDigits = new byte[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	private static final byte[] upperHexDigits = new byte[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

	private static class BitTwoArg extends TwoArgFunction {
		@Override
		public LuaValue call(LuaValue bitValue, LuaValue nValue) {
			switch (opcode) {
				case 0: // tohex
				{
					int n = nValue.optint(8);
					int bit = bitValue.checkint();

					byte[] hexes = lowerHexDigits;
					if (n < 0) {
						n = -n;
						hexes = upperHexDigits;
					}
					if (n > 8) n = 8;

					byte[] out = new byte[n];
					for (int i = n - 1; i >= 0; i--) {
						out[i] = hexes[bit & 15];
						bit >>= 4;
					}

					return valueOf(out);
				}
				case 1: // lshift
					return valueOf(bitValue.checkint() << (nValue.checkint() & 31));
				case 2: // rshift
					return valueOf(bitValue.checkint() >>> (nValue.checkint() & 31));
				case 3: // arshift
					return valueOf(bitValue.checkint() >> (nValue.checkint() & 31));
				case 4: // rol
				{
					int b = bitValue.checkint();
					int n = nValue.checkint() & 31;
					return valueOf((b << n) | (b >>> (32 - n)));
				}
				case 5: // ror
				{
					int b = bitValue.checkint();
					int n = nValue.checkint() & 31;
					return valueOf((b << (32 - n)) | (b >>> n));
				}
				default:
					return NIL;
			}
		}

		private static void bind(LuaTable table, LuaTable env) {
			for (int i = 3; i < 9; i++) {
				BitTwoArg func = new BitTwoArg();
				func.opcode = i - 3;
				func.name = names[i];
				func.env = env;
				table.rawset(names[i], func);
			}
		}
	}

	private static class BitVarArg extends VarArgFunction {
		@Override
		public Varargs invoke(Varargs varargs) {
			int value = varargs.arg1().checkint(), len = varargs.narg();
			if (len == 1) return varargs.arg1();

			switch (opcode) {
				case 0: {
					for (int i = 2; i <= len; i++) {
						value &= varargs.arg(i).checkint();
					}
					break;
				}
				case 1: {
					for (int i = 2; i <= len; i++) {
						value |= varargs.arg(i).checkint();
					}
					break;
				}
				case 2: {
					for (int i = 2; i <= len; i++) {
						value ^= varargs.arg(i).checkint();
					}
					break;
				}
			}

			return valueOf(value);
		}

		private static void bind(LuaTable table, LuaTable env) {
			for (int i = 9; i < 12; i++) {
				BitVarArg func = new BitVarArg();
				func.opcode = i - 9;
				func.name = names[i];
				func.env = env;
				table.rawset(names[i], func);
			}
		}
	}

	public static void setup(LuaTable env) {
		LuaTable table = new LuaTable(0, names.length + 3);
		BitOneArg.bind(table, env);
		BitTwoArg.bind(table, env);
		BitVarArg.bind(table, env);

		table.rawset("blshift", table.rawget("lshift"));
		table.rawset("brshift", table.rawget("arshift"));
		table.rawset("blogic_rshift", table.rawget("rshift"));

		env.rawset("bitop", table);
	}
}
