#include <curses.h>
#include <stdio.h>
#include <signal.h>
#include <sys/time.h>
#include <stdlib.h>

#define wall '@'
#define top 22
#define startX 4

//��1��
int LL[4][4][2] = {
	{{1, 0}, {1, -1}, {1, -2}, {0, -2}},
	{{0, -1}, {1, -1}, {2, -1}, {2, -2}},
	{{0, 0}, {1, 0}, {0, -1}, {0, -2}},
	{{0, -2}, {1, -2}, {2, -2}, {0, -1}}
};

//��2��
int LR[4][4][2] = {
	{{0, 0}, {0, -1}, {0, -2}, {1, -2}},
	{{0, -2}, {1, -2}, {2, -2}, {2, -1}},
	{{0 ,0}, {1, 0}, {1, -1}, {1, -2}},
	{{0, -1}, {1, -1}, {2, -1}, {0, -2}}
};

//��3��
int T[4][4][2] = {
	{{0, -1}, {1, -1}, {2, -1}, {1, 0}},
	{{1, 0}, {0, -1}, {1, -1}, {1, -2}},
	{{0 ,0}, {1, 0}, {2, 0}, {1, -1}},
	{{0 ,0}, {0, -1}, {0, -2}, {1, -1}}
};

//��4��
int O[4][4][2] = {
	{{0 ,0}, {1, 0}, {0, -1}, {1, -1}},
	{{0 ,0}, {1, 0}, {0, -1}, {1, -1}},
	{{0 ,0}, {1, 0}, {0, -1}, {1, -1}},
	{{0 ,0}, {1, 0}, {0, -1}, {1, -1}}
};

//��5��
int ZL[4][4][2] = {
	{{1, 0}, {2, 0}, {0, -1}, {1, -1}},
	{{0 ,1}, {0, 0}, {1, 0}, {1, -1}},
	{{1, 0}, {2, 0}, {0, -1}, {1, -1}},
	{{0 ,1}, {0, 0}, {1, 0}, {1, -1}}
};

//��6��
int ZR[4][4][2] = {
	{{0 ,0}, {1, 0}, {1, -1}, {2, -1}},
	{{1 ,1}, {0 ,0}, {1, 0}, {0, -1}},
	{{0 ,0}, {1, 0}, {1, -1}, {2, -1}},
	{{1 ,1}, {0 ,0}, {1, 0}, {0, -1}},
};

//��7��
int I[4][4][2] = {
	{{0 ,0}, {1, 0}, {2, 0}, {3, 0}},
	{{1, 0}, {1, 1}, {1, 2}, {1, 3}},
	{{0 ,0}, {1, 0}, {2, 0}, {3, 0}},
	{{1, 0}, {1, 1}, {1, 2}, {1, 3}}
};

int color[] = {COLOR_BLACK, COLOR_RED, COLOR_CYAN, COLOR_GREEN, 
				COLOR_WHITE, COLOR_BLUE, COLOR_YELLOW, COLOR_MAGENTA};

int map[18][32] = {0};   //��ɫ��ͼ
int _mp[12][32];         //��־λ���Ƿ�Ϊ��     
int cur_mode[4][4][2];   //��ǰ����ģ��
int next_mode[4][4][2];  //next����ģ��

int score = 0;          // �÷�
int pause_flag = 0;     //��ͣ��־
int sleep_time = 350;   //��ʱ���
int cur_speed = 2;      //��ǰ�½��ٶ�
int dir_x[3] = {0, -1, 1};
int dir_y[3] = {-1, 0, 0};
int cur_stone = 1;      //��ǰ����
int cur_pd = 0;         //��ǰ����ĳ���
int next_stone = 6;     //next����   
int next_pd = 2;        //next����ĳ���

//��ǰ�������ʼ����
int cur_x = startX;
int cur_y = top;

//��ʾ��һ���������ʼ����
int next_x = 12; 
int next_y = 17;

int touch_flag = 0; //�Ƿ����
int row = 1;
int rise_time = 1;

void get_brick_mode(int stone, int mode[4][4][2]);
void init_map();

void draw_rect(int x, int y, int fg, int bg)
{
	int flag = bg + 1;
	init_pair(flag, color[fg], color[bg]);
	attron(COLOR_PAIR(flag));
	move(x, y); 
	addstr("[]");
	attroff(COLOR_PAIR(flag));
	move(LINES - 1, COLS - 1);
}

void rand_stone_and_placedir()
{
	next_stone = rand() % 7 + 1;
	next_pd = rand()% 3;	
}

//չʾ��һ����״����
void display_next_brick(int c)
{
	int i, x, y;
	for(i = 0; i < 4; i ++)    
	{	
		x = next_x + next_mode[next_pd][i][0];
		y = next_y + next_mode[next_pd][i][1];	
		map[x][y] = c;
	}
	
}

//����Ļչʾ
void display()
{
	clear();
	keypad(stdscr, TRUE);
	set_ticker(sleep_time);
	
	display_next_brick(next_stone);
	
	int i, j, k, bg;
	for(i = 20; i < 54; i += 2)
	{
		if(i == 40)
			continue;
		for(j = 0; j < 20; j ++)
		{
			int x = 1 + (i - 20) / 2;
			int y = 20 - j ;
			int c = map[x][y];
			if(c == -3){
				draw_rect(j, i, 3, 0);
				move(1,1);
				printw("S");
			}
			else
				draw_rect(j, i, 0, c);
		}
	}

	for(i = 0; i <= 20; i ++)
	{
		mvaddch(i, 19, wall);
		mvaddch(i, 40, wall);
	}
	for(i = 19; i <= 40; i ++)
		mvaddch(20, i, wall);
	

	move(8, 43);
	printw("score: %d", score);
	move(9, 43);
	printw("cur_speed: %d", cur_speed);
	
	//init_pair(8, color[3], color[0]);
	//attron(COLOR_PAIR(8));

	int loc = 12;
	move(loc, 43);
	printw("Rules");
	move(loc + 1, 43);
	printw("1. space -> pause");
	move(loc + 2, 43);
	printw("2. 'UP' -> change shape");
	move(loc + 3, 43);
	printw("3. 'LEFT' -> move to left");
	move(loc + 4, 43);
	printw("4. 'RIGHT' -> move to right");
	move(loc + 5, 43);
	printw("5. '+,-' -> speed up/down");
	move(loc + 6, 43);
	printw("6. 'r' -> restart game");
	move(loc + 7, 43);
	printw("7. 'q' -> quit game");

	//attroff(COLOR_PAIR(8));
	
	move(LINES - 1, COLS - 1);
	refresh();
}

//��ʱ��
int set_ticker( int n_msecs )
{
    struct itimerval new_timeset;
    long n_sec, n_usecs;

    n_sec = n_msecs / 1000 ;		/* int part	*/
    n_usecs = ( n_msecs % 1000 ) * 1000L ;	/* remainder	*/

    new_timeset.it_interval.tv_sec = n_sec;        /* set reload       */
    new_timeset.it_interval.tv_usec = n_usecs;      /* new ticker value */
    new_timeset.it_value.tv_sec = n_sec  ;      /* store this       */
    new_timeset.it_value.tv_usec = n_usecs ;     /* and this         */

	return setitimer(ITIMER_REAL, &new_timeset, NULL);
}

//�������������ɫ
void paint_brick(int startx, int starty, int bg, int pd)    
{
	int i, x, y, m, n;
	for(i = 0; i < 4; i++)
	{
		x = startx + cur_mode[pd][i][0];
		y = starty + cur_mode[pd][i][1];
		map[x][y] = bg;	
		m = 20 -y;
		n = 2 * x + 18;
		draw_rect(m, n, 0, bg);
	}
}


//�������ƶ�
void Move(int dir)
{
	int stone = cur_stone;
	int pd = cur_pd;
	int m = cur_x;
	int n = cur_y;
	m += dir_x[dir];
	n += dir_y[dir];
	
	if(m == 0 && stone != 7)  //ײ����ǽ
		return ;
	if(stone == 7)    //���������������Ϊ7ʱ
	{
		if(pd % 2 == 0 && m == 0)
			return ;
		if(pd % 2 && m == -1)
			return ;
	}
	int i, x, y, s, t;
	for(i = 0; i < 4; i++)      //ײ����ǽ
	{
		x = m + cur_mode[pd][i][0];
		if(x == 11)
			return ;
	}
	for(i = 0; i < 4; i++)      //ײ����������
	{
		x = m + cur_mode[pd][i][0];
		y = n + cur_mode[pd][i][1];
		if(_mp[x][y] == -1)
			return ;
	}
	
	//������ԭ���ĵط���Ϊ����ɫ
	paint_brick(cur_x, cur_y, 0, pd);

	cur_x = m;
	cur_y = n;

	//�������ƶ�����λ��
	paint_brick(cur_x, cur_y, stone, pd);
}

//�ı���״
void change_shape()
{

	int stone = cur_stone;
	int pre_x = cur_x;
	int pre_y = cur_y;
	int pd = cur_pd;
	int pd1 = (pd + 1) % 4;
	int flag_1 = 0;
	int flag_2 = 0;
	
	int i, x, y, m, n;
	for(i = 0; i < 4; i++)    //�������ε���λ���Ƿ�ռ����ǽ
	{	
		x = cur_x + cur_mode[pd1][i][0];
		y = cur_y + cur_mode[pd1][i][1];	
		if(_mp[x][y] == -1)
			flag_1 = 1;
		if(_mp[x][y] == -2)
			flag_2 = 1;
	}
	if(flag_1)
		return ;
	if(flag_2)
	{
		int sign = -1;              //����ǽ
		if(cur_x == 0) sign = 1;    //����ǽ
		x = cur_x + sign * 1;
		if(stone == 7 && sign == -1)  //�����������Ϊ7�ҿ���ǽʱ
			x = x +  sign * 1;;
		for(i = 0; i < 4; i++)    //�������ε���λ���Ƿ�ռ
		{	
			int m = x + cur_mode[pd1][i][0];
			int n = cur_y + cur_mode[pd1][i][1];
			if(_mp[m][n] < 0)
				return ;
		}
		cur_x = x;	
	}
	//������ԭ���ĵط���Ϊ����ɫ
	paint_brick(pre_x, pre_y, 0, pd);

	cur_pd = pd1;

	//������任����λ��	
	paint_brick(cur_x, cur_y, stone, pd1);
}

void reset_game()
{
	score = 0;
	touch_flag = 0;
	cur_x = startX;
	cur_y = top;
	sleep_time = 350;
	cur_speed = 2;
	init_map();
	display();
}


//���̼����¼�
void keylistener()
{
	while(1){
		int key = getch();
		switch(key)
		{
		case KEY_UP:
			change_shape();
			break;
		case KEY_DOWN:
			Move(0);
			break;
		case KEY_LEFT:
			Move(1);
			break;
		case KEY_RIGHT:
			Move(2);
			break;
		case 'r':
			reset_game();
			break;
		case '+':       //����
			sleep_time -= 50;
			cur_speed ++;
			if(sleep_time <= 250)
			{
				sleep_time = 250;
				cur_speed = 4;
			}
			set_ticker(sleep_time);
			display();
			break;
		case '-':       //����
			sleep_time += 50;
			cur_speed --;
			if(sleep_time >= 400)
			{
				sleep_time = 400;
				cur_speed = 1;
			}
			set_ticker(sleep_time);
			display();
			break;
		case ' ':       //��ͣ
			pause_flag = !pause_flag;
			break;
		case 'q':       //�˳�
			return ;
			break;
		default:
			break;
		}
	}
}

void get_mode(int mode[4][4][2], int x[4][4][2])
{
	int i, j, k;
	for(i = 0; i < 4; i++)
		for(j = 0; j < 4; j++)
			for(k = 0; k < 2; k ++)
				mode[i][j][k] = x[i][j][k];
}

//��ȡ��״ģ��
void get_brick_mode(int stone, int mode[4][4][2])
{
	switch(stone)
	{
	case 1:
		get_mode(mode, LL);
		break;
	case 2:
		get_mode(mode, LR);
		break;
	case 3:
		get_mode(mode, T);
		break;
	case 4:
		get_mode(mode, O);
		break;
	case 5:
		get_mode(mode, ZL);
		break;
	case 6:
		get_mode(mode, ZR);
		break;
	case 7:
		get_mode(mode, I);
		break;
	}
}

//�ж��Ƿ�����
bool is_drop()
{	
	int pd = cur_pd;
	int i, x, y;
	for(i = 0; i < 4; i ++)
	{
		x = cur_x + cur_mode[pd][i][0];
		y = cur_y + cur_mode[pd][i][1];
		if(_mp[x][y - 1] == -1 ) // ������������
			return true;
	}
	return false;
}

//�Ƿ񴥶�
bool is_touch_top()
{
	int i;
	for(i = 1; i < 11; i ++)
		if(map[i][21] != 0 || map[i][20] != 0)
			return true;
	return false;
}

//�Ƿ������ȥ����
void is_change()
{

	int r = row;
	int i, j;
	for(j = row; j < 22; j++)
	{
		int flag = 1;
		for(i = 1; i < 11; i ++ )
			if(map[i][j] == 0)
				flag = 0;
		if(!flag)
		{
			for(i = 1; i < 11; i ++ )
			{
				map[i][r] = map[i][j];
				_mp[i][r] = _mp[i][j];
			}
			r ++;
		}
	}

	score += (22 - row - r);
	for(j = r; j < 22; j++)
	{
		for(i = 1; i < 11; i ++ )
		{
			map[i][j] = 0;
			_mp[i][j] = 0;
		}
	}
}

//��غ����²����µķ�����Ϸ�����
void after_drop()
{
	display_next_brick(0);
	
	cur_stone = next_stone;
	cur_pd = next_pd;
	
	get_mode(cur_mode, next_mode);
		
	rand_stone_and_placedir();
	get_brick_mode(next_stone, next_mode);
	
	cur_x = startX;
 	cur_y = top;
	
	display();
}

void rise_up()
{
	rise_time ++;
	
	if(rise_time % 30)
		return ;
	
	int i, j, m, n, k;
	for(j = 21; j > row; j --)
		for(i = 1; i < 11; i ++ )
		{
			map[i][j] = map[i][j - 1];
			_mp[i][j] = _mp[i][j - 1];
		}
	for(i = 1; i < 11; i ++ )
	{
		map[i][row] = -3;
		_mp[i][row] = -1;
	}
	for(k = 0; k < 4; k++)
	{
		m = cur_x + cur_mode[cur_pd][k][0];
		n = cur_y + cur_mode[cur_pd][k][1];	
		map[m][n] = cur_stone;
		map[m][n + 1] = 0;
	}
	row ++;
	display();
}

void OnTimer(int signum)
{
	int i, x, y, m, n, k;
	int pd = cur_pd;
	if(!touch_flag && !pause_flag)
	{
		rise_up();
		if(is_drop())  //�ŵ�
		{
			//�ڵ�ͼ�ϱ��λ�ñ�ռ
			for(k = 0; k < 4; k++)
			{
				m = cur_x + cur_mode[pd][k][0];
				n = cur_y + cur_mode[pd][k][1];	
				_mp[m][n] = -1;
			}
			is_change();           //��ȥ�������
			if(is_touch_top())     //�Ƿ�ﵽ���ˣ���Ϸ����
			{
				touch_flag = 1;
				move(8, 5);
				printw("GAMEOVER!!!");
				move(LINES - 1, COLS - 1);
				refresh();
				return ;
			}
			after_drop();	//��غ����²����µķ�����Ϸ�����
			return ;
		}
		//������ԭ���ĵط���Ϊ����ɫ
		paint_brick(cur_x, cur_y, 0, cur_pd);

		cur_y --;
		//�������ƶ�����λ��
		paint_brick(cur_x, cur_y, cur_stone, cur_pd);
	}
	refresh();
}


void init_map()
{
	int i, j;
	for(i = 0; i < 18; i ++)
		for(j = 0; j < 32; j ++)
			map[i][j] = 0;
	for(i = 0; i < 12; i ++)
	{
		for(j = 0; j < 32; j ++)
		{
			if(i == 0 || i == 11)
				map[i][j] = -2;
			if(j == 0)
				map[i][j] = -1;
		}
	}
	for(i = 0; i < 12; i++)
		for(j = 0; j < 30; j++)
			_mp[i][j] = map[i][j];
	
	rand_stone_and_placedir();
	get_brick_mode(cur_stone, cur_mode);
	get_brick_mode(next_stone, next_mode);
}


int main()
{
	init_map();
	srand((unsigned)time(NULL));
	
	initscr();
	noecho();
	crmode();
	start_color();
	
	display();
	
	signal(SIGALRM, OnTimer);
	set_ticker(sleep_time);
	
	keylistener();
	endwin();
	return 0;
}