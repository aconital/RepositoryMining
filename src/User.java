
public class User implements Node {

	
	private String name;
	private String email;
	
	public User()
	{}
	public User(String name,String email)
	{
		this.setName(name);
		this.setEmail(email);
	}
	@Override
	public Node getNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node getNodeDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node getIntractionwith(Node n) {
		// TODO Auto-generated method stub

		return null;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

}
