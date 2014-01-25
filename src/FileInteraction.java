
public class FileInteraction implements Edge {

	private String data;
	private Resource rsc;
	private User user;
	
	public FileInteraction(Resource rsc,User user)
	{
		this.rsc=rsc;
		this.user=user;
		
	}
	public FileInteraction(Resource rsc,User user,String data)
	{	this.data=data;
		this.rsc=rsc;
		this.user=user;
		
	}
	public String getData()
	{
		return data;
	}
	public void setData(String data)
	{
		this.data=data;
	}

	@Override
	public void setNode(Node n,int nodeType) {
		if(nodeType == Constants.RESOURCE)
			rsc= (Resource)n;
		else
			user=(User)n;
		
	}



	@Override
	public boolean hasNode(Node n) {
		// TODO Auto-generated method stub
		return false;
	}
}
