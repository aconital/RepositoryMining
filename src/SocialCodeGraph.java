import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Transformer;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.CommitFile;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryIssue;
import org.eclipse.egit.github.core.SearchIssue;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.event.IssueCommentPayload;
import org.eclipse.egit.github.core.event.PullRequestPayload;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.GitHubService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.control.LayoutScalingControl;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

import java.util.Scanner;

import javax.swing.JFrame;

public class SocialCodeGraph {
	static GitHubClient client = new GitHubClient();
	private static String oAuthKey="50ec05b630c6d418473809c9f8946f18df33dd7e";
	//##########################
	//no static -refactor later
	//##########################
	private static Set<UserInteraction> userInts;
	private static Set<Person> users;
	private static Set<FileInteraction> fileInts;
	private static Set<Resource> resources;

	private static  DirectedGraph<Node, Edge> graph =new DirectedSparseMultigraph<Node, Edge>();
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
		System.out.println("Please Wait! Processing...");


		populateSystem(selectedRepo);

		createGraph();


	}

	public static void createGraph()
	{		for(UserInteraction ui: userInts)
	{	graph.addVertex(ui.getHead());
	graph.addVertex(ui.getTail());
	graph.addEdge(ui, ui.getHead(), ui.getTail());
	}
	for(FileInteraction fi:fileInts)
	{
		graph.addVertex(fi.getHead());
		graph.addVertex(fi.getTail());
		graph.addEdge(fi, fi.getHead(), fi.getTail());
	}
	Layout<Node, Edge> layout = new CircleLayout(graph);
	//   layout.setSize(new Dimension(500,500));

	BasicVisualizationServer<Node,Edge> vv = new BasicVisualizationServer<Node,Edge>(layout);
	vv.setPreferredSize(new Dimension(700,700));    
	vv.setGraphLayout(layout);
	vv.scaleToLayout(new LayoutScalingControl());
	//  vv.setAutoscrolls(true);

	vv.setBounds(0, 0, 700, 700);
	// Setup up a new vertex to paint transformer...
	Transformer<Node,Paint> vertexPaint = new Transformer<Node,Paint>() {
		public Paint transform(Node i) {
			if(i instanceof Person)
				return Color.GREEN;
			else
				return Color.BLUE;
		}
	};  
	// Set up a new stroke Transformer for the edges
	float dash[] = {10.0f};
	final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
	Transformer<Edge, Stroke> edgeStrokeTransformer = new Transformer<Edge, Stroke>() {
		public Stroke transform(Edge s) {
			return edgeStroke;
		}
	};
	vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
	vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);

	//make this clickable so user can choose the interaction
	vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller(){
		@Override
		public String transform(Object e)
		{	if(e instanceof Person)
			return ((Person) e).getName();
		else
			return ((Resource) e).getFilename();
		}
	});

	vv.getRenderer().getVertexLabelRenderer().setPosition(Position.E);        

	JFrame frame = new JFrame("Repository Interaction Graph");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().add(vv);
	frame.pack();
	frame.setVisible(true);  
	}


	public static void populateSystem(SearchRepository repo)
	{
		IssueService issue=new IssueService(client);
		List<Comment> comments;
		userInts = new HashSet<UserInteraction>();
		fileInts=new HashSet<FileInteraction>();
		resources=new HashSet<Resource>();
		users=new HashSet<Person>();

		Map<String,String> params =new HashMap<String,String>();
		params.put("state", "open");

		try { 

			for(Issue xissue: issue.getIssues(repo,params ))
			{

				comments=issue.getComments(repo, xissue.getNumber());

				//Creating an edge between Asignee and others
				createAssigneeInteraction(xissue,comments);
				//Creating an edge between Creator & commenter (Think when creator comments again?!)
				createCommenterInteraction(xissue,comments);
			}


			//PullRequest Section

			List<RepositoryCommit> repositoryCommitList;
			Set<String> shaList =new HashSet<String>();
			CommitService cs=new CommitService(client);
			repositoryCommitList=cs.getCommits(repo);

			for(RepositoryCommit rc:repositoryCommitList)	
			{	
				shaList.add(rc.getSha());			
			}
			for(String sha:shaList)
			{
				RepositoryCommit repCommit=cs.getCommit(repo, sha);
				if(repCommit!=null){
				List<CommitFile> commitFileList=repCommit.getFiles();
				User user=repCommit.getCommitter();
				String commiter="";	
				if(user!=null){
						commiter=user.getLogin();
						String message=repCommit.getCommit().getMessage();
						//API CURRENTLY DOESNT HAVE DATE
						createFileInteraction(commiter,commitFileList,sha,message);
					}
				}

			}
			for(FileInteraction fi:fileInts)
			{
				fi.printDetails();
			}
			//######### CREATE UI#########
			for(UserInteraction ui:userInts)
			{
				ui.printDetails();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}







	}
	private static void createFileInteraction(String commiter,List<CommitFile> commitFileList,String sha,String message) 
	{
		Person user=userFinder(commiter);
		Resource resource;

		for(CommitFile cf:commitFileList)
		{	
		
			resource=resourceFinder(cf.getFilename());
			FileInteractionFinder(resource,user,sha,message);
		}
	}
	private static void FileInteractionFinder(Resource rsc,Person user,String sha,String data)
	{
			if(rsc==null || user== null)
				return;
			
			for(FileInteraction fi: fileInts)
			{
				if(fi.hasNode(rsc, user))
				{
					if(!fi.hasData(data))
					{
						fi.addInteraction(sha, data);
						return;
					}
				}
			}
			FileInteraction fileInt=new FileInteraction(rsc, user);
			fileInt.addInteraction(sha, data);
			fileInts.add(fileInt);
			return;

	}
	private static Resource resourceFinder(String name)
	{	for(Resource rsc:resources)
		{
		if(rsc.getFilename().equals(name))
			return rsc;
		}
		Resource r=new Resource(name);
		resources.add(r);
		return r;
	}

	private static void createCommenterInteraction(Issue xissue, List<Comment> comments) {
		Person user1;
		Person user2;
		//between commenters
		for(Comment c:comments)
		{ 	user1=userFinder(c.getUser().getLogin());
		for(Comment d:comments)
		{	Calendar date=DateToCalendar(d.getCreatedAt());
		user2=userFinder(d.getUser().getLogin());
		UserInteractionFinder(user1,user2, date, c.getBody());
		}

		}
		//between Creator and commenters
		for(Comment c:comments)
		{ 	user1=userFinder(c.getUser().getLogin());
		user2=userFinder(xissue.getUser().getLogin());
		Calendar date=DateToCalendar(c.getCreatedAt());
		UserInteractionFinder(user1,user2, date, c.getBody());

		}

	}

	private static void createAssigneeInteraction(Issue xissue,List<Comment> comments) {
		Person user1;
		Person user2;
		if(xissue.getAssignee()== null)
			return;
		String interactionType="";
		//assignee
		user1=userFinder(xissue.getAssignee().getLogin());

		//between creator and assignee
		user2=userFinder(xissue.getUser().getLogin());
		Calendar date=DateToCalendar(xissue.getCreatedAt());
		UserInteractionFinder(user1, user2, date, xissue.getBody());


		//between commenters and assignee
		for(Comment c:comments)
		{
			{
				user2=userFinder(c.getUser().getLogin());
				date=DateToCalendar(xissue.getCreatedAt());
				interactionType="Commenter-Assignee Interaction";
				UserInteractionFinder(user1, user2, date, interactionType);

			}
		}
	}
	private static void UserInteractionFinder(Person user1,Person user2,Calendar date,String data)
	{	if(user1.equals(user2))
		return;

	for(UserInteraction ui:userInts)
	{
		if(ui.hasNode(user1, user2))
		{	if(!ui.hasData(data))
			ui.addInteraction(date, data);
		return;
		}
	}

	UserInteraction userInt=new UserInteraction(user1, user2);
	userInt.addInteraction(date,data);
	userInts.add(userInt);
	return;
	}
	private static Person userFinder(String name)
	{	for(Person user:users)
	{
		if(user.getName().toString().equals(name))
			return user;
	}
	Person p=userCreation(name);
	users.add(p);
	return p;
	}
	private static Person userCreation(String name)	
	{	
		UserService u=new UserService(client);
		User gitUser;
		Person user = null;
		try {
			gitUser = u.getUser(name);
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
