import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Test;


public class test {

	@Test
	public void test() throws FileNotFoundException {
		Republic r = new Republic("bdr1.txt");
		System.out.println(r);
	}

}
