package box;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;//读取字符文件类FileReader
import java.io.IOException;


public class map {
	int[][] map=new int[20][20];
	int manX,manY;
	
	public map(int level){
		String filepath="mapc/"+level+".txt";
		File file = new File(filepath);
		FileReader fr = null;//利用FileReader流来读取一个文件中的数据
		BufferedReader br = null;//字符读到缓存里
		
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			for (int i = 0; i < 15; i++){
				String line = br.readLine();//以行为单位，一次读一行 利用BufferedReader的readLine，读取分行文本
				byte[] point = line.getBytes();//将字符串转换为字节数组
				for (int j = 0; j < 15; j++) {
					map[i][j] = point[j] - 48;// 根据ASCall码表要减掉30H（十进制的48）
					if (map[i][j] == 5 || map[i][j] == 6 || map[i][j] == 7|| map[i][j] == 8){
						manX = i;
						manY = j;
					}
				}
			}
		} 
		catch (FileNotFoundException e){
			e.printStackTrace();//深层次的输出异常调用的流程
		} 
		catch (IOException e){
			
			e.printStackTrace();//深层次的输出异常调用的流程
		}
		catch(NullPointerException e){
			e.printStackTrace();//深层次的输出异常调用的流程
		}
		finally {
			if (br == null){
				try{
					br.close();
				} 
				catch (IOException e){
					e.printStackTrace();
				}
				br = null;
			}
			if (fr == null){
				try {
					fr.close();
				} catch (IOException e){
					e.printStackTrace();
				}
				fr = null;
			}
		}

	}

	public int[][] getMap() {
		return map;
	}

	public int getManX() {
		return manX;
	}
	public int getManY() {
		return manY;
	}
}
