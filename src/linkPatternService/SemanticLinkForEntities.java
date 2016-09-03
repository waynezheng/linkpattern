package linkPatternService;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import linkPatternUtil.Element;
import linkPatternUtil.TwoTuple;
import linkPatternUtil.UserData;
import database.DBConnection;
import database.SingleEntity;


public class SemanticLinkForEntities implements SemanticLink {

	/**
	 * @param args
	 */
	
	//建一个path索引！<path,{ends}>
	private HashMap<String,HashSet<String>> allPathMap= new HashMap<String,HashSet<String>>();
	private HashMap<String,HashSet<String>> originalallPathMap= new HashMap<String,HashSet<String>>();
//	原始上下位的层次关系p->sup;  sup->p
	private HashMap<String, HashSet<String>> originalPropertyHierarchyMap=new HashMap<String, HashSet<String>>();
	private HashMap<String, HashSet<String>> inverseoriginalPropertyHierarchyMap=new HashMap<String, HashSet<String>>();

//	当前格中外延的全部组合
	private HashSet<ArrayList<String>> currentUris = new HashSet<ArrayList<String>>(); 
//	当前格中所有的内涵	
	private HashSet<Element> currentElements = new HashSet<Element>();
//	所有的props
	private HashSet<String> allPaths = new HashSet<String>();
//	output
	private HashMap<Element, HashSet<Element>> lattice = new HashMap<Element, HashSet<Element>>();
	private HashMap<Element, HashSet<String>> link2entities = new HashMap<Element, HashSet<String>>();

	private HashSet<String> needfilterPuriSet=new HashSet<String>();
	private HashSet<String> needfilterPuriSet1=new HashSet<String>();
	private HashSet<String> currentUriSet=new HashSet<String>();


	public static final String FIELD_URI = "uri";
	
	
	public SemanticLinkForEntities(UserData userData, HashSet<String> currentCollection, ArrayList<TwoTuple<String,HashSet<String>>> corfMemberURIs){
    	System.out.println("------SemanticLinkForEntities-------");
    	needfilterPuriSet=CommonServiceForLink.add2needFilterUriSet();
    	System.out.println("! "+ currentCollection);
    	currentUriSet.addAll(currentCollection);
    	
    	for(String euri:currentCollection){
//    		buildPathfromCurrentUri(userData, euri);
    		newbuildPathfromCurrentUri(userData, euri);
    	}

		mergePathbypropertysamelocalname();
		newbuildOriginalPropertyHierarchyMap();
	}
	
	//创建paths
    public void newbuildPathfromCurrentUri(UserData userData, String currentUri){
    	HashMap<String, HashSet<String>> tempallPathMap=LogLuceneIndexer.findProperty2ValueMap(currentUri);
    	for(String path:tempallPathMap.keySet() ){
    		if(allPathMap.containsKey(path)){
    			for(String e: tempallPathMap.get(path)){
    				allPathMap.get(path).add(e);
    			}	
    		}else{
    			allPathMap.put(path, tempallPathMap.get(path));
    		}
    	}
    }
	
	
	//创建paths
    public void buildPathfromCurrentUri(UserData userData, String currentUri){
    	SingleEntity se = new SingleEntity(currentUri);
	    ArrayList<String> forwardProperties=new ArrayList<String>();
	    Connection conn =  DBConnection.getConnection();
	    HashSet<String> fProperties = se.getForwardProperties(conn);
	    for(String puri : fProperties){
		    forwardProperties.add(puri);
	    }
	    if(forwardProperties.size()!=0){
		    bulidForwardPathForuri(currentUri,forwardProperties,se,conn);
	    }
	
	    ArrayList<String> backwardProperties=new ArrayList<String>();
	    HashSet<String> bProperties = se.getBackwardProperties(conn);
	    for(String puri : bProperties){
		    backwardProperties.add(puri);
        }
	    if(backwardProperties.size()!=0){
		    bulidBackwardPathForuri(currentUri,backwardProperties,se,conn);
		}
	    
	    try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
  //正向paths
    public void bulidForwardPathForuri(String currentUri,ArrayList<String> forwardProperties,SingleEntity se, Connection conn){
    	HashSet<String> visitedProperties = new HashSet<String>();
    	for(String forwardpro:forwardProperties){
    		if(!needfilterPuriSet.contains(forwardpro)&&!visitedProperties.contains(forwardpro)){
    			HashSet<String> sameAsProperties=new HashSet<String>();
//    			sameAsProperties=CommonServiceForLink.getEquivalentPropertyFromDB(forwardpro);
    			sameAsProperties.add(forwardpro);
    			for(String sameP:sameAsProperties){
    				visitedProperties.add(sameP);
    			}
    			String forwardpro1="0"+forwardpro;
    			ArrayList<String> path=new ArrayList<String>();
    			path.add(forwardpro1);
    			
    			for(String p:sameAsProperties){
    				if(forwardProperties.contains(p)){
        				HashSet<String> forwardTriple=new HashSet<String>();
        				forwardTriple=se.getForwardPropertyValue(p,conn);
        				if(forwardTriple.size()!=0){
        					for(String ores : forwardTriple){
        						putonePathToallPathMap(currentUri,path,ores);
        					}	
        				}
    				}
    			}
		  }
	   }
    }
          
//反向paths
    public void bulidBackwardPathForuri(String currentUri,ArrayList<String> backwardProperties, SingleEntity se, Connection conn){
    	HashSet<String> visitedProperties = new HashSet<String>();
    	for(String backwardpro:backwardProperties){
    		if(!needfilterPuriSet.contains(backwardpro)&&!visitedProperties.contains(backwardpro)){
    			HashSet<String> sameAsProperties=new HashSet<String>();
//    			sameAsProperties=CommonServiceForLink.getEquivalentPropertyFromDB(backwardpro);
    			sameAsProperties.add(backwardpro);
    			for(String sameP:sameAsProperties){
    				visitedProperties.add(sameP);
    			}
    			String backwardpro1="1"+backwardpro;
    			ArrayList<String> path=new ArrayList<String>();
    			path.add(backwardpro1);
    			
    			for(String p:sameAsProperties){
    				HashSet<String> backwardTriple=new HashSet<String>();
    				backwardTriple=se.getBackwardPropertyValue(p, conn);
    				if(backwardTriple.size()!=0){
    					for(String sres : backwardTriple){ 
    						putonePathToallPathMap(currentUri,path,sres);
    					} 
    				}
    			}
    		}
    	}
    }
	
  //建索引！<path,{ends}> <end,{paths}>
    public void putonePathToallPathMap(String startRes, ArrayList<String> path,String destRes){
    	String propertychain="";
    	if(path.size()>1){
    		for(String pathitem:path){
			    propertychain+=pathitem+";";
		    }
    	}else{
    		propertychain=path.get(0);
    	}
//    	System.out.println("propertychain"+propertychain);
    	String endElement=destRes;
//      if(!currentUriSet.contains(endElement)){
		   if(allPathMap.containsKey(propertychain)){
			   allPathMap.get(propertychain).add(endElement);
		   }else{
				HashSet<String> endSet=new HashSet<String>();
				endSet.add(endElement);
				allPathMap.put(propertychain, endSet);
	       }
//      }
    }
    
    public void mergePathbypropertysamelocalname() {
//    	HashMap<String,ArrayList<String>> localnameToPathMap= new HashMap<String,ArrayList<String>>();
//    	for(String p:allPathMap.keySet()){
//    		String pLocalName=CommonServiceForLink.getLocalName(p);
//    		if(localnameToPathMap.containsKey(pLocalName)){
//    			localnameToPathMap.get(pLocalName).add(p);
//    		}else{
//    			ArrayList<String> pset=new ArrayList<String>();
//    			pset.add(p);
//    			localnameToPathMap.put(pLocalName, pset);
//    		}
//    	}
//		if(localnameToPathMap.keySet().size()>0){
//			for(String pLocalName:localnameToPathMap.keySet()){
//				ArrayList<String> samenamePset=localnameToPathMap.get(pLocalName);
//				for(String p:samenamePset){
//					for(String end:allPathMap.get(p)){
//						allPathMap.get(samenamePset.get(0)).add(end);
//					}
//				}
//				for(int i=1;i<samenamePset.size();i++){
//					allPathMap.remove(samenamePset.get(i));
//				}
//			}
//		}
		
  		for(String p:allPathMap.keySet()){
  			HashSet<String> endset=new HashSet<String>();
  			for(String e:allPathMap.get(p)){
  				endset.add(e);
  			}
  			originalallPathMap.put(p, endset);
  	    }
	}

    //生成上层的Link	
    public void newbuildOriginalPropertyHierarchyMap(){
    	System.out.println("--newbuildPropertyHierarchy----");
    	originalPropertyHierarchyMap=CommonServiceForLink.buildOriginalPropertyHierarchyMap();
    	for(String p: originalPropertyHierarchyMap.keySet()){
    		for(String supP:originalPropertyHierarchyMap.get(p)){
    			if(inverseoriginalPropertyHierarchyMap.containsKey(supP)){
    				inverseoriginalPropertyHierarchyMap.get(supP).add(p);
	  		 	}else{
	  		 		HashSet<String> children=new HashSet<String>();
	  		 		children.add(p);
	  		 		inverseoriginalPropertyHierarchyMap.put(supP, children);
	  			}	
    		}
    	}
    	
    	HashSet<String> allProperties=new HashSet<String>();
  		for(String p:allPathMap.keySet()){
  	    	allProperties.add(p);
  	    }

  		HashSet<String> needConSupProperties=new HashSet<String>();
  		needConSupProperties.addAll(allProperties);
  		HashSet<String> visitedProperties=new HashSet<String>();
  		
  		while(needConSupProperties.size()>0){
  			HashSet<String> tempneedConSupProperties=new HashSet<String>();
  			for(String p: needConSupProperties){
  				if(!visitedProperties.contains(p)){
  					tempneedConSupProperties.add(p);
  				}
  			}
	  		if(tempneedConSupProperties.size()==0){
  		  		break;
  		  	}
  			
  	  		for(String puri:tempneedConSupProperties){
  	  			visitedProperties.add(puri);
  	  			needConSupProperties.remove(puri);
  	  		    String flag=puri.substring(0,1);
  	  			String newpuri=puri.substring(1);
  	  			if(!originalPropertyHierarchyMap.containsKey(newpuri)){
  	  				break;
  	  			}
  	  			HashSet<String> supProperty=originalPropertyHierarchyMap.get(newpuri);
  	  			supProperty.remove(puri);
  		  		for(String supP:supProperty){
  		  			needConSupProperties.add(supP);
  	  	  			if(!inverseoriginalPropertyHierarchyMap.containsKey(supP)){
  	  	  				break;
  	  	  			}
  		 		    HashSet<String> brotherAndself=inverseoriginalPropertyHierarchyMap.get(supP);
//  		 		 System.out.println("brotherAndself--"+brotherAndself);

  		 		    HashSet<String> endsetOfsupP=new HashSet<String>();
  		 		    for(String p: brotherAndself ){
  		 		    	String newP=flag+p;
  		 		    	if(allPathMap.containsKey(newP)){
  		 		    		endsetOfsupP.addAll(allPathMap.get(newP));
  		 		    	}
  		 		    }
  		 		    String newsupP=flag+supP;
		 		    if(allPathMap.containsKey(newsupP)){
  		 		    	allPathMap.get(newsupP).addAll(endsetOfsupP);
  		 		    }else{
  		 		    	allPathMap.put(newsupP, endsetOfsupP);
  		 		    }
		 		    
		 		    if(originalPropertyHierarchyMap.containsKey(puri)){
		 		    	originalPropertyHierarchyMap.get(puri).add(newsupP);
  		 		    }else{
  		 		    	HashSet<String> newsupPset= new HashSet<String>();
  		 		    	newsupPset.add(newsupP);
  		 		    	originalPropertyHierarchyMap.put(puri, newsupPset);
  		 		    } 
  		  		}
  	  		}
  		 }
  		
		
		for (String u: allPathMap.keySet()) {
			System.out.println( "-key--" + u);
		}
  		
  		
  		
    }


    //获取当前uri
	public HashSet<String> getMemberURIs(){
		return this.currentUriSet;
	}
    
    
    public ArrayList<String> getAllEndSet(UserData userData) {
		HashSet<String> endset=new HashSet<String>();
		ArrayList<String> newendset=new ArrayList<String>();
		for (String path: allPathMap.keySet()) {
			endset.addAll(allPathMap.get(path));
		}
		newendset.addAll(endset);
		return newendset;
	}
  
	public ArrayList<String> getEndSetbyIntersectionOperationLinks(
			ArrayList<Element> links) {	
		HashSet<String> endset=new HashSet<String>();
		ArrayList<String> newendset=new ArrayList<String>();
		if (links.size() == 0) {
			for (String path: allPathMap.keySet()) {
				endset.addAll(allPathMap.get(path));
			}
		}
		else if (links.size() == 1) {
//			System.out.println("links.get(0)--"+links.get(0));
			endset.addAll(getEndSetbySingleElement(links.get(0)));
		}
		else if (links.size() > 1) {
			endset.addAll(getEndSetbySingleElement(links.get(0)));
			for (int i=0; i<links.size(); i++) {
				endset.retainAll(getEndSetbySingleElement(links.get(i)));
			} 
		}
		
		newendset.addAll(endset);
		newendset = CommonServiceForLink.rankByLexiOrder(newendset);
		return newendset;
	}
	
	public HashSet<String> getEndSetbySingleElement(Element e) {
		HashSet<String> endset = new HashSet<String>();
		ArrayList<String> props = e.getElements();
		if (props.size() == 1) {
			if (allPathMap.get(props.get(0)).size() != 0)
				endset.addAll(allPathMap.get(props.get(0)));
		}
		else if (props.size() > 1) {
			endset.addAll(allPathMap.get(props.get(0)));
			for (int i=1; i<props.size(); i++) {
				endset.retainAll(allPathMap.get(props.get(i)));
			}
		}
		return endset;
	}
	
	public ArrayList<String> getEndSetbyIntersectionOperationOriginalLinks(
			ArrayList<Element> links) {
		// TODO Auto-generated method stub
		HashSet<String> endset=new HashSet<String>();
		ArrayList<String> newendset=new ArrayList<String>();
		if (links.size() == 0) {
			for (String path: originalallPathMap.keySet()) {
				endset.addAll(originalallPathMap.get(path));
			}
		}
		else if (links.size() == 1) {
//			System.out.println("links.get(0)--"+links.get(0));
			endset.addAll(getEndSetbySingleElementByOriginal(links.get(0)));
		}
		else if (links.size() > 1) {
			endset.addAll(getEndSetbySingleElementByOriginal(links.get(0)));
			for (int i=0; i<links.size(); i++) {
				endset.retainAll(getEndSetbySingleElementByOriginal(links.get(i)));
			} 
		}
		
		newendset.addAll(endset);
		newendset = CommonServiceForLink.rankByLexiOrder(newendset);
		return newendset;
	}
	
	public HashSet<String> getEndSetbySingleElementByOriginal(Element e) {
		HashSet<String> endset = new HashSet<String>();
		ArrayList<String> props = e.getElements();
		if (props.size() == 1) {
			endset.addAll(originalallPathMap.get(props.get(0)));
		}
		else if (props.size() > 1) {
			endset.addAll(originalallPathMap.get(props.get(0)));
			for (int i=1; i<props.size(); i++) {
				endset.retainAll(originalallPathMap.get(props.get(i)));
			}
		}
		return endset;
	}
	
	
    public void bordat() {

		boolean topisExisted = false; //是否存在真实的root节点
		int totalnum = getAllLinkedEntities().size(); //连接到的所有实体的数目
		allPaths = new HashSet<String>(allPathMap.keySet());
		ArrayList<String> rootList = new ArrayList<String>();
		for (String path: allPaths) {
			if (allPathMap.get(path).size() == totalnum) {
				topisExisted = true;
				rootList.add(path);
			}
		}
		
		if (!topisExisted) {
			System.out.println("root isn't existed");
			Element root = new Element();
			lattice.put(root, new HashSet<Element>());
			addNode(root);
		}
		else {
			System.out.println("root is existed");
			Element root = new Element();
			rootList = CommonServiceForLink.rankByLexiOrder(rootList);
			root.setElements(rootList);
			if (rootList.size() > 1) 
				root.setIntersectionFlag(true);
			lattice.put(root, new HashSet<Element>());
			addNode(root);
		}
	}
	
	public void addNode(Element u) {
		if (!currentElements.contains(u)) {
			currentElements.add(u);
		}
		ArrayList<String> uprops = u.getElements();
		if (uprops.size() != 0) {
			link2entities.put(u, new HashSet<String>(getEntities(uprops)));
		}
		else {
			link2entities.put(u, getAllLinkedEntities());
		}
		HashSet<String> candidate = new HashSet<String>(allPaths);
		candidate.removeAll(uprops);
		TreeMap<Integer, ArrayList<Element>> num2entities = new TreeMap<Integer, ArrayList<Element>>(new Comparator<Integer>() {
			public int compare(Integer o0, Integer o1) {
				// TODO Auto-generated method stub
				return o1-o0;
			}
		});
		HashMap<Element, HashSet<String>> entities2paths = new HashMap<Element, HashSet<String>>();
		
		for (String prop: candidate) {
			ArrayList<String> props = new ArrayList<String>(uprops);
			props.add(prop);
			ArrayList<String> entities = getEntities(props);
			int num = entities.size();
			if (num != 0) {
				Element e = new Element();
				e.setElements(entities);
				if (!num2entities.containsKey(num)) {
					ArrayList<Element> es = new ArrayList<Element>();
					es.add(e);
					num2entities.put(num, es);
				}
				else {
					ArrayList<Element> es = num2entities.get(num);
					es.add(e);
					num2entities.put(num, es);
				}
				if (!entities2paths.containsKey(e)) {
					HashSet<String> ps = new HashSet<String>();
					ps.add(prop);
					entities2paths.put(e, ps);
				}
				else {
					HashSet<String> ps = entities2paths.get(e);
					ps.add(prop);
					entities2paths.put(e, ps);
				}
			}
		}
		for (int num: num2entities.keySet()) {
			ArrayList<Element> eList = num2entities.get(num);
			for (Element e: eList) {
				ArrayList<String> uris = e.getElements();
				HashSet<String> props = entities2paths.get(e);
				props.addAll(uprops);
				ArrayList<String> propList = new ArrayList<String>(props);
				propList = CommonServiceForLink.rankByLexiOrder(propList);
				Element v = new Element();
				v.setElements(propList);
				if (propList.size() > 1) {
					v.setIntersectionFlag(true);
				}
				if (!currentUris.contains(uris) || currentElements.contains(v)) {	
					if (!lattice.containsKey(u)) {
						HashSet<Element> eset = new HashSet<Element>();
						eset.add(v);
						lattice.put(u, eset);
					}
					else {
						HashSet<Element> eset = lattice.get(u);
						eset.add(v);
						lattice.put(u, eset);
					}
					currentUris.add(uris);
					addNode(v);
				}
			}
		}			
	}
	
	public ArrayList<String> getEntities(ArrayList<String> props) { //返回排好序的uri list
		HashSet<String> uris = getAllLinkedEntities();
		for (String prop: props) {
			uris.retainAll(allPathMap.get(prop));
		}
		ArrayList<String> uriList = new ArrayList<String>(uris);
		uriList = CommonServiceForLink.rankByLexiOrder(uriList);
		return uriList;
	}
	
	public HashSet<String> getAllLinkedEntities() {
		HashSet<String> set = new HashSet<String>();
		for (String path: allPathMap.keySet()) {
			set.addAll(allPathMap.get(path));
		} 
		return set;
	}
	
	public ArrayList<String> getIrreducibleList(ArrayList<String> paths) {
		if (paths.size() == 0) 
			return paths;
		ArrayList<String> newPaths = new ArrayList<String>(paths); 
		for (String path: paths) {
			if (originalPropertyHierarchyMap.containsKey(path)) {
				HashSet<String> ancestor = originalPropertyHierarchyMap.get(path);
				newPaths.removeAll(ancestor);
			}
		}
		newPaths = CommonServiceForLink.rankByLexiOrder(newPaths);
		return newPaths;
	}
	
	public Element getIrreducibleElement(Element e) {
		Element u = new Element();
		u.setElements(getIrreducibleList(e.getElements()));
		u.setIntersectionFlag(e.isIntersectionLink());
		return u;
	}
	
    public HashMap<Element, HashSet<Element>> getLattice() {
    	HashMap<Element, HashSet<Element>> reducedLattice = new HashMap<Element, HashSet<Element>>();
    	for (Element e: lattice.keySet()) {
    		Element u = getIrreducibleElement(e);
    		if (lattice.get(e).size() == 0) {
    			reducedLattice.put(u, new HashSet<Element>());
    		}
    		else {
    			HashSet<Element> us = new HashSet<Element>();
    			for (Element v: lattice.get(e)) {
    				us.add(getIrreducibleElement(v));
    			}
    			reducedLattice.put(u, us);
    		}
    	}
    	return reducedLattice;
    }
	
    public HashMap<Element, HashSet<String>> getLink2entities() {
    	HashMap<Element, HashSet<String>> reducedLink2entities = new HashMap<Element, HashSet<String>>();
    	for (Element e: link2entities.keySet()) {
    		reducedLink2entities.put(getIrreducibleElement(e), link2entities.get(e));
    	}
      	return reducedLink2entities;
    }
    
    public ArrayList<Element> getAllSinglePropLinks(UserData userData) {
    	ArrayList<Element> links = new ArrayList<Element>();
    	for (String p: originalallPathMap.keySet()) {
    		Element e = new Element();
    		ArrayList<String> paths = new ArrayList<String>();
    		paths.add(p);
    		e.setElements(paths);
    		links.add(e);
    	}
//		Collections.sort(links, new Comparator<Element>(){
//			@Override
//			public int compare(Element arg0, Element arg1) {
//				int en0 = getSinglePropLinkedEntities(arg0).size(); 
//				int en1 =  getSinglePropLinkedEntities(arg1).size();
//				if(en0 > en1)
//					return -1;
//				if(en0 < en1)
//					return 1;
//				return 0;
//			}
//		});
    	return links;
    }
    
    public ArrayList<String> getSinglePropLinkedEntities(Element e) {
    	String prop = e.getElements().get(0);
    	if (originalallPathMap.containsKey(prop)) {
    		HashSet<String> eset = originalallPathMap.get(prop);
    		ArrayList<String> elist = new ArrayList<String>(eset);
    		elist = CommonServiceForLink.rankByLexiOrder(elist);
    		return elist;
    	}
    	else 
    		return new ArrayList<String>();
    }
    
    public HashMap<Element, HashSet<Element>> getInverseLattice() {
    	HashMap<Element, HashSet<Element>> lattice = getLattice();
    	HashMap<Element, HashSet<Element>> inLattice = new HashMap<Element, HashSet<Element>>();
    	for (Element e: lattice.keySet()) {
    		for (Element s: lattice.get(e)) {
    			if (!inLattice.containsKey(s)) {
    				HashSet<Element> set = new HashSet<Element>();
    				set.add(e);
    				inLattice.put(s, set);
    			}
    			else {
    				HashSet<Element> set = inLattice.get(s);
    				set.add(e);
    				inLattice.put(s, set);
    			}
    		}
    	}
    	
    	HashSet<Element> roots = new HashSet<Element>(lattice.keySet());
    	roots.removeAll(inLattice.keySet());
    	for (Element e: roots) {
    		inLattice.put(e, new HashSet<Element>());
    	}
    	return inLattice;
    }
    
	public ArrayList<Element> getLinkPatternsByBMC(double ratio, ArrayList<Element> links,
			HashMap<Element, HashSet<Element>> lattice,
			HashMap<Element, HashSet<String>> Link2entities) {
		// TODO Auto-generated method stub
		ArrayList<Element> selectedlinkpattern =new ArrayList<Element>();

    	Element newroot=new Element();
		if(links.size()==0){
			for(Element linkpattern:link2entities.keySet()){
				if(linkpattern.getElements().size()==0){
					newroot=linkpattern;
				}
			}
			
			if(lattice.keySet().size()>10){
				lattice.remove(newroot);
				selectedlinkpattern = CommonServiceForLink.runEBMC(ratio, lattice, Link2entities);
			}else{
				for (Element u: Link2entities.keySet()) {
					selectedlinkpattern.add(u);
				}
				selectedlinkpattern.remove(newroot);
			}
			
		}else{
			newroot=links.get(links.size()-1);
			HashMap<Element, HashSet<Element>> minlattice=new HashMap<Element, HashSet<Element>>();
			if(lattice.containsKey(newroot)){
				minlattice.put(newroot, lattice.get(newroot));
				
				HashSet<Element> needPut2minlattice =new HashSet<Element>();
				HashSet<Element> children =lattice.get(newroot);
				for(Element child:children){
					needPut2minlattice.add(child);
				}
				
				
				while(needPut2minlattice.size()!=0){
					HashSet<Element> tempneedPut2minlattice =new HashSet<Element>();
					for(Element linkpattern:needPut2minlattice){
						tempneedPut2minlattice.add(linkpattern);
					}
					for(Element linkpattern:tempneedPut2minlattice ){
						needPut2minlattice.remove(linkpattern);
						if(lattice.containsKey(linkpattern)){
							minlattice.put(linkpattern, lattice.get(linkpattern));
						
							for(Element child:lattice.get(linkpattern)){
								needPut2minlattice.add(child);
							}
						}
					}
				}
			}
			
			HashMap<Element, HashSet<String>> minLink2entities=new HashMap<Element, HashSet<String>>();
			for (Element u: minlattice.keySet()) {
				minLink2entities.put(u, Link2entities.get(u));
				if (minlattice.get(u).size() != 0) {
					for (Element v: minlattice.get(u)) {
						minLink2entities.put(v, Link2entities.get(v));
				    }
				}
			}
			if(minlattice.keySet().size()>10){
				minlattice.remove(newroot);
				selectedlinkpattern = CommonServiceForLink.runEBMC(ratio, minlattice, minLink2entities);
			}else{
				for (Element u: minLink2entities.keySet()) {
					selectedlinkpattern.add(u);
				}
				selectedlinkpattern.remove(newroot);
			}
		}
		return selectedlinkpattern;
	}
	
	public HashMap<String, HashSet<String>> findProperty2ValueMap(String currentUri){
//		System.out.println("property:"+property); 
		HashMap<String,HashSet<String>> allPropertyMap= new HashMap<String,HashSet<String>>();
		IndexSearcher is = LogLuceneIndexer.getIndexSearcher();
		
		Term term = new Term(FIELD_URI,currentUri); 
		TermQuery luceneQuery = new TermQuery(term); 
		TopDocs hitdocs = null;
		try {
			hitdocs = is.search(luceneQuery, 1);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

        if(hitdocs!=null){
        	for(ScoreDoc sd:hitdocs.scoreDocs){
        			int index=sd.doc;
			        Document doc;
			        try {
				        doc = is.doc(index);
				        for(Fieldable field: doc.getFields()){
				        	if(!field.name().equals(FIELD_URI)){
				        		String fieldvalue=doc.get(field.name()).toString();
				        		if(fieldvalue.contains("http:")){
			        				HashSet<String> endset=new HashSet<String>();
			        				String ss[]=fieldvalue.split(" ");
			        				if(ss.length<100){
			        					for (int i = 0; i < ss.length; i++) {
				        					 endset.add(ss[i]); 
				        				}
				        				allPropertyMap.put(field.name(), endset);
			        				}else{
			        					for (int i = 0; i < 100; i++) {
			        					 endset.add(ss[i]); 
			        				    }
			        				    allPropertyMap.put(field.name(), endset);
			        				}
				        		}
				        	}
				        }    
		            } catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				        e.printStackTrace();
			        } catch (IOException e) {
				// TODO Auto-generated catch block
				        e.printStackTrace();
			        }	
          }
        }
		return allPropertyMap;		
	}
	

    public static void main(String[] args) {
		// TODO Auto-generated method stub
    	UserData userData=new UserData();
    	HashSet<String> currentCollection = new HashSet<String>();
    	currentCollection.add("http://dbpedia.org/resource/Steven_Spielberg");
    	currentCollection.add("http://dbpedia.org/resource/James_Cameron");
    	ArrayList<TwoTuple<String,HashSet<String>>> corfMemberURIs = new ArrayList<TwoTuple<String, HashSet<String>>>();
    	SemanticLinkForEntities se = new SemanticLinkForEntities(userData, currentCollection, corfMemberURIs);
    	System.out.println("inited");
		se.bordat();
		HashMap<Element, HashSet<Element>> la = se.getLattice();
		for (Element u: la.keySet()) {
			if (la.get(u).size() != 0) {
				for (Element v: la.get(u)) {
					System.out.println(u.getElements() + "---" + v.getElements());
				}
			}
			else {
				System.out.println(u.getElements() + "---" + "NULL");
			}
		}
		
		HashMap<Element, HashSet<String>> link2entities=se.getLink2entities();
		System.out.println("link pattern总数--" + la.keySet().size());
		System.out.println("实体总数--" + se.getAllEndSet(userData).size());
	
		
		System.out.println("OK ");
		System.exit(0);	
		
	}

	@Override
	public ArrayList<Element> getLinkPatternsByBMC03(ArrayList<Element> links,
			HashMap<Element, HashSet<Element>> lattice,
			HashMap<Element, HashSet<String>> Link2entities) {
		// TODO Auto-generated method stub
		
		ArrayList<Element> selectedlinkpattern =new ArrayList<Element>();
		selectedlinkpattern=getLinkPatternsByBMC(0.3, links, lattice, Link2entities);
		return selectedlinkpattern;
	}
	@Override
	public ArrayList<Element> getLinkPatternsByBMC05(ArrayList<Element> links,
			HashMap<Element, HashSet<Element>> lattice,
			HashMap<Element, HashSet<String>> Link2entities) {
		// TODO Auto-generated method stub
		ArrayList<Element> selectedlinkpattern =new ArrayList<Element>();
		selectedlinkpattern=getLinkPatternsByBMC(0.5, links, lattice, Link2entities);
		return selectedlinkpattern;
	}
	@Override
	public ArrayList<Element> getLinkPatternsByBMC08(ArrayList<Element> links,
			HashMap<Element, HashSet<Element>> lattice,
			HashMap<Element, HashSet<String>> Link2entities) {
		// TODO Auto-generated method stub
		ArrayList<Element> selectedlinkpattern =new ArrayList<Element>();
		selectedlinkpattern=getLinkPatternsByBMC(0.8, links, lattice, Link2entities);
		return selectedlinkpattern;
	}


}
