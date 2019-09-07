package box;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Game extends JFrame implements ActionListener{
	Image image[];
	ImageIcon icon;
	JButton start,tip,exit;
	Container c;
	public Game(){
		super("推箱子");
		c=getContentPane();
		FlowLayout layout=new FlowLayout();
		c.setLayout(layout);
		layout.setAlignment(FlowLayout.CENTER);
		icon=new ImageIcon("picture/game.png");
		image=new Image[]{icon.getImage()};
		start=new JButton("开始游戏");
		exit=new JButton("退出游戏");
		tip=new JButton("游戏说明");
		c.add(start);
		c.add(tip);
		c.add(exit);
		start.addActionListener(this);
		tip.addActionListener(this);
		exit.addActionListener(this);
		
		setSize(640,480);
		setVisible(true);
	}
	public void paint(Graphics g){
		super.paint(g);
		g.drawImage(image[0],10,65,this);


	}
	public static void main(String args[]){
		Game g=new Game();
		g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==exit)
			this.setVisible(false);
		if(e.getSource()==start){
			this.setVisible(false);
			pushbox c=new pushbox();
		}
		if(e.getSource()==tip){
			String t="本游戏分为13关，难度逐关递增，可以自己选关";
			JOptionPane.showMessageDialog(null, t);
		}
		
	}
}
