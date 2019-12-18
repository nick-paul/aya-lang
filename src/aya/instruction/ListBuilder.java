package aya.instruction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

import aya.exceptions.AyaRuntimeException;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.list.GenericList;
import aya.obj.list.List;
import aya.obj.list.ListRangeUtils;

public class ListBuilder extends Instruction {

	
	private Block initialList;
	private Block map;
	private Block[] filters;
	private int num_captures;

	public ListBuilder(Block initial, Block map, Block[] filters, int num_captures) {
		this.initialList = initial;
		this.map = map;
		this.filters = filters;
		this.num_captures = num_captures;
	}
	
	public List createList(Stack<Obj> outerStack) {
		Block initial = initialList.duplicate();

		for (int p = 0; p < num_captures; p++) {
			initial.add(outerStack.pop());
		}
		
		initial.eval();
		
		ArrayList<Obj> res = new ArrayList<Obj>();			//Initialize the argument list
		res.addAll(initial.getStack());						//Copy the results into the argument list
		
		boolean allLists = false;							//Check if all arguments are lists
		if(res.size() > 1) {
			allLists = true;								//All arguments may be a list
			for (Obj o : res) {
				if(!o.isa(Obj.LIST)) {
					allLists = false;
					break;
				}
			}
		}
		
		ArrayList<Obj> list = null;
		List outList = null;
		
		//If all arguments are lists, dump each list's respective element onto the stack of the map block
		// [[1 2][3 4], +] => 1 3 +, 2 4 + => [4 6]
		if (allLists) {
			ArrayList<List> listArgs = new ArrayList<List>(res.size());
			int size = -1;
			
			//Check lengths and cast objects
			for(int i = 0; i < res.size(); i++) {
				listArgs.add((List)(res.get(i)));
				if(size == -1) {
					size = listArgs.get(0).length();
				} else if (size != listArgs.get(i).length()) {
					throw new AyaRuntimeException("List Builder: All lists must be same length");
				}
			}
			
			list = new ArrayList<Obj>(size);
			
			//Dump items from the lists into the blocks and apply the map if needed
			for(int i = 0; i < size; i++) {
				Block b = new Block();
				for (int j = 0; j < listArgs.size(); j++) {
					b.push(listArgs.get(j).get(i));
				}
				
				//Apply the map
				if(map != null) {
					b.addAll(map.getInstructions().getInstrucionList());
					b.eval();		
				}
				list.addAll(b.getStack());
			}
			
			outList = new GenericList(list).promote();
			
		} else {
			outList = ListRangeUtils.buildRange(new GenericList(res));							//Create the initial range
			if(map != null) {
				outList = this.map.mapTo(outList);				//Map 'map' to the list
			}
		}
		
		if(filters != null) {								//Apply the filters to the list
			for (Block filter : filters) {
				outList = filter.filter(outList);
			}
		}
		return outList;
	}
	

	@Override
	public void execute(Block b) {
		b.push(createList(b.getStack()));
	}

	@Override
	protected String repr(LinkedList<Long> visited) {
		StringBuilder sb = new StringBuilder("[");
		sb.append(initialList.toString(false) + ", ");
		if(map != null) {
			sb.append(map.toString(false) + ", ");
		}
		if(filters != null) {
			for (Block b : filters) {
				sb.append(b.toString(false) + ", ");
			}
		}
		sb.setLength(sb.length()-2);
		sb.append("]");
		return sb.toString();
	}

	
}