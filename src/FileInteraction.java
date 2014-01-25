
public class FileInteraction implements Edge {

	private String data;
	private Resource rsc;
	private User user;
	
	public FileInteraction(Resource rsc,User user)
	{
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
	public void setNode(Node n) {
		// TODO Auto-generated method stub
		
	}
}
