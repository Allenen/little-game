package snake;


import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;

import javax.swing.*;

import java.util.*;

public class greedsnake extends JFrame implements KeyListener,ActionListener{
	
	static int ik=0,jk=0,flag=0;
	Boolean running=true;
	String direction="DOWN";
	String pre_dir="DOWN";
	snakemove snake;
	int level=1;
	JButton restart,pause,speed_add,speed_sub;
	JComboBox mode; 
	String modes[]={"挑战模式","普通模式"};
	static int sleeptime=250,speed=250;
	
	
	public greedsnake(){
		super("贪吃蛇");	
		Container c=getContentPane();
		FlowLayout layout=new FlowLayout();
		layout.setAlignment(FlowLayout.CENTER);
		c.setLayout(layout);
		c.setBackground(Color.cyan);
		restart=new JButton("重新开始");
		pause=new JButton("暂停");
		speed_add=new JButton("加速");
		speed_sub=new JButton("减速");
		mode=new JComboBox(modes);
		restart.addActionListener(this);
		pause.addActionListener(this);
		speed_add.addActionListener(this);
		speed_sub.addActionListener(this);
		c.add(restart);
		c.add(pause);
		c.add(speed_add);
		c.add(speed_sub);
		c.add(mode);
		//游戏模式的选择
		mode.addItemListener(
				new ItemListener(){
					public void itemStateChanged(ItemEvent e){
						if(e.getStateChange()==e.SELECTED){
							level=1+mode.getSelectedIndex();
							running=true;
							direction="DOWN";
							pre_dir="DOWN";
							repaint();
							requestFocus();
						}
					}
				}
				);
		
		this.setFocusable(true);
		this.addKeyListener(this);
		setSize(500,600);
		setVisible(true);
	}
	//贪吃蛇身体和食物节点
	class node{
		int m,n;
		node(int m,int n){
			this.m=m;
			this.n=n;
		}
	}
	
	
	
	public void paint(Graphics g){
		ik++;
		if(ik==2) return;
		super.paint(g);
		g.drawString("可以通过键盘上的 ' r ' 重新开始游戏 ，空格键实现暂停",100,510);
		g.drawString("可以通过键盘上的 ' + ' , ' - ' 实现加速减速",100,530);
		g.drawString("当前速度：", 10,100);
		g.drawString(" "+speed, 30, 120);
		snake=new snakemove();
		snake.start();
	}
	
	

	@Override
	public void actionPerformed(ActionEvent e) {
		Graphics g=getGraphics();
		switch(e.getActionCommand()){
		case "重新开始"  :
			running=true;
			sleeptime=250;
			g.setColor(Color.CYAN);
			g.drawString(" "+speed, 30, 120);
			speed=250;
			g.setColor(Color.BLACK);
			g.drawString(" "+speed, 30, 120);
			direction="DOWN";
			pre_dir="restart";
			paint(g);
			break;
		case "暂停" :
			direction="STOP";
			break;
		case "减速" :
			sleeptime+=10;
			speed-=10;
			g.setColor(Color.CYAN);
			g.drawString(" "+(speed+10), 30, 120);
			g.setColor(Color.BLACK);
			g.drawString(" "+speed, 30, 120);
			break;
		case "加速" :
			sleeptime-=10;
			speed+=10;
			g.setColor(Color.CYAN);
			g.drawString(" "+(speed-10), 30, 120);
			g.setColor(Color.BLACK);
			g.drawString(" "+speed, 30, 120);
			break;
		}
		
		requestFocus();
	}
	
	public class snakemove extends Thread{
		
		map m=new map(level);  //加载地图
		int[][] map=new int[20][20];
		Image[] picture;
		ImageIcon[] image=new ImageIcon[8];
		int snakeX,snakeY;
		int foodX,foodY;
		int x,y;
		ImageObserver imo;
		Graphics g=getGraphics();
		LinkedList l=new LinkedList();  //蛇身链表
		
		public snakemove(){
			
			getmap(map);
			for(int k=0;k<8;k++)
				image[k]=new ImageIcon("pic/"+k+".png");
			picture=new Image[]{
					image[0].getImage(),
					image[1].getImage(),
					image[2].getImage(),
					image[3].getImage(),
					image[4].getImage(),
					image[5].getImage(),
					image[6].getImage(),
					image[7].getImage()
			};
			node n1=new node(snakeX-1,snakeY);
			node n2=new node(snakeX-2,snakeY);
			l.addFirst(n2);
			l.addFirst(n1);
			for (int i = 0; i < 20; i++)
				for (int j = 0; j < 20; j++)
					g.drawImage(picture[map[i][j]],70+20*j,70+20*i, imo);

		}
		//随机产生食物
		public void food(Graphics g){
			Random r=new Random();
			foodX=r.nextInt(15)+2;
			foodY=r.nextInt(15)+2;
			Iterator it=l.iterator();
			while(it.hasNext()){ 
				node body=(node)it.next();
				if(foodX==body.m&&foodY==body.n){
					foodX=r.nextInt(14)+2;
					foodY=r.nextInt(13)+3;
				}
			}
			map[foodX][foodY]=5;
			g.drawImage(picture[5],foodY*20+70,foodX*20+70,imo);
		}
		public void getmap(int[][] map){
			for(int i=0;i<20;i++)
				for(int j=0;j<20;j++)
					map[i][j]=m.map[i][j];
			snakeX=m.snakeX;
			snakeY=m.snakeY;
			foodX=m.foodX;
			foodY=m.foodY;
			
		}
		//判断是否过关
		public Boolean pass(){
			int p=0;
			for(int i=0;i<20;i++)
				for(int j=0;j<20;j++){
					if(map[i][j]==5) p++;
				}
			if(p==0) return true;
			else return false;
		}
		public void move(int level){
			x=snakeX;
			y=snakeY;
			switch(direction){
			case "UP" :snakeX-=1;
			           break;
			case "DOWN" :snakeX+=1;
			             break;
			case "LEFT" : snakeY-=1;
			              break;
			case "RIGHT" :snakeY+=1;
			              break;
			case "STOP"  :break;
			case "restart":l.clear();
			default:break;
			}
			
			if(map[snakeX][snakeY]==6||map[snakeX][snakeY]==7||map[snakeX][snakeY]==2){				
				running=false;
			    JOptionPane.showMessageDialog(null, "撞墙，游戏结束！！");
				
			}
			if(map[snakeX][snakeY]==5){
				node f=new node(x,y);
				l.addFirst(f);
				g.drawImage(picture[4],y*20+70,x*20+70,imo);
				g.drawImage(picture[3],snakeY*20+70,snakeX*20+70,imo);
				map[snakeX][snakeY]=1;
				if(level==2) {
					food(g);
				}
			}
			else{
				if(running==true&&direction!="STOP"){
					Iterator it=l.iterator();
					while(it.hasNext()){ 
						node body=(node)it.next();
						if(snakeX==body.m&&snakeY==body.n){
							if((pre_dir=="DOWN"&&direction=="UP")||(pre_dir=="UP"&&direction=="DOWN")
									||(pre_dir=="LEFT"&&direction=="RIGHT")||(pre_dir=="RIGHT"&&direction=="LEFT")){
								direction=pre_dir;
								snakeX=x;
								snakeY=y;
							}
							else{
								running=false;
						    	JOptionPane.showMessageDialog(null, "蛇头与身体相撞，游戏结束！！");
							}
						}
					}
					node last=(node)l.getLast();
	    			g.drawImage(picture[1],last.n*20+70,last.m*20+70,imo);
		    		g.drawImage(picture[4],y*20+70,x*20+70,imo);
		       		g.drawImage(picture[3],snakeY*20+70,snakeX*20+70,imo);		
				
				
	      			l.removeLast();
		    		node f=new node(x,y);
				
		    		l.addFirst(f);
				}
				if(pass()&&level==1){
					running=false;
					JOptionPane.showMessageDialog(null, "游戏结束,恭喜过关！！");
				}
					
				
			}
			pre_dir=direction;
		}
		public void run(){
			Graphics g=getGraphics();
			
			
			while(running){
				try{
					Thread.sleep(sleeptime);
				}
				catch(Exception e){break;}
				move(level);
			}
		}
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		Graphics g=getGraphics();
		switch(e.getKeyCode()){
		case KeyEvent.VK_UP :
			direction="UP";
			break;
		case KeyEvent.VK_DOWN :
			direction="DOWN";
			break;
		case KeyEvent.VK_LEFT :
			direction="LEFT";
			break;
		case KeyEvent.VK_RIGHT :
			direction="RIGHT";
			break;
		case KeyEvent.VK_R  :
			restart.doClick();
			break;
		case KeyEvent.VK_SPACE :
			pause.doClick();
			break;
		case KeyEvent.VK_SUBTRACT :
			speed_sub.doClick();
			break;
		case KeyEvent.VK_ADD :
			speed_add.doClick();
			break;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}
	
	public static void main(String args[]){
		greedsnake s=new greedsnake();
		s.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
