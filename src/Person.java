import java.util.ArrayList;


public class Person implements Node {
	private String name;
	private String email;
	private ArrayList<Edge> edges;
	
	public Person()
	{}
	public Person(String name,String email)
	{
		this.setName(name);
		this.setEmail(email);
		edges=new ArrayList<Edge>();
	}
	public void addEdge(Edge e)
	{
		edges.add(e);
	}
	public ArrayList<Edge> getEdges()
	{
		return edges;
	}
	@Override
	public Node getNode() {
		// TODO Auto-generated method stub
		return this;
	}

/* 
 * 	FIIIIIXXX THISSS
 * 
 */
	@Override
	public String getIntractionwith(Node n) {
	//	for(Edge e:edges)
	//	{
		//	if(e.hasNode(n))
	//		{
		//		return e.getData();
	//		}
	//	}
					
		return null;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

}
