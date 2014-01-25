
public class Resource implements Node{

	private String filename;
	
	public Resource (){}
	public Resource (String filename)
	{
		this.filename=filename;
	}
	
	@Override
	public Node getNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node getNodeDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node getIntractionwith(Node n) {
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

}
