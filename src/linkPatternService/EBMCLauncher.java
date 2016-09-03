package linkPatternService;

public class EBMCLauncher {
	
	public static int[] getEBMCResult(double costOfSet[],double weightOfElement[],double Budget,int indexOfSet[],int indexOfElement[],double membershipValue[])
	{
		EbmcAlgrithm a=new EbmcAlgrithm();
		a.SetN(costOfSet.length);
		a.SetM(weightOfElement.length);
		a.SetL(Budget);
		a.SetWeight(costOfSet);
		double profit[]=new double[indexOfElement.length];
		for(int i=0;i<profit.length;i++)
			profit[i]=membershipValue[i]*weightOfElement[indexOfElement[i]];
		a.SetProfit(indexOfSet, indexOfElement, profit);
		return a.getSelectID();
	}
}

