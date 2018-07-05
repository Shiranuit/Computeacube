package dan200.computercraft.core.apis;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.WeakTable;
import org.lwjgl.Sys;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.lua.LuaException;

public class LuaObject extends LuaValue {
	
	public static final LuaString INDEX       = valueOf("__index");

	/** LuaString constant with value "__newindex" for use as metatag */
	public static final LuaString NEWINDEX    = valueOf("__newindex");

	/** LuaString constant with value "__call" for use as metatag */
	public static final LuaString CALL        = valueOf("__call");

	/** LuaString constant with value "__mode" for use as metatag */
	public static final LuaString MODE        = valueOf("__mode");

	/** LuaString constant with value "__metatable" for use as metatag */
	public static final LuaString METATABLE   = valueOf("__metatable");

	/** LuaString constant with value "__add" for use as metatag */
	public static final LuaString ADD         = valueOf("__add");

	/** LuaString constant with value "__sub" for use as metatag */
	public static final LuaString SUB         = valueOf("__sub");

	/** LuaString constant with value "__div" for use as metatag */
	public static final LuaString DIV         = valueOf("__div");

	/** LuaString constant with value "__mul" for use as metatag */
	public static final LuaString MUL         = valueOf("__mul");

	/** LuaString constant with value "__pow" for use as metatag */
	public static final LuaString POW         = valueOf("__pow");

	/** LuaString constant with value "__mod" for use as metatag */
	public static final LuaString MOD         = valueOf("__mod");

	/** LuaString constant with value "__unm" for use as metatag */
	public static final LuaString UNM         = valueOf("__unm");

	/** LuaString constant with value "__len" for use as metatag */
	public static final LuaString LEN         = valueOf("__len");

	/** LuaString constant with value "__eq" for use as metatag */
	public static final LuaString EQ          = valueOf("__eq");

	/** LuaString constant with value "__lt" for use as metatag */
	public static final LuaString LT          = valueOf("__lt");

	/** LuaString constant with value "__le" for use as metatag */
	public static final LuaString LE          = valueOf("__le");

	/** LuaString constant with value "__tostring" for use as metatag */
	public static final LuaString TOSTRING    = valueOf("__tostring");

	/** LuaString constant with value "__concat" for use as metatag */
	public static final LuaString CONCAT      = valueOf("__concat");
	
	public static final LuaString NOT      = valueOf("__not");
	
	public static final LuaString LTEQ      = valueOf("__lteq");
	
	public static final LuaString LEEQ      = valueOf("__leeq");
	
	public static final LuaString AND      = valueOf("__and");
	
	public static final LuaString OR      = valueOf("__or");
		
	public static final LuaString TONUMBER      = valueOf("__tonumber");

	public String typename = "nil";
	protected LuaValue m_metatable;
	
	public LuaObject(String typename) {
		this.typename=typename;
	}

	@Override
	public int type() {
		return 10;
	}

	@Override
	public String typename() {
		return this.typename;
	}
	

	private boolean hasMetatable(String str, int type) {
		LuaValue v = this.metatag(LuaValue.valueOf(str));
		if (!v.isnil() && v.type() == type) {
			return true;
		}
		return false;
	}
	
	private boolean hasMetatable(LuaString str, int type) {
		LuaValue v = this.metatag(str);
		if (!v.isnil() && v.type() == type) {
			return true;
		}
		return false;
	}
	
	private boolean hasMetatable(String str) {
		LuaValue v = this.metatag(LuaValue.valueOf(str));
		if (!v.isnil()) {
			return true;
		}
		return false;
	}
	
	private boolean hasMetatable(LuaString str) {
		LuaValue v = this.metatag(str);
		if (!v.isnil()) {
			return true;
		}
		return false;
	}
	
	private LuaValue getMetatable(String str, int type) {
		if (hasMetatable(str, type)) {
			return this.metatag(LuaValue.valueOf(str));
		}
		return LuaValue.NIL;
	}
	
	private LuaValue getMetatable(LuaString str, int type) {
		if (hasMetatable(str, type)) {
			return this.metatag(str);
		}
		return LuaValue.NIL;
	}
	
	
	public LuaValue not() {
		return checkmetatag(NOT, "attempt to perform not on ").call(this);
	}
	
	public LuaValue len()  { 
		return checkmetatag(LEN, "attempt to get length of ").call(this);  
	}
	
	public boolean   eq_b( LuaValue val ) {
		return checkmetatag(EQ, "attempt to compare "+val.typename() + " and ").call(this, val).checkboolean();
	}
	
	public boolean lt_b( LuaValue rhs )
	{
		return checkmetatag(LT, "attempt to compare "+rhs.typename() + " and ").call(this, rhs).checkboolean(); 
	}
	
	public boolean lteq_b( LuaValue rhs ) { 
		return checkmetatag(LTEQ, "attempt to compare "+rhs.typename() + " and ").call(this, rhs).checkboolean(); 
	}
	
	public boolean   gt_b( LuaValue rhs )         {
		return checkmetatag(LE, "attempt to compare "+rhs.typename() + " and ").call(this, rhs).checkboolean();
	}
	
	public boolean   gteq_b( LuaValue rhs )         {
		return checkmetatag(LEEQ, "attempt to compare "+rhs.typename() + " and ").call(this, rhs).checkboolean();
	}
	
	public LuaValue   and( LuaValue rhs )      {
		System.out.println("and");
		if (hasMetatable(AND,TFUNCTION)) {
			return getMetatable(AND, TFUNCTION).call(this, rhs);
		}
		return super.and(rhs);
	}
	
	public LuaValue   or( LuaValue rhs )      {
		if (hasMetatable(OR,TFUNCTION)) {
			return getMetatable(OR, TFUNCTION).call(this, rhs);
		}
		return super.or(rhs);
	}
	
	public LuaValue   tostring()      {
		LuaValue r = super.tostring();
		if (hasMetatable(TOSTRING,TFUNCTION)) {
			return getMetatable(TOSTRING, TFUNCTION).call(this);
		}
		return r;
	}
	public LuaValue   tonumber()      {
		LuaValue r = super.tonumber();
		if (hasMetatable(TONUMBER,TFUNCTION)) {
			return getMetatable(TONUMBER, TFUNCTION).call(this);
		}
		return r;
	}
	
	public boolean   toboolean()      {
		boolean r = super.toboolean();
		if (hasMetatable("__toboolean",TFUNCTION)) {
			return getMetatable("__toboolean", TFUNCTION).call(this).toboolean();
		}
		return r;
	}
	
	public LuaValue getmetatable() {
		return m_metatable;
	}
	
	public LuaValue setmetatable(LuaValue metatable) {
		m_metatable = metatable;
		return this;
	}
	

	
}
