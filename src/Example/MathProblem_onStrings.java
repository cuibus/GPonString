package Example;


import GPonString.Configuration;
import GPonString.Constants;
import GPonString.GP;
import GPonString.GPUtils;
import GPonString.IFitnessFunction;
import GPonString.Operator;

public class MathProblem_onStrings {
	public static final int NR_GENERATIONS = 200;
	public static final Constants constants = new Constants();
	public static void main(String[] args) {
		Configuration c = new Configuration();
		c.constants = constants;
		FitnessFunc ff = new FitnessFunc();
		c.setFitnessFunction(ff);
		
		GP gp = new GP(c);
		gp.initializePopulation();
		for (int i=0;i<NR_GENERATIONS;i++) {
			gp.evolve();
			String fittest = gp.getFittestChromosome();
			System.out.println(i + ". Fitness: " + ff.evaluate(fittest) + " " + fittest);
		}	
		
		//System.out.println(ff.evaluate("(+|(^|0|x)|(^|0|x))"));
	}
	
}
class FitnessFunc implements IFitnessFunction{
	GPUtils utils = new GPUtils(MathProblem_onStrings.constants);
	final int[] xValues = {0, 1, 2, 3, 4, 5};
	final int[] yValues = {1, 4, 5, 0, 9, 11 };	//we aim for 2x+1
	public double evaluate(String chr) {
		double performance = 0;
		for (int i=0;i<xValues.length;i++) {
			double chrValue = SymbolicRegression(chr, xValues[i]);
			if (Double.isNaN(chrValue))
				performance += 1000;
			else
				performance += Math.abs(chrValue - yValues[i]);
		}
		performance += utils.getNrNodes(chr);
		return performance;
	}
	
	public double SymbolicRegression(String s, double x) {
		if (utils.isLeaf(s))
			if (s.equals("x")) return x;
			else return Integer.parseInt(s);
		else {
			Operator op = utils.getOperator(s);
			double firstValue = SymbolicRegression(utils.getFirstSubtree(s), x);
			//System.out.println("first: " + firstValue + " " + utils.getFirstSubtree(s));
			double secondValue = op.numChildren == 2 ? SymbolicRegression(utils.getSecondSubtree(s), x) : 0; //if only one child, this value is not relevant
			if (Double.isNaN(firstValue) || Double.isNaN(secondValue))
				return Double.NaN;
			
			switch (op.operator) {
				case "+" : return firstValue + secondValue;
				case "-" : return firstValue - secondValue;
				case "*" : return firstValue * secondValue;
				case "/" : return (secondValue != 0) ? firstValue / secondValue : Double.NaN;
				case "^" : return Math.pow(firstValue, secondValue);
				case "sin" : return Math.sin(firstValue);
				case "cos" : return Math.cos(firstValue);
				case "ln" : return firstValue > 0 ? Math.log(firstValue) : Double.NaN;
				default: return Double.NaN;				
			}
		}
		
	}
}
