package box;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import javax.swing.*;

public class pushbox extends JFrame implements KeyListener,ActionListener{
	map map_level=new map(1);    //首次获取地图
	int[][] map=new int[15][15];  //地图上每个位置所对应的图片
	int[][] map_restart=new int[15][15];  //备用图片
	static int manX,manY;                //人所处的位置
	                          
	static int level=1;                    //所处关卡
	static int step=0;
	Image[] picture;                       //地图上的所有图片
	ImageIcon[] image=new ImageIcon[10];        
	Stack<Integer> back_stack = new Stack<Integer>();                       //存储所前行的路径
	JButton next,previous,back,restart,exit;    //按钮：下一关，上一关，重新开始，退出   
	String current_level="第"+level+"关";    //当前所处关卡（右上角显示文本）
	String[] level_button=new String[13];   //复选框的内容
	JComboBox selectBox;                     //选关复选框
	
	final int Up = 0;
	final int Down = 1;
	final int Left = 2;
	final int Right = 3;
	
	int[] dir_x = {-1, 1, 0, 0};
	int[] dir_y = {0, 0, -1, 1};
	
	public pushbox(){
		super("推箱子");
		
		getmap(map);
		

		previous=new JButton("上一关"); 
		next=new JButton("下一关");
		restart=new JButton("重新开始");
		back=new JButton("后退");
		exit=new JButton("退出游戏");
		for(int i=0;i<13;i++)
			level_button[i]="第"+Integer.toString(i+1)+"关";
		
		selectBox=new JComboBox(level_button);
		
		
		FlowLayout layout=new FlowLayout();   //布局模式
		Container c=getContentPane();
		c.setLayout(layout);
		layout.setAlignment(FlowLayout.CENTER);   //控件在布局中的位置

		c.setBackground(Color.CYAN);         //背景色
		c.add(previous); 
		c.add(next);
		c.add(back);
		c.add(restart);
		c.add(selectBox);
		c.add(exit);
		
		previous.addActionListener((ActionListener) this);
		previous.addKeyListener(this);
		next.addActionListener((ActionListener) this);
		next.addKeyListener(this);
		back.addActionListener(this);
		back.addKeyListener(this);
		restart.addActionListener((ActionListener) this);
		restart.addKeyListener(this);
		exit.addActionListener((ActionListener) this);
		exit.addKeyListener(this);
		selectBox.addItemListener(                   //监听复选框点击事件
				new ItemListener(){
					public void itemStateChanged(ItemEvent e){
						if(e.getStateChange()==e.SELECTED){
							step=0;
							back_stack.clear();            //每次重新选关后都要清空路径栈
							level=selectBox.getSelectedIndex()+1;   //获取所选的关卡
							map_level=new map(level);             //重新获取对应关卡的地图
							getmap(map);                     
							current_level="第"+level+"关";         //右上角提示所处关卡文本的更新
							repaint();                           //重画地图
							requestFocus();                      //重新获取焦点（因为按钮事件后键盘事件不起作用）
						}
					}
				}
		);
		
		for(int k=0;k<10;k++){
			image[k]=new ImageIcon("picture/"+k+".png");             //获取图片
		}
		picture=new Image[]{
				image[0].getImage(),
				image[1].getImage(),
				image[2].getImage(),
				image[3].getImage(),
				image[4].getImage(),
				image[5].getImage(),
				image[6].getImage(),
				image[7].getImage(),
				image[8].getImage(),
				image[9].getImage()
		};
		this.setFocusable(true);
		this.addKeyListener((KeyListener)this);
		setSize(640,640);
		setVisible(true);
	}
	//判断是否通关（方法是，检查地图中是否还有箱子编号4）
	public Boolean pass(){
		int p=0;
		for(int i=0;i<15;i++)
			for(int j=0;j<15;j++){
				if(map[i][j]==4) p++;
			}
		if(p==0) return true;
		else return false;
	}
	//获取地图上每个位置所对应的图片编号
	public void getmap(int[][] map){
		for(int i=0;i<15;i++)
			for(int j=0;j<15;j++)
				map[i][j]=map_level.map[i][j];
		manX=map_level.manX;
		manY=map_level.manY;
	}
    
	public void paint(Graphics g){
		super.paint(g);
		//提示信息
		back_stack.push(Down + 5);   //这里是一个细节，先将“DOWN”存入栈内
		g.drawString(current_level,500,90);
		g.drawString("第"+step+"步",400,90);
		g.drawString("用键盘上的R重新开始，Q退出游戏",200,565);
		g.drawString("用键盘pageup,pagedown切换关卡",200,580);
		g.drawString("用键盘上的上下左右键控制人的移动",200,600);
		g.drawString("用键盘上的Backspace键后退",200,620);
        //在面板上画地图
		for (int i = 0; i < 15; i++)
			for (int j = 0; j < 15; j++)
				g.drawImage(picture[map[i][j]],100+30*j,100+30*i, this);
	}
	public void Step(Graphics g){
		g.setColor(Color.cyan);
		g.drawString("第"+step+"步",400,90);
		step++;
		g.setColor(Color.BLACK);
		g.drawString("第"+step+"步",400,90);
	}
	
	public void move(int dir, Graphics g){
		int man = dir + 5;	
		int dx = dir_x[dir];
		int dy = dir_y[dir];	
		
		int x = manX;
		int y = manY;
		manX += dx;
		manY += dy;
		
		int origin_picture = map[manX][manY];
		int next_picture = map[manX + dx][manY + dy];
		
		if((map[manX][manY]==4||map[manX][manY]==3)&&(map[manX+dx][manY+dy]==2||map[manX+dx][manY+dy]==4||map[manX+dx][manY+dy]==3))   //上面是箱子，箱子上面是墙或箱子
		{	
			manX -= dx;
			manY -= dy;
			return ;
		}
		if(map[manX][manY]==2)     //上面是墙
		{	
			manX -= dx;
			manY -= dy;
			return ;
		}
		
		if(map[manX][manY]==1||map[manX][manY]==9){                         //人上面是草地或是目的地
			g.drawImage(picture[man],100+30*manY,manX*30+100,this);        //人的朝向变化
			if(map[x][y]==9)                                   //原来位置是目的地
				g.drawImage(picture[9],100+30*y,100+30*x,this);
			else{                                               //原来位置是草地
				g.drawImage(picture[1],100+30*y,x*30+100,this);
				map[x][y]=1;
			}
			Step(g);
		}
		
		
		if((map[manX][manY]==4||map[manX][manY]==3)&&map[manX+dx][manY+dy]==1){        //上面是箱子，箱子上面是草地
			g.drawImage(picture[man],100+30*manY,manX*30+100,this);        //人的朝向变化
			if(map[manX][manY]==3)                                      //若是移动一大目的地的箱子，原箱子位置值为9
				map[manX][manY]=9;
			if(map[x][y]==9)                                   //原来位置是目的地
				g.drawImage(picture[9],100+30*y,100+30*x,this);
			else{                                               //原来位置是草地
				g.drawImage(picture[1],100+30*y,x*30+100,this);
				map[x][y]=1;
			}
			g.drawImage(picture[4],100+30*(manY+dy),(manX+dx)*30+100,this);    //草地位置变箱子
			map[manX+dx][manY+dy]=4;
			Step(g);
		}
		if((map[manX][manY]==4||map[manX][manY]==3)&&map[manX+dx][manY+dy]==9){                 //上面是箱子，箱子上面是目的地
			g.drawImage(picture[man],100+30*manY,manX*30+100,this);        //人的朝向变化
			if(map[manX][manY]==3)                                      //若是移动一大目的地的箱子，原箱子位置值为9
				map[manX][manY]=9;
			else map[manX][manY]=0;
			if(map[x][y]==9)                                   //原来位置是目的地
				g.drawImage(picture[9],100+30*y,100+30*x,this);
			else {                                              //原来位置是草地
				g.drawImage(picture[1],100+30*y,x*30+100,this);
				map[x][y]=1;
			}
			g.drawImage(picture[3],100+30*(manY+dy),(manX+dx)*30+100,this);    //目的地位置变箱子
			
			map[manX+dx][manY+dy]=3;
			Step(g);
		}
		
		back_stack.push(next_picture);
		back_stack.push(origin_picture);
		back_stack.push(manY);
		back_stack.push(manX);
		back_stack.push(man);
		
		if(pass())
			JOptionPane.showMessageDialog(null, "恭喜你过关了！");
	}
	//键盘事件
	public void keyPressed(KeyEvent e) {
		Graphics g=getGraphics();
		int dir ;
		switch(e.getKeyCode()){
		case KeyEvent.VK_UP:
			dir = Up;		
			move(dir, g);    
			break;
		case KeyEvent.VK_DOWN :
			dir = Down;	
			move(dir, g);    
			break;
		case KeyEvent.VK_LEFT:
			dir = Left;	
			move(dir, g);
			break;
		case KeyEvent.VK_RIGHT:
			dir = Right;	
			move(dir, g); 
			break;
		case KeyEvent.VK_BACK_SPACE:
			back.doClick();     
			break;
		case KeyEvent.VK_PAGE_DOWN :
			next.doClick();     
			break;
		case KeyEvent.VK_PAGE_UP:
			previous.doClick(); 
			break;
		case KeyEvent.VK_R:
			restart.doClick();  
			break;
		case KeyEvent.VK_Q :
			exit.doClick();     
			break;
		}
	}

	public void keyReleased(KeyEvent e) {}

	public void keyTyped(KeyEvent e) {}
	//悔步后退
	public void step_back(Graphics g){
		g.setColor(Color.cyan);
		g.drawString("第"+step+"步",400,90);
		step--;
		g.setColor(Color.BLACK);
		g.drawString("第"+step+"步",400,90);
	}

	public void Back(){
		
		if(back_stack.size()==2) 
			return;
		int dir=back_stack.pop() - 5;  //获取上一步的方向
		

		int s = back_stack.pop();
		int t = back_stack.pop();
		int origin_picture = back_stack.pop();
		int netx_picture = back_stack.pop();
		int pre_dir = back_stack.lastElement();
		
		Graphics g=getGraphics();
		
		int dx = dir_x[dir];
		int dy = dir_y[dir];
		g.drawImage(picture[origin_picture],100+30*t,100+30*s,this);
		g.drawImage(picture[netx_picture],100+30*(t+dy),100+30*(s+dx),this);
	    g.drawImage(picture[pre_dir],100+30*(t-dy),100+30*(s-dx),this);
	    manX = s - dx;
	    manY = t - dy;
	    map[s][t] = origin_picture;
	    map[s+dx][t+dy] = netx_picture;
	    map[s-dx][t-dy] = pre_dir;          
	    step_back(g);
		
	}
	//按钮事件监听
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==restart){  //重新开始
			back_stack.clear();       //清空路径栈
			step=0;
			getmap(map);
			repaint();
		}
		else if(e.getSource()==exit)       //退出游戏
			this.setVisible(false);
		else if(e.getSource()==next){     //下一关
			back_stack.clear();
			step=0;
			level++;
			if(level!=14){
				map_level=new map(level);
				getmap(map);
				current_level="第"+level+"关";
				repaint();
			}
		}
		else if(e.getSource()==previous){    //上一关
			back_stack.clear();
			step=0;
			level--;
			if(level!=0){
				map_level=new map(level);
				getmap(map);
				current_level="第"+level+"关";
				repaint();
			}
		}
		else if(e.getSource()==back)
			Back();
		 requestFocus();
	}

	
}
