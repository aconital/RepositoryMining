import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.LayoutScalingControl;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

import java.util.Scanner;

import javax.swing.JFrame;
/*
 * Application: Repository Miner
 * Author: Hirad Roshandel
 * Date:January-April 2014
 * 
 * This is an early version of a repository mining application which goes through
 * a github repository and gathers information and creates a network between all users and files
 * who had interacted with each other [currently] through issues and commits and draws a graph
 * 
 * HOW TO: (1)Simply run the application and type in the name of the repository which you are 
 * looking for. The application searches within GitHub for all repository which similar name
 * (2) Enter the index number of the repository which you are looking
 * (3) Once the graph is created, use mouse scroll to zoom in/out, User T and P buttons to toggle
 * between picking mode and camera mode. In picking mode you can select a node and move it around.
 * In camera mode you can pan and move the camera. 
 */
public class SocialCodeGraph {
	private  GitHubClient client;
	private  String oAuthKey;
	private  Set<UserInteraction> userInts;
	private  Set<Person> users;
	private  Set<FileInteraction> fileInts;
	private  Set<Resource> resources;

	private SparseGraph<Node, Edge> graph;

	public SocialCodeGraph()
	{	client = new GitHubClient();
	oAuthKey="50ec05b630c6d418473809c9f8946f18df33dd7e";
	userInts = new HashSet<UserInteraction>();
	fileInts=new HashSet<FileInteraction>();
	resources=new HashSet<Resource>();
	users=new HashSet<Person>();
	graph =new SparseGraph<Node, Edge>();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		SocialCodeGraph scg=new SocialCodeGraph();
		//Finding the right Repository (NEED REFACTOR)
		scg.getGitClient().setOAuth2Token(scg.getOAuth());
		Scanner userInputScanner = new Scanner(System.in);
		System.out.println("Please Enter the repository name");
		String query= userInputScanner.nextLine();
		List<SearchRepository> repoList= scg.searchRepositories(query);
		while(repoList.size()==0)
		{
			System.out.println("We couldn't find any Repository with that name.Try again");
			query= userInputScanner.nextLine();
			repoList= scg.searchRepositories(query);
		}
		System.out.println("We have found these repositories.Please enter the index of the one which you are intrested in.");
		int indexOfRepo= userInputScanner.nextInt();
		SearchRepository selectedRepo= repoList.get(indexOfRepo);
		System.out.println("You have selected "+selectedRepo.getName()+" Owner:"+selectedRepo.getOwner());
		System.out.println("Please Wait! Processing...");


		scg.populateSystem(selectedRepo);

		scg.createGraph();


	}

	public void createGraph()
	{		for(UserInteraction ui: userInts)
	{	
		graph.addVertex(ui.getHead());
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

	VisualizationViewer<Node,Edge> vv = new VisualizationViewer<Node,Edge>(layout);
	vv.setPreferredSize(new Dimension(700,700));    
	vv.setGraphLayout(layout);
	vv.scaleToLayout(new LayoutScalingControl());
	//  vv.setAutoscrolls(true);
	DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
	gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
	vv.setGraphMouse(gm); 
	// Add the mouses mode key listener to work it needs to be added to the visualization component
	vv.addKeyListener(gm.getModeKeyListener());
	//picked NODE
	final PickedState<Node> pickedNodeState = vv.getPickedVertexState();
	//picked EDGE
	final PickedState<Edge> pickedinteractionState = vv.getPickedEdgeState();
	//listener for picked node
	pickedNodeState.addItemListener(new ItemListener() {

		@Override
		public void itemStateChanged(ItemEvent e) {
			Object subject = e.getItem();
			if (subject instanceof Person) {
				Person vertex = (Person) subject;
				if (pickedNodeState.isPicked(vertex)) {
					System.out.println("Name:"+vertex.getName()+" Email:"+vertex.getEmail());
				}
				else if (subject instanceof Resource)
				{
					Resource file = (Resource) subject;
					if (pickedNodeState.isPicked(file)) {
						System.out.println("Filename:"+file.getFilename());
					}
				}

			}}});
	//LISTENER for picked edge
	pickedinteractionState.addItemListener(new ItemListener() {

		@Override
		public void itemStateChanged(ItemEvent e) {
			Object subject = e.getItem();
			if(subject instanceof Edge)
			{
				Edge interaction = (Edge) subject;
				if (pickedinteractionState.isPicked(interaction)) {
					interaction.printDetails();
				}
			}}}
			);
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


	public void populateSystem(SearchRepository repo)
	{
		IssueService issue=new IssueService(client);
		List<Comment> comments;


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
			Map<String,Calendar> shaList =new HashMap<String,Calendar>();
			CommitService cs=new CommitService(client);
			repositoryCommitList=cs.getCommits(repo);
			Calendar date;
			String sha;
			for(RepositoryCommit rc:repositoryCommitList)	
			{	 date=DateToCalendar(rc.getCommit().getCommitter().getDate());
			sha=rc.getSha();
			shaList.put(sha,date);	

			}
			Iterator iter = shaList.keySet().iterator();
			while(iter.hasNext()) {
				sha = (String)iter.next();
				date = (Calendar)shaList.get(sha);
				RepositoryCommit repCommit=cs.getCommit(repo, sha);
				if(repCommit!=null){
					List<CommitFile> commitFileList=repCommit.getFiles();

					User user=repCommit.getCommitter();
					String commiter="";	
					if(user!=null){
						commiter=user.getLogin();
						String message=repCommit.getCommit().getMessage();
						createFileInteraction(commiter,commitFileList,date,message);
					}
				}

			}
			//TODO: Remove this part if you want the console to be clear
			/*
			 * This Part is only to show that system is populated correctly
			 */
			for(FileInteraction r:fileInts)
			{
				r.printDetails();
			}
			for(UserInteraction u:userInts)
			{
				u.printDetails();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private  void createFileInteraction(String commiter,List<CommitFile> commitFileList,Calendar date,String message) 
	{
		Person user=userFinder(commiter);
		Resource resource;

		for(CommitFile cf:commitFileList)
		{	

			resource=resourceFinder(cf.getFilename());
			FileInteractionFinder(resource,user,date,message);
		}
	}
	private  void FileInteractionFinder(Resource rsc,Person user,Calendar date,String data)
	{
		if(rsc==null || user== null)
			return;

		for(FileInteraction fi: fileInts)
		{
			if(fi.hasNode(rsc, user))
			{
				if(!fi.hasData(data))
				{
					fi.addInteraction(date, data);
					return;
				}
			}
		}
		FileInteraction fileInt=new FileInteraction(rsc, user);
		fileInt.addInteraction(date, data);
		fileInts.add(fileInt);
		return;

	}
	private  Resource resourceFinder(String name)
	{	for(Resource rsc:resources)
	{
		if(rsc.getFilename().equals(name))
			return rsc;
	}
	Resource r=new Resource(name);
	resources.add(r);
	return r;
	}

	private  void createCommenterInteraction(Issue xissue, List<Comment> comments) {
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

	private  void createAssigneeInteraction(Issue xissue,List<Comment> comments) {
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
	private void UserInteractionFinder(Person user1,Person user2,Calendar date,String data)
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
	private Person userFinder(String name)
	{	for(Person user:users)
	{
		if(user.getName().toString().equals(name))
			return user;
	}
	Person p=userCreation(name);
	users.add(p);
	return p;
	}
	private  Person userCreation(String name)	
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
	private  Calendar DateToCalendar(Date date){ 
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
	public  List<SearchRepository> searchRepositories(String query)
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

	public String getOAuth()
	{return oAuthKey;}
	public GitHubClient getGitClient()
	{return client;}
}
