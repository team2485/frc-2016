import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoggerToCSV {
	public static String jsonToCSV(JSONArray data) {
		ArrayList<Double> times = new ArrayList<Double>();
		Map<String, Set<String>> componentToFields = new TreeMap<String, Set<String>>();
		System.out.println("Converting to map");
		for (int i = 0; i < data.length(); i++) {
			JSONObject thisTimeData = data.getJSONObject(i);
			times.add(thisTimeData.getDouble("Time"));
			if (thisTimeData.getString("Type").equals("printErr")) {
				continue;
			}
			for (Iterator<String> iterator = thisTimeData.keys(); iterator
					.hasNext();) {
				String key = iterator.next();
				if (key.equals("Type") || key.equals("Time")
						|| key.equals("Mode")) {
					continue;
				}
				Set<String> fields = new TreeSet<String>();
				if (componentToFields.containsKey(key)) {
					fields = componentToFields.get(key);
				}
				JSONObject thisComponentData = thisTimeData.getJSONObject(key);
				for (Iterator<String> iterator2 = thisComponentData.keys(); iterator2
						.hasNext();) {
					fields.add(iterator2.next());

				}
				componentToFields.put(key, fields);
			}
		}
		String ret = "Time,Mode,";

		// add component headings
		System.out.println("adding component headings");
		for (Iterator<String> iterator = componentToFields.keySet().iterator(); iterator
				.hasNext();) {
			String component1 = iterator.next();
			ret += component1;
			for (int i = 0; i < componentToFields.get(component1).size(); i++) {
				ret += ",";
			}

		}
		ret += "\n";

		// add field headings
		System.out.println("adding field headings");
		ret += ",,";
		for (Iterator<String> iterator = componentToFields.keySet().iterator(); iterator
				.hasNext();) {
			String component1 = iterator.next();
			Set<String> fields = componentToFields.get(component1);
			for (Iterator<String> iterator2 = fields.iterator(); iterator2
					.hasNext();) {
				ret += iterator2.next() + ",";
			}
		}
		ret += "\n";

		// add data
		CharArrayWriter dataCharArray = new CharArrayWriter();
		System.out.println("adding data");
		for (int i = 0; i < times.size(); i++) {
			dataCharArray.append(times.get(i) + ",");
			JSONObject thisTimeData = data.getJSONObject(i);
			dataCharArray.append(thisTimeData.getString("Mode") + ",");
			if (thisTimeData.getString("Type").equals("printErr")) {
				for (Iterator<String> iterator = componentToFields.keySet()
						.iterator(); iterator.hasNext();) {
					String component1 = iterator.next();
					if (thisTimeData.isNull(component1)) {
						for (int j = 0; j < componentToFields.get(component1)
								.size(); j++) {
							dataCharArray.append(",");
						}
					} else {
						dataCharArray
								.append(thisTimeData.getString(component1));
					}
				}
			} else {
				for (Iterator<String> iterator = componentToFields.keySet()
						.iterator(); iterator.hasNext();) {
					String component1 = iterator.next();
					Set<String> fields = componentToFields.get(component1);
					if (thisTimeData.isNull(component1)) {
						for (int j = 0; j < fields.size(); j++) {
							dataCharArray.append(",");
						}
					}
					JSONObject thisComponentData = thisTimeData
							.getJSONObject(component1);
					for (Iterator<String> iterator2 = fields.iterator(); iterator2
							.hasNext();) {
						String field = iterator2.next();
						try {
							dataCharArray.append(thisComponentData.get(field)
									.toString());
						} finally {
						}
						dataCharArray.append(",");
					}
				}
			}
			dataCharArray.append("\n");
		}
		ret += dataCharArray.toString();
		return ret;
	}

	public static void main(String[] args) {
		try {
			System.out.println("Reading");
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					args[0])));
			String in = reader.readLine();
			reader.close();

			System.out.println("Constructing array");
			in = in.substring(0, in.length() - 1);
			JSONArray arr = new JSONArray("[" + in + "]");
			System.out.println("Converting to CSV");
			String out = jsonToCSV(arr);

			System.out.println("Writing");
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					args[1])));
			writer.write(out);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
