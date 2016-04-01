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
	private static int arr[]={-1,-1,-1};

	private FileList(List<File> l){				//constructor
		list=l;
	}

	private static void  options(int i){			//stores the options entered
		arr[i]=i;
	}

	public static FileList of(String path) throws FileNotFoundException, SecurityException {	//creates a FileList object, checks
		Path p=Paths.get(path);									//if the path is valid and stores the
		List<File> lst=new ArrayList<File>();							//file(s) in the object
		boolean isAbs=p.isAbsolute();
		for(int i=1;i<=p.getNameCount();i++){
			String s=p.subpath(0,i).toString();
			if(isAbs)
				s="/".concat(s);
			File temp=new File(s);
			if(!temp.exists())
				throw new FileNotFoundException("Can't Access "+temp.toPath()+" : No Such File or Directory");
			if(!temp.canRead() || !temp.canExecute())
				throw new SecurityException("Can't Open Directory "+temp.toPath()+" : Permission Denied");
			if(i==p.getNameCount()){
				if(temp.isDirectory())
					lst=Arrays.asList(temp.listFiles());
				else
					lst.add(temp);
			}
		}	
		return new FileList(lst);
	}
	
	public static FileList empty(){
		List<File> l=new ArrayList<File>();
		return new FileList(l);
	}

	public  List<File> files(){
		List<File> l=this.list;
		return l;
	}

	public boolean contains(File f){
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



	
	public static void main(String [] args) throws FileNotFoundException, IOException{	//command line parsing is done in main
			String p=new String(".");
			for(int i=0;i<args.length;i++){
				if(args[i].charAt(0)=='-'){
					for(int x=1;x<args[i].length();x++)
						switch(args[i].charAt(x)){
							case 'A': FileList.options(FileList.ALL);
								  break;
							case 'c': FileList.options(FileList.CANONICAL);
								  break;
							case 'l': FileList.options(FileList.EXTENDED);
								  break;
							default: System.out.println("Invalid options");
								  System.exit(1);
						}
				}
				else{
					p=args[i];
					break;
				}
			}
			FileList fl=FileList.of(p);
			List<String> mlist=FileList.format(fl,FileList.arr);
			for(String str:mlist)
				System.out.println(str);				
	}
}
