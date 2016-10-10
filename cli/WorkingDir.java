package cli;
import java.io.*;

class WorkingDir {

	static String getCurrentDir() throws IOException {
  		File file = new File(".");
    	return file.getCanonicalPath();
	}

 	public static void main(String [] args) throws IOException {
    	System.out.println(getCurrentDir());
 	}

}
