package dan200.computercraft.core.apis;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.lua.LuaException;

public class SerialAPI implements ILuaAPI {
	
	@Override
	public String[] getNames() {
		return new String[]{"serialutil"};
	}
	
	private static String[] Typenames = new String[]{"boolean","number","string","table"};
	private static void checkArg(Object[] p, int num, int type) throws LuaException {
		String err = "arg "+(num+1)+": expected a "+Typenames[type];
		if(p.length<num+1) 
			throw new LuaException(err);
		switch(type) {
			case 0:
				if(p[num] instanceof Boolean)
					break;
				throw new LuaException(err);
			case 1:
				if(p[num] instanceof Double)
					break;
				throw new LuaException(err);
			case 2:
				if(p[num] instanceof String)
					break;
				throw new LuaException(err);
			case 3:
				if(p[num] instanceof HashMap<?,?>)
					break;
				throw new LuaException(err);
		}
	}
	
	@Override
	public String[] getMethodNames() {
		return new String[]{"writer","reader"};
	}
	
	@Override
	public Object[] callMethod(ILuaContext context, int method, Object[] arguments)
			throws LuaException, InterruptedException {
		try {
			byte[] frst;
			if(arguments.length>0) {
				checkArg(arguments,0,2);
				frst = ((String) arguments[0]).getBytes("ISO-8859-1");
			} else {
				frst = new byte[0];
			}
			switch(method) {
				case 0:
					return new Object[]{new Writer(frst)};
				case 1:
					return new Object[]{new Reader(frst)};
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void startup() {
	}
	
	@Override
	public void shutdown() {
	}
	
	@Override
	public void advance(double timestep) {
	}
	
	private class Writer implements ILuaObject {
		
		private List<Byte> data;
		private byte subByte;
		private byte subBytePos;
		private final String[] methods = new String[]{"write","writeBits","writeByte","writeShort","writeInteger","writeVarInt","writeDouble","writeString","writeBool","writeTable","writeVar","flush"};
		
		public Writer(byte[] in) {
			data = new ArrayList<Byte>();
			subByte = 0;
			subBytePos = 0;
			append(in);
		}
		
		@Override
		public String[] getMethodNames() {
			return methods;
		}
		
		public void append(byte[] s) {
			for(int i = 0; i < s.length; i++) {
				write(s[i]);
			}
		}
		
		private void flushSubByte() {
			if(subBytePos>0) {
				subBytePos = 0;
				write(subByte);
				subByte = 0;
			}
		}
		
		public void write(byte b) {
			flushSubByte();
			data.add(Byte.valueOf(b));
		}
		
		public void writeInteger(Double n) {
			int v = Integer.reverseBytes(n.intValue());
			
			for(int i = 0; i < 4; i++) {
				write((byte) (v & 255));
				v = v >> 8;
			}
		}
		
		public void writeShort(Double n) {
			int v = Short.reverseBytes(n.shortValue());
			write((byte) (v & 255));
			v = v >> 8;
			write((byte) (v & 255));
		}
		
		public void writeVarInt(Double n) {
			long v = n.longValue();
			do {
				byte b = (byte) (v & 127);
				v = (v >> 7L);
				if(v>0)
					b = (byte) (b | 128);
				write(b);
			} while(v>0);
		}
		
		public void writeDouble(Double n) {
			long dt = Long.reverseBytes(Double.doubleToRawLongBits(n));
			for(int i = 0; i < 8; i++) {
				write((byte) (dt&255));
				dt = dt >> 8;
			}
		}
		
		public void writeString(byte[] s) {
			writeVarInt(Double.valueOf(s.length+1));
			append(s);
		}
		
		public void writeBits(Double num, Double numBits) {
			int inum = num.intValue();
			byte bnumBits = numBits.byteValue();
			if(bnumBits<0) return;
			if(bnumBits>32) bnumBits=32;
			int nextb;
			for(int i = 0; i < bnumBits; i+=nextb) {
				nextb = Math.min(8-subBytePos,bnumBits-i);
				int it = (inum >> (bnumBits-nextb-i)) & ((1<<nextb) - 1);
				subByte = (byte) (subByte | (it << (8-subBytePos-nextb)));
				subBytePos += nextb;
				if(subBytePos == 8) flushSubByte();
			}
		}
		
		public void writeBool(Boolean b) {
			writeBits(b?1d:0d,1d);
		}
		
		public void writeTable(HashMap<Object, Object> tb) {
			writeVarInt(Double.valueOf(tb.size()));
			for(Object key: tb.keySet()) {
				writeVar(key);
				writeVar(tb.get(key));
			}
		}
		
		@SuppressWarnings("unchecked")
		public void writeVar(Object o) {
			if(o instanceof Boolean) {
				write((byte) 0);
				writeBool((Boolean) o);
				return;
			}
			if(o instanceof Double) {
				if(((Double) o)%1==0) {
					write((byte) 4);
					writeVarInt((Double) o);
					return;
				}
				write((byte) 1);
				writeDouble((Double) o);
				return;
			}
			if(o instanceof String) {
				write((byte) 2);
				try {
					writeString(((String) o).getBytes("ISO-8859-1"));
				} catch(UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				return;
			}
			if(o instanceof HashMap<?,?>) {
				write((byte) 3);
				writeTable((HashMap<Object,Object>) o);
				return;
			}
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Object[] callMethod(ILuaContext context, int method, Object[] arguments)
				throws LuaException, InterruptedException {
			switch(method) {
				case 0: //write
					SerialAPI.checkArg(arguments, 0, 2);
					try {
						append(((String) arguments[0]).getBytes("ISO-8859-1"));
					} catch(UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					break;
				case 1: //writeBits
					SerialAPI.checkArg(arguments, 0, 1);
					SerialAPI.checkArg(arguments, 1, 1);
					writeBits((Double) arguments[0],(Double) arguments[1]);
					break;
				case 2: //writeByte
					SerialAPI.checkArg(arguments, 0, 1);
					write(((Double) arguments[0]).byteValue());
					break;
				case 3: //writeShort
					SerialAPI.checkArg(arguments, 0, 1);
					writeShort((Double) arguments[0]);
					break;
				case 4: //writeInteger
					SerialAPI.checkArg(arguments, 0, 1);
					writeInteger((Double) arguments[0]);
					break;
				case 5: //writeVarInt
					SerialAPI.checkArg(arguments, 0, 1);
					writeVarInt((Double) arguments[0]);
					break;
				case 6: //writeDouble
					SerialAPI.checkArg(arguments, 0, 1);
					writeDouble((Double) arguments[0]);
					break;
				case 7: //writeString
					SerialAPI.checkArg(arguments, 0, 2);
					try {
						writeString(((String) arguments[0]).getBytes("ISO-8859-1"));
					} catch(UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					break;
				case 8: //writeBool
					SerialAPI.checkArg(arguments, 0, 0);
					writeBool((Boolean) arguments[0]);
					break;
				case 9: //writeTable
					SerialAPI.checkArg(arguments, 0, 3);
					writeTable((HashMap<Object,Object>) arguments[0]);
					break;
				case 10: //writeVar
					for(int i = 0; i < arguments.length; i++) {
						writeVar(arguments[i]);
					}
					break;
				case 11: //flush
					flushSubByte();
					byte[] buf2 = new byte[data.size()];
					for(int i = 0; i < data.size(); i++) {
						buf2[i] = data.get(i).byteValue();
					}
					data.clear();
					return new Object[]{buf2};
			}
			return new Object[]{};
		}

		
	}
	
	private class Reader implements ILuaObject {
		
		private List<Byte> data;
		private byte subByte;
		private byte subBytePos;
		private final String[] methods = new String[]{"read","readBits","readByte","readShort","readInteger","readVarInt","readDouble","readString","readBool","readTable","readVar","append","bytesLeft"};
		
		public Reader(byte[] st) {
			data = new ArrayList<Byte>();
			subByte = 0;
			subBytePos = 0;
			append(st);
		}
		
		@Override
		public String[] getMethodNames() {
			return methods;
		}
		
		public void checkSize(int n) throws LuaException {
			if(data.size()<n) throw new LuaException("Out of data.");
		}
		
		public Object[] read(int n) throws LuaException {
			int rl = Math.min(n, data.size());
			Object[] rt = new Object[rl];
			
			for(int i = 0; i < rl; i++) {
				rt[i] = readByte();
			}
			
			return rt;
		}
		
		public Double readByte() throws LuaException {
			checkSize(1);
			Double v = Double.valueOf(data.get(0) & 0xFF);
			data.remove(0);
			return v;
		}
		
		public Double readShort() throws LuaException {
			checkSize(2);
			int v = ((data.get(0) & 0xFF) << 8) | (data.get(1) & 0xFF);
			data.remove(0); data.remove(0);
			return Double.valueOf(v);
		}
		
		public Double readInteger() throws LuaException {
			checkSize(4);
			int v = ((data.get(0) & 0xFF) << 24) | ((data.get(1) & 0xFF) << 16) | ((data.get(2) & 0xFF) << 8) | (data.get(3) & 0xFF);
			data.remove(0); data.remove(0); data.remove(0); data.remove(0);
			return Double.valueOf(v);
		}
		
		public Double readVarInt() throws LuaException {
			long v = 0;
			byte n = 0;
			byte tmp = 0;
			do {
				checkSize(1);
				tmp = data.get(0).byteValue();
				v = v | (( (long) (tmp & 127) ) << n);
				n += 7;
				data.remove(0);
			} while((tmp & 128) != 0);
			return Double.valueOf(v);
		}
		
		public Double readDouble() throws LuaException {
			checkSize(8);
			long v = 0;
			for(int i = 0; i <8; i++) {
				v = v << 8;
				v = v | (data.get(0) & 0xFFL);
				data.remove(0);
			}
			return Double.longBitsToDouble(v);
		}
		
		public byte[] readString() throws LuaException {
			int t = readVarInt().intValue();
			if(t==0) return null;
			checkSize(t-1);
			byte[] dt = new byte[t-1];
			for(int i = 0; i < t-1; i++) {
				dt[i] = data.get(0).byteValue();
				data.remove(0);
			}
			return dt;
		}
		
		public Double readBits(Double numBits) throws LuaException {
			byte bnumBits = numBits.byteValue();
			if(bnumBits<0) return 0d;
			if(bnumBits>32) bnumBits=32;
			if(bnumBits>8-subBytePos || subBytePos==0)
				checkSize((bnumBits+7)/8);
			int n = 0;
			int nextb;
			for(int i = 0; i < bnumBits; i += nextb) {
				if(subBytePos == 0) {
					subByte = data.get(0).byteValue();
					data.remove(0);
				}
				nextb = Math.min(8-subBytePos, bnumBits-i);
				n = n | (((subByte >> (8-subBytePos-nextb)) & ((1<<nextb)-1)) << (bnumBits-i-nextb));
				subBytePos += nextb;
				if(subBytePos==8) {
					subByte = 0;
					subBytePos = 0;
				}
			}
			return Double.valueOf(n);
		}
		
		public Boolean readBool() throws LuaException {
			return Boolean.valueOf(readBits(1d)>0d);
			//checkSize(1);
			//boolean b = data.get(0).intValue() != 0;
			//data.remove(0);
			//return Boolean.valueOf(b);
		}
		
		public HashMap<Object, Object> readTable() throws LuaException {
			int t = readVarInt().intValue();
			HashMap<Object, Object> tab = new HashMap<Object, Object>();
			for(int i = 0; i < t; i++) {
				Object key = readVar();
				Object val = readVar();
				tab.put(key, val);
			}
			return tab;
		}
		
		public Object readVar() throws LuaException {
			checkSize(1);
			byte tp = data.get(0).byteValue();
			data.remove(0);
			switch(tp) {
				case 0:
					return readBool();
				case 1:
					return readDouble();
				case 2:
					return readString();
				case 3:
					return readTable();
				case 4:
					return readVarInt();
			}
			return null;
		}
		
		public void append(byte[] s) {
			for(int i = 0; i < s.length; i++) {
				data.add(Byte.valueOf(s[i]));
			}
		}
		
		@Override
		public Object[] callMethod(ILuaContext context, int method, Object[] arguments)
				throws LuaException, InterruptedException {
			if(method != 1 && method !=8) {
				subByte = 0;
				subBytePos = 0;
			}
			switch(method) {
				case 0: //read
					int num = 1;
					if(arguments.length>0) {
						SerialAPI.checkArg(arguments, 0, 1);
						num = ((Double) arguments[0]).intValue();
						num = num<1 ? 1 : num;
					}
					return read(num);
				case 1: //readBits
					checkArg(arguments, 0, 1);
					return new Object[]{readBits((Double) arguments[0])};
				case 2: //readByte
					return new Object[]{readByte()};
				case 3: //readShort
					return new Object[]{readShort()};
				case 4: //readInteger
					return new Object[]{readInteger()};
				case 5: //readVarInt
					return new Object[]{readVarInt()};
				case 6: //readDouble
					return new Object[]{readDouble()};
				case 7: //readString
					return new Object[]{readString()};
				case 8: //readBool
					return new Object[]{readBool()};
				case 9: //readTable
					return new Object[]{readTable()};
				case 10: //readVar
					return new Object[]{readVar()};
				case 11: //append
					SerialAPI.checkArg(arguments, 0, 2);
					try {
						append(((String) arguments[0]).getBytes("ISO-8859-1"));
					} catch(UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					break;
				case 12: //bytesLeft
					return new Object[]{Double.valueOf(data.size())};
			}
			return new Object[]{};
		}
		
	}
}
