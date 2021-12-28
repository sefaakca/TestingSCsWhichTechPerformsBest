package mainpackage;
import abianalyser.AnalyseSolidityAbiFile;
import abianalyser.InputsByTransaction;
import abianalyser.OffSpring;
import abianalyser.Population;
import abianalyser.StructType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import InitializeandUpdatePopulation.InitializeandUpdatePopulation;



public class Start_GA {
	
	public static Population[] population;
	public static ArrayList<Population> currentpopulation = new ArrayList<Population>();
	public static JSONArray abiListRef= new JSONArray();
	public static ArrayList<StructType> inputList = new ArrayList<StructType>();
	public static StructType st = new StructType();
	static int it_counter =0;
	
	
	 public static Logger logger = Logger.getLogger("MyLog");  
	 public static FileHandler fh;
	
	//public static AnalyseSolidityAbiFile analyseabifile = new AnalyseSolidityAbiFile();
	public static InitializeandUpdatePopulation _initialisepop = new InitializeandUpdatePopulation();
	
	public static void main(String[] args) throws Throwable, IOException 
	{
		//JSON parser object to parse read file
		
		if(args.length<9)
		{
			System.out.println("Please give all necessary inputs !!");
			return;
		}
		
		//User inputs ************
		String AbifileName = args[0];
		String contractName = args[1];
		Path bytecodesPath = Paths.get(args[2]);
		int pop_size = Integer.parseInt(args[3]);
		int DESIRED_MIN_COVERAGE = Integer.parseInt(args[4]);
		int MAX_ITERATION = Integer.parseInt(args[5]);
		Path scriptFilePath= Paths.get(args[6]);
		double strtoDoubleMu = Double.parseDouble(args[7]);
		int mutationRate = (int) (strtoDoubleMu*100);
		double strtoDoubleCro = Double.parseDouble(args[8]);
		int crossoverRate = (int) (strtoDoubleCro*100);
		Path opcodePath = Paths.get(args[9]);
		String output_file = args[10];
		//*******************************************************
		String[] splittedString=null;
		try{
			
			java.util.List<String> opcodes = Files.readAllLines(opcodePath);
			for (String string : opcodes) {
				splittedString =string.split(" ");
			}
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		int totalNumberofInstruction = splittedString.length;
		//totalNumberofInstruction = totalNumberofInstruction*10;
		population = new Population[pop_size];
		
		fh = new FileHandler("../Genetic_Algorithm_Time.log",true);  
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();  
        fh.setFormatter(formatter);  
        System.out.println("GA STARTED");
		//timer started
		long startTime = System.currentTimeMillis();
				
		currentpopulation = _initialisepop.GenerateFirstRandomPopulation(AbifileName,contractName,bytecodesPath,pop_size, population, currentpopulation);
		
		currentpopulation = AnalyseSolidityAbiFile.CalculateFitnessValue(currentpopulation,scriptFilePath,totalNumberofInstruction,opcodePath);
        boolean is_stop = true;
        do {
        	
            //Sort current population
            Collections.sort(currentpopulation,Population.CoverageComparator);
                  
            Population[] selectedparents = AnalyseSolidityAbiFile.selectParentByElite(currentpopulation);
                
            OffSpring[] generated_offspring = AnalyseSolidityAbiFile.apply_single_point_crossover(selectedparents[0], selectedparents[1],scriptFilePath,crossoverRate,mutationRate,totalNumberofInstruction,opcodePath);
            //System.out.println(generated_offspring[0].transactions);
            //System.out.println(generated_offspring[1].transactions);
    
            currentpopulation = AnalyseSolidityAbiFile.compare_parent_vs_offspring(currentpopulation, selectedparents, generated_offspring, pop_size);
            it_counter++;
            is_stop = AnalyseSolidityAbiFile.check_stop_conditions(currentpopulation, DESIRED_MIN_COVERAGE, MAX_ITERATION,it_counter);
            System.out.println("Iteration_Count ON EACH: " + it_counter);
        }while(is_stop == true);
      //timer stopped
        long stopTime = System.currentTimeMillis();
        _initialisepop.FinalWrite(currentpopulation.get(0),output_file);
        long elapsedTime = stopTime - startTime;
        System.out.println("TIME TAKEN: " + elapsedTime);
        logger.info(contractName + ": " + elapsedTime);
        System.out.println("BEST COVERAGE RATE AFTER GA APPLIED:" + currentpopulation.get(0).coverage);
        System.out.println("Iteration_Count: " + it_counter);
        System.out.println("DONE WITHOUT EXCEPTION");
	}

}
