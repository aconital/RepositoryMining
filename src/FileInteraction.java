import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;


public class FileInteraction implements Edge {
	//change this to calender
	private HashMap<String,String> interaction;
	private Resource rsc;
	private Person user;
	
	public FileInteraction(Resource rsc,Person user)
	{
		this.rsc=rsc;
		this.user=user;
		interaction=new HashMap<String, String>();
	}
	public FileInteraction(Resource rsc,Person user,String data)
	{	
		this.rsc=rsc;
		this.user=user;
		
	}

	public void addInteraction(String sha,String data)
	{
		interaction.put(sha, data);
	}
	public String getData(Calendar sha)
	{
		return interaction.get(sha);
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
	public String DateToString(Calendar sha) {
		int year=sha.get(Calendar.YEAR);;
		int month=sha.get(Calendar.MONTH);;
		int day=sha.get(Calendar.DAY_OF_MONTH);
		String stringsha= Integer.toString(year)+"/"+Integer.toString(month)+"/"+Integer.toString(day);

		return stringsha;
	}
	
	public void printDetails()
	{	System.out.println("**********************");
		System.out.println("File: "+rsc.getFilename()+" User: "+user.getName());
		Iterator it= interaction.keySet().iterator();
		while(it.hasNext())
		{	String key=(String) it.next();
			System.out.println("sha: "+key+" Data: "+interaction.get(key));
		}
		System.out.println("**********************");
	}

}
