
public class UserInteraction implements Edge {

	private String data;
	private User user1;
	private User user2;
	
	public UserInteraction(User user1,User user2)
	{
		this.user1=user1;
		this.user2=user2;
		
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
