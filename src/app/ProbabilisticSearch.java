package app;

public class ProbabilisticSearch {

	static CellDetails[][] landscape;
	static int length = 10, width = 10;
	static int rowTarget,colTarget;
	
	public static void main(String[] args) {
		landscape = new CellDetails[length][width];
		populateLandscape();
		printLandscape();
		int count = 1;
		while(true) {
			int[] XY = pickNext();
			System.out.println("Checking ... "+(XY[0]+1)+" - "+(XY[1]+1)+" Count: "+count);
			if(chkLandscape(XY[0],XY[1])) {
				System.out.println("Target Found !!!! @ Row - "+(XY[0]+1)+" & Col - "+(XY[1]+1)+" Count: "+count);
				System.out.println("Actual Target Location :: "+(rowTarget+1)+"-"+(colTarget+1));
				System.out.println();
				break;
			} else {
				reCalcProb(XY[0], XY[1]);
			}
			++count;
		}
	}
	
	public static void populateLandscape() {
		char type;
		double probForFind;
		double relativeProb = (1/(length*width));
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
			}
		}
		
		System.out.println("Count :: Flat Land - "+lCount+" Hilly - "+hCount+" Forest - "+fCount+" Caves "+cCount);
	}
	
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
			System.out.print((i+1)+"  ");
			for(int j=0; j < width; j++) {
				System.out.print(landscape[i][j].toString()+"  ");
			}
			System.out.println();
		}
	}
	
	public static boolean chkLandscape(int row, int col) {
		if(landscape[row][col].target && landscape[row][col].probForFind >= Math.random()) {
			return true;
		} else {
			return false;
		}
	}
	
	public static int[] pickNext() {
		int[] XY = new int[2];
		double nextCell=0.0;
		for(int i=0; i<length; i++) {
			for(int j=0; j<width; j++) {
				if(nextCell<landscape[i][j].relativeProb) {
					nextCell = landscape[i][j].relativeProb;
					XY = new int[]{i,j};
				}
			}
		}
		
		return XY;
	}
	
	public static void reCalcProb(int row, int col) {
		double relProb = landscape[row][col].relativeProb;
		double probForFind = landscape[row][col].probForFind;
		double overallProb = 1-relProb;
		
		for(int i=0; i<length; i++) {
			for(int j=0; j<width; j++) {
					if(row!=i && col!=j) {
						landscape[i][j].relativeProb = landscape[i][j].relativeProb*(1+((relProb*(1-probForFind)/overallProb)));
					}
			}
		}
		landscape[row][col].relativeProb = relProb*probForFind;
	}
	
	static class CellDetails {
		char type;
		double probForFind;
		double relativeProb;
		boolean target;
		
		public CellDetails(char type, double prob, double relativeProb, boolean target) {
			this.type = type;
			this.probForFind = prob;
			this.relativeProb = prob;
			this.target = target;
		}
		
		public String toString() {
			return type+"-"+probForFind;
		}
	}
}