
public class UserInteraction implements Edge {

	private String data;
	private User user1;
	private User user2;
	
	public UserInteraction(User user1,User user2)
	{
		this.user1=user1;
		this.user2=user2;
		
	}
	public UserInteraction(User user1,User user2,String data)
	{
		this.user1=user1;
		this.user2=user2;
		this.data=data;
		
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
	public void setNode(Node n,int userNum) {
		if (userNum == Constants.USER_1)
			user1=(User)n;
		else
			user2=(User)n;
		
	}

	@Override
	public boolean hasNode(Node n) {
		User temp= (User)n;
		if(temp.equals(user1) || temp.equals(user2))
			return true;
		return false;
	}
	
}
