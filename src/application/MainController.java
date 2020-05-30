package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MainController {
	Map[] boys;
	Map[] girls;
	
	@FXML
	private ChoiceBox chbYear;
	
	@FXML
	private ChoiceBox chbGender;
	
	@FXML
	private TextField txtfName;
	
	@FXML
	private Button btnFindRanking;
	
	@FXML
	private Label lblRanking;
	
	@FXML
	public void initialize() {
		//Add years to chbYear
		chbYear.setItems(FXCollections.observableArrayList(
			"2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009", "2010" 
		));
		//Set year to first possible selection
		chbYear.getSelectionModel().selectFirst();
		
		//Add genders to chbGender
		chbGender.setItems(FXCollections.observableArrayList(
			"Boy", "Girl"
		));
		//Set gender to first possible selection
		chbGender.getSelectionModel().selectFirst();
		
		//Download files for years 2001-2010
		for (int i = 2001; i < 2011; i++) {
			try {
				URL url = new URL("http://liveexample.pearsoncmg.com/data/babynamesranking" + i + ".txt");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				int rc = conn.getResponseCode();
				
				if (rc == HttpURLConnection.HTTP_OK) {
		            InputStream is = conn.getInputStream();
		            FileOutputStream os = new FileOutputStream(new File(i + ".txt"));
		            
		            int ds = is.read();
		            while (ds != -1) {
		                os.write(ds);
		                ds = is.read();
		            }
		 
		            os.close();
		            is.close();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//Open files and insert into maps
		boys = getNamesFromFile(1);
		girls = getNamesFromFile(3);
		
		
	}
	
	public void getResults() {
		//Get result rank
		String resultRank;
		
		if (chbGender.getValue().equals("Boy")) {
			resultRank = (String) boys[Integer.valueOf(chbYear.getValue().toString()) - 2001].get(txtfName.getText());
		} else {
			resultRank = (String) girls[Integer.valueOf(chbYear.getValue().toString()) - 2001].get(txtfName.getText());
		}
		
		//Get gender
		String gender = chbGender.getValue().toString();
		
		//Get name
		String name = txtfName.getText();
		
		//Get year
		String year = chbYear.getValue().toString();
		
		//Check if results not found in data
		if (resultRank == null) {
			lblRanking.setText(name + " not found in year " + year + " data for " + gender + "s");
			return;
		}
		
		//Write results to label
		lblRanking.setText(gender + " name " + name + " is ranked #" + resultRank + " in year " + year);
	}
	
	private Map[] getNamesFromFile(int gender) {
		//Create map to be returned
		Map[] map = new Map[10];
		
		//Loop through files
		for (int i = 2001; i < 2011; i++) {
			//Create map for current year
			Map<String, String> innerMap = new HashMap<>();
			
			Scanner dataScanner = null;
			
			//Create scanner to read file
			try {
				dataScanner = new Scanner(new File(i + ".txt"));
			} catch (FileNotFoundException e) {
				System.out.println("No data file found for the year " + i);
				System.exit(1);
			}
			
			//Read file into map
			while(dataScanner.hasNext()) {
				ArrayList<String> arrayList = new ArrayList<>();
				//Add the 5 instances of data on each line of the file to an ArrayList
				for(int j = 0; j < 5; j++) {
					arrayList.add(j, dataScanner.next());
				}
				//Add name and rank to innerMap (gender will be 1 for Male and 3 for Female)
				innerMap.put(arrayList.get(gender), arrayList.get(0));
			}
			
			//Add to outer map (subtract 2001 to have first instance equal 0)
			map[i - 2001] = innerMap;
		}
		
		return map;
	}
}
