package abianalyser;



import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class OffSpring extends Population{


		public String nameofP1;
		public String nameofP2;

		
		@SuppressWarnings("unchecked")
		public JSONObject deepCopy(JSONObject _new, JSONArray _old)
		{
			String test ="";
			
			JSONObject jsnobj = new JSONObject();
			
			jsnobj.put("transactions", _old);
			
			test = jsnobj.toJSONString();//get("transactions");
			JSONParser parser = new JSONParser();
			try {
				 _new = (JSONObject) parser.parse(test);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return _new;
		}
}
