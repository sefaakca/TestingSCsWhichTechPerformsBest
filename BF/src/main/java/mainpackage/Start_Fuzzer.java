package mainpackage;

import abianalyser.AnalyseAbiFile;


import java.nio.file.Path;
import java.nio.file.Paths;





public class Start_Fuzzer 
{
	public static AnalyseAbiFile gtf = new AnalyseAbiFile();
	public static long startTime = System.currentTimeMillis();
	
	
	
    @SuppressWarnings("static-access")
	public static void main( String[] args )
    {
    	if(args.length<3)
		{
			System.out.println("Please give all necessary inputs !!");
			return;
		}
		
		//User inputs ************
		String AbifileName = args[0];
		String contractName = args[1];
		Path bytecodesPath = Paths.get(args[2]);
		String output_file = args[3];
		//*******************************************************
		
		gtf.GenerateRandomInput(AbifileName, contractName, bytecodesPath, output_file,startTime);
    }
}
