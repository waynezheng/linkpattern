package linkPatternService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import linkPatternUtil.Element;
import linkPatternUtil.Link;
import linkPatternUtil.ThreeTuple;
import linkPatternUtil.UserData;


public interface SemanticLink {
	
//	//	所有Link:交的+交之外
//	public ArrayList<Element> getAllKindsOfLinks(UserData userData);
////	//	交的link到达的实体集 
//	public ArrayList<String> getEndSetbyLink(Element link);

	public ArrayList<String> getAllEndSet(UserData userData);
//	//	link的交达到的实体集
	public ArrayList<String> getEndSetbyIntersectionOperationLinks(ArrayList<Element> links);
 //
	public ArrayList<String> getEndSetbyIntersectionOperationOriginalLinks(ArrayList<Element> links);
	//	获取列表型的links
	public ArrayList<Element> getAllSinglePropLinks(UserData userData);
	//	获取单prop链接到的实体
	public ArrayList<String> getSinglePropLinkedEntities(Element e);
	
	//	获取完整的格
	public HashMap<Element, HashSet<Element>> getLattice();
	//	获取格中每个link对应的实体
	public HashMap<Element, HashSet<String>> getLink2entities();
	//	获取反向的格
	public HashMap<Element, HashSet<Element>> getInverseLattice();
	//	建格
	public void bordat();
	
	
	//BMC
	public ArrayList<Element> getLinkPatternsByBMC03(ArrayList<Element> links, HashMap<Element, HashSet<Element>> lattice,
			HashMap<Element, HashSet<String>> Link2entities);
	
	public ArrayList<Element> getLinkPatternsByBMC05(ArrayList<Element> links, HashMap<Element, HashSet<Element>> lattice,
			HashMap<Element, HashSet<String>> Link2entities);
	
	public ArrayList<Element> getLinkPatternsByBMC08(ArrayList<Element> links, HashMap<Element, HashSet<Element>> lattice,
			HashMap<Element, HashSet<String>> Link2entities);

	
}
