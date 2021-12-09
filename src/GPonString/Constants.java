package GPonString;


public class Constants{
	public Operator[] operators = new Operator[] {
			new Operator("+", 2),
			new Operator("-", 2),
			new Operator("*", 2),
			new Operator("/", 2),
			new Operator("^", 2),
			new Operator("ln", 1),
			new Operator("sin", 1),
			new Operator("cos", 1)};
	public String[] leafs = new String[] {"0", "1", "2", "3", "4", "5", "x"};
	
	public Operator getRandomOperatorExcept(String operator) {
		//TODO check this, it doesnt work properly
		int i = (int)(Math.random() * operators.length-1);
		return operators[i].operator.equals(operator) ?
			operators[i+1] : operators[i];
	}
	public Operator getRandomOperator() {
		int i = (int)(Math.random() * operators.length);
		return operators[i];
	}
	public int getNumChildren(String operator) {
		//TODO optimize this
		for (Operator op : operators) {
			if (op.operator.equals(operator))
				return op.numChildren;
		}
		throw new RuntimeException("Operator " + operator + "not found");
	}
	public String getRandomLeaf() { return leafs[(int)(Math.random() * leafs.length)]; }
	
	public double probabilityToMutateOperator = 0.4;
}


class EnhancedPetriConstants extends Constants{
	private String[] leftLeafs = new String[] {"r0", "r1", "r2"};
	private String[] rightLeafs = new String[] {"c0", "c1", "c2"};
	
	public EnhancedPetriConstants() {
		this.leafs = new String[leftLeafs.length * rightLeafs.length];
		for (int i=0;i<leftLeafs.length;i++)
			for (int j=0;j<rightLeafs.length;j++)
				this.leafs[i*rightLeafs.length+j] = leftLeafs[i]+rightLeafs[j];
	}
	/*
	public String getRandomLeftLeaf() { return leftLeafs[(int)(Math.random() * leftLeafs.length)]; }
	public String getRandomRightLeaf() { return rightLeafs[(int)(Math.random() * rightLeafs.length)]; }
	public double probabilityToMutateLeftLeaf = 0.3;
	public double probabilityToMutateRightLeaf = 0.3;
	*/
}
