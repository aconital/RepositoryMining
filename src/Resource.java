import java.util.ArrayList;


public class Resource implements Node{

	private String filename;
	private ArrayList<Edge> edges;
	
	public Resource (){}
	public Resource (String filename)
	{
		this.filename=filename;
		edges=new ArrayList<Edge>();
	}
	
	@Override
	public Node getNode() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public String getIntractionwith(Node n) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the filename
	 */
	public  String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public  void setFilename(String filename) {
		this.filename = filename;
	}
	@Override
	public ArrayList<Edge> getEdges() {
		// TODO Auto-generated method stub
		return edges;
	}
	public void addEdge(Edge e)
	{
		edges.add(e);
	}
}
