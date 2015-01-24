package framework;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;


public class LuaClient {
	EventLogger logger;
	Globals globals;
	LuaValue chunk;

	public LuaClient (EventLogger l, String file) {
		logger = l;
		logger.flow("LuaClient init");

		globals = JsePlatform.standardGlobals();
		chunk = globals.loadfile("../resources/scripts/" + file);
		chunk.call();

		LuaValue log = globals.get("log");
		LuaValue res = log.call("connect");
		log(file + "> " + res.toString());
	}

	public String call (String f, LuaValue a, LuaValue b, LuaValue c) {	
		log("calling " + f + "(" + a.toString() + ", " + b.toString() + ", " + c.toString() + ")");
		LuaFunction luaFun = (LuaFunction)globals.get(f);
		LuaValue res = luaFun.call(a, b, c);

		return res.toString();
	}

	public String call (String f, LuaValue a, LuaValue b) {	
		log("calling " + f + "(" + a.toString() + ", " + b.toString() + ")");
		LuaFunction luaFun = (LuaFunction)globals.get(f);
		LuaValue res = luaFun.call(a, b);

		return res.toString();
	}

	public String call (String f, LuaValue a) {	
		log("calling " + f + "(" + a.toString() + ")");
		LuaFunction luaFun = (LuaFunction)globals.get(f);
		LuaValue res = luaFun.call(a);

		return res.toString();
	}

	public String call (String f) {	
		log("calling " + f);
		LuaValue luaFun = globals.get(f);
		LuaValue res = luaFun.call();

		return res.toString();
	}

	public LuaValue get(String v) {
		return globals.get(v);
	}

	public void set(String name, String s) {
		LuaValue k = CoerceJavaToLua.coerce(name);
		LuaValue l = CoerceJavaToLua.coerce(s);
		globals.set(k, l);
	}

	public void set(String name, float f) {
		LuaValue k = CoerceJavaToLua.coerce(name);
		LuaValue l = CoerceJavaToLua.coerce(f);
		globals.set(k, l);
	}



	private void log (String s) { System.out.println("[Lua/Client]> " + s);}
}
