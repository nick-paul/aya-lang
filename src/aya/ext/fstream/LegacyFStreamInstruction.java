package aya.ext.fstream;

import aya.StreamMgr;
import aya.exceptions.AyaRuntimeException;
import aya.exceptions.TypeError;
import aya.instruction.named.NamedInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.character.Char;
import aya.obj.list.Str;
import aya.obj.number.Num;

public class LegacyFStreamInstruction extends NamedInstruction {
	
	public LegacyFStreamInstruction() {
		super("fstream.O");
		_doc = "Stream operations";
		/*
		arg("NC", "stream operations: l:readline, b:readchar, a:readall, c:close, f:flush, i:info");
		arg("SC", "open/close stream: w:write, r:read");
		arg("SN", "write to stream");
		*/
	}

	@Override
	public void execute(Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		
		if (a.isa(Obj.CHAR) && b.isa(Obj.NUMBER)) {
			char c = ((Char)a).charValue();
			int i  = ((Num)b).toInt();
			
			switch (c) {
			case 'l':
				// Push 0 if invalid
				String line = StreamMgr.readline(i);
				if (line == null) {
					block.push(Num.ZERO);
				} else {
					block.push(new Str(line));
				}
				break;
			case 'b':
				// Since 0 is a valid byte, push -1 if invalid
				block.push(Num.fromInt(StreamMgr.read(i)));
				break;
			case 'a':
				// Pushes 0 if invalid
				String all = StreamMgr.readAll(i);
				if (all == null) {
					block.push(Num.ZERO);
				} else {
					block.push(new Str(all));
				}
				break;
			case 'c':
				// Close the file
				block.push(StreamMgr.close(i) ? Num.ONE : Num.ZERO);
				break;
			case 'f':
				// Flush
				block.push(StreamMgr.flush(i) ? Num.ONE : Num.ZERO);
				break;
			case 'i':
				// Info 0:does not exist, 1:input, 2:output
				block.push(Num.fromInt(StreamMgr.info(i)));
				break;
			default:
				throw new AyaRuntimeException("Invalid char for operator 'O': " + c);
			}
			
		} else if (a.isa(Obj.NUMBER)) {
			int i = ((Num)a).toInt();
			block.push(StreamMgr.print(i, b.str()) ? Num.ONE : Num.ZERO);
		} else if (a.isa(Obj.CHAR)) {
			char c = ((Char)a).charValue();
			String filename = b.str();
			block.push(Num.fromInt(StreamMgr.open(filename, c+"")));
		} else {
			throw new TypeError(this, "Unexpected values ", a, b);
		}
	}

}