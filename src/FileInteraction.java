import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;


public class FileInteraction implements Edge {
	//change this to calender
	private HashMap<Calendar,String> interaction;
	private Resource rsc;
	private Person user;
	
	public FileInteraction(Resource rsc,Person user)
	{
		this.rsc=rsc;
		this.user=user;
		interaction=new HashMap<Calendar, String>();
	}
	public FileInteraction(Resource rsc,Person user,String data)
	{	
		this.rsc=rsc;
		this.user=user;
		
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
	{return rsc;}
	public Node getTail()
	{return user;}
	public boolean hasData(String data)
	{
		if(interaction.containsValue(data))
			return true;
		return false;
	}
	@Override
	public boolean hasNode(Node n1,Node n2) {
		Resource temp1= (Resource)n1;
		Person temp2= (Person)n2;
		if((temp1.equals(rsc) && temp2.equals(user)))
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
		System.out.println("File: "+rsc.getFilename()+" User: "+user.getName());
		Iterator it= interaction.keySet().iterator();
		while(it.hasNext())
		{	Calendar key=(Calendar) it.next();
			System.out.println("date: "+key.getTime().toString()+" Data: "+interaction.get(key));
		}
		System.out.println("**********************");
	}

}
