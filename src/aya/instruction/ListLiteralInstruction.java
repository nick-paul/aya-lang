package aya.instruction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

import aya.entities.InstructionStack;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.list.GenericList;
import aya.obj.list.List;

public class ListLiteralInstruction extends Instruction {
	//The number of items that should be popped from the stack and added to the front of this list
	int num_captures;
	Block block;
	
	public ListLiteralInstruction(InstructionStack is, int num_pops) {
		this.num_captures = num_pops;
		block = new Block(is);
	}
	
	public InstructionStack getInstructions() {
		return block.getInstructions();
	}
	
	public int getPops() {
		return num_captures;
	}
	
	public List getListCopy(Stack<Obj> outerStack) {
		Block b = block.duplicate();
		int p = num_captures;
		
		//Remove the pops from the outer stack
		while (p > 0) {
			p--;
			b.getInstructions().push(outerStack.pop());
		}
		
		b.eval();
		return new GenericList(new ArrayList<Obj>(b.getStack())).promote();
	}
	
	/** Returns null if is not list literal */
	public List toList() {
		ArrayList<Obj> items = new ArrayList<Obj>();
		ArrayList<Instruction> instrs = block.getInstructions().getInstrucionList();
		for (int i = instrs.size()-1; i>=0; i--) {
			if (instrs.get(i) instanceof DataInstruction) {
				items.add(((DataInstruction)(instrs.get(i))).getData());
			} else {
				return null;
			}
		}
		return new GenericList(items).promote();
	}
	
	public ListLiteralInstruction duplicate() {
		ListLiteralInstruction ll = new ListLiteralInstruction(block.getInstructions().duplicate(), num_captures);
		ll.block.getStack().addAll(this.block.getStack());
		return ll;
	}
	

	@Override
	public void execute(Block b) {
		b.push(getListCopy(b.getStack()));
		
	}

	@Override
	protected String repr(LinkedList<Long> visited) {
		String captures = "";
		if (num_captures > 0) {
			captures = num_captures + "| ";
		}
		return "[" + captures + block.getInstructions().toString() + "]";
	}
}

