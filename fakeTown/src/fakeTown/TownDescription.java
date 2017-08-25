package fakeTown;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.StringElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.AdjPhraseSpec;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

public class TownDescription {
	private StringBuilder description;
	
	public TownDescription() throws IOException {
		// TODO Auto-generated method stub
		
		// set up
		Lexicon lexicon = Lexicon.getDefaultLexicon();
		NLGFactory nlgFactory = new NLGFactory(lexicon);
		Realiser realiser = new Realiser(lexicon);
		
		
		// 6 metropolitan counties and their metropolitan boroughs
		String[] metroCounties = {"Greater Manchester", "Merseyside", "South Yorkshire", 
								"Tyne and Wear", "West Midlands", "West Yorkshire"};
		String[][] metroBoroughs = new String[6][];
		metroBoroughs[0] = new String[]{"Manchester", "Bolton", "Bury", "Oldham", "Rochdale", 
									 "Salford", "Stockport", "Tameside", "Trafford", "Wigan"};
		metroBoroughs[1] = new String[]{"Liverpool", "Knowsley", "St Helens", "Sefton", "Wirral"};
		metroBoroughs[2] = new String[]{"Sheffield", "Barnsley", "Doncaster", "Rotherham"};
		metroBoroughs[3] = new String[]{"Newcaster upon Tyne", "Gateshead", "South Tyneside", 
									 "North Tyneside", "Sunderland"};
		metroBoroughs[4] = new String[]{"Birmingham", "Coventry", "Dudley", "Sandwell", 
									 "Solihull", "Walsall", "Wolverhampton"};
		metroBoroughs[5] = new String[]{"Leeds", "Bradford", "Calderdale", "Kirklees", "Wakefield"};
		
		// 17 Non-metropolitan counties
		String[] nonMetroCounties = {"Buckinghamshire","Cambridgeshire","Cumbria","Derbyshire",
										"Devon","Dorset","East Sussex","Essex","Gloucestershire",
										"Hampshire","Hertfordshire","Kent","Lancashire",
										"Leicestershire","Lincolnshire","Norfolk","Northamptonshire",
										"North Yorkshire","Nottinghamshire","Oxfordshire","Somerset",
										"Staffordshire","Suffolk","Surrey","Warwickshire","West Sussex",
										"Worcestershire"};
		
		// adjective describing a town
		String[] townTypes = {"", "small", "market", "historic market", 
								"new", "large", "resort", "industrial",
								"spa", "railway", "satellite", "seaside"};
		
		// Scotland, Wales, Northern Ireland
		String[] UK = {"Scotland", "Wales", "Northern Ireland"};
		
		// direction: north, south, west, east
		String[] direction = {"north", "south", "west", "east", 
								"northeast", "northwest", 
								"southeast", "southwest"};
		
		/*
			OPENING SENTENCE: "XXX is a town."
		*/
		String townName = readPlaceName("trainingResultBest.txt");
		NPPhraseSpec aTown = nlgFactory.createNounPhrase("a", "town");			// create an NP
		Random r = new Random();
		AdjPhraseSpec adjTown = nlgFactory.
				createAdjectivePhrase(townTypes[r.nextInt(townTypes.length)]);	// create AdjP
		aTown.addModifier(adjTown); 
		SPhraseSpec isATown = nlgFactory.createClause(townName,				// create sentence
				"is", aTown);
		DocumentElement sentence = nlgFactory
				.createSentence(isATown);										// create a sentence
		
		/*
			LOCATIONS: based on "Districts of England" https://en.wikipedia.org/wiki/Districts_of_England
					  #0 not in England
					  #1 in the Metropolitan District
					  #2 in London borough
					  #3 in non-metropolitan district
		*/
		switch(r.nextInt(4)){
				// #0 not in England
		case 0: StringElement notInEngland = new StringElement("in " + UK[r.nextInt(UK.length)]);
				sentence.addComponent(notInEngland);break;
				
				// #1 in the Metropolitan District
		case 1: int tempRandom1 = r.nextInt(metroCounties.length);
				int tempRandom2;
				if(tempRandom1 == 0)
					tempRandom2 = r.nextInt(10);
				else if(tempRandom1 == 1 || tempRandom1 == 3 || tempRandom1 == 5)
					tempRandom2 = r.nextInt(5);
				else if(tempRandom1 == 2)
					tempRandom2 = r.nextInt(4);
				else
					tempRandom2 = r.nextInt(7);
				
				StringElement inMetroDistrict = new StringElement("located in the Metropolitan District of the City of " 
																+ metroBoroughs[tempRandom1][tempRandom2] + " in " 
																+ metroCounties[tempRandom1] + ", England.");
				StringElement supplement = new StringElement(", " + r.nextInt(100) + " miles " 
																+ direction[r.nextInt(direction.length)]
																+ " of " + metroBoroughs[tempRandom1][tempRandom2] + ".");
				StringElement supplement1 = new StringElement("\nTogether with its surrounding suburbs and settlements, "
																+ "the town forms part of the Metropolitan Borough of " 
																+ metroCounties[tempRandom1]);
				sentence.addComponent(inMetroDistrict);
				// choose supplement
				if(r.nextBoolean())
					sentence.addComponent(r.nextInt(2) == 0 ? supplement : supplement1);
				else{
					sentence.addComponent(supplement);
					sentence.addComponent(supplement1);
				}break;
				
				// #2 in London borough
		case 2: 
				String borough = readPlaceName("LondonBoroughs.txt");
				StringElement inLondon = new StringElement("within the London Borough of " + borough + " in " 
															+ direction[r.nextInt(direction.length)] + " London, England");
				StringElement supplement3 = new StringElement(", " + r.nextInt(20) + " miles " 
															+ direction[r.nextInt(direction.length)]
															+ " of Charing Cross.");
				sentence.addComponent(inLondon);
				if(r.nextBoolean())
					sentence.addComponent(supplement3);
				break;
				
				// #3 in non-metropolitan district
		case 3: BufferedReader reader = new BufferedReader(new FileReader("non-metropolitanDistricts.txt"));
				String line = reader.readLine();
				ArrayList<String> lines = new ArrayList<String>();
				while (line != null) {
				     lines.add(line);
				     line = reader.readLine();
				}
				reader.close();
				int selectedRow = r.nextInt(lines.size());
				String randomLine = lines.get(r.nextInt(lines.size()));
				randomLine = lines.get(selectedRow);
				String[] selectedCounties = randomLine.split(",");
				StringElement inNonDistrict = new StringElement("in the " + selectedCounties[r.nextInt(selectedCounties.length)] 
																+ " District of " + nonMetroCounties[selectedRow] + ", England");
				sentence.addComponent(inNonDistrict);break;
		}
		
		/*
			POPULATION and AREA
		*/
	    DocumentElement population = nlgFactory.createSentence("It had a population of " 
	    								+ NumberFormat.getNumberInstance(Locale.US).format(r.nextInt(150000))
	    								+ " in the " + (2010 + r.nextInt(7)) + " census, with an area of " 
	    								+ String.format("%.2f", (r.nextDouble() * 400)) + " square miles");
	    
	    DocumentElement population1 = nlgFactory.createSentence("The population at the " + (2010 + r.nextInt(7)) + " census was " 
	    								+ NumberFormat.getNumberInstance(Locale.US).format(r.nextInt(150000)));
	   
	    DocumentElement population2 = nlgFactory.createSentence("According to the " + (2010 + r.nextInt(7))
	    								+ " census, the town had a population of " 
	    								+ NumberFormat.getNumberInstance(Locale.US).format(r.nextInt(1000) + 1000));
	    /*
	    	HISTORY
	    */
	    String[] industry = {"mining", "fishing", "animal husbandry", 
	    					"woodcutting", "farming", "agriculture", "shipbuilding", 
	    					"textile manufacturing", "wool industry"};
	    String[] time = {"2000 years", "2500 years", "3000 years", 
	    					"3500 years", "4000 years", "4500 years", "5000 years"};
	    String[] yearOrCentury = {"years", "centuries"};
	    String[] background = {"mackerel fishing hamlet", "farming village", "quarrying village"};
	    String[] development = {"an elegant Georgian seaside resort",
	    							"a more complex village",
	    								"a holiday village"};
	    String[] period = {"late 18th", "early 17th", "mid-18th", "mid and late 17th"};
	   
	    // history 0
	    DocumentElement history = nlgFactory.createSentence("The area around " + townName 
	    							+ " has been populated for about " 
	    							+ time[r.nextInt(time.length)]+ ". Historically " + townName + 
	    							" was economically dependent on " + industry[r.nextInt(9)] +
	    							", which made it one of the most affluent towns in U.K. during " 
	    							+ (r.nextInt(5) + 11) + "th century");
	    // history 1
	    DocumentElement history1 = nlgFactory.createSentence("The area around " + townName + " has been populated for about " 
	    							+ time[r.nextInt(time.length)] + ". For many " + yearOrCentury[r.nextInt(2)] 
	    							+ " , " + townName + " was a small " + background[r.nextInt(background.length)]
	    							+ " until in the " + period[r.nextInt(period.length)]
	    							+ " century it developed into "	+ development[r.nextInt(development.length)]);
	    /*
	    	DETTIALS including 4 diff versions: detail, detail1, detail2, detail3
	    */
	    String[] months = {"Janaury", "February", "March", "April", "May", "Junn", "Jull", 
	    					"August", "September", "Ocotober", "November", "December"};
	    String[] titles = {"best", "safest", "most beautiful", "most expensive", 
	    					"most dangerous", "most desirable"};
	    String[] reasons = {"high life expectancy and disposable income",
	    					"low overall crime rates",
	    					"natural scenery",
	    					"high living costs",
	    					"high crime rate",
	    					"high quality of life"};
	    String[] sources = {"Quality of Life Index rated ", "Sunday Times ranked "};
	   
	    int randomPick = r.nextInt(titles.length);
	    DocumentElement detail = nlgFactory.createSentence("In " + months[r.nextInt(12)] 
	    							+ " " + (2010 + r.nextInt(6)) + ", the " + sources[r.nextInt(2)]
	    							+ townName + " the \""+ titles[randomPick] 
	    							+ " place to live\" in the United Kingdom due to its "
	    							+ reasons[randomPick]);
	    
	    BufferedReader reader;
		String line;
		ArrayList<String> lines;
	    
	    // read educational institution names from file
	    reader = new BufferedReader(new FileReader("100educational institutes names.txt"));
	    line = reader.readLine();
	    lines = new ArrayList<String>();
	    while (line != null) {
	    	lines.add(line);
	    	line = reader.readLine();
	    }
	    reader.close();
	    String eduName = lines.get(r.nextInt(lines.size()));
	    lines.remove(eduName);
	    String eduName1 = lines.get(r.nextInt(lines.size()));
	   
	    // read club names from file
	    reader = new BufferedReader(new FileReader("clubNames.txt"));
	    line = reader.readLine();
	    lines = new ArrayList<String>();
	    while (line != null) {
	    	lines.add(line);
	    	line = reader.readLine();
	    }
	    reader.close(); 
	    String clubName = lines.get(r.nextInt(lines.size()));
	    
	    DocumentElement detail1 = nlgFactory.createSentence("The town includes two educational institutions: "
	    							+ eduName + " and " + eduName1 + "; together with various shops and other amenities including " 
	    							+ clubName + " Rugby Club.");
	    // read chapel names from file
	    reader = new BufferedReader(new FileReader("chapelNames.txt"));
	    line = reader.readLine();
	    lines = new ArrayList<String>();
	    while (line != null) {
	    	lines.add(line);
	    	line = reader.readLine();
	    }
	    reader.close(); 
	    String chapelName = lines.get(r.nextInt(lines.size()));
	    DocumentElement detail2 = nlgFactory.createSentence(townName + " is famed for its surviving abbey buildings, including "
	    							+ chapelName + ", a 14th-century pilgrimage chapel that stands on a hill");
	    // read human names from file
	    reader = new BufferedReader(new FileReader("humanNames.txt"));
	    line = reader.readLine();
	    lines = new ArrayList<String>();
	    while (line != null) {
	    	lines.add(line);
	    	line = reader.readLine();
	    }
	    reader.close(); 
	    String humanName = lines.get(r.nextInt(lines.size()));
	    lines.remove(humanName);
	    String humanName1 = lines.get(r.nextInt(lines.size()));
	    lines.remove(humanName1);
	    String humanName2 = lines.get(r.nextInt(lines.size()));
	    
	    DocumentElement detail3 = nlgFactory.createSentence(townName + " was the birthplace of the politicians " +  humanName
	    							+ ", " + humanName1 + "; and of the rugby footballer " + humanName2);
	    /*
	    	NAME ORIGIN
		*/
	    String[] origins = {"Brythonic", "Cumbric", "Cornish", 	//https://en.wikipedia.org/wiki/List_of_generic_forms_in_place_names_in_the_United_Kingdom_and_Ireland
	    					"Irish", "Latin", "Middle English", 
	    					"Norman French", "Old English", 
	    					"Pictish", "Scots Gaelic", "Welsh"};
	    String[] meanings = {"mouth of a river", "a meeting of waters", "oak tree", "ash tree", 
	    						"hill at the wet place", "farmstead on a hill", "(place at) the stiff oak",
	    						"field of the bog", "field of the river-mouth", "field of the cliff",
	    						"stream where alders grow", "farmstead or village on a Roman road",
	    						"black marsh", "height of merriment", "height of schools",
	    						"spring or stream in a broad valley", "little throat", "crooked grove",
	    						"the little corner", "hill with a peak", "northern ridge", "raven",
	    						"farmstead or village of a man called " + townName.substring(0, r.nextInt(townName.length())),
	    						"stronghold of a woman called " + townName.substring(0, r.nextInt(townName.length()))};
	    // https://en.wikipedia.org/wiki/List_of_generic_forms_in_place_names_in_the_United_Kingdom_and_Ireland
	    // http://www.oxfordreference.com/view/10.1093/acref/9780199609086.001.0001/acref-9780199609086	  
	    DocumentElement nameOrigin = nlgFactory.createSentence("The name may derive from " + origins[r.nextInt(origins.length)]
	    															+ ", meaning " + meanings[r.nextInt(meanings.length)]);
	    DocumentElement nameOrigin1 = nlgFactory.createSentence("The place name is thought to come from " + origins[r.nextInt(origins.length)]
	    															+ ", referring to " + meanings[r.nextInt(meanings.length)]);
	    DocumentElement nameOrigin2 = nlgFactory.createSentence("The origin of the name is uncertain. It may derive from "
	    															+ origins[r.nextInt(origins.length)] + ", in which case "
	    															+ "the name presumably meant \"" + meanings[r.nextInt(meanings.length)]
	    															+ "\". Another possibility is that the name means \""
	    															+ meanings[r.nextInt(meanings.length)] + "\".");
	    DocumentElement[] nameOriginList = {nameOrigin, nameOrigin1, nameOrigin2};
	   
	    /*
	    	MIXING
	    */
	    description = new StringBuilder(realiser.realiseSentence(sentence) + "\n");	
	    switch(r.nextInt(3)){
	    case 0: description.append(realiser.realiseSentence(population) + "\n");break;
	    case 1: description.append(realiser.realiseSentence(population1) + "\n");break;
	    case 2: description.append(realiser.realiseSentence(population2) + "\n");
	    }
		if(r.nextBoolean())
			description.append(realiser.realiseSentence(r.nextInt(2) == 0 ? history : history1) + "\n");
		if(r.nextBoolean())
			description.append(realiser.realiseSentence(nameOriginList[r.nextInt(nameOriginList.length)]) + "\n");
		switch(r.nextInt(4)){
	    case 0: description.append(realiser.realiseSentence(detail) + "\n");break;
	    case 1: description.append(realiser.realiseSentence(detail1) + "\n");break;
	    case 2: description.append(realiser.realiseSentence(detail2) + "\n");break;
	    case 3: description.append(realiser.realiseSentence(detail3) + "\n");break;
	    }
	}

	private static String readPlaceName(String fileName) throws IOException {
		// TODO Auto-generated method stub
		// randomly read a town name from the file
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = reader.readLine();
		ArrayList<String> lines = new ArrayList<String>();
		while (line != null) {
			if(line.contains("(") || line.contains(")") || line.contains("/"))
			// eliminate the records containing (, ), or /
				;
		    else
		    	lines.add(line);
		    line = reader.readLine();
		}
		reader.close();
		Random r = new Random();
		return lines.get(r.nextInt(lines.size()));
	}

	
	public StringBuilder getDescription() {
		return description;
	}

}
