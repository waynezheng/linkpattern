package linkPatternServlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import linkPatternService.AutoComplete;
import linkPatternService.CommonServiceForLink;
import linkPatternService.SemanticLink;
import linkPatternService.SemanticLinkForEntities;
import linkPatternService.SemanticLinkForSingleEntity;
import linkPatternUtil.Element;
import linkPatternUtil.TwoTuple;
import linkPatternUtil.UserData;

import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import database.DBConnection;

/**
 * Servlet implementation class SLinkServlet
 */
@WebServlet("/SLinkServlet")
public class SLinkServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SLinkServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    private ArrayList<String> getOption(HttpServletRequest request){
		String option = null;
		String json = null;
		if (request.getParameter("getAllLinks") != null) {
			json = request.getParameter("getAllLinks");
			option = "getAllLinks";
		}
		else if (request.getParameter("getLinkedEntities") != null) {
			json = request.getParameter("getLinkedEntities");
			option = "getLinkedEntities";
		}
		else if (request.getParameter("getOriginalLinkedEntities") != null) {
			json = request.getParameter("getOriginalLinkedEntities");
			option = "getOriginalLinkedEntities";
		}
		else if (request.getParameter("getAllLinkedEntities") != null) {
			json = request.getParameter("getAllLinkedEntities");
			option = "getAllLinkedEntities";
		}
		else if (request.getParameter("getHierarchicalLinks") != null) {
			json = request.getParameter("getHierarchicalLinks");
			option = "getHierarchicalLinks";
		}
		else if (request.getParameter("getBMCLinks1") != null) {
			json = request.getParameter("getBMCLinks1");
			option = "getBMCLinks1";
		}
		else if (request.getParameter("getBMCLinks2") != null) {
			json = request.getParameter("getBMCLinks2");
			option = "getBMCLinks2";
		}
		else if (request.getParameter("getBMCLinks3") != null) {
			json = request.getParameter("getBMCLinks3");
			option = "getBMCLinks3";
		}
		else if (request.getParameter("getAutoComplete") != null) {
			json = request.getParameter("getAutoComplete");
			option = "getAutoComplete";
		}
		ArrayList<String> l = new ArrayList<String>();
		l.add(option);
		l.add(json);
		
		return l;
		
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		System.out.println("enter servlet");
		ArrayList<String> optionList = getOption(request);
		String option = optionList.get(0);
		String json = optionList.get(1);
		
		HttpSession session = request.getSession();		
		UserData userData = (UserData) session.getAttribute("userData");
		if(userData == null){
			userData = new UserData();
			session.setAttribute("userData", userData);
		}
		
		if (option == null) {
			//do nothing
		}
		else if (option.equals("getAllLinks")) {
			System.out.println("getAllLinks");
			String str = URLDecoder.decode(json, "utf-8");
			JSONObject reqtJSON = (JSONObject)JSONValue.parse(str);
			String spoints = (String)reqtJSON.get("uri");
			System.out.println(spoints);
			JSONArray linkJA = new JSONArray();
			if (!spoints.contains(";")) {
				HashSet<String> corefCurrentUri = new HashSet<String>();
				String projectPath = 
						request.getSession().getServletContext().getRealPath("");
				SemanticLink se = userData.getPreviousSemanticLinkForSingleEntity(spoints, projectPath);
				se.bordat();
				final HashMap<Element, HashSet<String>> links2entities = se.getLink2entities();
				ArrayList<Element> links=new ArrayList<Element>();
				for(Element linkpattern: links2entities.keySet()){
					if(linkpattern.getElements().size()>0){
						links.add(linkpattern);
					}
					
				}
				Collections.sort(links, new Comparator<Element>(){
					@Override
					public int compare(Element arg0, Element arg1) {
						int en0 = links2entities.get(arg0).size(); 
						int en1 =  links2entities.get(arg1).size();
						if(en0 > en1)
							return -1;
						if(en0 < en1)
							return 1;
						return 0;
					}
				});
				for (Element link: links) { 
					JSONObject linkJO = new JSONObject();
					JSONArray props = new JSONArray();
					linkJO.put("label", link.getLabel());
					linkJO.put("flag", link.haveBNode());
					linkJO.put("blockID", link.getblockID());
					linkJO.put("intersectionflag", link.isIntersectionLink());
					linkJO.put("num", links2entities.get(link).size());
					ArrayList<String> elements = link.getElements();
					for (String ele: elements) {
						JSONObject prop = new JSONObject();
						prop.put("prop", ele);
						props.add(prop);
					}
					linkJO.put("props", props);
					linkJA.add(linkJO);
				}
			}
			else {
				ArrayList<TwoTuple<String,HashSet<String>>> corfMemberURIs = new ArrayList<TwoTuple<String,HashSet<String>>>();
				String[] uris = spoints.split(";");
				HashSet<String> currentUris = new HashSet<String>();
				CollectionUtils.addAll(currentUris, uris);
				SemanticLink es = userData.getPreviousSemanticLinkForEntities(currentUris);
				ArrayList<Element> links = es.getAllSinglePropLinks(userData);
				for (Element link: links) {
					JSONObject linkJO = new JSONObject();
					JSONArray props = new JSONArray();
					linkJO.put("label", link.getLabel());
					linkJO.put("flag", link.haveBNode());
					linkJO.put("blockID", link.getblockID());
					linkJO.put("intersectionflag", link.isIntersectionLink());
					linkJO.put("num", es.getSinglePropLinkedEntities(link).size());
					ArrayList<String> elements = link.getElements();
					for (String ele: elements) {
						JSONObject prop = new JSONObject();
						prop.put("prop", ele);
						props.add(prop);
					}
					linkJO.put("props", props);
					linkJA.add(linkJO);
				}	
			}
			JSONObject jsob = new JSONObject();
			jsob.put("links", linkJA);
			response.getWriter().print(jsob);
		}
		else if (option.equals("getLinkedEntities")) {
			String str = URLDecoder.decode(json, "utf-8");
			JSONObject reqtJSON = (JSONObject)JSONValue.parse(str);
			String spoints = (String)reqtJSON.get("uri");
			JSONArray taskArray = (JSONArray) reqtJSON.get("patternfilters");
			JSONArray flagArray = (JSONArray)taskArray.get(0);
			JSONArray blockIDArray = (JSONArray)taskArray.get(1);
			JSONArray intersectionflagArray = (JSONArray)taskArray.get(2);
			JSONArray propsArray = (JSONArray)taskArray.get(3);
			
			JSONArray result = new JSONArray();
			
			if (!spoints.contains(";")) {
				HashSet<String> corefCurrentUri = new HashSet<String>();
				String projectPath = 
						request.getSession().getServletContext().getRealPath("");
				SemanticLink se = userData.getPreviousSemanticLinkForSingleEntity(spoints, projectPath);
				ArrayList<Element> links = new ArrayList<Element>();
				for (int i=0; i<flagArray.size(); i++) {
					boolean intersectionflag = Boolean.parseBoolean((String)intersectionflagArray.get(i));
					JSONArray props = (JSONArray)propsArray.get(i);
					ArrayList<String> pathset = new ArrayList<String>();
					for (int j=0; j<props.size(); j++) {
						String path = (String)props.get(j);
						pathset.add(path);
					}
					Element link = new Element();
					pathset = CommonServiceForLink.rankByLexiOrder(pathset);
					link.setElements(pathset);
					link.setIntersectionFlag(intersectionflag);
					links.add(link);
				}
				ArrayList<String> entities = se.getEndSetbyIntersectionOperationLinks(links);
				for (String entity: entities) {
					result.add(entity);
				}
			}
			else {
				ArrayList<TwoTuple<String, HashSet<String>>> corfMemberURIs = new ArrayList<TwoTuple<String,HashSet<String>>>();
				String[] uris = spoints.split(";");
				HashSet<String> currentUris = new HashSet<String>();
				CollectionUtils.addAll(currentUris, uris);
				SemanticLink es = userData.getPreviousSemanticLinkForEntities(currentUris);
		//		SemanticLinkForEntities es = new SemanticLinkForEntities(userData, currentUris, corfMemberURIs);
		//		es.getAllKindsOfLinks(userData);
				ArrayList<Element> links = new ArrayList<Element>();
				for (int i=0; i<flagArray.size(); i++) {
				//	boolean flag = Boolean.parseBoolean((String)flagArray.get(i));
				//	int blockID = Integer.parseInt((String)blockIDArray.get(i));
					boolean intersectionflag = Boolean.parseBoolean((String)intersectionflagArray.get(i));
					JSONArray props = (JSONArray)propsArray.get(i);
					ArrayList<String> pathset = new ArrayList<String>();
					for (int j=0; j<props.size(); j++) {
						String path = (String)props.get(j);
						pathset.add(path);
					}
					Element link = new Element();
					pathset = CommonServiceForLink.rankByLexiOrder(pathset);
				//	link.setBlockID(blockID);
				//	link.setFlag(flag);
					link.setElements(pathset);
					link.setIntersectionFlag(intersectionflag);
					links.add(link);
				}
				ArrayList<String> entities = es.getEndSetbyIntersectionOperationLinks(links);
				for (String entity: entities) {
					result.add(entity);
				}
			}
			JSONObject jsob = new JSONObject();
			jsob.put("entities", result);
			response.getWriter().print(jsob);
		}
		else if (option.equals("getOriginalLinkedEntities")) {
			System.out.println("getLinkedEntities");
			String str = URLDecoder.decode(json, "utf-8");
			JSONObject reqtJSON = (JSONObject)JSONValue.parse(str);
			String spoints = (String)reqtJSON.get("uri");
			JSONArray taskArray = (JSONArray) reqtJSON.get("patternfilters");
			JSONArray flagArray = (JSONArray)taskArray.get(0);
			JSONArray blockIDArray = (JSONArray)taskArray.get(1);
			JSONArray intersectionflagArray = (JSONArray)taskArray.get(2);
			JSONArray propsArray = (JSONArray)taskArray.get(3);			
			JSONArray result = new JSONArray();
			
			if (!spoints.contains(";")) {
				HashSet<String> corefCurrentUri = new HashSet<String>();
				String projectPath = 
						request.getSession().getServletContext().getRealPath("");
				SemanticLink se = userData.getPreviousSemanticLinkForSingleEntity(spoints, projectPath);
				ArrayList<Element> links = new ArrayList<Element>();
				for (int i=0; i<flagArray.size(); i++) {
					boolean intersectionflag = Boolean.parseBoolean((String)intersectionflagArray.get(i));
					JSONArray props = (JSONArray)propsArray.get(i);
					ArrayList<String> pathset = new ArrayList<String>();
					for (int j=0; j<props.size(); j++) {
						String path = (String)props.get(j);
						pathset.add(path);
					}
					Element link = new Element();
					pathset = CommonServiceForLink.rankByLexiOrder(pathset);
					link.setElements(pathset);
					link.setIntersectionFlag(intersectionflag);
					links.add(link);
				}
				ArrayList<String> entities = se.getEndSetbyIntersectionOperationOriginalLinks(links);
				for (String entity: entities) {
					result.add(entity);
				}
			}
			else {
				ArrayList<TwoTuple<String, HashSet<String>>> corfMemberURIs = new ArrayList<TwoTuple<String,HashSet<String>>>();
				String[] uris = spoints.split(";");
				HashSet<String> currentUris = new HashSet<String>();
				CollectionUtils.addAll(currentUris, uris);
				SemanticLink es = userData.getPreviousSemanticLinkForEntities(currentUris);
				ArrayList<Element> links = new ArrayList<Element>();
				for (int i=0; i<flagArray.size(); i++) {
					boolean intersectionflag = Boolean.parseBoolean((String)intersectionflagArray.get(i));
					JSONArray props = (JSONArray)propsArray.get(i);
					ArrayList<String> pathset = new ArrayList<String>();
					for (int j=0; j<props.size(); j++) {
						String path = (String)props.get(j);
						pathset.add(path);
					}
					Element link = new Element();
					pathset = CommonServiceForLink.rankByLexiOrder(pathset);
					link.setElements(pathset);
					link.setIntersectionFlag(intersectionflag);
					links.add(link);
				}
				ArrayList<String> entities = es.getEndSetbyIntersectionOperationOriginalLinks(links);
				for (String entity: entities) {
					result.add(entity);
				}
			}
			JSONObject jsob = new JSONObject();
			jsob.put("entities", result);
			response.getWriter().print(jsob);
		}
		else if (option.equals("getAllLinkedEntities")) {
			System.out.println("getAllLinkedEntities");
			String str = URLDecoder.decode(json, "utf-8");
			JSONObject reqtJSON = (JSONObject)JSONValue.parse(str);
			String spoints = (String)reqtJSON.get("uri");
			JSONArray result = new JSONArray();
			if (!spoints.contains(";")) {
				String projectPath = 
						request.getSession().getServletContext().getRealPath("");
				SemanticLink se = userData.getPreviousSemanticLinkForSingleEntity(spoints, projectPath);
				ArrayList<String> entities = se.getAllEndSet(userData);
				for (String entity: entities) {
					result.add(entity);
				}
			}
			else {
				String[] uris = spoints.split(";");
				HashSet<String> currentUris = new HashSet<String>();
				CollectionUtils.addAll(currentUris, uris);
				SemanticLink es = userData.getPreviousSemanticLinkForEntities(currentUris);
				ArrayList<String> entities = es.getAllEndSet(userData);
				for (String entity: entities) {
					result.add(entity);
				}
			}
			JSONObject jsob = new JSONObject();
			jsob.put("entities", result);
			response.getWriter().print(jsob);
		}
		else if (option.equals("getHierarchicalLinks")) {
			System.out.println("getHierarchicalLinks");
			String str = URLDecoder.decode(json, "utf-8");
			JSONObject reqtJSON = (JSONObject)JSONValue.parse(str);
			String spoints = (String)reqtJSON.get("uri");
			JSONArray result = new JSONArray();
			if (!spoints.contains(";")) {
				String projectPath = 
						request.getSession().getServletContext().getRealPath("");
				SemanticLink se = userData.getPreviousSemanticLinkForSingleEntity(spoints, projectPath);
				se.bordat();
				HashMap<Element, HashSet<Element>> inLattice = se.getInverseLattice();
				HashMap<Element, HashSet<String>> links2entities = se.getLink2entities();
				HashMap<Element, ArrayList<String>> eId = new HashMap<Element, ArrayList<String>>();
				int id = 0;
				for (Element u: inLattice.keySet()) {
					if (inLattice.get(u).size() == 0) {
						ArrayList<String> ids = new ArrayList<String>();
						ids.add("#");
						eId.put(u, ids);
					}
					else if (inLattice.get(u).size() == 1) {
						ArrayList<String> ids = new ArrayList<String>();
						ids.add(id+"");
						eId.put(u, ids);
						id++;
					}
					else {
						ArrayList<String> ids = new ArrayList<String>();
						for (int i=0; i<inLattice.get(u).size(); i++) {
							ids.add(id+"");
							id++;
						}
						eId.put(u, ids);
					}
				}
				for (Element u: inLattice.keySet()) {
					ArrayList<String> props = u.getElements();
					int entities_num = links2entities.get(u).size();
					String lpLabel = "";
					String lpMembers = "";
					if (props.size() != 0) {
					for (int i=0; i<props.size(); i++) {
						String prop = props.get(i);
						lpMembers += prop + ";";
						if (prop.startsWith("1")) {
							prop = "is " + CommonServiceForLink.getLocalName(prop) + " of & ";
							lpLabel += prop;
						}
						else {
							prop = CommonServiceForLink.getLocalName(prop) + " & ";
							lpLabel += prop;
						}
					}
					lpLabel = lpLabel.substring(0, lpLabel.length()-3);
					lpMembers = lpMembers.substring(0, lpMembers.length()-1);
					}
					if (inLattice.get(u).size() == 1) {
						Element v = new Element();
						for (Element e: inLattice.get(u)) {
							v = e;
						}
						JSONObject ob = new JSONObject();
						ob.put("id", eId.get(u).get(0));
						ob.put("parent", eId.get(v).get(0));
						ob.put("text", lpLabel+"("+entities_num+")");
						JSONObject li_attr = new JSONObject();
						li_attr.put("members", lpMembers);
						li_attr.put("intersection", u.isIntersectionLink());
						ob.put("li_attr", li_attr);
						result.add(ob);
					}
					else if (inLattice.get(u).size() >= 1){
						int i = 0;
						for (Element v: inLattice.get(u)) {
							JSONObject ob = new JSONObject();
							ob.put("id", eId.get(u).get(i));
							ob.put("parent", eId.get(v).get(0));
							ob.put("text", lpLabel+"("+entities_num+")");
							JSONObject li_attr = new JSONObject();
							li_attr.put("members", lpMembers);
							li_attr.put("intersection", u.isIntersectionLink());
							ob.put("li_attr", li_attr);
							result.add(ob);
							i++;
						}
					}
				}
				
			}
			else {
				String[] uris = spoints.split(";");
				HashSet<String> currentUris = new HashSet<String>();
				CollectionUtils.addAll(currentUris, uris);
				SemanticLink es = userData.getPreviousSemanticLinkForEntities(currentUris);
				es.bordat();
				HashMap<Element, HashSet<Element>> inLattice = es.getInverseLattice();
				HashMap<Element, HashSet<String>> links2entities = es.getLink2entities();
				HashMap<Element, ArrayList<String>> eId = new HashMap<Element, ArrayList<String>>();
				int id = 0;
				for (Element u: inLattice.keySet()) {
					if (inLattice.get(u).size() == 0) {
						ArrayList<String> ids = new ArrayList<String>();
						ids.add("#");
						eId.put(u, ids);
					}
					else if (inLattice.get(u).size() == 1) {
						ArrayList<String> ids = new ArrayList<String>();
						ids.add(id+"");
						eId.put(u, ids);
						id++;
					}
					else {
						ArrayList<String> ids = new ArrayList<String>();
						for (int i=0; i<inLattice.get(u).size(); i++) {
							ids.add(id+"");
							id++;
						}
						eId.put(u, ids);
					}
				}
				for (Element u: inLattice.keySet()) {
					ArrayList<String> props = u.getElements();
					int entities_num = links2entities.get(u).size();
					String lpLabel = "";
					String lpMembers = "";
					if (props.size() != 0) {
					for (int i=0; i<props.size(); i++) {
						String prop = props.get(i);
						lpMembers += prop + ";";
						if (prop.startsWith("1")) {
							prop = "is " + CommonServiceForLink.getLocalName(prop) + " of & ";
							lpLabel += prop;
						}
						else {
							prop = CommonServiceForLink.getLocalName(prop) + " & ";
							lpLabel += prop;
						}
					}
					lpLabel = lpLabel.substring(0, lpLabel.length()-3);
					lpMembers = lpMembers.substring(0, lpMembers.length()-1);
					}
					if (inLattice.get(u).size() == 1) {
						Element v = new Element();
						for (Element e: inLattice.get(u)) {
							v = e;
						}
						JSONObject ob = new JSONObject();
						ob.put("id", eId.get(u).get(0));
						ob.put("parent", eId.get(v).get(0));
						ob.put("text", lpLabel+"("+entities_num+")");
						JSONObject li_attr = new JSONObject();
						li_attr.put("members", lpMembers);
						li_attr.put("intersection", u.isIntersectionLink());
						ob.put("li_attr", li_attr);
						result.add(ob);
					}
					else if (inLattice.get(u).size() >= 1){
						int i = 0;
						for (Element v: inLattice.get(u)) {
							JSONObject ob = new JSONObject();
							ob.put("id", eId.get(u).get(i));
							ob.put("parent", eId.get(v).get(0));
							ob.put("text", lpLabel+"("+entities_num+")");
							JSONObject li_attr = new JSONObject();
							li_attr.put("members", lpMembers);
							li_attr.put("intersection", u.isIntersectionLink());
							ob.put("li_attr", li_attr);
							result.add(ob);
							i++;
						}
					}
				}
			}
			JSONObject jsob = new JSONObject();
			jsob.put("h", result);
			response.getWriter().print(jsob);
		}
		else if (option.equals("getBMCLinks1")) {
			System.out.println("getBMCLinks");
			String str = URLDecoder.decode(json, "utf-8");
			JSONObject reqtJSON = (JSONObject)JSONValue.parse(str);
			String spoints = (String)reqtJSON.get("uri");
			JSONArray taskArray = (JSONArray) reqtJSON.get("patternfilters");
			JSONArray flagArray = (JSONArray)taskArray.get(0);
			JSONArray blockIDArray = (JSONArray)taskArray.get(1);
			JSONArray intersectionflagArray = (JSONArray)taskArray.get(2);
			JSONArray propsArray = (JSONArray)taskArray.get(3);
			JSONArray result = new JSONArray();
			if (!spoints.contains(";")) {
				String projectPath = 
						request.getSession().getServletContext().getRealPath("");
				SemanticLink se = userData.getPreviousSemanticLinkForSingleEntity(spoints, projectPath);
				se.bordat();
				HashMap<Element, HashSet<Element>> lattice = se.getLattice();
				HashMap<Element, HashSet<String>> links2entities = se.getLink2entities();
				ArrayList<Element> links = new ArrayList<Element>();
				for (int i=0; i<intersectionflagArray.size(); i++) {
					boolean intersectionflag = Boolean.parseBoolean((String)intersectionflagArray.get(i));
					JSONArray props = (JSONArray)propsArray.get(i);
					ArrayList<String> pathset = new ArrayList<String>();
					for (int j=0; j<props.size(); j++) {
						String path = (String)props.get(j);
						pathset.add(path);
					}
					Element link = new Element();
					pathset = CommonServiceForLink.rankByLexiOrder(pathset);
					link.setElements(pathset);
					link.setIntersectionFlag(intersectionflag);
					links.add(link);
				}
				
				ArrayList<Element> bmc = se.getLinkPatternsByBMC03(links, lattice, links2entities);
				int bmc_length = bmc.size() > 15 ? 15 : bmc.size();
				for (int i=0; i<bmc_length; i++) {
					Element e = bmc.get(i);
					JSONObject object = new JSONObject();
					JSONArray members = new JSONArray();
					String label = "";
					ArrayList<String> props = e.getElements();
					for (String prop: props) {
						if (prop.startsWith("0"))
							label += CommonServiceForLink.getLocalName(prop) + " & ";
						else 
							label += "is " + CommonServiceForLink.getLocalName(prop) + " of & ";
						JSONObject member = new JSONObject();
						member.put("prop", prop);
						members.add(member);
					}
					label = label.substring(0, label.length()-3);
					object.put("label", label);
					object.put("members", members);
					object.put("intersectionflag", e.isIntersectionLink());
					object.put("num", links2entities.get(e).size());
					result.add(object);
				}	
			}
			else {
				String[] uris = spoints.split(";");
				HashSet<String> currentUris = new HashSet<String>();
				CollectionUtils.addAll(currentUris, uris);
				SemanticLink es = userData.getPreviousSemanticLinkForEntities(currentUris);
				es.bordat();
				HashMap<Element, HashSet<Element>> lattice = es.getLattice();
				HashMap<Element, HashSet<String>> links2entities = es.getLink2entities();
				ArrayList<Element> links = new ArrayList<Element>();
				for (int i=0; i<intersectionflagArray.size(); i++) {
					boolean intersectionflag = Boolean.parseBoolean((String)intersectionflagArray.get(i));
					JSONArray props = (JSONArray)propsArray.get(i);
					ArrayList<String> pathset = new ArrayList<String>();
					for (int j=0; j<props.size(); j++) {
						String path = (String)props.get(j);
						pathset.add(path);
					}
					Element link = new Element();
					pathset = CommonServiceForLink.rankByLexiOrder(pathset);
					link.setElements(pathset);
					link.setIntersectionFlag(intersectionflag);
					links.add(link);
				}
				ArrayList<Element> bmc = es.getLinkPatternsByBMC03(links, lattice, links2entities);
				int bmc_length = bmc.size() > 15 ? 15 : bmc.size();
				for (int i=0; i<bmc_length; i++) {
					Element e = bmc.get(i);
					JSONObject object = new JSONObject();
					JSONArray members = new JSONArray();
					String label = "";
					ArrayList<String> props = e.getElements();
					for (String prop: props) {
						if (prop.startsWith("0"))
							label += CommonServiceForLink.getLocalName(prop) + " & ";
						else 
							label += "is " + CommonServiceForLink.getLocalName(prop) + " of & ";
						JSONObject member = new JSONObject();
						member.put("prop", prop);
						members.add(member);
					}
					label = label.substring(0, label.length()-3);
					object.put("label", label);
					object.put("members", members);
					object.put("intersectionflag", e.isIntersectionLink());
					object.put("num", links2entities.get(e).size());
					result.add(object);
				}
			}
			JSONObject jsob = new JSONObject();
			jsob.put("bmc", result);
			response.getWriter().print(jsob);
		}
		else if (option.equals("getBMCLinks2")) {
			System.out.println("getBMCLinks");
			String str = URLDecoder.decode(json, "utf-8");
			JSONObject reqtJSON = (JSONObject)JSONValue.parse(str);
			String spoints = (String)reqtJSON.get("uri");
			JSONArray taskArray = (JSONArray) reqtJSON.get("patternfilters");
			JSONArray flagArray = (JSONArray)taskArray.get(0);
			JSONArray blockIDArray = (JSONArray)taskArray.get(1);
			JSONArray intersectionflagArray = (JSONArray)taskArray.get(2);
			JSONArray propsArray = (JSONArray)taskArray.get(3);
			JSONArray result = new JSONArray();
			if (!spoints.contains(";")) {
				String projectPath = 
						request.getSession().getServletContext().getRealPath("");
				SemanticLink se = userData.getPreviousSemanticLinkForSingleEntity(spoints, projectPath);
				se.bordat();
				HashMap<Element, HashSet<Element>> lattice = se.getLattice();
				HashMap<Element, HashSet<String>> links2entities = se.getLink2entities();
				ArrayList<Element> links = new ArrayList<Element>();
				for (int i=0; i<intersectionflagArray.size(); i++) {
					boolean intersectionflag = Boolean.parseBoolean((String)intersectionflagArray.get(i));
					JSONArray props = (JSONArray)propsArray.get(i);
					ArrayList<String> pathset = new ArrayList<String>();
					for (int j=0; j<props.size(); j++) {
						String path = (String)props.get(j);
						pathset.add(path);
					}
					Element link = new Element();
					pathset = CommonServiceForLink.rankByLexiOrder(pathset);
					link.setElements(pathset);
					link.setIntersectionFlag(intersectionflag);
					links.add(link);
				}
				
				ArrayList<Element> bmc = se.getLinkPatternsByBMC05(links, lattice, links2entities);
				int bmc_length = bmc.size() > 15 ? 15 : bmc.size();
				for (int i=0; i<bmc_length; i++) {
					Element e = bmc.get(i);
					JSONObject object = new JSONObject();
					JSONArray members = new JSONArray();
					String label = "";
					ArrayList<String> props = e.getElements();
					for (String prop: props) {
						if (prop.startsWith("0"))
							label += CommonServiceForLink.getLocalName(prop) + " & ";
						else 
							label += "is " + CommonServiceForLink.getLocalName(prop) + " of & ";
						JSONObject member = new JSONObject();
						member.put("prop", prop);
						members.add(member);
					}
					label = label.substring(0, label.length()-3);
					object.put("label", label);
					object.put("members", members);
					object.put("intersectionflag", e.isIntersectionLink());
					object.put("num", links2entities.get(e).size());
					result.add(object);
				}	
			}
			else {
				String[] uris = spoints.split(";");
				HashSet<String> currentUris = new HashSet<String>();
				CollectionUtils.addAll(currentUris, uris);
				SemanticLink es = userData.getPreviousSemanticLinkForEntities(currentUris);
				es.bordat();
				HashMap<Element, HashSet<Element>> lattice = es.getLattice();
				HashMap<Element, HashSet<String>> links2entities = es.getLink2entities();
				ArrayList<Element> links = new ArrayList<Element>();
				for (int i=0; i<intersectionflagArray.size(); i++) {
					boolean intersectionflag = Boolean.parseBoolean((String)intersectionflagArray.get(i));
					JSONArray props = (JSONArray)propsArray.get(i);
					ArrayList<String> pathset = new ArrayList<String>();
					for (int j=0; j<props.size(); j++) {
						String path = (String)props.get(j);
						pathset.add(path);
					}
					Element link = new Element();
					pathset = CommonServiceForLink.rankByLexiOrder(pathset);
					link.setElements(pathset);
					link.setIntersectionFlag(intersectionflag);
					links.add(link);
				}
				ArrayList<Element> bmc = es.getLinkPatternsByBMC05(links, lattice, links2entities);
				int bmc_length = bmc.size() > 15 ? 15 : bmc.size();
				for (int i=0; i<bmc_length; i++) {
					Element e = bmc.get(i);
					JSONObject object = new JSONObject();
					JSONArray members = new JSONArray();
					String label = "";
					ArrayList<String> props = e.getElements();
					for (String prop: props) {
						if (prop.startsWith("0"))
							label += CommonServiceForLink.getLocalName(prop) + " & ";
						else 
							label += "is " + CommonServiceForLink.getLocalName(prop) + " of & ";
						JSONObject member = new JSONObject();
						member.put("prop", prop);
						members.add(member);
					}
					label = label.substring(0, label.length()-3);
					object.put("label", label);
					object.put("members", members);
					object.put("intersectionflag", e.isIntersectionLink());
					object.put("num", links2entities.get(e).size());
					result.add(object);
				}
			}
			JSONObject jsob = new JSONObject();
			jsob.put("bmc", result);
			response.getWriter().print(jsob);
		}
		else if (option.equals("getAutoComplete")) {
			System.out.println("getAutoComplete");
			String str = URLDecoder.decode(json, "utf-8");
			JSONObject reqtJSON = (JSONObject)JSONValue.parse(str);
			String prefix = (String)reqtJSON.get("prefix");
			JSONArray result = AutoComplete.getResults(prefix);
			response.getWriter().print(result);
		}
		else if (option.equals("getBMCLinks3")) {
			System.out.println("getBMCLinks");
			String str = URLDecoder.decode(json, "utf-8");
			JSONObject reqtJSON = (JSONObject)JSONValue.parse(str);
			String spoints = (String)reqtJSON.get("uri");
			System.out.println("实体： " + spoints); 
			JSONArray taskArray = (JSONArray) reqtJSON.get("patternfilters");
			JSONArray flagArray = (JSONArray)taskArray.get(0);
			JSONArray blockIDArray = (JSONArray)taskArray.get(1);
			JSONArray intersectionflagArray = (JSONArray)taskArray.get(2);
			JSONArray propsArray = (JSONArray)taskArray.get(3);
			JSONArray result = new JSONArray();
			if (!spoints.contains(";")) {
				String projectPath = 
						request.getSession().getServletContext().getRealPath("");
				SemanticLink se = userData.getPreviousSemanticLinkForSingleEntity(spoints, projectPath);
				long startTime = System.currentTimeMillis();
				se.bordat();
				long endTime = System.currentTimeMillis();
				HashMap<Element, HashSet<Element>> lattice = se.getLattice();
				HashMap<Element, HashSet<String>> links2entities = se.getLink2entities();
				ArrayList<Element> links = new ArrayList<Element>();
				for (int i=0; i<intersectionflagArray.size(); i++) {
					boolean intersectionflag = Boolean.parseBoolean((String)intersectionflagArray.get(i));
					JSONArray props = (JSONArray)propsArray.get(i);
					ArrayList<String> pathset = new ArrayList<String>();
					for (int j=0; j<props.size(); j++) {
						String path = (String)props.get(j);
						pathset.add(path);
					}
					Element link = new Element();
					pathset = CommonServiceForLink.rankByLexiOrder(pathset);
					link.setElements(pathset);
					link.setIntersectionFlag(intersectionflag);
					links.add(link);
				}
				long startTime2 = System.currentTimeMillis();
				ArrayList<Element> bmc = se.getLinkPatternsByBMC08(links, lattice, links2entities);
				long endTime2 = System.currentTimeMillis();
				int bmc_length = bmc.size() > 15 ? 15 : bmc.size();
				for (int i=0; i<bmc_length; i++) {
					Element e = bmc.get(i);
					JSONObject object = new JSONObject();
					JSONArray members = new JSONArray();
					String label = "";
					ArrayList<String> props = e.getElements();
					for (String prop: props) {
						if (prop.startsWith("0"))
							label += CommonServiceForLink.getLocalName(prop) + " & ";
						else 
							label += "is " + CommonServiceForLink.getLocalName(prop) + " of & ";
						JSONObject member = new JSONObject();
						member.put("prop", prop);
						members.add(member);
					}
					label = label.substring(0, label.length()-3);
					object.put("label", label);
					object.put("members", members);
					object.put("intersectionflag", e.isIntersectionLink());
					object.put("num", links2entities.get(e).size());
					result.add(object);
				}	
			}
			else {
				String[] uris = spoints.split(";");
				HashSet<String> currentUris = new HashSet<String>();
				CollectionUtils.addAll(currentUris, uris);
				SemanticLink es = userData.getPreviousSemanticLinkForEntities(currentUris);
				es.bordat();
				HashMap<Element, HashSet<Element>> lattice = es.getLattice();
				HashMap<Element, HashSet<String>> links2entities = es.getLink2entities();
				ArrayList<Element> links = new ArrayList<Element>();
				for (int i=0; i<intersectionflagArray.size(); i++) {
					boolean intersectionflag = Boolean.parseBoolean((String)intersectionflagArray.get(i));
					JSONArray props = (JSONArray)propsArray.get(i);
					ArrayList<String> pathset = new ArrayList<String>();
					for (int j=0; j<props.size(); j++) {
						String path = (String)props.get(j);
						pathset.add(path);
					}
					Element link = new Element();
					pathset = CommonServiceForLink.rankByLexiOrder(pathset);
					link.setElements(pathset);
					link.setIntersectionFlag(intersectionflag);
					links.add(link);
				}
				ArrayList<Element> bmc = es.getLinkPatternsByBMC08(links, lattice, links2entities);
				int bmc_length = bmc.size() > 15 ? 15 : bmc.size();
				for (int i=0; i<bmc_length; i++) {
					Element e = bmc.get(i);
					JSONObject object = new JSONObject();
					JSONArray members = new JSONArray();
					String label = "";
					ArrayList<String> props = e.getElements();
					for (String prop: props) {
						if (prop.startsWith("0"))
							label += CommonServiceForLink.getLocalName(prop) + " & ";
						else 
							label += "is " + CommonServiceForLink.getLocalName(prop) + " of & ";
						JSONObject member = new JSONObject();
						member.put("prop", prop);
						members.add(member);
					}
					label = label.substring(0, label.length()-3);
					object.put("label", label);
					object.put("members", members);
					object.put("intersectionflag", e.isIntersectionLink());
					object.put("num", links2entities.get(e).size());
					result.add(object);
				}
			}
			JSONObject jsob = new JSONObject();
			jsob.put("bmc", result);
			response.getWriter().print(jsob);
		}
	}
}
