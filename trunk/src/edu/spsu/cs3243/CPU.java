package edu.spsu.cs3243;

public class CPU {
	private PCB currentProcess;
	private int[] registers;
	private String[] cache;
	private int pc;
	private boolean running;

	public CPU(int largestSize) {
		currentProcess = null;
		registers = new int[16];
		cache = new String[largestSize];

		for (int i = 0; i < registers.length; i++)
			registers[i] = 0;
		
		for (int i = 0; i < cache.length; i++)
			cache[i] = "00000000";
	}

	public void run(PCB nextProcess, ProcessQueue runningQueue, ProcessQueue terminatedQueue) {
		// Setup the CPU before the process is run
		for (int i = 0; i < registers.length; i++)
			registers[i] = 0;

		int start = nextProcess.instMemLoc, end = start + nextProcess.getSize();
		for (int i = 0; i < cache.length; i++) {
			if (i < end - start)
				cache[i] = RAM.hexFormat(RAM.instance().read(start + i));
			else
				cache[i] = "00000000";
		}

		nextProcess.pc = nextProcess.instMemLoc;
		currentProcess = nextProcess;
		pc = currentProcess.instMemLoc;

		// Run the process
		running = true;
		while (running) {
			String hex = cache[pc - currentProcess.instMemLoc];
			String binaryString = hexToBinary(hex);
			
			Logger.log("About to execute the following instruction: (hex)%s. (binary)%s. Current PC: %d.", hex, binaryString, pc);
			executeInstruction(binaryString);
		}
		
		Logger.log("PROCESS DONE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! " + currentProcess);
		runningQueue.remove(nextProcess);
		terminatedQueue.add(nextProcess);
	}

	public void executeInstruction(String binaryString) {
		if (getOpcode(binaryString) == 0x13) {
			System.out.println("NOP Found!");
		} else if (getOpcode(binaryString) == 0x12) {
			System.out.println("HLT Found! Halting the execute!");
			running = false;
			return;
		}

		int instructionType = getInstructionType(binaryString);
		
		Logger.log("Instruction type: %d", instructionType);

		switch (instructionType) {
		case 0:
			doArithmatic(binaryString);
			break;
		case 1:
			doConditionalBranch(binaryString);
			break;
		case 2:
			doUnconditionalJump(binaryString);
			break;
		case 3:
			doIO(binaryString);
			break;
		default:
			System.out.println("We came across an instruction that we didn't handle properly. Dumping it: " + binaryString);
			break;
		}
	}

	private void doArithmatic(String binaryString) {
		Logger.log("Doing an arithmatic instruction!");
		int opcode = getOpcode(binaryString);
		int reg1 = getArithSReg1(binaryString);
		int reg2 = getArithSReg2(binaryString);
		int dReg = getArithDReg(binaryString);
		Logger.log("IO args: opcode: %d. reg1: %d. reg2: %d. dReg: %d", opcode, reg1, reg2, dReg);

		// 04 MOV R Transfers the content of one register into another
		// 05 ADD R Adds content of two S-regs into D-reg
		// 06 SUB R Subtracts content of two S-regs into D-reg
		// 07 MUL R Multiplies content of two S-regs into D-reg
		// 08 DIV R Divides content of two S-regs into D-reg
		// 09 AND R Logical AND of two S-regs into D-reg
		// 0A OR R Logical OR of two S-regs into D-reg
		// 10 SLT R Sets the D-reg to 1 if first S-reg is less than second B-reg, and 0 otherwise
		switch (opcode) {
		case 4: // MOV
			registers[reg1] = registers[reg2];
			break;
		case 5: // ADD
			registers[dReg] = registers[reg1] + registers[reg2];
			break;
		case 6: // SUB
			registers[dReg] = registers[reg1] - registers[reg2];
			break;
		case 7: // MUL
			registers[dReg] = registers[reg1] * registers[reg2];
			break;
		case 8: // DIV
			if (registers[reg2] <= 0) {
				System.out.println("Divide by zero!!!");
				break;
			}
			registers[dReg] = registers[reg1] / registers[reg2];
			break;
		case 9: // AND
			registers[dReg] = registers[reg1] & registers[reg2];
			break;
		case 10: // OR
			registers[dReg] = registers[reg1] | registers[reg2];
			break;
		case 0x10: // SLT
			registers[dReg] = (registers[reg1] < registers[reg2]) ? 1 : 0;
			break;
		default:
			break;
		}

		pc++;
	}

	private void doConditionalBranch(String binaryString) {
		Logger.log("Doing a conditional branch instruction!");
		int opcode = getOpcode(binaryString);
		int bReg = getCondBReg(binaryString);
		int dReg = getCondDReg(binaryString);
		int address = getCondAddress(binaryString);
		Logger.log("IO args: opcode: %d. bReg: %d. dReg: %d. address: %d", opcode, bReg, dReg, address);

		// 02 ST I Stores content of a reg. into an address
		// 03 LW I Loads the content of an address into a reg.
		// 0B MOVI I Transfers address/data directly into a register
		// 0C ADDI I Adds a data directly to the content of a register
		// 0D MULI I Multiplies a data directly to the content of a register
		// 0E DIVI I Divides a data directly to the content of a register
		// 0F LDI I Loads a data/address directly to the content of a register
		// 11 SLTI I Sets the D-reg to 1 if first S-reg is less than a data, and 0 otherwise
		// 15 BEQ I Branches to an address when content of B-reg = D-reg
		// 16 BNE I Branches to an address when content of B-reg <> D-reg
		// 17 BEZ I Branches to an address when content of B-reg = 0
		// 18 BNZ I Branches to an address when content of B-reg <> 0
		// 19 BGZ I Branches to an address when content of B-reg > 0
		// 1A BLZ I Branches to an address when content of B-reg < 0

		switch (opcode) {
		case 0x02: // ST
			if(dReg != 0) 
				cache[registers[dReg]] = RAM.hexFormat(Integer.toHexString(registers[bReg]));
			else
				cache[address] = RAM.hexFormat(Integer.toHexString(registers[bReg]));
			break;
		case 0x03: // LW
			registers[dReg] = Integer.parseInt(cache[registers[bReg]], 16);
			break;
		case 0x0B: // MOVI
			if (address != 0)
				registers[dReg] = address;
			else
				registers[dReg] = registers[bReg];
			break;
		case 0x0C: // ADDI
			registers[dReg] += address;
			break;
		case 0x0D: // MULI
			registers[dReg] *= address;
			break;
		case 0x0E: // DIVI
			if (address <= 0) {
				System.out.println("Divide by zero!!!");
				break;
			}
			registers[dReg] /= address;
			break;
		case 0x0F: // LDI
			registers[dReg] = address;
			break;
		case 0x11: // SLTI
			registers[dReg] = (registers[bReg] < address) ? 1 : 0;
			break;
		case 0x15: // BEQ
			if (registers[bReg] == registers[dReg])
				pc = currentProcess.instMemLoc + address;
			else
				pc++;
			break;
		case 0x16: // BNE
			if (registers[bReg] != registers[dReg])
				pc = currentProcess.instMemLoc + address;
			break;
		case 0x17: // BEZ
			if (registers[bReg] == 0)
				pc = currentProcess.instMemLoc + address;
			break;
		case 0x18: // BNZ
			if (registers[bReg] != 0)
				pc = currentProcess.instMemLoc + address;
			break;
		case 0x19: // BGZ
			if (registers[bReg] > 0)
				pc = currentProcess.instMemLoc + address;
			break;
		case 0x1A: // BLZ
			if (registers[bReg] < 0)
				pc = currentProcess.instMemLoc + address;
			break;
		}

		pc++;
	}

	private void doUnconditionalJump(String binaryString) {
		Logger.log("Doing an unconditional jump instruction!");
		int opcode = getOpcode(binaryString);
		int jumpAddress = getJumpAddress(binaryString);
		Logger.log("IO args: opcode: %d. jumpAddress: %d.", opcode, jumpAddress);
		
		if (opcode == 0x14) {
			pc = jumpAddress;
		}
	}

	private void doIO(String binaryString) {
		Logger.log("Doing an IO instruction!");
		int opcode = getOpcode(binaryString);
		int reg1 = getIOReg1(binaryString);
		int reg2 = getIOReg2(binaryString);
		int address = getIOAddress(binaryString);
		Logger.log("IO args: opcode: %d. reg1: %d. reg2: %d. address: %d", opcode, reg1, reg2, address);
		// 00 RD I/O Reads content of I/P buffer into a accumulator
		// 01 WR I/O Writes the content of accumulator into O/P buffer

		switch (opcode) {
		case 0x00: // RD
			if (reg2 != 0) {
				registers[reg1] = Integer.parseInt(cache[registers[reg2]]);
			} else {
				String read = cache[address];
				int i = Integer.parseInt(read, 16);
				Logger.log("Read from RAM: %s. As int: %d", read, i);
				registers[reg1] = i;
			}
			break;
		case 0x01: // WR
			if (reg2 != 0) {
				cache[registers[reg2]] = RAM.hexFormat(Integer.toHexString(registers[reg1]));
			} else {
				String hex = RAM.hexFormat(Integer.toHexString(registers[reg1]));
				cache[address] = hex;
			}
			break;
		}

		pc++;
	}

	private int getInstructionType(String binaryString) {
		return Integer.parseInt(binaryString.substring(0, 2), 2);
	}

	private int getOpcode(String binaryString) {
		return Integer.parseInt(binaryString.substring(2, 8), 2);
	}

	// Arithmatic opcode stuff
	private int getArithSReg1(String binaryString) {
		return Integer.parseInt(binaryString.substring(8, 12), 2);
	}

	private int getArithSReg2(String binaryString) {
		return Integer.parseInt(binaryString.substring(12, 16), 2);
	}

	private int getArithDReg(String binaryString) {
		return Integer.parseInt(binaryString.substring(16, 20), 2);
	}

	// Conditional Branch and Immediate opcode stuff
	private int getCondBReg(String binaryString) {
		return Integer.parseInt(binaryString.substring(8, 12), 2);
	}

	private int getCondDReg(String binaryString) {
		return Integer.parseInt(binaryString.substring(12, 16), 2);
	}

	private int getCondAddress(String binaryString) {
		int i = Integer.parseInt(binaryString.substring(16, 32), 2);
		if(i != 1)
			i /= 4;
		return i;
	}

	// UnConditional Jump opcode stuff
	private int getJumpAddress(String binaryString) {
		return Integer.parseInt(binaryString.substring(8, 32), 2) / 4;
	}

	// Input and Output opcode stuff
	private int getIOReg1(String binaryString) {
		return Integer.parseInt(binaryString.substring(8, 12), 2);
	}

	private int getIOReg2(String binaryString) {
		return Integer.parseInt(binaryString.substring(12, 16), 2);
	}

	private int getIOAddress(String binaryString) {
		return Integer.parseInt(binaryString.substring(16, 32), 2) / 4;
	}

	/*
	 * Utility Methods
	 */
	public static String hexToBinary(String s) {
		if (s != null && s != "") {
			String ret = Long.toBinaryString(Long.parseLong(s, 16));

			// If the binary string is less that 32 'bits' long, pad the left side to make it that long
			for (int i = 0 + ret.length(); i < 32; i++) {
				ret = "0" + ret;
			}

			return ret;
		}

		return "";
	}

	// Converts a string of binary into chunks so we can see what each part is
	public String arithmaticString(String binaryString) {
		return String.format("%d. %d. %d. %d. %d. %d.", getInstructionType(binaryString), getOpcode(binaryString), getArithSReg1(binaryString), getArithSReg2(binaryString), getArithDReg(binaryString), binaryString.substring(20));
	}

	// Converts a string of binary into chunks so we can see what each part is
	public String conditionalString(String binaryString) {
		return String.format("%d. %d. %d. %d. %d.", getInstructionType(binaryString), getOpcode(binaryString), getCondBReg(binaryString), getCondDReg(binaryString), getCondAddress(binaryString));
	}

	// Converts a string of binary into chunks so we can see what each part is
	public String unconditionalString(String binaryString) {
		return String.format("%d. %d. %d.", getInstructionType(binaryString), getOpcode(binaryString), getJumpAddress(binaryString));
	}

	// Converts a string of binary into chunks so we can see what each part is
	public String ioString(String binaryString) {
		return String.format("%d. %d. %d. %d. %d.", getInstructionType(binaryString), getOpcode(binaryString), getIOReg1(binaryString), getIOReg2(binaryString), getIOAddress(binaryString));
	}
}
