import java.io.IOException;


public class main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Republic r = new Republic("bdr3.txt");
		System.out.println(r);
		r.writeToFile("puc.txt");
	}

}
