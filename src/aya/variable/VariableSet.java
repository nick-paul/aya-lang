package aya.variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import aya.obj.Obj;
import aya.util.Pair;

public class VariableSet {
	private HashMap<Long, Obj> vars;
	private boolean captureAllAssignments;

	
	public VariableSet(Variable[] argNames, long[] argTypes, LinkedList<Long> copyOnInit) {
		this.vars = new HashMap<Long, Obj>();
		this.captureAllAssignments = false;
	}
	
	public VariableSet(boolean is_module) {
		this.vars = new HashMap<Long, Obj>();
		this.captureAllAssignments = is_module;
	}
	
	public boolean isModule() {
		return captureAllAssignments;
	}
	
	/** Return the number of mappings this variable set has */
	public int size() {
		return vars.size();
	}
	
	public void setVar(Variable v, Obj o) {
		vars.put(v.getID(),o);
	}
	
	public void unsetVar(Variable v) {
		vars.remove(v.getID());
	}
	
	
	public void setVar(long v, Obj o) {
		vars.put(v,o);
	}
	
	public Obj getObj(long id) {
		return vars.get(id);
	}
	
	public Obj getObj(Variable v) {
		return vars.get(v.getID());
	}
	
	public HashMap<Long, Obj> getMap() {
		return vars;
	}
	
	/** Returns true if this set contains a definition for the variable v */
	public boolean hasVar(Variable v) {
		return vars.containsKey(v.getID());
	}
	
	/** Returns true if this set contains a definition for the variable v */
	public boolean hasVar(long v) {
		return vars.containsKey(v);
	}
	
	public String toString() {
		return headerStr();
	}
	
	
	/**
	 * Output the variable set as a space separated list of name(value) items
	 * If the value is 0, do not print the value or the parenthesis
	 */
	public String headerStr() {
		StringBuilder sb = new StringBuilder("");
		
		Iterator<Entry<Long, Obj>> it = vars.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Long, Obj> pair = (Map.Entry<Long, Obj>)it.next();
			sb.append(Variable.decodeLong(pair.getKey()));

			boolean print = true;
			if (pair.getValue().isa(Obj.NUM)) {
				aya.obj.number.Number n = (aya.obj.number.Number)pair.getValue();
				if (n.toInt() == 0) {
					print = false;
				}
			}
			
			if (print) {
				//sb.append("(" + pair.getValue().str() + ") ");
				sb.append("(..) ");
			} else {
				sb.append(" ");
			}
		}
		
		return sb.toString();
	}
	
	/** Sets all vars in the VariableSet */
	public void setAllVars(HashMap<Long, Obj> map) {
		this.vars = map;
	}
	
	@SuppressWarnings("unchecked")
	@Override public VariableSet clone() {
		VariableSet out = new VariableSet(captureAllAssignments);
		out.setAllVars((HashMap<Long, Obj>)vars.clone());
		return out;
	}
	
	/** Create a deep copy of the variable set */
	public VariableSet deepcopy() {
		VariableSet out = new VariableSet(captureAllAssignments);
		for (Long l : vars.keySet()) {
			out.setVar(l, vars.get(l).deepcopy());
		}
		return out;
	}

	public void clear() {
		vars.clear();
	}
	
	/** Add all variables from `other` to this var set. Overwrite if needed */
	public void merge(VariableSet other) {
		Iterator<Entry<Long, Obj>> it = other.vars.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry<Long,Obj> pair = (Map.Entry<Long, Obj>)it.next();
	    	this.setVar(pair.getKey(), pair.getValue());
	    }
	}

	/** Return all variables as a list of pairs */
	public ArrayList<Pair<Variable, Obj>> getAllVars() {
		ArrayList<Pair<Variable,Obj>> out = new ArrayList<Pair<Variable, Obj>>();
		Iterator<Entry<Long, Obj>> it = vars.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry<Long,Obj> pair = (Map.Entry<Long, Obj>)it.next();
	    	out.add(new Pair<Variable, Obj>(new Variable(pair.getKey()), pair.getValue()));
	    }
	    return out;
	}

	/** Return all variables */
	public ArrayList<Long> keys() {
		ArrayList<Long> out = new ArrayList<Long>();
		Iterator<Entry<Long, Obj>> it = vars.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry<Long,Obj> pair = (Map.Entry<Long, Obj>)it.next();
	    	out.add(pair.getKey());
	    }
	    return out;
	}
	
	/** Return all Objs */
	public ArrayList<Obj> values() {
		ArrayList<Obj> out = new ArrayList<Obj>();
		Iterator<Entry<Long, Obj>> it = vars.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry<Long,Obj> pair = (Map.Entry<Long, Obj>)it.next();
	    	out.add(pair.getValue());
	    }
	    return out;
	}

	/** Merge variables fromt he given variable set only if they are defined in this one */
	public void mergeDefined(VariableSet varSet) {
		for (Entry<Long, Obj> e : varSet.getMap().entrySet()) {
			if (vars.containsKey(e.getKey())) {
				vars.put(e.getKey(), e.getValue());
			}
		}
		
	}
	

	/** Remove the mapping defined by the given id */
	public void remove(long id) {
		vars.remove(id);
	}

	/** Copy variable mappings from the input set to this one. Does not copy additional metadata such as args */
	public void update(VariableSet other) {
		vars.putAll(other.vars);
	}
	
	
}
