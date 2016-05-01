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
	public final static int  ALL=0, EXTENDED=1, CANONICAL=2;
	private static int options[]={-1,-1,-1};

	private FileList(List<File> l){				//private constructor
		list=l;
	}

	public static void  setOptions(int i){			//records entered options
		options[i]=i;
	}
	
	public static int [] getOptions(){			//returns the list of options
		return options;
	}

	public void add(File f) throws FileNotFoundException, SecurityException{	//adds a file to object's list
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

	public static FileList of(String path) throws FileNotFoundException, SecurityException {	//creates a FileList object after
		Path p=Paths.get(path);									//checking if the path is valid
		FileList f=FileList.empty();								//and adds the file(s) to the object
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

	public static FileList empty(){						//creates an empty FileList object
		List<File> l=new ArrayList<File>();
		return new FileList(l);
	}

	public  List<File> files(){						//returns a copy of an object's list
		List<File> l=new ArrayList<File>(this.list);
		return l;
	}

	public boolean contains(File f){					//checks if a list contains a certian file
		for(File temp:this.list)
			if(f.equals(temp))
				return true;
		return false;
	}

	public static List<String> format(FileList flist, int ... opts) throws IOException{	//formats the output based on the options
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



	
	public static void main(String [] args) throws FileNotFoundException, IOException{		//command line parsing is done in main
			String s=new String(".");
			FileList fl=FileList.empty();
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
					s=args[i];
					break;
				}
			}
			try{
				fl=FileList.of(s);
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
