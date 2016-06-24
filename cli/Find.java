package cli;
import java.util.*;
import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.lang.*;
import java.text.*;

class Find{
    //static CLI options
    private static String path=new String(".");
    private static String name=new String();
    private static String prune=new String();

    //private constructor to prevent creating Find objects
	private Find(){}

    //getter and setter methods that could be used if necessary

    public static String getPath(){
        return path;
    }

    public static String getName(){
        return name;
    }

    public static String getPrune(){
        return prune;
    }

    public static void setPath(String p){
        path = p;
    }

    public static void setName(String n){
        name = n;
    }

    public static void setPrune(String p){
        prune = p;
    }

    //helper method to in() that recursively traverses through the file tree
	private static void treeFiles(FileList f, String path) throws FileNotFoundException{
		try{
			for(File file:FileList.of(path).files()){
				if(file.getName().equals(name) || name.equals(""))
                               		f.add(file);
				if(file.isDirectory()  && !file.getName().equals(prune))
					treeFiles(f,file.getPath());
			}
		}
		catch(SecurityException e){System.out.println("find: '"+path+"' Permission denied");}
	}

	public static FileList in() throws FileNotFoundException, SecurityException{
        FileList.setOptions(FileList.CANONICAL);
        FileList.setOptions(FileList.ALL);
		FileList f=FileList.empty();
		Find.treeFiles(f,path);
		return f;
	}

    //parses the command line arguemnts
    public static void parseArgs(String [] args){
        for(int i=0;i<args.length;i++){
            if(args[i].charAt(0)=='-'){
                if(args[i].equals("-name") || args[i].equals("-prune")){
                    if(i==args.length-1){
                        System.out.println("Find: missing argument");
                        System.exit(1);
                    }
                    if(args[i].equals("-name")){
                        name=args[i+1];
                        i++;
                    }
                    else{
                        prune=args[i+1];
                        i++;
                    }
                }
                else{
                    System.out.println("Find: Error: unrecognized option: "+args[i]);
                                        System.exit(1);
                     }
            }
            else{
                if(!path.equals(".")){
                    System.out.println("Find: Error: unrecognized option: "+args[i]);
                    System.exit(1);
                }
                else
                    path=args[i];
            }
        }
    }

	public static void main(String [] args) throws IOException{
        Find.parseArgs(args);
		try{
		FileList f=Find.in();
		List<String> list=FileList.format(f,FileList.getOptions());
		for(String file:list)
			System.out.println(file);
		}
		catch(FileNotFoundException q){System.out.println("Can't Access "+path+" : No Such File or Directory");}
	}
}


