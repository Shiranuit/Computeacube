/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.core.apis;

import static dan200.computercraft.core.apis.ArgumentHelper.getString;
import static dan200.computercraft.core.apis.ArgumentHelper.optString;
import static dan200.computercraft.core.apis.ArgumentHelper.optTable;

import java.io.Console;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.core.apis.http.HTTPCheck;
import dan200.computercraft.core.apis.http.HTTPRequest;
import dan200.computercraft.core.apis.http.HTTPTask;

public class SocketAPI implements ILuaAPI
{
	private static ListenerThread listener;
	private IAPIEnvironment computer;
	private static int sid = 0;
	private List<CCSocket> sockets;

	static {
		listener = new ListenerThread();
		listener.start();
	}
	
    public SocketAPI( IAPIEnvironment environment )
    {
		sockets = new ArrayList<CCSocket>();
    	computer = environment;
    }

    @Override
    public String[] getNames()
    {
        return new String[] {
            "socket"
        };
    }

    @Override
    public void startup( )
    {
    }

    @Override
    public void advance( double _dt )
    {
    }

    @Override
    public void shutdown( )
    {
    }

    @Nonnull
    @Override
    public String[] getMethodNames()
    {
         return new String[] {
            "connect",
            "getConnections"
        };
    }

    @Override
    public Object[] callMethod( @Nonnull ILuaContext context, int method, @Nonnull Object[] arguments ) throws LuaException
    {
    	switch(method) {
			case 0:
				if(sockets.size()>=16) throw new LuaException("Too many opened connections");
				if(arguments.length<2) throw new LuaException("Expected Address, Port");
				if(!(arguments[0] instanceof String)) throw new LuaException("Address must be a string");
				if(!(arguments[1] instanceof Double)) throw new LuaException("Port must be a number");
				try {
					SocketChannel sc = SocketChannel.open();
					SocketAddress sa = new InetSocketAddress((String)arguments[0], ((Double) arguments[1]).intValue());
					sc.configureBlocking(false);
					sc.connect(sa);
					CCSocket sock = new CCSocket(sc, sa, computer, this);
					sockets.add(sock);
					Object[] params;
					while(true) {
						try {
							params = context.pullEvent("socket_connect");
							if(params.length>1 && sock.getID().equals(params[1]))
								break;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					return new Object[]{sock};
				} catch (IOException e) {
					throw new LuaException(e.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
				}
			case 1:
				Object[] objs = new Object[sockets.size()];
				int i = 0;
				for(CCSocket sock: sockets)
					objs[i++] = sock;
				return objs;
		}
			
		return null;
    }
    
    private class CCSocket implements ILuaObject {

		private IAPIEnvironment computer;
		private SocketChannel sock;
		private String id;
		private boolean closed;
		private int waitFor;
		private PipedInputStream in;
		private PipedOutputStream out;
		private SocketAPI api;
		
		public CCSocket(SocketChannel sc, SocketAddress sa, IAPIEnvironment comp, SocketAPI socketAPI) throws LuaException {
			computer = comp;
			sock = sc;
			closed = false;
			api = socketAPI;
			try {
				in = new PipedInputStream();
				out = new PipedOutputStream(in);
				id = ((InetSocketAddress) sa).getAddress().toString() + "@" + sid++;
			} catch (IOException e) {
				throw new LuaException(e.getMessage());
			}
			listener.register(this);
			waitFor = 0;
		}
		
		public String getID() {
			return id;
		}
		
		public SocketChannel getSocket() {
			return sock;
		}
		
		public void onRead() {
			ByteBuffer buf = ByteBuffer.allocate(1024);
			try {
				int num = 1;
				while(num>0) {
					num = sock.read(buf);
					if(num == -1) {
						computer.queueEvent("socket_message", new Object[]{getID(), -1});
						close();
						return;
					}
					if(num>0)
						out.write(buf.array(),0,num);
				}
				if(waitFor>0 && in.available() >= waitFor) {
					byte[] msg = new byte[waitFor];
					in.read(msg, 0, waitFor);
					computer.queueEvent("socket_message", new Object[]{getID(), waitFor, msg});
					waitFor = 0;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void close() {
			api.sockets.remove(this);
			closed = true;
			try {
				sock.close();
			} catch (IOException e) { }
		}

		public boolean onConnected() {
			computer.queueEvent("socket_connect", new Object[]{getID()});
			return !closed;
		}
		
		@Override
		public String[] getMethodNames() {
			return new String[] {"write", "read", "close", "getID", "getState"};
		}

		@Override
		public Object[] callMethod(ILuaContext context, int method, Object[] arguments)
				throws LuaException, InterruptedException {
			if(closed && method<3) throw new LuaException("Socket is closed");
			switch(method) {
				case 0:
					if(!(arguments[0] instanceof String)) throw new LuaException("Expected string");
					try {
						sock.write(ByteBuffer.wrap(((String) arguments[0]).getBytes("ISO-8859-1")));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace(); //wtf
					} catch (IOException e) {
						throw new LuaException("IOException");
					}
					break;
				case 1:
					if(arguments.length<1) {
						waitFor = 1;
					} else if(arguments[0] instanceof Double) {
						if((Double) arguments[0] < 1) throw new LuaException("number of bytes must be greater than 0");
						waitFor = ((Double) arguments[0]).intValue();
					} else {
						throw new LuaException("unknown read filter");
					}
					try {
						if(waitFor>0 && in.available() >= waitFor) {
							byte[] msg = new byte[waitFor];
							in.read(msg, 0, waitFor);
							computer.queueEvent("socket_message", new Object[]{getID(), waitFor, msg});
							waitFor = 0;
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					Object[] params;
					while(true) {
						params = context.pullEvent("socket_message");
						if(params.length>2 && getID().equals(params[1]))
							break;
					}
					if(params.length==3)
						return new Object[]{false,((Double) params[2]).intValue()==0?"ERROR":"CLOSED"};
					else //>2
						return new Object[]{true,params[3],params[2]};
				case 2:
					listener.unregister(this);
					close();
					break;
				case 3:
					return new Object[]{getID()};
				case 4:
					return new Object[]{closed?"CLOSED":(sock.isConnectionPending()?"PENDING":"READY")};
			}
			return null;
		}
		
	}
	
	private static class ListenerThread extends Thread {
		private Selector selector;
		private Map<SocketChannel, CCSocket> socks;
		public List<SocketChannel> toadd;
		@Override
		public void run() {
			socks = new HashMap<SocketChannel, CCSocket>();
			toadd = new ArrayList<SocketChannel>();
			try {
				selector = Selector.open();
				while (true) {
					int num = selector.select();
					if (toadd.size()>0) {
						for(SocketChannel sc: toadd) {
							sc.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
						}
						toadd.clear();
					}
					//if (num == 0)
					//	continue;
					
					Set<SelectionKey> keys = selector.selectedKeys();
					Iterator<SelectionKey> it = keys.iterator();
					while (it.hasNext()) {
						SelectionKey key = it.next();
						if ((key.readyOps() & SelectionKey.OP_READ) != 0) {
							SocketChannel sc = (SocketChannel)key.channel();
							socks.get(sc).onRead();
						} else if ((key.readyOps() & SelectionKey.OP_CONNECT) != 0) {
							SocketChannel sc = (SocketChannel)key.channel();
							sc.finishConnect();
							//sc.write(ByteBuffer.wrap(("lol").getBytes()));
							if(socks.get(sc).onConnected())
								sc.register(selector, SelectionKey.OP_READ);
							else
								sc.register(selector, 0);
						}
					}
					keys.clear();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void register(CCSocket ccSocket) {
			SocketChannel sc = ccSocket.getSocket();
			socks.put(sc, ccSocket);
			toadd.add(sc);
			selector.wakeup();
		}
		
		public void unregister(CCSocket ccSocket) {
			SocketChannel sc = ccSocket.getSocket();
			try {
				sc.register(selector, 0);
			} catch (ClosedChannelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			socks.remove(sc);
		}
	}
}
