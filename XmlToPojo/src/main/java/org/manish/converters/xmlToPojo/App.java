package org.manish.converters.xmlToPojo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.SchemaGenerator;
import org.jsonschema2pojo.SchemaMapper;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.SourceType;
import org.jsonschema2pojo.rules.RuleFactory;

import com.sun.codemodel.JCodeModel;

public class App {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		String packageName = "org.manish.converted.pojo";
		App app = new App();
		try {
			String xml = app.readFile("simple.xml");
			String inputJson = app.convertToJson(xml);
			File outputPojoDirectory = new File("." + File.separator + "convertedPojo");
			outputPojoDirectory.mkdirs();

			app.convert2JSON(inputJson, outputPojoDirectory, packageName, "simple");
		} catch (IOException e) {
			System.out.println("Encountered issue while converting to pojo: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void convert2JSON(String inputJson, File outputPojoDirectory, String packageName, String className)
			throws IOException {
		JCodeModel codeModel = new JCodeModel();
		String source = inputJson;

		GenerationConfig config = new DefaultGenerationConfig() {
			@Override
			public boolean isGenerateBuilders() {
				return true;
			}
			public SourceType getSourceType() {
				return SourceType.JSON;
			}
		};
		SchemaMapper mapper = new SchemaMapper(
				new RuleFactory(config, new Jackson2Annotator(config), new SchemaStore()), new SchemaGenerator());
		mapper.generate(codeModel, className, packageName, source);

		codeModel.build(outputPojoDirectory);
	}

	public String convertToJson(String xml) {
		JSONObject jsonobj;
		String json = null;
		try {
			jsonobj = XML.toJSONObject(xml);
			json = jsonobj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	public String readFile(String path) throws IOException {
		StringBuilder sb = new StringBuilder();
		String sCurrentLine;
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {

			while ((sCurrentLine = br.readLine()) != null) {
				sb.append(sCurrentLine);
			}
		}
		return sb.toString();
	}
}
