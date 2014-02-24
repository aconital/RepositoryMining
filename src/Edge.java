import java.util.Calendar;
import java.util.Date;


public interface Edge {

	
	public String getData(Calendar date);
	public String DateToString(Calendar date);
	public boolean hasNode(Node n1,Node n2);
}
