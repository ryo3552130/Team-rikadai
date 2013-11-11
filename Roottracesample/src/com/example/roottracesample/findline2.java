package com.example.roottracesample;
public class findline2{
    public static void main(String[] args){
	try{
		int x[] = {1,2,4,5,7};//与えられた複数の地点のx座標
	    int y[] = {6,2,4,1,1};
	    double X = 9.0;//取得した現在位置
	    double Y = -1.0;
	    double [] D = new double [x.length];
	    double DD=0;
	    for (int i=0;i<x.length;i++){
	    	double d = (1.00*x[i]-y[i]+1)*(1.00*x[i]-y[i]+1)/(1*1+1*1);	
	    	D[i]=d;
	    }
	    for (int i=0;i<D.length;i++){
	    	DD = D[i]+DD;	//距離の和
	    }
	    
	    double dmin=DD;//最小の距離の和
	    DD=0;//最小距離の和を初期化
	    int A=1;
	    int C=1;
	    System.out.println("初期の最小距離の和は"+dmin);

	    for(int a=-10;a<=10;a++){
		    for(int c=-10;c<=10;c++){
		    	for (int i=0;i<x.length;i++){
			    	double d = (1.00*a*x[i]-y[i]+c)*(a*x[i]-y[i]+c)/(a*a+1);	
			    	D[i]=d;
			    }
		    	DD=0;//最小距離の和を初期化
		    	for (int i=0;i<D.length;i++){
			    	DD = D[i]+DD;	//距離の和
			    }
			
			if(DD<dmin){
			    dmin=DD;//最小距離の和
			    A=a;//dminとなるような傾き
			 
			    C=c;//dminとなるような切片
			    
			}
		    }
	    }

	    
	    System.out.println("dmin="+dmin);
	    System.out.println("A="+A);
	    System.out.println("C="+C);
	    System.out.println("直線はy="+A+"x+"+C);
	    
	    double C2=Y+(X/A);
	    
	    System.out.println("直行する直線はy="+(-1/A)+"x"+C2);
	    double XX=(C2-C)/(A+1/A);//２直線の交点のx座標
	    double YY=A*XX+C;
	    //System.out.println("XX="+XX);
	    //System.out.println("YY="+YY);
	    System.out.println("取得した現在位置は("+X+","+Y+")です");
	    System.out.println("予測される正確な座標は("+XX+","+YY+")です");
   	}catch(ArithmeticException e){
	    System.out.println("0で除算はできません。");
	}
    }
}
