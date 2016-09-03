package linkPatternUtil;

import java.util.ArrayList;

public class Element {
	//实体项的基本信息
	private String label="";
	ArrayList<String> elements=new 	ArrayList<String>();
	//Chain:含有空白节点为true;不含为false!
	private  boolean flag = false;
	private  int blockID = 0;
	//交的link为true;非交的为false!
	private  boolean Intersectionflag = false;
	
	public Element() {
	}
	
	public Element(String label) {
		this.label=label;
	}
	
	public Element(Element item) {
		this.label= item.getLabel();
		this.elements=item.getElements();
	}
	public Element(Link item) {
		this.label= item.getLabel();
		this.elements=item.getElements();
	}
	public Element(Feature f) {
		this.label= f.getFeatureName();
		for(Property p: f.getMembers()){
			String s=p.getFuri();
//			System.out.println("s:--"+s);
			this.elements.add(s);
		}
	}
	
	public String getLabel() {
		return label;
	}

	public ArrayList<String> getElements() {
		return elements;
	}
	public boolean haveBNode() {
		return flag;
	}
	public boolean isIntersectionLink() {
		return Intersectionflag;
	}
	
	public int getblockID() {
		return blockID;
	}
	
	public Element setLabel(String label) {
		this.label = label;
		return this;
	}

	public Element setElements(ArrayList<String> elements) {
		this.elements = elements;
		return this;
	}
	public Element setFlag(boolean flag) {
		this.flag = flag;
		return this;
	}
	public Element setIntersectionFlag(boolean flag) {
		this.Intersectionflag = flag;
		return this;
	}
	public Element setBlockID(int blockId) {
		this.blockID = blockId;
		return this;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 17;
//		for(String element:elements){
//			result = prime * result + element.hashCode();
//		}
		for(int i=0;i<elements.size();i++){
			result = prime * result + elements.get(i).hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Element other = (Element) obj;
		if (elements == null) {
			if (other.elements != null)
			return false;
		}
		if (elements.size()!=other.elements.size()){
			return false;
		}
		if(elements.containsAll(other.elements)){
			return true;
	    }
		return false;  
	}
	
	public static void main(String[] args) {
		
//		ArrayList<String> s=new ArrayList<String>();
//		ArrayList<String> s1=new ArrayList<String>();
//		s.add("c");s.add("A");s.add("a");s.add("cw");s.add("6");
//		s1.add("b");s1.add("a");s1.add("6");s1.add("cw");s1.add("c");
//		s=CommonServiceForLink.rankByLexiOrder(s);
//		System.out.println("s:"+s);
//		s1=CommonServiceForLink.rankByLexiOrder(s1);
//		Element e1=new Element( );
//		Element e2=new Element( );
//		e1.setItemElements(s);
//		e2.setItemElements(s1);
//		System.out.println("e1:"+e1.hashCode());
//		System.out.println("e2:"+e2.hashCode());
		
	}


    //实体项的用户访问时间列表；对应用户列表
	
	


}
