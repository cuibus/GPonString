package GPonString;


public class Configuration {
	public boolean PreserveFittestIndividual = true;
	public int PopulationSize = 50;
	public IFitnessFunction fitnessFunction;
	public int maxChromosomeDepth = 5;
	public double mutationRatio = 0.4;
	public double crossoverRatio = 0.4;
	public Constants constants = new Constants();
	
	public void setFitnessFunction(IFitnessFunction fitnessFunction) {
		this.fitnessFunction = fitnessFunction;
	}
}
