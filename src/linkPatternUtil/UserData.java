package linkPatternUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import linkPatternService.SemanticLinkForEntities;
import linkPatternService.SemanticLinkForSingleEntity;

/**
 * 这个类必须是可序列化的
 * !!!need Check: synchronized of every method
 * @author Saisai Gong
 */
public class UserData{
//	private static Logger log = Logger.getLogger(UserData.class);
//	static{
//		log.setLevel(Level.OFF);
//	}
//	

	private String userName = null;

	private SemanticLinkForSingleEntity slink4sentity = null;
	private SemanticLinkForEntities slink4col = null;
	private String prevEID = null;
	private HashSet<String> prevCID = new HashSet<String>();
	
	public SemanticLinkForEntities getPreviousSemanticLinkForEntities(HashSet<String> colID){
		this.prevCID = colID;
		HashSet<String> memberURIs = new HashSet<String>();
		HashSet<String> currentCollectionURIs = new HashSet<String>();
		ArrayList<TwoTuple<String,HashSet<String>>> corfMemberURIs = new ArrayList<TwoTuple<String,HashSet<String>>>();
		
		if(prevCID.size() != 0 && prevCID.equals(colID)){
			if(this.slink4col != null && this.slink4col.getMemberURIs().containsAll(memberURIs)&&
					memberURIs.containsAll(this.slink4col.getMemberURIs())){
				System.out.println("c_existed");
				return this.slink4col;
			}
				
		}
		this.slink4col = new SemanticLinkForEntities(this, colID, corfMemberURIs);
		System.out.println("c_not existed");
		return this.slink4col;
	}
	
	public SemanticLinkForSingleEntity getPreviousSemanticLinkForSingleEntity(String eid, String projectPath){
		this.prevEID = eid;
		HashSet<String> corefUris = new HashSet<String>();
		if(prevEID != null && prevEID.equals(eid)){
			if(this.slink4sentity != null && this.slink4sentity.getCurrentUri().equals(eid)){
				System.out.println("s_existed！！");
				return this.slink4sentity;
			}
		}
		this.slink4sentity = 
				new SemanticLinkForSingleEntity(this, eid, corefUris, projectPath);
		System.out.println("s_not existed！！");
		return this.slink4sentity;
	}
	

	public UserData(){
	}

	public String getUserName(){
		return this.userName;
	}
	

}
