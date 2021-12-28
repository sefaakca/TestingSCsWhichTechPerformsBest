package InitializeandUpdatePopulation;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import abianalyser.AnalyseSolidityAbiFile;
import abianalyser.InputsByTransaction;
import abianalyser.Population;
import abianalyser.StructType;

public class InitializeandUpdatePopulation {
	public String str="(";
	
	public Random rnd = new Random();
	//general json object
	public static JSONObject obj = new JSONObject();
	
	//transactions json array
	public JSONArray transactions = new JSONArray();
	
	//Object for link references
	JSONObject linkreferences = new JSONObject();
	
	//Object for account details
	JSONObject accountsDetails = new JSONObject();
	
	public static Map<Integer,JSONArray> inpListByTran = new HashMap<Integer, JSONArray>();
	
	//public InputsByTransaction inpbyTransaction = new InputsByTransaction();
	
	int idcounter = 0;
	
	public static JSONArray abiListRef= new JSONArray();
	public static ArrayList<StructType> inputList = new ArrayList<StructType>();
	public static StructType st = new StructType();
	
	
	@SuppressWarnings("unchecked")
	public void WriteJSONFile(JSONArray test,ArrayList<StructType> val,String CName,List<String> bytecodes)
	{
		Iterator<StructType> iter = val.iterator(); 
		
		JSONArray _bytearradd = new JSONArray();
		JSONArray _intarradd = new JSONArray();
		JSONArray _bytesarradd = new JSONArray();
		while(iter.hasNext())
		{
			StructType stt = iter.next();
	
			//View functions will not added to input type
			if(stt.getstateMut().size()>=1 && stt.getstateMut().get(0)!=null)
			{
				if(stt.getstateMut().get(0).equals("view"))
				{
					return;
				}
			}
			
			if(stt.getFunTypes().get(0).equals("event"))
			{
				return;
			}
			
			
			
			accountsDetails.put("account{0}", "0xca35b7d915458ef540ade6068dfe2f44e8fa733c");
			accountsDetails.put("account{1}", "0x14723a09acff6d2a60dcdf7aa4aff308fddc160c");
			accountsDetails.put("account{2}", "0x4b0897b0513fdc7c541b6d9d7e929c4e5364d2db");
			accountsDetails.put("account{3}", "0x583031d1113ad414f02576bd6afabfb302140225");
			accountsDetails.put("account{4}", "0xdd870fa1b7c4700f2bd7f44238821c26f7392148");
			
			
			
			//accountsDetails added to general object
			obj.put("accounts", accountsDetails);
			
			
			//link references added to general object
			obj.put("linkReferences",linkreferences);
			
			//JsonArray for transactions
			//JSONArray transactions = new JSONArray();
			
			
			//JsonObject inside the transactions - it will be for each transaction
			JSONObject transactionObject = new JSONObject();

			//unixTime created for timestamp
			long unixTime = System.currentTimeMillis() / 1000L;
			
			transactionObject.put("id", idcounter);
			
			idcounter++;
			
			//time stamp value is added
			transactionObject.put("timestamp", Objects.toString(unixTime));

			
			//recordDetails object is created
			JSONObject recordDetails = new JSONObject();

			
			
			if(stt.getPayable().get(0)=="false")
			{
				//value added
				recordDetails.put("value", "0");
			}
			else
			{
				
				//value added
				recordDetails.put("value",rnd.nextInt(200000000) );
			}
			
			//parameter array created
			JSONArray parameters = new JSONArray();
			//More than one parameters
			for(int k=0;k<stt.getinpValues().size();k++)
			{
				if(stt.getInpType().get(k).contains("bytes"))
				{
					for(int i =0;i<stt.bytestrarr.size();i++)
					{
						String prs2str = stt.bytestrarr.get(i);
						_bytesarradd.add(prs2str);
					}
					parameters.add(_bytesarradd);
					
				} else if(stt.getInpType().get(k).contains("byte"))
				{
					for(int i =0;i<stt.byteint.size();i++)
					{
						String prs2str = Integer.toString(stt.byteint.get(i));
						_bytearradd.add(prs2str);
					}
					parameters.add(_bytearradd);
					
				} else if(stt.getInpType().get(k).contains("int")){
					
					if(stt.getInpType().get(k).contains("[")) {
						for(int i =0;i<stt.intint.size();i++)
						{
							String prs2str = Integer.toString(stt.intint.get(i));
							_intarradd.add(prs2str);
						}
						parameters.add(_intarradd);

					}
					else {
						parameters.add(stt.getinpValues().get(k));
					}
					
				} 
				else
				{
					parameters.add(stt.getinpValues().get(k));
				}
				
				
			}

				
			//parameter object added to recordDetails
			recordDetails.put("parameters", parameters);
			
			//abi added to parameter details
			recordDetails.put("abi", "12345687954689451d564ad5132");
			
			
			//Special info for constructor type
			if(stt.funTypes.get(0).equals("constructor"))
			{
				//contractName added to parameter details
				recordDetails.put("contractName", CName);
					
				//byte-code added to recordDetails
				recordDetails.put("bytecode", bytecodes.get(0));
				//recordDetails.put("opcodes",opcodes.get(0));
				recordDetails.put("name","");
				//Link references object is created
				JSONObject linkreferencesinRecord = new JSONObject();
					
				//link references object added to RecordDetails
				recordDetails.put("linkReferences",linkreferencesinRecord);
			}
			else
			{
				recordDetails.put("to","created{"+ Objects.toString(unixTime)+"}");
				//name-type-from are added to recordDetails
				recordDetails.put("name",stt.getNames().get(0));
			}
			
			//More than one input type will be added according to format
			boolean inp=true;
			JSONArray strList = new JSONArray();
			for(int r=0;r<stt.getInpType().size();r++)
			{
				if(r==stt.getInpType().size()-1) {
					str += stt.getInpType().get(r)+")";
					strList.add(stt.getInpType().get(r));
					//inpbyTransaction.inputTypes.add(stt.getInpType().get(r));
					//inpListByTran.put(idcounter, stt.getInpType().get(r));

				}
				else {
					str +=stt.getInpType().get(r) + ",";
					strList.add(stt.getInpType().get(r));
					
					//inpbyTransaction.inputTypes.add(stt.getInpType().get(r));
				}
					
				
				recordDetails.put("inputs",str);
				inp=false;
			}
			if(inp)
			{
				inpListByTran.put(idcounter, strList);
				recordDetails.put("inputs","()");
			}
			
			inpListByTran.put(idcounter, strList);

			recordDetails.put("type", stt.getFunTypes().get(0));
			
			recordDetails.put("from", "account{"+new Random().nextInt(5)+"}");
			
			//recordDetails is added to TransactionObject
			transactionObject.put("record", recordDetails);
			

				transactions.add(transactionObject);
			//TransactionObject is added to Transactions Array
			
			str="(";
		}
		
		
		
		
	}
	@SuppressWarnings("unchecked")
	public ArrayList<Population> GenerateFirstRandomPopulation(String fileName, String contractName, Path bytecodesPath , int pop_size, Population[] population, ArrayList<Population> currentpopulation){
		
		JSONParser jsonParser = new JSONParser();
		
		try (FileReader reader = new FileReader(fileName))
		{
			
			//List<String> opcodes = Files.readAllLines(opcodesPath);
			
			
			List<String> bytecodes = Files.readAllLines(bytecodesPath);
			
			//Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONArray abiList = (JSONArray) obj;
            //Sort according to type
            List<JSONObject> jsonValues = new ArrayList<JSONObject>();
            for(int s=0; s<abiList.size();s++) {
            	jsonValues.add((JSONObject)abiList.get(s));
            }
            
            Collections.sort( jsonValues, new Comparator<JSONObject>() {
                private static final String KEY_NAME = "type";

                @Override
                public int compare(JSONObject a, JSONObject b) {
                    String valA = new String();
                    String valB = new String();

                    try {
                        valA = (String) a.get(KEY_NAME);
                        valB = (String) b.get(KEY_NAME);
                    } 
                    catch (Exception e) {
                       throw e;
                    }

                    return valA.compareTo(valB);
                }
            });
            
            
            for (int i = 0; i < abiList.size(); i++)
            	abiListRef.add(jsonValues.get(i));

            for(int initial_pop = 0; initial_pop < pop_size; initial_pop++)
            {
	            for(int sizeofcall=0;sizeofcall<10;sizeofcall++) {
		            for(int i=0;i<abiListRef.size();i++)
		            {
		            	JSONObject abiobj= (JSONObject)abiListRef.get(i);
		            	if(abiobj.get("type").equals("constructor")&& sizeofcall==0) {
	
		            		st = AnalyseSolidityAbiFile.parseObjectInputs(((JSONObject)abiListRef.get(i)));
		            		st = AnalyseSolidityAbiFile.parseFunctionType(((JSONObject)abiListRef.get(i)));
		            		st = AnalyseSolidityAbiFile.parseFunctionName(((JSONObject)abiListRef.get(i)));
		            		st = AnalyseSolidityAbiFile.parseStateMutability(((JSONObject)abiListRef.get(i)));
		            		st = AnalyseSolidityAbiFile.parsePayableField(((JSONObject)abiListRef.get(i)));
		            		inputList.add(st);
			            	WriteJSONFile(abiListRef,inputList,contractName,bytecodes);
							st.clear();
							inputList.clear();
		            	} else if(!abiobj.get("type").equals("constructor")) {
		            		st = AnalyseSolidityAbiFile.parseObjectInputs(((JSONObject)abiListRef.get(i)));
		            		st = AnalyseSolidityAbiFile.parseFunctionType(((JSONObject)abiListRef.get(i)));
		            		st = AnalyseSolidityAbiFile.parseFunctionName(((JSONObject)abiListRef.get(i)));
		            		st = AnalyseSolidityAbiFile.parseStateMutability(((JSONObject)abiListRef.get(i)));
		            		st = AnalyseSolidityAbiFile.parsePayableField(((JSONObject)abiListRef.get(i)));
			            	inputList.add(st);
			            	WriteJSONFile(abiListRef,inputList,contractName,bytecodes);
							st.clear();
							inputList.clear();
		            	}
		            }
	            }
	          
	           
	            currentpopulation.add(PopulationPrePost(population[initial_pop], abiListRef, initial_pop, 0, 0));
            }
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		
		return currentpopulation;
	}
	
	@SuppressWarnings("unchecked")
	public void FinalWrite(Population p,String output_file) {
		
		JSONObject abiobject = new JSONObject();
		abiobject.put("12345687954689451d564ad5132", abiListRef);
		
		JSONObject finalwrite = new JSONObject();
		finalwrite.put("transactions", p.transactions.get("transactions"));
		finalwrite.put("accounts",p.accounts);
		finalwrite.put("abis",abiobject);
		
		
		
		
		//Write JSON file
    	try (FileWriter file = new FileWriter(output_file)) { //Integer.toString(p.test_id)

            file.write(finalwrite.toJSONString());
            file.flush();
            //transactions.clear();
            //abiobject.clear();
            System.out.println(p.test_id + " file created");

        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	@SuppressWarnings("unchecked")
	public List<String> PreperaforCalculation(Population p) {
		List<String> vmInputList = new ArrayList<String>();
		JSONObject abiobject = new JSONObject();
		abiobject.put("12345687954689451d564ad5132", abiListRef);
		
		JSONObject finalwrite = new JSONObject();
		finalwrite.put("transactions", p.transactions.get("transactions"));
		finalwrite.put("accounts",p.accounts);
		finalwrite.put("abis",abiobject);
		finalwrite.put("test_id",p.test_id);
		String str = finalwrite.toJSONString();
		vmInputList.add(str);
    	
    	return vmInputList;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> PreperaforCalculation2(ArrayList<Population> pList) {
		List<String> vmInputList = new ArrayList<String>();
		JSONObject abiobject = new JSONObject();
		
		abiobject.put("12345687954689451d564ad5132", abiListRef);
		for (Population population : pList) {
			JSONObject finalwrite = new JSONObject();
			
			finalwrite.put("transactions", population.transactions.get("transactions"));
			finalwrite.put("accounts",population.accounts);
			finalwrite.put("abis",abiobject);
			finalwrite.put("test_id",population.test_id);
			String str = finalwrite.toJSONString();
			vmInputList.add(str);
			
		}
	
		
    	return vmInputList;
	}
	
	@SuppressWarnings("unchecked")
	public Population PopulationPrePost( Population p2,JSONArray abiList, int test_id, float coverage, float gasusage) {
		JSONObject abi = new JSONObject();
		abi.put("12345687954689451d564ad5132", abiList);
		
		JSONObject transactions_obj =new JSONObject();
		transactions_obj.put("transactions", transactions);
		//p = new Population(test_id,coverage,abi,accountsDetails,linkreferences,transactions_obj);
		
		
		Population p = new Population();
		p.abi= abi;

		p.transactions = p.deepCopy(p.transactions, transactions_obj);

		p.accounts = accountsDetails;
		p.linkReferences = linkreferences;
		p.test_id = test_id;
		p.coverage = coverage;
		transactions.clear();
		transactions_obj.clear();
		
		return p;
		
	}

}