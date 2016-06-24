package cli;
import java.util.*;
import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.lang.*;
import java.text.*;
public class FileList{

	private List<File> list;
    private static String path=new String(".");
	public final static int  ALL=0, EXTENDED=1, CANONICAL=2;
	private static int options[]={-1,-1,-1};

    //constructor
	private FileList(List<File> l){
		list=l;
	}

    //setter method for options
	public static void  setOptions(int i){
		options[i]=i;
	}

    //getter method for options
	public static int [] getOptions(){
		return options;
	}

    //getter method for path
    public static String getPath(){
        return path;
    }

    //setter method for path
    public static void setPath(String p){
        path=p;
    }

    //adds files to the list after verifying that they're valid
	public void add(File f) throws FileNotFoundException, SecurityException{
		Path p=f.toPath();
		File temp=new File("");
		boolean isAbs=p.isAbsolute();
                for(int i=1;i<=p.getNameCount();i++){
                	String s=p.subpath(0,i).toString();
                        if(isAbs)
                                s="/".concat(s);
                        temp=new File(s);
			if(!temp.exists())
                                throw new FileNotFoundException("Can't Access "+temp.toPath()+" : No Such File or Directory");
                        if((!temp.canRead() || !temp.canExecute()) && temp.isDirectory() && i!=p.getNameCount())
                                throw new SecurityException("Can't Open Directory "+temp.toPath()+" : Permission Denied");
		}
               this.list.add(temp);
	}

    //verifies that a path is valid and calls the add() method
	public static FileList of(String path) throws FileNotFoundException, SecurityException {
		Path p=Paths.get(path);
		FileList f=FileList.empty();
		List<File> lst=new ArrayList<File>();
		File temp=new File("");
		boolean isAbs=p.isAbsolute();
		for(int i=1;i<=p.getNameCount();i++){
			String s=p.subpath(0,i).toString();
			if(isAbs)
				s="/".concat(s);
			temp=new File(s);
			if(!temp.exists())
				throw new FileNotFoundException("Can't Access "+temp.toPath()+" : No Such File or Directory");
			if(!temp.canRead() || !temp.canExecute() && temp.isDirectory())
				throw new SecurityException("Can't Open Directory "+temp.toPath()+" : Permission Denied");
		}
		if(temp.isDirectory())
			f.list=Arrays.asList(temp.listFiles());
		else
			f.list.add(temp);
		return f;
	}

    //static factory method to create an empty FileList object
	public static FileList empty(){
		List<File> l=new ArrayList<File>();
		return new FileList(l);
	}

    //returns a copy of the files list
	public  List<File> files(){
		List<File> l=new ArrayList<File>(this.list);
		return l;
	}

    //checks if the list contains a certain file
	public boolean contains(File f){
		for(File temp:this.list)
			if(f.equals(temp))
				return true;
		return false;
	}

    //formats the output based on the command line options
	public static List<String> format(FileList flist, int ... opts) throws IOException{
		List<String> slist=new ArrayList<String>();
		String temp=new String();
		Collections.sort(flist.list);
		for(File f: flist.list){
			if(opts[ALL]==ALL || !f.isHidden()){
				if(opts[EXTENDED]==EXTENDED){
					if(f.isDirectory())
						temp="d";
					else
						temp="-";
				PosixFileAttributes attr=Files.readAttributes((f.toPath()),PosixFileAttributes.class);
				temp=temp.concat(PosixFilePermissions.toString(attr.permissions())+" ");
				temp=temp.concat(attr.owner()+"\t");
				temp=temp.concat(attr.group()+"  ");
				temp=temp.concat(Long.toString(attr.size())+"  ");
				temp=temp.concat(attr.lastModifiedTime()+"   ");

				}
			if(opts[CANONICAL]==CANONICAL)
				temp=temp.concat(f.getCanonicalPath());
			else
				temp=temp.concat(f.getName());
			if(f.isDirectory())
				temp=temp.concat("/");

			slist.add(temp);
			temp=new String();
			}
		}

		return slist;
	}

    //parses the command line arguments
    public static void parseArgs(String [] args){
        for(int i=0;i<args.length;i++){
            if(args[i].charAt(0)=='-'){
                for(int x=1;x<args[i].length();x++)
                    switch(args[i].charAt(x)){
                        case 'A': FileList.setOptions(FileList.ALL);
                              break;
                        case 'c': FileList.setOptions(FileList.CANONICAL);
                              break;
                        case 'l': FileList.setOptions(FileList.EXTENDED);
                              break;
                        default: System.out.println("Invalid options");
                              System.exit(1);
                    }
                }
            else{
                path=args[i];
                break;
            }
        }
    }


	public static void main(String [] args) throws FileNotFoundException, IOException{
            FileList.parseArgs(args);
			FileList fl=FileList.empty();
			try{
				fl=FileList.of(FileList.getPath());
			}
			catch(FileNotFoundException e){
				System.out.println(e.getMessage());
				System.exit(1);
			}
			catch(SecurityException e){
				System.out.println(e.getMessage());
                System.exit(1);
            }
			List<String> mlist=FileList.format(fl,FileList.getOptions());
			for(String str:mlist)
				System.out.println(str);
	}
}
