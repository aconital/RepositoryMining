import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.RepositoryIssue;
import org.eclipse.egit.github.core.SearchIssue;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.event.IssueCommentPayload;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;

import java.util.Scanner;

public class SocialCodeGraph {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Finding the right Repository (NEED REFACTOR)
		Scanner userInputScanner = new Scanner(System.in);
		System.out.println("Please Enter the repository name");
		String query= userInputScanner.nextLine();
		List<SearchRepository> repoList= searchRepositories(query);
		while(repoList.size()==0)
		{
			System.out.println("We couldn't find any Repository with that name.Try again");
			 query= userInputScanner.nextLine();
			 repoList= searchRepositories(query);
		}
		System.out.println("We have found these repositories.Please enter the index of the one which you are intrested in.");
		int indexOfRepo= userInputScanner.nextInt();
		SearchRepository selectedRepo= repoList.get(indexOfRepo);
		System.out.println("You have selected "+selectedRepo.getName()+" Owner:"+selectedRepo.getOwner());
		System.out.println("Here are all the issues in this repository");
		populateSystem(selectedRepo);
		//Now that we have our Repo we populate the system
		createGraph("");
	}
	
	public static void createGraph(String projectname)
	{	
		
	}
	public static void populateSystem(SearchRepository repo)
	{
		IssueService issue=new IssueService();
		List<Comment> comments;
		List<UserInteraction> userInts = new ArrayList<UserInteraction>();
		Person user1;
		Person user2;
		try {
			List<SearchIssue> i=issue.searchIssues(repo, "open", "/issues");
			
			for(SearchIssue a:i)
			{
				System.out.println(a.getTitle());			
				comments=issue.getComments(repo, a.getNumber());
				for(Comment c:comments)
				{
					if(!c.getUser().getLogin().toString().equals(a.getUser().toString()))
							{
								 user1=userCreation(a.getUser());
								 user2=userCreation(c.getUser().getLogin());
							UserInteraction userInt=new UserInteraction(user1, user2,c.getBody());
							System.out.println("hello"+userInt.getData());
							userInts.add(userInt);
							}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static Person userCreation(String name)
	{	UserService u=new UserService();
		User gitUser;
		Person user = null;
		try {
			gitUser = u.getUser(name);
			System.out.println("user:"+gitUser.getLogin());
				
			 user=new Person(gitUser.getLogin(), gitUser.getEmail()) ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return user;
		
	}
	public static List<SearchRepository> searchRepositories(String query)
	{	List<SearchRepository> repoList = null;
		RepositoryService repo=new RepositoryService();
		try {			
			repoList=repo.searchRepositories(query);
		
			for(int i=0;i<repoList.size();i++)
				System.out.println(i+":"+" "+repoList.get(i).getName() + " Owner: "+repoList.get(i).getOwner());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return repoList;
	}
}
 