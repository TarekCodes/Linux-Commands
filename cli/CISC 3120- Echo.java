class Echo{
	private final int E=0, N=1;
	private int opts[], f;
	private String str;
	
	Echo(){
		opts=new int[]{-1,-1};
		str=new String();
		f=0;
	}
	
	private void setOptions(int i){
		opts[i]=i;
	}

	void parseArgs(String [] args){
		for(int i=0;i<args.length && f<2;i++){
                        switch(args[i]){
                                case "-e": this.setOptions(E);
                                           f++;
                                           break;
                                case "-n": this.setOptions(N);
                                           f++;
                                           break;
                        }
                        if(opts[E]==E && opts[N]==N)
                                break;
                }
		StringBuilder strBuilder=new StringBuilder();
		for(int s=f;s<args.length;s++){
			strBuilder.append(args[s]);
			if(s!=args.length-1)
				strBuilder.append(" ");
		}
		str=strBuilder.toString();
	}

	String repeat(){
		if(opts[E]==E){
			str=str.replace("\\n","\n");
			str=str.replace("\\t","\t");
		}
		if(opts[N]!=N)
			str=str.concat("\n");
		return str;
	}		

        public static void main(String args[]){
      		Echo obj=new Echo();
		obj.parseArgs(args);
		System.out.print(obj.repeat());
	}
}				
