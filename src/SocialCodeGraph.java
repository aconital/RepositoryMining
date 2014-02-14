import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.RepositoryIssue;
import org.eclipse.egit.github.core.SearchIssue;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.event.IssueCommentPayload;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;

import java.util.Scanner;

public class SocialCodeGraph {
	static GitHubClient client = new GitHubClient();
	private static String oAuthKey="50ec05b630c6d418473809c9f8946f18df33dd7e";
	private static List<UserInteraction> userInts;
	private static Person user1;
	private static Person user2;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Finding the right Repository (NEED REFACTOR)
		client.setOAuth2Token(oAuthKey);
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
		IssueService issue=new IssueService(client);
		List<Comment> comments;
		userInts = new ArrayList<UserInteraction>();
		
		try { 
			int issueSize=repo.getOpenIssues();

			//	List<SearchIssue> i=issue.searchIssues(repo, "open", "license");

			for(int j=1;j<=issueSize;j++){

				Issue xissue=issue.getIssue(repo,j);
				System.out.println("\n"+"<<Issue Title>>:"+xissue.getTitle());			
				comments=issue.getComments(repo, xissue.getNumber());

				//Creating an edge between Asignee and others
				createAssigneeInteraction(xissue,comments);
				//Creating an edge between Creator & commenter (Think when creator comments again?!)
				createCommenterInteraction(xissue,comments);

			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void createCommenterInteraction(Issue xissue, List<Comment> comments) {
		for(Comment c:comments)
		{
			if(!c.getUser().getLogin().equals(xissue.getUser().getLogin()))
			{
				user1=userCreation(xissue.getUser().getLogin());
				user2=userCreation(c.getUser().getLogin());
				Calendar date=DateToCalendar(c.getCreatedAt());

				UserInteraction userInt=new UserInteraction(user1, user2,c.getBody(),date);
				System.out.println("<<Data>>: "+userInt.getData());
				
				//creating Date
				int year=date.get(Calendar.YEAR);;
				int month=date.get(Calendar.MONTH);;
				int day=date.get(Calendar.DAY_OF_MONTH);
				String stringDate= Integer.toString(year)+"/"+Integer.toString(month)+"/"+Integer.toString(day);
				System.out.println("<<Date>>: "+stringDate);
				
				userInts.add(userInt);
			}
		}
		
	}

	private static void createAssigneeInteraction(Issue xissue,List<Comment> comments) {
		if(xissue.getAssignee()== null)
			return;
		String interactionType="";
		//between creator and assignee
		if(!xissue.getUser().getLogin().equals(xissue.getAssignee().getLogin())){
			user1=userCreation(xissue.getAssignee().getLogin());
			user2=userCreation(xissue.getUser().getLogin());
			Calendar date=DateToCalendar(xissue.getCreatedAt());
			UserInteraction userInt=new UserInteraction(user1, user2,xissue.getBody(),date);
			userInts.add(userInt);
		}
		//between commenters and assignee
		for(Comment c:comments)
		{
			if(!c.getUser().getLogin().equals(xissue.getAssignee().getLogin()) &&
					!c.getUser().getLogin().equals(xissue.getUser().getLogin())	)
			{
				user1=userCreation(xissue.getAssignee().getLogin());
				user2=userCreation(c.getUser().getLogin());
				Calendar date=DateToCalendar(xissue.getCreatedAt());
				interactionType="Commenter-Assignee Interaction";
				UserInteraction userInt=new UserInteraction(user1, user2,interactionType,date);
				System.out.println("<<Data>>: "+interactionType);
				
				//creating Date
				int year=date.get(Calendar.YEAR);;
				int month=date.get(Calendar.MONTH);;
				int day=date.get(Calendar.DAY_OF_MONTH);
				String stringDate= Integer.toString(year)+"/"+Integer.toString(month)+"/"+Integer.toString(day);
				System.out.println("<<Date>>: "+stringDate);
				
				userInts.add(userInt);
			}
		}
	}

	public static Person userCreation(String name)
	{	UserService u=new UserService(client);
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
	private static Calendar DateToCalendar(Date date){ 
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
	public static List<SearchRepository> searchRepositories(String query)
	{	List<SearchRepository> repoList = null;

	RepositoryService repo=new RepositoryService(client);
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
