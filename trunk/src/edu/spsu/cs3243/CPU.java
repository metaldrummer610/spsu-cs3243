package edu.spsu.cs3243;

public class CPU {
	private PCB currentProcess;
	private int[] registers;
	private int pc;
	private boolean running;

	public CPU() {
		currentProcess = null;
		registers = new int[16];

		for (int i = 0; i < registers.length; i++)
			registers[i] = 0;
	}

	public void run(PCB nextProcess, ProcessQueue terminatedQueue) {
		// Starts processing the next process
		/*
		 * Pretty much, this is what I need to do... 1. Get the address of the next instruction 2. Decode that instruction 3. Do what the instruction wants to do 4. Repeat Decoding
		 * the instructions 1. Take the instruction and convert it to a binary string 2. Pick apart the string to find all the different parts
		 */

		currentProcess = nextProcess;
		pc = currentProcess.pc;

		running = true;
		while (running) {
			String binaryString = hexToBinary(RAM.instance().read(currentProcess.instMemLoc + pc));
			executeInstruction(binaryString);
		}

		// TODO: Do we need any debugging or print outs here?
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
		int opcode = getOpcode(binaryString);
		int reg1 = getArithSReg1(binaryString);
		int reg2 = getArithSReg2(binaryString);
		int dReg = getArithDReg(binaryString);

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
			if (registers[reg2] <= 0)
				System.out.println("Divide by zero!!!"); // Probably need to add more information, like a memory dump or something...?
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
		int opcode = getOpcode(binaryString);
		int bReg = getCondBReg(binaryString);
		int dReg = getCondDReg(binaryString);
		int address = getCondAddress(binaryString);

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
			RAM.instance().write(RAM.hexFormat(Integer.toHexString(registers[bReg])), address);
			break;
		case 0x03: // LW
			registers[bReg] = Integer.parseInt(RAM.instance().read(address), 16);
			break;
		case 0x0B: // MOVI
			if (address != 0)
				registers[dReg] = address;
			else
				registers[bReg] = registers[dReg];
			break;
		case 0x0C: // ADDI
			registers[dReg] += address;
			break;
		case 0x0D: // MULI
			registers[dReg] *= address;
			break;
		case 0x0E: // DIVI
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
		int opcode = getOpcode(binaryString);
		int jumpAddress = getJumpAddress(binaryString);

		if (opcode == 0x14) {
			pc = jumpAddress; // TODO: Verify that this is correct
		}
	}

	private void doIO(String binaryString) {
		int opcode = getOpcode(binaryString);
		int reg1 = getIOReg1(binaryString);
		int reg2 = getIOReg2(binaryString);
		int address = getIOAddress(binaryString);
		// 00 RD I/O Reads content of I/P buffer into a accumulator
		// 01 WR I/O Writes the content of accumulator into O/P buffer

		switch (opcode) {
		case 0x00: // RD
			// TODO: Finish when you figure it out
			if (reg2 != 0) {
				registers[reg1] = registers[reg2];
			} else {
				registers[reg1] = Integer.parseInt(RAM.instance().read(currentProcess.instMemLoc + address), 16);
			}
			break;
		case 0x01: // WR
			// TODO: Finish when you figure it out
			if (reg2 != 0) {
				registers[reg2] = registers[reg1];
			} else {
				RAM.instance().write(Integer.toHexString(registers[reg1]), currentProcess.instMemLoc +  address);
			}
			break;
		}

		pc++;
	}

	private int getInstructionType(String binaryString) {
		return Integer.parseInt(binaryString.substring(0, 2), 2);
	}

	private int getOpcode(String binaryString) {
		return Integer.parseInt(binaryString.substring(2, 6), 2); // This may possibly be wrong... Double check this
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
		return Integer.parseInt(binaryString.substring(16, 32), 2);
	}

	// UnConditional Jump opcode stuff
	private int getJumpAddress(String binaryString) {
		return Integer.parseInt(binaryString.substring(8, 24), 2);
	}

	// Input and Output opcode stuff
	private int getIOReg1(String binaryString) {
		return Integer.parseInt(binaryString.substring(8, 12), 2);
	}

	private int getIOReg2(String binaryString) {
		return Integer.parseInt(binaryString.substring(12, 16), 2);
	}

	private int getIOAddress(String binaryString) {
		return Integer.parseInt(binaryString.substring(16, 32), 2);
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
