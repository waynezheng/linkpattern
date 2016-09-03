package linkPatternService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import linkPatternUtil.EditDistance;
import linkPatternUtil.Element;
import linkPatternUtil.Link;
import linkPatternUtil.TwoTuple;
import database.*;

public class CommonServiceForLink {
	
	public static String getLocalName(String uri){
		int pos = uri.lastIndexOf("#");
		if(pos >= 0){
			return uri.substring(pos + 1);
		}
		pos = uri.lastIndexOf("/");
		if(pos >= 0 && pos < uri.length() - 1){
			return uri.substring(pos + 1);
		}
		else if(pos == uri.length() - 1){
			return uri.substring(pos + 1, uri.length() - 1);
		}
		return null;
	}
	
    public static HashSet<String> add2needFilterUriSet(){
    	HashSet<String> needfilterUriSet=new HashSet<String>();
		needfilterUriSet.add("http://xmlns.com/foaf/0.1/mbox");
		needfilterUriSet.add("http://xmlns.com/foaf/0.1/workplaceHomepage");
		needfilterUriSet.add("http://xmlns.com/foaf/0.1/workInfoHomepage");
		needfilterUriSet.add("http://xmlns.com/foaf/0.1/homepage");
		needfilterUriSet.add("http://usefulinc.com/ns/doap#homepage");
		needfilterUriSet.add("http://xmlns.com/foaf/0.1/schoolHomepage");
		needfilterUriSet.add("http://xmlns.com/foaf/0.1/img");
		needfilterUriSet.add("http://xmlns.com/foaf/0.1/page");
		needfilterUriSet.add("http://xmlns.com/foaf/0.1/weblog");
		needfilterUriSet.add("http://www.w3.org/2002/07/owl#sameAs");
		needfilterUriSet.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		needfilterUriSet.add("http://www.w3.org/1999/xhtml/vocab#stylesheet");
		needfilterUriSet.add("http://xmlns.com/foaf/0.1/isPrimaryTopicOf");
		needfilterUriSet.add("http://semantic-mediawiki.org/swivt/1.0#page");
		needfilterUriSet.add("http://www.w3.org/ns/prov#wasDerivedFrom");
		
		needfilterUriSet.add("http://dbpedia.org/property/website");
		needfilterUriSet.add("http://dbpedia.org/property/hasPhotoCollection");
		needfilterUriSet.add("http://dbpedia.org/property/wikiPageUsesTemplate");
		needfilterUriSet.add("http://dbpedia.org/ontology/wikiPageExternalLink");
		needfilterUriSet.add("http://dbpedia.org/ontology/wikiPageInterLanguageLink");
		needfilterUriSet.add("http://dbpedia.org/ontology/wikiPageRedirects");
		needfilterUriSet.add("http://dbpedia.org/ontology/wikiPageDisambiguates");
		needfilterUriSet.add("http://dbpedia.org/property/wikiPageExternalLink");
		needfilterUriSet.add("http://dbpedia.org/property/wikiPageInterLanguageLink");
		needfilterUriSet.add("http://dbpedia.org/property/wikiPageRedirects");
		needfilterUriSet.add("http://dbpedia.org/property/wikiPageDisambiguates");
		needfilterUriSet.add("http://dbpedia.org/ontology/wikiPageRevisionLink");
		needfilterUriSet.add("http://dbpedia.org/ontology/wikiPageEditLink");
		needfilterUriSet.add("http://dbpedia.org/ontology/wikiPageHistoryLink");
		needfilterUriSet.add("http://dbpedia.org/ontology/wikiPageWikiLink");
		needfilterUriSet.add("http://it.dbpedia.org/property/wikiPageUsesTemplate");
		needfilterUriSet.add("http://fr.dbpedia.org/property/wikiPageUsesTemplate");
		
		needfilterUriSet.add("http://purl.org/dc/terms/subject");
		needfilterUriSet.add("http://dbpedia.org/property/subject");
		needfilterUriSet.add("http://www.w3.org/2004/02/skos/core#subject");
		needfilterUriSet.add("http://purl.org/dc/elements/1.1/subject");
		needfilterUriSet.add("http://dbpedia.org/property/type");
		needfilterUriSet.add("http://dbpedia.org/ontology/type");
		needfilterUriSet.add("http://dbpedia.org/ontology/assembly");
		needfilterUriSet.add("http://dbpedia.org/ontology/genre");
		
		needfilterUriSet.add("http://dbpedia.org/property/wordnet_type");
		needfilterUriSet.add("http://xmlns.com/foaf/0.1/depiction");
		needfilterUriSet.add("http://xmlns.com/foaf/0.1/primaryTopic");
		needfilterUriSet.add("http://dbpedia.org/ontology/thumbnail");
		needfilterUriSet.add("http://dbpedia.org/ontology/termPeriod");
		needfilterUriSet.add("http://data.nytimes.com/elements/topicPage");
		needfilterUriSet.add("http://schema.org/about");
		needfilterUriSet.add("http://www.w3.org/2006/03/wn/wn20/schema/hyponymOf");
		needfilterUriSet.add("http://creativecommons.org/ns#attributionURL");
		needfilterUriSet.add("http://de.dbpedia.org/property/wikiPageUsesTemplate");
		needfilterUriSet.add("http://fr.dbpedia.org/property/wikiPageUsesTemplate");			
		needfilterUriSet.add("http://d-nb.info/standards/elementset/gnd#gndSubjectCategory");
		needfilterUriSet.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#seeAlso");
		needfilterUriSet.add("http://www.w3.org/2000/01/rdf-schema#seeAlso");
		needfilterUriSet.add("http://www.semanlink.net/2001/00/semanlink-schema#tag");
		needfilterUriSet.add("http://data.linkedct.org/vocab/has_provenance");
		return needfilterUriSet;
	}
	
    public static HashMap<String, HashSet<String>> buildOriginalPropertyHierarchyMap(){
    	HashMap<String, HashSet<String>> originalPropertyHierarchyMap=new HashMap<String, HashSet<String>>();

    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/birthPlace", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/place")));

    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/residence", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));

    	
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/deathPlace", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/place")));

    	
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/country", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/county", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));
    	
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/sourceCountry", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/country")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/city", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/state", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/stateOfOrigin", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));
    	
    	
    	
//    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/location", 
//    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/locationCity", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/locationCountry", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));
    	
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/locatedInArea", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));
    	
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/place", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/populationPlace", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/place")));
    	    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/restingPlace", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/place"))); 	
       originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/foundationPlace",
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/place")));   	
    	
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/region", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));

    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/headquarter", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));

    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/hometown", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));

    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/nationality", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));
    	
    	
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/garrison", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/homeport", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/hubAirport", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/museum", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/sourceMountain", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/sourcePlace", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/spokenIn", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/broadcastArea", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/campus", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/capital", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));
     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/targetAirport", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/location")));
    	
    	
    	
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/director", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/writer", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));

    	
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/producer", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));

    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/executiveProducer", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/editing", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));	
    	
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/creator", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/property/creator", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/developer", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/property/developer", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/starring", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/author", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/commander", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
//    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/distributor", 
//    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/designer", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
//    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/training", 
//    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/builder", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/award", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/billed", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/foundedBy", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/commander", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/architect", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/maintainedBy", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/operator", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/premierePlace", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/recordLabel", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/recordedIn", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/regionServed", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/significantBuilding", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/significantProject", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/university", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/sportCountry", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/destination", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/thirdDriverCountry", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
     	
     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/affiliation", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
     	
     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/countryWithFirstAstronaut", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/anthem", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/presenter", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/almaMater", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/commandStructure", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
     	
     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/coach", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/foundingPerson", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));

     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/narrator", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/musicComposer", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/cinematography", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/co-participate with")));
     	
     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/subsequentWork", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/Works")));
     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/previousWork", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/Works")));
     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/basedOn", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/Works")));
     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/album", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/Works")));
     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/Album", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/Works")));
     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/series", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/Works")));
     	
     	
     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/routeEnd", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/hasCommonBoundary")));

        originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/battle", 
        		new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/isPartOf")));
     		
     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/party", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/Member")));
     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/employer", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/Member")));
     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/network", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/Member")));
     	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/ethnicity", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/Member")));

    	
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/mother", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/parent")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/father", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/parent")));

    	
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/parent", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/human-relations")));
    	
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/spouse", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/human-relations")));
	
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/child", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/human-relations")));
    	
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/sibling", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/human-relations"))); 	
    	
//    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/human-family-relations", 
//    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/human-relations")));
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/influencedBy", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/human-relations")));
    	
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/influenced", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/human-relations")));
    	
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/relative", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/human-relations")));

    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/relation", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/human-relations")));
    	
    	originalPropertyHierarchyMap.put("http://dbpedia.org/ontology/knownFor", 
    			new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/human-relations")));
	
    	return originalPropertyHierarchyMap;
    }
    
	public static ArrayList<Element>  runEBMC(double ratio, HashMap<Element, HashSet<Element>> lattice,
			HashMap<Element, HashSet<String>> link2entities){
		ArrayList<Element> allLinkPatterns=new ArrayList<Element>();
		for(Element link: lattice.keySet()){
			if(link.getElements().size()>0){
				allLinkPatterns.add(link);
			}
		}
		HashSet<String> allrelatedentities=new HashSet<String>();
		for(Element link: link2entities.keySet()){
			allrelatedentities.addAll(link2entities.get(link));
		}
		
		double sumCost = 0.0;
		double w1=0.33;	double w2=0.33;	double w3=0.33;	int k=10;
		double costOfLPSet[]=new double[link2entities.keySet().size()];
		
		for(int i=0;i<allLinkPatterns.size();i++){
			double info=0.0;
			double c1=link2entities.get(allLinkPatterns.get(i)).size();
			info= (Math.log(allrelatedentities.size()+1.0)-Math.log(c1+1.0))/Math.log(allrelatedentities.size()+1.0);
			
			double conc=0.0;
			double c2=0.0;
			if(lattice.containsKey(allLinkPatterns.get(i))){
				c2=lattice.get(allLinkPatterns.get(i)).size()-0.5;
				conc=Math.pow(1/2.0, c2);
			}
			
			double spec=0.0;
			spec=computSpec(allLinkPatterns.get(i), lattice);
			
			costOfLPSet[i]= w1*info + w2*conc +w3*spec +1.0/(k+1.0);	
		}
		
//		entity-->link pattern
		HashMap<String, HashSet<Element>> entity2links=new HashMap<String, HashSet<Element>>();
		for(Element linkpattern:link2entities.keySet()){
			for(String e:link2entities.get(linkpattern)){
				if(entity2links.containsKey(e)){
					entity2links.get(e).add(linkpattern);
				}else{
					HashSet<Element> linkpatterns=new HashSet<Element>();
					linkpatterns.add(linkpattern);
					entity2links.put(e, linkpatterns);
				}
			}
		}
		
		ArrayList<String> allEntities=new ArrayList<String>();
		for(String e: entity2links.keySet()){
			allEntities.add(e);
		}
		double weightOfEntities[]=new double[entity2links.keySet().size()];
		for(int i=0;i<allEntities.size();i++){
			weightOfEntities[i]= 1.0;			
		}
		
		double Budget;
		for(int i=0;i<costOfLPSet.length;i++){
			sumCost=sumCost+costOfLPSet[i];
		}
		
			Budget=sumCost*ratio;

		int indexOfLPSet[];
		int indexOfElement[];
		double membershipValue[];
		
		int sumEdge=0;
		for(int i=0;i<allLinkPatterns.size();i++)
		{
			sumEdge=sumEdge+link2entities.get(allLinkPatterns.get(i)).size();
		}
		
		indexOfLPSet=new int[sumEdge+1];
		indexOfElement=new int[sumEdge+1];
		membershipValue=new double[sumEdge+1];

		int index=0;
		for(int i=0;i<allLinkPatterns.size();i++){
			HashSet<String> reachedentities=link2entities.get(allLinkPatterns.get(i));
			for(String e:reachedentities){
				indexOfLPSet[index]=i;
				indexOfElement[index]=allEntities.indexOf(e);
				membershipValue[index]= 1.0;
				index++;
			}
		}
		
		
		int resultLinkpatternIndex[]=EBMCLauncher.getEBMCResult(costOfLPSet,weightOfEntities,
				Budget,indexOfLPSet,indexOfElement,membershipValue);
		
		ArrayList<Element> selectedlinkpattern=new ArrayList<Element>();

		for(int i=0;i<resultLinkpatternIndex.length;i++){
			selectedlinkpattern.add(allLinkPatterns.get(resultLinkpatternIndex[i]));
		}
		return selectedlinkpattern;
	}
	
	public static double computSpec(Element lp, HashMap<Element, HashSet<Element>> lattice){
		double depth=1.0;
		double spec=0.0;
		if(!lattice.containsKey(lp)){
			spec=1.0;
		}else{
			HashSet<Element> childs= new HashSet<Element>();
			childs.addAll(lattice.get(lp));
			if(childs.size()==0){
				spec=1.0;
			}else{
				int flag =1;
				while(childs.size()!=0){
					depth++;
					HashSet<Element> nextchilds= new HashSet<Element>();
					for(Element childlp:childs){
						if(lattice.containsKey(childlp)){
							nextchilds.addAll(lattice.get(childlp));
						}else{
							depth++;
							flag=0;
							break;
						}
					}
					if(flag==1){
						childs.clear();
						childs.addAll(nextchilds);
					}else
						break;
				}
			}
		}
		spec= (10.0-depth)/10.0;
		return spec;
	}
    
	public static ArrayList<String> rankByLexiOrder(ArrayList<String> allLinks){
    	ArrayList<String> linkList=new ArrayList<String>();
    	String[] arrayToSort=new String[allLinks.size()];
    	for(int i=0;i<allLinks.size();i++){
    		arrayToSort[i]=allLinks.get(i);
    	}
    	Arrays.sort(arrayToSort,String.CASE_INSENSITIVE_ORDER);
    	for(int i=0;i<arrayToSort.length;i++){
    		linkList.add(arrayToSort[i]);
    	}
    	return linkList;
    }
    
    public static int makeHashCode(ArrayList<String> allLinks){
		final int prime = 31;
		int result = 17;
		for(int i=0;i<allLinks.size();i++){
			result = prime * result + allLinks.get(i).hashCode();
		}
		return result;
    }

	public static void main(String[] args) {
		
	}


    
    
    
    
    
    
    
    
    
    
    
}
