package app;

public class ProbabilisticSearch {

	static CellDetails[][] landscape;
	static int length = 50, width = 50;
	
	public static void main(String[] args) {
		landscape = new CellDetails[length][width];
		populateLandscape();
		printLandscape();
		
	}
	
	public static void populateLandscape() {
		char type;
		double probForFind;
		int rowTarget = (int) (Math.random() * length);
		int colTarget = (int) (Math.random() * width);
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
					landscape[i][j] = new CellDetails(type,probForFind,true);
				else
					landscape[i][j] = new CellDetails(type,probForFind,false);
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
	
	static class CellDetails {
		char type;
		double probForFind;
		boolean target;
		
		public CellDetails(char type, double prob, boolean target) {
			this.type = type;
			this.probForFind = prob;
			this.target = target;
		}
		
		public String toString() {
			return type+"-"+probForFind;
		}
	}
	
	
}
