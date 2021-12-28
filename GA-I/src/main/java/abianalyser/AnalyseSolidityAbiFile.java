package abianalyser;

import java.awt.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import InitializeandUpdatePopulation.InitializeandUpdatePopulation;
import randomGenerator.AdressGenerator;
import randomGenerator.BooleanGenerator;
import randomGenerator.ByteGenerator;
import randomGenerator.IntegerGenerator;
import randomGenerator.StringGenerator;


public class AnalyseSolidityAbiFile 
{
	public static IntegerGenerator intgen = new IntegerGenerator();
	public static StringGenerator strgen = new StringGenerator();
	public static ByteGenerator bytgen = new ByteGenerator();
	public static BooleanGenerator boolgen = new BooleanGenerator();
	public static AdressGenerator addgen = new AdressGenerator();
	public static ArrayList<StructType> inputList = new ArrayList<StructType>();
	public static StructType st = new StructType();
	public static InitializeandUpdatePopulation _popin = new InitializeandUpdatePopulation();

	
	
	public static ArrayList<Population> CalculateFitnessValue(ArrayList<Population> pList, Path scriptFilePath,int totalNumberofInstruction,Path opcodePath) throws InterruptedException
	{

		//send separately
		for (Population population : pList) {
			java.util.List<String> test = _popin.PreperaforCalculation(population);
			String batchString=test.toString();
			
			HashMap<Integer,ArrayList<Float>> rest = RunCoverageScript(population.test_id, batchString,scriptFilePath,totalNumberofInstruction,opcodePath);

			population.coverage =Float.parseFloat(rest.get(population.test_id).get(0).toString());
			
		}
		
		return pList;

	}
	
	public static OffSpring CalculateOffSpringFitnessValue(OffSpring o,Path scriptFilePath, int totalNumberofInstruction, Path opcodePath) throws InterruptedException
	{
		java.util.List<String> test = _popin.PreperaforCalculation(o);
		String batchString=test.toString();
		HashMap<Integer,ArrayList<Float>> rest = RunCoverageScript(o.test_id,batchString,scriptFilePath,totalNumberofInstruction,opcodePath);
		
			o.coverage =Float.parseFloat(rest.get(o.test_id).get(0).toString());
			

		//calculate the coverage of offspring.
		//return Float.parseFloat(RunCoverageScript(transactionstr,scriptFilePath));
		return o;
	}
	
	
	
	
	public static Population[] selectParentByElite(ArrayList<Population> currentpop)
	{

		Population[] selectedparents = new Population[2];
		
		selectedparents[0]= currentpop.get(0);
		selectedparents[1] = currentpop.get(1);
		
		return selectedparents;
		

		
	}
	public static int generateUniqueID() {

		
		int uid  = (int)(System.currentTimeMillis()/100000);
		
		return uid;
	}
	
	@SuppressWarnings("unchecked")
	public static OffSpring[] apply_single_point_crossover(Population p1, Population p2,Path scriptFilePath, int crossoverRate, int mutationRate, int totalNumberofInstruction,Path opcodePath)
	{
		OffSpring[] os = new OffSpring[2];
		try {
			
			
			
			JSONArray holdtransactions_o1 = new JSONArray();
			JSONArray holdtransactions_o2 = new JSONArray();
			
			JSONArray transactionList_from_p1 =(JSONArray)p1.transactions.get("transactions");
			JSONArray transactionList_from_p2 =(JSONArray)p2.transactions.get("transactions");
			
			int transaction_count_from_p1 = (transactionList_from_p1.size()*crossoverRate)/100;
			
			//generate first offspring -o[0]
			//************************************************************
			for (int i = 0; i < transaction_count_from_p1; i++) 
				holdtransactions_o1.add(transactionList_from_p1.get(i));
			for(int s=transaction_count_from_p1; s<transactionList_from_p2.size();s++)
				holdtransactions_o1.add(transactionList_from_p2.get(s));
			 //**************END OF GENERATION O1 *******************
			
			
			//generate second offspring -o[1]
			//************************************************************
			for(int s = 0; s<transaction_count_from_p1;s++ )
				holdtransactions_o2.add(transactionList_from_p2.get(s));

            for(int s = transaction_count_from_p1; s<transactionList_from_p1.size();s++ )
            	holdtransactions_o2.add(transactionList_from_p1.get(s));
            //**************END OF GENERATION O2 *******************
            
            //JSONObject jsonobject = (JSONObject) holdtransactions_o1.get(0);
            //System.out.println(jsonobject.get("id").toString());
            
            //Generate first offspring from p1 & p2
            Random r = new Random();
    		
            os[0] = new OffSpring();
    		os[0].abi = p1.abi;
    		os[0].accounts = p1.accounts;
    		os[0].linkReferences = p1.linkReferences;
    		os[0].transactions = os[0].deepCopy(os[0].transactions, holdtransactions_o1);
    		os[0].nameofP1 = Integer.toString(p1.test_id);
    		os[0].nameofP2 = Integer.toString(p2.test_id);
    		os[0].test_id = generateUniqueID();//Integer.valueOf(os[0].nameofP1 + os[0].nameofP2);

    		//****************************************
    		
    		//Generate second offspring from p1 & p2
    		
    		os[1] = new OffSpring();
    		os[1].abi = p2.abi;
    		os[1].accounts = p2.accounts;
    		os[1].linkReferences = p2.abi;
    		os[1].transactions = os[1].deepCopy(os[1].transactions, holdtransactions_o2);
    		os[1].nameofP1 = Integer.toString(p2.test_id);
    		os[1].nameofP2 = Integer.toString(p1.test_id);
    		os[1].test_id = generateUniqueID();//Integer.valueOf(os[1].nameofP1 + os[1].nameofP2);

    		//****************************************
    		ArrayList<Integer> tidList = new ArrayList<Integer>();
    		for (OffSpring offspr : os) {
    			JSONArray JARR = (JSONArray) offspr.transactions.get("transactions");
        		Iterator<JSONObject> jit = JARR.iterator();
        		while(jit.hasNext())
        			tidList.add(Integer.valueOf(jit.next().get("id").toString()));

        		
        		offspr = apply_mutation(offspr,tidList,mutationRate);
        		OffSpring returnedOffSpring = new OffSpring();
        		
        		returnedOffSpring = CalculateOffSpringFitnessValue(offspr,scriptFilePath,totalNumberofInstruction,opcodePath);
        		
        		offspr.coverage = returnedOffSpring.coverage;
			}
    		

            
		}catch (Exception e) {
			// TODO: handle exception
		}
		return os;
		
	}
	
	
	@SuppressWarnings("unchecked")
	public static OffSpring apply_mutation(OffSpring mutant,ArrayList<Integer> transactionids,int mutationRate)
	{
		
		StructType stt = new StructType();
		int mutaion_rate = (int) Math.ceil((transactionids.size()*mutationRate)/100);
		for(int i =0; i<mutaion_rate;i++)
		{
			int max_rnd = transactionids.size();
			Random rnd = new Random();
			int mutated_transaction = transactionids.get(rnd.nextInt(max_rnd));
			
			JSONArray mutantthingy = (JSONArray)mutant.transactions.get("transactions");
			Iterator<JSONObject> obit = mutantthingy.iterator();
			while(obit.hasNext())
			{
				JSONObject current_obj = obit.next();
				int next = Integer.valueOf(current_obj.get("id").toString());
				if(next==mutated_transaction) {
					JSONObject records = (JSONObject) current_obj.get("record");
					String types  = (String) records.get("inputs");
					types = types.substring(1, types.length()-1);
					String[] values = types.split(",");
					stt = parseObjectInputs(values);
					
					
					
					records.replace("parameters", stt.inpValues);
					records.replace("from", "account{"+new Random().nextInt(5)+"}");
					stt.clear();
				}

			}
			
		}
		
		return mutant;
	}
	
	public static ArrayList<Population> compare_parent_vs_offspring(ArrayList<Population> currentPop, Population[] pops, OffSpring[] childs, int pop_size)
	{
		//This function will be improved
		try {
			for (OffSpring offSpring : childs) {
				for (Population each : currentPop) { //travel around all population
					if(offSpring.coverage >= each.coverage) {
						if(!(currentPop.contains(offSpring)))
							currentPop.add(offSpring);
					}
					else {
						if(!(currentPop.contains(each)))
							currentPop.add(each);
					}
						
				}
			}
			
			Collections.sort(currentPop,Population.CoverageComparator);
			
			do {
				if(currentPop.size()>pop_size) 
					currentPop.remove(currentPop.size()-1);	
				
			}while(currentPop.size() > pop_size);
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		
		return currentPop;
		
		
	}
	
	
	public static Boolean check_stop_conditions(ArrayList<Population> population, int DESIRED_MIN_COVERAGE, int MAX_ITERATION,int counter )
	{
		//This will check stop conditions.
		//FIRST STOP CONDITION IS COVERAGE -TAKEN BY USER INPUT
		//SECOND STOP CONDITION IS ITERATION COUNT -TAKEN BY USER INPUT
		for (Population population2 : population) {
			if(population2.coverage >= DESIRED_MIN_COVERAGE || counter >= MAX_ITERATION) {
				
				return false;
			}
				
		}
		
		return true;
	}
	
	
	public static HashMap<Integer, ArrayList<Float>> RunCoverageScript(int test_id, String inp_file, Path scriptFilePath, int totalNumberofInstruction,Path opcodePath) throws InterruptedException {
		
		HashMap<Integer, ArrayList<Float>> coverageRes = new HashMap<Integer, ArrayList<Float>>();
		try {
			
			
			String jsonformBatchStr = "";
			org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();
			try {
				JSONArray json = (JSONArray) parser.parse(inp_file);
				jsonformBatchStr = json.toJSONString();
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String filePath = scriptFilePath+"/scriptfile2.sh";
			String strsss = Integer.toString(totalNumberofInstruction);
			String opcodeString = opcodePath.toString();
			ProcessBuilder pb = new ProcessBuilder(filePath,jsonformBatchStr , strsss, opcodeString, "10");
			//Process s = Runtime.getRuntime().exec(scriptFilePath+"/scriptfile2.sh " + jsonformBatchStr + totalNumberofInstruction );
			//s.waitFor();
			Process s = pb.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			s.waitFor(10, TimeUnit.MINUTES);
			String output = "", line = "";
			while((line = br.readLine()) != null) {
				output += line;
			}
			System.out.println(output);
			if(output.isEmpty()) {
				ArrayList<Float> emptyArrList =new ArrayList<Float>();
				emptyArrList.add((float) 0.0);
				emptyArrList.add((float) 0.0);
				coverageRes.put(test_id,emptyArrList );
			} else {
				String[] pairs = output.split(" ");
				ArrayList<Float> arrList = new ArrayList<Float>();
				arrList.add(Float.parseFloat(pairs[pairs.length-3]));
				arrList.add(Float.parseFloat(pairs[pairs.length-1]));
				coverageRes.put(test_id,arrList);
				/*
				for (int i=0;i<pairs.length;i++) {
					 String pair = pairs[i];
					 String[] keyValue = pair.split(":");
					 coverageRes.put(Integer.valueOf(keyValue[0]), Float.parseFloat(keyValue[1]));
				}*/
			}
			//String to HashMap conversion
			
			System.out.println(coverageRes);
			
			return coverageRes;

				
			
		} catch (IOException   e) {//| InterruptedException
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return coverageRes;
	}
	


	public static StructType parseObjectInputs(JSONObject inputs) 
	{
		try {
			//Get input lists
			JSONArray inputsObject = (JSONArray) inputs.get("inputs");
			//Boolean btype=true;
			if(inputsObject!=null &&!inputsObject.isEmpty())
			{
				for(int i=0;i<inputsObject.size();i++)
				{
					//Get input Type
					JSONObject inputType = (JSONObject) inputsObject.get(i);
					//btype=true;
					if(!inputType.isEmpty())
					{
						String vartype =inputType.get("type").toString();
						if(vartype.contains("int")) {
							if(vartype.contains("["))
							{
								int[] intret=intgen.GenerateIntegerArray(vartype);
								String _retval=Arrays.toString(intret);
								st.addInpType(vartype);
								
								st.addInpValues2(_retval,vartype);
							}
							else
							{
								String _retval=intgen.GenerateInteger(vartype);
								st.addInpType(vartype);
								st.addInpValues(_retval);
							}
						}
						else if(vartype.contains("String") || vartype.contains("string")) {
								String _retval=strgen.stringGenerator();
								st.addInpType(vartype);
								st.addInpValues(_retval);
							
						}
						else if(vartype.contains("byte")) {
							String[] intret=bytgen.byteGeneratorNewArray(vartype);
							String _retval=Arrays.toString(intret);
							st.addInpType(vartype);
							
							st.addInpValues2(_retval,vartype);
							
						}
						else if(vartype.equals("bool") || vartype.equals("Bool")) {
							String _retval=boolgen.boolGenerator();
							st.addInpType(vartype);
							st.addInpValues(_retval);

						}
						
						else if(vartype.contains("address") || vartype.contains("Address")) {
							
							if(!(vartype.contains("["))) {
								String add= addgen.addressGenerator();
								st.addInpType(vartype);
								st.addInpValues(add);
							}
							else {
								String[] add= addgen.addressGeneratorList();
								st.addInpType(vartype);
								st.addInpValues2(Arrays.deepToString(add),vartype);
							}
								
						}
						
					}
				}
			}
		}
		catch (Exception e) {
			throw e;
		}
		
		return st;
		
	}
	
	public static StructType parseObjectInputs(String[] inputsObject) 
	{
		try {
			//Get input lists
			//JSONArray inputsObject = (JSONArray) inputs;
			//Boolean btype=true;
			if(inputsObject!=null &&inputsObject.length!=0)
			{
				for(int i=0;i<inputsObject.length;i++)
				{
					//Get input Type
					String inputType = inputsObject[i];
					//btype=true;
					if(!inputType.isEmpty())
					{
						String vartype =inputType;
						if(vartype.contains("int")) {
							if(vartype.contains("["))
							{
								int[] intret=intgen.GenerateIntegerArray(vartype);
								String _retval=Arrays.toString(intret);
								st.addInpType(vartype);
								
								st.addInpValues2(_retval,vartype);
							}
							else
							{
								String _retval=intgen.GenerateInteger(vartype);
								st.addInpType(vartype);
								st.addInpValues(_retval);
							}
						}
						else if(vartype.contains("String") || vartype.contains("string")) {
								String _retval=strgen.stringGenerator();
								st.addInpType(vartype);
								st.addInpValues(_retval);
							
						}
						else if(vartype.contains("byte")) {
							String[] intret=bytgen.byteGeneratorNewArray(vartype);
							String _retval=Arrays.toString(intret);
							st.addInpType(vartype);
							
							st.addInpValues2(_retval,vartype);
							
						}
						else if(vartype.equals("bool") || vartype.equals("Bool")) {
							String _retval=boolgen.boolGenerator();
							st.addInpType(vartype);
							st.addInpValues(_retval);

						}
						
						else if(vartype.contains("address") || vartype.contains("Address")) {
							
							if(!(vartype.contains("["))) {
								String add= addgen.addressGenerator();
								st.addInpType(vartype);
								st.addInpValues(add);
							}
							else {
								String[] add= addgen.addressGeneratorList();
								st.addInpType(vartype);
								st.addInpValues2(Arrays.deepToString(add),vartype);
							}
								
						}
						
					}
				}
			}
		}
		catch (Exception e) {
			throw e;
		}
		
		return st;
		
	}
	
	public static StructType parseFunctionType(JSONObject f_Type)
	{
		try {
			String functionType = (String) f_Type.get("type");
			st.addFunType(functionType);

		}
		catch (Exception e) {
			throw e;
		}
		return st;
		
	}
	
	public static StructType parseFunctionName(JSONObject f_Name)
	{
		try {
			String functionName = (String) f_Name.get("name");
			
			st.addName(functionName);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return st;
	}
	public static StructType parseStateMutability(JSONObject state)
	{
		try {
			String stateMutability = (String) state.get("stateMutability");

			st.addStateMu(stateMutability);


			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return st;
	}
	public static StructType parsePayableField(JSONObject state)
	{
		try {

			Boolean payableState = (Boolean) state.get("payable");
			
			st.addPayable(payableState.toString());

			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return st;
	}
}