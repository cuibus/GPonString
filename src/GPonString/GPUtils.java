package GPonString;


public class GPUtils {
	Constants c;
	public GPUtils(Constants c) {
		this.c = c;
	}

	public StringBuilder generateRandom(int maxdepth) {
		int depth = (int)(Math.random() * maxdepth) + 1;
		Operator op = c.getRandomOperator();
		StringBuilder result = new StringBuilder("(").append(op.operator).append("|");
		if (depth == 1) {
			result.append(c.getRandomLeaf());
			if (op.numChildren == 2)
				result.append("|").append(c.getRandomLeaf());
		}
		else {
			switch (op.numChildren) {
				case 1: result.append(generateRandom(maxdepth-1));break;
				case 2: result.append(generateRandom(maxdepth-1)).append("|").append(generateRandom(maxdepth-1));break;
				default: break;
			}
		}
		result.append(")");
		return result;
	}
	
	public void mutate(StringBuilder s) {
		// TODO: pt operator: toti operatorii au sansa egala de mutatie. Acuma nu au
		// TODO: pt frunza: toate frunzele au sanse egale de mutatie. Acuma nu au
		double r = Math.random();
		if (r < c.probabilityToMutateOperator) {
			int positionToStartSearching = (int)(Math.random() * s.lastIndexOf("("));
			int operatorPos = s.indexOf("(", positionToStartSearching) + 1;
			int separatorPos = s.indexOf("|", operatorPos);
			String oldOperator = s.substring(operatorPos, separatorPos);
			Operator newOperator = c.getRandomOperatorExcept(oldOperator);
			if (c.getNumChildren(oldOperator) == 1 && newOperator.numChildren == 2) {
				int insertPos = getClosingPosition(s, operatorPos);
				s.insert(insertPos, "|");
				s.insert(insertPos+1, c.getRandomLeaf());
			}
			if (c.getNumChildren(oldOperator) == 2 && newOperator.numChildren == 1) {
				int closePos = getClosingPosition(s, operatorPos);
				int secondOpPos = getSecondOperandPosition(s,  operatorPos);
				s.replace(secondOpPos, closePos,  "");
			}
			s.replace(operatorPos, separatorPos, newOperator.operator);
		}
		else {
			int positionToStartSearching = (int)(Math.random() * s.lastIndexOf("|"));
			int pos = findNextLeafPos(s, positionToStartSearching);
			int nextPipe = s.indexOf("|", pos);
			int endPos = Math.min(s.indexOf(")", pos), nextPipe == -1 ? s.length() : nextPipe);
			s.replace(pos, endPos, c.getRandomLeaf());
		}
	}
	
	public StringBuilder[] crossover(StringBuilder s1, StringBuilder s2) {
		int[] positions1 = getLeafOrSubtree(s1);
		int[] positions2 = getLeafOrSubtree(s2);
		String subgroup1 = s1.substring(positions1[0], positions1[1]); 
		//System.out.println("Subgroup1: " + subgroup1);
		String subgroup2 = s2.substring(positions2[0], positions2[1]); 
		//System.out.println("Subgroup2: " + subgroup2);
		
		StringBuilder child1 = s1.replace(positions1[0], positions1[1], subgroup2);
		StringBuilder child2 = s2.replace(positions2[0], positions2[1], subgroup1);
		return new StringBuilder[] {child1, child2};
	}
	
	private int[] getLeafOrSubtree(StringBuilder s) {
		boolean leafNotSubtree = Math.random() < 0.5;
		if (leafNotSubtree) {
			int positionToStartSearching = (int)(Math.random() * s.lastIndexOf("|"));
			int startGroupPos = findNextLeafPos(s, positionToStartSearching);
			int endGroupPos = startGroupPos;
			while (s.charAt(endGroupPos) != '|' && s.charAt(endGroupPos) != ')' && endGroupPos < s.length())
				endGroupPos++;
			return new int[] {startGroupPos, endGroupPos};
		}
		else {
			int positionToStartSearching = (int)(Math.random() * s.lastIndexOf("("));
			int startGroupPos = s.indexOf("(", positionToStartSearching);
			int endGroupPos = getClosingPosition(s, startGroupPos) + 1;
			return new int[] {startGroupPos, endGroupPos};
		}
	}
	
	private int findNextLeafPos(StringBuilder s, int positionToStartSearching) {
		int pos = positionToStartSearching;
		do {
			pos = s.indexOf("|", pos+1);
		} while (pos != -1 && s.charAt(pos+1) == '('); // find this pattern: | followed by anything else than '(' => we found a leaf
		pos++; // go to position after pipe
		return pos;
	}
	
	private int getClosingPosition(StringBuilder s, int pos) {
		// returns the position of the first corresponding closing bracket after pos. Pos must be on operator.
		int numOpenBrackets = 1;
		while (pos < s.length() && numOpenBrackets > 0) {
			pos++;
			if (s.charAt(pos) == '(')
				numOpenBrackets++;
			else if (s.charAt(pos) == ')')
				numOpenBrackets--;
		}
		return s.indexOf(")", pos);
	}
	private int getSecondOperandPosition(StringBuilder s, int pos) {
		// returns the position of the second operand after pos. Pos must be on operator.
		int numOpenBrackets = 0;
		pos = s.indexOf("|", pos);
		do {
			pos++;
			if (s.charAt(pos) == '(')
				numOpenBrackets++;
			else if (s.charAt(pos) == ')')
				numOpenBrackets--;
		} while (pos < s.length() && numOpenBrackets > 0);
		int indexPipe = s.indexOf("|", pos+1);
		return indexPipe != -1 ? indexPipe : s.indexOf(")", pos+1);
	}
	
	public Operator getOperator(String s) {
		int separatorPos = s.indexOf("|", 0);
		String operator = s.substring(1, separatorPos);
		for (int i=0;i<c.operators.length;i++)
			if (operator.equals(c.operators[i].operator))
				return c.operators[i];
		return null;
	}
	public String getFirstSubtree(String s) {
		int separatorPos = s.indexOf("|", 0);
		int secondOpPosition = getSecondOperandPosition(new StringBuilder(s),  1);
		return s.substring(separatorPos+1, secondOpPosition);
	}
	public String getSecondSubtree(String s) {
		int secondOpPosition = getSecondOperandPosition(new StringBuilder(s),  1);
		if (s.charAt(secondOpPosition) != '|')
			return ""; // there is no second operand
		else
			return s.substring(secondOpPosition+1, s.length()-1);
	}
	public boolean isLeaf(String s) {
		for (String leaf : c.leafs)
			if (leaf.equals(s))
				return true;
		return false;
	}
	
	public int getNrNodes(String s) {
		if (isLeaf(s)) return 1;
		else {
			int nrNodes = 0;
			Operator op = getOperator(s);
			nrNodes += getNrNodes(getFirstSubtree(s));
			if (op.numChildren == 2)
				nrNodes += getNrNodes(getFirstSubtree(s));
			return nrNodes;
		}	
	}
	
	public static void main(String[] args) {
		Constants c = new Constants();
		GPUtils utils = new GPUtils(c);
		utils.c.probabilityToMutateOperator = 0;
		
		for (int i=0;i<20;i++) {
			StringBuilder s1 = utils.generateRandom(5);
			
			System.out.println(s1);
			System.out.println(utils.getFirstSubtree(s1.toString()));
			System.out.println(utils.getSecondSubtree(s1.toString()));
			System.out.println();
		}

		
	}

	public static StringBuilder copy(StringBuilder s) {
		return new StringBuilder(s.toString());
	}
}
//examples:
// (op|leftchild|rightchild)
// (op|ot1|ot2)
// (op|ot1|(op|(op|ot1|ot2)|ot2)) 

// op: operators: + - * / ^ ln sin
// ot: operands: 0 1 2 3 4 ... x y z
// use special operands: 
// 		leftleaf: r0 r1 r2 ...
//		rightleaf: c1,c2,c3