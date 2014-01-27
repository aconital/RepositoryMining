import java.io.IOException;
import java.util.List;

import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.service.RepositoryService;


public class SocialCodeGraph {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		createGraph("");
	}
	
	public static void createGraph(String projectname)
	{
		
	}
	
	public List<SearchRepository> searchRepositories(String query)
	{	List<SearchRepository> repoList = null;
		RepositoryService repo=new RepositoryService();
		try {			
			repoList=repo.searchRepositories(query);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return repoList;
	}
}
 