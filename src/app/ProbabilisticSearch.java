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

import java.util.ArrayList;
import java.util.Scanner;

public class ProbabilisticSearch {

	static boolean moving = true;
	static CellDetails[][] landscape; //Data Structure containing the grid of land
	static double[][] findingTargetProb;
	static int dimension = 50; //length and width of the grid
	static int rowTarget, colTarget; //row and column in which the target is located
	static int maximumSearchTime = dimension*dimension*52; //upperbound for maximum number of searches
	static int currentSearch; //current search iteration
	static String targetMove = "";
	
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.println("Enter 1 for Stationary Target, Enter 2 for Moving Target, Enter 3 for Simulation, Enter any other number to quit.");
		int option = in.nextInt();
		int option2 = 0;
		if (!(option == 1 || option == 2 || option == 3)){
			System.exit(0);
		}
		if (option == 1) {
			System.out.println("Enter 1 for Rule 1, Enter 2 for Rule 2, Enter any other number to quit.");
			option2 = in.nextInt();
		}
		
		if (option == 3){ //Simulate 100 grids for each Rule, calculate average number of searches
			int rule1Count = 0;
			int rule2Count = 0;
			int total1 = 100;
			int total2 = 100;
			
			for (int i = 0; i<100; i++) {
				System.out.println("Rule 1, iteration: " + (i+1));
				landscape = new CellDetails[dimension][dimension];
				populateLandscape();
				currentSearch = 1;
				
				while(currentSearch < maximumSearchTime) { //continues to search until target is found or 10000 cells searched
					int[] XY = pickNext();
					if(chkLandscape(XY[0],XY[1])) {
						break;
					} else {
						reCalcProb(XY[0], XY[1]);
					}
					++currentSearch;
				}
				
				if (currentSearch < maximumSearchTime) {
					System.out.println("Search Time: " + currentSearch);
					rule1Count = rule1Count + currentSearch;
				} else {
					System.out.println("Search Time exceeded.");
					total1 = total1-1;
				}
			}

			currentSearch = 1;
			
			for (int i = 0; i< 100; i++) {
				System.out.println("Rule 2, iteration: " + (i+1));
				landscape = new CellDetails[dimension][dimension];
				populateLandscape();
				currentSearch = 1;
				findingTargetProb = new double[dimension][dimension];
				
				while(currentSearch < maximumSearchTime) { //continues to search until target is found or 10000 cells searched
					int[] XY = pickNextRule2();
					if(chkLandscape(XY[0],XY[1])) {
						break;
					} else {
						reCalcProb(XY[0], XY[1]);
					}
					++currentSearch;
				}
				
				if (currentSearch < maximumSearchTime) {
					System.out.println("Search Time: " + currentSearch);
					rule2Count = rule2Count + currentSearch;
				} else {
					System.out.println("Search Time exceeded.");
					total2 = total2-1;
				}
			}
			
			System.out.println("Rule 1 Average: " + (double)rule1Count/1000);
			System.out.println("Rule 2 Average: " + (double)rule2Count/1000);
			System.exit(0);
			
		} 
		
		landscape = new CellDetails[dimension][dimension];
		populateLandscape();
		printLandscape();
		currentSearch = 1;
		
		if (option == 1) {
			if (option2 == 1) {
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
			} else if (option2 == 2) {
				findingTargetProb = new double[dimension][dimension];
				
				while(currentSearch < maximumSearchTime) { //continues to search until target is found or 10000 cells searched
					int[] XY = pickNextRule2();
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
				
			} else {
				System.exit(0);
			}
			
		} else if (option == 2) {
			while(currentSearch < maximumSearchTime) { //continues to search until target is found or 10000 cells searched
				int[] XY;
				if(moving)
					XY = pickMovingNext();
				else
					XY = pickNext();
				
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
		
		if(currentSearch >= maximumSearchTime) {
			System.out.println("Couldn't find the target after "+maximumSearchTime+" number of moves..");
		}
		in.close();

	}
	
	/*
	 * Function to randomly populate the Grid of land, as well as randomly assign target
	 */
	public static void populateLandscape() {
		char type;
		double probForFind;
		double relativeProb = (double)(1.0/(dimension*dimension));
		rowTarget = (int) (Math.random() * (dimension-1));
		colTarget = (int) (Math.random() * (dimension-1));
		System.out.println("Target Location :: "+(rowTarget+1)+"-"+(colTarget+1));
		int lCount = 0, hCount = 0, fCount = 0, cCount = 0;
		for(int i = 0; i < dimension; i++) {
			for(int j=0; j < dimension; j++) {
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
		for(int i = 0; i < dimension; i++) {
			if(i < 10)
				System.out.print((i+1)+"      ");
			else
				System.out.print((i+1)+"     ");
		}
		System.out.println();
		for(int i = 0; i < dimension; i++) {
			if(i < 9){
				System.out.print((i+1)+"  ");	
			} else {
				System.out.print((i+1)+" ");
			}
			
			for(int j=0; j < dimension; j++) {
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
		for(int i=0; i<dimension; i++) {
			for(int j=0; j<dimension; j++) {
				//System.out.println(landscape[i][j].relativeProb[currentSearch-1]);
				if(nextCell < landscape[i][j].relativeProb) {
					nextCell = landscape[i][j].relativeProb;
					XY = new int[]{i,j};
				}
			}
		}
		return XY;
	}
	
	public static int[] pickNextRule2() {
		int[] XY = new int[2];
		double nextCell=0.0;
		for(int i=0; i<dimension; i++) {
			for(int j=0; j<dimension; j++) {
				double scale = 0.0;
				if (landscape[i][j].type == 'L') {
					scale = 0.9; 
				} else if (landscape[i][j].type == 'H') {
					scale = 0.7;
				} else if (landscape[i][j].type == 'F') {
					scale = 0.3;
				} else if (landscape[i][j].type == 'C') {
					scale = 0.1;
				}
				findingTargetProb[i][j] = scale*landscape[i][j].relativeProb;
				if(nextCell < findingTargetProb[i][j]) {
					nextCell = findingTargetProb[i][j];
					XY = new int[]{i,j};
				}
			}
		}
		return XY;
	}
	
	public static int[] pickMovingNext() {
		int[] XY = new int[2];
		String move = move();
		XY = getNext(move);
		
		return XY;
	}
	
	public static int[] getNext(String move) {
		int[] XY = new int[2];
		
		if(targetMove == "") {
			targetMove = move;
		} else {
			targetMove += "-"+move.split("-")[1];
		}
		System.out.println("Move "+move+"  -  "+targetMove);
		String[] trackTarget = targetMove.split("-");
		int i=0;
		ArrayList<int[]> location = find(trackTarget[i]);
		while(! location.isEmpty() && ++i<trackTarget.length) {
			location = findNext(trackTarget,location,i);
		}
		
		for(int j=0; j<location.size(); j++) {
			if(j == 0) {
				XY = location.get(0);
			}
			int[] loc = new int[2];
			loc = location.get(j);
			
			if(landscape[XY[0]][XY[1]].relativeProb < landscape[loc[0]][loc[1]].relativeProb) {
				XY = loc;
			}
		}
		
		return XY;
	}
	
	public static ArrayList<int[]> find(String locate) {
		ArrayList<int[]> loc = new ArrayList<int[]>();
		for(int i=0; i<dimension; i++) {
			for(int j=0; j<dimension; j++) {
				if(landscape[i][j].type == locate.charAt(0)){
					loc.add(new int[] {i,j});
				}
			}
		}
		
		return loc;
	}
	
	public static ArrayList<int[]> findNext(String[] trackTarget, ArrayList<int[]> location, int pos) {
		ArrayList<int[]> targetLoc = new ArrayList<int[]>();
		char type = trackTarget[pos].charAt(0);
		
		for(int i=0; i<location.size(); i++) {
			int row = location.get(i)[0];
			int col = location.get(i)[1];
			
			if(row>0 && landscape[row-1][col].type == type) {
				targetLoc.add(new int[] {row-1,col});
			} 
			if(row<dimension-1 && landscape[row+1][col].type == type) {
				targetLoc.add(new int[] {row+1,col});
			} 
			if(col>0 && landscape[row][col-1].type == type) {
				targetLoc.add(new int[] {row,col-1});
			} 
			if(col<dimension-1 && landscape[row][col+1].type == type) {
				targetLoc.add(new int[] {row,col+1});
			}
		}
		
		return targetLoc;
	}
	public static String move() {
		
		double rand = Math.random();
		int tempCol=colTarget, tempRow=rowTarget;
		
		if(rowTarget > 0 && rowTarget < dimension-1 && colTarget > 0 && colTarget < dimension-1) {
			if(rand <= 0.25) {
				tempRow = rowTarget-1;
			} else if(rand > 0.25 && rand <= 0.5) {
				tempCol = colTarget-1;
			} else if(rand > 0.5 && rand <= 0.75) {
				tempCol = colTarget+1;
			} else if(rand > 0.75 ) {
				tempRow = rowTarget+1;
			}
		} else if(rowTarget > 0 && rowTarget < dimension-1) {
			if(rand <= 0.33 && colTarget == 0) {
				tempCol = colTarget+1;
			} else if(rand <= 0.33 && colTarget == dimension-1) {
				tempCol = colTarget-1;
			} else if(rand > 0.33 && rand <= 0.66) {
				tempRow = rowTarget-1;
			} else {
				tempRow = rowTarget+1;
			}
		} else if(colTarget > 0 && colTarget < dimension-1) {
			if(rand <= 0.33 && rowTarget == 0) {
				tempRow = rowTarget+1;
			} else if(rand <= 0.33 && rowTarget == dimension-1) {
				tempRow = rowTarget-1;
			} else if(rand > 0.33 && rand <= 0.66) {
				tempCol = colTarget-1;
			} else {
				tempCol = colTarget+1;
			}
		} else {
			if(rowTarget == 0 && colTarget == 0) {
				if(rand <= 0.5) {
					tempRow = rowTarget+1;
				} else {
					tempCol = colTarget+1;
				}
			} else if(rowTarget == 0 && colTarget == dimension-1) {
				if(rand <= 0.5) {
					tempRow = rowTarget+1;
				} else {
					tempCol = colTarget-1;
				}
			} else if(rowTarget == dimension-1 && colTarget == 0) {
				if(rand <= 0.5) {
					tempRow = rowTarget-1;
				} else {
					tempCol = colTarget+1;
				}
			} else {
				if(rand <= 0.5) {
					tempRow = rowTarget-1;
				} else {
					tempCol = colTarget-1;
				}
			}
		}
		
		landscape[rowTarget][colTarget].target = false;
		char initialType = landscape[rowTarget][colTarget].type;
		rowTarget = tempRow; colTarget = tempCol;
		landscape[rowTarget][colTarget].target = true;
		char finalType = landscape[rowTarget][colTarget].type;
		System.out.println("Moved :: "+(rowTarget+1)+"-"+(colTarget+1));
		return initialType+"-"+finalType;
	}
	
	/*
	 * Function to recalculate relative probabilities of containing the target after a search has completed 
	 */
	public static void reCalcProb(int row, int col) {
		double relProb = landscape[row][col].relativeProb;
		double probForFind = landscape[row][col].probForFind;
		double overallProb = 1-relProb;
		
		for(int i=0; i<dimension; i++) {
			for(int j=0; j<dimension; j++) {
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