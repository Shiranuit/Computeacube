package dan200.computercraft.core.apis;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.runtime.ScriptFunction;

public class JS implements ILuaAPI{
	
	public ScriptEngine engine;
	public IAPIEnvironment env;
	public JS(IAPIEnvironment env) {
		this.engine = new ScriptEngineManager().getEngineByName("nashorn");
		this.env = env;
	}
	@Override
	public String[] getMethodNames() {
		// TODO Auto-generated method stub
		return new String[] {"eval","get","set"};
	}
	
	
	
	public LuaValue JSToLua(Object o) {
		if (o != null) {
			if (o instanceof ScriptObjectMirror) {
				final ScriptObjectMirror sc = (ScriptObjectMirror)o;
				final IAPIEnvironment _env = this.env;
				if (sc.isFunction()) {
					LuaFunction function = new VarArgFunction() {
						@Override
						public Varargs invoke(Varargs args) {
							Object[] oArgs = _env.getComputer().lj_machine.toObjects(args, 1);
							Object r =sc.call(sc, oArgs);
							return JSToLua(r);
						}
					};
					return function;
				} else if (sc.isArray()) {
					LuaTable table = new LuaTable();
					for (String k : sc.getOwnKeys(true)) {
						if (!k.equals("length"))
						table.set(Integer.valueOf(k)+1, this.env.getComputer().lj_machine.toValue(sc.get(k)));	
					}
					return table;
				} else if (sc.isExtensible()){
					LuaTable table = new LuaTable();
					for (String k : sc.getOwnKeys(true)) {
						table.set(k, JSToLua(sc.get(k)));
					}
					return table;
				} else if (sc.isEmpty()) {
					return LuaValue.NIL;
				}
			} else if (o instanceof ScriptFunction) {
	
			} else if (o instanceof Integer) {
				int i = ((Integer)o).intValue();
				return LuaValue.valueOf(i);
			} else {
				return this.env.getComputer().lj_machine.toValue(o);
			}
		}
		return LuaValue.NIL;
	}
	
	@Override
	public Object[] callMethod(ILuaContext context, int method, Object[] arguments)
			throws LuaException, InterruptedException {
		switch (method) {
		case 0: {
			try {
				Object o = this.engine.eval(arguments[0].toString());
				return new Object[] {this.env.getComputer().lj_machine.toObject(JSToLua(o))};
			} catch (ScriptException e) {
					return new Object[] {e.getMessage()};
			}
		}
		case 1: {
			if (arguments.length > 1) {
				return new Object[] {this.env.getComputer().lj_machine.toObject(JSToLua(this.engine.getContext().getAttribute(arguments[0].toString(), this.engine.getContext().ENGINE_SCOPE)))};
			}
		}
		case 2: {
			if (arguments.length > 2) {
				this.engine.getContext().setAttribute(arguments[0].toString(), arguments[1], this.engine.getContext().ENGINE_SCOPE);
			}
		}
		default:
			break;
		}
		return null;
	}
	@Override
	public String[] getNames() {
		// TODO Auto-generated method stub
		return new String[] {"nashorn"};
	}
	@Override
	public void startup() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void advance(double _dt) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
}
