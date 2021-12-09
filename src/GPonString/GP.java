package GPonString;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class GP {
	ArrayList<StringBuilder> population;
	Configuration conf;
	private GPUtils utils;
	public GP(Configuration conf) {
		this.conf = conf;
		this.utils = new GPUtils(conf.constants);
	}
	public void initializePopulation() {
		population = new ArrayList<StringBuilder>();
		for (int i=0;i<conf.PopulationSize;i++)
			population.add(utils.generateRandom(conf.maxChromosomeDepth));
	}
	public void evolve() {
		this.evaluateAll();
		
		ArrayList<StringBuilder> newPop = new ArrayList<StringBuilder>();
		if (conf.PreserveFittestIndividual)
			newPop.add(population.get(0));
		
		newPop.addAll(this.mutation());
		newPop.addAll(this.crossover());
		
		for (int i=newPop.size();i<conf.PopulationSize;i++) {
			newPop.add(utils.generateRandom(conf.maxChromosomeDepth));
		}
		
		population = newPop;
	}
	
	public String getFittestChromosome() {
		this.evaluateAll();
		return population.get(0).toString();
				
	}
	
	private double[] partialPerformances;
	private void evaluateAll(){
		// evaluate performance
		HashMap<StringBuilder, Double> performance = new HashMap<>();
		for (int i=0;i<population.size();i++)
			performance.put(population.get(i), conf.fitnessFunction.evaluate(population.get(i).toString()));
		
		// sort population
		Collections.sort(population, new Comparator<StringBuilder>() {
			public int compare(StringBuilder chr1, StringBuilder chr2) {
				//System.out.println("compare: " + performance.get(chr1) + " " + performance.get(chr2) + ", chr1: " + chr1 + ", chr2: " + chr2);
				if (performance.get(chr1) == performance.get(chr2))
					return 0;
				else return performance.get(chr1) < performance.get(chr2) ? -1 : 1;
			}
		});
		/*
		for (int i=0;i<population.size();i++) {
			System.out.println("perf: " + performance.get(population.get(i)) + " " + population.get(i));
		}
		*/
		// prepare roullete wheel selection
		partialPerformances = new double[population.size()];
		partialPerformances[0] = performance.get(population.get(0));
		for (int i=1;i<population.size();i++)
			partialPerformances[i] = partialPerformances[i-1] + performance.get(population.get(i));
	}
	private StringBuilder selectOne() {
		double perfIndex = Math.random() * partialPerformances[population.size()-1];
		int index = 0;
		while (perfIndex > partialPerformances[index])
			index++;
		return population.get(index);
	}
	
	private ArrayList<StringBuilder> mutation() {
		ArrayList<StringBuilder> mutated = new ArrayList<StringBuilder>();
		for (int i=0;i<conf.PopulationSize*conf.mutationRatio;i++) {
			StringBuilder chr = GPUtils.copy(selectOne());
			utils.mutate(chr);
			mutated.add(chr);
		}
		return mutated;
	}
	private ArrayList<StringBuilder> crossover() {
		ArrayList<StringBuilder> crossovered = new ArrayList<StringBuilder>();
		for (int i=0;i<conf.PopulationSize*conf.crossoverRatio;i++) {
			StringBuilder chr1 = GPUtils.copy(selectOne());
			StringBuilder chr2 = GPUtils.copy(selectOne());
			StringBuilder[] children = utils.crossover(chr1, chr2);
			crossovered.add(children[0]);
			crossovered.add(children[1]);
		}
		return crossovered;
	}
}

