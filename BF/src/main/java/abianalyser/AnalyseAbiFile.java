package abianalyser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Thread.State;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import GenerateTestFile.GenerateTestFile;
import randomGenerator.AdressGenerator;
import randomGenerator.BooleanGenerator;
import randomGenerator.ByteGenerator;
import randomGenerator.IntegerGenerator;
import randomGenerator.StringGenerator;

public class AnalyseAbiFile 
{
	public static IntegerGenerator intgen = new IntegerGenerator();
	public static StringGenerator strgen = new StringGenerator();
	public static ByteGenerator bytgen = new ByteGenerator();
	public static BooleanGenerator boolgen = new BooleanGenerator();
	public static AdressGenerator addgen = new AdressGenerator();
	public static GenerateTestFile _writeJson = new GenerateTestFile();
	
	public static JSONArray abiListRef= new JSONArray();
	
	public static ArrayList<StructType> inputList = new ArrayList<StructType>();
	public static StructType st = new StructType();
	
	
	
	
	public static long endTime = System.currentTimeMillis();
	@SuppressWarnings("unchecked")
	public static void GenerateRandomInput(String AbifileName, String contractName, Path bytecodesPath, String output_file,long startTime) 
	{
		

		JSONParser jsonParser = new JSONParser();
		
		try (FileReader reader = new FileReader(AbifileName))
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
            for (int i = 0; i < abiList.size(); i++) {
            	abiListRef.add(jsonValues.get(i));
            }
            
            

            for(int size_of_call=0;size_of_call<120;size_of_call++) {
	            for(int i=0;i<abiListRef.size();i++)
	            {
	            	JSONObject teststst= (JSONObject)abiListRef.get(i);
	            	if(teststst.get("type").equals("constructor")&& size_of_call==0) {

		            	parseObjectInputs(((JSONObject)abiListRef.get(i)));
		            	parseFunctionType(((JSONObject)abiListRef.get(i)));
		            	parseFunctionName(((JSONObject)abiListRef.get(i)));
		            	parseStateMutability(((JSONObject)abiListRef.get(i)));
		            	parsePayableField(((JSONObject)abiListRef.get(i)));
		            	inputList.add(st);
						_writeJson.WriteJSONFile(abiListRef,inputList,contractName,bytecodes);
						st.clear();
						inputList.clear();
	            	} else if(!teststst.get("type").equals("constructor")) {
		            	parseObjectInputs(((JSONObject)abiListRef.get(i)));
		            	parseFunctionType(((JSONObject)abiListRef.get(i)));
		            	parseFunctionName(((JSONObject)abiListRef.get(i)));
		            	parseStateMutability(((JSONObject)abiListRef.get(i)));
		            	parsePayableField(((JSONObject)abiListRef.get(i)));
		            	inputList.add(st);
						_writeJson.WriteJSONFile(abiListRef,inputList,contractName,bytecodes);
						st.clear();
						inputList.clear();
	            	}
	            }
            	endTime = System.currentTimeMillis();
	            //System.out.println(endTime-startTime + "milliseconds");
            }
            
           //System.out.println("DONE");
            _writeJson.FinalWrite(abiListRef,output_file);

            
            System.out.println(contractName);
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
	}

	public static void parseObjectInputs(JSONObject inputs) 
	{
		try {
			//Get input lists
			JSONArray inputsObject = (JSONArray) inputs.get("inputs");
			Boolean btype=true;
			if(inputsObject!=null &&!inputsObject.isEmpty())
			{
				for(int i=0;i<inputsObject.size();i++)
				{
					//Get input Type
					JSONObject inputType = (JSONObject) inputsObject.get(i);
					btype=true;
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
		
	}
	
	private static void parseFunctionType(JSONObject f_Type)
	{
		try {
			String functionType = (String) f_Type.get("type");
			st.addFunType(functionType);

		}
		catch (Exception e) {
			throw e;
		}
		
	}
	
	private static void parseFunctionName(JSONObject f_Name)
	{
		try {
			String functionName = (String) f_Name.get("name");
			
			st.addName(functionName);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	private static void parseStateMutability(JSONObject state)
	{
		try {
			String stateMutability = (String) state.get("stateMutability");

			st.addStateMu(stateMutability);


			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	private static void parsePayableField(JSONObject state)
	{
		try {

			Boolean payableState = (Boolean) state.get("payable");
			
			st.addPayable(payableState.toString());

			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
