import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class UserInteraction implements Edge {
	//person-person based on file interact
	//adding date 
	private Person user1;
	private Person user2;
	private HashMap<Calendar,String> interaction;
	
	public UserInteraction(Node user1,Node user2)
	{
		this.user1=(Person) user1;
		this.user2=(Person) user2;
		interaction=new HashMap<Calendar, String>();
		
	}
	public void addInteraction(Calendar date,String data)
	{
		interaction.put(date, data);
	}
	public String getData(Calendar date)
	{
		return interaction.get(date);
	}
	
	public Node getHead()
	{return user1;}
	public Node getTail()
	{return user2;}
	public boolean hasData(String data)
	{
		if(interaction.containsValue(data))
			return true;
		return false;
	}
	@Override
	public boolean hasNode(Node n1,Node n2) {
		Person temp1= (Person)n1;
		Person temp2= (Person)n2;
		if((temp1.equals(user1) && temp2.equals(user2)) || 
		   (temp1.equals(user2) && temp2.equals(user1)))
			return true;
		return false;
	}
	@Override
	public String DateToString(Calendar date) {
		int year=date.get(Calendar.YEAR);;
		int month=date.get(Calendar.MONTH);;
		int day=date.get(Calendar.DAY_OF_MONTH);
		String stringDate= Integer.toString(year)+"/"+Integer.toString(month)+"/"+Integer.toString(day);

		return stringDate;
	}
	
	public void printDetails()
	{	System.out.println("**********************");
		System.out.println("user1: "+user1.getName()+" User2: "+user2.getName());
		Iterator it= interaction.keySet().iterator();
		while(it.hasNext())
		{	Calendar key=(Calendar) it.next();
			System.out.println("Date: "+key.getTime().toString()+" Data: "+interaction.get(key));
		}
		System.out.println("**********************");
	}
	
}
