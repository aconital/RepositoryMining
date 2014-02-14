import java.util.Calendar;
import java.util.Date;


public class FileInteraction implements Edge {

	private String data;
	private Resource rsc;
	private Person user;
	
	public FileInteraction(Resource rsc,Person user)
	{
		this.rsc=rsc;
		this.user=user;
		
	}
	public FileInteraction(Resource rsc,Person user,String data)
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
	public boolean hasNode(Node n) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Calendar getDate() {
		// TODO Auto-generated method stub
		return null;
	}
}
