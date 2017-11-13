/*
================================================================================================================
Project - Probabilistic Search

Class for Probabilistic Search Assignment; 
Run to begin Probabilistic Search. First, a random map of Flat, Hilly, Forested, and Cave cells are populated
with the frequencies of 0.2, 0.3, 0.3, 0.2 respectively. A target cell is then randomly assigned, and the
probability of each cell containing the target is calculated, assuming the target is equally likely to be in
any cell. The searcher then proceeds to search cells in order of most likely to contain the target to least
likely.

Legend:
L - Flat Land
H - Hilly
F - Forested
C - Maze of Caves
================================================================================================================
*/

package app;

public class ProbabilisticSearch {

	static CellDetails[][] landscape; //Data Structure containing the grid of land
	static int length = 10, width = 10; //length and width of the grid
	static int rowTarget, colTarget; //row and column in which the target is located
	static int maximumSearchTime = length*width*100;
	static int currentSearch;
	
	public static void main(String[] args) {
		landscape = new CellDetails[length][width];
		populateLandscape();
		printLandscape();
		currentSearch = 1;
		while(currentSearch < maximumSearchTime) { //continues to search until target is found or 10000 cells searched
			int[] XY = pickNext();
			System.out.println("Checking ... "+(XY[0]+1)+" - "+(XY[1]+1)+" Count: "+currentSearch);
			if(chkLandscape(XY[0],XY[1])) {
				System.out.println("Target Found !!!! @ Row - "+(XY[0]+1)+" & Col - "+(XY[1]+1)+" Count: "+currentSearch);
				System.out.println("Actual Target Location :: "+(rowTarget+1)+"-"+(colTarget+1));
				System.out.println();
				break;
			} else {
				reCalcProb(XY[0], XY[1]);
			}
			++currentSearch;
		}
	}
	
	/*
	 * Function to randomly populate the Grid of land, as well as randomly assign target
	 */
	public static void populateLandscape() {
		char type;
		double probForFind;
		double relativeProb = (double)(1.0/(length*width));
		rowTarget = (int) (Math.random() * length);
		colTarget = (int) (Math.random() * width);
		System.out.println("Target Location :: "+(rowTarget+1)+"-"+(colTarget+1));
		int lCount = 0, hCount = 0, fCount = 0, cCount = 0;
		for(int i = 0; i < length; i++) {
			for(int j=0; j < width; j++) {
				double rand = Math.random();
				if(rand <= 0.2) {
					type = 'L';	// Flat Land
					probForFind = 0.9;
					++lCount;
				} else if(rand > 0.2 && rand <= 0.5) {
					type = 'H';	// Hilly Area
					probForFind = 0.7;
					++hCount;
				} else if(rand > 0.5 && rand <= 0.8) {
					type = 'F';	// Forest
					probForFind = 0.3;
					++fCount;
				} else {
					type = 'C';	// Caves and tunnels
					probForFind = 0.1;
					++cCount;
				}
				
				if(rowTarget == i && colTarget == j)
					landscape[i][j] = new CellDetails(type,probForFind,relativeProb,true);
				else
					landscape[i][j] = new CellDetails(type,probForFind,relativeProb,false);
				
				landscape[i][j].probBeliefOverTime[0] = landscape[i][j].relativeProb;
			}
		}
		
		System.out.println("Count :: Flat Land - "+lCount+" Hilly - "+hCount+" Forest - "+fCount+" Caves "+cCount);
	}
	
	/*
	 * Function to print the Grid of Land
	 */
	public static void printLandscape() {	
		System.out.print("   ");
		for(int i = 0; i < width; i++) {
			if(i < 10)
				System.out.print((i+1)+"      ");
			else
				System.out.print((i+1)+"     ");
		}
		System.out.println();
		for(int i = 0; i < length; i++) {
			if(i < 9){
				System.out.print((i+1)+"  ");	
			} else {
				System.out.print((i+1)+" ");
			}
			
			for(int j=0; j < width; j++) {
				System.out.print(landscape[i][j].toString()+"  ");
			}
			System.out.println();
		}
	}
	
	/*
	 * Searches a cell, returns true if the cell contains the target AND search was successful based on search probability 
	 */
	public static boolean chkLandscape(int row, int col) {
		if(landscape[row][col].target && landscape[row][col].probForFind >= Math.random()) {
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * Function to find the cell with the greatest likelihood of containing the target cell
	 */
	public static int[] pickNext() {
		int[] XY = new int[2];
		double nextCell=0.0;
		for(int i=0; i<length; i++) {
			for(int j=0; j<width; j++) {
				//System.out.println(landscape[i][j].relativeProb[currentSearch-1]);
				if(nextCell < landscape[i][j].relativeProb) {
					nextCell = landscape[i][j].relativeProb;
					XY = new int[]{i,j};
				}
			}
		}
		return XY;
	}
	
	/*
	 * Function to recalculate relative probabilities of containing the target after a search has completed 
	 */
	public static void reCalcProb(int row, int col) {
		double relProb = landscape[row][col].relativeProb;
		double probForFind = landscape[row][col].probForFind;
		double overallProb = 1-relProb;
		
		for(int i=0; i<length; i++) {
			for(int j=0; j<width; j++) {
					if(row!=i && col!=j) {
						landscape[i][j].probBeliefOverTime[currentSearch] = landscape[i][j].relativeProb;
						landscape[i][j].relativeProb = landscape[i][j].relativeProb*(1+((relProb*(1-probForFind)/overallProb)));
						//landscape[i][j].relativeProb = (1.0-relProb*probForFind)*landscape[i][j].relativeProb/overallProb;
						//given the target was not found in the searched cell
						//and the probability of finding the target if the target was in the cell,
						//evenly distributes the decrease in probability of the searched cell among the other cells 
					}
			}
		}
		landscape[row][col].probBeliefOverTime[currentSearch] = landscape[row][col].relativeProb;
		landscape[row][col].relativeProb = relProb*probForFind;
	}
	
	/*
	 * Class for the grid, contains cell type, probability for finding the target given the target is in the cell,
	 * relative probability of containing the target, an array containing all of the relative probabilities
	 * at time t, and finally a boolean for if the cell contains the target.
	 */
	static class CellDetails {
		char type;
		double probForFind;
		double relativeProb;
		double[] probBeliefOverTime;
		boolean target;
		
		public CellDetails(char type, double prob, double relativeProb, boolean target) {
			this.type = type;
			this.probForFind = prob;
			this.relativeProb = relativeProb;
			probBeliefOverTime = new double[maximumSearchTime];
			this.target = target;
		}
		
		public String toString() {
			return type+"-"+probForFind;
		}
	}
}