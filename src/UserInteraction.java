import java.util.Calendar;



public class UserInteraction implements Edge {

	private String data;
	private Person user1;
	private Person user2;
	private Calendar  date;
	
	public UserInteraction(Person user1,Person user2)
	{
		this.user1=user1;
		this.user2=user2;
		
	}
	public UserInteraction(Person user1,Person user2,String data,Calendar  date)
	{
		this.user1=user1;
		this.user2=user2;
		this.data=data;
		this.date=date;
		
	}
	public String getData()
	{
		return data;
	}
	public Calendar   getDate()
	{
		return date;
	}

	@Override
	public boolean hasNode(Node n) {
		Person temp= (Person)n;
		if(temp.equals(user1) || temp.equals(user2))
			return true;
		return false;
	}
	
}
