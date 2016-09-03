package linkPatternUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;


public class Graph {
	
	public static final int INFINITY=Integer.MAX_VALUE;
	
	private HashMap<Object,Vertex> vertexMap=new HashMap<Object,Vertex>();
	
	public ArrayList<Vertex> toExpand = new ArrayList<Vertex>();
	public ArrayList<Vertex>  getAllpathelement(){
		return toExpand;
	}
	
	//让vertexMap取得对Vertex对象的引用
	private Vertex getVertex(Object vertexName){
	   Vertex v=vertexMap.get(vertexName);
	   if(v==null)
	   {
		   v=new Vertex(vertexName);
		   vertexMap.put(vertexName,v);
	   }
	   return v;
	}
	//将距离初始化
	private void clearAll(){
		for(Iterator< Vertex> itr=vertexMap.values().iterator();itr.hasNext();)
		{
			itr.next().reset();
		}
	}
	//显示实际最短路径
	private void printPath(Vertex dest){
		
		if(dest.path!=null)
		{
			toExpand.add(dest);
			printPath(dest.path);
		}
	}
	
	//添加一条新的边
	public void addEdge(Object sourceName,Object destName){
		
		Vertex v=getVertex(sourceName);
		Vertex w=getVertex(destName);
		v.adj.add(w);
	}
	//显示一条路径
	public void printPath(Object destName) throws NoSuchElementException{
		
		Vertex	w=vertexMap.get(destName);
		
		if(w==null)
			throw new NoSuchElementException("Destination vertex not found!");
		else if(w.dist==INFINITY){
			
		}
		//不可达的要特殊处理下！！
		else {
			toExpand.clear();
			printPath(w);
		}
	}
	
	//无权最短路径计算
	public void unweighted(Object startName){
		
		clearAll();
		Vertex start=vertexMap.get(startName);
		if(start==null)
			throw new NoSuchElementException("Start vertex not found!");
		LinkedList<Vertex> q=new LinkedList<Vertex>();
		q.addLast(start);
		start.dist=0;
		
		while(!q.isEmpty())
		{
			Vertex v=q.removeFirst();
			for(Iterator<Vertex> itr=v.adj.iterator();itr.hasNext();)
			{
				Vertex w=itr.next();
				if(w.dist==INFINITY)
				{
					w.dist=v.dist+1;
					w.path=v;
					q.addLast(w);
				}
				
			}
		}		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Graph graph=new Graph();
		graph.addEdge(1, 2);
		graph.addEdge(1, 3);graph.addEdge(1, 5);
		graph.addEdge(3, 4);
		graph.addEdge(4, 5);
		graph.addEdge(6, 6);
		graph.unweighted(5);//起点
		graph.printPath(6);//终点
		ArrayList<Vertex> q=graph.getAllpathelement(); 
            for(int j=q.size()-1;j>=0;j--){
     	      Vertex o=q.get(j);
     	      System.out.print(o.getVertex()+" ");
            }
            System.out.println("OK "+q.size());
		System.out.println("OK ");
		System.exit(0);
	}
	
	
	
	
}