package traceUI;
import java.io.*;
import java.lang.Math;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.*;


public class UI {
		//定义变量
		static int k=0;//面数
		static double a, lp, w, yo, Dis;//入瞳半径，入瞳距，视场角，物高，物距
		static double EFFL;//焦距
		double lp_, um;//出瞳距，中间量
		static double lt_, ls_;//场曲计算中间量
		static double[] r, n, u, NF, NC, d;//曲率半径，d光折射率，角度，F光折射率，C光折射率，间距
		static double[] h, i, l, su, si, s, t;//像高，角度，像距，中间量
		static double l_;//理想像距
		static double l2;
		static String out="";//输出字符串
		
		
	//调用该函数计算各面像高，各面像距等量
	public static void calc(int ifaxid, int ifgauss, double kw, double kn)	{
			double um=w;
			//判断是否轴上光，是否理想光，分四种情况
			if (ifaxid == 1&&ifgauss == 1){
				for (int m = 1; m <= k; m++){
					//判断物距是否无穷
					if (-Dis>1e10){
						h[1] = a; u[1] = 0;
						if (m == 1)
							i[m] = a / r[m];
						else
							i[m] = (l[m] - r[m])*u[m] / r[m];
					}
					else{
						u[1] = Math.atan(a / (lp - l[1]));
						i[m] = (l[m] - r[m])*u[m] / r[m];
					}	
					i[m + 1] = i[m] * n[m] / n[m + 1];
					u[m + 1] = u[m] + i[m] - i[m + 1];
					l[m + 1] = i[m + 1] * r[m] / u[m + 1] + r[m];
					h[m] = l[m + 1] * u[m + 1];
					if (m<k)
						l[m + 1] = l[m + 1] - d[m];
				}
			}
			if (ifaxid == 0&&ifgauss == 1){
				if (-Dis>1e10){
					u[1] = Math.sin(w); l[1] = lp;
				}
				else{
					u[1] = Math.sin(Math.atan(yo / (lp - l[1]))); 
					l[1] = lp;
				}
				for (int m = 1; m <= k; m++)		{
					i[m] = (l[m] - r[m])*u[m] / r[m];
					i[m + 1] = i[m] * n[m] / n[m + 1];
					u[m + 1] = u[m] + i[m] - i[m + 1];
					l[m + 1] = i[m + 1] * r[m] / u[m + 1] + r[m];
					h[m] = l[m + 1] * u[m + 1];
					if (m<k)
						l[m + 1] = l[m + 1] - d[m];
				}
			}
			if (ifaxid == 1&&ifgauss == 0)	{
				if (-Dis>1e10){
					u[1] = 0; h[1] = kn*a;
					l[1] = lp + kn*a / Math.tan(u[1]);
				}
				else{
					um = Math.atan(a / (lp - l[1]));
					su[1] = kn*Math.sin(um);
					u[1] = Math.asin(su[1]);
				}
				for (int m = 1; m <= k; m++){
					if (-Dis>1e10){
						if (m == 1){
							si[m] = h[m] / r[m];
							i[m] = Math.asin(si[m]);
						}
						else{
							si[m] = (l[m] - r[m])*su[m] / r[m];
							i[m] = Math.asin(si[m]);
						}
					}
					else{
						si[m] = (l[m] - r[m])*su[m] / r[m];
						i[m] = Math.asin(si[m]);
					}
					si[m + 1] = si[m] * n[m] / n[m + 1];
					i[m + 1] = Math.asin(si[m + 1]);
					u[m + 1] = u[m] + i[m] - i[m + 1];
					su[m + 1] = Math.sin(u[m + 1]);
					l[m + 1] = si[m + 1] * r[m] / su[m + 1] + r[m];
					if (m<k)
						l[m + 1] = l[m + 1] - d[m];
				}
			}
			if (ifaxid == 0&&ifgauss == 0){
				if (-Dis>1e10){
					u[1] = kw*w;
					l[1] = lp + kn*a / Math.tan(u[1]);
				}
				else{
					um = Math.atan(a / (lp - l[1]));
					u[1] = Math.atan((kw*yo - kn*a) / (lp - l[1]));
					l[1] = lp + kn*a / Math.tan(u[1]);
				}
				for (int m = 1; m <= k; m++){
					su[m] = Math.sin(u[m]);
					si[m] = (l[m] - r[m])*su[m] / r[m];
					i[m] = Math.asin(si[m]);
					si[m + 1] = si[m] * n[m] / n[m + 1];
					i[m + 1] = Math.asin(si[m + 1]);
					u[m + 1] = u[m] + i[m] - i[m + 1];
					su[m + 1] = Math.sin(u[m + 1]);
					l[m + 1] = si[m + 1] * r[m] / su[m + 1] + r[m];
					if (m<k)
						l[m + 1] = l[m + 1] - d[m];
				}
			}
		}
	//该函数对视场、孔径进行循环，计算各情况下的像高
	public static String CalcComm()
	{
		double[] kn = { 1,-1,0.7,-0.7,0 };
		double[] kw = { 1,0.7 };
		double[] l1, u1, i1, h1, su1, si1;
		double[][] Cwn;
		double lk;
		l1=new double [20];
		u1=new double [20];
		i1=new double [20];
		h1=new double [20];
		si1=new double [20];
		su1=new double [20];
		Cwn=new double [20][20];
		
		for (int i = 0; i < 5; i++)
			for (int j = 0; j < 2; j++) {
				if (-Dis < 1e10)
				{
					u1[1] = Math.atan((kw[j]*yo - kn[i]*a) / (lp - Dis));
					l1[1] = lp + kn[i]*a / Math.tan(u1[1]);
					su1[1] = Math.sin(u1[1]);
				}
				else {
					u1[1] = kw[j] * w;
					l1[1] = lp + kn[i] * a / Math.tan(u1[1]);
				}
				for (int m = 1; m <= k; m++) {
					su1[m] = Math.sin(u1[m]);
					si1[m] = (l1[m] - r[m])*su1[m] / r[m];
					i1[m] = Math.asin(si1[m]);
					si1[m + 1] = si1[m] * n[m] / n[m + 1];
					i1[m + 1] = Math.asin(si1[m + 1]);
					u1[m + 1] = u1[m] + i1[m] - i1[m + 1];
					su1[m + 1] = Math.sin(u1[m + 1]);
					l1[m + 1] = si1[m + 1] * r[m] / su1[m + 1] + r[m];
					h1[m] = l1[m + 1] * u1[m + 1];
					h1[m + 1] = h1[m] - d[m] * u1[m + 1];
					if (m<k) l1[m + 1] = l1[m + 1] - d[m];
				}
				l[1] = Dis;
				calc(1, 1, 1, 1);
				lk = l[k + 1];
				Cwn[i][j] = -1*(lk - l1[k + 1])*Math.tan(u1[k + 1]);
				
			}
		String s;
		s="1视场1孔径彗差:"+String.format("%.4f", (Cwn[0][0]+Cwn[1][0])/2-Cwn[4][0]);
		s+="\r\n\r\n1视场0.7孔径彗差:"+String.format("%.4f", (Cwn[2][0]+Cwn[3][0])/2-Cwn[4][0]);
		s+="\r\n\r\n0.7视场1孔径彗差:"+String.format("%.4f", (Cwn[0][1]+Cwn[1][1])/2-Cwn[4][1]);
		s+="\r\n\r\n0.7视场1孔径彗差:"+String.format("%.4f", (Cwn[2][1]+Cwn[3][1])/2-Cwn[4][1]);
		return s;
	}
	//通过杨氏公式，计算场曲和像散
	public static void CalcAbbration(double L)
	{
		double[] x, PA, D, a, i_, s_, t_;
		x=new double[20];
		PA=new double[20];;
		D=new double[20];
		a=new double[20];
		i_=new double[20];
		s_=new double[20];
		t_=new double[20];
		
		for (int m = 1; m <= k; m++) {
			PA[m] = l[m] * Math.sin(u[m]) / Math.cos((i[m] - u[m]) / 2);
			x[m] = Math.pow(PA[m], 2) / 2 / r[m];
		}
		if(-L>1e10)
			s[1] = t[1] = -1e12;
		else {
			t[1] = s[1] = (Dis - x[1]) / Math.cos(u[1]);
		}
		for (int m = 1; m < k; m++) {
			D[m] = (d[m] - x[m] + x[m + 1]) / Math.cos(u[m + 1]);
		}
		
		for (int m = 1; m<=k; m++)	{
			i_[m] = Math.asin(n[m] / n[m + 1] * Math.sin(i[m]));
			a[m] = n[m + 1] * Math.cos(i_[m]) / r[m] - n[m] * Math.cos(i[m]) / r[m] ;
			s_[m] = n[m + 1] / (a[m] + n[m] / s[m]);
			t_[m] = n[m + 1] * Math.pow(Math.cos(i_[m]), 2)/(n[m] * Math.pow(Math.cos(i[m]), 2) / t[m] + a[m]);
			if (m < k)		{
				t[m + 1] = t_[m]-D[m];
				s[m + 1] = s_[m]-D[m];
			}
		}
		lt_ = t_[k] * Math.cos(u[k+1 ]) + x[k];
		ls_ = s_[k] * Math.cos(u[k+1 ]) + x[k];
	}
	
	
	//计算各折射率下的实际像高
	public static double calcy(double kita, int mode){
		double[] l1, u1, i1, h1,su1,si1, nmode; 
		double yb, lk;
		l1=new double [20];
		u1=new double [20];
		i1=new double [20];
		h1=new double [20];
		su1=new double [20];
		si1=new double [20];
		nmode=new double [20];
		//根据参数，选择不同的折射率
		if(mode==1){
			System.arraycopy(n, 1, nmode, 1, k+1);
		}
		if(mode==2){
			System.arraycopy(NF, 1, nmode, 1, k+1);
		}
		if(mode==3){
			System.arraycopy(NC, 1, nmode, 1, k+1);
		}
		//判断物距是否无穷
		if (-Dis < 1e10) {
			u1[1]=Math.atan(kita*yo/(lp-Dis));
			l1[1]=lp;
			su1[1] = Math.sin(u1[1]);
			for (int m = 1; m <= k; m++) {
				si1[m] = (l1[m] - r[m])*su1[m] / r[m];
				i1[m]=Math.asin(si1[m]);
				si1[m + 1] = si1[m] * nmode[m] / nmode[m + 1];
				i1[m+1]=Math.asin(si1[m+1]);
				u1[m+1]=u1[m]+i1[m]-i1[m+1];
				su1[m+1]=Math.sin(u1[m+1]);
				l1[m+1]=si1[m+1]*r[m]/su1[m+1]+r[m];
				if (m < k) 
					l1[m + 1] = l1[m + 1] - d[m];
			}

		}
		else
		{
			u1[1] = kita*w;
			l1[1]=lp;
			for (int m = 1; m <= k; m++) {
				su1[m] = Math.sin(u1[m]);
				si1[m] = (l1[m] - r[m])*su1[m] / r[m];
				i1[m] = Math.asin(si1[m]);
				si1[m + 1] = si1[m] * nmode[m] / nmode[m + 1];
				i1[m + 1] = Math.asin(si1[m + 1]);
				u1[m + 1] = u1[m] + i1[m] - i1[m + 1];
				su1[m + 1] = Math.sin(u1[m + 1]);
				l1[m + 1] = si1[m + 1] * r[m] / su1[m + 1] + r[m];
				h1[m] = l1[m + 1] * u1[m + 1];
				h1[m + 1] = h1[m] - d[m] * u1[m + 1];
				if (m<k) 
					l1[m + 1] = l1[m + 1] - d[m];
			}
		}
		l[1] = Dis;
		calc(1, 1, 1, 1);
		lk = l[k + 1];
		
		yb = -1*((lk - l1[k + 1])*Math.tan(u1[k + 1]));
		return yb;
	}
	//计算理想像高
	public static double calcGaussY(double kw, double L){
		double[] l1, u1, i1, h1;
		double yb;
		l1=new double [20];
		u1=new double [20];
		i1=new double [20];
		h1=new double [20];
		//判断是否无穷远
		if (-L < 1e10)
		{	
			l1[1] = Dis;
			u1[1] = Math.sin(Math.atan(kw*a / (lp - Dis)));
			
			for (int m = 1; m <= k; m++) {
				i1[m] = (l1[m] - r[m])*u1[m] / r[m];
				i1[m + 1] = i1[m] * n[m] / n[m + 1];
				u1[m + 1] = u1[m] + i1[m] - i1[m + 1];
				l1[m + 1] = i1[m + 1] * r[m] / u1[m + 1] + r[m];
				h1[m] = l1[m + 1] * u1[m + 1];
				if (m<k) l1[m + 1] = l1[m + 1] - d[m];
			}
			double beta=u1[1]/u1[k+1];
			yb=kw*yo*beta;
		}
		else {
			yb = -1*EFFL*Math.tan(kw*w);
		}
		return yb;
	}
	
	public static void main(String[] args){
		
			r = new double[20];
			n = new double[20];
			d = new double[20];
			u = new double[20];
			NF = new double[20];
			NC = new double[20];
			h = new double[20];
			i  = new double[20];
			l= new double[20];
			su= new double[20];
			si= new double[20];
			s= new double[20];
			t= new double[20];
			
			
		//初始化图形界面
		Display display=new Display();
		
		Shell shell=new Shell(display,SWT.SHELL_TRIM);
		shell.setText("RayTrace");
		shell.setSize(600, 600);
	
		
		Group grp1= new Group(shell,SWT.NONE);
		grp1.setText("输入数据");
		grp1.setLocation(5,5);
		grp1.setSize(300, 500);


		Text num = new Text(grp1,SWT.BORDER);
		num.setSize(100, 20);
		num.setLocation(100, 20);
		Label Lnum= new Label(grp1,SWT.LEFT);
		Lnum.setLocation(10, 20);
		Lnum.setSize(100, 20);
		Lnum.setText("面数：");
		Lnum.setFont(new Font(display,"微软雅黑",10,SWT.NORMAL));
		
		Text nindex = new Text(grp1,SWT.MULTI|SWT.V_SCROLL|SWT.BORDER);
		nindex.setSize(110, 40);
		nindex.setLocation(100,50 );
		Label Lnindex= new Label(grp1,SWT.LEFT);
		Lnindex.setLocation(10, 50);
		Lnindex.setSize(70, 30);
		Lnindex.setText("折射率：");
		Lnindex.setFont(new Font(display,"微软雅黑",10,SWT.NORMAL));
		
		Text dst = new Text(grp1,SWT.MULTI|SWT.V_SCROLL|SWT.BORDER);
		dst.setSize(110, 40);
		dst.setLocation(100, 100);
		Label Ldst= new Label(grp1,SWT.LEFT);
		Ldst.setLocation(10, 100);
		Ldst.setSize(100, 30);
		Ldst.setText("间距：");
		Ldst.setFont(new Font(display,"微软雅黑",10,SWT.NORMAL));
		
		Text rp = new Text(grp1,SWT.BORDER);
		rp.setSize(100, 20);
		rp.setLocation(100, 150);
		Label Lrp= new Label(grp1,SWT.LEFT);
		Lrp.setLocation(10, 150);
		Lrp.setSize(100, 30);
		Lrp.setText("入瞳半径：");
		Lrp.setFont(new Font(display,"微软雅黑",10,SWT.NORMAL));
		
		Text pdst = new Text(grp1,SWT.BORDER);
		pdst.setSize(100, 20);
		pdst.setLocation(100, 180);
		Label Lpdst= new Label(grp1,SWT.LEFT);
		Lpdst.setLocation(10, 180);
		Lpdst.setSize(100, 30);
		Lpdst.setText("入瞳距：");
		Lpdst.setFont(new Font(display,"微软雅黑",10,SWT.NORMAL));
		
		Text wangle = new Text(grp1,SWT.BORDER);
		wangle.setSize(100, 20);
		wangle.setLocation(100, 210);
		Label Lwangle= new Label(grp1,SWT.LEFT);
		Lwangle.setLocation(10, 210);
		Lwangle.setSize(100, 30);
		Lwangle.setText("物方视场角：");
		Lwangle.setFont(new Font(display,"微软雅黑",10,SWT.NORMAL));
		
		Text oheight = new Text(grp1,SWT.BORDER);
		oheight.setSize(100, 20);
		oheight.setLocation(100, 240);
		Label Loheight= new Label(grp1,SWT.LEFT);
		Loheight.setLocation(10, 240);
		Loheight.setSize(100, 30);
		Loheight.setText("物高：");
		Loheight.setFont(new Font(display,"微软雅黑",10,SWT.NORMAL));
		
		Text nindexF = new Text(grp1,SWT.MULTI|SWT.V_SCROLL|SWT.BORDER);
		nindexF.setSize(110, 40);
		nindexF.setLocation(100, 270);
		Label LnindexF= new Label(grp1, SWT.LEFT);
		LnindexF.setLocation(10, 270);
		LnindexF.setSize(100, 30);
		LnindexF.setText("nF：");
		LnindexF.setFont(new Font(display,"微软雅黑",10,SWT.NORMAL));
		
		Text nindexC = new Text(grp1,SWT.MULTI|SWT.V_SCROLL|SWT.BORDER);
		nindexC.setSize(110, 40);
		nindexC.setLocation(100, 320);
		Label LnindexC= new Label(grp1,SWT.LEFT);
		LnindexC.setLocation(10, 320);
		LnindexC.setSize(100, 30);
		LnindexC.setText("nC：");
		LnindexC.setFont(new Font(display,"微软雅黑",10,SWT.NORMAL));
		
		Text rdst = new Text(grp1,SWT.MULTI|SWT.V_SCROLL|SWT.BORDER);
		rdst.setSize(110, 40);
		rdst.setLocation(100, 370);
		Label Lrdst= new Label(grp1,SWT.LEFT);
		Lrdst.setLocation(10, 370);
		Lrdst.setSize(100, 30);
		Lrdst.setText("各面曲率半径：");
		Lrdst.setFont(new Font(display,"微软雅黑",10,SWT.NORMAL));
		
		Text odst = new Text(grp1,SWT.BORDER);
		odst.setSize(100, 20);
		odst.setLocation(100, 420);
		Label Lodst= new Label(grp1,SWT.LEFT);
		Lodst.setLocation(10, 420);
		Lodst.setSize(100, 30);
		Lodst.setText("物距：");
		Lodst.setFont(new Font(display,"微软雅黑",10,SWT.NORMAL));
		
		
		Menu menu = new Menu(shell,SWT.BAR);
		shell.setMenuBar(menu);
		MenuItem file = new MenuItem(menu,SWT.CASCADE);
		file.setText("&File");

		Menu filemenu = new Menu(shell,SWT.DROP_DOWN);
		file.setMenu(filemenu);
		MenuItem Item11= new MenuItem(filemenu,SWT.PUSH);
		Item11.setText("Import");
		MenuItem Item12= new MenuItem(filemenu,SWT.PUSH);
		Item12.setText("Export");
		MenuItem calc = new MenuItem(menu,SWT.CASCADE);
		calc.setText("&Calc");

		Menu calcmenu = new Menu(shell,SWT.DROP_DOWN);
		calc.setMenu(calcmenu);
		MenuItem Item21= new MenuItem(calcmenu,SWT.DROP_DOWN);
		Item21.setText("球差");
		MenuItem Item22= new MenuItem(calcmenu,SWT.DROP_DOWN);
		Item22.setText("彗差");
		
		MenuItem Item23= new MenuItem(calcmenu,SWT.DROP_DOWN);
		Item23.setText("场曲&&像散");
		MenuItem Item24= new MenuItem(calcmenu,SWT.DROP_DOWN);
		Item24.setText("色差");
		MenuItem Item25= new MenuItem(calcmenu,SWT.DROP_DOWN);
		Item25.setText("畸变");
		
		MenuItem about = new MenuItem(menu,SWT.CASCADE);
		about.setText("&About");
		Menu aboutmenu = new Menu(shell,SWT.DROP_DOWN);
		about.setMenu(aboutmenu);
		MenuItem Item31=new MenuItem(aboutmenu,SWT.DROP_DOWN);
		Item31.setText("Help");
		
		
		Text dscrb2=new Text(shell,SWT.LEFT);
		dscrb2.setText("像差");
		dscrb2.setLocation(330, 220);
		dscrb2.setSize(80, 20);
		dscrb2.setEditable(false);
		dscrb2.setFont(new Font(display,"微软雅黑",10,SWT.NORMAL));
		
		
		Text data2=new Text(shell,SWT.LEFT|SWT.MULTI|SWT.BORDER);
		data2.setEditable(false);
		data2.setLocation(330, 250);
		data2.setSize(200, 250);
		data2.setFont(new Font(display,"微软雅黑",10,SWT.NORMAL));
		
		
		
		Item21.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				String s2="1孔径球差：";
				calc(1,0,0,1);//轴上，实际，0视场，全孔径
				l2=l[k+1];
				//实际像距减理想像距
				s2+=String.format("%.4f", l2-l_);//l_为理想像距
				
				double l3,l4;
				calc(1,1,0,0.7);//轴上，理想，0视场，0.7孔径
				l3=l[k+1];
				calc(1,0,0,0.7);//轴上，实际，0视场，0.7孔径
				l4=l[k+1];
				s2+="\r\n\r\n0.7孔径球差"+String.format("%.4f", l4-l3);
				out+=s2;
				data2.setText(s2);
			}
		});
		
		Item22.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				data2.setText(CalcComm());
				out+=CalcComm();
			}
		});
		
		Item23.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				//计算出轴外实际光的一些数据后计算场曲和像散
				calc(0,0,1,0);
				CalcAbbration(Dis);
				String s2;
				s2="弧矢场曲:"+String.format("%.4f", ls_-l_);
				s2+="\r\n\r\n子午场曲:"+String.format("%.4f", lt_-l_);
				s2+="\r\n\r\n像散:"+String.format("%.4f", lt_-ls_);
				data2.setText(s2);
				out+=s2;
			}
		});
		
		Item24.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				double y3, y4, y5, y6, y7, y8;
				//计算各折射率的像高
				y3=calcy(1,1);
				y4=calcy(0.7,1);			
				y5=calcy(1,2);
				y6=calcy(0.7, 2);
				y7=calcy(1,3);
				y8=calcy(0.7, 3);
				//System.out.println("y6="+y6+"\r\n"+"y8="+y8);
				String s2;
				s2="1视场倍率色差"+String.format("%.6f", y5-y7);
				s2+="\r\n1视场f光实际像高"+String.format("%.6f",y5);
				s2+="\r\n1视场c光实际像高"+String.format("%.6f",y7);
				s2+="\r\n\r\n0.7视场倍率色差"+String.format("%.6f",y6-y8);
				s2+="\r\n0.7视场f光实际像高"+String.format("%.6f",y6);
				s2+="\r\n0.7视场c光实际像高"+String.format("%.6f",y8);
				
				//计算各折射率各孔径的实际像距，得出位置色差
				double lc1, lc2, lf1, lf2;
				double[] nt;
				nt = new double [20];
				nt=n.clone();
				n=NC.clone();
				calc(1, 0, 0, 1);
				lc1 = l[k + 1];
				calc(1, 0, 0, 0.7);
				lc2 = l[k + 1];
				n=NF.clone();
				calc(1, 0, 0, 1);
				lf1 = l[k + 1];
				calc(1, 0, 0, 0.7);
				lf2 = l[k + 1];
				n=nt.clone();
				s2+="\r\n\r\n1视场位置色差："+String.format("%.6f",lf1-lc1);
				s2+="\r\n\r\n0.7视场位置色差："+String.format("%.6f",lf2-lc2);
				out+=s2;
				data2.setText(s2);

			}
		});
		
		Item25.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				//计算实际和理想像高，相减后得出畸变
				double y3,y4,y1,y2;
				y3=calcy(1,1);
				y4=calcy(0.7,1);
				y1=calcGaussY(1,Dis);
				y2=calcGaussY(0.7,Dis);
				String s;
				
				s="1视场畸变："+String.format("%.6f", (y3-y1));
				s+="\r\n实际像高："+String.format("%.6f",y3)+"\r\n"+"理想像高："+String.format("%.6f",y1);
				s+="\r\n\r\n0.7视场畸变："+String.format("%.6f", (y4-y2));
				s+="\r\n实际像高："+String.format("%.6f",y4)+"\r\n"+"理想像高："+String.format("%.6f",y2);
				out+=s;
				data2.setText(s);
			}
		});
		//导入文件部分						
		Item11.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				FileDialog fdlg1=new FileDialog(shell,SWT.OPEN);
				String fileName = fdlg1.open();
				if(fileName!=null){
						FileInputStream fis = null;
						try {
							fis = new FileInputStream(fileName);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						InputStreamReader isr=new InputStreamReader(fis);						
						int in;
						String s = "";
						try {
							while ((in=isr.read())!=-1){
								s+=(char)in;
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String lines[] = s.split("\\r?\\n");
						String t="";
						k=Integer.valueOf(lines[0]);
						num.setText(lines[0]);
						for(int m=1;m<=k+1;m++){
							n[m]=Double.valueOf(lines[m]);
							t+=(lines[m]+"\r\n");
						}
						nindex.setText(t);	t="";
						for(int m=1;m<k;m++){
							d[m]=Double.valueOf(lines[m+k+1]);
							t+=(lines[m+k+1]+"\r\n");
						}
						dst.setText(t); t="";
						a=Double.valueOf(lines[2*k+1]);
						rp.setText(lines[2*k+1]);
						
						lp=Double.valueOf(lines[2*k+2]);
						pdst.setText(lines[2*k+2]);
						
						w=Double.valueOf(lines[2*k+3]);
						wangle.setText(lines[2*k+3]);
						
						yo=Double.valueOf(lines[2*k+4]);
						oheight.setText(lines[2*k+4]);
						
						for(int m=1;m<=k+1;m++){
							NF[m]=Double.valueOf(lines[m+2*k+4]);
							t+=(lines[m+2*k+4]+"\r\n");
						}
						nindexF.setText(t); t="";
						for(int m=1;m<=k+1;m++){
							NC[m]=Double.valueOf(lines[m+3*k+5]);
							t+=(lines[m+3*k+5]+"\r\n");
						}
						nindexC.setText(t); t="";
						for(int m=1;m<=k;m++){
							r[m]=Double.valueOf(lines[m+4*k+6]);
							t+=(lines[m+4*k+6]+"\r\n");
						}
						rdst.setText(t); t="";
										
						Dis=Double.valueOf(lines[5*k+7]);
						odst.setText(lines[5*k+7]);

				}
			}
		});				
		//导出文件部分
		Item12.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				FileDialog fdlg2=new FileDialog(shell,SWT.SAVE);
				fdlg2.setFileName("Export.txt");
				String fileName = fdlg2.open();
				if(fileName!=null){
						FileOutputStream fos = null;
						try {
							fos = new FileOutputStream(fileName);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						OutputStreamWriter osw=new OutputStreamWriter(fos);
						try {
							osw.write(out);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							osw.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
				}
			}
		});

		//打开帮助文件部分
		Item31.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				try { 

				    Process process = Runtime.getRuntime().exec(
				    "cmd.exe  /c notepad C:/Users/May22/help.txt");
				} catch (Exception e) {
				e.printStackTrace();
				}
			}
		});
		
		Text dscrb1=new Text(shell,SWT.LEFT);
		dscrb1.setText("基础数据");
		dscrb1.setLocation(330, 5);
		dscrb1.setSize(80, 20);
		dscrb1.setEditable(false);
		dscrb1.setFont(new Font(display,"微软雅黑",10,SWT.NORMAL));
		
		Text data=new Text(shell,SWT.LEFT|SWT.BORDER|SWT.MULTI);
		data.setEditable(false);
		data.setLocation(330, 30);
		data.setSize(200, 180);
		data.setFont(new Font(display,"微软雅黑",10,SWT.NORMAL));
		
		
		Button ok=new Button(grp1,SWT.PUSH);
		ok.setText("OK");
		ok.setBounds(230, 420, 50, 20);
		ok.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){		
				if(num.getText()!="")
					k=Integer.valueOf(num.getText());
				if(rp.getText()!="")
					a=Double.valueOf(rp.getText());
				if(pdst.getText()!="")
					lp=Double.valueOf(pdst.getText());
				if(wangle.getText()!="")
					w=Double.valueOf(wangle.getText());
				if(oheight.getText()!="")
					yo=Double.valueOf(oheight.getText());
				if(odst.getText()!="")
					Dis=Double.valueOf(odst.getText());
				
				String s;
				s=nindex.getText();
				String lines[] = s.split("\\r?\\n");
				for(int i=1;i<=k+1;i++){
					if(lines[i-1]!="")
						n[i]=Double.valueOf(lines[i-1]);
				}
				s=dst.getText();
				String lines1[] = s.split("\\r?\\n");
				for(int i=1;i<=k-1;i++){
					if(lines1[i-1]!="")
						d[i]=Double.valueOf(lines1[i-1]);
		
				}
				s=rdst.getText();
				String lines2[] = s.split("\\r?\\n");
				for(int i=1;i<=k;i++){
					if(lines2[i-1]!="")
						r[i]=Double.valueOf(lines2[i-1]);

				}
				s=nindexF.getText();
				String lines3[] = s.split("\\r?\\n");
				for(int i=1;i<=k+1;i++){
					if(lines3[i-1]!="")
						NF[i]=Double.valueOf(lines3[i-1]);
				}
				s=nindexC.getText();
				String lines4[] = s.split("\\r?\\n");
				for(int i=1;i<=k+1;i++){
					if(lines4[i-1]!="")
						NC[i]=Double.valueOf(lines4[i-1]);
				}																

				EFFL = getEFFL();//计算焦距
				String s1="EFFL: ";
				
				s1+=String.format("%.4f", EFFL);
				
				s1+="\r\n\r\n"+"1视场理想像高：";
				l[1]=Dis;
				s1+=String.format("%.4f", calcGaussY(1,Dis));
				s1+="\r\n\r\n"+"0.7视场理想像高：";
				s1+=String.format("%.4f", calcGaussY(0.7,Dis));
				s1+="\r\n\r\n出瞳距:";
				s1+=String.format("%.4f", calclp(d[1],w));
				l[1]=Dis;
				calc(1,1,0,1);
				l_=l[k+1];
				s1+="\r\n\r\n高斯像距:"+String.format("%.4f", l_);
				//System.out.println(s1);
				data.setText(s1);
				out+=s1;
				
			}
			public double getEFFL(){
				l[1]=-1e12;//将物距设为无穷远
				calc(1,1,1,1);//轴上，理想
				return  h[1]/u[k+1];
			}
			
			public double calclp(double l,double u){
				double[] l2, u2, i2, h2;
				l2=new double [20];
				u2=new double [20];
				i2=new double [20];
				h2=new double [20];
				
				u2[2] = Math.sin(u);
				l2[2] = -l;

				for (int m = 2; m <= k; m++) {
					i2[m] = (l2[m] - r[m])*u2[m] / r[m];
					i2[m + 1] = i2[m] * n[m] / n[m + 1];
					u2[m + 1] = u2[m] + i2[m] - i2[m + 1];
					l2[m + 1] = i2[m + 1] * r[m] / u2[m + 1] + r[m];
					h2[m] = l2[m + 1] * u2[m + 1];
					h2[m + 1] = h2[m] - d[m] * u2[m + 1];
					if (m<k) l2[m + 1] = l2[m + 1] - d[m];
					else l2[m + 1] = l2[m + 1];
				}
				return l2[k + 1];
			}								
		});
		shell.open();
		while(!shell.isDisposed()){
			if(!display.readAndDispatch())
				display.sleep();
		}
		
		display.dispose();
	}

}
